package com.fyp.securepickanddrop.adapterclasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;
import com.fyp.securepickanddrop.constantclasses.SharedPrefManager;
import com.fyp.securepickanddrop.constantclasses.VolleyRequests;
import com.fyp.securepickanddrop.modelsclasses.InstituteModelClass;
import com.fyp.securepickanddrop.modelsclasses.RideRequestsModel;
import com.fyp.securepickanddrop.modelsclasses.UserModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyIntitutesAdapter extends RecyclerView.Adapter<MyIntitutesAdapter.ViewHolder> {
    Context context;
    List<InstituteModelClass> modelList;
    ArrayList<InstituteModelClass> arrayList;
    private ProgressDialog pDialog;
    String user_id="",deleteinstitute="delete-institute";

    public MyIntitutesAdapter(Context context, List<InstituteModelClass> itemList) {
        this.context = context;
        this.modelList = itemList;
        this.arrayList=new ArrayList<InstituteModelClass>();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        UserModelClass userModelClass = SharedPrefManager.getInstance(context).getUser();
        if (userModelClass!=null){
            user_id=userModelClass.getUser_id();
        }else {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_institute_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final InstituteModelClass model = modelList.get(position);
        holder.tv_name.setText(model.getIns_name());
        holder.img_delete.setOnClickListener(view -> {
            DeleteInstitutes(user_id,model.getIns_id(),position);
        });
    }

    private void DeleteInstitutes(String user_id, String ins_id, int position) {
        pDialog.setMessage("Please Wait....");
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.mainurl+deleteinstitute, new Response.Listener<String>() {
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
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            modelList.remove(position);
                            notifyDataSetChanged();
                        } else {
                            pDialog.dismiss();
                            Toast.makeText(context, "No Post", Toast.LENGTH_SHORT).show();
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
                params.put("institute_id",ins_id );
               // params.put("driver_id",user_id );

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
        ImageView img_delete;
        TextView tv_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_delete=itemView.findViewById(R.id.img_delete);
            tv_name=itemView.findViewById(R.id.txt_item_name);
        }
    }
}
