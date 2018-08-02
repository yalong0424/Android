package com.mediatek.draganddraw;

import android.graphics.PointF;

/** 代表一个矩形框实例*/
public class Box {
    private PointF mOriginPT;
    private PointF mCurrentPT;

    public Box(PointF originPT) {
        mOriginPT = originPT;
        mCurrentPT = originPT;
    }

    public PointF getCurrentPT() {
        return mCurrentPT;
    }

    public void setCurrentPT(PointF currentPT) {
        mCurrentPT = currentPT;
    }

    public PointF getOriginPT() {
        return mOriginPT;
    }
}
