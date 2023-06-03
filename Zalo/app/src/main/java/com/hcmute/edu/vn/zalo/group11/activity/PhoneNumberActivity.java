package com.hcmute.edu.vn.zalo.group11.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.hcmute.edu.vn.zalo.group11.R;
import com.hcmute.edu.vn.zalo.group11.databinding.ActivityPhoneNumberBinding;

public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;

    //check dùng để kiểm tra SĐT có hợp lệ hay không
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgCheck.setVisibility(View.GONE);
        //Đính kèm CarrierNumber editText vào CCP
        binding.ccp.registerCarrierNumberEditText(binding.phoneBox);

        //Trình xử lý thay đổi tính hợp lệ sẽ nhận được callBack mỗi khi tính hợp lệ của các thay đổi số đã nhập.
        binding.ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                //Kiểm tra xem số điện thoại có đúng định dạng với quốc gia hay không
                if (isValidNumber){
                    //Nếu đúng thì hiển thị tick xanh
                    binding.imgCheck.setImageResource(R.drawable.valid);
                    check = true;
                }else {
                    //Nếu sai thì hiển thị X đỏ
                    binding.imgCheck.setImageResource(R.drawable.invalid);
                    check = false;
                }
            }
        });

        //Bắt sự kiện nếu không có ký tự nào thì sẽ ẩn icon Check đằng sau đi, còn nếu có thì sẽ tự động hiện
        binding.phoneBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Lấy dữ liệu từ editable(edtPhone)
                String input = editable.toString().trim();
                //Kiểm tra xem có ký tự nào trong input không
                if (input.length() > 0){
                    //Nếu có thì sẽ hiện icon check lên
                    binding.imgCheck.setVisibility(View.VISIBLE);
                }else {
                    //Nếu không thì sẽ ẩn icon Check đi
                    binding.imgCheck.setVisibility(View.GONE);
                }

            }
        });

        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kiểm tra xem số điện thoại nhập vào có hợp lệ không
                if (check) {
                    String phoneNumber = '+' + binding.ccp.getSelectedCountryCode() + binding.phoneBox.getText().toString().trim();
                    Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                }else {
                    //Ngược lại thì thông báo
                    Toast.makeText(PhoneNumberActivity.this,"Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}