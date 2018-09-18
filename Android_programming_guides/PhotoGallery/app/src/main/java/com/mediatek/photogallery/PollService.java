package com.mediatek.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

/** PollService是用来轮询搜索结果的服务。
 * 服务的Intent又叫命令，所谓命令，就是要服务做事的一条指令。服务的种类不同，其执行命令的方式也不尽相同。
 * IntentService服务顺序执行其命令队列里的命令。IntentService服务收到第一条命令时，它就会启动，触发一个
 * 后台线程，然后将命令放入命令队列。随后，IntentService服务按照顺序依次执行命令队列中的每一条命令，
 * 并针对每一条命令在后台线程上调用onHandleIntent(Intent)方法。新进命令总是放置在队尾。最后，执行完队列中的
 * 全部命令后，IntentService服务也随即停止并销毁。
 * */
public class PollService extends IntentService{
    private static final String TAG = "PollService";

    //set interval to 15 minute，设置PollService服务每15 * 60s运行一次，即使进程停止了，
    // AlarmManager依然会不断发送Intent，反复启动PollService服务。
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15);

    public static final String ACTION_SHOW_NOTIFICATION = "com.mediatek.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.mediatek.photogallery.PRIVATE"; //定义一个自定义权限常量

    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    /** 功能：开关定时器。
     * AlarmManager是可以发送Intent的系统服务。如何告诉AlarmManager我想发的intent呢？
     * 使用PendingIntent。使用PendingIntent打包一个intent：“我想启动PollService服务。”
     * 然后，将其发送给系统中的其他部件，如AlarmManager。
     * */
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = PollService.newIntent(context);
        /** 使用PendingIntent.getService()来创建一个用来启动PollService的PendingIntent。
         * PendingIntent.getService()方法打包了一个Context.startService(Context)方法的调用。
         * 其四个参数为：一个用来发送intent的Context；一个区分PendingIntent来源的请求代码；
         * 一个待发送的Intent对象；一组用来决定如何创建PendingIntent的标志符。\
         * */
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, 0);
        // 设置或取消定时器。*/
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            /** 设置定时器可调用AlarmManager.setRepeating(...)方法。
             * 该方法同样具有四个参数：一个描述定时器时间基准的常量；定时器启动的时间；
             * 定时器循环的时间间隔以及一个到时要发送的PendingIntent。
             *
             * AlarmManager.ELAPSED_REALTIME 是基准时间值， 这表明我们是以SystemClock.elapsedRealtime()
             * 走过的时间来确定何时启动时间的。也就是说，经过一段指定的时间，就启动定时器。
             * 假如使用AlarmManager.RTC，启动基准时间就是当前时刻（例如，System.currentTimeMillis()）。
             * 也就是说，一旦到了某个固定时刻，就启动定时器。
             * */
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pendingIntent);
        }
        else {
            /** 取消定时器可调用AlarmManager.cancel(PendingIntent)方法。
             * 通常，也需同步取消PendingIntent，取消PendingIntent也有助于跟踪定时器状态。
             * */
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        //在设置定时器后存下它的启停状态
        QueryPreferences.setAlarmOn(context, isOn);
    }

    /** 功能：判定定时器的启停状态。
     * 一个PendingIntent只能登记一个定时器。这也是isOn值为false时，setServiceAlarm(Context, boolean)
     * 方法的工作原理：首先调用AlarmManager.cancel(PendingIntent)方法撤销PendingIntent的定时器，
     * 然后撤销PendingIntent。
     * 既然撤销定时器也随即撤消了PendingIntent，可通过检查PendingIntent是否存在来确认定时器激活与否。
     * 具体代码实现时，传入PendingIntent.FLAG_NO_CREATE标志给PendingIntent.getService(...)方法即可。
     * 该标志表示如果PendingIntent不存在，则返回null，而不是创建它。
     * */
    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    public PollService() {
        super(TAG);
    }

    //IntentService服务会按照顺序依次执行命令队列中的每一条命令，并针对每一条命令在后台线程上调用onHandleIntent(Intent)方法
    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }
        Log.i(TAG, "Received an intent: " + intent);
        String query = QueryPreferences.getStoredQuery(this);
        List<GalleryItem> galleryItems;
        if (query == null) {
            galleryItems = new FlickrFetcher().fetchRecentPhotos();
        }
        else {
            galleryItems = new FlickrFetcher().searchPhotos(query);
        }

        if (galleryItems.size() == 0) {
            return;
        }
        String lastResultId = QueryPreferences.getLastResultId(this);
        String resultId = galleryItems.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        }
        else {
            Log.i(TAG, "Got a new result: " + resultId);
            Resources resources = getResources();
            Intent activityIntent = PhotoGalleryActivity.newIntent(this);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    activityIntent, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title)) //配置ticker text
                    .setSmallIcon(android.R.drawable.ic_menu_report_image) //配置小图标
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent) //知道用户单击Notification消息时所触发的动作行为
                    .setAutoCancel(true) //设置为true，则用户点击了Notification消息时，该消息就会从消息抽屉中删除
                    .build();

            //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            //传递的id是消息的标识符，在整个应用中应该是唯一的.
            //如果使用同一ID发送两条消息，则第二条消息会替换掉第一条消息。
            // 在实际开发中，这也是进度条或其他动态视觉效果的实现方式。
            //notificationManager.notify(0, notification);

            /** 当有新结果时，设置应用对外广播。
             * 要对外广播自己定制的broadcast intent，只需要创建一个intent，并传入sendBroadcase(Intent)即可。
             * 要限制broadcast intent的使用权限，可以将权限字符串作为参数传入sendBroadcast()方法。
             * 有了这个权限，所有应用都必须使用相同的权限才能接受你发送的broadcast intent。*/
            //sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PERM_PRIVATE);
            showBackgroundNotification(0, notification);
        }
        QueryPreferences.setLastResultId(this, resultId);
    }

    //打包一个Notification的调用，然后以一个broadcast发出。
    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(NOTIFICATION, notification);
        /** 必须在有序broadcast中，broadcast receiver使用setResultCode(int) setResultData(String)或
         * setResultExtras(Bundle)或setResult(int, String, Bundle)方法才能起作用。
         * Context.sendOrderedBroadcast(Intent, String, BroadcastReceiver, Handler, int, String, Bundle)
         * 前两个参数与sendBroadcast(Intent, String)作用一样，剩下五个参数依次为：
         * 一个result receiver，一个支持result receiver运行的Handler，一个结果代码初始值，
         * 一个结果数据以及一个有序broadcast的结果附加内容。
         * result receiver比较特殊，只有在所有 有序broadcast 的接收者都结束运行后，result receiver才开始运行。*/
        sendOrderedBroadcast(intent, PERM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }

    //在后台连接网络时，使用ConnectivityManager来确认网络连接是否可用。
    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = connectivityManager.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && connectivityManager.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
