package com.mediatek.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    //定义父子activity通过Intent Extra通信所使用的Extra Key值
    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.mediatek.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.mediatek.geoquiz.answer_shown";

    private boolean mAnswerIsTrue;
    private boolean mHasCheated;

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mHasCheated = getIntent().getBooleanExtra(EXTRA_ANSWER_SHOWN, false);

        mAnswerTextView = (TextView)findViewById(R.id.answer_text_view);

        mShowAnswerButton = (Button)findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                updateAnswerTextView();
            }
        });

        if (mHasCheated){
            updateAnswerTextView();
        }
    }

    private void setAnswerShownResult(boolean isAnswerShown){
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    private void updateAnswerTextView(){
        if (mAnswerIsTrue)
            mAnswerTextView.setText(R.string.true_button);
        else
            mAnswerTextView.setText(R.string.false_button);
        setAnswerShownResult(true);
        mShowAnswerButton.setEnabled(false);
    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, boolean hasCheated){
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_ANSWER_SHOWN, hasCheated);
        return intent;
    }

    public static boolean wasAnswerShown(Intent intent){
        return intent.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }
}
