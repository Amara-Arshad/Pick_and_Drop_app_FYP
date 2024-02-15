package com.fyp.securepickanddrop.adapterclasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.modelsclasses.RideRequestsModel;

import java.util.ArrayList;
import java.util.List;


public class UserRideRequestsAdapter extends RecyclerView.Adapter<UserRideRequestsAdapter.ViewHolder> {
    Context context;
    List<RideRequestsModel> modelList;
    ArrayList<RideRequestsModel> arrayList;

    public UserRideRequestsAdapter(Context context, List<RideRequestsModel> itemList) {
        this.context = context;
        this.modelList = itemList;
        this.arrayList = new ArrayList<RideRequestsModel>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_ride_requests_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RideRequestsModel model = modelList.get(position);

        holder.tv_name.setText(model.getUser_name());
        holder.tv_institute.setText(model.getInstitute_name());
        holder.tv_location.setText(model.getPickup_lat() + "  " + model.getPickup_lng());
        holder.tv_no_of_seats.setText(model.getNo_of_seats());
        int request_status= Integer.parseInt(model.getRequest_status());
        if (request_status==0){
            holder.tv_status.setText("pending");
        }else if (request_status==2){
            holder.tv_status.setText("Rejected");
        }else if (request_status==1){
            holder.tv_status.setText("Accepted");
        }

        Glide.with(context).load(model.getUser_image()).dontAnimate().fitCenter().placeholder(R.drawable.bussdriver).into(holder.img_user);

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_institute, tv_location, tv_no_of_seats,tv_status;
        ImageView img_user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.fullName);
            tv_institute = itemView.findViewById(R.id.request_institute);
            tv_location = itemView.findViewById(R.id.request_location);
            tv_no_of_seats = itemView.findViewById(R.id.requested_seats);
            tv_status = itemView.findViewById(R.id.requeste_status);
            img_user = itemView.findViewById(R.id.profile_image);
        }
    }
}
