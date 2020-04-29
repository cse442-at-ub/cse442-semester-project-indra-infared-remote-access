package com.indra.indra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.userapikey.UserApiKeyCredential;
import org.bson.Document;

public class LoginActivity extends AppCompatActivity {
    private EditText Username;
    private EditText Password;
    private Button Login;
    private Button Signup;
    private TextView ErrorText;
    private String mongoAppId = "indra-cqutq";
    private String mongoAPIKey = "6Db22acWCuY935LZz7BnERvtsdNMsOdDrDZHrRmWgjRlGTbxGRroK0dphcZAyh0T";
    private RemoteMongoCollection<Document> usersCol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = (EditText) findViewById(R.id.editText);
        Password = (EditText) findViewById(R.id.editText2);
        Signup = (Button) findViewById(R.id.signupButton);
        Login = (Button) findViewById(R.id.loginButton);
        ErrorText = (TextView) findViewById(R.id.errorMessage);

//       authenticateDB();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Username.getText().toString(), Password.getText().toString());
            }
        });
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent2);
            }
        });

    }

    private void authenticateDB() {
        //mongoDB connection - from MongoDB official 'how to connect java(android)'
        final StitchAppClient client = Stitch.initializeDefaultAppClient(mongoAppId);
        client.getAuth().loginWithCredential(new UserApiKeyCredential(mongoAPIKey));
        if(client.getAuth().isLoggedIn()) {
            Log.d("login", "logged into db");
        }
        else {
            Log.d("login", "FAILED TO LOG IN");
        }
        final RemoteMongoClient mongoClient = client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        usersCol = mongoClient.getDatabase("indra-users").getCollection("users");

        // end MongoDB connection code
    }
    private void validate(String nameUser, String passUser)
    {
        Document filter = new Document();
        filter.put("username", nameUser);
        filter.put("password", passUser);
        Task<Document> resp = usersCol.findOne(filter);
        resp.addOnCompleteListener(new OnCompleteListener<Document>() {
            @Override
            public void onComplete(@NonNull Task<Document> task) {
                if(task.getResult() == null) {
                    Log.d("login", "No doc found");
                    ErrorText.setText("Incorrect login info. Try again.");
                }
                else if(task.isSuccessful()) {
                    Log.d("login", "username+password found");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Log.d("login", "unexpected error!!");
                }
            }
        });

    }
}
