package com.hcmute.edu.vn.zalo.group11.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmute.edu.vn.zalo.group11.R;
import com.hcmute.edu.vn.zalo.group11.databinding.ItemChatBinding;
import com.hcmute.edu.vn.zalo.group11.databinding.ItemReceiveBinding;
import com.hcmute.edu.vn.zalo.group11.databinding.ItemSentBinding;
import com.hcmute.edu.vn.zalo.group11.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> listmessages;

    //Set giá trị để nhận biết tin nhắn đó alf gửi đi hay nhận
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    //Để lưu ID của phòng nhận và gửi
    String senderRoom;
    String receiverRoom;

    public MessageAdapter(Context context, ArrayList<Message> listmessages, String senderRoom, String receiverRoom) {
        this.context = context;
        this.listmessages = listmessages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Kiểm tra xem viewType là của bên nhận hay gửi
        if (viewType == ITEM_SENT){
            //Nếu là bên gửi
            //Khởi tạo View gửi theo thành phần của layout item_sent
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false);
            return new SendViewHolder(view);
        }else {
            //Nếu là bên nhận
            //Khởi tạo View nhận theo thành phần của layout item_rêciver
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    //Kiểm tra xem tin nhắn của là của nhận hay gửi đi
    @Override
    public int getItemViewType(int position) {
        //Lấy thông tin tin nhắn dựa theo vị trí danh sách
        Message message = listmessages.get(position);
        //Kiểm tra xem tài khoản đang sử dụng với senderID của tin nhắn có giống nhau hay không
        // Note:ở đây em đặt senderID là số điện thoại người gửi
        if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().equals(message.getSenderId()))
        {
            //Nếu giống thì trả về kết quả là bên gửi
            return ITEM_SENT;
        }else {
            //Nếu không thì trả về kết quả là bên nhận
            return ITEM_RECEIVE;
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Lấy thông tin tin nhắn dựa theo vị trí danh sách
        Message message = listmessages.get(position);

        //Tạo mảng các reaction để người dùng lựa chọn
        int reaction[]= new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        //Khởi tạo ReactionsConfig với những biểu tưởng ở trên
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reaction)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            //Nếu không lựa chọn thì ẩn thanh reaction
            if (pos<0){
                return true;
            }
            //Kiểm tra xem holder hiện tại có phải là SendViewHolder hay không
            if (holder.getClass() == SendViewHolder.class) {
                //Khởi tạo ReceiverViewHolder
                SendViewHolder sendViewHolder = (SendViewHolder) holder;
                //Lấy hình ảnh reaction theo lựa chọn người dùng
                sendViewHolder.binding.feeling.setImageResource(reaction[pos]);
                //Hiển thị Image mô tả reaction
                sendViewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else {
                //Nếu đang là holder nhận tin nhắn
                //Khởi tạo ReceiverViewHolder
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                //Lấy hình ảnh reaction theo lựa chọn người dùng
                receiverViewHolder.binding.feeling.setImageResource(reaction[pos]);
                //Hiển thị Image mô tả reaction
                receiverViewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            //Lấy giá trị của việc reaction
            message.setFeeling(pos);

            //Cập nhật feeling tin nhắn cho senderRoom bằng đường dẫn tới vị trí lưu trữ tin nhắn
            FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            //Cập nhật feeling tin nhắn cho receiverRoom bằng đường dẫn tới vị trí lưu trữ tin nhắn
            FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);
            return true; // true đang đóng cửa sổ bật lên, false đang yêu cầu lựa chọn mới
        });

        //Kiểm tra xem holder hiện tại có phải là SendViewHolder hay không
        if (holder.getClass() == SendViewHolder.class){
            SendViewHolder sendViewHolder = (SendViewHolder) holder;

            //Phía tin nhắn cuẩ auth gửi
            //Xử lí sự kiện khi tin nhắn hình ảnh bị nhấn giữ lâu
            sendViewHolder.binding.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //Khởi tạo AlertDialog
                    AlertDialog.Builder dialogxoa = new AlertDialog.Builder(context);
                    //Hiển thị dialog để xác nhận lại với người dùng có muốn xóa tin nhắn hay không
                    dialogxoa.setMessage("Bạn chắc chắn muốn xóa không?");
                    //Mô tả button đồng ý của dialog là "Có"
                    dialogxoa.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        //Xử lý sự kiện khi nhấn button
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Xóa tin nhắn theo ID tin nhắn của bên người nhận
                            FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(message.getMessageId()).removeValue();

                            //Xóa tin nhắn theo ID tin nhắn của bên người gửi
                            FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(message.getMessageId()).removeValue();
                        }
                    });
                    //Mô tả button phủ định của dialog là "không"
                    dialogxoa.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    //Hiển thị dialog xóa tin nhắn
                    dialogxoa.show();
                    return false;
                }
            });

            //Nếu tin nhắn text mô tả là photo
            if (message.getMessage().equals("photo")) {
                //Lấy hình ảnh từ URL và hiển thị hình ảnh đó lên Image tin nhắn
                Glide.with(context).load(message.getImageUrl()).into(sendViewHolder.binding.image);
                //Set nhắn hình ảnh được hiện thị
                sendViewHolder.binding.image.setVisibility(View.VISIBLE);
                //Set Text không được hiển thị
                sendViewHolder.binding.message.setVisibility(View.GONE);
            }else {
                //Nếu không phải tin nhắn hình ảnh
                //Set Text hiển thị
                sendViewHolder.binding.message.setVisibility(View.VISIBLE);
                //Hiển thị tin nhắn ra View
                sendViewHolder.binding.message.setText(message.getMessage());
                //Image tin nhắn ẩn
                sendViewHolder.binding.image.setVisibility(View.GONE);
            }

            //Nếu feeling của tin nhắn >= 0 thì sẽ hiển thị cảm xúc theo feeling
            if (message.getFeeling() >= 0){
                //Lấy hình ảnh dựa theo biến feeling để hiện thị reaction
                sendViewHolder.binding.feeling.setImageResource(reaction[message.getFeeling()]);
                //set Image mô tả cảm xúc hiển thị
                sendViewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else {
                //set Image mô tả cảm xúc ẩn
                sendViewHolder.binding.feeling.setVisibility(View.GONE);
            }
            //Lấy thời gian gửi tin nhắn
            long time = message.getTimestamp();
            //Thiết lập format của thời gian
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a");
            //Đưa thời gian ra màn hình hiển thị
            sendViewHolder.binding.time.setText(dateFormat.format(new Date(time)));
            //Sự kiện khi người dùng chạm vào tin nhắn

            sendViewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    //Hiển thị các biểu tượng cảm xúc
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });
        }else {
            //Nếu đang là holder nhận tin nhắn
            //Khởi tạo ReceiverViewHolder
            ReceiverViewHolder recyclerView = (ReceiverViewHolder) holder;

            //Phía tin nhắn của auth nhận
            //Xử lí sự kiện khi tin nhắn hình ảnh bị nhấn giữ lâu
            recyclerView.binding.imageReceiver.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //Khởi tạo AlertDialog
                    AlertDialog.Builder dialogxoa = new AlertDialog.Builder(context);
                    //Hiển thị dialog để xác nhận lại với người dùng có muốn xóa tin nhắn hay không
                    dialogxoa.setMessage("Bạn chắc chắn muốn xóa không?");
                    //Mô tả button đồng ý của dialog là "Có"
                    dialogxoa.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        //Xử lý sự kiện khi nhấn button
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Xóa tin nhắn theo ID tin nhắn của bên auth
                            //Vì auth không có quyền xóa tin nhắn của đối phương nên chỉ xóa ở senderRoom
                            FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(message.getMessageId()).removeValue();

                        }
                    });
                    //Mô tả button phủ định của dialog là "không"
                    dialogxoa.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    //Hiển thị dialog xóa tin nhắn
                    dialogxoa.show();
                    return false;
                }
            });

            //Kiểm tra tin nhắn là hình ảnh hay không
            if (message.getMessage().equals("photo")) {
                //Nếu tin nhắn text mô tả là photo
                //Lấy hình ảnh từ URL và hiển thị hình ảnh đó lên Image tin nhắn
                Glide.with(context).load(message.getImageUrl()).into(recyclerView.binding.imageReceiver);
                //Set nhắn hình ảnh được hiện thị
                recyclerView.binding.imageReceiver.setVisibility(View.VISIBLE);
                //Set Text không được hiển thị
                recyclerView.binding.message.setVisibility(View.GONE);
            }else {
                //Nếu không phải tin nhắn hình ảnh
                //Set Text hiển thị
                recyclerView.binding.message.setVisibility(View.VISIBLE);
                //Hiển thị tin nhắn ra View
                recyclerView.binding.message.setText(message.getMessage());
                //Image tin nhắn ẩn
                recyclerView.binding.imageReceiver.setVisibility(View.GONE);
            }
            //Nếu feeling của tin nhắn >= 0 thì sẽ hiển thị cảm xúc theo feeling
            if (message.getFeeling() >= 0){
                //Lấy hình ảnh dựa theo biến feeling để hiện thị reaction
                recyclerView.binding.feeling.setImageResource(reaction[message.getFeeling()]);
                //set Image mô tả cảm xúc hiển thị
                recyclerView.binding.feeling.setVisibility(View.VISIBLE);
            }else {
                //set Image mô tả cảm xúc ẩn
                recyclerView.binding.feeling.setVisibility(View.GONE);
            }
            //Lấy thời gian gửi tin nhắn
            long time = message.getTimestamp();
            //Thiết lập format của thời gian
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a");
            //Đưa thời gian ra màn hình hiển thị
            recyclerView.binding.time.setText(dateFormat.format(new Date(time)));

            //Sự kiện khi người dùng chạm vào tin nhắn
            recyclerView.binding.message.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    //Hiển thị các biểu tượng cảm xúc
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        //Lấy số lượng tin nhắn trong mảng
        return listmessages.size();
    }

    //Tạo ra SendViewHolder và RecycleViewHolder để dễ dàng chuyển thôi vị trí
    // của các tin nhắn của người gửi và người nhận trên cùng 1 view chatroom

    public class SendViewHolder extends RecyclerView.ViewHolder{

        //Ánh xạ tới các thành phần của View người gửi
        ItemSentBinding binding;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        //Ánh xạ tới các thành phần của View người nhận
        ItemReceiveBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }
}
