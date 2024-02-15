package com.fyp.securepickanddrop.adapterclasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.activities.DriverMainActivity;
import com.fyp.securepickanddrop.activities.MessageActivity;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.fyp.securepickanddrop.modelsclasses.PassengerModelClass;
import com.fyp.securepickanddrop.modelsclasses.RideRequestsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverRideRequestsAdapter extends RecyclerView.Adapter<DriverRideRequestsAdapter.ViewHolder> {
    Context context;
    List<RideRequestsModel> modelList;
    ArrayList<RideRequestsModel> arrayList;
    private ProgressDialog pDialog;
    String user_type = "", updateBookingurl = "change-request-status";

    public DriverRideRequestsAdapter(Context context, List<RideRequestsModel> itemList) {
        this.context = context;
        this.modelList = itemList;
        this.arrayList = new ArrayList<RideRequestsModel>();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_requests_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RideRequestsModel model = modelList.get(position);
        String request_status = model.getRequest_status();
        Toast.makeText(context, "" + request_status, Toast.LENGTH_SHORT).show();
        if (request_status.equals("0")) {

            holder.tv_name.setText(model.getUser_name());
            holder.tv_institute.setText(model.getInstitute_name());
            holder.tv_location.setText(model.getPickup_lat() + "  " + model.getPickup_lng());
            holder.tv_no_of_seats.setText(model.getNo_of_seats());
            Glide.with(context).load(model.getUser_image()).dontAnimate().fitCenter().placeholder(R.drawable.bussdriver).into(holder.img_user);
        }

        holder.btn_accept.setOnClickListener(view -> {
           // ShowConfirmationDialog("najam");
            UpdateBooking(model.getRequest_id(), "1", position);
            Toast.makeText(context, "" + model.getRequest_id(), Toast.LENGTH_SHORT).show();
        });
        holder.btn_reject.setOnClickListener(view -> {
            UpdateBooking(model.getRequest_id(), "2", position);
            Toast.makeText(context, "" + model.getRequest_id(), Toast.LENGTH_SHORT).show();
        });
        holder.btn_chat.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userid", model.getFirebase_id());
            context.startActivity(intent);
        });
    }

    private void ShowConfirmationDialog(String institute_nanme) {
        EditText editText = new EditText(context);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Enter the Price");
        alertDialog.setView(editText);
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.applogo);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String editTextInput = editText.getText().toString();
                Toast.makeText(context, ""+editTextInput, Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void UpdateBooking(String request_id, String status, int position) {
        pDialog.setMessage("Please Wait....");
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl + updateBookingurl, new Response.Listener<String>() {
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
                            Toast.makeText(context, "status updated", Toast.LENGTH_SHORT).show();
                            modelList.remove(position);
                            notifyDataSetChanged();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.dismiss();
                } catch (JSONException e) {
                    pDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(context, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(context, "Some Error", Toast.LENGTH_SHORT).show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("request_id", request_id);
                params.put("status", status);

                return params;

            }
        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_institute, tv_location, tv_no_of_seats;
        ImageView img_user;
        Button btn_accept, btn_reject, btn_chat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.fullName);
            tv_institute = itemView.findViewById(R.id.request_institute);
            tv_location = itemView.findViewById(R.id.request_location);
            tv_no_of_seats = itemView.findViewById(R.id.requested_seats);
            btn_accept = itemView.findViewById(R.id.acceptButton);
            btn_reject = itemView.findViewById(R.id.declineButton);
            btn_chat = itemView.findViewById(R.id.chat_btn);
            img_user = itemView.findViewById(R.id.profile_image);
        }
    }
}
