package com.indra.indra.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.indra.indra.MainActivity;
import com.indra.indra.R;
import com.indra.indra.models.RemoteModel;
import com.indra.indra.objects.RemoteButtonHandlerDaemon;
import com.indra.indra.ui.buttons.RemoteButton;
import com.indra.indra.ui.buttons.RemoteImageButton;

import java.util.ArrayList;

public class BasicDeviceFragment extends Fragment implements View.OnTouchListener {
    public String _deviceName;
    private RemoteModel baseDevice;
    private int layoutId;
    private RemoteButtonHandlerDaemon remoteButtonDaemon;

    public BasicDeviceFragment(RemoteModel basicDevice, int layoutId) {
        _deviceName = basicDevice.getDisplayName();
        this.baseDevice = basicDevice;
        this.layoutId = layoutId;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setMenuItemChecked(R.id.nav_remote);
        View inflatedFragment = inflater.inflate(layoutId, container, false);
        MainActivity activity = (MainActivity)getActivity();
        this.remoteButtonDaemon = RemoteButtonHandlerDaemon.getInstance(activity.getClientSocket(), activity);

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


        ArrayList<View> buttons = inflatedFragment.getTouchables();

        for(View button : buttons){

            if(button instanceof RemoteImageButton || button instanceof RemoteButton){
                button.setOnTouchListener(this);
            }

        }


        return inflatedFragment;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction();

        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP){
            Handler daemonHandler = remoteButtonDaemon.getButtonEventMessageHandler();
            String lircName = baseDevice.getLircName();
            String buttonName = v instanceof RemoteButton ? ((RemoteButton)v).getLircName() : ((RemoteImageButton) v).getLircName();

            Bundle buttonPressBundle = new Bundle();
            buttonPressBundle.putString(RemoteButtonHandlerDaemon.REMOTE_NAME_KEY, lircName);
            buttonPressBundle.putString(RemoteButtonHandlerDaemon.BUTTON_NAME_KEY, buttonName);
            buttonPressBundle.putInt(RemoteButtonHandlerDaemon.BUTTON_EVENT_KEY, action);
            Message msg = daemonHandler.obtainMessage();
            msg.setData(buttonPressBundle);
            daemonHandler.sendMessage(msg);
            return false;
        }


        return false;
    }

}
