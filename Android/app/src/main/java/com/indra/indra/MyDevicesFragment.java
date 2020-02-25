package com.indra.indra;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MyDevicesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedFragment = inflater.inflate(R.layout.fragment_my_devices, container, false);
        FloatingActionButton fab = inflatedFragment.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Used to switch between fragments in the current activity
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.replace(R.id.fragment_container, new ToolbarFragment()).commit();
            }
        });

        LinearLayout sv = inflatedFragment.findViewById(R.id.devices_view);
        //DUMMY DATA
        BaseDeviceClass tv = new BaseDeviceClass("Living Room TV");
        BaseDeviceClass lights = new BaseDeviceClass("String Lights");
        Button tvButton = new Button(getActivity());
        Button lightsButton = new Button(getActivity());

        tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Used to switch between fragments in the current activity
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.replace(R.id.fragment_container, new RemoteFragment()).commit();
            }
        });

        lightsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Used to switch between fragments in the current activity
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.replace(R.id.fragment_container, new BasicDeviceFragment()).commit();
            }
        });

        tvButton.setText(tv.getDeviceName());
        lightsButton.setText(lights.getDeviceName());

        sv.addView(tvButton);
        sv.addView(lightsButton);

        return inflatedFragment;
    }
}
