package edu.hnie.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import edu.hnie.weather.utils.Utility;

public class MainActivity extends AppCompatActivity {
    //key=1ae15dd8174c4096b98f404feff0d97a
    //bc0418b57b2d4918819d3974ac1285d9
    private List<String> weatherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> weatherList = Utility.initWeatherList(MainActivity.this);
        if (weatherList != null && weatherList.size() > 0) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        } else {
            //相当与送信
            Intent intent = new Intent(this, SelectCityActivity.class);
            //开始送信
            startActivity(intent);
            finish();
        }
    }
}
