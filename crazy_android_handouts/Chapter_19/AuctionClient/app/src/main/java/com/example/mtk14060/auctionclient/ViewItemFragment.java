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

//查看流派物品的Fragment
public class ViewItemFragment extends Fragment
{
    private static final String ACTION = "action";

    Button bnHome;
    ListView succList;
    TextView viewTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_item, container, false);
        bnHome = rootView.findViewById(R.id.bn_home);
        succList = rootView.findViewById(R.id.succList);
        viewTitle = rootView.findViewById(R.id.view_title);
        bnHome.setOnClickListener(new HomeListener(getActivity()));

        String action = getArguments().getString(ACTION);
        //如果是查看流拍物品，修改标题
        if (action.equals(AuctionClientActivity.VIEW_FAIL_JSP_PAGE))
        {
            viewTitle.setText(R.string.view_fail);
        }
        try
        {
            //向指定URL发送请求，并把服务器响应数据转换成JSONArray对象
            String url = HttpUtil.BASE_URL + action;
            JSONArray jsonArray = new JSONArray(HttpUtil.getRequest(url));
            //将JSONArray包装成Adapter
            JSONArrayAdapter jsonArrayAdapter = new JSONArrayAdapter(getActivity(),
                    jsonArray, "name", true);
            succList.setAdapter(jsonArrayAdapter);
        }
        catch (Exception e)
        {
            DialogUtil.showDialog(getActivity(), R.string.server_response_error, false);
            e.printStackTrace();
        }
        succList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //查看指定物品的详细情况
                viewItemDetail(position);
            }
        });

        return rootView;
    }

    private void viewItemDetail(int position) {
        // 加载detail.xml界面布局代表的视图
        View detailView = getActivity().getLayoutInflater().inflate(R.layout.detail, null);

        // 获取detail.xml界面布局中的文本框
        TextView itemName = detailView.findViewById(R.id.itemName);
        TextView itemKind = detailView.findViewById(R.id.itemKind);
        TextView maxPrice = detailView.findViewById(R.id.maxPrice);
        TextView itemRemark = detailView.findViewById(R.id.itemRemark);

        // 获取被单击的列表项
        JSONObject jsonObj = (JSONObject) succList.getAdapter().getItem(position);
        try
        {
            // 通过文本框显示物品详情
            itemName.setText(jsonObj.getString("name"));
            itemKind.setText(jsonObj.getString("kind"));
            maxPrice.setText(jsonObj.getString("maxPrice"));
            itemRemark.setText(jsonObj.getString("desc"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        DialogUtil.showDialog(getActivity(), detailView);
    }

    public static ViewItemFragment newInstance(String jspPage)
    {
        ViewItemFragment fragment = new ViewItemFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ACTION, jspPage);
        fragment.setArguments(arguments);
        return fragment;
    }
}
