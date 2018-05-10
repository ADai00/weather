package edu.hnie.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import edu.hnie.weather.gson.Weather;
import edu.hnie.weather.utils.HttpUtils;
import edu.hnie.weather.utils.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //得到更新时间间隔
        String auto_update_time = preferences.getString("auto_update_time", null);
        // 更新天气
        updateWeather();
        // 启动定时任务
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60 * 60 * 1000;
        if (auto_update_time != null) {
            anHour *= Integer.parseInt(auto_update_time.substring(0, 1));
        }
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final List<String> weatherList = Utility.initWeatherList(AutoUpdateService.this);
        if (weatherList != null && weatherList.size() > 0) {
            Weather weather = Utility.handleWeatherResponse(weatherList.get(0));
            String weatherId = weather.basic.getCid();
            final String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" +
                    weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";

            HttpUtils.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseStr = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseStr);
                    if (weather != null && "ok".equals(weather.status)) {
                        List<String> tempList = new ArrayList<>();
                        for (String weatherStr : weatherList) {
                            if (weatherStr.contains(weather.basic.getCid())) {
                                tempList.add(responseStr);
                                continue;
                            }
                            tempList.add(weatherStr);
                        }
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("weatherListSize", tempList.size());
                        for (int i = 0; i < tempList.size(); i++) {
                            editor.putString("weatherItem_" + i, tempList.get(i));
                        }
                        editor.apply();
                    }
                }
            });
        }
    }

}
