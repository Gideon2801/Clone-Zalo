package com.hcmute.edu.vn.zalo.group11.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.hcmute.edu.vn.zalo.group11.fragment.ChatFragment;
import com.hcmute.edu.vn.zalo.group11.fragment.ContactFragment;
import com.hcmute.edu.vn.zalo.group11.fragment.ProfileFragment;
import com.hcmute.edu.vn.zalo.group11.fragment.TimeLineFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    //Xét các trường hợp để hiển thị các fragment khác nhau
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChatFragment();
            case 1:
                return new ContactFragment();
            case 2:
                return new TimeLineFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new ChatFragment();
        }
    }

    //Lấy số lượng các fragment sẽ được chuyển đổi
    @Override
    public int getCount() {
        return 4;
    }
}
