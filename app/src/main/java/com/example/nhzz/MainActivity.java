package com.example.nhzz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.example.nhzz.databinding.ActivityMainBinding;

import static com.baidu.mapapi.map.MyLocationConfiguration.*;

public class MainActivity extends BasicActivity {
    private ActivityMainBinding binding;
    private BaiduMap baiduMap;
    private LocationClient mLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        request();

        baiduMap = binding.bmapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(binding.getRoot().getContext());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

//设置locationClientOption
        mLocationClient.setLocOption(option);

//注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
//开启地图定位图层
        mLocationClient.start();

        LocationMode mode = LocationMode.FOLLOWING;
        MyLocationConfiguration mLocationConfiguration = new MyLocationConfiguration(mode,
                true, null, 0, 0);
        baiduMap.setMyLocationConfiguration(mLocationConfiguration);
    }

    @Override
    protected void onResume() {
        binding.bmapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        binding.bmapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        binding.bmapView.onDestroy();
        super.onDestroy();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || binding.bmapView == null){
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
        }
    }
}