package com.fyp.securepickanddrop.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.android.volley.toolbox.Volley;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.google.firebase.auth.PhoneAuthProvider;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationFragment extends Fragment implements View.OnClickListener{
    View view;
    ImageView dr_image;
    Button btn_registration;
    ImageButton img_cnic_front,img_cnic_back;
    EditText et_dr_name,et_dr_email,et_dr_mobile,et_dr_address,et_dr_password,et_confirm_password;
    private int GALLERY = 1, CAMERA = 2;
    String user_type="",driver_image="",cninc_f="",back_link="R",cnic_b="",d_name,d_email,d_mobile,d_address,d_password,d_confirm_password;
    private int clickImage;
    String IsUserExist="is-user-exist";
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.driver_registration_screen,container,false);
       initializing();
        return view;
    }

    private void initializing() {
        if (getArguments()!=null){
            user_type=getArguments().getString("user_type");
        }else {
            Toast.makeText(getContext(), "some info is missing", Toast.LENGTH_SHORT).show();
        }
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        dr_image=view.findViewById(R.id.profileImage);
        btn_registration=view.findViewById(R.id.registration_btn);
        img_cnic_front=view.findViewById(R.id.cnicFrontSideButton);
        img_cnic_back=view.findViewById(R.id.cnicBackSideButton);
        et_dr_name=view.findViewById(R.id.firstName);
        et_dr_email=view.findViewById(R.id.emailAddress);
        et_dr_mobile=view.findViewById(R.id.phoneNo);
        et_dr_address=view.findViewById(R.id.address);
        et_dr_password=view.findViewById(R.id.password);
        et_confirm_password=view.findViewById(R.id.confirmPassword);
        requestMultiplePermissions();
        dr_image.setOnClickListener(this);
        img_cnic_front.setOnClickListener(this);
        img_cnic_back.setOnClickListener(this);
        btn_registration.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.registration_btn:

                    // here call IsUserExist method
                    if (validate()){
                        IsUserExist(d_mobile);
                    }else {
                        Toast.makeText(getContext(), "Some info missing", Toast.LENGTH_SHORT).show();
                    }


                /*    VerifyPhoneFragment fragment=new VerifyPhoneFragment();
                    Bundle args = new Bundle();
                    args.putString("user_type", user_type);
                    args.putString("name", d_name);
                    args.putString("email", d_email);
                    args.putString("address", d_address);
                    args.putString("mobile", d_mobile);
                    args.putString("password", d_password);
                    args.putString("image", driver_image);
                    args.putString("cnic_f", cninc_f);
                    args.putString("cnic_b", cnic_b);
                    args.putString("link", back_link);
                    fragment.setArguments(args);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container_login, fragment);
                    fragmentTransaction.commit();
                    Log.d("Driver Registration","IMAGES"+driver_image+"<<===>>"+cnic_b+cninc_f);*/
                    

                break;
            case R.id.profileImage:
                clickImage=1;
                showPictureDialog();

                break;
            case R.id.cnicBackSideButton:
                clickImage=3;
                showPictureDialog();
                break;
            case R.id.cnicFrontSideButton:
                clickImage=2;
                showPictureDialog();
                break;
        }
    }



    //Validating data
    private boolean validate() {
        boolean valid = true;
        d_name = et_dr_name.getText().toString();
        d_email = et_dr_email.getText().toString();
        d_mobile ="+92" + et_dr_mobile.getText().toString();
        d_address = et_dr_address.getText().toString();
        d_password = et_dr_password.getText().toString();
        d_confirm_password = et_confirm_password.getText().toString();


        Log.d("IMG", "THIS is image" + d_email);

        if (d_name.isEmpty()) {
            et_dr_name.setError("Please enter your name");
            valid = false;
        } else {
            et_dr_name.setError(null);
        }
        if (d_email.isEmpty()) {
            et_dr_email.setError("Please enter email");
            valid = false;
        } else {
            et_dr_email.setError(null);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(d_email).matches()) {
            et_dr_email.setError("Email formate is wrong");
            valid = false;
        } else {
            et_dr_email.setError(null);
        }

        if (!d_email.matches(".+@gmail.com")) {
            et_dr_email.setError("only gmail.com allows");
            valid = false;
        } else {
            et_dr_email.setError(null);
        }

        if (d_mobile.isEmpty()) {
            et_dr_mobile.setError("Please enter mobile");
            valid = false;
        } else {
            et_dr_mobile.setError(null);
        }
        if (d_address.isEmpty()) {
            et_dr_address.setError("Please enter address");
            valid = false;
        } else {
            et_dr_address.setError(null);
        }
        if (d_password.isEmpty() || d_confirm_password.isEmpty() || !d_confirm_password.equals(d_password)) {
            et_dr_password.setError("Password don't Match");
            et_confirm_password.setError("Password don't Match");
            valid = false;
        } else {
            et_dr_password.setError(null);
            et_confirm_password.setError(null);
        }

        if (!isValidPassword(d_password)) {
            et_dr_password.setError("Password must contain 8 character/numbers and special symbol");
            et_confirm_password.setError("Password must contain 8 character/numbers and special symbol");
            valid = false;
            Log.d("SignUp","validation");

        } else {
            et_dr_password.setError(null);
            et_confirm_password.setError(null);
            Log.d("SignUp","Pass validation");
        }

        if (driver_image.isEmpty()) {
            Toast.makeText(getContext(), "Please select profile image", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (cninc_f.isEmpty()) {
            Toast.makeText(getContext(), "Upload CNIC Front side", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (cnic_b.isEmpty()) {
            Toast.makeText(getContext(), "Upload CNIC Back side", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (user_type.isEmpty()) {
            Toast.makeText(getContext(), "Reload the screen please", Toast.LENGTH_SHORT).show();
            valid = false;
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

    private void IsUserExist(final String mobilenumber) {
        Log.e("check1122", "mobile number" + mobilenumber);
        pDialog.setMessage("Registring ...");
        pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, ConstantValues.mainurl+IsUserExist, new Response.Listener<String>() {
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
                            Toast.makeText(getContext(), "Number Already Registered", Toast.LENGTH_SHORT).show();

                        } else {
                            pDialog.dismiss();
                            // here call the verification fragment
                            VerifyPhoneFragment fragment=new VerifyPhoneFragment();
                            Bundle args = new Bundle();
                            args.putString("user_type", user_type);
                            args.putString("name", d_name);
                            args.putString("email", d_email);
                            args.putString("address", d_address);
                            args.putString("mobile", d_mobile);
                            args.putString("password", d_password);
                            args.putString("image", driver_image);
                            args.putString("cnic_f", cninc_f);
                            args.putString("cnic_b", cnic_b);
                            args.putString("link", back_link);
                            fragment.setArguments(args);
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container_login, fragment);
                            fragmentTransaction.commit();
                            Log.d("Driver Registration","IMAGES"+driver_image+"<<===>>"+cnic_b+cninc_f);

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
                params.put("mobile", mobilenumber);
                return params;

            }

        };
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(stringRequest);
        //VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                openGalleryImage();
                                break;
                            case 1:
                                openCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void openGalleryImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
    }

    public void openCamera() {
        try {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, CAMERA);
            }
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Your device doesn't support capturing images!";
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (clickImage) {
            case 1:
                if (requestCode == GALLERY) {
                    if (data != null) {
                        Uri contentURI = data.getData();
                        try {

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                            dr_image.setImageBitmap(bitmap);
                            driver_image=getStringImage(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else if (requestCode == CAMERA) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    dr_image.setImageBitmap(thumbnail);
                    driver_image=getStringImage(thumbnail);
                }

                break;
            case 2:
                if (requestCode == GALLERY) {
                    if (data != null) {
                        Uri contentURI = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                            img_cnic_front.setImageBitmap(bitmap);
                            cninc_f=getStringImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }

                } else if (requestCode == CAMERA) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    img_cnic_front.setImageBitmap(thumbnail);
                    cninc_f=getStringImage(thumbnail);

                }
                break;
            case 3:
                if (requestCode == GALLERY) {
                    if (data != null) {
                        Uri contentURI = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                            img_cnic_back.setImageBitmap(bitmap);
                            cnic_b=getStringImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }

                } else if (requestCode == CAMERA) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    img_cnic_back.setImageBitmap(thumbnail);
                    cnic_b=getStringImage(thumbnail);
                }
                break;

        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void requestMultiplePermissions() {
        Dexter.withContext(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                            Log.d("permissions are granted","done");
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Alert Dialog");
                            alertDialog.setMessage("Please Allow Permissions to use this App");
                            alertDialog.setCancelable(false);
                            alertDialog.setIcon(R.drawable.applogo);

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });

                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

}
