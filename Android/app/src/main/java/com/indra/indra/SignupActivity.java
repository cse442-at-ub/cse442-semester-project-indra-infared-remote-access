package com.indra.indra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.providers.userapikey.UserApiKeyAuthProviderClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.core.auth.providers.userapikey.internal.*;
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
    private String mongoAppId = "indra-cqutq";
    private String mongoAPIKey = "6Db22acWCuY935LZz7BnERvtsdNMsOdDrDZHrRmWgjRlGTbxGRroK0dphcZAyh0T";
    private StitchAppClient stitchClient;
    private RemoteMongoClient mongoClient;
    private RemoteMongoCollection itemsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = (EditText) findViewById(R.id.editText3);
        password = (EditText) findViewById(R.id.editText4);
        Signup1 = (Button) findViewById(R.id.button2);
        authorizeDB2();
        Signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createKey(username.getText().toString(), password.getText().toString());
            }
        });
    }
    private void authorizeDB2(){
        UserApiKeyAuthProviderClient apikeyclient = Stitch.getAppClient(mongoAppId).getAuth().getProviderClient(UserApiKeyAuthProviderClient.factory);
        apikeyclient.createApiKey(mongoAPIKey).addOnCompleteListener(new OnCompleteListener<UserApiKey>() {
            @Override
            public void onComplete(@NonNull Task<UserApiKey> task) {
                if (task.isSuccessful()){
                    Log.d("Signup", "Successfully created user API: "+ task.getResult().getKey());
                }
                else {
                    Log.e("Signup", "Error creating user API: "+ task.getException());
                }
            }
        });

    }
    private void createKey(String user, String pass) {
        itemsCollection = mongoClient.getDatabase("store").getCollection("items");
        Document newDoc = new Document();
        newDoc.append("Username", user).append("Password", pass);
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

