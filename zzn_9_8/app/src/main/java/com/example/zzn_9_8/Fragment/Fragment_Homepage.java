package com.example.zzn_9_8.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.zzn_9_8.Login.login_Activity;
import com.example.zzn_9_8.Main.MainActivity;
import com.example.zzn_9_8.R;
import com.example.zzn_9_8.Register.register_Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/****************************首页页面*************************/
public class Fragment_Homepage extends Fragment implements SensorEventListener {
    private String content;

    public Fragment_Homepage(String content) {
        this.content = content;
    }

    /****************************关于显示经纬度街道地理数据*************************/
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private TextView text_city;
    private TextView text_street;
    private TextView latitude;
    private TextView longitude;
    /****************************关于拍照*************************/
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    private Button but_camera;
    private String output_image = null;
    public String output_image_2 = null;
    public Date date = null;
    private File outputImage = null;
    private File outputVideo=null;
    private boolean if_takephoto = false;
    private boolean if_takevideo = false;
    private  String userid = null;
    /************************关于EditText**********************/
  /*  public EditText edit_text;
    public Button button_commit;
    String edit_text_message = null;*/

    /*************************传感器****************/
    public SensorManager mSensorManager;
    public boolean sensor_start = false;
    public boolean sensor_times = false;
    public TextView tmp_textview;
    public TextView weather_textview;
    public String o_1 = null;
    public String o_2 = null;
    public String o_3 = null;
    public String light = null;
    public String o_1_2 = null;
    public String o_2_2 = null;
    public String o_3_2 = null;
    public String light_2 = null;
    public int type_sensor;
    /****************************关于录像*************************/
    public Button but_video;
    public Date date2 = null;
    public String output_video = null;
    private Uri imageUri2;
    public static final int TAKE_VIDEO = 3;
    /***********************文件存储缓存路径*********************/
    private String image_photo_path = null;
    private String image_video_path = null;
    /****************************关于获取天气温度信息*************************/
    private String la_titude = null;//纬度
    private String long_titude = null;//经度
    private String la_titude_2 = null;//纬度
    private String long_titude_2 = null;//经度
    private boolean weather_start = false;
    private String temperature = null;
    private String weather_condition = null;
    public  String city_homepage;
    public String District;
    public String street;
    /*****************************复选框****************************/
    RadioGroup.OnCheckedChangeListener radioButtonListener;
    private String radiobutton = "自然灾害";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage_layout, container, false);
        /************************各种控件初始化**********************/
        text_city = (TextView) view.findViewById(R.id.text_city);
        text_street = (TextView) view.findViewById(R.id.District_street);
        but_camera = view.findViewById(R.id.camera);
        latitude = (TextView) view.findViewById(R.id.Latitude);
        longitude = (TextView) view.findViewById(R.id.Longitude);
        but_video = (Button) view.findViewById(R.id.video);
        /************************各种控件初始化**********************/
        final MainActivity mainActivity = (MainActivity) getActivity();
        /*************************获取用户id*************************/
        SharedPreferences p = mainActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
        userid = p.getString("ID", "");
        /**************************获取用户当前的地理位置***************************/
        mSensorManager = (SensorManager) mainActivity.getSystemService(Context.SENSOR_SERVICE);//获取传感器管理服务
        mLocationClient = new LocationClient(mainActivity);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setEnableSimulateGps(false);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();

        /************************关于EditText**********************/
        tmp_textview=view.findViewById(R.id.tmp);
        weather_textview=view.findViewById(R.id.cond_txt);
        weather_start = true;

        /*****************************复选框*************************/
        RadioGroup group = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioButtonListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.disasters) {
                    radiobutton = "自然灾害";
                }
                if (checkedId == R.id.audit) {
                    radiobutton = "资源审计";
                }
            }
        };
        group.setOnCheckedChangeListener(radioButtonListener);

        /**************************注册方向传感器监听***************************/
        Sensor mSensorOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//注册方向传感器
        Sensor light_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener((SensorEventListener) this, mSensorOrientation, mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener((SensorEventListener) this, light_sensor, mSensorManager.SENSOR_DELAY_NORMAL);
        /**********************拍照******************/
        but_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sensor_start = true;
                sensor_times = true;
                /************************照片的名字定义成时间格式：年月日时分秒***************************/
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                date = new Date();
                output_image = format.format(date);//获取手机当前时间信息
                output_image_2 = output_image;
                output_image = output_image + ".jpg";
                //将拍到的照片存储到目录中
                outputImage = new File(mainActivity.getExternalCacheDir(), output_image);
                image_photo_path = mainActivity.getExternalCacheDir().getPath();
                try {
                    if (outputImage.exists()) {

                    } else
                        outputImage.createNewFile();//创建目录
                } catch (IOException e) {
                    e.printStackTrace();//异常处理
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(mainActivity, "com.example.zzn_9_8.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");//启动照相功能
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        /**********************录像******************/
        but_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensor_start = true;
                sensor_times = true;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                date2 = new Date();
                output_video = format.format(date2);//获取手机当前时间信息
                output_video = output_video + ".mp4";
                outputVideo= new File(mainActivity.getExternalCacheDir(), output_video);
                image_video_path = mainActivity.getExternalCacheDir().getPath();
                try {
                    if (outputVideo.exists()) {
                        //如果文件夹已经存在
                    } else
                       outputVideo.createNewFile();//创建目录
                } catch (IOException e) {
                    e.printStackTrace();//异常处理
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri2 = FileProvider.getUriForFile(mainActivity, "com.example.zzn_9_8.fileprovider", outputVideo);
                } else {
                    imageUri2 = Uri.fromFile(outputVideo);
                }
                Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");//启动照相功能
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri2);
                startActivityForResult(intent, TAKE_VIDEO);
            }
        });
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //为什么要放在onSensorChanged下
        //因为首先指向执行一次查询当前城市天气，所以用weather_start
        //由于网络可能信号不好，如果把下面的函数放在on_create中,有可能执行到该函数的时候，city_homepage还是null
        //是onSensorChanged一直在执行的
        if (city_homepage != null && weather_start == true) {
            get_weather_okhttp(city_homepage);
            weather_start = false;
        }
        if (sensor_start == true) {
            type_sensor = sensorEvent.sensor.getType();//判断传感器类型
            switch (type_sensor) {
                case Sensor.TYPE_ORIENTATION:
                    o_1 = Float.toString(sensorEvent.values[0]);
                    o_2 = Float.toString(sensorEvent.values[1]);
                    o_3 = Float.toString(sensorEvent.values[2]);
                    break;
                case Sensor.TYPE_LIGHT:
                    light = Float.toString(sensorEvent.values[0]);
                    break;
            }
            if (sensor_times == true)//on_sensor_changed()函数其实是开启fag_page这个fragment页面之后就一直运行
            //所以实际上手机是可以一直获取到传感器信息的，本部分是实现获取拍照那一瞬间的传感器信息并发送
            {
                //这边说一下，传感器数据不是同时获得的，例如拍照的时候，手机可能先获得方向传感器的数据，再获得光传感器的数据
                //那么就存在一个问题，有可能方向传感器获取到的时候，光的传感器数据为null
                o_1_2 = o_1;
                o_2_2 = o_2;
                o_3_2 = o_3;
                light_2 = light;
            } else
            {
            }
            //所以要求方向传感器数据和光数据都有的时候，才停止接收传感器数据
            if (o_1 != null && light != null) {
                sensor_times = false;
            }
        } else {
            //啥也不干
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    //没重写,因为本次功能不需要重写这个接口函数
    }


    /******************************在首页显示经纬度模块**************************************/
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            /************************text控件显示街道和经纬度*************/
            double l1 = location.getLatitude();//纬度
            la_titude = String.valueOf(l1);
            la_titude_2=la_titude.substring(0,4);
            la_titude="纬度："+la_titude.substring(0,6);
            double l2 = location.getLongitude();
            long_titude = String.valueOf(l2);
            long_titude_2=long_titude;
            long_titude="经度："+long_titude.substring(0,6);
            city_homepage = location.getCity();
            District = location.getDistrict();
            street = location.getStreet();
            String DisandStreet=District+street;
            text_city.setText(city_homepage);
            text_street.setText(DisandStreet);
            latitude.setText(la_titude);
            longitude.setText(long_titude);
        }
    }

    /************************拍照结果查询模块***********************/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity mainActivity = (MainActivity) getActivity();
        switch (requestCode) {
            case TAKE_PHOTO://拍照成功
                if (resultCode == mainActivity.RESULT_OK) {
                    try {
                        /********************拍出的图片压缩**************************/
                        //以下代码可以使用，但是我没有使用，主要是因为压缩会降低图片清晰度
                     /*   BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap bitmap = BitmapFactory.decodeFile(outputImage.getPath(),
                                options);
                        bitmap = compressImage(bitmap, 100);

                        if (bitmap != null) {
                            FileOutputStream fos = null;
                            fos = new FileOutputStream(outputImage);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);//将bitmap转化为 FileOutputStream格式
                            fos.flush();
                            fos.close();

                        }*/
                    } catch (Exception e)
                    {
                    }
                    sensor_start = false;//结束接收传感器数据
                    o_1 = null;//将传感器数据清零，不然的话，第二次的数据可能是第一次得到的。举个例子，第一次拍照结束之前得到了光强
                    //为56，第二次拍照之后，获得到方向传感器数据，但是还没有获取到光强数据，但是由于第一次光强没有清零，为56，就会使得
                    //if (o_1!=null&&light!=null){sensor_times=false;}结束获取数据，并直接使用56的光强数据
                    light = null;
                    if_takephoto = true;
                    Toast.makeText(mainActivity, "拍照成功", Toast.LENGTH_SHORT).show();
                    if(radiobutton.equals("自然灾害"))
                    {
                        Intent intent = new Intent(mainActivity, DisasterDialog.class);
                        pass_information_photo(intent);

                    }
                    else
                    {
                        Intent intent = new Intent(mainActivity, ResourceDialog.class);
                        pass_information_photo(intent);
                    }
                }
                break;
            case TAKE_VIDEO:
                if (resultCode == mainActivity.RESULT_OK) {
                    sensor_start = false;
                    o_1 = null;
                    light = null;
                    if_takevideo = true;
                    Toast.makeText(mainActivity, "录像成功", Toast.LENGTH_SHORT).show();
                    if(radiobutton.equals("自然灾害"))
                    {
                        Intent intent = new Intent(mainActivity, DisasterDialog.class);
                        pass_information_video(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(mainActivity, ResourceDialog.class);
                        pass_information_video(intent);
                    }
                }
            default:
                break;
        }
    }
    private void pass_information_photo(Intent intent)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date current=new Date();
        String current_time;
        current_time=format.format(current);
        String filepath=image_photo_path+"/"+output_image;
        String savepath="/"+userid+"/photo/"+output_image;
        String filetype="pic";
       /* String all=userid+","+filetype+","+o_1_2+","+o_2_2+","+o_3_2+","+light_2+","+radiobutton+","+la_titude_2+","+long_titude_2+","+city_homepage
                +","+District+","+street +","+savepath+","+current_time;*/
        String all=userid+","+filetype+","+light_2+","+radiobutton+","+la_titude_2+","+city_homepage
                +","+District +","+savepath+","+current_time;
        //MainActivity mainActivity = (MainActivity) getActivity();
      //  Intent intent = new Intent(mainActivity, DisasterDialog.class);
        intent.putExtra("all",all).putExtra("filepath",filepath).putExtra("image",output_image);
        startActivity(intent);
    }
    private void pass_information_video(Intent intent)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date current=new Date();
        String current_time;
        current_time=format.format(current);
        String filepath=image_video_path+"/"+output_video;
        String savepath="/"+userid+"/videp/"+output_video;
        String filetype="vid";
       /* String all=userid+","+filetype+","+o_1_2+","+o_2_2+","+o_3_2+","+light_2+","+radiobutton+","+la_titude_2+","+long_titude_2+","+city_homepage
                +","+District+","+street +","+savepath+","+current_time;*/
        String all=userid+","+filetype+","+light_2+","+radiobutton+","+la_titude_2+","+city_homepage
                +","+District +","+savepath+","+current_time;
        intent.putExtra("all",all).putExtra("filepath",filepath).putExtra("image",output_video);
        startActivity(intent);
    }

    public void get_weather_okhttp(String str_url) {
        final String URL_1 = "https://free-api.heweather.net/s6/weather/" + "now?location=" + str_url + "&key=1fe21d8406904fd5875894469cae3007";
        new Thread(new Runnable() {
            Response response1 = null;
            String responseData1 = null;
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request1 = new Request.Builder().url(URL_1).build();
                try {
                    response1 = client.newCall(request1).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    responseData1 = response1.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData1);
                        JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                        temperature = jsonArray.getJSONObject(0).getJSONObject("now").getString("tmp");
                        weather_condition = jsonArray.getJSONObject(0).getJSONObject("now").getString("cond_txt");
                        show_message(temperature, weather_condition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void show_message(final String tmp, final String weather_condition) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 tmp_textview.setText(tmp+"℃");
                 weather_textview.setText(weather_condition);
            }
        });
    }

    private Bitmap compressImage(Bitmap image, int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//100表示不进行压缩，70表示压缩70%,
        // 这句话是将bitmap格式的image转化为ByteArrayOutputStream baos
        int options = 100;
        // 循环判断如果压缩后图片是否大于size,大于继续压缩
        while ((baos.toByteArray().length / 1024) > size) {//一般得到的是300kb左右的大小
            baos.reset();
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }
}
