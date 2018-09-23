package com.company.jiming.coolweather.db;

import org.litepal.crud.LitePalSupport;

/**
 * 数据库City表对应的City实体类
 * 实体类名即表名，实体类中的每一个字段都对应着数据库表中的一个属性列。
 * */
public class City extends LitePalSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
