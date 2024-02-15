package com.fyp.securepickanddrop.fragment;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.activities.RegistrationLoginActivity;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;
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

public class EditUserProfileFragment extends Fragment implements View.OnClickListener {
    View view;
    EditText edit_name, edit_mobile, edit_addres, edit_email;
    ImageView edit_photo;
    TextView edit_imageupload;
    Button edit_info_btn;
    private int clickImage;
    private int GALLERY = 1, CAMERA = 2;
    private ProgressDialog pDialog;
    Bitmap bitmap;
    String edit_user_name = "",updateProfile="update-profile", UserId = "", edit_user_email = "", edit_user_address = "", edit_user_mobile = "", edit_user_photo = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_profile_screen, container, false);
        initializing();
        return view;
    }

    private void initializing() {
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        requestMultiplePermissions();
        edit_addres = view.findViewById(R.id.user_address_edit);
        edit_name = view.findViewById(R.id.user_name_edit);
        edit_mobile = view.findViewById(R.id.user_mobile_number_edit);
        edit_email = view.findViewById(R.id.user_email_edit);
        edit_photo = view.findViewById(R.id.iv_profile_photo_edit);
        edit_imageupload = view.findViewById(R.id.tv_UploadProfilePhoto_edit);
        edit_info_btn = view.findViewById(R.id.edit_btn_user);
        edit_info_btn.setOnClickListener(this);
        edit_imageupload.setOnClickListener(this);
        edit_photo.setOnClickListener(this);
        BindData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_btn_user:
                if (validate()) {
                    UserUpdate(UserId, edit_user_name, edit_user_email, edit_user_mobile, edit_user_address, edit_user_photo);
                }
                break;
            case R.id.tv_UploadProfilePhoto_edit:
            case R.id.iv_profile_photo_edit:
                clickImage=1;
                showPictureDialog();
                break;
        }
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

                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                            edit_photo.setImageBitmap(bitmap);
                            //edit_user_photo=getStringImage(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else if (requestCode == CAMERA) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    edit_photo.setImageBitmap(bitmap);
                    //edit_user_photo=getStringImage(bitmap);
                }

                break;


        }
    }


    private void BindData() {
        UserModelClass userModelClass = SharedPrefManager.getInstance(getContext()).getUser();
        if (userModelClass != null) {
            String number = userModelClass.getUser_mobile();
            number = number.substring(3);
            edit_name.setText(userModelClass.getUser_name());
            edit_email.setText(userModelClass.getUser_email());
            edit_mobile.setText(number);
            edit_addres.setText(userModelClass.getUser_address());
            Glide.with(getContext()).load(userModelClass.getUser_image()).dontAnimate().fitCenter().placeholder(R.drawable.applogo).into(edit_photo);
        }
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Edit Profile");
        super.onViewCreated(view, savedInstanceState);
    }

    private void UserUpdate(final String userId, final String edit_user_name, final String edit_user_email, final String edit_user_mobile, final String edit_user_address, final String edit_user_photo) {
        pDialog.setMessage("Updating....");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl+updateProfile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.hide();
                Log.d("EditCompany Fragment","editCompany method call"+response.toString());
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        if (jsonObject.getString("status").equals("true")) {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "Info Updated Successfully", Toast.LENGTH_SHORT).show();
                            SharedPrefManager.getInstance(getContext()).logOut();
                            // Goto Login Page
                            Intent intent=new Intent(getContext(), RegistrationLoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), " Sorry try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                    Toast.makeText(getContext(), "edit error catch"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.d("Edit company","Volley response errror is"+error.getMessage());
                Toast.makeText(getActivity(), "Error Please tyr again "+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", edit_user_name);
                params.put("mobile", edit_user_mobile);
                params.put("email", edit_user_email);
                params.put("address", edit_user_address);
                params.put("user_id", userId);
                params.put("user_image", edit_user_photo);
                return params;
            }
        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }


    //Validating data
    private boolean validate() {
        boolean valid = true;
        edit_user_name = edit_name.getText().toString();
        edit_user_email = edit_email.getText().toString();
        edit_user_mobile = "+92"+edit_mobile.getText().toString();
        edit_user_address = edit_addres.getText().toString();
        UserModelClass userModelClass = SharedPrefManager.getInstance(getContext()).getUser();
        UserId=userModelClass.getUser_id();
        if (bitmap!=null){
            edit_user_photo = getStringImage(bitmap);
        }else {
            edit_user_photo=userModelClass.getUser_image();
        }

        Log.d("IMG", "THIS is image" + edit_user_photo);

        if (edit_user_name.isEmpty()){
            edit_name.setError("Plaese enter name");
            valid=false;
        }else {
            edit_name.setError(null);
        }
        if (UserId.isEmpty()){
            Toast.makeText(getContext(), "Error reload the activity", Toast.LENGTH_SHORT).show();
            valid=false;
        }
        if (edit_user_email.isEmpty()) {
            edit_email.setError("Pleaase enter email");
            valid = false;
        } else {
            edit_email.setError(null);
        }
        if (edit_user_mobile.isEmpty()) {
            edit_mobile.setError("Please enter mobile");
            valid = false;
        } else {
            edit_mobile.setError(null);
        }
        if (edit_user_address.isEmpty()) {
            edit_addres.setError("Please enter address");
            valid = false;
        } else {
            edit_addres.setError(null);
        }
        if (edit_user_photo.isEmpty()) {
            Toast.makeText(getContext(), "Please select a photo", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}

