package com.indra.indra.fragments;

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

import com.indra.indra.R;
import com.indra.indra.db.DatabaseUtil;
import com.indra.indra.models.RemoteButtonModel;
import com.indra.indra.models.RemoteModel;
import com.indra.indra.ui.buttons.MyDeviceMenuButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountSettingsFragment extends Fragment {
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

        TextView mAccountName = inflatedFragment.findViewById(R.id.account_name);
        mAccountName.setText(DatabaseUtil.DEFAULT_USER);

        TableLayout devicesTable = inflatedFragment.findViewById(R.id.account_settings_devices);
        DatabaseUtil util = new DatabaseUtil(getActivity());

        ArrayList<RemoteModel> remoteModels = util.getDevicesForUser(DatabaseUtil.DEFAULT_USER);

        for(final RemoteModel deviceClass : remoteModels){
            TableRow row = new TableRow(getActivity());

            //TODO: fix width
            TextView remoteName = new TextView(getActivity());
            remoteName.setText(deviceClass.getDisplayName());
            //remoteName.setWidth((getActivity().getWindow().getAttributes().width)*(4/5));

            row.addView(remoteName);

            Button b = new Button(getActivity());
            b.setText("edit");
            //b.setWidth((getActivity().getWindow().getAttributes().width)/5);

            row.addView(b);

            devicesTable.addView(row);
        }
        return inflatedFragment;
    }
}
