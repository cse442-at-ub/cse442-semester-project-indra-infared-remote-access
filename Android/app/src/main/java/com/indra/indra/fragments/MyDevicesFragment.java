package com.indra.indra.fragments;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.indra.indra.MainActivity;
import com.indra.indra.db.DatabaseUtil;
import com.indra.indra.models.RemoteModel;
import com.indra.indra.R;
import com.indra.indra.ui.buttons.MyDeviceMenuButton;

import java.util.ArrayList;


public class MyDevicesFragment extends Fragment {

    private TextView ipAddressDisplay;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedFragment = inflater.inflate(R.layout.fragment_my_devices, container, false);
        FloatingActionButton fab = inflatedFragment.findViewById(R.id.fab);
        String piAddress = ((MainActivity) getActivity()).getRaspberryPiIP();
        if(((MainActivity)this.getActivity()).easterEggOn()) {
            RelativeLayout rl  = inflatedFragment.findViewById(R.id.mdLayout);
            rl.setBackgroundResource(R.drawable.bluehs);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Used to switch between fragments in the current activity

                if(((MainActivity) getActivity()).getRaspberryPiIP() == null){
                    ((MainActivity) getActivity()).editIpAddressDialog(getString(R.string.ip_missing_warning), inflatedFragment.findViewById(R.id.ip_address_display));
                } else {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    transaction.replace(R.id.fragment_container, new ToolbarFragment()).commit();
                }

            }
        });

        LinearLayout sv = inflatedFragment.findViewById(R.id.devices_view);
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
                    if(((MainActivity)getActivity()).getRaspberryPiIP() == null){
                        displayDialog(getString(R.string.ip_missing_warning), inflatedFragment);
                    } else {
                        ((MainActivity) getActivity()).openDeviceFragment(deviceClass);
                    }
                }
            });

            sv.addView(b);
        }


        this.ipAddressDisplay = inflatedFragment.findViewById(R.id.ip_address_display);

        if(piAddress == null){
            ipAddressDisplay.setText("No IP address is set");
        } else {
            ipAddressDisplay.setText("Pi IP: " + piAddress);
        }

        Button editIpButton = inflatedFragment.findViewById(R.id.editIpAddress);
        editIpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog("Provide the IP address of your Pi.", inflatedFragment);
            }
        });

        return inflatedFragment;
    }


    public void displayDialog(String title, View inflatedFragment){
        ((MainActivity) getActivity()).editIpAddressDialog(title, ipAddressDisplay);
    }
}
