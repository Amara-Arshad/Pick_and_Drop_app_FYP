package com.fyp.securepickanddrop.adapterclasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.activities.MessageActivity;
import com.fyp.securepickanddrop.modelsclasses.PassengerModelClass;
import com.fyp.securepickanddrop.modelsclasses.RideRequestsModel;

import java.util.ArrayList;
import java.util.List;

public class MyPassengersAdapter extends RecyclerView.Adapter<MyPassengersAdapter.ViewHolder> {

    Context context;
    List<RideRequestsModel> modelList;
    ArrayList<RideRequestsModel> arrayList;
    public MyPassengersAdapter(Context context, List<RideRequestsModel> itemList) {
        this.context = context;
        this.modelList = itemList;
        this.arrayList=new ArrayList<RideRequestsModel>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.passengers_list_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RideRequestsModel model=modelList.get(position);
        holder.tv_name.setText(model.getUser_name());
        holder.tv_institute.setText(model.getInstitute_name());
        holder.tv_address.setText(model.getPickup_lat()+"   "+model.getPickup_lng());
        Glide.with(context).load(model.getUser_image()).dontAnimate().fitCenter().placeholder(R.drawable.bussdriver).into(holder.img_user);
        holder.tv_chat.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userid", model.getFirebase_id());
            context.startActivity(intent);
        });
        holder.tv_call.setOnClickListener(view -> {

        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_user;
        TextView tv_name,tv_institute,tv_address,tv_chat,tv_call;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user=itemView.findViewById(R.id.img_dr);
            tv_name=itemView.findViewById(R.id.txt_name);
            tv_institute=itemView.findViewById(R.id.txt_institute);
            tv_address=itemView.findViewById(R.id.txt_address);
            tv_chat=itemView.findViewById(R.id.btn_chat);
            tv_call=itemView.findViewById(R.id.btn_call);
        }
    }
}
