package com.example.zzn_9_8.Login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zzn_9_8.Main.MainActivity;
import com.example.zzn_9_8.R;
import com.example.zzn_9_8.Register.register_Activity;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*********登录界面***********/
public class login_Activity extends AppCompatActivity {
    private Button but_register;
    private Button but_login;
    private EditText password;
    private EditText userid;
    private ImageView iv_eye;
    private boolean isOpenEye = true;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        but_register = (Button) findViewById(R.id.register_button);
        iv_eye = findViewById(R.id.eye);
        iv_eye.setSelected(true);
        but_login = findViewById(R.id.login_button);
        userid = findViewById(R.id.userid);
        password = findViewById(R.id.userpassword);

        /******隐藏上边框*****/
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        /*******密码是否可见******/
        iv_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpenEye == true) {
                    iv_eye.setSelected(false);
                    isOpenEye = false;
                    /*由可见转成不可见*/
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                } else {
                    iv_eye.setSelected(true);
                    isOpenEye = true;
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        /*******登录状态保存******/
        SharedPreferences p = getSharedPreferences("data", Context.MODE_PRIVATE);
        state = p.getInt("statement", 0);
        if (state == 1) {
            //自动跳转，无需再次登录
            Intent intent_but_register = new Intent(login_Activity.this, MainActivity.class);
            startActivity(intent_but_register);
            finish();
        }

        but_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url_login = "http://xxxxx/app/login";
                final String id = userid.getText().toString();
                final String userpassword = password.getText().toString();
                /*******自留的一个后门，默认账号*****/
                if (id.equals("180802") && userpassword.equals("123456")) {
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putInt("statement", 1);
                    editor.putString("ID", "180802");
                    editor.apply();
                    show_success();
                    show_success();
                    Intent intent_but_register = new Intent(login_Activity.this, MainActivity.class);
                    startActivity(intent_but_register);
                    finish();
                } else {
                    /*判断账号密码是否输入*/
                    boolean s1 = password.getText().toString().trim().isEmpty();
                    boolean s2 = password.getText().toString().trim().isEmpty();
                    if (s2 == true || s1 == s2) {
                        Toast.makeText(login_Activity.this, "账号或密码尚未输入", Toast.LENGTH_SHORT).show();
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                /*****将账号密码提交到指定API验证*****/
                                Response response = null;
                                String responseData = null;
                                String login_result = null;
                                OkHttpClient client = new OkHttpClient();
                                HashMap<String, String> map = new HashMap<>();
                                map.put("id", id);
                                map.put("password", userpassword);
                                Gson gson = new Gson();
                                String data = gson.toJson(map);
                                RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), data);
                                Request request = new Request.Builder()
                                        .url(url_login)
                                        .post(body)
                                        .build();
                                try {
                                    response = client.newCall(request).execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    /******获取API返回值******/
                                    responseData = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseData);
                                    login_result = jsonObject.getString("status");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if ("1".equals(login_result)) {
                                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                    /*****记录登录状态和用户ID****/
                                    editor.putInt("statement", 1);
                                    editor.putString("ID", id);
                                    editor.apply();
                                    show_success();
                                    Intent intent_but_register = new Intent(login_Activity.this, MainActivity.class);
                                    startActivity(intent_but_register);
                                    finish();
                                } else {
                                    show_error();
                                }
                            }
                        }).start();
                    }
                }
            }
        });


        but_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent_register = new Intent(login_Activity.this, register_Activity.class);
               startActivity(intent_register);
            }
        });

    }

    public void show_error() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(login_Activity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void show_success() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(login_Activity.this, "登入成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

