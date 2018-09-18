package com.mediatek.geoquiz;

class QuestionVersions{
    public static int QUESTION_VERSIONS_ONE = 0;
}

public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mAnswered;
    private boolean mAnswerCorrect;
    private boolean mIsCheater;
    private static int version = QuestionVersions.QUESTION_VERSIONS_ONE;

    public Question(int textResId, boolean answerTrue){
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mAnswered = false;
        mAnswerCorrect = false;
        mIsCheater = false;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public boolean isAnswered() {
        return mAnswered;
    }

    public void setAnswered(boolean answered) {
        mAnswered = answered;
    }

    public boolean isAnswerCorrect() {
        return mAnswerCorrect;
    }

    public void setAnswerCorrect(boolean answerCorrect) {
        mAnswerCorrect = answerCorrect;
    }

    public boolean isCheater() {
        return mIsCheater;
    }

    public void setCheater(boolean cheater) {
        mIsCheater = cheater;
    }

    public String saveStateToString(){
        String sQuestionObj = "";
        sQuestionObj += version;
        sQuestionObj += ":";
        sQuestionObj += mTextResId;
        sQuestionObj += ":";
        sQuestionObj += mAnswerTrue;
        sQuestionObj += ":";
        sQuestionObj += mAnswered;
        sQuestionObj += ":";
        sQuestionObj += mAnswerCorrect;
        sQuestionObj += ":";
        sQuestionObj += mIsCheater;
        return sQuestionObj;
    }

    public void restoreStateFromString(String sQuestionObj){
        String[] sQuestions = sQuestionObj.split("\\:");
        version = Integer.parseInt(sQuestions[0]);
        mTextResId = Integer.parseInt(sQuestions[1]);
        mAnswerTrue = Question.parseBoolFromString(sQuestions[2]);
        mAnswered = Question.parseBoolFromString(sQuestions[3]);
        mAnswerCorrect = Question.parseBoolFromString(sQuestions[4]);
        mIsCheater = Question.parseBoolFromString(sQuestions[5]);
    }

    private static Boolean parseBoolFromString(String sBool){
        if (sBool.equalsIgnoreCase("true"))
        {
            return true;
        }
        else {
            return false;
        }
    }
}
