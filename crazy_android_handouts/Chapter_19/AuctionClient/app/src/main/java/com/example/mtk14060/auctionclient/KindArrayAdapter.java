package com.example.mtk14060.auctionclient;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * KindArrayAdapter用于包装KindArray对象，给ListView提供列表项，列表项既包括种类名称又包括种类描述。
 * */
public class KindArrayAdapter extends BaseAdapter {
    //需要包装的JSONArray
    private JSONArray kindJSONArray;
    private Context context;

    public KindArrayAdapter(JSONArray kindJSONArray, Context context)
    {
        this.kindJSONArray = kindJSONArray;
        this.context = context;
    }

    @Override
    public int getCount() {
        return kindJSONArray.length();
    }

    @Override
    public Object getItem(int position) {
        return kindJSONArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        try
        {
            return ((JSONObject)getItem(position)).getInt("id");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView imageView = new ImageView(context);
        imageView.setPadding(10, 0, 20, 0);
        imageView.setImageResource(R.drawable.item);
        linearLayout.addView(imageView);
        TextView kindNameView = new TextView(context);
        try {
            String kindName = ((JSONObject)getItem(position)).getString("kindName");
            kindNameView.setText(kindName);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        kindNameView.setTextSize(20);
        linearLayout.addView(kindNameView);
        container.addView(linearLayout);
        TextView descView = new TextView(context);
        descView.setPadding(30, 0, 0, 0);
        try {
            String kindDesc = ((JSONObject)getItem(position)).getString("kindDesc");
            kindNameView.setText(kindDesc);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        descView.setTextSize(16);
        container.addView(descView);
        return container;
    }
}
