package com.example.mtk14060.auctionclient.util;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.example.mtk14060.auctionclient.AuctionClientActivity;
import com.example.mtk14060.auctionclient.R;

/**
 * @file: DialogUtil.java
 * @date: 2018/9/14 12:16
 * @author: MTK14060
 * @function: 对本系统用到的各种对话框进行管理
 * @remark:
 */
public class DialogUtil
{
    public static void showDialog(final Context context, int resID, boolean goHome)
    {
        String msg = context.getResources().getString(resID);
        showDialog(context, msg, goHome);
    }

    //定义一个显示消息的对话框
    public static void showDialog(final Context context, String msg, boolean goHome)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setCancelable(false);
        if (goHome)
        {
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, AuctionClientActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            });
        }
        else {
            builder.setPositiveButton(R.string.confirm, null);
        }
    }

    //定义一个显示指定组件的对话框
    public static void showDialog(Context context, View view)
    {
        new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("确定", null)
                .create()
                .show();
    }
}
