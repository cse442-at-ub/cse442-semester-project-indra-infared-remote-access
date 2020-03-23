package com.indra.indra.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.nkzawa.socketio.client.Socket;
import com.indra.indra.MainActivity;
import com.indra.indra.R;
import com.indra.indra.objects.BaseDeviceClass;
import com.indra.indra.objects.buttons.RemoteButton;
import com.indra.indra.objects.buttons.RemoteImageButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BasicDeviceFragment extends Fragment implements View.OnClickListener {
    public String _deviceName;
    private BaseDeviceClass baseDevice;
    private int layoutId;

    public BasicDeviceFragment(BaseDeviceClass basicDevice, int layoutId) {
        _deviceName = basicDevice.getDisplayName();
        this.baseDevice = basicDevice;
        this.layoutId = layoutId;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setMenuItemChecked(R.id.nav_remote);
        View inflatedFragment = inflater.inflate(layoutId, container, false);

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
                button.setOnClickListener(this);
            }

        }


        return inflatedFragment;
    }

    @Override
    public void onClick(View v) {
        String buttonCode = v instanceof RemoteButton ? ((RemoteButton)v).getLircName() : ((RemoteImageButton) v).getLircName();

        clickButton(buttonCode);
    }


    private void clickButton(String buttonCode){
        Socket clientSocket = ((MainActivity)getActivity()).getClientSocket();

        HashMap<String, String> jsonMap = new HashMap<>();
        jsonMap.put("remote", baseDevice.getLircName());
        jsonMap.put("button", buttonCode);

        JSONObject message = new JSONObject(jsonMap);
        clientSocket.emit("button_press", message.toString());
        Log.d("SOCKETIO", message.toString());
        vibrateOnClick();
    }

    private void vibrateOnClick(){
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(20, 255));
    }
}
