package com.example.mtk14060.auctionclient;

import android.content.Context;
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

public class ChooseItemFragment extends Fragment {
    public static final int ADD_BID = 0x1009;
    private static final String KIND_ID_KEY = "kindId";

    Button bnHome;
    ListView succList;
    TextView viewTitle;
    Callbacks mCallbacks;

    //重新该方法，该方法返回的View将作为Fragment显示的组件
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_item, container, false);
        bnHome = rootView.findViewById(R.id.bn_home);
        succList = rootView.findViewById(R.id.succList);
        viewTitle = rootView.findViewById(R.id.view_title);
        bnHome.setOnClickListener(new HomeListener(getActivity()));
        long kindId = getArguments().getLong(KIND_ID_KEY);
        String url = HttpUtil.BASE_URL + "itemList.jsp?kindId=" + kindId;
        try
        {
            //根据种类ID获取该种类对应的所有物品
            JSONArray jsonArray = new JSONArray(HttpUtil.getRequest(url));
            JSONArrayAdapter jsonArrayAdapter = new JSONArrayAdapter(getActivity(),
                    jsonArray, "name", true);
            succList.setAdapter(jsonArrayAdapter);
        }
        catch (Exception e)
        {
            DialogUtil.showDialog(getActivity(), R.string.server_response_error, false);
            e.printStackTrace();
        }
        viewTitle.setText(R.string.item_list);
        succList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jsonObject = (JSONObject)succList.getAdapter().getItem(position);
                Bundle bundle = new Bundle();
                try
                {
                    bundle.putInt("itemId", jsonObject.getInt("id"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                mCallbacks.onItemSelected(ADD_BID, bundle);
            }
        });
        return rootView;
    }

    //当该Fragment被添加、显示到所属Activity时，回调该方法
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //如果该Fragment所属的Activity没有实现Callbacks接口，则抛出异常
        if (!(context instanceof Callbacks))
        {
            throw new IllegalStateException(getActivity().getResources().getString(R.string.imp_callbacks_chooseItem));
        }
        mCallbacks = (Callbacks)context;
    }

    //当该Fragment从它所属的Activity中被删除时回调该方法
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static ChooseItemFragment newInstance(long kindId)
    {
        ChooseItemFragment fragment = new ChooseItemFragment();
        Bundle args = new Bundle();
        args.putLong(KIND_ID_KEY, kindId);
        fragment.setArguments(args);
        return fragment;
    }
}
