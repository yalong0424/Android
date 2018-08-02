package com.mediatek.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/** 定制视图：负责处理所有的图形绘制和触摸事件。
 * 处理触摸事件一般有两种方式：
 * 一、为视图View设置触摸事件监听器：
 * public void setOnTouchListener(View.OnTouchListener listener)
 * 其工作方式与setOnClickListener(View.OnClickListener)相同，实现View.OnTouchListener接口，功能触摸事件发生时调用。
 * 二、在定制视图中覆盖View以下方法：
 * public boolean onTouchEvent(MotionEvent event)
 * */
public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";

    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();

    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    //视图从代码中实例化时调用
    public BoxDrawingView(Context context) {
        this(context, null); //调用自身构造函数
    }

    //视图从布局文件中实例化时调用
    //从布局文件中实例化的视图会收到一个AttributeSet实例，该实例包含了XML布局文件中指定的XML属性。
    public BoxDrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet); //调用基类构造函数

        //paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        //paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    /** 该方法接收一个MotionEvent类的实例，该类用来描述包括位置和动作的触摸事件，动作用于描述事件所处的阶段。
     * 可以使用
     * public final int getAction()
     * 方法查看动作值。*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF currentPT = new PointF(event.getX(), event.getY());
        String action = "";

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "MotionEvent::ACTION_DOWN";
                //reset drawing state
                mCurrentBox = new Box(currentPT);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "MotionEvent::ACTION_MOVE";
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrentPT(currentPT); //跟踪运动事件
                    invalidate(); //强制BoxDrawingView重新绘制自己，从而使用户在屏幕上拖拽时能实时看到矩形框
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "MotionEvent::ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "MotionEvent::ACTION_CANCEL";
                mCurrentBox = null;
                break;
        }
        Log.i(TAG, action + " at x=" + currentPT.x + ", y=" + currentPT.y);
        return super.onTouchEvent(event);
    }

    /** 该方法用于处理View的图形绘制。
     * Canvas和Paint是Android系统的两大绘制类。
     * Canvas类拥有我们需要的所有绘制操作。其方法可决定绘在哪里以及绘什么，比如线条、圆形、字词、矩形等。
     * Paint类决定如何绘制。其方法可指定绘制图形的特征，例如是否填充图形、使用什么字体绘制、线条是什么颜色等。
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "BoxDrawingView::onDraw(Canvas) called...");
        //fill the background 使用米白背景paint填充canvas以衬托矩形框
        canvas.drawPaint(mBackgroundPaint);

        for (Box box: mBoxen) {
            float left = Math.min(box.getOriginPT().x, box.getCurrentPT().x);
            float right = Math.max(box.getOriginPT().x, box.getCurrentPT().x);
            float top = Math.min(box.getOriginPT().y, box.getCurrentPT().y);
            float bottom = Math.max(box.getOriginPT().y, box.getCurrentPT().y);

            canvas.drawRect(left, top, right, bottom, mBoxPaint); //在屏幕上绘制红色矩形框
        }
    }
}
