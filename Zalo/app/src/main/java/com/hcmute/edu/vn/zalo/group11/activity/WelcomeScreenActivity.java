package com.hcmute.edu.vn.zalo.group11.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hcmute.edu.vn.zalo.group11.R;

public class WelcomeScreenActivity extends AppCompatActivity {

    Button btn_BatDau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        //Ánh xạ với button trên View
        btn_BatDau = findViewById(R.id.button_BatDau);

        //Xử lý sự kiện khi nhấn button Login
        btn_BatDau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Chuyển đến trang Login
                startActivity(new Intent(WelcomeScreenActivity.this, PhoneNumberActivity.class));
            }
        });
    }
}