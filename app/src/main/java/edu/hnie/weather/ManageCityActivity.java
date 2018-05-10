package edu.hnie.weather;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.hnie.weather.gson.Forecast;
import edu.hnie.weather.gson.Weather;
import edu.hnie.weather.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class ManageCityActivity extends AppCompatActivity {
    private LinearLayout cityListLayout;
    private FloatingActionButton addCityButton;
    private TextView commonTitleText;
    private Button backWeatherButton;

    private List<String> weatherList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_city);
        cityListLayout = findViewById(R.id.city_list_layout);
        addCityButton = findViewById(R.id.add_city_button);
        commonTitleText = findViewById(R.id.common_title_text);
        backWeatherButton = findViewById(R.id.back_button);
        commonTitleText.setText("管理城市");
        //给添加按钮设置监听事件
        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageCityActivity.this, SelectCityActivity.class);
                intent.putExtra("flag", true);
                startActivity(intent);
                finish();
            }
        });
        backWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageCityActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //初始化已经选择过的城市
        initManageCityList();
    }

    /**
     * 初始化已经选择过的城市
     */
    private void initManageCityList() {
        weatherList.clear();
        cityListLayout.removeAllViews();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int weatherListSize = preferences.getInt("weatherListSize", 0);
        //判断保存城市信息的list是否不为空；有就将数据显示出来；没有就跳到选择城市的页面
        if (weatherListSize > 0) {
            for (int i = 0; i < weatherListSize; i++) {
                String weatherStr = preferences.getString("weatherItem_" + i, null);
                weatherList.add(weatherStr);
            }
        } else {
            Intent intent = new Intent(ManageCityActivity.this, SelectCityActivity.class);
            startActivity(intent);
            finish();
        }

        if (weatherList != null && weatherList.size() > 0) {
            //显示天气信息
            showWeatherInfo();
        }
    }

    private void showWeatherInfo() {
        for (int i = 0; i < weatherList.size(); i++) {
            String weatherStr = weatherList.get(i);
            final Weather weather = Utility.handleWeatherResponse(weatherStr);
            View view = LayoutInflater.from(this).inflate(R.layout.manage_city_list_item, cityListLayout, false);
            TextView cityName = view.findViewById(R.id.city_name);
            ImageView cityItemImg = view.findViewById(R.id.city_item_img);
            TextView tmpMaxAndMin = view.findViewById(R.id.tmp_max_and_min);
            cityName.setText(weather.basic.getLocation());
            cityItemImg.setImageResource(Utility.loadPic(weather.now.getCond_txt(), Utility.LOAD_PIC_FORECAST));
            Forecast forecast = weather.forecastList.get(0);
            tmpMaxAndMin.setText(forecast.getTmp_max() + "℃" + " / " + forecast.getTmp_min() + "℃");
            boolean flag = false;
            if ((i + 1) % 3 == 1) {
                view.setBackgroundResource(R.drawable.beijing_bg);
                flag = true;
            }
            if (!flag) {
                if ((i + 1) % 3 == 0) {
                    view.setBackgroundResource(R.drawable.qingdao_bg);
                    flag = true;
                }
            }
            if (!flag) {
                if ((i + 1) % 3 == 2) {
                    view.setBackgroundResource(R.drawable.xianggang_bg);
                }
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ManageCityActivity.this, WeatherActivity.class);
                    intent.putExtra("weatherId", weather.basic.getCid());
                    startActivity(intent);
                    finish();
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    initPopWindow(v, weather.basic.getCid());
                    return true;
                }
            });
            cityListLayout.addView(view);
        }
    }

    /**
     *
     * @param v
     * @param weatherId
     */
    private void initPopWindow(View v, final String weatherId) {
        View view = LayoutInflater.from(ManageCityActivity.this).inflate(R.layout.manage_city_popup_item, null, false);
        TextView deleteCityButton = view.findViewById(R.id.delete_city_button);
        TextView cityToTop = view.findViewById(R.id.city_to_top);
        if (weatherList.get(0).contains(weatherId)) {
            cityToTop.setVisibility(View.GONE);
        }
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x000000));    //要为popWindow设置一个背景才有效


        int height = this.getWindowManager().getDefaultDisplay().getHeight();
        if (height - (int) (v.getY()) > 400) {
            //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
            popWindow.showAsDropDown(v, 300, 0);
        } else {
            popWindow.showAsDropDown(v, 300, -410);
        }


        //设置popupWindow里的按钮的事件
        deleteCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(ManageCityActivity.this)
                        .setMessage("确认删除？")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<String> tempList = new ArrayList<>();
                                for (String weatherStr : weatherList) {
                                    if (weatherStr.contains(weatherId)) {
                                        continue;
                                    }
                                    tempList.add(weatherStr);
                                }
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ManageCityActivity.this).edit();
                                editor.putInt("weatherListSize", tempList.size());
                                for (int i = 0; i < tempList.size(); i++) {
                                    editor.putString("weatherItem_" + i, tempList.get(i));
                                }
                                editor.apply();
                                initManageCityList();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                popWindow.dismiss();
            }
        });

        cityToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> tempList = new ArrayList<>();
                for (String weatherStr : weatherList) {
                    if (weatherStr.contains(weatherId)) {
                        tempList.add(0, weatherStr);
                        continue;
                    }
                    tempList.add(weatherStr);
                }
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ManageCityActivity.this).edit();
                editor.putInt("weatherListSize", tempList.size());
                for (int i = 0; i < tempList.size(); i++) {
                    editor.putString("weatherItem_" + i, tempList.get(i));
                }
                editor.apply();
                initManageCityList();
                popWindow.dismiss();
                Toast.makeText(ManageCityActivity.this, "已置顶", Toast.LENGTH_SHORT).show();
            }
        });

    }
}


