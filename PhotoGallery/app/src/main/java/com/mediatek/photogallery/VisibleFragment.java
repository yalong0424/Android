package com.mediatek.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

public abstract class VisibleFragment extends Fragment {
    private static final String TAG = "VisibleFragment";

    @Override
    public void onStart() {
        super.onStart();
        /**代码创建的IntentFilter与AndroidManifest.xml中声明的IntentFilter是一样的。
         * <intent-filter>
         * <action android:name="com.bignerdranch.android.photogallery.SHOW_NOTIFICATION" />
         * </intent-filter>
         * 任何使用XML定义的IntentFilter均能以代码的方式定义。要在代码中配置IntentFilter，
         * 可以直接调用addCategory(String)、addAction(String)和addDataPath(String)等方
         */
        IntentFilter intentFilter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        /** 在没有添加权限管控之前，其他任何应用都可以通过自己创建的broadcast intent来触发这里的broadcast receiver，
         * 而我们希望自定义的broadcast receiver不要关心所有应用发出的broadcast intent广播，而只关心PhotoGallery自定义的
         * broadcast intent广播。此时，通过在registerReceiver()方法中传入自定义的权限就可以保证这一点。*/
        getActivity().registerReceiver(mOnShowNotification, intentFilter, PollService.PERM_PRIVATE, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }

    /**  动态broadcast receiver是在代码中而不是在配置文件
     中完成登记声明的。要在代码中登记，可调用registerReceiver(BroadcastReceiver,
     IntentFilter)方法；取消登记时，则调用unregisterReceiver(BroadcastReceiver)方法。
     receiver自身通常被定义为一个内部类实例，如同一个按钮点击监听器。然而，在register-
     Receiver(...)和unregisterReceiver(BroadcastReceiver)方法中，你要的是同一个实例，
     因此需要将receiver赋值给一个实例变量。
    */
    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(), "Got a broadcast: " + intent.getAction(),
                    Toast.LENGTH_LONG).show();
            //if we receive this, we're visible, so cancel the notification
            Log.i(TAG, "cancelling notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };
}
