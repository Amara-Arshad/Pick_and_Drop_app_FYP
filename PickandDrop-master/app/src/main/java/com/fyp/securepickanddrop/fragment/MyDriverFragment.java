package com.fyp.securepickanddrop.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.adapterclasses.MyDriversAdapter;
import com.fyp.securepickanddrop.adapterclasses.MyPassengersAdapter;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.fyp.securepickanddrop.modelsclasses.RideRequestsModel;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDriverFragment extends Fragment {
    View view;
    RecyclerView p_recyclerView;
    List<RideRequestsModel> ItemList;
    private ProgressDialog pDialog;
    MyDriversAdapter mAdapter;
    String getdriversUrl = "user-requests",user_id="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.my_drivers_list_fragment,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        UserModelClass userModelClass= SharedPrefManager.getInstance(getContext()).getUser();
        if (userModelClass!=null){
            user_id=userModelClass.getUser_id();
        }
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        p_recyclerView = view.findViewById(R.id.rv_mydriversList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        p_recyclerView.addItemDecoration(dividerItemDecoration);
        //GridLayoutManager gridLayoutManager=new GridLayoutManager(MainActivity.this,2);
        ItemList = new ArrayList<>();
        p_recyclerView.setLayoutManager(linearLayoutManager);
        if (!user_id.isEmpty()){
            MyDrivers(user_id);
        }else {
            Toast.makeText(getContext(), "driver id missing", Toast.LENGTH_SHORT).show();
        }
   /*     mAdapter = new MyPassengersAdapter(getContext(), ItemList);
        p_recyclerView.setAdapter(mAdapter);*/
    }

    private void MyDrivers(String user_id) {
        pDialog.setMessage("please Wait....");
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl+getdriversUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response is===?", response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Log.d("status", "CHECK" + jsonObject.getString("status"));
                        if (jsonObject.getString("status").equals("true")) {

                            JSONArray jsonArray1= new JSONArray(jsonObject.getString("requests"));
                            // JSONObject jsonObject1 = (JSONObject) jsonArray1.getJSONObject();
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject jsonObject1 = (JSONObject) jsonArray1.getJSONObject(j).getJSONObject("request");

                                if (jsonObject1.getString("status").equals("1")){
                                    RideRequestsModel model = new RideRequestsModel();
                                    model.setRequest_id(jsonObject1.getString("id"));
                                    model.setRequest_status(jsonObject1.getString("status"));
                                    model.setPickup_lng(jsonObject1.getString("long"));
                                    model.setPickup_lat(jsonObject1.getString("lat"));
                                    model.setNo_of_seats(jsonObject1.getString("no_of_seats"));
                                    model.setUser_id(jsonObject1.getJSONObject("driver").getString("id"));
                                    model.setUser_name(jsonObject1.getJSONObject("driver").getString("name"));
                                    model.setUser_image(jsonObject1.getJSONObject("driver").getString("user_image"));
                                    model.setFirebase_id(jsonObject1.getJSONObject("driver").getString("firebase_id"));
                                    model.setInstitute_id(jsonObject1.getJSONObject("institute").getString("id"));
                                    model.setInstitute_name(jsonObject1.getJSONObject("institute").getString("name"));
                                    model.setRating(jsonArray1.getJSONObject(j).getString("average_rating"));
                                    if (!jsonArray1.getJSONObject(j).isNull("schedule")){

                                        model.setStart_time(jsonArray1.getJSONObject(j).getJSONObject("schedule").getString("start_time"));
                                        model.setEnd_time(jsonArray1.getJSONObject(j).getJSONObject("schedule").getString("end_time"));
                                    }
                                    ItemList.add(model);
                                }

                            }

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "No Institute", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();
                    if (ItemList != null) {
                        mAdapter = new MyDriversAdapter(getContext(), ItemList);
                        p_recyclerView.setAdapter(mAdapter);
                    } else {
                        Toast.makeText(getContext(), "NO DATA", Toast.LENGTH_SHORT).show();
                    }
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
                Toast.makeText(getContext(), "Some Error"+error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                return params;
            }

        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("My Drivers");
        super.onViewCreated(view, savedInstanceState);
    }
}
