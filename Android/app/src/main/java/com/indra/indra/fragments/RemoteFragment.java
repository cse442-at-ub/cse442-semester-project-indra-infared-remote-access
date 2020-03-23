package com.indra.indra.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.indra.indra.MainActivity;
import com.indra.indra.R;

import java.util.ArrayList;

public class RemoteFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setMenuItemChecked(R.id.nav_remote);
        View inflatedFragment = inflater.inflate(R.layout.fragment_default_tv_remote, container, false);

        inflatedFragment.findViewById(R.id.settingsButton).setOnClickListener(this);
        //all touchable objects in remote get assigned to onClick() method as listener (all buttons)
        ArrayList<View> allButtons = ((TableLayout)(inflatedFragment.findViewById(R.id.bigButtonHolder))).getTouchables();
        for(View b : allButtons) {
           b.setOnClickListener(this);
        }

        return inflatedFragment;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.settingsButton:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new SettingsFragment(getString(R.string.living_room_tv))).commit();
                break;
            //any button on default remote frag pressed that's not the settings button is on remote
            //remote id for those buttons is expected to be an accurate message for what we need to send
            //TODO: split into different cases
            case R.id.bPower:
                Log.d("Connection Alerts", "Try to send " + "POWER" + " to server");
                ((MainActivity)getActivity()).socketSendToServer("POWER"); //TODO: Filler until message to send is determined
                break;
            default:
                Log.d("Connection Alerts", "Try to send " + "OTHER INPUT" + " to server");
                ((MainActivity)getActivity()).socketSendToServer("OTHER INPUT"); //TODO: Filler until message to send is determined
                break;
        }
    }
}
