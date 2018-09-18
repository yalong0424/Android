package com.example.mtk14060.auctionclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mtk14060.auctionclient.util.DialogUtil;
import com.example.mtk14060.auctionclient.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewBidFragment extends Fragment {
    Button bnHome;
    ListView bidList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_bid, container , false);
        // 获取界面上的返回按钮
        bnHome = rootView.findViewById(R.id.bn_home);
        bidList = rootView.findViewById(R.id.bidList);
        // 为返回按钮的单击事件绑定事件监听器
        bnHome.setOnClickListener(new HomeListener(getActivity()));
        // 定义发送请求的URL
        String url = HttpUtil.BASE_URL + "viewBid.jsp";
        try
        {
            // 向指定URL发送请求，并把服务器响应包装成JSONArray对象
            JSONArray jsonArray = new JSONArray(HttpUtil.getRequest(url));
            // 将JSONArray包装成Adapter
            JSONArrayAdapter adapter = new JSONArrayAdapter(getActivity()
                    , jsonArray, "item", true);
            bidList.setAdapter(adapter);
        }
        catch (Exception e)
        {
            DialogUtil.showDialog(getActivity(), R.string.server_response_error, false);
            e.printStackTrace();
        }
        bidList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                // 查看竞价详情
                viewBidDetail(position);
            }
        });
        return rootView;
    }

    private void viewBidDetail(int position) {
        // 加载bid_detail.xml界面布局代表的视图
        View detailView = getActivity().getLayoutInflater().inflate(R.layout.bid_detail, null);
        // 获取bid_detail界面中的文本框
        TextView itemName = detailView.findViewById(R.id.itemName);
        TextView bidPrice = detailView.findViewById(R.id.bidPrice);
        TextView bidTime = detailView.findViewById(R.id.bidTime);
        TextView bidUser = detailView.findViewById(R.id.bidUser);
        // 获取被单击项目所包装的JSONObject
        JSONObject jsonObj = (JSONObject) bidList.getAdapter().getItem(position);
        try
        {
            // 使用文本框来显示竞价详情。
            itemName.setText(jsonObj.getString("item"));
            bidPrice.setText(jsonObj.getString("price"));
            bidTime.setText(jsonObj.getString("bidDate"));
            bidUser.setText(jsonObj.getString("user"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        DialogUtil.showDialog(getActivity(), detailView);
    }
}
