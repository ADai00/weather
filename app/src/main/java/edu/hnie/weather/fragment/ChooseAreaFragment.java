package edu.hnie.weather.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.hnie.weather.ManageCityActivity;
import edu.hnie.weather.R;
import edu.hnie.weather.WeatherActivity;
import edu.hnie.weather.dao.City;
import edu.hnie.weather.dao.County;
import edu.hnie.weather.dao.Province;
import edu.hnie.weather.utils.HttpUtils;
import edu.hnie.weather.utils.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView chooseAreaTextView;
    private Button backButton;
    private ListView chooseAreaListView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;//省列表
    private List<City> cityList;//市列表
    private List<County> countyList;//县列表
    private Province selectedProvince;//选中的省份
    private City selectedCity;//选中的城市
    private int currentLevel;//当前级别

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        chooseAreaTextView = view.findViewById(R.id.choose_area_title);
        backButton = view.findViewById(R.id.choose_area_back_button);
        chooseAreaListView = view.findViewById(R.id.choose_area_list_view);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        chooseAreaListView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chooseAreaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果当前等级
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    //遍历当前省份下面的城市
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weatherId", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                } else if(currentLevel == LEVEL_PROVINCE){
                    Intent intent = new Intent(getActivity(),ManageCityActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 遍历全国所有的省份
     */
    private void queryProvinces() {
        boolean flag = getActivity().getIntent().getBooleanExtra("flag", false);
        //设置标题
        chooseAreaTextView.setText("中国");
        if (flag) {
            //将返回按钮显示
            backButton.setVisibility(View.VISIBLE);
        }else {
            //将返回按钮隐藏
            backButton.setVisibility(View.GONE);
        }
        //查找数据库
        provinceList = DataSupport.findAll(Province.class);
        //判断查找到的省份的数据的大小是否大于0
        if (provinceList.size() > 0) {
            //将用来显示省市数据的List清空
            dataList.clear();
            //遍历省份的list集合
            for (Province province : provinceList) {
                //将省份的名字添加到dataList集合中
                dataList.add(province.getProvinceName());
            }
            //
            adapter.notifyDataSetChanged();
            //设置ListView的选择位置为0
            chooseAreaListView.setSelection(0);
            //设置当前的等级
            currentLevel = LEVEL_PROVINCE;
        } else {//判断查找到的省份的数据的大小等于0
            //访问API
            String address = "http://guolin.tech/api/china";
            //调用该方法访问API
            queryFromServer(address, "province");
        }
    }


    //
    private void queryCities() {
        // 设置标题
        chooseAreaTextView.setText(selectedProvince.getProvinceName());
        // 返回按钮
        backButton.setVisibility(View.VISIBLE);
        //
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId()))
                .find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            chooseAreaListView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    private void queryCounties() {
        chooseAreaTextView.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId()))
                .find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            chooseAreaListView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 访问API
     *
     * @param address url地址
     * @param type    类型
     */
    private void queryFromServer(String address, final String type) {
        //显示正在加载。。。
        showProgressDialog();
        //进行网络请求
        HttpUtils.sendOkHttpRequest(address, new Callback() {

            //请求失败
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭正在加载的显示
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }


            //请求成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //拿到返回的省份数据
                String responseText = response.body().string();
                // 判断解析是否成功
                boolean result = false;

                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 在屏幕正中间显示正在加载。。。
     */
    private void showProgressDialog() {
        //判断progressDialog是否为空
        if (progressDialog == null) {
            //新建一个ProgressDialog
            progressDialog = new ProgressDialog(getActivity());
            //设置显示的信息
            progressDialog.setMessage("正在加载...");
            //设置点击其他地方不消失
            progressDialog.setCanceledOnTouchOutside(false);
        }
        //将progressDialog显示在屏幕中
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
