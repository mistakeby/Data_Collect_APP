package com.example.zzn_9_8.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzn_9_8.Fragment.Fragment_GPS;
import com.example.zzn_9_8.Fragment.Fragment_Homepage;
import com.example.zzn_9_8.Fragment.Fragment_Weather;
import com.example.zzn_9_8.Fragment.Fragment_me;
import com.example.zzn_9_8.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Fragment_me.MyListener {

    /*********************底部导航栏变量载入**************************/
    private TextView text_homepage;
    private TextView text_weather;
    private TextView text_me;
    private TextView text_gps;
    private Fragment_Weather frg_weather;
    private Fragment_Homepage frg_homepage;
    private Fragment_GPS frg_gps;
    private Fragment_me frg_me;
    private FragmentManager frg_Manager;
    /*******************权限数组***********************/
//    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,
//            Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,Manifest.permission.RECORD_AUDIO};
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    private int requestCodePre = 321;
    private int requestCodeSer = 123;

    /**********************************测试************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*****************************底部导航栏载入*******************/
        setContentView(R.layout.main_layout);//activity_main对应导航栏的设计页面
        /**********************申请动态权限**********************/
        checkPermissons();
        frg_Manager = getSupportFragmentManager();
        bindViews();
        //默认点击Fragment_Homepage
        text_homepage.performClick();
    }

    /*******************************关于底部导航栏功能实现*****************************/
    private void bindViews() {
        text_homepage = (TextView) findViewById(R.id.homepage);
        text_gps = (TextView) findViewById(R.id.gps);
        text_weather = (TextView) findViewById(R.id.weather);
        text_me= (TextView) findViewById(R.id.me);
        text_homepage.setOnClickListener(this);
        text_me.setOnClickListener(this);
        text_gps.setOnClickListener(this);
        text_weather.setOnClickListener(this);
    }
    private void set_Selected() {
        text_homepage.setSelected(false);
        text_weather.setSelected(false);
        text_gps.setSelected(false);
        text_me.setSelected(false);
    }
    private void hide_AllFragment(FragmentTransaction fragmentTransaction) {
        if (frg_me != null) fragmentTransaction.hide(frg_me);
        if (frg_gps != null) fragmentTransaction.hide(frg_gps);
        if (frg_homepage != null) fragmentTransaction.hide(frg_homepage);
        if (frg_weather!= null) fragmentTransaction.hide(frg_weather);
    }
    public void onClick(View v) {
        FragmentTransaction f_Transaction = frg_Manager.beginTransaction();
        hide_AllFragment(f_Transaction);
        fragment_switch(f_Transaction, v);
    }
    public void fragment_switch(FragmentTransaction f_Transaction, View v) {
        switch (v.getId()) {
            case R.id.homepage:
                set_Selected();
                text_homepage.setSelected(true);
                if (frg_homepage == null) {
                    frg_homepage= new Fragment_Homepage("");
                    f_Transaction.add(R.id.ly_content, frg_homepage);
                } else {
                    f_Transaction.show(frg_homepage);
                }
                break;
            case R.id.gps://定位
                set_Selected();//讲四个fragment都设置为不可用
                text_gps.setSelected(true);//把选择到的那个fragment设置为可用
                if (frg_gps == null) {//刚开始加载时，若frg不存在，就创建一个
                    frg_gps = new Fragment_GPS("");
                    f_Transaction.add(R.id.ly_content, frg_gps);
                } else {//加载之后。存在就显示。
                    // 刚打开APP时，就会先创建，之后再点击导航栏的定位之后就不会重新创建一个frg了，而是显示原来创建好的
                    f_Transaction.show(frg_gps);
                }
                break;
            case R.id.weather:
                set_Selected();
                text_weather.setSelected(true);
                if (frg_weather == null) {
                    frg_weather = new Fragment_Weather("");
                    f_Transaction.add(R.id.ly_content, frg_weather);
                } else {
                    f_Transaction.show(frg_weather);
                }
                break;
            case R.id.me:
                set_Selected();
                text_me.setSelected(true);
                if (frg_me == null) {
                    frg_me = new Fragment_me("");
                    f_Transaction.add(R.id.ly_content, frg_me);
                } else {
                    f_Transaction.show(frg_me);
                }
                break;
        }
        f_Transaction.commit();
    }

    /*******************************关于权限*****************************/


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestCodePre) {//requestCodePre找到permissions[]
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//M表示版本号23
                boolean grantFlas = false;// 判断该权限是否已经授权
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        //-----------存在未授权-----------
                        grantFlas = true;//grantFlags=true表示有权限没有被授权
                      //  System.out.println("表示存在未授权的权限");
                    }
                }

                if (grantFlas) {
                   // System.out.println("有未授权的权限执行这一步");
                    boolean shouldShowRequestFlas = false;
                    for (String per : permissions) {//for循环依次判断permissions[]中权限是否被用户授权
                        if (shouldShowRequestPermissionRationale(per)) {
                            shouldShowRequestFlas = true;//用户如果之前拒绝过权限，设置 shouldShowRequestFlas = true
                        }
                    }
                    if (shouldShowRequestFlas) {
                        // shouldShowRequestFlas = true表示未授权
                        // 提示用户去应用设置界面手动开启权限
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                        builder.setTitle("提示");
                        builder.setMessage("当前还有必要权限没有授权，是否前往授权？");
                        builder.setCancelable(false);
                        builder.setPositiveButton("前往", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                goToAppSetting();
                            }
                        });
                        builder.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                } else {
                    //-----------授权成功-----------
                   // System.out.println("全部被授权的权限执行这一步");
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /*********************************权限*******************/
    private void checkPermissons() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
            boolean perimissionFlas = false;
            for (String permissionStr : permissions) {//依次检查权限列表
                int per = ContextCompat.checkSelfPermission(this, permissionStr);
             //per=1表示之前没有给过授权，per=0表示之前给过授权
                if (per != PackageManager.PERMISSION_GRANTED) {
                    perimissionFlas = true;
                    System.out.println("permissionFlags" + perimissionFlas);
                }
            }
            if (perimissionFlas) {// 如果有权限没有授予允许，就去提示用户请求授权,执行这一步
                ActivityCompat.requestPermissions(this, permissions, requestCodePre);
                //无论 permissions[]是否得到用户授权，都会返回requestCodePre
            }
            System.out.println("requestCodePre" + requestCodePre);
        }
    }

    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, requestCodeSer);
    }


    @Override
    public void sendContent2() {
        finish();
    }
}

