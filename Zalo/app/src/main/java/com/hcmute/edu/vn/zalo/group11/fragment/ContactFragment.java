package com.hcmute.edu.vn.zalo.group11.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmute.edu.vn.zalo.group11.R;
import com.hcmute.edu.vn.zalo.group11.adapter.ContactAdapter;
import com.hcmute.edu.vn.zalo.group11.model.User;

import java.util.ArrayList;
import java.util.Objects;


public class ContactFragment extends Fragment {

    ContactAdapter contactAdapter; //* Adapter contact
    private ArrayList<User> userContacts, appContacts;
    String authPhoneNumber;
    FirebaseDatabase database;
    RecyclerView recyclerView;
    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //* ánh xạ các view vào binding
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        //Ánh xạ và thiết lập hiển thị RecycleView
        recyclerView = view.findViewById(R.id.list_contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Thiết lập đường dẫn tới bộ nhớ của ứng dụng
        database = FirebaseDatabase.getInstance("https://zalo-b293e-default-rtdb.asia-southeast1.firebasedatabase.app/");

        userContacts = new ArrayList<>(); //* danh sách contacts của user
        appContacts = new ArrayList<>();  //* danh sách contact của app
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //* Tham chiếu đến FirebaseAuth

        //* lấy ra số điện thoại người dùng đã đăng nhập
        authPhoneNumber = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();

        getUserContact(); //* lấy tất cả các contacts của user trên điện thoại

        return view;
    }

    private void getUserContact() {
        //Thông qua ContactsContract để lấy contact trong điện thoại
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        //Trả về 1 cursor - quản lí dữ liệu contact trong điện thoại
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        userContacts.clear();
        while (cursor.moveToNext()){
            //Lấy thông tin trong danh bạ điện thoại
            String contactName = ContactsContract.Contacts.DISPLAY_NAME;
            //Lấy thông tin số điện thoại trong danh bạ
            String contactPhone = ContactsContract.CommonDataKinds.Phone.NUMBER;
            //Lấy vị trí cột trong dữ liệu
            int vtName = cursor.getColumnIndex(contactName);
            int vtphone = cursor.getColumnIndex(contactPhone);
            //Lấy dữ liệu trong cột name
            String name = cursor.getString(vtName);
            //Lấy dữ liệu trong cột phone và điều hiểu lại số điện thoại
            String phone = "+84" + cursor.getString(vtphone).substring(1);
            //Thêm user vào mảng các user với 2 thông tin là name và phone number
            userContacts.add(new User("",name,phone,"","",""));
        }
        getUserAppContact(userContacts);
    }
    private void getUserAppContact(ArrayList<User> userContacts) {

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) { //? Trong trường hợp snapshot tồn tại
                    appContacts.clear(); //* clear danh sách danh bạ trước
                    for (DataSnapshot ds : snapshot.getChildren())  {
                        //* lấy ra từng số điện thoại của user trên firebase
                        String number = ds.child("phoneNumber").getValue().toString();
                        for (User user : userContacts) {
                            // Trong trường hợp số điện thoại trên firebase bằng với sdt trong danh bạ
                            // và phải khác với số điện thoại đang sử dụng để thao tác với hệ thống
                            if (user.getPhoneNumber().equals(number) && !number.equals(authPhoneNumber)) {
                                // get uID theo key uId
                                String uID = ds.child("userID").getValue().toString();
                                // get image theo key image
                                String image = ds.child("profileImage").getValue().toString();
                                // get birthday theo key image
//                                String birthday = Objects.requireNonNull(ds.child("birthday").getValue()).toString();
//                                // get address theo key image
//                                String address = ds.child("address").getValue().toString();

                                //* Tạo thể hiện user
                                User newContact = new User();
                                newContact.setPhoneNumber(user.getPhoneNumber());
                                newContact.setUserName(user.getUserName()); //* set name cho user
                                newContact.setUserID(uID); //* set uID cho user
                                newContact.setProfileImage(image); //* set image cho user
//                                newContact.setBirthday(birthday); //* set birthday cho user
//                                newContact.setAddress(address);  //* set address cho user
                                database.getReference().child("Contacts").child(authPhoneNumber).child(user.getPhoneNumber()).setValue(newContact)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                                appContacts.add(newContact); //* thêm user vào app contacts
                                break;
                            }
                        }
                    }
                    //* Khởi tạo adapter cho fragment
                    contactAdapter = new ContactAdapter(appContacts, getContext());
                    //* set adapter vào recycler view
                    recyclerView.setAdapter(contactAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
