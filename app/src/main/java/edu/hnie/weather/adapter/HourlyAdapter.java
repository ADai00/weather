package edu.hnie.weather.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.hnie.weather.R;
import edu.hnie.weather.gson.Hourly;
import edu.hnie.weather.utils.Utility;

import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private List<Hourly> hourlyList;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView weatherHourlyTime;
        ImageView weatherHourlyImg;
        TextView weatherHourlyTmp;

        public ViewHolder(View view) {
            super(view);
            weatherHourlyTime = view.findViewById(R.id.weather_hourly_time);
            weatherHourlyImg = view.findViewById(R.id.weather_hourly_img);
            weatherHourlyTmp = view.findViewById(R.id.weather_hourly_tmp);
        }
    }

    public HourlyAdapter(List<Hourly> hourlyList) {
        this.hourlyList = hourlyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weather_hourly_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(hourlyList.size() > 0 && hourlyList != null) {
            Hourly hourly = hourlyList.get(i);
            viewHolder.weatherHourlyTime.setText(hourly.getTime().substring(hourly.getTime().indexOf(" ")));
            viewHolder.weatherHourlyImg.setImageResource(Utility.loadPic(hourly.getCond_txt(),Utility.LOAD_PIC_HOURLY));
            viewHolder.weatherHourlyTmp.setText(hourly.getTmp() + "℃");
        }
    }

    @Override
    public int getItemCount() {
        return hourlyList.size();
    }


}
