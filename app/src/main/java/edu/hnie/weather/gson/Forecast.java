package edu.hnie.weather.gson;

/**
 * 天气预报
 */
public class Forecast {
    private String date;//预报日期
    private String cond_txt_d;//白天天气状况描述
    private String tmp_max;//最高温度
    private String tmp_min;//最低温度
    private String uv_index;//紫外线强度

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCond_txt_d() {
        return cond_txt_d;
    }

    public void setCond_txt_d(String cond_txt_d) {
        this.cond_txt_d = cond_txt_d;
    }

    public String getTmp_max() {
        return tmp_max;
    }

    public void setTmp_max(String tmp_max) {
        this.tmp_max = tmp_max;
    }

    public String getTmp_min() {
        return tmp_min;
    }

    public void setTmp_min(String tmp_min) {
        this.tmp_min = tmp_min;
    }

    public String getUv_index() {
        return uv_index;
    }

    public void setUv_index(String uv_index) {
        this.uv_index = uv_index;
    }
}
