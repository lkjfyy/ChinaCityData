package com.lkj.chinacitydata.bean;

import java.util.List;

/**
 * 地级市
 * @author jx on 2018/4/12.
 */

public class City {
    private String code;
    private String name;
    private List<Area> cityList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Area> getAreaList() {
        return cityList;
    }

    public void setAreaList(List<Area> areaList) {
        this.cityList = areaList;
    }
}
