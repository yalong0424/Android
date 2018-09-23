package com.company.jiming.coolweather.db;

import org.litepal.crud.LitePalSupport;

/**
 * 数据库Province表的Province实体类
 * 类名即表名，实体类中的每一个字段都对应着数据库表中的一个列名。
 * */
public class Province extends LitePalSupport {
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
