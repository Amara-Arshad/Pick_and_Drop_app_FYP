package com.fyp.securepickanddrop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        UserModelClass userModelClass = SharedPrefManager.getInstance(this).getUser();
        if (userModelClass != null) {
            switch (userModelClass.getUser_type()) {
                case "3": {
                    Intent intent = new Intent(this, DriverMainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                case "2":
                case "1": {
                    Intent intent=new Intent(this, UserMainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
            }

        } else {
            Intent intent = new Intent(SplashActivity.this, RegistrationLoginActivity.class);
            startActivity(intent);
            finish();
        }


    }
}