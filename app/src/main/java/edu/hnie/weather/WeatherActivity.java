package edu.hnie.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.hnie.weather.adapter.HourlyAdapter;
import edu.hnie.weather.gson.Forecast;
import edu.hnie.weather.gson.LifeStyle;
import edu.hnie.weather.gson.Weather;
import edu.hnie.weather.service.AutoUpdateService;
import edu.hnie.weather.utils.DateUtils;
import edu.hnie.weather.utils.HttpUtils;
import edu.hnie.weather.utils.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {


    private SwipeRefreshLayout weatherRefreshLayout;
    private ScrollView weatherLayout;
    private TextView weatherTitleCity;
    private TextView weatherNowTmp;
    private TextView weatherNowCondTxt;
    private LinearLayout weatherForecastLayout;
    private RecyclerView weatherHourlyLayout;
    private ImageView weatherImgView;
    private TextView weatherNowWind;
    private TextView weatherNowVis;
    private TextView weatherLifestyleDrsgTxt;
    private TextView weatherLifestyleComfTxt;
    private TextView weatherForecastUvIndex;
    private TextView selectCityButton;
    private TextView weatherSetting;
    private TextView weatherCalendar;
    private TextView weatherHourlyTmpMaxAndMin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherLayout = findViewById(R.id.weather_layout);
        weatherTitleCity = findViewById(R.id.weather_title_city);
        weatherNowTmp = findViewById(R.id.weather_now_tmp);
        weatherNowCondTxt = findViewById(R.id.weather_now_cond_txt);
        weatherForecastLayout = findViewById(R.id.weather_forecast_layout);
        weatherHourlyLayout = findViewById(R.id.weather_hourly_layout);
        weatherNowWind = findViewById(R.id.weather_now_wind);
        weatherNowVis = findViewById(R.id.weather_now_vis);
        weatherForecastUvIndex = findViewById(R.id.weather_forecast_uv_index);
        weatherLifestyleDrsgTxt = findViewById(R.id.weather_lifestyle_drsg_txt);
        weatherLifestyleComfTxt = findViewById(R.id.weather_lifestyle_comf_txt);
        selectCityButton = findViewById(R.id.select_city_button);
        weatherSetting = findViewById(R.id.weather_setting);
        weatherCalendar = findViewById(R.id.weather_calendar);
        weatherImgView = findViewById(R.id.weather_img);
        weatherRefreshLayout = findViewById(R.id.weather_refresh_layout);
        weatherRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        weatherHourlyTmpMaxAndMin = findViewById(R.id.weather_hourly_tmp_max_and_min);

        List<String> weatherList = Utility.initWeatherList(WeatherActivity.this);
        final String weatherId;
        // 判断是否传入了叫weatherId的数据
        if (getIntent().getStringExtra("weatherId") != null) {
            // 拿到传入的数据
            weatherId = getIntent().getStringExtra("weatherId");
            // 设置显示
            weatherLayout.setVisibility(View.VISIBLE);
            // 通过传入的weatherId访问API
            requestWeather(weatherId);
        } else {
            if (weatherList != null && weatherList.size() > 0) {
                Weather weather = Utility.handleWeatherResponse(weatherList.get(0));
                weatherId = weather.basic.getCid();
                showWeatherInfo(weather);
                //加载背景图片
                weatherImgView.setImageResource(Utility.loadPic(weather.now.getCond_txt(), Utility.LOAD_PIC_BACKGROUND));
            } else {
                weatherId = null;
            }
        }

        //
        weatherRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        selectCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, ManageCityActivity.class);
                startActivity(intent);
                finish();
            }
        });
        weatherSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
        weatherCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, CalendarActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void requestWeather(String weatherId) {
        if (weatherId == null) {
            throw new IllegalArgumentException("");
        }
        String url = "https://free-api.heweather.com/s6/weather?location=" +
                weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtils.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        weatherRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 拿到返回的数据
                final String responseStr = response.body().string();
                // 返回的数据解析并保存到天气实体类中
                final Weather weather = Utility.handleWeatherResponse(responseStr);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 判断天气是否为空且 状态为ok 表示成功
                        if (weather != null && "ok".equals(weather.status)) {

                            // 用来标记没有相同的weatherId
                            boolean flag = false;
                            // 创建一个临时的list
                            List<String> tempList = new ArrayList<>();

                            List<String> weatherList = Utility.initWeatherList(WeatherActivity.this);
                            //判断是否已经存在该weatherId
                            if (weatherList != null && weatherList.size() > 0) {
                                //遍历以及存在的天气的List
                                for (String weatherStr : weatherList) {
                                    //判断是否包含weatherId
                                    if (weatherStr.contains(weather.basic.getCid())) {
                                        tempList.add(responseStr);
                                        flag = true;
                                        continue;
                                    }
                                    tempList.add(weatherStr);
                                }
                            }

                            //
                            if (!flag) {
                                weatherList.add(responseStr);
                            } else {
                                weatherList = new ArrayList<>(tempList);
                            }
                            //天气信息数据保存到手机中
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putInt("weatherListSize", weatherList.size());
                            for (int i = 0; i < weatherList.size(); i++) {
                                editor.putString("weatherItem_" + i, weatherList.get(i));
                            }
                            editor.apply();

                            //将天气信息显示在界面上
                            showWeatherInfo(weather);

                            //加载背景图片
                            weatherImgView.setImageResource(Utility.loadPic(weather.now.getCond_txt(), Utility.LOAD_PIC_BACKGROUND));
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        }

                        weatherRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

    }


    /**
     *
     * 显示天气
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        weatherTitleCity.setText(weather.basic.getLocation());
        weatherNowTmp.setText(weather.now.getTmp() + "℃");
        weatherNowCondTxt.setText(weather.now.getCond_txt());
        weatherNowWind.setText("风向风速: " + weather.now.getWind_dir() + " " + weather.now.getWind_spd() + "公里 / 小时");
        weatherNowVis.setText("能见度: " + weather.now.getVis() + "公里");

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        weatherHourlyLayout.setLayoutManager(manager);
        HourlyAdapter hourlyAdapter = new HourlyAdapter(weather.hourlyList);
        weatherHourlyLayout.setAdapter(hourlyAdapter);

        weatherForecastLayout.removeAllViews();
        for (int i = 0; i < weather.forecastList.size(); i++) {
            Forecast forecast = weather.forecastList.get(i);
            if (i == 0) {
                weatherHourlyTmpMaxAndMin.setText(forecast.getTmp_max() + "℃" + " / " + forecast.getTmp_min() + "℃");
                weatherForecastUvIndex.setText("紫外线指数: " + forecast.getUv_index());
                continue;
            }
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.weather_forecast_item, weatherForecastLayout, false);
            TextView weatherForecastDate = view.findViewById(R.id.weather_forecast_date);
            ImageView weatherForecastCondTxtImg = view.findViewById(R.id.weather_forecast_cond_txt_img);
            TextView weatherForecastTmpMaxAndMin = view.findViewById(R.id.weather_forecast_tmp_max_and_min);
            if (i == 1) {
                weatherForecastDate.setText(forecast.getDate().substring(forecast.getDate().indexOf("-") + 1) + "明天");
            } else {
                weatherForecastDate.setText(forecast.getDate().substring(forecast.getDate().indexOf("-") + 1) + DateUtils.dayForWeek(forecast.getDate()));
            }
            weatherForecastCondTxtImg.setImageResource(Utility.loadPic(forecast.getCond_txt_d(), Utility.LOAD_PIC_FORECAST));
            weatherForecastTmpMaxAndMin.setText(forecast.getTmp_max() + "℃" + " / " + forecast.getTmp_min() + "℃");
            weatherForecastLayout.addView(view);
        }
        for (LifeStyle lifeStyle : weather.lifeStyleList) {
            if ("drsg".equals(lifeStyle.getType())) {
                weatherLifestyleDrsgTxt.setText("穿衣指数: " + lifeStyle.getTxt());
                continue;
            }
            if ("comf".equals(lifeStyle.getType())) {
                weatherLifestyleComfTxt.setText("舒适度: " + lifeStyle.getTxt());
                continue;
            }
        }
        weatherLayout.setVisibility(View.VISIBLE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean auto_update = preferences.getBoolean("auto_update", false);
        if(auto_update) {
            Log.d("WeatherActivity: ", "我的自动更新服务启动了。。。。 ");
            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
            startService(intent);
        }

    }

}
