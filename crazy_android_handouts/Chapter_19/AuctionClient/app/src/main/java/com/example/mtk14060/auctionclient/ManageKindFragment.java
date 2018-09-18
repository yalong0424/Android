package com.example.mtk14060.auctionclient;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.mtk14060.auctionclient.util.DialogUtil;
import com.example.mtk14060.auctionclient.util.HttpUtil;

import org.json.JSONArray;

public class ManageKindFragment extends Fragment
{
    public static final int ADD_KIND = 0x1007;

    Button bnHome, bnAdd;
    ListView kindList;
    Callbacks mCallbacks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.manage_kind, container, false);
        bnHome = rootView.findViewById(R.id.bn_home);
        bnAdd = rootView.findViewById(R.id.bnAdd);
        kindList = rootView.findViewById(R.id.kindList);
        bnHome.setOnClickListener(new HomeListener(getActivity()));
        bnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用Fragment所在的Activity的onItemSelected()方法
                mCallbacks.onItemSelected(ADD_KIND, null);
            }
        });
        //定义发送请求的URL
        String url = HttpUtil.BASE_URL + "viewKind.jsp";
        try
        {
            //向指定URL发送请求，并把服务器响应包装成JSONArray对象
            final JSONArray jsonArray = new JSONArray(HttpUtil.getRequest(url));
            //把JSONArray数组包装成Adapter，供ListView进行显示
            kindList.setAdapter(new KindArrayAdapter(jsonArray, getActivity()));
        }
        catch (Exception e)
        {
            DialogUtil.showDialog(getActivity(), R.string.server_response_error, false);
            e.printStackTrace();
        }
        return rootView;
    }

    //当该Fragment被添加、显示到Activity时，回调该方法
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //如果Activity没有实现Callbacks接口，则抛出异常
        if (!(context instanceof Callbacks))
        {
            throw new IllegalStateException(getActivity().getResources().getString(R.string.imp_callbacks_managekind));
        }
        mCallbacks = (Callbacks)context;
    }

    //当该Fragment从它所属的Activity中被删除时，回调该方法

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
