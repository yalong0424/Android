package com.example.mtk14060.auctionclient;

import android.content.Context;
import android.graphics.Color;
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
 * JSONArrayAdapter类对JSON数组进行了包装，并作为ListView的内容Adapter
 */
public class JSONArrayAdapter extends BaseAdapter {
    private Context context;
    //定义需要包装的JSONArray对象
    private JSONArray jsonArray;
    //定义列表项显示JSONObject对象的哪个属性
    private String property;
    private boolean hasIcon;

    public JSONArrayAdapter(Context context, JSONArray jsonArray, String property, boolean hasIcon)
    {
        this.context = context;
        this.jsonArray = jsonArray;
        this.property = property;
        this.hasIcon = hasIcon;
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return jsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        try
        {
            //返回物品的ID
            JSONObject jsonObject = (JSONObject) getItem(position);
            return jsonObject.getInt("id");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView imageView = new ImageView(context);
        imageView.setPadding(10, 0, 20, 0);
        imageView.setImageResource(R.drawable.item);
        linearLayout.addView(imageView);

        TextView textView = new TextView(context);
        try
        {
            JSONObject jsonObject = (JSONObject)getItem(position);
            String itemName = jsonObject.getString(property);
            textView.setText(itemName);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        textView.setTextSize(20);
        if (hasIcon)
        {
            linearLayout.addView(textView);
            return linearLayout;
        }
        else {
            textView.setTextColor(Color.BLACK);
            return textView;
        }
    }
}
