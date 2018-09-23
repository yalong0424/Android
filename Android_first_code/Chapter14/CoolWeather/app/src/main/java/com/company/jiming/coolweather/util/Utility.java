package com.company.jiming.coolweather.util;

import android.text.TextUtils;

import com.company.jiming.coolweather.db.City;
import com.company.jiming.coolweather.db.County;
import com.company.jiming.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析和处理JSON数据的工具类。
 * 由于此处JSON数据比较简单，故选择使用org.json中的JSONObject和JSONArray来解析，而不是用GSON解析
 * */
public class Utility {
    private static final String COL_NAME = "name";
    private static final String COL_ID = "id";
    private static final String COL_WEATHER_ID = "weather_id";

    /**
     * 解析和处理服务器返回的省级数据
     * */
    public static boolean handleProvinceResponse(String response)
    {
        if (TextUtils.isEmpty(response))
        {
            return false;
        }
        try {
            JSONArray allProvinces = new JSONArray(response);
            for (int i = 0; i < allProvinces.length(); ++i)
            {
                JSONObject provinceObject = allProvinces.getJSONObject(i);
                Province province = new Province();
                province.setProvinceName(provinceObject.getString(COL_NAME));
                province.setProvinceCode(provinceObject.getInt(COL_ID));
                province.save(); //将实体类数据存储到数据库中
            }
            return true;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * */
    public static boolean handleCityResponce(String response, int provinceId)
    {
        if (!TextUtils.isEmpty(response))
        {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); ++i)
                {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString(COL_NAME));
                    city.setCityCode(cityObject.getInt(COL_ID));
                    city.setProvinceId(provinceId);
                    city.save(); //将实体类数据写到数据库中
                }
                return true;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     * */
    public static boolean handleCountyResponce(String response, int cityId)
    {
        if (!TextUtils.isEmpty(response))
        {
            try {
                JSONArray allCountries = new JSONArray(response);
                for (int i = 0; i < allCountries.length(); ++i)
                {
                    JSONObject countryObject = allCountries.getJSONObject(i);
                    County country = new County();
                    country.setCountyName(countryObject.getString(COL_NAME));
                    country.setWeatherId(countryObject.getString(COL_WEATHER_ID));
                    country.setCityId(cityId);
                    country.save(); //将数据存储到数据库中
                }
                return true;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }
}
