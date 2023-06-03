package com.hcmute.edu.vn.zalo.group11.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.hcmute.edu.vn.zalo.group11.R;
import com.hcmute.edu.vn.zalo.group11.activity.MainActivity;
import com.hcmute.edu.vn.zalo.group11.activity.OTPActivity;
import com.hcmute.edu.vn.zalo.group11.activity.SetupProfileActivity;
import com.hcmute.edu.vn.zalo.group11.activity.WelcomeScreenActivity;
import com.hcmute.edu.vn.zalo.group11.adapter.ChatAdapter;
import com.hcmute.edu.vn.zalo.group11.model.User;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    //Hiển thị dialog để người dùng chỉnh sửa thông tin
    ProgressDialog dialog;
    //Các thành phần chính trong View
    ImageView imageProfile, editname, editbirth, editaddress;
    TextView txtname, txtuserID, txtphone, txtbirthday, txtaddress;
    Button logout;
    //Các thông tin cần sử dụng cho nhiều hàm
    String name, userID, phone, birthday, address, imageUrl;
    //Khai báo các Service của firebase
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    //Khai báo hình ảnh kiểu uri để lấy đưuòng dẫn của hình ảnh
    Uri image;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Tạo tham chiếu đến bộ nhớ của ứng dụng
        database = FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        //Tạo tham chiếu đến tài khoản đang sử dụng
        auth = FirebaseAuth.getInstance();
        //Lấy số điện thoại của tài khoản đnag đưuọc sử dụng
        phone = auth.getCurrentUser().getPhoneNumber();
        //Tạo tham chiếu đến nơi lưu trữ trên Storage
        storage = FirebaseStorage.getInstance();

        //Ánh xạ đến các thành phần chính trong View
        imageProfile = view.findViewById(R.id.user_imageview);
        editname = view.findViewById(R.id.image_edit_name);
        editbirth = view.findViewById(R.id.image_edit_birthday);
        editaddress = view.findViewById(R.id.image_edit_address);
        txtname = view.findViewById(R.id.profile_name);
        txtuserID = view.findViewById(R.id.profileID);
        txtphone = view.findViewById(R.id.text_profile_phone);
        txtbirthday = view.findViewById(R.id.dayOfBirth);
        txtaddress = view.findViewById(R.id.profile_address);
        logout = view.findViewById(R.id.button_Logout);

        //Sự kiện khi nhấn vào hình đại diện
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khởi tạo Intent
                Intent intent = new Intent();
                //Đặt hành động cho intent
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //Đến nơi lưu trữ ảnh
                intent.setType("image/*");
                //Start intent với request code là 123
                startActivityForResult(intent, 123);
            }
        });
        //Sự kiện thi nhấn button chỉnh sửa ở ô tên người dùng
        editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Gọi hàm mở dialog với nội dung cần sửa là tên người dùng và từ khóa là name
                openDialog("Tên người dùng", "name");
            }
        });
        //Sự kiện thi nhấn button chỉnh sửa ở ô sinh nhật
        editbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Gọi hàm mở dialog với nội dung cần sửa là Sinh nhật và từ khóa là birth
                openDialog("Sinh nhật", "birth");
            }
        });
        //Sự kiện thi nhấn button chỉnh sửa ở ô địa chỉ
        editaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Gọi hàm mở dialog với nội dung cần sửa là địa chỉ và từ khóa là address
                openDialog("Địa chỉ", "address");
            }
        });

        showDataUser();

        //Sự kiện khi nhấn button Logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khởi tạo Intent đến Welcome Screen
                Intent intent = new Intent(getContext(), WelcomeScreenActivity.class);
                //Chuyển đổi sang trạng thái Offline và lưu lại vào RealTime Database
                database.getReference().child("Active").child(phone).setValue("Offline");
                //Đăng xuất khỏi tài khoản đnag sử dụng
                auth.signOut();
                //Bắt đầu intent chuyển đến trang Welcome
                startActivity(intent);
            }
        });

        database.getReference().child("users").child(auth.getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Lấy tên người dùng được lưu trên hệ thống của tài khoản đang sử dụng
                name = snapshot.child("userName").getValue(String.class);
                userID = snapshot.child("userID").getValue(String.class);
                imageUrl =  snapshot.child("profileImage").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Lấy thông tin từ View
        birthday = txtbirthday.getText().toString().trim();
        address = txtaddress.getText().toString().trim();

        return view;
    }

    private void openDialog(String noidung, String change) {
        //Khởi tạo dialog và ánh xạ đến View của dialog
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);

        //Khởi tạo 1 cửa sổ cho dialog
        Window window =dialog.getWindow();

        //Nếu không thể khởi tạo của sổ để hiển thị thì return kết thúc hàm
        if (window == null){
            return;
        }

        //Khởi tạo và thiết lập vị trí, giao diện dialog hiển thị
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAtributes = window.getAttributes();
        windowAtributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAtributes);

        //Có thể tắt dialog bằng cách nhần ra ngoiaf dialog
        dialog.setCancelable(true);

        //Ánh xạ các thành phần trên dialog
        EditText editText = dialog.findViewById(R.id.edt_Edit);
        Button btnXacNhan = dialog.findViewById(R.id.button_XacNhan);
        Button btnHuy = dialog.findViewById(R.id.button_Huy);
        TextView txtNoiDung = dialog.findViewById(R.id.textView_NoiDung);

        //Hiển thị thành phần mà người dùng muốn chỉnh sửa lên tiêu đề dialog
        txtNoiDung.setText(noidung);

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kiểm tra xem trường mà người dùng muốn thay đổi là gì để thiết lập đường dẫn tới đó
                if (change == "name") {
                    //Lưu  nội dung được nhập trên editText (ở đây là chỉnh sửa tên người dùng)
                    name = editText.getText().toString().trim();
                }else if (change == "birth") {
                    // Lưu với nội dung được nhập trên editText (ở đây là chỉnh sửa ngày sinh)
                    birthday = editText.getText().toString().trim();

                }else if (change == "address") {
                    //:ưu với nội dung được nhập trên editText (ở đây là chỉnh sửa ngày sinh)
                    address = editText.getText().toString().trim();
                }
                //Khởi tạo user với những thông tin đã được cập nhật
                User user = new User(userID,name,phone,imageUrl,birthday,address);
                //Cập nhật thông tin user vào RealTime Database
                database.getReference().child("users").child(auth.getCurrentUser().getPhoneNumber()).setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        });
                //Thông báo đến người dùng đã cập nhật thông tin thành công
                Toast.makeText(getActivity(), "Đã cập nhât", Toast.LENGTH_SHORT).show();
                //Tắt dialog
                dialog.dismiss();
                //Hiển thị lại thông tin User
                showDataUser();
            }
        });

        //Khi nhấn button Hủy của dialog
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tắt dialog
                dialog.dismiss();
            }
        });

        //Hiển thị dialog chỉnh sửa thông tin
        dialog.show();
    }

    //Hàm hiển thị thông tin người dùng
    private void showDataUser() {
        //Thiết lập đường dẫn đến nới lưu trữ thông tin người dùng trên RealTime Database
        database.getReference().child("users").child(auth.getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Khởi tạo user với thông tin được lấy từ RealTime Database
                User user = snapshot.getValue(User.class);
                //Hiển thị các thông tin lên View
                //Sử dụng Glide để lấy ảnh từ URl và truyền vào imageProfile, nếu không có thì sẽ hiển thị hình mặc định là icon_person
                Glide.with(getContext()).load(user.getProfileImage()).placeholder(R.drawable.icon_person).into(imageProfile);
                //Lấy thông tin còn lại và hiện thị lên các TextView
                txtname.setText(user.getUserName());
                txtuserID.setText(user.getUserID());
                txtphone.setText(user.getPhoneNumber());
                txtbirthday.setText(user.getBirthday());
                txtaddress.setText(user.getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Khi người dùng đã chọn đưuọc ảnh để Upload thì sẽ đến hàm này
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Khởi tạo dialog
        dialog = new ProgressDialog(getContext());
        //Đặt message hiển thị để biết hệ thống đang làm gì
        dialog.setMessage("Đang cập nhật thông tin...");
        //Dialog không thể tắt cho tới khi đưuọc gọi hàm dismiss()
        dialog.setCancelable(false);
        //Hiển thị dialog
        dialog.show();

        //Nếu đã chọn được ảnh
        if (requestCode == 123) {
            if (data != null) {
                if (data.getData() != null) {
                    //Hiển thị ảnh lên View
                    imageProfile.setImageURI(data.getData());
                    //Lấy uri của ảnh
                    image = data.getData();
                }
            }
        }

        //Thiết lập đường dẫn tới vị trí lưu ảnh
        StorageReference reference = storage.getReference().child("Profile").child(auth.getCurrentUser().getPhoneNumber());
        //Tải ảnh lên Firebase Storage
        reference.putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    //Lấy URL của ảnh trên Storage
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Lấy địa chỉ URL của ảnh vừa upload lên
                            String imageURL = uri.toString();
                            //Khởi tạo user với các thông tin đã lấy đưuọc
                            User user = new User(userID, name, phone, imageURL,birthday,address);
                            //Cập nhật thông tin user vào RealTime Database
                            database.getReference().child("users").child(auth.getCurrentUser().getPhoneNumber()).setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                        }
                                    });
                            //Đóng dialog khi hệ thống đã xử lí xong
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}