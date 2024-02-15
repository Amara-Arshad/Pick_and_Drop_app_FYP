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
import com.fyp.securepickanddrop.adapterclasses.MyPassengersAdapter;
import com.fyp.securepickanddrop.adapterclasses.SearchAdapter;
import com.fyp.securepickanddrop.adapterclasses.SearchedDriversAdapter;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.fyp.securepickanddrop.modelsclasses.InstituteModelClass;
import com.fyp.securepickanddrop.modelsclasses.PassengerModelClass;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchedDriversFragment extends Fragment {
    View view;
    RecyclerView p_recyclerView;
    List<UserModelClass> ItemList;
    private ProgressDialog pDialog;
    SearchedDriversAdapter mAdapter;
    String getDriversUrl = "search-drivers",institute_name="",institue_id="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.searched_drivers_screen,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        if (getArguments()!=null){
            institute_name=getArguments().getString("name");
            institue_id=getArguments().getString("institute_id");
        }else {
            Toast.makeText(getContext(), "Some error", Toast.LENGTH_SHORT).show();
        }
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        p_recyclerView = view.findViewById(R.id.rv_driversList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        p_recyclerView.addItemDecoration(dividerItemDecoration);
        //GridLayoutManager gridLayoutManager=new GridLayoutManager(MainActivity.this,2);
        ItemList = new ArrayList<>();
        p_recyclerView.setLayoutManager(linearLayoutManager);

        if (!institute_name.isEmpty()){
            GetDrivers(institute_name);
        }else {
            Toast.makeText(getContext(), "please select institute", Toast.LENGTH_SHORT).show();
        }
    }

    private void GetDrivers(String institute_name) {
        pDialog.setMessage("please Wait....");
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl+getDriversUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response is", response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Log.d("status", "CHECK" + jsonObject.getString("status"));
                        if (jsonObject.getString("status").equals("true")) {
                            UserModelClass model = new UserModelClass();
                            model.setInstitute_id(jsonObject.getString("institute_id"));
                            model.setIntitute_name(jsonObject.getString("institute_name"));

                            JSONArray jsonArray1= new JSONArray(jsonObject.getString("drivers"));
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject jsonObject1 = (JSONObject) jsonArray1.get(j);

                                model.setUser_name(jsonObject1.getString("name"));
                                model.setUser_id(jsonObject1.getString("id"));
                                model.setUser_email(jsonObject1.getString("email"));
                                model.setUser_address(jsonObject1.getString("address"));
                                model.setUser_mobile(jsonObject1.getString("mobile"));
                                model.setUser_image(jsonObject1.getString("user_image"));
                                model.setRating(jsonObject1.getString("average_rating"));
                                model.setNo_of_seats(jsonObject1.getString("no_of_seats"));
                                model.setFirebase_id(jsonObject1.getString("firebase_id"));
                                if (!jsonObject1.isNull("schedule")){
                                    model.setStart_time(jsonObject1.getJSONObject("schedule").getString("start_time"));
                                    model.setEnd_time(jsonObject1.getJSONObject("schedule").getString("end_time"));
                                }
                                ItemList.add(model);
                            }

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "No Institute", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();
                    if (ItemList != null) {
                        mAdapter = new SearchedDriversAdapter(getContext(), ItemList);
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
                Toast.makeText(getContext(), "Some Error", Toast.LENGTH_SHORT).show();


            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", institute_name);
                return params;
            }

        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Drivers List");
        super.onViewCreated(view, savedInstanceState);
    }
}
