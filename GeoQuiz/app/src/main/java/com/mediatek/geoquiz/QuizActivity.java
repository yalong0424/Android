package com.mediatek.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    //private Button mNextButton;
    //private Button mPrevButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;
    private Button mCheatButton;

    private static final String TAG = "QuizActivity"; //定义该Activity在Log中的Tag标识常量
    private static final String KEY_INDEX = "index"; //定义通过Bundle存储自定义数据时的KEY值:当前问题索引值
    private static final String KEY_QUESTIONS = "questions"; //定义通过Bundle存储自定义数据时的KEY值:所有问题状态值
    private static final int REQUEST_CODE_CHEAT = 0; //定义请求代码，用以标识CheatActivity的反馈信息

    private QuestionArray mQuestionBank = new QuestionArray();
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity.onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            String sQuestionsState = savedInstanceState.getString(KEY_QUESTIONS, "");
            mQuestionBank.restoreQuestionsFromString(sQuestionsState);
        }

        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.getLength();
                updateQuestion();
            }
        });

        mTrueButton = (Button)findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*
                Toast true_toast = Toast.makeText(QuizActivity.this, R.string.correct_toast,
                        Toast.LENGTH_SHORT);
                true_toast.setGravity(Gravity.TOP, 0, 0);
                true_toast.show();
                */
                checkAnswer(true);
            }
        });

        mFalseButton = (Button)findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*
                Toast.makeText(QuizActivity.this, R.string.incorrect_toast,
                        Toast.LENGTH_SHORT).show();
                */
                checkAnswer(false);
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (mCurrentIndex == 0)
                    mCurrentIndex = mQuestionBank.getLength() - 1;
                else
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.getLength();
                updateQuestion();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.getLength();
                updateQuestion();
            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //start activity
               // Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
                boolean answerIsTrue = mQuestionBank.getQuestionByIndex(mCurrentIndex).isAnswerTrue();
                boolean isCheated = mQuestionBank.getQuestionByIndex(mCurrentIndex).isCheater();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue, isCheated);
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
    }

    //测试Activity其他生命周期回调方法调用时机
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "Activity.onStart() called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "Activity.onResume() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "Activity.onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "Activity.onSaveInstanceState() called");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putString(KEY_QUESTIONS, mQuestionBank.saveQuestionsToString());
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "Activity.onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "Activity.onDestroy() called");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){ //验证子activity返回的结果代码是否符合预期
            return;
        }
        if (data == null){ //验证子activity是否返回有效的数据
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT){
            boolean isCheater = CheatActivity.wasAnswerShown(data);
            mQuestionBank.getQuestionByIndex(mCurrentIndex).setCheater(isCheater);
        }
    }

    private void updateQuestion(){
        int question = mQuestionBank.getQuestionByIndex(mCurrentIndex).getTextResId();
        mQuestionTextView.setText(question);
        updateButtonState();
    }

    private void checkAnswer(boolean userPressedTrue){
        Question question = mQuestionBank.getQuestionByIndex(mCurrentIndex);
        boolean answerIsTrue = question.isAnswerTrue();
        question.setAnswerCorrect(answerIsTrue == userPressedTrue);

        int messageResId = 0;
        int gravityLocation = Gravity.BOTTOM;
        if (answerIsTrue == userPressedTrue) {
            messageResId = R.string.correct_toast;
            gravityLocation = Gravity.TOP;
        }
        else {
            messageResId = R.string.incorrect_toast;
        }
        Toast toastObj = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
        toastObj.setGravity(gravityLocation, 0, 0);
        toastObj.show();

        if (question.isCheater()){
            Toast toast = Toast.makeText(this, R.string.judgment_toast, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        question.setAnswered(true);
        updateButtonState();
        resetQuestionStates();
    }

    private void updateButtonState(){
        boolean isAnswered = mQuestionBank.getQuestionByIndex(mCurrentIndex).isAnswered();
        mTrueButton.setEnabled(!isAnswered);
        mFalseButton.setEnabled(!isAnswered);
    }

    private void resetQuestionStates(){
        if (mQuestionBank.isAllAnswered()){
            giveTestPoint();

            mQuestionBank.resetQuestionStates();
            mCurrentIndex = 0;

            updateQuestion();
        }
    }

    private void giveTestPoint(){
        int correctNum = mQuestionBank.getCorrectedNum();
        int cheatedNum = mQuestionBank.getCheatedNum();
        double correctPercent = ((double)correctNum / mQuestionBank.getLength()) * 100;
        String msg = new Formatter().format("Congratulations, your correct rate is %.2f.", correctPercent).toString();
        if (cheatedNum > 0){
            String cheatedMsg = new Formatter().format("\nBut you have cheated %d question.", cheatedNum).toString();
            if (cheatedNum > 1)
                cheatedMsg = cheatedMsg.replace("question", "questions");
            msg += cheatedMsg;
        }
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM|Gravity.LEFT, 0, 0);
        toast.show();
    }

}
