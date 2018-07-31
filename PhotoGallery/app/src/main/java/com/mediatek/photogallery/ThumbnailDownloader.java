package com.mediatek.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/* 消息是Message类的一个实例，Message中有三个实例变量需要自己定义：
* 1. What：消息ID，用户自定义的int型消息代码，用来描述消息；
* 2. obj：消息内容，用户指定，随消息发送的对象；
* 3. target：消息处理句柄，即处理消息的Handler.
* Message的目标(target)是一个Handler类的实例，创建Message时，target会自动与一个Handler相关联。
* Message待处理时，Handler对象负责触发消息处理事件。*/

//泛型参数T由于标识具体哪次下载
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0; //消息ID

    private Boolean mHasQuit = false;
    /*要处理消息以及消息指定的任务，首先需要一个Handler实例。
    * mRequestHandler用来存储对Handler的引用。这个Handler负责在ThumbnailDownloader后台线程上
    * 管理下载请求消息队列。还负责从消息队列里取出并处理下载请求消息。*/
    private Handler mRequestHandler;
    /* ConcurrentHashMap是一种线程安全的HashMap。*/
    private ConcurrentHashMap<T, String> mRequestMap = new ConcurrentHashMap<>();

    /* mResponseHandler用来存储来自主线程的Handler。
    通过mResponseHandler，ThumbnailDownloader能够使用与主线程Looper绑定的Handler.*/
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    /* 新增一个监听器接口响应请求：主线程发请求，相应结果是下载的图片。*/
    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    /* HandlerThread.onLooperPrepared()方法是在Looper首次检查消息队列之前调用的，所以该方法是创建Handler实现的好地方。*/
    @Override
    protected void onLooperPrepared() {
        /* Looper消息队列取得消息队列中的特定消息后，就会将该消息发送给它的目标Handler去处理该消息。
        * 消息一般是在目标Handler的Handler.handleMessage()方法中进行处理的。*/
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_DOWNLOAD) { //检查消息标识，只处理关心的消息
                    T target = (T)msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);
        if (url == null) {
            mRequestMap.remove(target);
        }
        else {
            mRequestMap.put(target, url);
            /*Handler不仅仅是处理Message的目标(target),还是创建和发布Message的接口。
            * 一般而言，不应该手动设置消息的目标Handler。创建消息时，最好调用Handler.obtainMessage()方法，
            * 传入必要的消息字段后，该方法会自动设置为该消息的目标Handler。
            * 一旦创建Message后，就可以调用消息的sendToTarget()方法将该消息发送给它的Handler，
            * 然后，该消息的Handler会将该消息放置在Looper消息队列的尾部。*/
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    public void cleanQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear(); //清除消息队列中的所有请求
    }

    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }
            byte[] bitmapBytes = new FlickrFetcher().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");
            /* mResponseHandler发送定制Message给主线程。同时，因为MResponseHandler是与主线程的Looper相关联的，
            * 所以UI更新代码会在主线程中完成。
            * Handler.post(Runnable)和Handler.obtainMessage类似，也是用来创建消息，并将该消息发送给它的Handler，
            * 然后，该消息的Handler会将该消息放置在该Handler相关联的Looper消息队列的尾部。*/
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mRequestMap.get(target) != null || mHasQuit) {
                        return;
                    }
                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        }
        catch (IOException ioe) {
            Log.e(TAG, "Error downloading image: ", ioe);
        }
    }
}
