package edu.hnie.weather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import edu.hnie.weather.R;
import edu.hnie.weather.WeatherActivity;
import edu.hnie.weather.dao.City;
import edu.hnie.weather.dao.County;
import edu.hnie.weather.dao.Province;
import edu.hnie.weather.gson.Weather;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用工具类
 */
public class Utility {

    public static final String LOAD_PIC_BACKGROUND = "background";
    public static final String LOAD_PIC_FORECAST = "forecast";
    public static final String LOAD_PIC_HOURLY = "hourly";

    /**
     * 解析和处理服务器返回的省级数据并保存到数据库
     * @param response 解析数据
     * @return
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                // 将返回的数据解析成JSON数组
                JSONArray allProvince = new JSONArray(response);
                // 遍历JSON数组
                for (int i = 0; i < allProvince.length(); i++) {
                    //得到JSON数组中的第i个Json对象
                    JSONObject jsonObject = allProvince.getJSONObject(i);
                    //新建一个省份的实体类
                    Province province = new Province();
                    //将JSON对象中的数据保存到实体类中
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    //将数据保存到数据库
                    province.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * @param response 解析数据
     * @param provinceId 省id
     * @return
     */
    public static boolean handleCityResponse(String response,int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCity = new JSONArray(response);
                for (int i = 0; i < allCity.length(); i++) {
                    JSONObject jsonObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     * @param response 解析数据
     * @param cityId 城市id
     * @return
     */
    public static boolean handleCountyResponse(String response,int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounty = new JSONArray(response);
                for (int i = 0; i < allCounty.length(); i++) {
                    JSONObject jsonObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析
     * @param response
     * @return
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据天气加载图片
     * @param condTxt
     * @return
     */
    public static int loadPic(String condTxt,String type){
        if (LOAD_PIC_BACKGROUND.equals(type)) {
            if (condTxt != null) {
                if (condTxt.contains("晴")) {
                    return R.drawable.bg_sun;
                } else if (condTxt.contains("多云")) {
                    return R.drawable.bg_cloudy;
                } else if (condTxt.contains("阴") || condTxt.contains("雾")) {
                    return R.drawable.bg_overcost;
                } else if (condTxt.contains("雨")) {
                    return R.drawable.bg_rain;
                } else if (condTxt.contains("雪")) {
                    return R.drawable.bg_snow;
                }
            }
        }else if (LOAD_PIC_HOURLY.equals(type)){
            if (condTxt != null) {
                if (condTxt.contains("晴")) {
                    return R.drawable.icon_sun_hourly;
                } else if (condTxt.contains("多云")) {
                    return R.drawable.icon_cloudy_hourly;
                } else if (condTxt.contains("阴") || condTxt.contains("雾")) {
                    return R.drawable.icon_overcost_hourly;
                } else if (condTxt.contains("雨")) {
                    return R.drawable.icon_rain_hourly;
                } else if (condTxt.contains("雪")) {
                    return R.drawable.icon_snow_hourly;
                }
            }
        } else if (LOAD_PIC_FORECAST.equals(type)) {
            if (condTxt != null) {
                if (condTxt.contains("晴")) {
                    return R.drawable.icon_sun_forecast;
                } else if (condTxt.contains("多云")) {
                    return R.drawable.icon_cloudy_forecast;
                } else if (condTxt.contains("阴") || condTxt.contains("雾")) {
                    return R.drawable.icon_overcost_forecast;
                } else if (condTxt.contains("雨")) {
                    return R.drawable.icon_rain_forecast;
                } else if (condTxt.contains("雪")) {
                    return R.drawable.icon_snow_forecast;
                }
            }
        }
        return 0;
    }

    public static List<String> initWeatherList(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int weatherListSize = preferences.getInt("weatherListSize", 0);
        List<String> weatherList = new ArrayList<>();
        if (weatherListSize > 0) {
            for (int i = 0; i < weatherListSize; i++) {
                String weatherStr = preferences.getString("weatherItem_" + i, null);
                weatherList.add(weatherStr);
            }
        }
        return weatherList;
    }
}
