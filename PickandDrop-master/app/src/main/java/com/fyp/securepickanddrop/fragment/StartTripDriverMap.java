package com.fyp.securepickanddrop.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fyp.securepickanddrop.BuildConfig;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.adapterclasses.MyIntitutesAdapter;
import com.fyp.securepickanddrop.adapterclasses.MyPassengersAdapter;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.fyp.securepickanddrop.modelsclasses.InstituteModelClass;
import com.fyp.securepickanddrop.modelsclasses.RideRequestsModel;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

public class StartTripDriverMap extends Fragment {
    View view;
    Button btn_end_trip;
    private ProgressDialog pDialog;
    GoogleMap map;
    SupportMapFragment mapFragment;
    String lat = "", lng = "", user_id = "", getPassengerUrl = "driver-requests",endtrip = "end-trip", getInstitutesUrl = "get-driver-institutes", set_live_location = "set-live-location";
    FusedLocationProviderClient client;
    boolean AnimationStatus = false;
    boolean StatusList = true;
    Location myLocation = null;
    Location myUpdatedLocation = null;
    static Marker carMarker;
    Bitmap BitMapMarker;
    Bitmap BitMapMarkerInstitute;
    Bitmap BitMapMarkerStudent;
    float Bearing = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.start_trip_p_driver, container, false);
        initializing();
        return view;
    }

    private void initializing() {
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        UserModelClass userModelClass = SharedPrefManager.getInstance(getContext()).getUser();
        if (userModelClass != null) {
            user_id = userModelClass.getUser_id();
        }
        btn_end_trip = view.findViewById(R.id.btn_end_trip);
        btn_end_trip.setOnClickListener(view1 -> {
            EndTripMethod(user_id);
        });
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car_marker);
        BitmapDrawable bitmapdrawinstitue = (BitmapDrawable) getResources().getDrawable(R.drawable.university);
        BitmapDrawable bitmapdrawstudent = (BitmapDrawable) getResources().getDrawable(R.drawable.locationstudent);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap b2 = bitmapdrawinstitue.getBitmap();
        Bitmap b3 = bitmapdrawstudent.getBitmap();
        BitMapMarker = Bitmap.createScaledBitmap(b, 110, 60, false);
        BitMapMarkerInstitute = Bitmap.createScaledBitmap(b2, 50, 60, false);
        BitMapMarkerStudent = Bitmap.createScaledBitmap(b3, 50, 60, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view);

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            GetCurrentLocation();
        } else if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            requestMultiplePermissions();
        } else {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 20);
        }


    }

    private void GetInstituteList(String user_id) {
        pDialog.setMessage("please Wait....");
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl + getInstitutesUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response is", response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Log.d("status", "CHECK" + jsonObject.getString("status"));
                        if (jsonObject.getString("status").equals("true")) {

                            JSONArray jsonArray1 = new JSONArray(jsonObject.getString("institutes"));
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject jsonObject1 = (JSONObject) jsonArray1.get(j);


                                LatLng latLng = new LatLng(Double.parseDouble(jsonObject1.getString("lat")), Double.parseDouble(jsonObject1.getString("long")));

                                MarkerOptions markerOptions1 = new MarkerOptions().position(latLng).title(jsonObject1.getString("name")).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarkerInstitute));
                                map.addMarker(markerOptions1);


                            }

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "No Institute", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();

                } catch (JSONException e) {
                    pDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(getContext(), "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                StatusList = false;

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
                params.put("driver_id", user_id);
                return params;

            }
        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }

    private void EndTripMethod(String user_id) {
        pDialog.setMessage("Please Wait....");
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl + endtrip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response is", response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Log.d("status", "CHECK" + jsonObject.getString("status"));
                        if (jsonObject.getString("status").equals("true")) {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "Your location Offline", Toast.LENGTH_SHORT).show();

                            getActivity().onBackPressed();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();
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
                params.put("driver_id", user_id);


                return params;

            }
        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
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
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map = googleMap;

                            getMyLocation();

                        }
                    });

                }
            }
        });

    }


    //to get user location
    private void getMyLocation() {
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
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                if (AnimationStatus) {
                    myUpdatedLocation = location;
                } else {
                    if (carMarker != null) {
                        carMarker.remove();
                    }
                    myLocation = location;
                    myUpdatedLocation = location;
                    LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    carMarker = map.addMarker(new MarkerOptions().position(latlng).
                            flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarker)));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            latlng, 17f);
                    if (StatusList) {
                        map.animateCamera(cameraUpdate);
                        GetInstituteList(user_id);
                        MyPassengers(user_id);
                    }

                }
                Bearing = location.getBearing();
                LatLng updatedLatLng = new LatLng(myUpdatedLocation.getLatitude(), myUpdatedLocation.getLongitude());
                changePositionSmoothly(carMarker, updatedLatLng, Bearing);
                lat = String.valueOf(myUpdatedLocation.getLatitude());
                lng = String.valueOf(myUpdatedLocation.getLongitude());
                if (!lat.isEmpty() && !lng.isEmpty()) {
                    SetLiveLocation(user_id, lat, lng);
                }
            }
        });
        //
    }


    private void MyPassengers(String user_id) {
        pDialog.setMessage("please Wait....");
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl+getPassengerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response is", response.toString());
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

                                    LatLng latLng = new LatLng(Double.parseDouble(jsonObject1.getString("lat")), Double.parseDouble(jsonObject1.getString("long")));

                                    MarkerOptions markerOptions1 = new MarkerOptions().position(latLng).title(jsonObject1.getJSONObject("user").getString("name")).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarkerStudent));
                                    map.addMarker(markerOptions1);

                                 /*   model.setPickup_lng(jsonObject1.getString("long"));
                                    model.setPickup_lat(jsonObject1.getString("lat"));
                                    model.setUser_name(jsonObject1.getJSONObject("user").getString("name"));*/


                                }

                            }

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(getContext(), "No Institute", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();

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
                params.put("driver_id", user_id);
                return params;
            }

        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }


    private void SetLiveLocation(String user_id, String altitude, String longitude) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl + set_live_location, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response is", response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Log.d("status", "CHECK" + jsonObject.getString("status"));
                        if (jsonObject.getString("status").equals("true")) {
                            Log.d("status", "Updated");
                            // Toast.makeText(getContext(), "updated", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(getContext(), "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), "Some Error", Toast.LENGTH_SHORT).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_id", user_id);
                params.put("lat", altitude);
                params.put("long", longitude);
                return params;

            }
        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }


    void changePositionSmoothly(final Marker myMarker, final LatLng newLatLng, final Float bearing) {

        final LatLng startPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        final LatLng finalPosition = newLatLng;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 5000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                myMarker.setRotation(bearing);
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                        startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                myMarker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
                myLocation.setLatitude(newLatLng.latitude);
                myLocation.setLongitude(newLatLng.longitude);
            }
        });
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("On Rout");
        super.onViewCreated(view, savedInstanceState);
    }
}
