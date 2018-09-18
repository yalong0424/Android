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

import com.example.mtk14060.auctionclient.util.DialogUtil;
import com.example.mtk14060.auctionclient.util.HttpUtil;

import org.json.JSONArray;

public class ChooseKindFragment extends Fragment
{
    public static final int CHOOSE_ITEM = 0x1008;

    Callbacks mCallbacks;
    Button bnHome;
    ListView kindList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.choose_kind, container, false);
        bnHome = rootView.findViewById(R.id.bn_home);
        kindList = rootView.findViewById(R.id.kindList);
        bnHome.setOnClickListener(new HomeListener(getActivity()));
        String url = HttpUtil.BASE_URL + "viewKind.jsp";
        try
        {
            //向指定URL发送请求，并将服务器响应包装成JSONArray对象
            JSONArray jsonArray = new JSONArray(HttpUtil.getRequest(url));
            kindList.setAdapter(new KindArrayAdapter(jsonArray, getActivity()));
        }
        catch (Exception e)
        {
            DialogUtil.showDialog(getActivity(), R.string.server_response_error, false);
            e.printStackTrace();
        }
        kindList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putLong("kindId", id);
                mCallbacks.onItemSelected(CHOOSE_ITEM, bundle);
            }
        });
        return rootView;
    }

    //当该Fragment被添加、显示到所属的Activity时回调该方法
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //判断该Fragment所属Activity是否实现了Callbacks接口，没有实现则抛出异常
        if (!(context instanceof Callbacks))
        {
            throw new IllegalStateException(getActivity().getResources().getString(R.string.imp_callbacks_choosekind));
        }
        mCallbacks = (Callbacks)context;
    }

    //当将该Fragment从它所属的Activity中删除时回调该方法
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
