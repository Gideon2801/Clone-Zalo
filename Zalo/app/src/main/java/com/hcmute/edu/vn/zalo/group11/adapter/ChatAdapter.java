package com.hcmute.edu.vn.zalo.group11.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmute.edu.vn.zalo.group11.activity.ChatActivity;
import com.hcmute.edu.vn.zalo.group11.R;
import com.hcmute.edu.vn.zalo.group11.databinding.ItemChatBinding;
import com.hcmute.edu.vn.zalo.group11.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder> {

    private List<User> list;
    private Context context;

    public ChatAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Khởi tạo View theo thành phần của layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        //Lấy từng tin nhắn theo vị trí danh sách
        User user = list.get(position);
        //lấy số điện thoại của người dùng đang thao tác với hệ thống (auth)
        String senderPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        //Tạo phòng gửi tin nhắn bằng số điện thoại auth + người nhắn tin
        String senderRoom = senderPhone + user.getPhoneNumber();

        FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference().child("Active").child(user.getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) { // Trong trường hợp snapshot tồn tại
                    //Lấy thông tin là online hay offline
                    String active = snapshot.getValue(String.class);
                    //Kiểm tra xem có online hay không
                    if (active.equals("Online")){
                        //Nếu có thì đưuọc hiển thị
                        holder.binding.active.setVisibility(View.VISIBLE);
                    } else {
                        //Nếu không thì hiển thị là hông được hiển thị
                        holder.binding.active.setVisibility(View.GONE);
                    }
                }else {
                    //Nếu snapshot không tồn tại thì không được hiển thị
                    holder.binding.active.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Thiết lập đường dẫn đến nơi chat với mọi người để lấy ra thông tin nhắn cuối
        FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference().child("chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Kiểm tra xem có snapshot hay không, Tức là kiểm tra xem 2 người đã có tin nhắn với nhau hay chưa
                        if (snapshot.exists()) {
                            //Lấy ra tin nhắn cuối cùng giữa 2 người
                            String lastMessage = snapshot.child("lastMessage").getValue(String.class);
                            //Lấy ra thời gian của tin nhắn cuối cùng
                            long time = snapshot.child("lastMessageTime").getValue(Long.class);
                            //Thiết lập format của thời gian
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a");
                            //Hieenrn thị thông tin ra View
                            holder.binding.textViewDesciption.setText(lastMessage);
                            holder.binding.textViewDate.setText(dateFormat.format(new Date(time)));
                        } else {
                            //Nếu chưa từng nhắn tin
                            //Hiển thị Desc là "Nhấn để trò chuyện"
                            holder.binding.textViewDesciption.setText("Nhấn để trò chuyện");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Đưa dữ liệu ra View
        holder.binding.textViewUserName.setText(user.getUserName());

        //lấy hình ảnh từ URL và hiển thị lên detailUserImage, nếu không có ảnh đại diện thì để ảnh mặc định là icon_person
        Glide.with(context).load(user.getProfileImage()).placeholder(R.drawable.icon_person).into(holder.binding.detailUserImage);

        //Xử lí sự kiện khi nhấn vào item chat tương ứng được hiển thị
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khởi tạo Intent với dích đến là ChatActivity

                Intent intent = new Intent(context, ChatActivity.class);
                //Truyền vào intent 3 giá trị là name, phone và image của người liên hệ
                intent.putExtra("name", user.getUserName());
                intent.putExtra("phone", user.getPhoneNumber());
                intent.putExtra("image", user.getProfileImage());
                //Start Intent và chuyển đến chatroom
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //Lấy kích thước của danh sách
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        //Ánh xạ các thành phần trong view
        ItemChatBinding binding;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChatBinding.bind(itemView);
        }
    }
}
