package com.mediatek.photogallery;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class PhotoPageFragment extends VisibleFragment {
    private static final String ARG_URI = "photo_page_url";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static PhotoPageFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        PhotoPageFragment photoPageFragment = new PhotoPageFragment();
        photoPageFragment.setArguments(args);
        return photoPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri = getArguments().getParcelable(ARG_URI);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_photo_page, container, false);

        mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        mProgressBar.setMax(100); //WebChromeClient reports in range 0-100

        mWebView = (WebView)view.findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        /** WebChromeClient是一个事件接口，用来响应那些改变浏览器中装饰元素的事件。
         * 这包括JavaScript警告信息、网页图标、状态条加载，以及当前网页标题的刷新。
         * */
        mWebView.setWebChromeClient(new WebChromeClient() {
            //ProgressBar的进度条更新回调方法
            public void onProgressChanged(WebView webView, int newProgress) {
                if (newProgress == 100) {
                    //进度值为100说明网页已加载完成，将ProgressBar视图隐藏起来
                    mProgressBar.setVisibility(View.GONE);
                }
                else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            //标题栏更新回调方法
            public void onReceivedTitle(WebView webView, String title) {
                AppCompatActivity activity = (AppCompatActivity)getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });
        //WebViewClient用来响应WebView上的渲染事件，即响应渲染事件的接口
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mUri.toString()); //加载URL必须等WebView配置完成后进行。

        return view;
    }
}
