package com.mediatek.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

/* 该类是PhotoGallery应用的数据存储引擎，主要用于读取和写入查询字符串。
* shared preferences本质上就是文件系统中的文件，可使用SharedPreferences类读写它。shared preferences实际上
* 是一种简单的XML文件，而SharedPreferences类提供了简单的接口，屏蔽了读写文件的实现细节。
* SharedPreferences类可以通过持久化存储保存数据，它用起来就像一个键值对仓库，键为字符串类型，值为原子数据类型。*/
public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery"; //定义查询字符串的存储KEY
    private static final String PREF_LAST_RESULT_ID = "lastResultId";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn"; //持久化存储定时器的启停状态

    public static String getStoredQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY, null);
    }

    public static void setStoredQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }

    public static String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_RESULT_ID, null);
    }

    public static void setLastResultId(Context context, String lastResultId) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_ID, lastResultId)
                .apply();
    }

    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON, isOn)
                .apply();
    }
}
