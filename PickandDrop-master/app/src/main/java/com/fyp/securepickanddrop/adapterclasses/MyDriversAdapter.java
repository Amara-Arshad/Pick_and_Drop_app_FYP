package com.fyp.securepickanddrop.adapterclasses;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.activities.MessageActivity;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.fyp.securepickanddrop.fragment.StartDriverTracking;
import com.fyp.securepickanddrop.modelsclasses.RideRequestsModel;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDriversAdapter extends RecyclerView.Adapter<MyDriversAdapter.ViewHolder> {
    Context context;
    List<RideRequestsModel> modelList;
    ArrayList<RideRequestsModel> arrayList;
    Dialog rankDialog;
    RatingBar ratingBar;
    private ProgressDialog pDialog;
    String add_rating="add-rating",customer_id="";
    public MyDriversAdapter(Context context, List<RideRequestsModel> itemList) {
        UserModelClass userModelClass= SharedPrefManager.getInstance(context).getUser();
        if (userModelClass!=null){
            customer_id=userModelClass.getUser_id();
        }
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        this.context = context;
        this.modelList = itemList;
        this.arrayList=new ArrayList<RideRequestsModel>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drivers_list_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RideRequestsModel model=modelList.get(position);
        holder.tv_name.setText(model.getUser_name());
        holder.tv_institute.setText(model.getInstitute_name());
        holder.tv_address.setText(model.getPickup_lat()+"   "+model.getPickup_lng());

        holder.start_time.setText("Start Time :"+model.getStart_time());
        holder.end_time.setText("Return Time :"+model.getEnd_time());
        holder.txt_avrating.setText("Rating :"+model.getRating());
        holder.ratingBar.setRating(Float.parseFloat(model.getRating()));
        Glide.with(context).load(model.getUser_image()).dontAnimate().fitCenter().placeholder(R.drawable.bussdriver).into(holder.img_user);
        holder.tv_chat.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userid", model.getFirebase_id());
            context.startActivity(intent);
        });
        holder.btn_rate.setOnClickListener(view -> {
            ShowDialog(model.getUser_id(),customer_id);
        });
        holder.tv_track.setOnClickListener(view -> {

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            StartDriverTracking fragment=new StartDriverTracking();
            Bundle args = new Bundle();
            args.putString("driver_id", String.valueOf(model.getUser_id()));
            fragment.setArguments(args);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("fragment").commit();


        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_user;
        RatingBar ratingBar;
        TextView btn_rate,tv_name,tv_institute,tv_address,tv_chat,tv_track,start_time,end_time,txt_avrating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user=itemView.findViewById(R.id.img_dr);
            tv_name=itemView.findViewById(R.id.txt_name);
            btn_rate=itemView.findViewById(R.id.btn_rate);
            txt_avrating=itemView.findViewById(R.id.txt_avrating);
            ratingBar=itemView.findViewById(R.id.rating_bar);
            tv_institute=itemView.findViewById(R.id.txt_institute);
            tv_address=itemView.findViewById(R.id.txt_address);
            tv_chat=itemView.findViewById(R.id.btn_chat);
            tv_track=itemView.findViewById(R.id.btn_track);
            start_time=itemView.findViewById(R.id.txt_starttime);
            end_time=itemView.findViewById(R.id.txt_endtime);
        }
    }

    public void ShowDialog(String user_id, String customer_id)
    {

        rankDialog =  new Dialog(context);
        rankDialog.setContentView(R.layout.rank_dialog);
        rankDialog.setCancelable(true);
        ratingBar = (RatingBar)rankDialog.findViewById(R.id.dialog_ratingbar);
        ratingBar.setRating(2);

        TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
        text.setText("Rate Driver");

        Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rankDialog.dismiss();
                Add_rating(user_id,customer_id,String.valueOf(ratingBar.getRating()));
            }
        });
        //now that the dialog is set up, it's time to show it
        rankDialog.show();


    }

    private void Add_rating(String user_id, String customer_id, String valueOfrating) {
        pDialog.setMessage("please Wait....");
        pDialog.show();
        Log.d("Response is", "DATA OF RATING"+user_id+"===ammar"+customer_id+"===rating"+valueOfrating);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl+add_rating, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                Log.d("Response is", response.toString());
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Log.d("status", "CHECK" + jsonObject.getString("status"));
                        if (jsonObject.getString("status").equals("true")) {

                            Toast.makeText(context, "Rating added thank you", Toast.LENGTH_SHORT).show();

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(context, "Sorry", Toast.LENGTH_SHORT).show();
                        }
                    }

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
                Toast.makeText(context, "Some Error"+error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", customer_id);
                params.put("driver_id", user_id);
                params.put("rating", valueOfrating);
                return params;
            }

        };
        VolleyRequests.getInstance().addRequestQueue(stringRequest);
    }


}
