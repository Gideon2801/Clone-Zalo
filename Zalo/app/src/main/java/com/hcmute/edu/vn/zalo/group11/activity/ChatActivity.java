package com.hcmute.edu.vn.zalo.group11.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;

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
import com.hcmute.edu.vn.zalo.group11.adapter.MessageAdapter;
import com.hcmute.edu.vn.zalo.group11.databinding.ActivityChatBinding;
import com.hcmute.edu.vn.zalo.group11.model.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessageAdapter messageAdapter;
    ArrayList<Message> listmessage;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String senderRoom, receiverRoom, senderPhone, receiverphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Thiết lập dialog chờ xử lí khi tải ảnh lên
        dialog = new ProgressDialog(this);
        //Đặt tiêu đề dialog
        dialog.setMessage("Đang tải ảnh ..");
        //Dialog không thể tắt
        dialog.setCancelable(false);

        ////Lấy thông tin được truyền thông qua Intent
        String name = getIntent().getStringExtra("name");
        String imgURL = getIntent().getStringExtra("image");
        receiverphone = getIntent().getStringExtra("phone");
        senderPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        //Tạo id cho 2 phòng để lưu tin nhắn cho 2 người bằng sđt
        senderRoom = senderPhone + receiverphone;
        receiverRoom = receiverphone + senderPhone;

        //Khởi tạo danh sách tin nhắn
        listmessage = new ArrayList<>();
        //Truyền thông tin vào adapter
        messageAdapter = new MessageAdapter(this, listmessage, senderRoom, receiverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Đưa Adapter ra RecycleView
        binding.recyclerView.setAdapter(messageAdapter);

        //Thiết lập đường dẫn tới Firebase
        database = FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        storage = FirebaseStorage.getInstance();

        //Tạo đường dẫn tới nơi lưu trữ thông tin on, off của người dùng
        database.getReference().child("Active").child(receiverphone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) { // Trong trường hợp snapshot tồn tại
                    //Lấy thông tin là online hay offline
                    String active = snapshot.getValue(String.class);
                    //Kiểm tra xem có online hay không
                    if (active.equals("Online")){
                        //Nếu có thì hiển thị là "Đang hoạt động"
                        binding.status.setText("Đang hoạt động");
                        //Đặt màu chữ là xanh lá
                        binding.status.setTextColor(ContextCompat.getColor(ChatActivity.this, R.color.green));
                    } else {
                        //Nếu không thì hiển thị là "Không hoạt động"
                        binding.status.setText("Không hoạt động");
                        //Đặt màu chữ là đỏ
                        binding.status.setTextColor(ContextCompat.getColor(ChatActivity.this, R.color.red));
                    }
                }else {
                    //Nếu snapshot không tồn tại thì hiển thị là "Không hoạt động"
                    binding.status.setText("Không hoạt động");
                    //Đặt màu chữ là đỏ
                    binding.status.setTextColor(ContextCompat.getColor(ChatActivity.this, R.color.red));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Hiển thị hình ảnh từ URL thông qua Glide, nếu không óc thì để ảnh mặc định là icon_person
        Glide.with(ChatActivity.this).load(imgURL).placeholder(R.drawable.icon_person).into(binding.profile);

        //Đưa tên người dùng ra View
        binding.name.setText(name);

        //Dẫn tới nơi lưu trữ tin nhắn
        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Làm mới danh sách tin nhắn
                        listmessage.clear();
                        //Lấy toàn bộ tin nhắn
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            //Khởi tạo từng tin nhắn ứng với từng snapshot
                            Message message = snapshot1.getValue(Message.class);
                            //Lấy ID của tin nhắn là snapshot.getKey()
                            message.setMessageId(snapshot1.getKey());
                            //Thêm tin nhắn vào mảng các tin nhắn
                            listmessage.add(message);
                        }
                        messageAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Sự kiện khi nhấn vào biểu tượng gửi tin nhắn
        binding.ImageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lấy nội dung của tin nhắn
                String textMessage = binding.messageBox.getText().toString();

                //Lấy thời gian gửi tin nhắn
                Date date = new Date();
                //Lưu thông tin tin nhắn theo những thông tin trên
                Message message =  new Message(textMessage, senderPhone, date.getTime());
                //Reset lại messBox
                binding.messageBox.setText("");
                //Tạo key ngẫu nhiên để làm ID tin nhắn
                String randomKey = database.getReference().push().getKey();

                //Lưu tin nhắn vào firebase cho cả 2 người
                database.getReference().child("chats")
                        .child(senderRoom)          //Lưu vào phòng người gửi
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)        //Lưu vào phòng người nhận
                                        .child("messages")
                                        .child(randomKey)
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });

                //Tạo HashMap để lưu trữ thông tin về tin nhắn cuối cùng giữa 2 người là thời gian gửi của tin nhắn đó
                HashMap<String, Object> lastMessage = new HashMap<>();
                lastMessage.put("lastMessage", message.getMessage());
                lastMessage.put("lastMessageTime", date.getTime());

                //Cập nhật lại tin nahwns cuối của cả 2 phòng
                database.getReference().child("chats").child(senderRoom).updateChildren(lastMessage);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMessage);
            }
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khởi tạo Intent để lấy hình ảnh trong máy
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            }
        });

        //xử lí sự kiện khi nhấn vào biểu tượng máy ảnh
        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khởi tạo Intent để chụp ảnh bằng camera
                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivityForResult(intent, 321);

            }
        });

        //Sử lí sự kiện khi nhấn button quay lại
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kết thúc Acctivity này
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Khi chọn được ảnh để tải
        if(requestCode == 123) {
            if(data != null) {
                if(data.getData() != null) {
                    //Lấy thông tin uri của hình ảnh
                    Uri sendImage = data.getData();
                    //lấy thời gian lịch
                    Calendar calendar = Calendar.getInstance();
                    //Tạo ra tên của bức ảnh và đường dẫn tới nới lưu trữ
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    //Hiển thị dialog
                    dialog.show();
                    //Tạo ID của tin nhắn bằng cách random
                    String randomKey = database.getReference().push().getKey();
                    //Khi tải file ảnh lên firebase thành công
                    reference.putFile(sendImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            //Tắt dialoog
                            dialog.dismiss();
                            if(task.isSuccessful()) {
                                //Lấy URL của bức ảnh trên Storage Firebase
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //Lấy đường dẫn
                                        String filePath = uri.toString();

                                        //Thời gian hiện tại
                                        Date date = new Date();
                                        // date.Time() là miliseconds tính từ 1/1/1970 12:00:00 AM
                                        //Lưu tin nhắn text là 'photo' để mô tả hình ảnh được gửi
                                        Message message = new Message("photo", senderPhone, date.getTime());
                                        //Lưu đường dẫn ảnh vào message
                                        message.setImageUrl(filePath);
                                        //Clear mmessBox
                                        binding.messageBox.setText("");

                                        //Tạo HashMap để lưu thông tin tin nhắn cuối cùng của 2 người nếu là ảnh
                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMessage", message.getMessage());
                                        lastMsgObj.put("lastMessageTime", date.getTime());

                                        //Cập nhật trên 2 phòng chat của người nhận và gửi
                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        //Lưu tin nhắn ở cả 2 phòng chat của người nhận và gửi
                                        database.getReference().child("chats")
                                                .child(senderRoom)      //Phòng người gửi
                                                .child("messages")
                                                .child(randomKey)
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        database.getReference().child("chats")
                                                                .child(receiverRoom)        //Phòng người nhận
                                                                .child("messages")
                                                                .child(randomKey)
                                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                    //Tạo khoảng thời gian để xóa hình ảnh
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Xóa file ảnh trên Storage Firebase
                            reference.delete();

                            //Xóa tin nhắn ảnh trên RealTime Database theo đường dẫn đến phòng người gửi
                            FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(randomKey).removeValue();

                            //Xóa tin nhắn ảnh trên RealTime Database theo đường dẫn đến phòng người nhận
                            FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                        .child(randomKey).removeValue();

                        }
                    },15000); //Demo sau 15 giây hình ảnh sẽ tự động bị xóa
                }
            }
        }
    }

}