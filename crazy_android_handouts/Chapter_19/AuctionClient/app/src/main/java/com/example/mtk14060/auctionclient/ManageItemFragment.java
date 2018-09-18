package com.example.mtk14060.auctionclient;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telecom.Call;
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

/**
 * 管理物品的Fragment，只要通过HttpUtil向服务器发送请求，并把服务器响应转换成JSONArray对象，然后把
 * JSONArray对象包装成Adapter，再使用ListView来显示这些物品即可。
 * */
public class ManageItemFragment extends Fragment
{
    public static final int ADD_ITEM = 0x1006;

    Button bnHome, bnAdd;
    ListView itemList;
    Callbacks mCallbacks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.manage_item, container, false);
        bnHome = rootView.findViewById(R.id.bn_home);
        bnAdd = rootView.findViewById(R.id.bnAdd);
        itemList = rootView.findViewById(R.id.itemList);
        bnHome.setOnClickListener(new HomeListener(getActivity()));
        bnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onItemSelected(ADD_ITEM, null);
            }
        });
        String url = HttpUtil.BASE_URL + "viewOwnerItem.jsp";
        try {
            //向指定URL发送请求
            JSONArray jsonArray = new JSONArray(HttpUtil.getRequest(url));
            //将服务器响应包装成Adapter，供ListView进行显示
            JSONArrayAdapter jsonArrayAdapter = new JSONArrayAdapter(getActivity(), jsonArray, "name", true);
            itemList.setAdapter(jsonArrayAdapter);
        }
        catch (Exception e)
        {
            DialogUtil.showDialog(getActivity(), R.string.server_response_error, false);
            e.printStackTrace();
        }
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewItemInBid(position);
            }
        });
        return rootView;
    }

    //当Fragment被添加、显示到Activity时回调该方法
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //判断Activity是否实现了Callbacks接口，没有实现则抛出异常
        if (!(context instanceof Callbacks))
        {
            throw new IllegalStateException(getActivity().getResources().getString(R.string.imp_callbacks_manageritem));
        }
        mCallbacks = (Callbacks)context;
    }

    //当该Fragment从它所属的Activity中被删除时回调该方法
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void viewItemInBid(int position) {
        View detailView = getActivity().getLayoutInflater().inflate(R.layout.detail_in_bid, null);
        TextView itemName = detailView.findViewById(R.id.itemName);
        TextView itemKind = detailView.findViewById(R.id.itemKind);
        TextView maxPrice = detailView.findViewById(R.id.maxPrice);
        TextView initPrice = detailView.findViewById(R.id.initPrice);
        TextView endTime = detailView.findViewById(R.id.endTime);
        TextView itemRemark = detailView.findViewById(R.id.itemRemark);
        //获取被单击列表项所包装的JSONObject
        JSONObject jsonObject = (JSONObject) itemList.getAdapter().getItem(position);
        try {
            itemName.setText(jsonObject.getString("name"));
            itemKind.setText(jsonObject.getString("kind"));
            maxPrice.setText(jsonObject.getString("maxPrice"));
            itemRemark.setText(jsonObject.getString("desc"));
            initPrice.setText(jsonObject.getString("initPrice"));
            endTime.setText(jsonObject.getString("endTime"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        DialogUtil.showDialog(getActivity(), detailView);
    }
}
