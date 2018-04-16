package edu.hnie.weather.gson;

/**
 * 基础信息
 */
public class Basic {
    private String location;//城市名称
    private String cid;//城市id

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
