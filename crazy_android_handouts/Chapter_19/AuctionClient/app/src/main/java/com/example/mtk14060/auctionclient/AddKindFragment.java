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

import com.example.mtk14060.auctionclient.util.DialogUtil;
import com.example.mtk14060.auctionclient.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class AddKindFragment extends Fragment {
    EditText etKindName, etKindDesc;
    Button bnAdd, bnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_kind, container, false);
        etKindName = rootView.findViewById(R.id.kindName);
        etKindDesc = rootView.findViewById(R.id.kindDesc);
        bnAdd = rootView.findViewById(R.id.bnAdd);
        bnCancel = rootView.findViewById(R.id.bnCancel);
        bnCancel.setOnClickListener(new HomeListener(getActivity()));
        bnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                {
                    String name = etKindName.getText().toString();
                    String desc = etKindDesc.getText().toString();
                    try
                    {
                        String result = addKind(name, desc);
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
        String name = etKindName.getText().toString().trim();
        if (name.equals(""))
        {
            DialogUtil.showDialog(getActivity(), R.string.kind_name_must_exist, false);
            return false;
        }
        return true;
    }

    private String addKind(String name, String desc) throws Exception
    {
        //使用Map封装请求参数
        Map<String, String> map = new HashMap<>();
        map.put("kindName", name);
        map.put("kindDesc", desc);
        //定义发送请求的URL
        String url = HttpUtil.BASE_URL + "addKind.jsp";
        return HttpUtil.postRequest(url, map);
    }
}
