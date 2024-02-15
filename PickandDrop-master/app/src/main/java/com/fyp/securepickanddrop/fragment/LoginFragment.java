package com.fyp.securepickanddrop.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.activities.DriverMainActivity;
import com.fyp.securepickanddrop.activities.UserMainActivity;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    View view;
    TextView tv_new_acount,tv_forget_password;
    EditText et_mobile,et_password;
    Button btn_login;
    String mobile="",password="",login="login";
    private ProgressDialog pDialog;
    UserModelClass userModelClass = new UserModelClass();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.login_screen,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        tv_forget_password=view.findViewById(R.id.tv_forget_password);
        tv_new_acount=view.findViewById(R.id.tv_new_account);
        et_mobile=view.findViewById(R.id.et_mobile_no);
        et_password=view.findViewById(R.id.et_password);
        btn_login=view.findViewById(R.id.loginButton);
        btn_login.setOnClickListener(view1 -> {

            if (validate()){
                LoginUser(mobile,password);
            }else {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
         /*   startActivity(new Intent(getActivity(), DriverMainActivity.class));
            getActivity().finish();*/

        });
        tv_new_acount.setOnClickListener(view1 -> {

            ChooseRegistrationTypeFragment fragment = new ChooseRegistrationTypeFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, fragment).addToBackStack("added").commit();

        });
        tv_forget_password.setOnClickListener(view1 -> {

            ForgetPassword forgetPasswordFragment = new ForgetPassword();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, forgetPasswordFragment).addToBackStack("added").commit();

        });
    }

    private void LoginUser(String mobile, String password) {

        Log.e("check1122", "mobile number" + mobile);
        pDialog.setMessage("Registring ...");
        pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, ConstantValues.mainurl+login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                Log.d("VerifyActivity","buyer method call"+response.toString());
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        pDialog.dismiss();
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        if (jsonObject.getString("status").equals("true")) {
                            JSONObject jsonObject1=jsonObject.getJSONObject("user");


                            if (jsonObject1.getString("user_type").equals("3")){

                                if (jsonObject1.getString("user_status").equals("1")){

                                    userModelClass.setUser_id(jsonObject1.getString("id"));
                                    userModelClass.setUser_name(jsonObject1.getString("name"));
                                    userModelClass.setUser_image(jsonObject1.getString("user_image"));
                                    userModelClass.setUser_email(jsonObject1.getString("email"));
                                    userModelClass.setUser_mobile(jsonObject1.getString("mobile"));
                                    userModelClass.setUser_address(jsonObject1.getString("address"));
                                    userModelClass.setUser_type(jsonObject1.getString("user_type"));
                                    userModelClass.setUser_status(jsonObject1.getString("user_status"));
                                    userModelClass.setUser_cnic_front(jsonObject1.getString("cnic_front"));
                                    userModelClass.setUser_cnic_back(jsonObject1.getString("cnic_back"));
                                    userModelClass.setFirebase_id(jsonObject1.getString("firebase_id"));



                                    if (SharedPrefManager.getInstance(getContext()).addUserToPref(userModelClass)) {
                                        pDialog.dismiss();
                                        Intent intent = new Intent(getContext(), DriverMainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    } else {
                                        pDialog.dismiss();
                                        Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }else if(jsonObject1.getString("user_status").equals("0")){

                                    userModelClass.setUser_id(jsonObject1.getString("id"));
                                    userModelClass.setUser_name(jsonObject1.getString("name"));
                                    userModelClass.setUser_image(jsonObject1.getString("user_image"));
                                    userModelClass.setUser_email(jsonObject1.getString("email"));
                                    userModelClass.setUser_mobile(jsonObject1.getString("mobile"));
                                    userModelClass.setUser_address(jsonObject1.getString("address"));
                                    userModelClass.setUser_type(jsonObject1.getString("user_type"));
                                    userModelClass.setUser_status(jsonObject1.getString("user_status"));
                                    userModelClass.setUser_cnic_front(jsonObject1.getString("cnic_front"));
                                    userModelClass.setUser_cnic_back(jsonObject1.getString("cnic_back"));
                                    userModelClass.setFirebase_id(jsonObject1.getString("firebase_id"));



                                    if (SharedPrefManager.getInstance(getContext()).addUserToPref(userModelClass)) {
                                        pDialog.dismiss();
                                        Intent intent = new Intent(getContext(), DriverMainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    } else {
                                        pDialog.dismiss();
                                        Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(getContext(), "Wait for Approvel from admin", Toast.LENGTH_SHORT).show();
                                }

                            }else{

                                userModelClass.setUser_id(jsonObject1.getString("id"));
                                userModelClass.setUser_name(jsonObject1.getString("name"));
                                userModelClass.setUser_image(jsonObject1.getString("user_image"));
                                userModelClass.setUser_email(jsonObject1.getString("email"));
                                userModelClass.setUser_mobile(jsonObject1.getString("mobile"));
                                userModelClass.setUser_address(jsonObject1.getString("address"));
                                userModelClass.setUser_type(jsonObject1.getString("user_type"));
                                userModelClass.setUser_status(jsonObject1.getString("user_status"));
                                userModelClass.setUser_cnic_front(jsonObject1.getString("cnic_front"));
                                userModelClass.setUser_cnic_back(jsonObject1.getString("cnic_back"));
                                userModelClass.setFirebase_id(jsonObject1.getString("firebase_id"));



                                if (SharedPrefManager.getInstance(getContext()).addUserToPref(userModelClass)) {
                                    pDialog.dismiss();
                                    Intent intent = new Intent(getContext(), UserMainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    pDialog.dismiss();
                                    Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                                }

                            }



                            Toast.makeText(getContext(), ""+jsonObject1.getString("mobile"), Toast.LENGTH_SHORT).show();

                           // Toast.makeText(getContext(), "Number Already Registered", Toast.LENGTH_SHORT).show();

                        } else {
                            pDialog.dismiss();
                            // here call the verification fragment
                            Toast.makeText(getContext(), ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                           // Log.d("Driver Registration","IMAGES"+driver_image+"<<===>>"+cnic_b+cninc_f);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                    Toast.makeText(getContext(), ""+e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getContext(), "Error Please tyr again"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile);
                params.put("password", password);
                return params;

            }

        };
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(stringRequest);


    }

    //Validating data
    private boolean validate() {
        boolean valid = true;
        mobile = "+92"+et_mobile.getText().toString().trim();
        password = et_password.getText().toString().trim();

        if (mobile.isEmpty()) {
            et_mobile.setError("Please Enter Phone");
            valid = false;
        } else {
            et_mobile.setError(null);
        }

        if (password.isEmpty()) {
            et_password.setError("Please Enter Correct Password");
            valid = false;
        } else {
            et_password.setError(null);
        }

        return valid;
    }
}
