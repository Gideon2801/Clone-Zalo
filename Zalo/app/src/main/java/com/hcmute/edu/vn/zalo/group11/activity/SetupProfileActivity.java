package com.hcmute.edu.vn.zalo.group11.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hcmute.edu.vn.zalo.group11.databinding.ActivitySetupProfileBinding;
import com.hcmute.edu.vn.zalo.group11.model.User;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    ProgressDialog dialog;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Thiết lập đường dẫn đến Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        storage = FirebaseStorage.getInstance();

        binding.imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khởi tạo Itent cho phép chọn hình ảnh trong máy
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            }
        });

        binding.btnSetProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lấy tên trong TextView
                String name = binding.edtName.getText().toString().trim();
                //Nếu người dùng không nhập tên
                if (name.isEmpty()){
                    //Hiện ra thông báo yêu cầu người dùng nhập tên
                    binding.edtName.setError("Vui lòng nhập tên");
                    return;
                }

                //Khởi tạo dialog chờ hệ thống xử lý
                dialog = new ProgressDialog(SetupProfileActivity.this);
                //Đặt tiêu đề dialog
                dialog.setMessage("Đang cập nhật thông tin...");
                //Dialog không thể tắt
                dialog.setCancelable(false);
                //Hiển thị Dialog
                dialog.show();

                //Kiểm tra xem người dùng đã chọn ảnh hay chưa
                if (image != null){
                    //Thiết lập đường dẫn đến nới lưu trữ
                    StorageReference reference = storage.getReference().child("Profile").child(auth.getCurrentUser().getPhoneNumber());
                    //Khi tải ảnh lên thành công
                    reference.putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                //Lấy URL của ảnh trên Storage Firebase
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //Lấy URL ảnh
                                        String imageURL = uri.toString();
                                        //Lấy ID người dùng
                                        String uid = auth.getUid();
                                        //Lấy số điện thoại người dùng
                                        String phone = auth.getCurrentUser().getPhoneNumber();
                                        //Lấy tên người dùng
                                        String name = binding.edtName.getText().toString().trim();
                                        //Khởi tạo user với những thông tin trên
                                        User user = new User(uid, name, phone, imageURL,"","");
                                        //Lưu thông tin người dùng lên RealTime Database
                                        database.getReference().child("users").child(phone).setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        //Khi hệ thống xử lý xong thì tắt dialog
                                                        dialog.dismiss();
                                                        //Tạo Intent đến SplashScreen
                                                        Intent intent = new Intent(SetupProfileActivity.this, SplashScreenActivity.class);
                                                        //start intent
                                                        startActivity(intent);
                                                        //Kết thúc activity này
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }else{
                    //Nếu người dùng không chọn ảnh
                    //Lấy ID người dùng
                    String uid = auth.getUid();
                    //Lấy số điện thoại người dùng
                    String phone = auth.getCurrentUser().getPhoneNumber();

                    //Khởi tạo user với những thông tin và không có ảnh
                    User user = new User(uid, name, phone, "Không có thông tin ảnh","","");

                    //Lưu thông tin người dùng lên RealTime Database
                    database.getReference().child("users").child(phone).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //Khi hệ thống xử lý xong thì tắt dialog
                                    dialog.dismiss();
                                    //Tạo Intent đến SplashScreen
                                    Intent intent = new Intent(SetupProfileActivity.this, SplashScreenActivity.class);
                                    //start intent
                                    startActivity(intent);
                                    //Kết thúc activity này
                                    finish();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (data != null) {
                if (data.getData() != null) {
                    binding.imageViewProfile.setImageURI(data.getData());
                    image = data.getData();
                }
            }
        }
    }
}