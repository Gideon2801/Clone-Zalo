package com.hcmute.edu.vn.zalo.group11.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.hcmute.edu.vn.zalo.group11.R;
import com.hcmute.edu.vn.zalo.group11.activity.MainActivity;
import com.hcmute.edu.vn.zalo.group11.databinding.ItemStoryBinding;
import com.hcmute.edu.vn.zalo.group11.model.Story;
import com.hcmute.edu.vn.zalo.group11.model.UserStory;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.Holder> {

    Context context;
    ArrayList<UserStory> storyArrayList;
    FirebaseAuth auth;

    public StoryAdapter(Context context, ArrayList<UserStory> storyArrayList) {
        this.context = context;
        this.storyArrayList = storyArrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Khởi tạo View theo thành phần của layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        //Khởi tạo user được lấy ra dựa theo vị trí trong danh sách
        UserStory userStory = storyArrayList.get(position);
        //Thiết lập đường dẫn tới tài khaonr user đang sử dụng
        auth = FirebaseAuth.getInstance();

        //Hiển thị hình ảnh người up story lên biểu tượng story, nếu không có ảnh đại diện thì để mặc định là icon_person
        Glide.with(context).load(userStory.getImageProfile()).placeholder(R.drawable.icon_person).into(holder.binding.image);

        //Sự kiện khi nhấn vào hình ảnh có story
        holder.binding.circularStoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khởi tạo mảng dứa danh sách các story
                ArrayList<MyStory> myStories = new ArrayList<>();
                //Lấy các hình ảnh mà người dùng dùng để up story
                for (Story story : userStory.getStories()){
                    myStories.add(new MyStory(story.getImageURL()));
                }
                //Khởi tạo View hiển thị story
                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // 5000 Millis (5 Seconds) thời gian story hiển thị
                        .setTitleText(userStory.getName()) // Đưa tên người dùng hiển thị ra Title Tetx
                        .setSubtitleText("") // Không có subtitleText
                        .setTitleLogoUrl(userStory.getImageProfile()) // đưa ảnh đại dienj của người dùng up story đó ra
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        //Lấy số lượng story trong mảng
        return storyArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        //Ánh xạ tới các thành phần của View
        ItemStoryBinding binding;
        public Holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStoryBinding.bind(itemView);
        }
    }
}
