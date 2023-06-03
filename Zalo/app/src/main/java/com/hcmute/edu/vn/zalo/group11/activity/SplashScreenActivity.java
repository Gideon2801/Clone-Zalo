package com.hcmute.edu.vn.zalo.group11.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.hcmute.edu.vn.zalo.group11.R;

public class SplashScreenActivity extends AppCompatActivity {
    //Kiểm tra xem người dùng đã đăng nhập vào hay chưa
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Đặt thời gian hiển thị Splash Screen là 3s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Lấy thông tin người dùng
                auth = FirebaseAuth.getInstance();
                //Nếu hiện tại có người dùng (auth.getCurrentUser != null)
                if (auth.getCurrentUser() != null){
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    //Chuyển đến trang Welcome và kết thúc trang Splash
                    startActivity(new Intent(SplashScreenActivity.this, WelcomeScreenActivity.class));
                    finish();
                }
            }
        }, 4000);
    }
}