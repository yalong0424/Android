package com.example.mtk14060.auctionclient;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.mtk14060.app.base.SingleFragmentActivity;

public class ViewItemActivity extends SingleFragmentActivity
{
    private static final String ACTION = "action";

    @Override
    protected Fragment createFragment() {
        String jspPage = getIntent().getStringExtra(ACTION);
        return ViewItemFragment.newInstance(jspPage);
    }

    public static Intent getViewItemInstance(Context context, String jspPage)
    {
        Intent intent = new Intent(context, ViewItemActivity.class);
        //action属性为请求的Servlet的URL
        intent.putExtra(ACTION, jspPage);
        return intent;
    }
}
