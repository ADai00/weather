package edu.hnie.weather.gson;

/**
 * 更新时间
 */
public class Update {
    private String loc;//当地时间，24小时制，格式yyyy-MM-dd HH:mm

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
