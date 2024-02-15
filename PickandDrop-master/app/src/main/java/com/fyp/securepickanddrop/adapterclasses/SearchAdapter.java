package com.fyp.securepickanddrop.adapterclasses;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.fragment.SearchedDriversFragment;
import com.fyp.securepickanddrop.modelsclasses.InstituteModelClass;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    Context context;
    List<InstituteModelClass> modelList;
    ArrayList<InstituteModelClass> arrayList;
    public SearchAdapter(Context context, List<InstituteModelClass> itemList) {
        this.context = context;
        this.modelList = itemList;
        this.arrayList=new ArrayList<InstituteModelClass>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_institutes_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final InstituteModelClass model = modelList.get(position);
        holder.tv_name.setText(model.getIns_name());
        holder.search_btn.setOnClickListener(view -> {

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            SearchedDriversFragment fragment=new SearchedDriversFragment();
            Bundle args = new Bundle();
            args.putString("name", String.valueOf(model.getIns_name()));
            args.putString("institute_id", String.valueOf(model.getIns_id()));
            fragment.setArguments(args);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.user_main_frame, fragment).addToBackStack("fragment").commit();

        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button search_btn;
        TextView tv_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            search_btn=itemView.findViewById(R.id.search_btn);
            tv_name=itemView.findViewById(R.id.txt_item_name);
        }
    }
}
