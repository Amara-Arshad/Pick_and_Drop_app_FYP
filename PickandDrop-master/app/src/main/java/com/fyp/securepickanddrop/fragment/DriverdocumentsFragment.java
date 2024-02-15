package com.fyp.securepickanddrop.fragment;

import android.Manifest;
import android.app.FragmentManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.activities.DriverMainActivity;
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

public class DriverdocumentsFragment extends Fragment {
    View view;
    EditText vehcile_no,vehcile_type,no_of_seats;
    ImageButton license_img,policy_certificate;
    Button btn_submit_doc;
    private int clickImage;
    String vehcile_t="",vehcile_n="",no_ofseats="",user_id="",license_image="",certificate="",urlsubmitdoc="submit-driver-documents";
    private int GALLERY = 1, CAMERA = 2;
    private ProgressDialog pDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.driver_document_screen,container,false);
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
        vehcile_no=view.findViewById(R.id.vehicle_no);
        vehcile_type=view.findViewById(R.id.vehcle_type);
        no_of_seats=view.findViewById(R.id.vehicle_seats);
        license_img=view.findViewById(R.id.license_front);
        policy_certificate=view.findViewById(R.id.certificate);
        btn_submit_doc=view.findViewById(R.id.Submit_doc);
        requestMultiplePermissions();
        license_img.setOnClickListener(view1 -> {
            clickImage=1;
            showPictureDialog();
        });
        policy_certificate.setOnClickListener(view1 -> {
            clickImage=2;
            showPictureDialog();
        });
        btn_submit_doc.setOnClickListener(view1 -> {
            if (validate()){
                Submitdocuments(user_id,vehcile_n,vehcile_t,license_image,certificate,no_ofseats);
            }

        });
    }

    private void Submitdocuments(String user_id,String vehcile_n, String vehcile_t, String license_image, String certificate,String no_ofseats) {
        pDialog.setMessage("Loading....");
        pDialog.show();

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, ConstantValues.mainurl+urlsubmitdoc, new Response.Listener<String>() {
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
                            Toast.makeText(getContext(), "Submit Successfully", Toast.LENGTH_SHORT).show();

                            SharedPrefManager.getInstance(getContext()).logOut();
                            startActivity(new Intent(getContext(), RegistrationLoginActivity.class));
                            getActivity().finish();

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
                params.put("vehicle_type", vehcile_n);
                params.put("vehicle_num", vehcile_t);
                params.put("licence_image", license_image);
                params.put("certificate_image", certificate);
                params.put("user_id", user_id);
                params.put("no_of_seats", no_ofseats);

                return params;

            }
        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest2);
    }


    //Validating data
    private boolean validate() {
        boolean valid = true;
        vehcile_n = vehcile_no.getText().toString();
        vehcile_t = vehcile_type.getText().toString();
        no_ofseats = no_of_seats.getText().toString();


        Log.d("IMG", "THIS is image" + vehcile_no);

        if (vehcile_n.isEmpty()) {
            vehcile_no.setError("Please enter vehcile number");
            valid = false;
        } else {
            vehcile_no.setError(null);
        }
        if (vehcile_t.isEmpty()) {
            vehcile_type.setError("Please enter vehcile type buss or car?");
            valid = false;
        } else {
            vehcile_type.setError(null);
        }
        if (no_ofseats.isEmpty()) {
            no_of_seats.setError("Please enter vehcile seats");
            valid = false;
        } else {
            no_of_seats.setError(null);
        }
        if (license_image.isEmpty()) {
            Toast.makeText(getContext(), "Please upload license image", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (certificate.isEmpty()) {
            Toast.makeText(getContext(), "Please upload policy certificate image", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
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
                            license_img.setImageBitmap(bitmap);
                            license_image=getStringImage(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else if (requestCode == CAMERA) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    license_img.setImageBitmap(thumbnail);
                    license_image=getStringImage(thumbnail);
                }

                break;
            case 2:
                if (requestCode == GALLERY) {
                    if (data != null) {
                        Uri contentURI = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                            policy_certificate.setImageBitmap(bitmap);
                            certificate=getStringImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }

                } else if (requestCode == CAMERA) {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    policy_certificate.setImageBitmap(thumbnail);
                    certificate=getStringImage(thumbnail);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Please submit your Info");
        super.onViewCreated(view, savedInstanceState);
    }
}
