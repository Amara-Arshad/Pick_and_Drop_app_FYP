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
import com.fyp.securepickanddrop.adapterclasses.MyIntitutesAdapter;
import com.fyp.securepickanddrop.adapterclasses.MyPassengersAdapter;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
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

public class MyInstitutesFragment extends Fragment {
    View view;
    RecyclerView i_recyclerView;
    List<InstituteModelClass> ItemList;
    private ProgressDialog pDialog;
    MyIntitutesAdapter mAdapter;
    String getInstitutesUrl = "get-driver-institutes",user_id="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.my_institute_screen,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        UserModelClass userModelClass = SharedPrefManager.getInstance(getContext()).getUser();
        if (userModelClass!=null){
            user_id=userModelClass.getUser_id();
        }else {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }

        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        i_recyclerView = view.findViewById(R.id.rv_myinstitutes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        i_recyclerView.addItemDecoration(dividerItemDecoration);
        //GridLayoutManager gridLayoutManager=new GridLayoutManager(MainActivity.this,2);
        ItemList = new ArrayList<>();
        i_recyclerView.setLayoutManager(linearLayoutManager);
        GetMyInstitutes(user_id);
    }

    private void GetMyInstitutes(String user_id) {
        pDialog.setMessage("please Wait....");
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl+getInstitutesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response is", response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Log.d("status", "CHECK" + jsonObject.getString("status"));
                        if (jsonObject.getString("status").equals("true")) {

                            JSONArray jsonArray1= new JSONArray(jsonObject.getString("institutes"));
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject jsonObject1 = (JSONObject) jsonArray1.get(j);
                                InstituteModelClass model = new InstituteModelClass();
                                model.setIns_name(jsonObject1.getString("name"));
                                model.setIns_id(jsonObject1.getString("id"));
                                model.setIns_lat(jsonObject1.getString("lat"));
                                model.setIns_lng(jsonObject1.getString("long"));
                                ItemList.add(model);
                            }

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "No Institute", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();
                    if (ItemList != null) {
                        mAdapter = new MyIntitutesAdapter(getContext(), ItemList);
                        i_recyclerView.setAdapter(mAdapter);
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
        getActivity().setTitle("My Institutes");
        super.onViewCreated(view, savedInstanceState);
    }
}
