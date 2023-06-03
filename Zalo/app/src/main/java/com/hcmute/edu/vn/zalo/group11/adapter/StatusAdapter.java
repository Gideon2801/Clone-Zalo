package com.hcmute.edu.vn.zalo.group11.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmute.edu.vn.zalo.group11.R;
import com.hcmute.edu.vn.zalo.group11.databinding.ItemStatusBinding;
import com.hcmute.edu.vn.zalo.group11.model.UserStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.Holder>{

    Context context;
    ArrayList<UserStatus> statusArrayList;

    public StatusAdapter(Context context, ArrayList<UserStatus> statusArrayList) {
        this.context = context;
        this.statusArrayList = statusArrayList;
    }

    @NonNull
    @Override
    public StatusAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Khởi tạo View theo thành phần của layout

        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent,false);
        return new StatusAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.Holder holder, int position) {

        //Lấy status theo từng vị trí trong mảng
        UserStatus userStatus = statusArrayList.get(position);

        //Thiết lập đường dẫn đến nơi lưu trữ Status
        FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference().child("Status").child(userStatus.getStatusID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Lấy tên người đăng status
                        String name = snapshot.child("name").getValue(String.class);
                        //Lấy thời gian đăng status
                        long time = snapshot.child("time").getValue(Long.class);
                        //Đặt format của thời gian
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a");

                        //Lấy thông tin mô tả của status
                        String desc = snapshot.child("description").getValue(String.class);
                        //Lấy địa chỉ hình ảnh của người đăng status
                        String imgProfileURL = snapshot.child("profileImage").getValue(String.class);
                        //Lấy địa chỉ hình ảnh mô tả status
                        String imgStatusURL = snapshot.child("statusImage").getValue(String.class);

                        //Hiển thị thông tin của status ra View
                        holder.binding.userName.setText(name);
                        holder.binding.desctiptionStatus.setText(desc);
                        holder.binding.statusTime.setText(dateFormat.format(new Date(time)));
                        //Dùng Glide để lấy ảnh từ URL và hiển thị lên view
                        Glide.with(context).load(imgProfileURL).placeholder(R.drawable.icon_person).into(holder.binding.imgUser);
                        if (imgStatusURL.equals("noImage")){
                            holder.binding.imgStatus.setVisibility(View.GONE);
                        }else {
                            Glide.with(context).load(imgStatusURL).into(holder.binding.imgStatus);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        //Lấy số lượng story trong mảng
        return statusArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        //Ánh xạ tới các thành phần của View
        ItemStatusBinding binding;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }
}
