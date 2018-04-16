package edu.hnie.weather.gson;

/**
 * 实况天气
 */
public class Now {
    private String tmp;//温度
    private String cond_txt;//天气
    private String wind_dir;//风向
    private String wind_sc;//风力
    private String wind_spd;//风速
    private String vis;//能见度

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

    public String getWind_dir() {
        return wind_dir;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    public String getWind_sc() {
        return wind_sc;
    }

    public void setWind_sc(String wind_sc) {
        this.wind_sc = wind_sc;
    }

    public String getWind_spd() {
        return wind_spd;
    }

    public void setWind_spd(String wind_spd) {
        this.wind_spd = wind_spd;
    }

    public String getVis() {
        return vis;
    }

    public void setVis(String vis) {
        this.vis = vis;
    }
}
