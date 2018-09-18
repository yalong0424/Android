package com.example.mtk14060.auctionclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mtk14060.auctionclient.util.DialogUtil;
import com.example.mtk14060.auctionclient.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddItemFragment extends Fragment {
    EditText etItemName, etItemDesc, etItemRemark, etInitPrice;
    Spinner itemKindSpinner, availTimeSpinner;
    Button bnAdd, bnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_item, container, false);
        etItemName = rootView.findViewById(R.id.itemName);
        etItemDesc = rootView.findViewById(R.id.itemDesc);
        etItemRemark = rootView.findViewById(R.id.itemRemark);
        etInitPrice = rootView.findViewById(R.id.initPrice);
        itemKindSpinner = rootView.findViewById(R.id.itemKind);
        availTimeSpinner = rootView.findViewById(R.id.availTime);
        //定义发送请求的地址
        String url = HttpUtil.BASE_URL + "viewKind.jsp";
        JSONArray jsonArray = null;
        try {
            //获取系统中的所有物品种类
            //向指定URL发送请求，并把服务器相应包装成JSONArray
            jsonArray = new JSONArray(HttpUtil.getRequest(url));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        JSONArrayAdapter jsonArrayAdapter = new JSONArrayAdapter(getActivity(), jsonArray, "kindName", false);
        //显示物品种类列表
        itemKindSpinner.setAdapter(jsonArrayAdapter);
        bnAdd = rootView.findViewById(R.id.bnAdd);
        bnCancel = rootView.findViewById(R.id.bnCancel);
        bnCancel.setOnClickListener(new HomeListener(getActivity()));
        bnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                {
                    String name = etItemName.getText().toString();
                    String desc = etItemDesc.getText().toString();
                    String remark = etItemRemark.getText().toString();
                    String price = etInitPrice.getText().toString();
                    JSONObject kind = (JSONObject)itemKindSpinner.getSelectedItem();
                    int avail = availTimeSpinner.getSelectedItemPosition();
                    switch (avail)
                    {
                        case 5:
                            avail = 7;
                            break;
                        case 6:
                            avail = 30;
                            break;
                        default:
                            avail += 1;
                            break;
                    }
                    try
                    {
                        String result = addItem(name, desc, remark, price, kind.getInt("id"), avail);
                        DialogUtil.showDialog(getActivity(), result, true);
                    }
                    catch (Exception e)
                    {
                        DialogUtil.showDialog(getActivity(), R.string.server_response_error, false);
                        e.printStackTrace();
                    }
                }
            }
        });
        return rootView;
    }

    private boolean validate() {
        String name = etItemName.getText().toString().trim();
        if (name.equals(""))
        {
            DialogUtil.showDialog(getActivity(), R.string.item_name_must_exist, false);
            return false;
        }
        String price = etInitPrice.getText().toString().trim();
        if (price.equals(""))
        {
            DialogUtil.showDialog(getActivity(), R.string.init_price_must_exist, false);
            return false;
        }
        try {
            //尝试把起拍价格转换为浮点数
            Double.parseDouble(price);
        }
        catch (NumberFormatException e)
        {
            DialogUtil.showDialog(getActivity(), R.string.init_price_must_number, false);
            return false;
        }
        return true;
    }

    private String addItem(String name, String desc, String remark, String initPrice, int kindId,
                           int availTime) throws Exception
    {
        //使用Map封装请求参数
        Map<String, String> map = new HashMap<>();
        map.put("itemName", name);
        map.put("itemDesc" , desc);
        map.put("itemRemark" , remark);
        map.put("initPrice" , initPrice);
        map.put("kindId" , kindId + "");
        map.put("availTime" , availTime + "");
        String url = HttpUtil.BASE_URL + "addItem.jsp";
        return HttpUtil.postRequest(url, map);
    }
}
