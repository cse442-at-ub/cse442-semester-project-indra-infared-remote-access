package com.indra.indra.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.indra.indra.R;


public class SettingsFragment extends Fragment {

    private String _deviceName;

    public SettingsFragment(String deviceName) {
        _deviceName = deviceName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedFragment = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView deviceText = inflatedFragment.findViewById(R.id.activeDevice);
        deviceText.setText(_deviceName); //find a way to get name of device
        return inflatedFragment;
    }

}
