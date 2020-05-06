package com.indra.indra.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.indra.indra.MainActivity;
import com.indra.indra.R;

import java.util.ArrayList;

public class RemoteFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setMenuItemChecked(R.id.nav_remote);
        View inflatedFragment = inflater.inflate(R.layout.fragment_default_tv_remote, container, false);

        //inflatedFragment.findViewById(R.id.settingsButton).setOnClickListener(this);
        //all touchable objects in remote get assigned to onClick() method as listener (all buttons)
        ArrayList<View> allButtons = ((TableLayout)(inflatedFragment.findViewById(R.id.bigButtonHolder))).getTouchables();
//        for(View b : allButtons) {
//           b.setOnClickListener(this);

        return inflatedFragment;
    }

}
