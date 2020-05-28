package com.example.zzn_9_8.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.zzn_9_8.Main.MainActivity;
import com.example.zzn_9_8.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Fragment_Weather extends Fragment implements SensorEventListener {

    /*************************传感器****************/
    public SensorManager mSensorManager;
    private String content;
    public Fragment_Weather(String content) {
        this.content = content;
    }

    /*************************气象****************/
    public TextView wind_dir;
    public TextView wind_sc;
    public TextView hum;
    public TextView vis;
    public TextView qlty;
    public TextView main;
    public TextView pm25;
    public TextView pm10;
    public TextView no2;
    public TextView so2;
    public TextView co;
    public TextView current_city;
    /***********************获取气象信息****************/
    public String city;
    public String latitude_1;
    public String longitude_1;
    boolean weather_start_1 = false;//让手机APP第一次启动之后（以当前定位的城市）请求和风API之后，不会再次请求，因为
    //onchanged()会一直执行，为了让okhttp只请求一次，所以设定这个值
    //boolean weather_start_2=false;
    //boolean weather_start_3=false;
    /***************str_url_1是定位城市或者用户输入的切换城市*****************/
    public String str_url_1 = null;
    public String wind_dir_txt = null;
    public String wind_sc_txt = null;
    public String hum_txt = null;
    public String vis_txt = null;
    public String qlty_txt = null;
    public String main_txt = null;
    public String pm25_txt = null;
    public String pm10_txt = null;
    public String no2_txt = null;
    public String so2_txt = null;
    public String co_txt = null;
    public String state_air;//判断ok_http请求和风API之后的接口返回状态
    private Button but;
    private Button but_default;
    private String edit_text_message;
    private EditText edit_text;
    private boolean change_city = false;
    private boolean change_city_1 = false;

    /*************************百度SDK****************/
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    /*************************百度SDK****************/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_layout, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();
        /**************************获取传感器管理服务***************************/
        mSensorManager = (SensorManager) mainActivity.getSystemService(Context.SENSOR_SERVICE);//获取传感器管理服务
        /**************************注册方向传感器监听***************************/
        Sensor mSensorOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//注册方向传感器
        mSensorManager.registerListener((SensorEventListener) this, mSensorOrientation, mSensorManager.SENSOR_DELAY_NORMAL);
        /*************************百度SDK****************/
        mLocationClient = new LocationClient(mainActivity);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        /*************************百度SDK****************/


        /*************************气象****************/
        wind_dir = (TextView) view.findViewById(R.id.wind_dir_1);
        wind_sc = (TextView) view.findViewById(R.id.wind_sc_1);
        hum = view.findViewById(R.id.hum_1);
        vis = view.findViewById(R.id.vis_1);
        qlty = view.findViewById(R.id.qlty_1);
        main = (TextView) view.findViewById(R.id.main_1);
        pm25 = (TextView) view.findViewById(R.id.pm25_1);
        pm10 = view.findViewById(R.id.pm10_1);
        no2 = view.findViewById(R.id.no2_1);
        so2 = view.findViewById(R.id.so2_1);
        co = view.findViewById(R.id.co_1);
        but = view.findViewById(R.id.commit);
        edit_text = view.findViewById(R.id.message_commit);
        but_default = view.findViewById(R.id.default_city);
        current_city = view.findViewById(R.id.current_city);
        /*************************百度SDK****************/
        mLocationClient.start();
        weather_start_1 = true;
               /***************切换查询城市按钮****************/
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_text_message = edit_text.getText().toString();
                change_city = true;//点击提交之后，change_city=true;
                /**********************判断edit_text是否输入为空****************/
                String s1 = edit_text.getText().toString().trim();
                if (s1.isEmpty()) {
                    Toast.makeText(mainActivity, "输入为空，请重新输入", Toast.LENGTH_SHORT).show();
                    change_city = false;
                } else {
                    Toast.makeText(mainActivity, "切换成功", Toast.LENGTH_SHORT).show();
                    change_message();
                    //默认是北京市
                    current_city.setText("当前城市：" + edit_text_message);
                }
                //清空edit_text
                edit_text.setText("");
            }
        });
        /***************默认按钮，切换到用户当前所在城市****************/
        but_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_city_1 = true;
                change_message();
                Toast.makeText(mainActivity, "已切换回当前所在城市", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    // @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (city != null && weather_start_1 == true)//在APP启动之后并且获取到当前定位之后执行，只执行一遍
        {
            get_weather_okhttp(city);//访问接口，特别强调一次，本程序采用百度地图SDK来获取定位信息，默认优先使用GPS定位，其次使用网络定位
            weather_start_1 = false;// weather_start_1=false;使得程序只执行一次（在用户没有点击默认按钮的情况下，如果点击了默认按钮，就好再次执行这个函数！）
            current_city.setText("当前城市：" + city);
        }
        if (change_city == true)//如果用户点击切换城市
        {
            get_weather_okhttp(edit_text_message);
            change_city = false;//change_city=false;让用户点击切换之后，
            // 执行此函数，请求一次之后，change_city=false，这样就不会再次调用此函数
            //关键：public void onSensorChanged(SensorEvent sensorEvent) 是默认一直执行的
        }
        if (change_city_1 == true)//用户点击默认
        {

            weather_start_1 = true;
            //执行这个：if(city!=null&&weather_start_1==true)//在APP启动之后并且获取到当前定位之后执行，只执行一遍
            //        {
            //            get_weather_okhttp(city);//访问接口，特别强调一次，本程序采用百度地图SDK来获取定位信息，默认优先使用
            //            //GPS定位，其次使用网络定位
            //            weather_start_1=false;// weather_start_1=false;使得程序只执行一次（在用户没有点击默认按钮的情况下，如果点击了默认按钮，就好再次执行这个函数！）
            //        }
            change_city_1 = false;//目的：只执行一次
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void get_weather_okhttp(String str) {
        final String URL_1 = "https://free-api.heweather.net/s6/weather/" + "now?location=" + str + "&key=1fe21d8406904fd5875894469cae3007";//密钥key
        final String URL_2 = "https://free-api.heweather.net/s6/air/" + "now?location=" + str + "&key=1fe21d8406904fd5875894469cae3007";
        new Thread(new Runnable() {
            Response response1 = null;
            String responseData1 = null;
            Response response2 = null;
            String responseData2 = null;
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request1 = new Request.Builder().url(URL_1).build();
                Request request2 = new Request.Builder().url(URL_2).build();
                try
                {
                    response1 = client.newCall(request1).execute();
                    response2 = client.newCall(request2).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    responseData1 = response1.body().string();
                    responseData2 = response2.body().string();
                    System.out.println(responseData1);
                    System.out.println(responseData2);
                    try {
                        //查询天气
                        JSONObject jsonObject = new JSONObject(responseData1);
                        JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                        //查询空气质量
                        JSONObject jsonObject1 = new JSONObject(responseData2);
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("HeWeather6");
                        //ok_http在接入天气接口时，如果用户输入的城市县城一级的，可能由于没有空气质量测量站
                        //所以返回拒绝，各项指标都没有
                        state_air = jsonArray1.getJSONObject(0).get("status").toString();
                        if (state_air.equals("ok"))
                        {
                            qlty_txt = jsonArray1.getJSONObject(0).getJSONObject("air_now_city").getString("qlty");
                            main_txt = jsonArray1.getJSONObject(0).getJSONObject("air_now_city").getString("main");
                            pm25_txt = jsonArray1.getJSONObject(0).getJSONObject("air_now_city").getString("pm25");
                            pm10_txt = jsonArray1.getJSONObject(0).getJSONObject("air_now_city").getString("pm10");
                            no2_txt = jsonArray1.getJSONObject(0).getJSONObject("air_now_city").getString("no2");
                            so2_txt = jsonArray1.getJSONObject(0).getJSONObject("air_now_city").getString("so2");
                            co_txt = jsonArray1.getJSONObject(0).getJSONObject("air_now_city").getString("co");
                            show_message_air(qlty_txt, main_txt, pm25_txt, pm10_txt, no2_txt, so2_txt, co_txt);
                        } else {
                            show_message_null();
                        }
                        //weather
                        wind_dir_txt = jsonArray.getJSONObject(0).getJSONObject("now").getString("wind_dir");
                        wind_sc_txt = jsonArray.getJSONObject(0).getJSONObject("now").getString("wind_sc");
                        hum_txt = jsonArray.getJSONObject(0).getJSONObject("now").getString("hum");
                        vis_txt = jsonArray.getJSONObject(0).getJSONObject("now").getString("vis");
                        show_message_weather(wind_dir_txt, wind_sc_txt, hum_txt, vis_txt);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void show_message_weather(final String s1, final String s2, final String s3, final String s4) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wind_dir.setText(s1);
                wind_sc.setText(s2);
                hum.setText(s3);
                vis.setText(s4);
            }
        });
    }

    public void show_message_air(final String s5, final String s6,
                                 final String s7, final String s8,
                                 final String s9, final String s10, final String s11) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qlty.setText(s5);
                main.setText(s6);
                pm25.setText(s7);
                pm10.setText(s8);
                no2.setText(s9);
                so2.setText(s10);
                co.setText(s11);
            }
        });
    }

    public void show_message_null() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qlty.setText("无该城市天气质量信息");
                main.setText("无该城市天气质量信息");
                pm25.setText("无该城市天气质量信息");
                pm10.setText("无该城市天气质量信息");
                no2.setText("无该城市天气质量信息");
                so2.setText("无该城市天气质量信息");
                co.setText("无该城市天气质量信息");
            }
        });
    }

    public void change_message() {
        wind_dir.setText("");
        wind_sc.setText("");
        hum.setText("");
        vis.setText("");
        qlty.setText("");
        main.setText("");
        pm25.setText("");
        pm10.setText("");
        no2.setText("");
        so2.setText("");
        co.setText("");

    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            city = location.getCity();
            double latitude = location.getLatitude();    //获取纬度信息
            latitude_1 = String.valueOf(latitude);
            double longitude = location.getLongitude();    //获取经度信息
            longitude_1 = String.valueOf(longitude);
            str_url_1 = longitude_1 + "," + latitude_1;

        }
    }
}
