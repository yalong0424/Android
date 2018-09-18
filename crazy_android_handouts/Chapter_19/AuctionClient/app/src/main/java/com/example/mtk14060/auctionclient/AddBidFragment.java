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
import android.widget.TextView;

import com.example.mtk14060.auctionclient.util.DialogUtil;
import com.example.mtk14060.auctionclient.util.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddBidFragment extends Fragment {
    private static final String ITEM_ID_KEY = "itemId";

    // 定义界面中文本框
    TextView itemName, itemDesc,itemRemark,itemKind
            ,initPrice , maxPrice ,endTime;
    EditText bidPrice;
    // 定义界面中两个按钮
    Button bnAdd, bnCancel;
    // 定义当前正在拍卖的物品
    JSONObject jsonObj;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.add_bid, container , false);
        // 获取界面中编辑框
        itemName = rootView.findViewById(R.id.itemName);
        itemDesc = rootView.findViewById(R.id.itemDesc);
        itemRemark = rootView.findViewById(R.id.itemRemark);
        itemKind = rootView.findViewById(R.id.itemKind);
        initPrice = rootView.findViewById(R.id.initPrice);
        maxPrice = rootView.findViewById(R.id.maxPrice);
        endTime = rootView.findViewById(R.id.endTime);
        bidPrice = rootView.findViewById(R.id.bidPrice);
        // 获取界面中的两个按钮
        bnAdd = rootView.findViewById(R.id.bnAdd);
        bnCancel = rootView.findViewById(R.id.bnCancel);
        // 为取消按钮的单击事件绑定事件监听器
        bnCancel.setOnClickListener(new HomeListener(getActivity()));
        // 定义发送请求的URL
        String url = HttpUtil.BASE_URL + "getItem.jsp?itemId=" + getArguments().getInt(ITEM_ID_KEY);
        try
        {
            // 获取指定的拍卖物品
            jsonObj = new JSONObject(HttpUtil.getRequest(url));
            // 使用文本框来显示拍卖物品的详情
            itemName.setText(jsonObj.getString("name"));
            itemDesc.setText(jsonObj.getString("desc"));
            itemRemark.setText(jsonObj.getString("remark"));
            itemKind.setText(jsonObj.getString("kind"));
            initPrice.setText(jsonObj.getString("initPrice"));
            maxPrice.setText(jsonObj.getString("maxPrice"));
            endTime.setText(jsonObj.getString("endTime"));
        }
        catch (Exception e1)
        {
            DialogUtil.showDialog(getActivity(), R.string.server_response_error, false);
            e1.printStackTrace();
        }
        bnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    // 执行类型转换
                    double curPrice = Double.parseDouble(
                            bidPrice.getText().toString());
                    // 执行输入校验
                    if(curPrice < jsonObj.getDouble("maxPrice"))
                    {
                        DialogUtil.showDialog(getActivity(), R.string.price_warning, false);
                    }
                    else
                    {
                        // 添加竞价
                        String result = addBid(jsonObj.getString("id"), curPrice + "");
                        // 显示对话框
                        DialogUtil.showDialog(getActivity(), result , true);
                    }
                }
                catch(NumberFormatException ne)
                {
                    DialogUtil.showDialog(getActivity(), R.string.price_must_number, false);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    DialogUtil.showDialog(getActivity(), R.string.server_response_error, false);
                }
            }
        });
        return rootView;
    }

    private String addBid(String itemId , String bidPrice)
            throws Exception
    {
        // 使用Map封装请求参数
        Map<String , String> map = new HashMap<>();
        map.put(ITEM_ID_KEY , itemId);
        map.put("bidPrice" , bidPrice);
        // 定义请求将会发送到addKind.jsp页面
        String url = HttpUtil.BASE_URL + "addBid.jsp";
        // 发送请求
        return HttpUtil.postRequest(url , map);
    }

    public static AddBidFragment newInstance(int itemId)
    {
        AddBidFragment fragment = new AddBidFragment();
        Bundle args = new Bundle();
        args.putInt(ITEM_ID_KEY, itemId);
        fragment.setArguments(args);
        return fragment;
    }
}
