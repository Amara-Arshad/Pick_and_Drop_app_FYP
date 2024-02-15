package com.fyp.securepickanddrop.adapterclasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.activities.DriverMainActivity;
import com.fyp.securepickanddrop.activities.MessageActivity;
import com.fyp.securepickanddrop.fragment.SearchedDriversFragment;
import com.fyp.securepickanddrop.fragment.SentRideRequestFragment;
import com.fyp.securepickanddrop.modelsclasses.PassengerModelClass;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;

import java.util.ArrayList;
import java.util.List;

public class SearchedDriversAdapter extends RecyclerView.Adapter<SearchedDriversAdapter.ViewHolder> {
    Context context;
    List<UserModelClass> modelList;
    ArrayList<UserModelClass> arrayList;
    public SearchedDriversAdapter(Context context, List<UserModelClass> itemList) {
        this.context = context;
        this.modelList = itemList;
        this.arrayList=new ArrayList<UserModelClass>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_drivers_list_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserModelClass modelClass=modelList.get(position);
        holder.driver_name.setText(modelClass.getUser_name());
        holder.driver_email.setText(modelClass.getUser_email());
        holder.driver_address.setText("Vehicle Seats:  "+modelClass.getNo_of_seats());
        holder.start_time.setText("Start Time :"+modelClass.getStart_time());
        holder.end_time.setText("Return Time :"+modelClass.getEnd_time());
        holder.rating.setText("Rating : "+modelClass.getRating());
        Glide.with(context).load(modelClass.getUser_image()).dontAnimate().fitCenter().placeholder(R.drawable.bussdriver).into(holder.driver_image);
        holder.btn_hire.setOnClickListener(view -> {

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            SentRideRequestFragment fragment=new SentRideRequestFragment();
            Bundle args = new Bundle();
            args.putString("name", String.valueOf(modelClass.getUser_name()));
            args.putString("driver_id", String.valueOf(modelClass.getUser_id()));
            args.putString("institute_id", String.valueOf(modelClass.getInstitute_id()));
            args.putString("institute_name", String.valueOf(modelClass.getIntitute_name()));
            fragment.setArguments(args);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("fragment").commit();


        });
        holder.btn_chat.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userid", modelClass.getFirebase_id());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView driver_image;
        TextView driver_name,driver_email,driver_address,btn_chat,btn_hire,start_time,end_time,rating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driver_image=itemView.findViewById(R.id.img_dr);
            rating=itemView.findViewById(R.id.txt_driver_rating);
            driver_name=itemView.findViewById(R.id.txt_driver_name);
            driver_email=itemView.findViewById(R.id.txt_dr_email);
            driver_address=itemView.findViewById(R.id.txt_driver_address);
            btn_chat=itemView.findViewById(R.id.btn_dr_message);
            btn_hire=itemView.findViewById(R.id.btn_dr_hire);
            start_time=itemView.findViewById(R.id.txt_starttime);
            end_time=itemView.findViewById(R.id.txt_endtime);
        }
    }
}
