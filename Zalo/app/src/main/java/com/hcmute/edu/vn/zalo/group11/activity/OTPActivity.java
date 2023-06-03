package com.hcmute.edu.vn.zalo.group11.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmute.edu.vn.zalo.group11.databinding.ActivityOtpactivityBinding;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;

    FirebaseAuth auth;

    String verificationId;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Khởi tạo dialog
        dialog = new ProgressDialog(this);
        //Đặt tiêu đề Dialog
        dialog.setMessage("Đang gửi mã xác nhận...");
        //Thiết lập dialog không thể tắt
        dialog.setCancelable(false);
        //Hiển thị dialog
        dialog.show();

        //Thiết lập đường dẫn đến firebase
        auth = FirebaseAuth.getInstance();
        //Lấy số điện thoại được gửi qua Intent
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        //Hiển thị số ddienj thoại lên TextView
        binding.phoneLbl.setText("Xác Minh " + phoneNumber);

        //Thiết lập thời gian để hệ thống gửi tin nhắn
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder()
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        //tắt dialog
                        dialog.dismiss();

                        verificationId = s;
                    }
                }).build();

        //Lấy mã OTP
        PhoneAuthProvider.verifyPhoneNumber(options);

        //Bắt sự kiện khi View OTP được điền đẩy đủ
        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                //Kiểm tra xem mã OTP có đúng hay không
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                //Đăng nhập hệ thống
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Thiết lập đường dẫn tới vị trí lưu trữ
                            FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference().child("Activity").child(phoneNumber).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                            //Chuyển đến trang SetupProfile thông qua intent
                            Intent intent = new Intent(OTPActivity.this, SetupProfileActivity.class);
                            startActivity(intent);
                            //Kết thúc các hành động trước đó;
                            finishAffinity();
                        }else {
                            //Nếu mã OTP không đúng thì thông baosc ho người dùng
                            Toast.makeText(OTPActivity.this,"Failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }
}