package com.indra.indra.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.appbar.AppBarLayout;
import com.indra.indra.MainActivity;
import com.indra.indra.R;
import com.indra.indra.objects.RemoteConfig;
import com.indra.indra.objects.RemoteConfigListAdapter;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ToolbarFragment extends Fragment {

    private static final String TAG = "ToolbarFragment";
    private static final int STANDARD_APPBAR = 0;
    private static final int SEARCH_APPBAR = 1;
    private int mAppBarState;
    private ListView remotesList;

    private EditText mBrandSearchContacts, mModelSearchContacts;
    private Button submitSearch;

    private AppBarLayout viewContactsBar, searchBar;
    private RemoteConfigListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewremotes, container, false);
        viewContactsBar = (AppBarLayout) view.findViewById(R.id.viewRemotesToolbar);
        searchBar = (AppBarLayout) view.findViewById(R.id.searchToolbar);
        remotesList = view.findViewById(R.id.remotesList);
        mBrandSearchContacts = view.findViewById(R.id.etBrandSearchContacts);
        mModelSearchContacts = view.findViewById(R.id.etModelSearchContacts);
        submitSearch = view.findViewById(R.id.bSubmitSearch);

        Log.d(TAG, "onCreateView: started");

        setAppBarState(STANDARD_APPBAR);
        ((MainActivity) getActivity()).setMenuItemChecked(R.id.nav_add_device);

        ImageView ivSearchContact = (ImageView) view.findViewById(R.id.ivSearchIcon);
        ivSearchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked searched icon");
                toggleToolBarState();
            }
        });

        ImageView ivBackArrow = (ImageView) view.findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked back arrow.");
                toggleToolBarState();
            }
        });

        setupContactList();

        remotesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                RemoteConfig yo = (RemoteConfig) parent.getItemAtPosition(position);
                Log.d(TAG, "listClick: clicked : " + yo.getName());

                //Used to switch between fragments in the current activity
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new AddDeviceFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return view;
    }

    // Initiate toggle (it means when you click the search icon it pops up the editText and clicking the back button goes to the search icon again)
    private void toggleToolBarState() {
        Log.d(TAG, "toggleToolBarState: toggling AppBarState.");
        if (mAppBarState == STANDARD_APPBAR) {
            setAppBarState(SEARCH_APPBAR);
        } else {
            setAppBarState(STANDARD_APPBAR);
        }
    }

    // Sets the appbar state for either search mode or standard mode.
    private void setAppBarState(int state) {

        Log.d(TAG, "setAppBaeState: changing app bar state to: " + state);

        mAppBarState = state;
        if (mAppBarState == STANDARD_APPBAR) {
            searchBar.setVisibility(View.GONE);
            viewContactsBar.setVisibility(View.VISIBLE);

            View view = getView();
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                im.hideSoftInputFromWindow(view.getWindowToken(), 0); // make keyboard hide
            } catch (NullPointerException e) {
                Log.d(TAG, "setAppBaeState: NullPointerException: " + e);
            }

        } else if (mAppBarState == SEARCH_APPBAR) {
            viewContactsBar.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); // make keyboard popup

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setAppBarState(STANDARD_APPBAR);
    }

    private void setupContactList() {
/*
        //dummy list, currently default list displayed in search until search is executed
        String[] remotes = {"directv/G051204",
                "directv/H23",
                "directv/HD20-100",
                "directv/RC16",
                "directv/RC24",
                "directv/RC32",
                "directv/RC64",

                "lg/42H3000",
                "lg/6710CDAL01G",
                "lg/6710CDAP01B",
                "lg/6711R1P072B",
                "lg/AKB33871420",
                "lg/AKB69680",
                "lg/AKB72915207",
                "lg/BD300",
                "lg/CC470TW",
                "lg/EC970W",
                "lg/EV230",
                "lg/MKJ32022805",
                "lg/PBAFA0189A",
                "lg/VF28",
                "panasonic/EUR511224",
                "panasonic/EUR511300",
                "panasonic/LSSQ0225",
                "panasonic/LSSQ0226",
                "panasonic/N2QADC000006",
                "panasonic/N2QAEC000012",
                "panasonic/NV-F65_HQ",
                "panasonic/NV-FJ610",
                "panasonic/NV-HS830",
                "panasonic/RAK-RX309W",
                "panasonic/RC331401",
                "panasonic/RC4346_01B ",
                "panasonic/RCR_195_DC1",
                "panasonic/RX-ED70",
                "panasonic/SA-AK25",
                "panasonic/SA-PM02",
                "panasonic/TC-21E1R",
                "panasonic/TNQ2637",
                "panasonic/TNQ8E0437"};


        final ArrayList<RemoteConfig> contacts = new ArrayList<>();
        for (int i = 0; i < remotes.length; i++) {
            contacts.add(new RemoteConfig(remotes[i]));
        }


*/
        submitSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String brandText = mBrandSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
                String modelText = mModelSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
                //adapter.clear();
//                String brandText = mBrandSearchContacts.getText().toString();
//                String modelText = mModelSearchContacts.getText().toString();
//                String fullText = "";
//                if (brandText.length() != 0 && modelText.length() != 0) {
//                    //adapter.filter(brandText.toLowerCase(Locale.getDefault()) + "/" + modelText.toLowerCase(Locale.getDefault()));
//                    fullText = brandText.toLowerCase(Locale.getDefault()) + "/" + modelText.toLowerCase(Locale.getDefault());
//                } else if (brandText.length() != 0 && modelText.length() == 0) {
//                   fullText = brandText.toLowerCase(Locale.getDefault());
//                } else if (brandText.length() == 0 && modelText.length() != 0) {
//                    fullText = modelText.toLowerCase(Locale.getDefault());
//                }
                //send search text to server
                Socket clientSocket = ((MainActivity)getActivity()).getClientSocket();

                HashMap<String, String> jsonMap = new HashMap<>();
                jsonMap.put("brand", brandText);
                jsonMap.put("model", modelText);

                JSONObject message = new JSONObject(jsonMap);
                clientSocket.emit("search_request", message.toString());
                Log.d("SOCKETIO", "Request emitted");

                //returning object from server
                clientSocket.on("search_results", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d("SOCKETIO", "Recieved response from server");
                        try {

                            JSONArray ja = (JSONArray) args[0];
                            final ArrayList<RemoteConfig> contacts = new ArrayList<>();
                            for(int i = 0; i < ja.length(); i++) {
                                JSONObject d = ja.getJSONObject(i);
                                String b = (String) d.get("brand");
                                String m = (String) d.get("device");
                                contacts.add(new RemoteConfig(b + " " + m));
                            }
                            adapter = new RemoteConfigListAdapter(getActivity(), R.layout.layout_remoteconfigs_listitem, contacts, "https://");
                            //updateRemoteList(contacts);
                            //setAppBarState(0);
                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                //setAppBarState(0);
                updateRemoteList();
                setAppBarState(0);
            }


        });

        //remotesList.setAdapter(adapter);

    }

    public void updateRemoteList() {
        remotesList.setAdapter(adapter);
    }


}