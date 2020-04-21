package com.indra.indra.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.indra.indra.MainActivity;
import com.indra.indra.R;
import com.indra.indra.db.DatabaseUtil;
import com.indra.indra.models.RemoteModel;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    private RemoteModel remote;
    private DatabaseUtil db;

    public SettingsFragment(RemoteModel remote) {
        this.remote = remote;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedFragment = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView deviceText = inflatedFragment.findViewById(R.id.activeDevice);
        deviceText.setText(remote.getDisplayName()); //find a way to get name of device
        db = new DatabaseUtil(getActivity());

        Button deleteButton = inflatedFragment.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        return inflatedFragment;
    }

    @Override
    public void onClick(View v) {

        new AlertDialog.Builder(getActivity())
                .setTitle("Are you sure you want to delete this remote?")
                .setMessage("This remote will no longer be in 'My Devices'.")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteRemote(remote);

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();


                        transaction.replace(R.id.fragment_container, new MyDevicesFragment()).commit();
                        ((MainActivity) getActivity()).setMenuItemChecked(R.id.nav_my_devices);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {/* Do nothing */}
                })
                .show();
    }
}
