package com.fyp.securepickanddrop.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.fyp.securepickanddrop.R;

public class UserHomeFragment extends Fragment {
    View view;
    CardView search_cardview,my_booking_cardview,my_drivers_cardview,alert_cardview,start_racking_cardview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.user_home_fragment,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        search_cardview=view.findViewById(R.id.search_cardview);
        my_booking_cardview=view.findViewById(R.id.bookingrequests_cardview);
        my_drivers_cardview=view.findViewById(R.id.driver_cardview);
        alert_cardview=view.findViewById(R.id.alert_cardview);
        start_racking_cardview=view.findViewById(R.id.trip_cardview);

        search_cardview.setOnClickListener(view1 -> {

            InstitutesListForSearchFragment fragment = new InstitutesListForSearchFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("added").commit();

        });
        my_booking_cardview.setOnClickListener(view1 -> {

            UserRideRequestsFragment fragment = new UserRideRequestsFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("added").commit();
        });
        my_drivers_cardview.setOnClickListener(view1 -> {
            MyDriverFragment fragment = new MyDriverFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("added").commit();
        });
        alert_cardview.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "1122"));
            startActivity(intent);
        });
        start_racking_cardview.setOnClickListener(view1 -> {

            MyDriverFragment fragment = new MyDriverFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("added").commit();
        });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Home");
        super.onViewCreated(view, savedInstanceState);
    }
}
