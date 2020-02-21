package com.indra.indra;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MyDevicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_devices);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Uncomment when NewDeviceActivity exists
                //startActivity(new Intent(MyDevicesActivity.this, NewDeviceActivity.class));
            }
        });

        LinearLayout sv = findViewById(R.id.devices_view);
        //DUMMY DATA
        Device tv = new Device("Living Room TV");
        Device lights = new Device("My Bedroom String Lights");
        Button tvButton = new Button(this);
        Button lightsButton = new Button(this);
        tvButton.setText(tv.getDeviceName());
        lightsButton.setText(lights.getDeviceName());

        sv.addView(tvButton);
        sv.addView(lightsButton);
    }

}
