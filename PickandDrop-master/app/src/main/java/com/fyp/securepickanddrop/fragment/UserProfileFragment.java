package com.fyp.securepickanddrop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;

public class UserProfileFragment extends Fragment {
    View view;
    ImageView userimg;
    TextView tv_user_name,tv_mobile,tv_email,tv_address,tv_edit_profile,tv_change_passowrd,tv_invite_friend;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.user_profile_screen,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        tv_user_name=view.findViewById(R.id.tv_user_name);
        userimg=view.findViewById(R.id.person_image2);
        tv_email=view.findViewById(R.id.txt_email);
        tv_mobile=view.findViewById(R.id.txt_mobile);
        tv_address=view.findViewById(R.id.txt_address);
        tv_edit_profile=view.findViewById(R.id.txt_edit_profile);
        tv_invite_friend=view.findViewById(R.id.txt_invite_friends);
        tv_change_passowrd=view.findViewById(R.id.txt_change_password);
        binddata();
        tv_edit_profile.setOnClickListener(view1 -> {
            EditUserProfileFragment fragment = new EditUserProfileFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("added").commit();
        });

        tv_change_passowrd.setOnClickListener(view1 -> {
            ChangePasswordFragment fragment = new ChangePasswordFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("added").commit();
        });
        tv_invite_friend.setOnClickListener(view1 -> {

            final String appPackageName = getContext().getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            getContext().startActivity(sendIntent);
        });
    }

    private void binddata() {
        UserModelClass userModelClass= SharedPrefManager.getInstance(getContext()).getUser();
        if (userModelClass!=null){
            tv_user_name.setText(userModelClass.getUser_name());
            tv_email.setText(userModelClass.getUser_email());
            tv_mobile.setText(userModelClass.getUser_mobile());
            tv_address.setText(userModelClass.getUser_address());
            Glide.with(getActivity()).load(userModelClass.getUser_image()).dontAnimate().fitCenter().placeholder(R.drawable.bussdriver).into(userimg);
        }
    }
}
