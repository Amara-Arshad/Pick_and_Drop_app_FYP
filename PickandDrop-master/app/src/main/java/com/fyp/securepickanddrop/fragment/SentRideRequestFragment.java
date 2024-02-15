package com.fyp.securepickanddrop.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.securepickanddrop.BuildConfig;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentRideRequestFragment extends Fragment {
    View view;
    GoogleMap map;
    SupportMapFragment mapFragment;
    EditText number_of_seats;
    TextView dr_name,institute_name;
    Button btn_sent_request;
    String institute_nanme, user_lat="",user_lng="",user_id="",driver_id="",driver_name="",institute_id="",no_of_seats="",sentrequesturl="request-driver";
    FusedLocationProviderClient client;
    private ProgressDialog pDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.sent_ride_request,container,false);
        initializing();
        return view;
    }

    private void initializing() {
        if (getArguments()!=null){
            institute_nanme=getArguments().getString("institute_name");
            institute_id=getArguments().getString("institute_id");
            driver_id=getArguments().getString("driver_id");
            driver_name=getArguments().getString("name");
        }
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        UserModelClass userModelClass= SharedPrefManager.getInstance(getContext()).getUser();
        if (userModelClass!=null){
            user_id=userModelClass.getUser_id();
        }
        number_of_seats = view.findViewById(R.id.et_noofseats);
        dr_name = view.findViewById(R.id.driver_name);
        institute_name = view.findViewById(R.id.institute_name);
        number_of_seats = view.findViewById(R.id.et_noofseats);
        btn_sent_request = view.findViewById(R.id.btn_sent_request);
        btn_sent_request.setOnClickListener(view1 -> {
            no_of_seats=number_of_seats.getText().toString();
            if (!no_of_seats.isEmpty()){
                ShowConfirmationDialog(no_of_seats);
            }else {
                Toast.makeText(getContext(), "Please enter seats", Toast.LENGTH_SHORT).show();
            }

        });

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view);

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            GetCurrentLocation();
        } else if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            requestMultiplePermissions();
        } else {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 20);
        }

        dr_name.setText(driver_name);
        institute_name.setText(institute_nanme);

    }



    private void ShowConfirmationDialog(String institute_nanme) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure to sent Request"+"\n"+institute_nanme +"\n"+"Seats"+no_of_seats);
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.applogo);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                SentRequestTodriver(user_id,institute_id,user_lat,user_lng,driver_id,no_of_seats);
                Log.d("Request Data","====>"+user_lat+user_lng+driver_id+user_id+no_of_seats+institute_id);

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Change name", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void SentRequestTodriver(String user_id, String institute_id, String user_lat, String user_lng, String driver_id, String no_of_seats) {
        Log.e("check1122", "mobile number" + institute_nanme);
        pDialog.setMessage("Registring ...");
        pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, ConstantValues.mainurl+sentrequesturl, new Response.Listener<String>() {
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
                            Toast.makeText(getContext(), "Request has been sent to driver", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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
                params.put("user_id", user_id);
                params.put("lat", user_lat);
                params.put("long", user_lng);
                params.put("institute_id", institute_id);
                params.put("driver_id", driver_id);
                params.put("no_of_seats", no_of_seats);
                return params;

            }

        };
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(stringRequest);
    }


    private void requestMultiplePermissions() {
        Dexter.withContext(getActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                            Log.d("permissions are granted", "done");
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

    private void GetCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {

                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions markerOptions1 = new MarkerOptions().position(latLng).title("I am there");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(markerOptions1);
                            user_lat= String.valueOf(latLng.latitude);
                            user_lng= String.valueOf(latLng.longitude);
                            googleMap.setMyLocationEnabled(true);

                            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(@NonNull LatLng latLng) {
                                    MarkerOptions markerOptions= new MarkerOptions();

                                    markerOptions.position(latLng);
                                    markerOptions.title(latLng.latitude+"  "+latLng.longitude);
                                    googleMap.clear();
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                                    googleMap.addMarker(markerOptions);
                                    user_lat= String.valueOf(latLng.latitude);
                                    user_lng= String.valueOf(latLng.longitude);

                                    Toast.makeText(getContext(), ""+user_lat+"===="+user_lng, Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });

                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode ==20){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(), "Thanks", Toast.LENGTH_SHORT).show();
                GetCurrentLocation();
            }else {
                Toast.makeText(getContext(), "Sorry", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Sent request");
        super.onViewCreated(view, savedInstanceState);
    }
}
