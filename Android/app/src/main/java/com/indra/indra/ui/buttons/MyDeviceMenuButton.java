package com.indra.indra.ui.buttons;

import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;

import com.indra.indra.models.RemoteModel;

public class MyDeviceMenuButton extends AppCompatButton {

    private RemoteModel remote;


    public MyDeviceMenuButton(RemoteModel remote, Context context){
        super(context);
        this.remote = remote;
        this.setText(this.remote.getDisplayName());
    }



}
