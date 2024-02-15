package com.fyp.securepickanddrop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fyp.securepickanddrop.R;

public class ChooseRegistrationTypeFragment extends Fragment {

    View view;
    Button btn_parent,btn_driver,btn_student;
    String user_type="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.choose_registration_type_screen,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        btn_driver=view.findViewById(R.id.btn_driver);
        btn_parent=view.findViewById(R.id.btn_parent);
        btn_student=view.findViewById(R.id.btn_student);
        btn_driver.setOnClickListener(view1 -> {
            user_type="3";
            CallRegistrationFragment(user_type);

        });
        btn_student.setOnClickListener(view1 -> {
            user_type="2";
            CallRegistrationFragment(user_type);
        });
        btn_parent.setOnClickListener(view1 -> {
            user_type="1";
            CallRegistrationFragment(user_type);
        });
    }

    private void CallRegistrationFragment(String user_type) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString("user_type", user_type);
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, fragment).addToBackStack("added").commit();
    }
}
