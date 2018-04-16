package edu.hnie.weather.gson;

/**
 * 逐小时预报
 */
public class Hourly {
    private String tmp;//温度
    private String cond_txt;//天气
    private String time;//时间

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getCond_txt() {
        return cond_txt;
    }

    public void setCond_txt(String cond_txt) {
        this.cond_txt = cond_txt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
