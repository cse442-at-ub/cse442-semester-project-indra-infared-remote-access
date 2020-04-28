package com.indra.indra.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.indra.indra.MainActivity;
import com.indra.indra.db.DatabaseUtil;
import com.indra.indra.models.RemoteButtonModel;
import com.indra.indra.models.RemoteModel;
import com.indra.indra.R;
import com.indra.indra.ui.buttons.MyDeviceMenuButton;

import java.util.ArrayList;


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
        final RemoteModel tv = new RemoteModel(getString(R.string.living_room_tv), "SamsungBN59-01054A");
        final RemoteModel lights = new RemoteModel(getString(R.string.string_lights), "DUMMY DATA");
        Button tvButton = new Button(getActivity());
        final Button lightsButton = new Button(getActivity());

        tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Used to switch between fragments in the current activity
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                Fragment nextActiveFragment = new BasicDeviceFragment(tv, R.layout.fragment_default_tv_remote);
                transaction.replace(R.id.fragment_container, nextActiveFragment).commit();
            }
        });

        lightsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Used to switch between fragments in the current activity
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                Fragment nextActiveFragment = new BasicDeviceFragment(lights, R.layout.fragment_basic_device);
                transaction.replace(R.id.fragment_container, nextActiveFragment).commit();
            }
        });

        tvButton.setText(tv.getDisplayName());
        lightsButton.setText(lights.getDisplayName());

//        sv.addView(tvButton);
//        sv.addView(lightsButton);

        DatabaseUtil util = new DatabaseUtil(getActivity());

        ArrayList<RemoteModel> remoteModels = util.getDevicesForUser(DatabaseUtil.DEFAULT_USER);

        if(remoteModels.isEmpty()){
            ((MainActivity) getActivity()).setCurrentRemote(null);
        }

        for(final RemoteModel deviceClass : remoteModels){
            MyDeviceMenuButton b = new MyDeviceMenuButton(deviceClass, getActivity());

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).openDeviceFragment(deviceClass);
                }
            });

//            b.setText(deviceClass.getDisplayName());

            sv.addView(b);
        }

        return inflatedFragment;
    }
}
