package com.indra.indra.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.indra.indra.LoginActivity;
import com.indra.indra.MainActivity;
import com.indra.indra.R;
import com.indra.indra.db.DatabaseUtil;
import com.indra.indra.models.RemoteButtonModel;
import com.indra.indra.models.RemoteModel;
import com.indra.indra.ui.buttons.MyDeviceMenuButton;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.userapikey.UserApiKeyCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteDeleteResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountSettingsFragment extends Fragment {
    private String mongoAppId = "indra-cqutq";
    private String mongoAPIKey = "6Db22acWCuY935LZz7BnERvtsdNMsOdDrDZHrRmWgjRlGTbxGRroK0dphcZAyh0T";
    private RemoteMongoCollection<Document> usersCol;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountSettingsFragment newInstance(String param1, String param2) {
        AccountSettingsFragment fragment = new AccountSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedFragment = inflater.inflate(R.layout.fragment_account_settings, container, false);

        String currentUser = ((MainActivity) getActivity()).getCurrentUser();
        EditText mAccountName = inflatedFragment.findViewById(R.id.account_name);
        mAccountName.setText(currentUser);

        authenticateDB();

        DatabaseUtil util = new DatabaseUtil(getActivity());

        Button save = inflatedFragment.findViewById(R.id.edit_button_name);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = mAccountName.getText().toString();
                Log.d("name", newName);

                Document filterDoc = new Document().append("username", currentUser);
                Document updateDoc = new Document().append("$set",
                        new Document()
                                .append("username", newName)
                );

                final Task<RemoteUpdateResult> updateTask =
                        usersCol.updateOne(filterDoc, updateDoc);
                updateTask.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
                    @Override
                    public void onComplete(@NonNull Task <RemoteUpdateResult> task) {
                        if (task.isSuccessful()) {
                            long numMatched = task.getResult().getMatchedCount();
                            long numModified = task.getResult().getModifiedCount();
                            Log.d("app", String.format("successfully matched %d and modified %d documents",
                                    numMatched, numModified));
                        } else {
                            Log.e("app", "failed to update document with: ", task.getException());
                        }
                    }
                });

                util.updateRemoteUsernames(currentUser, newName);
                ((MainActivity) getActivity()).setCurrentUser(newName);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new MyDevicesFragment()).commit();
            }
        });

        TableLayout devicesTable = inflatedFragment.findViewById(R.id.account_settings_devices);

        ArrayList<RemoteModel> remoteModels = util.getDevicesForUser(currentUser);

        for(final RemoteModel deviceClass : remoteModels){
            TableRow row = new TableRow(getActivity());

            //TODO: fix width
            TextView remoteName = new TextView(getActivity());
            remoteName.setText(deviceClass.getDisplayName());
            //remoteName.setWidth((getActivity().getWindow().getAttributes().width)*(4/5));

            row.addView(remoteName);

            Button b = new Button(getActivity());
            b.setText("edit");
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Used to switch between fragments in the current activity
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    transaction.replace(R.id.fragment_container, new SettingsFragment(deviceClass)).commit();
                }
            });
            //b.setWidth((getActivity().getWindow().getAttributes().width)/5);

            row.addView(b);

            devicesTable.addView(row);
        }

        Button delete = inflatedFragment.findViewById(R.id.delete_account);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document filterDoc = new Document().append("username", currentUser);

                final Task<RemoteDeleteResult> deleteTask = usersCol.deleteOne(filterDoc);
                deleteTask.addOnCompleteListener(new OnCompleteListener <RemoteDeleteResult> () {
                    @Override
                    public void onComplete(@NonNull Task <RemoteDeleteResult> task) {
                        if (task.isSuccessful()) {
                            long numDeleted = task.getResult().getDeletedCount();
                            Log.d("app", String.format("successfully deleted %d documents", numDeleted));
                        } else {
                            Log.e("app", "failed to delete document with: ", task.getException());
                        }
                    }
                });

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                // this will clear the back stack and displays no animation on the screen
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Intent intent = new Intent(((MainActivity) getActivity()), LoginActivity.class);
                startActivity(intent);
            }
        });
        return inflatedFragment;
    }

    private void authenticateDB() {
        //mongoDB connection - from MongoDB official 'how to connect java(android)'
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
        Log.d("login", "GOT HERE");
        usersCol = mongoClient.getDatabase("indra-users").getCollection("users");
        Log.d("login", "FINISHED WTF");
        // end MongoDB connection code
    }
}
