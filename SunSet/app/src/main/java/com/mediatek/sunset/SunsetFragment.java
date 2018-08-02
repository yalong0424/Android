package com.mediatek.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    //接收colors.xml文件中定义的色彩资源，以着处理天空随日落所呈现的色彩变换效果
    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = view;
        mSunView = (View)view.findViewById(R.id.sun);
        mSkyView = (View)view.findViewById(R.id.sky);

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        //为mSceneView视图设置监听器，只要用户点击它，就调用startAnimation()执行动画
        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimation();
            }
        });

        return view;
    }

    private void startAnimation() {
        /** 视图的local layout rect是其相对于父视图的位置和其尺寸大小的描述。视图一旦实例化，
         * 这些值都是相对固定的。。虽然可以修改这些值，从而改变视图的位置，但不推荐这么做。
         * 因为每次布局切换时，这些值都会被重置，所以才会有相对固定一说。
         * */
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getHeight();

        /** ObjectAnimator对象负责执行动画。
         * 动画效果的实现原理 ：ObjectAnimator是个属性动画制作对象。要获得某种动画效果，传统方式是设法在屏幕上移动视图，
         * 而属性动画制作对象ObjectAnimator却另辟蹊径：以一组不同的参数值反复调用属性设置方法。
         * 调用以下方法可以创建ObjectAnimator对象：
         * ObjectAnimator.ofFloat(mSunView, "y", 0, 1)
         * 上例中，新建ObjectAnimator一旦启动，就会以从0开始递增的参数值反复调用mSunView.
         * setY(float)方法：
         * mSunView.setY(0);
         * mSunView.setY(0.02);
         * mSunView.setY(0.04);
         * mSunView.setY(0.06);
         * mSunView.setY(0.08);
         * ...
         * 直到调用mSunView.setY(1)为止。这个0~1区间参数值的确定过程又称为interpolation。
         * 可以想象到，在这个interpolation过程中，即便很短暂，确定相邻参数值也是要耗费时间的；
         * 由于人眼的视觉暂留现象，动画效果就形成了。
         * */
        ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(mSunView,
                "y", sunYStart, sunYEnd)
                .setDuration(3000);
        /**使用不同的interpolator实现加速特效。
         * TimeInterpolator作用：改变A点到B点的动画效果。不同的TimeInterpolator对象可实现不同的动画特效。
         * 此处使用AccelerateInterpolator对象实现太阳加速落下的效果*/
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        /** 添加一个ObjectAnimator，实现天空色彩从mBlueSkyColor到mSunsetSkyColor变换的动画效果。
         * 不过还要处理天空颜色的过度，避免颜色夸张变化。
         * 颜色int数值并不是个简单的数字。它实际是由四个较小数字转换而来。因此，只有知道颜色的组成奥秘，
         * ObjectAnimator对象才能合理地确定蓝色和橘黄色之间的中间值。不过，知道如何确定颜色中间值还不够，
         * ObjectAnimator还需要一个TypeEvaluator子类的协助。
         * TypeEvaluator能帮助ObjectAnimator对象精确地计算开始到结束间的递增值。
         * Android提供的这个TypeEvaluator子类叫作ArgbEvaluator。
         * */
        ObjectAnimator sunsetSkyAnimator = ObjectAnimator.ofInt(mSkyView,
                "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        //夜空变化的动画代码
        ObjectAnimator nightSkyAnimator = ObjectAnimator.ofInt(mSkyView,
                "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        /** 播放多个动画。
         * 有时，你需要同时执行一些动画。在不要求动画播放顺序情况下，这很简单，同时调用start()方法就行了。
         * 但是多数情况是需要控制动画的播放顺序，就像播放电影动画一样，此处可以使用AnimatorListener。
         * AnimatorListener会让你知道动画什么时候结束。
         * 这样，执行完第一个动画，就可以接力执行第二个夜空变化的动画。然而，理论分析很简单，实际去做的话，
         * 少不了要准备多个监听器，这也很麻烦。
         * 好在Android还设计了方便又简单的AnimatorSet。
         * AnimatorSet运行你创建并执行一个动画集。说白了，AnimatorSet就是可以放在一起执行的动画集。
         *动画的play-with-before ：协同执行heightAnimator和sunsetSkyAnimator动画，在nightSkyAnimator
         之前执行heightAnimator动画。*/
        //heightAnimator.start();
        //sunsetSkyAnimator.start();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        animatorSet.start();
    }
}
