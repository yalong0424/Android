package com.mediatek.geoquiz;

public class QuestionArray {
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_asia, true)
    };

    public int getLength(){
        return mQuestionBank.length;
    }

    public Question getQuestionByIndex(int index){
        return mQuestionBank[index];
    }

    public boolean isAllAnswered(){
        boolean allAnswerd = true;
        for (short i = 0; i < mQuestionBank.length; ++i){
            if (!mQuestionBank[i].isAnswered())
            {
                allAnswerd = false;
                break;
            }
        }
        return allAnswerd;
    }

    public void resetQuestionStates(){
        for (short i = 0; i < getLength(); ++i)
        {
            mQuestionBank[i].setAnswered(false);
            mQuestionBank[i].setAnswerCorrect(false);
            mQuestionBank[i].setCheater(false);
        }
    }

    public int getCorrectedNum(){
        int correctNum = 0;
        for (int i = 0; i < mQuestionBank.length; ++i){
            if (mQuestionBank[i].isAnswerCorrect())
                ++correctNum;
        }
        return correctNum;
    }

    public int getCheatedNum(){
        int cheatedNum = 0; //偷看过答案的数量
        for (int i = 0; i < mQuestionBank.length; ++i){
            if (mQuestionBank[i].isCheater())
                ++cheatedNum;
        }
        return cheatedNum;
    }

    public String saveQuestionsToString(){
        String sQuestions = "";
        for (int i = 0; i < mQuestionBank.length; ++i){
            String sQuestionStr = mQuestionBank[i].saveStateToString();
            if (i == mQuestionBank.length - 1){
                sQuestions += sQuestionStr;
            }
            else {
                sQuestions += sQuestionStr + ";";
            }
        }
        return sQuestions;
    }

    public void restoreQuestionsFromString(String sQuestions){
        String[] sQuestionArr = sQuestions.split("\\;");
        for (int i = 0; i < sQuestionArr.length; ++i){
            mQuestionBank[i].restoreStateFromString(sQuestionArr[i]);
        }
    }
}
