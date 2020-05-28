package com.example.zzn_9_8.Fragment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    private String all = null;
    private String filepath = null;
    private String image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disasterdialog_layout);
        but_close = findViewById(R.id.but_exit);
        but_submit = findViewById(R.id.but_submit);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        get_infomation();
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
                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = new FormBody.Builder()
                                .add("all", all)
                                .build();
                        Request request = new Request.Builder()
                                .url("xxxxxxxxxxxxxxxxx/1")
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
                                .url("xxxxxxxxxxxxxxx/2")
                                .post(requestBody)
                                .build();

                        try {
                            client.newCall(request2).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
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
