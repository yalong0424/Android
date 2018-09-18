package com.mediatek.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* 监听带有BOOT_COMPLETED操作的broadcast intent，可知道设备是否已完成启动。无论何时，只要打开设备，
系统就会发送一个BOOT_COMPLETED broadcast intent。
broadcast receiver是接收intent的组件，想要监听broadcast intent，可以创建并登记一个standalone broadcast receiver。
在配置文件中完成声明后，即使应用还没有运行，只要有匹配的broadcast intent发来，broadcast receiver就会醒来接收。
一旦收到broadcast intent，broadcast receiver的onReceiver(Context, Intent)就会开始运行，随后broadcast receiver就会被销毁。
broadcast receiver的生命非常短暂，因而难以有所作为。例如，我们无法使用任何异步API或登记任何监听器，
因为一旦onReceive(Context, Intent)方法运行完，receiver就不存在了。
onReceive(Context, Intent)方法同样运行在主线程上，因此不能在该方法内做一些费时费力的事情，如网络连接或数据的永久存储等。
然而，这并不代表receiver一无用处。一些便利型任务就非常适合它，比如启动activity或服务（不需要等返回结果），
以及系统重启后重置定时运行的定时器。
*/
public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    //BroadcastReceiver是接收Intent的组件，当有Intent发送给StartupReceiver时，它的onReceive()方法就会被调用
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());
        //重启设备后，根据关机前的定时器启停状态决定是否重启定时器
        boolean isOn = QueryPreferences.isAlarmOn(context);
        PollService.setServiceAlarm(context, isOn);
    }
}
