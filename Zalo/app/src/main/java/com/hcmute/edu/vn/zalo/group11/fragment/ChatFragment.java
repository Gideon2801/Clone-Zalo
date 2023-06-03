package com.hcmute.edu.vn.zalo.group11.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmute.edu.vn.zalo.group11.R;
import com.hcmute.edu.vn.zalo.group11.adapter.ChatAdapter;
import com.hcmute.edu.vn.zalo.group11.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<User> users;
    ChatAdapter chatAdapter;

    //Khai báo danh sách các tin nhắn
    private List<User> list = new ArrayList<>();
    private RecyclerView recyclerView;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //Ánh xạ tới RecycleView
        recyclerView = view.findViewById(R.id.recycleView_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Gọi hàm getChatList()
        getChatList();

        return view;
    }

    //Hàm lấy danh sách những người đã chat với mình
    private void getChatList() {
        //Thiết lập tới nơi lưu trữ tài khoản đăng nhập
        auth = FirebaseAuth.getInstance();
        //Thiết lập tới nơi lưu trữ của ứng dụng
        database = FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        //Khởi tạo danh sách mảng
        users = new ArrayList<>();
        //Khởi tạo adapter và truyền adapter vào RecycleView
        chatAdapter = new ChatAdapter(users, getContext());
        recyclerView.setAdapter(chatAdapter);

        //Thiết lập đường dẫn đến nơi lưu những cuộc trò chuyện
        database.getReference().child("Chatted").child(auth.getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                //lấy toàn bộ user có trong child("Chatted") của người dùng
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    //Lấy thông tin user
                    User user = snapshot1.getValue(User.class);
                    //Nếu số điện thoại không trùng với số đang được sử dụng thì thêm vào mảng users
                    if (!user.getPhoneNumber().equals(auth.getCurrentUser().getPhoneNumber())) {
                        //Thêm user vào mảng
                        users.add(user);
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}