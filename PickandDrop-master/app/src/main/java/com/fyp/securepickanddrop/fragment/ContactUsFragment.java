package com.fyp.securepickanddrop.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fyp.securepickanddrop.R;
import com.fyp.securepickanddrop.constantclasses.ConstantValues;

public class ContactUsFragment extends Fragment {
    View view;
    TextView textView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.contact_us_framgent,container,false);
        textView=view.findViewById(R.id.tv_contacts);
        textView.setText(Html.fromHtml(ConstantValues.about));
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Contact Us");
        super.onViewCreated(view, savedInstanceState);
    }
}
