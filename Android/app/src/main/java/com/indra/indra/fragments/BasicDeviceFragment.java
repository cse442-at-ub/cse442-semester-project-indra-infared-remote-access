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

public class BasicDeviceFragment extends Fragment {
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

                //TODO: Uncomment when Settings page exists
                transaction.replace(R.id.fragment_container, new SettingsFragment(_deviceName)).commit();
            }
        });

        Button powerOnButton = inflatedFragment.findViewById(R.id.powerOnButton);
        powerOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).socketSendToServer("POWER ON"); //TODO: Filler until message to send is determined
                Log.d("Connection Alerts", "Try to send POWER ON to server");
            }
        });

        Button powerOffButton = inflatedFragment.findViewById(R.id.powerOffButton);
        powerOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).socketSendToServer("POWER OFF"); //TODO: Filler until message to send is determined
                Log.d("Connection Alerts", "Try to send POWER OFF to server");
            }
        });
        return inflatedFragment;
    }
}
