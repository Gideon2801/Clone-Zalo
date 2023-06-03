package com.hcmute.edu.vn.zalo.group11.activity;

import static android.Manifest.permission.READ_CONTACTS;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmute.edu.vn.zalo.group11.R;
import com.hcmute.edu.vn.zalo.group11.adapter.ViewPagerAdapter;
import com.hcmute.edu.vn.zalo.group11.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{READ_CONTACTS},100);
            }
        }

        setUpViewPager();

        //Xử lý chuyển trang khi chọn các item ở bottom navigation
        binding.bottomnavigattion.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Dựa vào các item của Navigation để chuyển đến các fragment tương ứng
                switch (item.getItemId()){
                    case R.id.chat:
                        //id của icon chat sẽ là vị trí 0 tương ứng với ChatFragment được setup trong ViewPager
                        binding.viewPager.setCurrentItem(0);
                        break;
                    case R.id.contact:
                        //id của icon contact sẽ là vị trí 1 tương ứng với ContactFragment được setup trong ViewPager
                        binding.viewPager.setCurrentItem(1);
                        break;
                    case R.id.timeline:
                        //id của icon contact sẽ là vị trí 2 tương ứng với TimeLineFragment được setup trong ViewPager
                        binding.viewPager.setCurrentItem(2);
                        break;
                    case R.id.profile:
                        //id của icon contact sẽ là vị trí 3 tương ứng với ProfileFragment được setup trong ViewPager
                        binding.viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });



    }
    //Cài đặt để hiện thị các fragment hợp với bottom Navigation
    private void setUpViewPager() {
        //Khởi tạo ViewPagerAdapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setAdapter(viewPagerAdapter);
        //Xử lí sự kiện khi fragment bị thay đổi
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            //Xử lý chuyển trang khi mình kéo qua phải, qua trái
            @Override
            public void onPageSelected(int position) {
                //Lựa chọn các vị trí
                switch (position){
                    case 0:
                        //Vị trí 0 là icon chat trong menu
                        binding.bottomnavigattion.getMenu().findItem(R.id.chat).setChecked(true);
                        break;
                    case 1:
                        //Vị trí 1 là icon contact trong menu
                        binding.bottomnavigattion.getMenu().findItem(R.id.contact).setChecked(true);
                        break;
                    case 2:
                        //Vị trí 2 là icon timeline trong menu
                        binding.bottomnavigattion.getMenu().findItem(R.id.timeline).setChecked(true);
                        break;
                    case 3:
                        //Vị trí 3 là icon profile trong menu
                        binding.bottomnavigattion.getMenu().findItem(R.id.profile).setChecked(true);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //Khi vẫn đang sử dung ứng dụng
    @Override
    protected void onResume() {
        super.onResume();
        //Lấy số điện thoại mà người dùng đang sử dụng để truy cập hệ thống
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        //Cập nhật trạng thái và lưu lại trên RealTime Database
        FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference().child("Active").child(phoneNumber).setValue("Online");
    }

    //Khi vẫn không sử dung ứng dụng
    @Override
    protected void onStop() {
        super.onStop();
        //Kiểm tra xem người dùng đã đăng xuất chưa, nếu đăng xuất rồi thì return kết thức hàm
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            return;
        }
        //Lấy số điện thoại mà người dùng đang sử dụng để truy cập hệ thống
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        //Cập nhật trạng thái và lưu lại trên RealTime Database
        FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference().child("Active").child(phoneNumber).setValue("Offline");
    }
}