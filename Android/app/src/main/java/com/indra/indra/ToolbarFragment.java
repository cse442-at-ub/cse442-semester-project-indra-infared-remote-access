package com.indra.indra;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Locale;

public class ToolbarFragment extends Fragment {

    private static final String TAG = "ToolbarFragment";
    private static final int STANDARD_APPBAR = 0;
    private static final int SEARCH_APPBAR = 1;
    private int mAppBarState;
    private ListView remotesList;

    EditText mSearchContacts;

    private AppBarLayout viewContactsBar, searchBar;
    private RemoteConfigListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewremotes, container, false);
        viewContactsBar = (AppBarLayout) view.findViewById(R.id.viewRemotesToolbar);
        searchBar = (AppBarLayout) view.findViewById(R.id.searchToolbar);
        remotesList = view.findViewById(R.id.remotesList);
        mSearchContacts = view.findViewById(R.id.etSearchContacts);

        Log.d(TAG, "onCreateView: started");

        setAppBarState(STANDARD_APPBAR);


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
        for (int i=0; i < remotes.length; i++) {
            contacts.add(new RemoteConfig(remotes[i]));
        }

        adapter = new RemoteConfigListAdapter(getActivity(), R.layout.layout_remoteconfigs_listitem, contacts, "https://");

        mSearchContacts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = mSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        remotesList.setAdapter(adapter);

    }

}