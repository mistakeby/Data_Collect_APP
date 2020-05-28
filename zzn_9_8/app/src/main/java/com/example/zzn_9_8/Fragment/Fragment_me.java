package com.example.zzn_9_8.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.example.zzn_9_8.Main.MainActivity;
import com.example.zzn_9_8.R;

/**************登入成功的fragment*******************/
public class Fragment_me extends Fragment {
    private String content;
    public TextView test1;
    public Button exit;
    public Button about1;
    public Fragment_me(String content) {
        this.content = content;
    }
    private Fragment_me.MyListener myListener;
    public interface MyListener{
      void sendContent2();
    }
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myListener = ( Fragment_me.MyListener ) getActivity();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.me_layout, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();
        test1=view.findViewById(R.id.hum_1);
        exit=view.findViewById(R.id.but_exit);
        about1=view.findViewById(R.id.about);
        String test2;
        SharedPreferences p=mainActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
        test2=p.getString("ID","");
        test1.setText("ID:"+test2);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mainActivity.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                editor.putInt("statement", 0);
                editor.apply();
                myListener.sendContent2( );
            }
        });
        about1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainActivity,"该APP用于自然灾害或者自然资源审计用途",Toast.LENGTH_LONG).show();
            }
        });
       return view;
    }
}
