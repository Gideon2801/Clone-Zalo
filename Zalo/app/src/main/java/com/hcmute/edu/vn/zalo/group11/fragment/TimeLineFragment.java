package com.hcmute.edu.vn.zalo.group11.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.hcmute.edu.vn.zalo.group11.activity.AddStatusActivity;
import com.hcmute.edu.vn.zalo.group11.adapter.StatusAdapter;
import com.hcmute.edu.vn.zalo.group11.adapter.StoryAdapter;
import com.hcmute.edu.vn.zalo.group11.model.Story;
import com.hcmute.edu.vn.zalo.group11.model.User;
import com.hcmute.edu.vn.zalo.group11.model.UserStatus;
import com.hcmute.edu.vn.zalo.group11.model.UserStory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class TimeLineFragment extends Fragment {

    //Được dùng để Lưu trữ thông tin các story
    StoryAdapter storyAdapter;
    ArrayList<UserStory> storyArrayList;
    //Được dùng để Lưu trữ thông tin các status
    ArrayList<UserStatus> statusArrayList;
    StatusAdapter statusAdapter;
    //Dùng để hiện thị dánh sách các status và story
    RecyclerView recyclerViewStory, recyclerViewStatus;
    //Các thành phần chính trong View
    ImageView image_user, image_story;
    TextView textMessage;
    //Hiển thị khi hệ thống cần thời gian xử lí
    ProgressDialog dialog;
    //lấy data từ Firebase
    FirebaseDatabase database;
    //Khởi tạo user để sử dụng cho các hàm
    User user;

    public TimeLineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_line, container, false);

        //Ánh xạ đến các thành phần trong View
        image_story = view.findViewById(R.id.addStory);
        image_user = view.findViewById(R.id.imgUser);
        textMessage = view.findViewById(R.id.messageBox);
        recyclerViewStory = view.findViewById(R.id.recycleView_story);
        recyclerViewStatus = view.findViewById(R.id.recycleView_status);

        //Khởi tạo 1 dialog để hiển thị khi xử lí với Storage
        dialog = new ProgressDialog(getContext());
        //Đặt thông báo cho dialog để người dùng có thể hiểu hệ thống đang làm gì
        dialog.setMessage("Đang tải ảnh lên...");
        //Không thể tắt dialog cho đến khi dialog được dismiss()
        dialog.setCancelable(false);

        //Khởi tạo mảng chứa status và story
        storyArrayList = new ArrayList<>();
        statusArrayList = new ArrayList<>();

        //Tạo tham chiếu bộ nhớ đến RealTime Database
        database = FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/");;
        //Chỉ đến vị trí lưu trữ thông tin người dùng theo số điện thoại đang đăng nhập
        database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                        //Hiển thị ảnh người dùng từ URL vào image_user, nếu không có thì để ảnh mặc định là icon_person
                        Glide.with(getContext()).load(user.getProfileImage()).placeholder(R.drawable.icon_person).into(image_user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //List Story
        //Thiết lập đường dẫn đến vị trí lưu trữ thông tin story
        database.getReference().child("Story").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){  // Trong trường hợp snapshot tồn tại
                    //Reset mảng Story của người dùng
                    storyArrayList.clear();
                    //Lấy toàn bộ thông tin của người up Story
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        //Khởi tạo thông tin cá nhân của người up Story
                        UserStory story = new UserStory();
                        //Lưu số điện thoại người dùng vào story
                        story.setPhone(snapshot1.child("phoneNumber").getValue(String.class));
                        //Lưu tên người dùng vào story
                        story.setName(snapshot1.child("name").getValue(String.class));
                        //Lưu địa chỉ hình ảnh người dùng vào story
                        story.setImageProfile(snapshot1.child("profileImage").getValue(String.class));
                        //Lưu thời gian gần nhất người dùng up story người dùng vào story
                        story.setLastUpdate(snapshot1.child("lastUpdate").getValue(Long.class));

                        //Khởi tạo mảng để lưu các story của người dùng
                        ArrayList<Story> stories = new ArrayList<>();

                        //Lấy toàn bộ thông tin Story của người dùng
                        for(DataSnapshot statusSnapshot : snapshot1.child("Stories").getChildren()) {
                            //Lấy từng thông tin chi tiết của story
                            Story sampleStory = statusSnapshot.getValue(Story.class);
                            //Thêm story vừa lấy được vào mảng stories
                            stories.add(sampleStory);
                        }

                        //Lưu danh sách các story của người dùng vào story
                        story.setStories(stories);
                        //Lưu vào mảng story người dùng
                        storyArrayList.add(story);
                    }
                    storyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Khởi tạo StoryAdapter
        storyAdapter = new StoryAdapter(getContext(), storyArrayList);

        //Thiết lập RecycleView của Story để danh các các story hiển thị theo phương ngang
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerViewStory.setLayoutManager(linearLayoutManager);
        recyclerViewStory.setAdapter(storyAdapter);

        //Sự kiện nhấn vào text để thêm status
        textMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khởi tạo intent đến AddStatusActivity
                Intent intent = new Intent(getContext(), AddStatusActivity.class);
                //Start, chuyển đến AddStatusActivity
                startActivity(intent);
            }
        });

        //Sự kiện nhấn vào biểu tượng thêm story
        image_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khởi tạo Intent
                Intent intent = new Intent();
                //TRuy cập vào để lấy hình ảnh
                intent.setType("image/*");
                //Đặt action cho Intent này
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //Bắt đầu intent với requestCode là 123
                startActivityForResult(intent, 123);
            }
        });

        //Lấy ra danh sách các Status
        //Thiết lập đường dẫn lưu thông tin Status
        database.getReference().child("Status").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Làm mới mảng chứa các Status
                statusArrayList.clear();
                //Lấy toàn bộ Status đã được lưu trữ
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    //Lấy ra ID của status
                    String ID = snapshot1.getKey();
                    //Khởi tạo 1 status
                    UserStatus status = new UserStatus();
                    //Lưu ID của Status đó
                    status.setStatusID(ID);
                    //Thêm status với ID đưuọc lưu trữ vào danh sách
                    statusArrayList.add(status);
                }
                statusAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Khởi tạo adapter cho fragment
        statusAdapter = new StatusAdapter(getContext(), statusArrayList);
        //Set Layout cho RecycleView
        recyclerViewStatus.setLayoutManager(new LinearLayoutManager(getContext()));
        //Set Adapter vào RecycleView
        recyclerViewStatus.setAdapter(statusAdapter);

        return view;

    }

    //Hàm thực hiện khi lựa chọn hình ảnh trong máy
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Nếu data kahsc null, Tức là đã lựa chonja nhr
        if (data!= null){
            if (data.getData() != null){
                //Hiển thị dialog loading để chờ xử lí lưu trữ lên Firebase
                dialog.show();

                // Tạo tham chiếu bộ nhớ từ ứng dụng
                FirebaseStorage storage = FirebaseStorage.getInstance();
                //Lấy thời gian bắt đầu up story
                Date date = new Date();
                //Thiết lập đường dẫn lưu trữ trong Storage
                StorageReference reference = storage.getReference().child("story").child(date.getTime() + "");

                //Tạo ID cho story mới
                String randomKey = database.getReference().push().getKey();

                //Tải dữ liệu ảnh lên Firebase Storage
                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            //Lấy thành công địa chỉ hình ảnh được lưu trữ trên Firebase Storage
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //Tạo mới 1 user story
                                    UserStory userStory = new UserStory();
                                    //Lấy tên của người up story
                                    userStory.setName(user.getUserName());
                                    //Lấy địa chỉ ảnh profile của người up story
                                    userStory.setImageProfile(user.getProfileImage());
                                    //Cập nhật thời gian up story để biết story gần nhất được up vào khi nào
                                    userStory.setLastUpdate(date.getTime());

                                    //Tạo HashMap để map thông tin để chuẩn bị lưu trữ trên RealTime Database
                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name",userStory.getName());
                                    obj.put("profileImage",userStory.getImageProfile());
                                    obj.put("lastUpdate", userStory.getLastUpdate());

                                    //Lấy đường dẫn hình ảnh
                                    String imageURL = uri.toString();
                                    //Tạo mới 1 story với ID, URL hình ảnh và thời gian up story
                                    Story story =new Story(randomKey, imageURL,userStory.getLastUpdate());

                                    //Tạo mới hoặc cập nhật thông tin của người dùng khi tạo story
                                    database.getReference().child("Story")
                                                    .child(user.getPhoneNumber())
                                                            .updateChildren(obj);

                                    //Lưu thông tin story vào RealTime Database
                                    database.getReference().child("Story")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                                            .child("Stories")
                                                                    .child(randomKey)
                                                                            .setValue(story);
                                    //Đóng Dialog
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
                //Thiết lập cơ chế sau 1 khoảng thời gian sẽ xóa dữ liệu
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Xóa file ảnh trên Firebase Storage
                        reference.delete();

                        //Xóa thông tin story quá hạn trong RealTime Database
                        database.getReference().child("Story")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                .child("Stories")
                                .child(randomKey)
                                .removeValue();

                    }
                },1000*60*60*24); //1000(mili giây) * 60(giây) * 60(phút) * 24(giờ); đặt thời gian story hiển thị là 1 ngày
            }
        }
    }
}