package com.hcmute.edu.vn.zalo.group11.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.hcmute.edu.vn.zalo.group11.activity.ChatActivity;
import com.hcmute.edu.vn.zalo.group11.databinding.ItemContactBinding;
import com.hcmute.edu.vn.zalo.group11.model.User;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder>{

    private List<User> list;
    private Context context;

    public ContactAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Khởi tạo View theo thành phần của layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact,parent,false);
        return new ContactAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.Holder holder, int position) {
        //Lấy thông tin người dùng theo vị trí trong danh sách
        User user = list.get(position);
        //Hiển thị thông tin lên View
        holder.binding.textViewUserName.setText(user.getUserName());
        holder.binding.textViewPhoneNumber.setText(user.getPhoneNumber());
        //lấy hình ảnh từ URL và hiển thị lên detailUserImage, nếu không có ảnh đại diện thì để ảnh mặc định là icon_person
        Glide.with(context).load(user.getProfileImage()).placeholder(R.drawable.icon_person).into(holder.binding.detailUserImage);

        //Xử lí sự kiện khi nhấn vào biểu tượng chat tương ứng với contact được hiển thị
        holder.binding.imageChatnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lấy số điện thoại của người sử dụng đnag thao tác (auth)
                String authPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                //Lưu thông tin liên hệ mà người dùng muốn Chat vào "Chatted" với đường dẫn của auth
                FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference().child("Chatted").child(authPhone).child(user.getPhoneNumber()).setValue(user);

                //Thiết lập đường dẫn đến nơi lưu trữ để lấy thông tin user đang sử dụng
                FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference().child("users").child(authPhone)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //Khởi tạo user để lấy thông tin người sử dụng(auth)
                                User user1 = snapshot.getValue(User.class);
                                //Lưu thông tin liên hệ của người dùng vào đường dẫn của người mà auth muốn Chat vào "Chatted"
                                FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference().child("Chatted").child(user.getPhoneNumber()).
                                        child(user1.getPhoneNumber()).setValue(user1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                //Khởi tạo Intent với dích đến là ChatActivity
                Intent intent = new Intent(context, ChatActivity.class);
                //Truyền vào intent 3 giá trị là name, phone và image của người liên hệ
                intent.putExtra("name", user.getUserName());
                intent.putExtra("phone",user.getPhoneNumber());
                intent.putExtra("image",user.getProfileImage());
                //Start Intent và chuyển đến chatroom
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //Lấy số lượng story trong mảng
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        //Ánh xạ tới các thành phần của View
        ItemContactBinding binding;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemContactBinding.bind(itemView);
        }
    }
}
