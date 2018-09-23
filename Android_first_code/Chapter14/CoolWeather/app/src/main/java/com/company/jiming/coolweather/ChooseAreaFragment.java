package com.company.jiming.coolweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.company.jiming.coolweather.db.City;
import com.company.jiming.coolweather.db.Country;
import com.company.jiming.coolweather.db.Province;

import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于遍历省市县数据的Fragment
 * */
public class ChooseAreaFragment extends Fragment {
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTRY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    private static final String CHINA_URL = "http://guolin.tech/api/china";
    private static final String API_KEY = "50894d7e048c42348099d5d4974a58a9";
    private static final String PROVINCE = "province";
    private static final String CITY = "city";
    private static final String COUNTY = "county";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }
                else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTRY) {
                    queryCities();
                }
                else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果数据库中没有查询到再去服务器上查询。
     * */
    private void queryProvinces() {
        titleText.setText(R.string.china);
        backButton.setVisibility(View.GONE); //不可见且不占据屏幕空间
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province: provinceList) {
                dataList.add(province.getProvinceName());
            }
            //数据源变更时需要让ListView刷新显示
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }
        else {
            queryFromServer(CHINA_URL, PROVINCE);
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果数据库中没有查询到再去服务器上查询。
     * */
    private void queryCities() {
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果数据库中没有查询到再去服务器上查询。
     * */
    private void queryCounties() {
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     * */
    private void queryFromServer(String chinaUrl, String province) {
    }
}
