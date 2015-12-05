package com.ntu.phongnt.healthdroid.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ntu.phongnt.healthdroid.R;

public class HomeFragment extends Fragment implements Button.OnClickListener {
    public HomeFragment() {
        super();
    }

    private Button button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.home, container, false);
        setRetainInstance(true);
        button = (Button) view.findViewById(R.id.send_button);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Toast toast = Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT);
        toast.show();
    }
}
