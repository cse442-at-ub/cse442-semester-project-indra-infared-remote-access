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
import com.mongodb.stitch.android.core.auth.providers.userapikey.UserApiKeyAuthProviderClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.core.auth.providers.userapikey.internal.*;
import com.mongodb.stitch.core.auth.providers.userapikey.UserApiKeyCredential;
import com.mongodb.stitch.core.auth.providers.userapikey.models.UserApiKey;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ChangeEventListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ErrorListener;


import org.bson.Document;

public class SignupActivity extends AppCompatActivity {

    private Button Signup1;
    private EditText username;
    private EditText password;
    private EditText repassword;
    private String mongoAppId = "indra-cqutq";
    private String mongoAPIKey = "6Db22acWCuY935LZz7BnERvtsdNMsOdDrDZHrRmWgjRlGTbxGRroK0dphcZAyh0T";
    private StitchAppClient stitchClient;
    private RemoteMongoClient mongoClient;
    private RemoteMongoCollection<Document> itemsCollection;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = (EditText) findViewById(R.id.editText3);
        password = (EditText) findViewById(R.id.editText4);
        repassword = (EditText) findViewById(R.id.editText5);
        errorText = (TextView) findViewById(R.id.errorMessage2);
        Signup1 = (Button) findViewById(R.id.button2);
        authorizeDB2();
            Signup1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sign_up(username.getText().toString(), password.getText().toString());
                }
            });
    }

    private void authorizeDB2(){
        StitchAppClient client;
        try {
            client = Stitch.getDefaultAppClient();
        }
        catch(Exception e) {
            client = Stitch.initializeDefaultAppClient(mongoAppId);
        }
        client.getAuth().loginWithCredential(new UserApiKeyCredential(mongoAPIKey));
        if(client.getAuth().isLoggedIn()) {
            Log.d("login", "logged into db");
        }
        else {
            Log.d("login", "FAILED TO LOG IN");
        }
        final RemoteMongoClient mongoClient = client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        itemsCollection = mongoClient.getDatabase("indra-users").getCollection("users");
    }

    private void sign_up(String user, String pass) {
        Document newDoc = new Document();
        newDoc.append("username", user).append("password", pass);
        final Task <RemoteInsertOneResult> insertTask = itemsCollection.insertOne(newDoc);
        insertTask.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                if(task.isSuccessful()){
                    Log.d("Signup", "Signup complete!!");
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Log.e("Signup", "Error signing up");
                }
            }
        });


    }

}

