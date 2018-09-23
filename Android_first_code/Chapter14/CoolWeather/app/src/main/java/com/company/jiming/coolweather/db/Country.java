package com.company.jiming.coolweather.db;

import org.litepal.crud.LitePalSupport;

/**
 * 数据库Country表对应的实体类Country
 * 实体类名即表名，实体类中的每一个字段都对应着数据库中表的一个列。
 * */
public class Country extends LitePalSupport {
    private int id;
    private String countryName;
    private String weatherId;
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
