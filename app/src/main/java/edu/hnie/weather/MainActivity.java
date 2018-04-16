package edu.hnie.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import edu.hnie.weather.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //key=1ae15dd8174c4096b98f404feff0d97a
    //bc0418b57b2d4918819d3974ac1285d9
    private List<String> weatherList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        List<String> weatherList = Utility.initWeatherList(MainActivity.this);
        if (weatherList != null && weatherList.size() > 0) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, SelectCityActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
