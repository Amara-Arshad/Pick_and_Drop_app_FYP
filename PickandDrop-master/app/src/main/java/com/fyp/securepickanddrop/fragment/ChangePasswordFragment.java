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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.activities.RegistrationLoginActivity;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordFragment extends Fragment {
    View view;
    EditText oldPass,newPass,confirmPass;
    Button updatebtn;
    String upDatePass_url="change-password";
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.change_password_screen,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        pDialog=new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        oldPass=view.findViewById(R.id.CurrentPass);
        newPass=view.findViewById(R.id.NewPass);
        confirmPass=view.findViewById(R.id.ConfirmPass);
        updatebtn=view.findViewById(R.id.btnUpdate);
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final UserModelClass userModelClass= SharedPrefManager.getInstance(getContext()).getUser();
                if (validate() && userModelClass!=null){
                    UpDatePassword(userModelClass.getUser_mobile(),oldPass.getText().toString(),newPass.getText().toString());
                }

            }
        });
    }

    //Validating data
    private boolean validate() {
        boolean valid = true;

        if (oldPass.getText().toString().isEmpty()){
            oldPass.setError("Enter old password");
            valid=false;
        }else {
            oldPass.setError(null);
        }
        if (!isValidPassword(newPass.getText().toString())) {
            confirmPass.setError("Password must contain 8 character/numbers and special symbol");
            valid = false;
            Log.d("SignUp","validation");
        } else {
            confirmPass.setError(null);
            Log.d("SignUp","Pass validation");
        }
        if (newPass.getText().toString().isEmpty() || confirmPass.getText().toString().isEmpty() || !newPass.getText().toString().equals(confirmPass.getText().toString())) {
            newPass.setError("Password don't Match");
            valid = false;
        } else {
            newPass.setError(null);
        }
        return valid;
    }

    //*****************************************************************
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[A-Za-z\\d][A-Za-z\\d!@#$%^&*()_+]{7,19}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    private void UpDatePassword(String user_mobile, String password, String newpassword) {
        pDialog.setMessage("Updating Wait.....");
        pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, ConstantValues.mainurl+upDatePass_url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        if (jsonObject.getString("status").equals("true")) {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                            SharedPrefManager.getInstance(getContext()).logOut();
                            startActivity(new Intent(getContext(), RegistrationLoginActivity.class));
                            getActivity().finish();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Error Please tyr again", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", user_mobile);
                params.put("password", password);
                params.put("new_password", newpassword);
                return params;

            }
        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Change Password");
        super.onViewCreated(view, savedInstanceState);
    }
}
