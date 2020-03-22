package com.indra.indra.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.indra.indra.MainActivity;
import com.indra.indra.R;

public class BasicDeviceFragment extends Fragment implements View.OnClickListener {
    public String _deviceName;

    public BasicDeviceFragment(String deviceName) {
        _deviceName = deviceName;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setMenuItemChecked(R.id.nav_remote);
        View inflatedFragment = inflater.inflate(R.layout.fragment_basic_device, container, false);

        ImageButton settingsButton =  inflatedFragment.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Used to switch between fragments in the current activity
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();


                transaction.replace(R.id.fragment_container, new SettingsFragment(_deviceName)).commit();
            }
        });

        Button powerOnButton = inflatedFragment.findViewById(R.id.powerOnButton);
        powerOnButton.setOnClickListener(this);

        Button powerOffButton = inflatedFragment.findViewById(R.id.powerOffButton);
        powerOffButton.setOnClickListener(this);

        return inflatedFragment;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.powerOnButton:
                Log.d("Connection Alerts", "Try to send POWER_ON to server");
                ((MainActivity)getActivity()).socketSendToServer("POWER_ON"); //TODO: Filler until message to send is determined
                break;

            case R.id.powerOffButton:
                Log.d("Connection Alerts", "Try to send POWER_OFF to server");
                ((MainActivity)getActivity()).socketSendToServer("POWER_OFF"); //TODO: Filler until message to send is determined
                break;
        }
    }
}
