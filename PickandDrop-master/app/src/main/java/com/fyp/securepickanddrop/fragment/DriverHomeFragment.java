package com.fyp.securepickanddrop.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DriverHomeFragment extends Fragment {
    View view;
    CardView add_institutes,emergency_call,passengers_list,requests_list,start_trip_cardview;
    private ProgressDialog pDialog;
    String user_id="",start_trip="start-trip";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.driver_home_fragmen,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        UserModelClass userModelClass= SharedPrefManager.getInstance(getContext()).getUser();
        if (userModelClass!=null){
            user_id=userModelClass.getUser_id();
        }
        add_institutes=view.findViewById(R.id.institutes_cardview);
        passengers_list=view.findViewById(R.id.passengers_cardview);
        emergency_call=view.findViewById(R.id.alert_cardview);
        requests_list=view.findViewById(R.id.requests_cardview);
        start_trip_cardview=view.findViewById(R.id.trip_cardview);
        start_trip_cardview.setOnClickListener(view1 -> {
            StartTripMethod(user_id);
        });
        add_institutes.setOnClickListener(view1 -> {

            AddinstitutesFragment fragment = new AddinstitutesFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("added").commit();

        });
        emergency_call.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "1122"));
            startActivity(intent);
        });
        passengers_list.setOnClickListener(view1 -> {

            MyPassengersFragment fragment = new MyPassengersFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("added").commit();

        });
        requests_list.setOnClickListener(view1 -> {

            DriverRideRequestFragment fragment = new DriverRideRequestFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("added").commit();

        });
    }

    private void StartTripMethod(String user_id) {
        pDialog.setMessage("Please Wait....");
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl+start_trip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response is", response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Log.d("status", "CHECK" + jsonObject.getString("status"));
                        if (jsonObject.getString("status").equals("true")) {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "Your location Online", Toast.LENGTH_SHORT).show();

                            StartTripDriverMap fragment = new StartTripDriverMap();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("added").commit();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();
                } catch (JSONException e) {
                    pDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(getContext(), "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getContext(), "Some Error", Toast.LENGTH_SHORT).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_id",user_id );


                return params;

            }
        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Home");
        super.onViewCreated(view, savedInstanceState);
    }
}
