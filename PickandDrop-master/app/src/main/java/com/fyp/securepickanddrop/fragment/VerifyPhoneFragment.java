package com.fyp.securepickanddrop.fragment;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chaos.view.PinView;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneFragment extends Fragment {
    View view;
    PinView pinView;
    Button btn_verify;
    TextView resend_tv;
    FirebaseAuth auth;
    DatabaseReference reference;
    String verificationCode;
    String user_type = "",firebaseUserId="", driver_image = "", link = "", cninc_f = "", register="register",cnic_b = "", d_name, d_email, d_mobile, d_address, d_password;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.verify_phone_screen, container, false);
        initializing();
        return view;
    }

    private void initializing() {
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        btn_verify = view.findViewById(R.id.verifyButton);
        resend_tv = view.findViewById(R.id.resendCode);
        pinView = view.findViewById(R.id.pinView);
        FirebaseApp.initializeApp(getContext());
        auth = FirebaseAuth.getInstance();


        if (getArguments() != null) {
            link = getArguments().getString("link");
            if (link.equals("R")) {
                user_type = getArguments().getString("user_type");
                d_mobile = getArguments().getString("mobile");
                d_name = getArguments().getString("name");
                d_email = getArguments().getString("email");
                d_address = getArguments().getString("address");
                d_password = getArguments().getString("password");
                driver_image = getArguments().getString("image");
                cninc_f = getArguments().getString("cnic_f");
                cnic_b = getArguments().getString("cnic_b");
            } else if (link.equals("F")) {
                d_mobile = getArguments().getString("mobile");
            }


            Log.d("VerifyCode", "Buyer Data  " + d_mobile+user_type+d_name+d_email+d_address+d_password+driver_image+cnic_b+cninc_f);


        }
        pDialog.setMessage("Loading ...");
        pDialog.show();
        sendVerificationCodeToUser();

        resend_tv.setOnClickListener(view1 -> {
            pDialog.setMessage("Loading ...");
            pDialog.show();

            sendVerificationCodeToUser();
        });
        btn_verify.setOnClickListener(view1 -> {
            String code = pinView.getText().toString();
            if (!code.isEmpty()) {
                verifyCode(code);
            } else {
                Toast.makeText(getContext(), "Please enter code", Toast.LENGTH_SHORT).show();
            }

        });

    }


    private void sendVerificationCodeToUser() {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(d_mobile)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        pDialog.dismiss();
                        Toast.makeText(requireActivity(), "Code Sent", Toast.LENGTH_SHORT).show();

                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    public void verifyCode(String userEnterVerificationCode) {
        if (userEnterVerificationCode.length() == 6) {
            if (verificationCode != null) {


                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode, userEnterVerificationCode);
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(task -> {


                            if (task.isSuccessful()) {


                                register(d_name,d_email,d_password);
                               /* if (link.equals("R")) {
                                    // call registration method
                                    RegistrationMethod(driver_image,d_name,d_mobile,d_email,d_address,d_password,cninc_f,cnic_b,user_type);


                                } else if (link.equals("F")) {
                                    //   move to password change fragment
                                    ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
                                    Bundle args = new Bundle();
                                    args.putString("mobile", d_mobile);
                                    resetPasswordFragment.setArguments(args);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, resetPasswordFragment).addToBackStack("added").commit();

                                }*/


                                Toast.makeText(getContext(), "Verify thanks", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(requireContext(), "Please Enter Correct Code.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(requireContext(), "Please check internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Please Enter Correct Code.", Toast.LENGTH_SHORT).show();
        }
    }



    private void register(final String username, String email, String password){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            firebaseUserId = firebaseUser.getUid();
                            Log.d("RegistrationActivity","User ID"+firebaseUserId+driver_image);
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", firebaseUserId);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");

                            hashMap.put("search", username.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        if (link.equals("R")) {
                                            // call registration method
                                            RegistrationMethod(driver_image,d_name,d_mobile,d_email,d_address,d_password,cninc_f,cnic_b,user_type,firebaseUserId);


                                        } else if (link.equals("F")) {
                                            //   move to password change fragment
                                            ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
                                            Bundle args = new Bundle();
                                            args.putString("mobile", d_mobile);
                                            resetPasswordFragment.setArguments(args);
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, resetPasswordFragment).addToBackStack("added").commit();

                                        }
                                        Toast.makeText(getContext(), "Add Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void RegistrationMethod(String driverImage, String d_name, String d_mobile, String d_email, String d_address, String d_password, String cninc_f, String cnic_b, String user_type,String firebaseUserId) {
        pDialog.setMessage("Registring User....");
        pDialog.show();

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, ConstantValues.mainurl+register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                Log.d("VerifyActivity","buyer method call"+response.toString());
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        if (jsonObject.getString("status").equals("true")) {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                            // Goto Login Page
                            LoginFragment loginFragment = new LoginFragment();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fragmentTransaction.replace(R.id.fragment_container_login, loginFragment);
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), " Sorry try Again", Toast.LENGTH_SHORT).show();
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
                Log.d("Response error","Volley response errror is"+error.getMessage());
                Toast.makeText(getActivity(), "Please ty again", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_image", driverImage);
                params.put("name", d_name);
                params.put("email", d_email);
                params.put("address", d_address);
                params.put("password", d_password);
                params.put("cnic_front", cnic_b);
                params.put("cnic_back", cninc_f);
                params.put("user_type", user_type);
                params.put("mobile", d_mobile);
                params.put("firebase_id", firebaseUserId);

                return params;

            }
        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest2);
    }

}
