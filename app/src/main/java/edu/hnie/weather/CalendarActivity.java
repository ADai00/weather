package edu.hnie.weather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    private Button backBtn;
    private TextView commonTitleText;
    private CalendarView calendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        backBtn = findViewById(R.id.back_button);
        commonTitleText = findViewById(R.id.common_title_text);
        calendarView = findViewById(R.id.calendar_view);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this,WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });
        commonTitleText.setText("日历");
        calendarView.setDate(new Date().getTime());
    }
}
