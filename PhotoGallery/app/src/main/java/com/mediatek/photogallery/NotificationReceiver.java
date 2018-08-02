package com.mediatek.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/** 虽然有时也能使用Context.sendOrderedBroadcast的result receiver接收broadcast和发布通知对象，
 * 但此处行不通。目标broadcast intent通常是在PollService对象消亡之前发出的，这意味着
 * broadcast receiver很可能也不存在了。因此，最终的broadcast receiver应该是个
 * standalone broadcast receiver---NotificationReceiver。而且还要保证NotificationReceiver在其他动态登记的
 * broadcast receiver之后运行--可以通过在AndroidManifest.xml登记时提供优先级来做到。*/
public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received result:" + getResultCode());
        /** 在登记NotificationReceiver时赋予它一个很低的优先级，这样就可以保证NotificationReceiver
         * 最后一个接收目标broadcast，这样的话，NotificationReceiver就知道该不该向NotificationManager发出通知了。
         * */
        if (getResultCode() != Activity.RESULT_OK) {
            //A foreground activity cancelled the broadcast
            return;
        }
        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
        Notification notification = (Notification)intent.getParcelableExtra(PollService.NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode, notification);
    }
}
