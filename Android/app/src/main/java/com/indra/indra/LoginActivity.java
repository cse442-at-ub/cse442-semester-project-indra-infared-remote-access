package com.indra.indra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private EditText Username;
    private EditText Password;
    private Button Login;
    private Button Signup;
    private TextView ErrorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = (EditText) findViewById(R.id.editText);
        Password = (EditText) findViewById(R.id.editText2);
        Signup = (Button) findViewById(R.id.button3);
        Login = (Button) findViewById(R.id.loginButton);
        ErrorText = (TextView) findViewById(R.id.errorMessage);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Username.getText().toString(), Password.getText().toString());
            }
        });
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(LoginActivity.this, signupActivity.class);
                startActivity(intent2);
            }
        });

    }
    private void validate(String nameUser, String passUser){
        if ((nameUser.equals("Indra")) && (passUser.equals("password"))){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            ErrorText.setText("Incorrect login info. Try again.");
        }

    }
}
