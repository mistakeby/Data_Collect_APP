package com.example.zzn_9_8.Fragment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zzn_9_8.Login.login_Activity;
import com.example.zzn_9_8.R;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DisasterDialog extends AppCompatActivity {
    private Button but_close;
    private Button but_submit;
    private EditText edit_1;
    private EditText edit_2;
    private EditText edit_3;
    private EditText edit_4;
    private String all = null;
    private String filepath = null;
    private String image = null;
  /*  private String level=null;
    private String type=null;
    private String situation=null;
    private String description=null;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disasterdialog_layout);
        but_close = findViewById(R.id.but_exit);
        but_submit = findViewById(R.id.but_submit);
        edit_1=findViewById(R.id.edit_1);
        edit_2=findViewById(R.id.edit_2);
        edit_3=findViewById(R.id.edit_3);
        edit_4=findViewById(R.id.edit_4);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        get_infomation();
        final String []array_str=all.split(",");
        but_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        but_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        if (array_str[1].equals("pic")) {
                         /*   String level = edit_1.getText().toString();
                            String type = edit_2.getText().toString();
                            String situation = edit_3.getText().toString();*/
                            String description = edit_4.getText().toString();
                            OkHttpClient client = new OkHttpClient();
                            RequestBody body = new FormBody.Builder()
                                    .add("nodeID", array_str[0])
                                    .add("filetype", array_str[1])
                                  /*  .add("z_degree", array_str[2])
                                    .add("x_degree", array_str[3])
                                    .add("y_degree", array_str[4])*/
                                    .add("light", array_str[2])
                                    .add("topic", array_str[3])
                                    .add("latitude", array_str[4])
                                 /*   .add("longitude", array_str[8])*/
                                    .add("city", array_str[5])
                                    .add("district", array_str[6])
                                 /*   .add("street", array_str[11])*/
                                    .add("savePath", array_str[7])
                                    .add("originalTime", array_str[8])
                                  /*  .add("level", level)
                                    .add("type", type)
                                    .add("situation", situation)*/
                                    .add("descprition", description)
                                    .build();
                            Request request = new Request.Builder()
                                    .url("http://192.168.1.103:8011/photo")
                                    .post(body)
                                    .build();
                            try {
                                client.newCall(request).execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            File file = new File(filepath);
                            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("file", image, fileBody)
                                    .build();
                            Request request2 = new Request.Builder()
                                    .url("http://192.168.1.103:8011/photo")
                                    .post(requestBody)
                                    .build();

                            try {
                                client.newCall(request2).execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                          /*  String level = edit_1.getText().toString();
                            String type = edit_2.getText().toString();
                            String situation = edit_3.getText().toString();*/
                            String description = edit_4.getText().toString();
                            OkHttpClient client = new OkHttpClient();
                            RequestBody body = new FormBody.Builder()
                                    .add("nodeID", array_str[0])
                                    .add("filetype", array_str[1])
                              /*      .add("z_degree", array_str[2])
                                    .add("x_degree", array_str[3])
                                    .add("y_degree", array_str[4])*/
                                    .add("light", array_str[2])
                                    .add("topic", array_str[3])
                                    .add("latitude", array_str[4])
                                  /*  .add("longitude", array_str[8])*/
                                    .add("city", array_str[5])
                                    .add("district", array_str[6])
                                  /*  .add("street", array_str[7])*/
                                    .add("savePath", array_str[7])
                                    .add("originalTime", array_str[8])
                                  /*  .add("level", level)
                                    .add("type", type)
                                    .add("situation", situation)*/
                                    .add("descprition", description)
                                    .build();
                            Request request = new Request.Builder()
                                    .url("http://192.168.1.103:8011/video")
                                    .post(body)
                                    .build();
                            try {
                                client.newCall(request).execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            File file = new File(filepath);
                            RequestBody fileBody = RequestBody.create(MediaType.parse("video/mp4"), file);
                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("file", image, fileBody)
                                    .build();
                            Request request2 = new Request.Builder()
                                    .url("http://192.168.1.103:8011/video")
                                    .post(requestBody)
                                    .build();

                            try {
                                client.newCall(request2).execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                Toast.makeText(DisasterDialog.this, "信息发送成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void get_infomation() {
        Intent getintent = getIntent();
        all = getintent.getStringExtra("all");
        filepath = getintent.getStringExtra("filepath");
        image = getintent.getStringExtra("image");
        System.out.println(all);
    }
}
