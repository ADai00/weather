package edu.hnie.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class SettingActivity extends AppCompatActivity {
    private TextView commonTitleText;
    private Switch autoUpdateSwitch;
    private Button backWeatherButton;
    private RelativeLayout settingLayout;
    private TextView autoUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        commonTitleText = findViewById(R.id.common_title_text);
        commonTitleText.setText("设置");
        settingLayout = findViewById(R.id.setting_layout);
        autoUpdateTime= findViewById(R.id.auto_update_time);
        autoUpdateSwitch = findViewById(R.id.auto_update_switch);
        backWeatherButton = findViewById(R.id.back_weather_button);
        backWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor edit = preferences.edit();
        boolean auto_update = preferences.getBoolean("auto_update", false);
        String auto_update_time = preferences.getString("auto_update_time", null);
        if (auto_update_time != null){
            autoUpdateTime.setText(auto_update_time);
        }
        if (auto_update) {
            autoUpdateSwitch.setChecked(auto_update);
        } else {
            autoUpdateSwitch.setChecked(auto_update);
        }

        autoUpdateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    autoUpdateSwitch.setSwitchTextAppearance(SettingActivity.this,R.style.switch_true);
                    edit.putBoolean("auto_update", true);
                    edit.apply();
                } else {
                    autoUpdateSwitch.setSwitchTextAppearance(SettingActivity.this,R.style.switch_false);
                    edit.putBoolean("auto_update", false);
                    edit.apply();
                }
            }
        });

        settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopWindow(v);
            }
        });

    }

    private void initPopWindow(View v) {
        View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.setting_popup_item, null, false);
        RadioGroup radioGroup = view.findViewById(R.id.update_time_radio_group);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton button = (RadioButton) radioGroup.getChildAt(i);
            if(button.getText().toString().equals(autoUpdateTime.getText().toString())){
                button.setChecked(true);
            }
        }
        Button updateTimeButton_cancel = view.findViewById(R.id.update_time_button_cancel);
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

        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v, 800, 20);


        //设置popupWindow里的按钮的事件

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                settingUpdateTime(radioButton);
                popWindow.dismiss();
            }
        });

        updateTimeButton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });

    }

    private void settingUpdateTime(RadioButton radioButton) {
        autoUpdateTime.setText(radioButton.getText());
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("auto_update_time",radioButton.getText().toString());
        editor.apply();
    }
}
