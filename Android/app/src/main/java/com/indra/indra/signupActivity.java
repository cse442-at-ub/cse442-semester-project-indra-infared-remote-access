package com.indra.indra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class signupActivity extends AppCompatActivity {

    private Button Signup1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Signup1 = (Button) findViewById(R.id.button2);

        Signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(signupActivity.this, MainActivity.class);
                startActivity(intent3);
            }
        });
    }
}
