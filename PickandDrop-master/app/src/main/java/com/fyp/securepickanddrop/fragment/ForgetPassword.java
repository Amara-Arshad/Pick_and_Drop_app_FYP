package com.fyp.securepickanddrop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fyp.securepickanddrop.R;

public class ForgetPassword extends Fragment {
    View view;
    Button btn_submit;
    EditText et_number;
    String mobile_number="",back_link="F";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.forget_password_screen,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        btn_submit=view.findViewById(R.id.btn_submit);
        et_number=view.findViewById(R.id.et_mobile);
        btn_submit.setOnClickListener(view1 -> {
            mobile_number="+92"+et_number.getText().toString();
            if (!mobile_number.isEmpty()){
                VerifyPhoneFragment fragment = new VerifyPhoneFragment();
                Bundle args = new Bundle();
                args.putString("mobile", mobile_number);
                args.putString("link", back_link);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, fragment).addToBackStack("added").commit();
            }else {
                Toast.makeText(getActivity(), "Please enter number", Toast.LENGTH_SHORT).show();
            }


        });
    }
}
