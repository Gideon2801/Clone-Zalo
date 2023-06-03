package com.hcmute.edu.vn.zalo.group11.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hcmute.edu.vn.zalo.group11.databinding.ActivityAddStatusBinding;
import com.hcmute.edu.vn.zalo.group11.model.User;
import com.hcmute.edu.vn.zalo.group11.model.UserStatus;

import java.util.Date;
import java.util.HashMap;

public class AddStatusActivity extends AppCompatActivity {

    ActivityAddStatusBinding binding;
    Uri image;
    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ////Thiết lập đường đẫn đến Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        storage = FirebaseStorage.getInstance();

        //Lấy thông tin chủ tài khoản
        database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Lưu thông tin user
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //Xử lí khi nhấn vào button chọn ảnh
        binding.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Khởi tạo Intent cho phép người dùng chọn ảnh từ máy của mình
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            }
        });

        binding.SelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Khởi tạo Intent cho phép người dùng chọn ảnh từ máy của mình
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            }
        });

        //Xử lí sự kiện nhấn button để up Status
        binding.UpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lấy nội dung text
                String description = binding.addDescStatus.getText().toString().trim();

                //Nếu người dùng không ghi mô tả và chọn ảnh up load thì sẽ hiện thông báo yêu cầu phải có ít nhất 1 trong 2
                if (description.isEmpty() && image == null) {
                    Toast.makeText(AddStatusActivity.this, "Bạn phải có ảnh hoặc mô tả trạng thái thì mới có thể đăng lên", Toast.LENGTH_LONG).show();
                    //Kết thúc hàm
                    return;
                }
                //Khởi tạo dialog
                dialog = new ProgressDialog(AddStatusActivity.this);
                //Đặt tiêu đề cho dialog
                dialog.setMessage("Đang đăng tải lên...");
                //Dialog không thể tắt
                dialog.setCancelable(false);
                //Hiển thị dialog
                dialog.show();

                //Nếu người dùng chọn ảnh để up
                if (image != null) {
                    //Tải file ảnh lên Storage Firebase theo đường dẫn thiết lập
                    StorageReference reference = storage.getReference().child("Status").child(auth.getCurrentUser().getPhoneNumber());
                    reference.putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                //Lấy URL của hình được up lên Firebase
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //Khởi tạo UserStatus
                                        UserStatus userStatus = new UserStatus();
                                        //Truyền thông tin cần thiết của user vào userStatus
                                        userStatus.setName(user.getUserName());                 //tên
                                        userStatus.setImageProfile(user.getProfileImage());     //URL ảnh
                                        userStatus.setPhone(user.getPhoneNumber());             //Số điện thoại

                                        //Tạo ID status ngẫu nhiên
                                        String randomKey = database.getReference().push().getKey();
                                        //Lấy URL hình ảnh
                                        String imageURL = uri.toString();
                                        //Khởi tạo thời gian up Status
                                        Date date = new Date();

                                        //Khởi tạo HashMap để truyền thông tin đối tượng
                                        HashMap<String, Object> obj = new HashMap<>();
                                        obj.put("statusID", randomKey);
                                        obj.put("phoneNumber", userStatus.getPhone());
                                        obj.put("name", userStatus.getName());
                                        obj.put("profileImage", userStatus.getImageProfile());
                                        obj.put("description", description);
                                        obj.put("statusImage",imageURL);
                                        obj.put("time",date.getTime());

                                        //Cập nhật thông tin của status
                                        database.getReference().child("Status")
                                                .child(randomKey)
                                                .updateChildren(obj);
                                        //kết thúc Activity này
                                        finish();
                                        //Tắt dialog
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    });
                }else {
                    UserStatus userStatus = new UserStatus();
                    //Truyền thông tin cần thiết của user vào userStatus
                    userStatus.setName(user.getUserName());                 //tên
                    userStatus.setImageProfile(user.getProfileImage());     //URL ảnh
                    userStatus.setPhone(user.getPhoneNumber());             //Số điện thoại

                    //Tạo ID status ngẫu nhiên
                    String randomKey = database.getReference().push().getKey();
                    //Lấy URL hình ảnh
                    String imageURL = "noImage";
                    //Khởi tạo thời gian up Status
                    Date date = new Date();

                    //Khởi tạo HashMap để truyền thông tin đối tượng
                    HashMap<String, Object> obj = new HashMap<>();
                    obj.put("statusID", randomKey);
                    obj.put("phoneNumber", userStatus.getPhone());
                    obj.put("name", userStatus.getName());
                    obj.put("profileImage", userStatus.getImageProfile());
                    obj.put("description", description);
                    obj.put("statusImage",imageURL);
                    obj.put("time",date.getTime());

                    //Cập nhật thông tin của status
                    database.getReference().child("Status")
                            .child(randomKey)
                            .updateChildren(obj);
                    //kết thúc Activity này
                    finish();
                    //Tắt dialog
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Khi người dùng chọn được ảnh
        if (requestCode == 123) {
            if (data != null) {
                if (data.getData() != null) {
                    //Hiển thị ảnh ra View và lấy thông tin URL của hình ảnh
                    binding.imageViewSelect.setImageURI(data.getData());
                    image = data.getData();
                }
            }
        }
    }
}