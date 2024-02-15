package com.fyp.securepickanddrop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.fragment.AboutUsFragment;
import com.fyp.securepickanddrop.fragment.ContactUsFragment;
import com.fyp.securepickanddrop.fragment.DriverHomeFragment;
import com.fyp.securepickanddrop.fragment.MyDriverFragment;
import com.fyp.securepickanddrop.fragment.UserChatListFragment;
import com.fyp.securepickanddrop.fragment.UserHomeFragment;
import com.fyp.securepickanddrop.fragment.UserProfileFragment;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;
import com.google.android.material.navigation.NavigationView;

public class UserMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    TextView drivername, drivermail;
    ImageView userPhoto;
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.user_main_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        drivername = navigationView.getHeaderView(0).findViewById(R.id.username);
        drivermail = navigationView.getHeaderView(0).findViewById(R.id.useremail);
        userPhoto = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        final UserModelClass userModelClass = SharedPrefManager.getInstance(UserMainActivity.this).getUser();
        if (userModelClass != null) {
            drivername.setText(userModelClass.getUser_name());
            drivermail.setText(userModelClass.getUser_email());
            Glide.with(UserMainActivity.this).load(userModelClass.getUser_image()).dontAnimate().fitCenter().placeholder(R.drawable.bussdriver).into(userPhoto);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame,
                    new UserHomeFragment()).commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame,
                    new UserHomeFragment()).commit();

        }else if (id==R.id.nav_my_drivers){
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame,
                    new MyDriverFragment()).addToBackStack("fragment").commit();
        }

        else if (id == R.id.nav_profile) {

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame,
                    new UserProfileFragment()).addToBackStack("fragment").commit();

        }
        else if (id == R.id.nav_my_chat) {

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame,
                    new UserChatListFragment()).addToBackStack("fragment").commit();

        }
        else if (id == R.id.nav_about) {

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame,
                    new AboutUsFragment()).addToBackStack("fragment").commit();

        }else if (id == R.id.nav_contact) {

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame,
                    new ContactUsFragment()).addToBackStack("fragment").commit();

        }
        else if (id == R.id.nav_logout) {

            SharedPrefManager.getInstance(UserMainActivity.this).logOut();
            startActivity(new Intent(this, RegistrationLoginActivity.class));
            this.finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
