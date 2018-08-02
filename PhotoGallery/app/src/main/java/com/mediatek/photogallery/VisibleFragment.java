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
            /** 普通broadcast intent是单向通信的，只是在概念上同时被所有人接收，而实际上，onReceive()方法是在主线程是调用的，
             * 所有各个receiver并没有同步并发运行。
             * 使用有序broadcast intent可以实现双向通信，有序broadcast运行多个broadcast receiver依次处理
             * broadcast intent，此外，通过传入一个名为result receiver的特别broadcast receiver，有序broadcast还支持
             * 让broadcast发送者接收broadcast接收者发出的反馈信息。
             * 在接收方来看，有序broadcast与普通broadcast没有什么不同，。然而，我们却因此获得了特别的工具：
             一套改变receiver返回值的方法。
             这里，我们需要取消通知信息。这很简单，使用一个简单的整型结果码，将此要求告诉信息发送者就可以了。
             具体就是使用setResultCode(int)设置一个Activity.RESULT_CANCELLED结果码
             来告诉SHOW_NOTIFICATION的发送者应该如何处理通知消息。
             同时，这个消息也会发送给接收链中的所有broadcast receiver。*/
            setResultCode(Activity.RESULT_CANCELED);
            /** 使用有序broadcast将广播接收者broadcast receiver发出的返回结果反馈给广播发送者broadcast intent，
             * 可以使用setResultCode(int)方式，如需返回更多复杂数据，还可以调用setResultData(String)或
             * setResultExtra(Bundle)方法。如需设置三个参数值，还可以调用setResult(int, String, Bundle).
             * broadcast receiver设置返回值后，每个后续广播接收者broadcast receiver均可看到或者修改它们。*/
        }
    };
}
