package com.indra.indra.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.indra.indra.MainActivity;
import com.indra.indra.R;
import com.indra.indra.models.RemoteButtonModel;
import com.indra.indra.models.RemoteModel;
import com.indra.indra.objects.RemoteButtonHandlerDaemon;
import com.indra.indra.ui.buttons.RemoteButton;
import com.indra.indra.ui.buttons.RemoteImageButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BasicDeviceFragment extends Fragment implements View.OnTouchListener {
    public String _deviceName;
    private RemoteModel baseDevice;
    private int layoutId;
    private RemoteButtonHandlerDaemon remoteButtonDaemon;

    public BasicDeviceFragment(RemoteModel basicDevice, int layoutId) {
        _deviceName = basicDevice.getDisplayName();
        this.baseDevice = basicDevice;
        this.layoutId = layoutId;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setMenuItemChecked(R.id.nav_remote);
        View inflatedFragment = inflater.inflate(layoutId, container, false);
        MainActivity activity = (MainActivity)getActivity();
        this.remoteButtonDaemon = RemoteButtonHandlerDaemon.getInstance(activity.getClientSocket(), activity);

        ImageButton settingsButton =  inflatedFragment.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Used to switch between fragments in the current activity
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();


                transaction.replace(R.id.fragment_container, new SettingsFragment(_deviceName)).commit();
            }
        });


        TextView tv = inflatedFragment.findViewById(R.id.remote_fragment_remote_name);
        tv.setText(baseDevice.getDisplayName());

        ArrayList<RemoteButtonModel> buttonModels = baseDevice.getButtonModels();
        TableLayout table = inflatedFragment.findViewById(R.id.bigButtonHolder);
//        TableRow row = new TableRow(getActivity());
//        int count = 0;
//        for(final RemoteButtonModel button : buttonModels) {
//            RemoteButton b = new RemoteButton(getActivity(), button.getLircName());
//            b.setText(button.getDisplayName());
//
//            int buttonWidth = (int)Math.ceil((getActivity().getWindow().getAttributes().width * .99) / 3);
//
////            b.setWidth(buttonWidth);
//            b.setOnTouchListener(this);
//
//            row.addView(b);
//            count++;
//            if (count == 3) {
//                count = 0;
//
//                table.addView(row);
//                row = new TableRow(getActivity());
//            }
//        }

        addButtonsToRemote(table);

//        ArrayList<View> buttons = inflatedFragment.getTouchables();


//        for(View button : buttons){
//
//            if(button instanceof RemoteImageButton || button instanceof RemoteButton){
//                button.setOnTouchListener(this);
//            }
//
//        }


        return inflatedFragment;
    }


    private void addButtonsToRemote(TableLayout tableLayout){
        ArrayList<RemoteButtonModel> powerButtons = new ArrayList<>();
        ArrayList<RemoteButtonModel> numberButtons = new ArrayList<>();
        boolean hasVolume = false;
        boolean hasChannel = false;

        boolean upArrow = false;
        boolean downArrow = false;
        boolean leftArrow = false;
        boolean rightArrow = false;
        boolean okButton = false;

        for(RemoteButtonModel buttonModel : baseDevice.getButtonModels()) {
            if (buttonModel.getLircName().contains("POWER")) {
                powerButtons.add(buttonModel);
            } else if (buttonModel.getLircName().contains("KEY_VOLUME")) {
                hasVolume = true;
            } else if (buttonModel.getLircName().contains("KEY_CHANNELUP") ||
                    buttonModel.getLircName().contains("KEY_CHANNELDOWN")) {
                hasChannel = true;
            } else if (buttonModel.getLircName().equals("KEY_UP")) {
                upArrow = true;
            } else if (buttonModel.getLircName().equals("KEY_DOWN")){
                downArrow = true;
            }else if (buttonModel.getLircName().equals("KEY_LEFT")) {
                leftArrow = true;
            } else if (buttonModel.getLircName().equals("KEY_RIGHT")){
                rightArrow = true;
            }else if (buttonModel.getLircName().equals("KEY_OK")){
                okButton = true;
            }else {
                try {
                    int parsed = Integer.parseInt(buttonModel.getDisplayName());
                    if (parsed <= 9 && parsed >= 0){
                        numberButtons.add(buttonModel);
                    }
                } catch (NumberFormatException e){ }
            }
        }

        boolean usingArrows = upArrow & downArrow & leftArrow & rightArrow;

        addPowerButtons(tableLayout, powerButtons);

        if(numberButtons.size() == 10){
            addNumberButtons(tableLayout, numberButtons);
        }



        addChannelsAndVolume(tableLayout, hasChannel, hasVolume);
        addArrows(tableLayout, usingArrows, okButton);


        ArrayList<RemoteButtonModel> buttonModels = baseDevice.getButtonModels();

        TableRow row = new TableRow(getActivity());
        int count = 0;
        int buttonWidth = (int)Math.ceil((getActivity().getWindow().getAttributes().width * .99) / 3);
        for(final RemoteButtonModel button : buttonModels) {
            boolean add = true;


//            b.setWidth(buttonWidth);


            if(powerButtons.size() > 0 && button.getLircName().contains("POWER")){
                add = false;
            }

            if(numberButtons.size() == 10){
                try {
                    int num = Integer.parseInt(button.getDisplayName());
                    if(num < 10){
                        add = false;
                    }
                } catch (NumberFormatException e) {}
            }

            if(button.getLircName().contains("VOLUME") || button.getLircName().contains("KEY_CHANNELUP")
                    || button.getLircName().contains("KEY_CHANNELDOWN")){
                add = false;
            }

            if((button.getLircName().equals("KEY_DOWN") || button.getLircName().equals("KEY_UP") ||
                    button.getLircName().equals("KEY_LEFT") || button.getLircName().equals("KEY_RIGHT") ||
                    button.getLircName().equals("KEY_OK")) && usingArrows){
                add = false;
            }

            if(add){
                RemoteButton b = new RemoteButton(getActivity(), button.getLircName());
                b.setOnTouchListener(this);
                b.setText(button.getDisplayName());
                row.addView(b);
                count++;
            }


            if (count == 3) {
                count = 0;

                tableLayout.addView(row);
                row = new TableRow(getActivity());
            }
        }

        if(count != 0){
            tableLayout.addView(row);
        }


    }


    private void addArrows(TableLayout tableLayout, boolean usingArrows, boolean usingOkButton){
        if(usingArrows){
            TableRow top = new TableRow(getActivity());
            TableRow mid = new TableRow(getActivity());
            TableRow bot = new TableRow(getActivity());

            TableRow.LayoutParams topBotParams = new TableRow.LayoutParams();
            topBotParams.column = 1;
            TableRow.LayoutParams midParam = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            midParam.column = 2;

            RemoteImageButton up = new RemoteImageButton(getActivity(),"KEY_UP");
            up.setImageResource(R.drawable.ic_arrow_up_black);
            up.setOnTouchListener(this);
            top.addView(up, topBotParams);

            RemoteImageButton left = new RemoteImageButton(getActivity(), "KEY_LEFT");
            left.setImageResource(R.drawable.ic_arrow_left_black);
            left.setOnTouchListener(this);
            left.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mid.addView(left);

            if(usingOkButton){
                RemoteButton ok = new RemoteButton(getActivity(), "KEY_OK");
                ok.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_enter_black, 0, 0);
                ok.setText("ENTER");
                ok.setOnTouchListener(this);
                mid.addView(ok);
            }

            RemoteImageButton right = new RemoteImageButton(getActivity(), "KEY_RIGHT");
            right.setImageResource(R.drawable.ic_arrow_right_black);
            right.setOnTouchListener(this);
            right.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mid.addView(right, midParam);


            RemoteImageButton down = new RemoteImageButton(getActivity(), "KEY_DOWN");
            down.setImageResource(R.drawable.ic_arrow_down_black);
            down.setOnTouchListener(this);
            bot.addView(down, topBotParams);


            tableLayout.addView(top);
            tableLayout.addView(mid);
            tableLayout.addView(bot);
        }
    }

    private void addNumberButtons(TableLayout tableLayout, ArrayList<RemoteButtonModel> numberButtons){
        TableRow tr = new TableRow(getActivity());
        for(int i = 0; i < numberButtons.size(); i++){
            RemoteButton button = new RemoteButton(getActivity(), numberButtons.get(i).getLircName());
            button.setText(numberButtons.get(i).getDisplayName());
            button.setOnTouchListener(this);
            if(i < 9) {
                tr.addView(button);
                if (i % 3 == 2) {
                    tableLayout.addView(tr);
                    tr = new TableRow(getActivity());
                }
            } else {

                TableRow.LayoutParams params = new TableRow.LayoutParams();
                params.column = 1;
                params.span = 1;

                tr.addView(button, params);
                tableLayout.addView(tr);
            }
        }
    }


    private void addChannelsAndVolume(TableLayout parent, boolean usingChannels, boolean usingVolume){

        TableRow top = new TableRow(getActivity());
        TableRow bottom = new TableRow(getActivity());

        RemoteButton increaseVol = new RemoteButton(getActivity(), "KEY_VOLUMEUP");
        increaseVol.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_vol_chan_up, 0 , 0);
        increaseVol.setText("VOL");
        increaseVol.setOnTouchListener(this);

        RemoteButton decreaseVol = new RemoteButton(getActivity(), "KEY_VOLUMEDOWN");
        decreaseVol.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0 , R.drawable.ic_vol_chan_down);
        decreaseVol.setText("VOL");
        decreaseVol.setOnTouchListener(this);

        RemoteButton increaseChannel = new RemoteButton(getActivity(), "KEY_CHANNELUP");
        increaseChannel.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_vol_chan_up, 0 , 0);
        increaseChannel.setText("CH");
        increaseChannel.setOnTouchListener(this);

        RemoteButton decreaseChannel = new RemoteButton(getActivity(), "KEY_CHANNELDOWN");
        decreaseChannel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0 , R.drawable.ic_vol_chan_down);
        decreaseChannel.setText("CH");
        decreaseChannel.setOnTouchListener(this);

        if(usingChannels && usingVolume){
            TableRow.LayoutParams volParams = new TableRow.LayoutParams();
            volParams.column = 0;
            volParams.span = 1;

            TableRow.LayoutParams chanParams = new TableRow.LayoutParams();
            chanParams.column = 2;
            volParams.span = 1;

            top.addView(increaseVol, volParams);
            top.addView(increaseChannel, chanParams);

            bottom.addView(decreaseVol, volParams);
            bottom.addView(decreaseChannel, chanParams);
        } else if(usingVolume){
            TableRow.LayoutParams volParams = new TableRow.LayoutParams();
            volParams.column = 0;
            volParams.span = 3;


            top.addView(increaseVol, volParams);
            bottom.addView(decreaseVol, volParams);
        } else if(usingChannels){
            TableRow.LayoutParams chanParams = new TableRow.LayoutParams();
            chanParams.column = 0;
            chanParams.span = 3;


            top.addView(increaseChannel,chanParams);
            bottom.addView(decreaseChannel, chanParams);
        }


        parent.addView(top);
        parent.addView(bottom);
    }


    private void addPowerButtons(TableLayout tableLayout, ArrayList<RemoteButtonModel> buttonModels){
        TableRow tr = new TableRow(getActivity());
        int count = 0;
        for(RemoteButtonModel buttonModel : buttonModels){
            RemoteImageButton powerButton = new RemoteImageButton(getActivity(), buttonModel.getLircName());
            powerButton.setImageResource(R.drawable.ic_power_black);

            if(buttonModel.getLircName().equals("KEY_POWER")){
                powerButton.setBackgroundColor(Color.RED);
            } else if (buttonModel.getLircName().equals("KEY_POWER2")){
                powerButton.setBackgroundColor(Color.GREEN);
            }

            powerButton.setOnTouchListener(this);

            tr.addView(powerButton);
            count++;

            if(count == 3){
                tableLayout.addView(tr);
                count = 0;
            }
        }

        if (count != 0){
            tableLayout.addView(tr);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getActionMasked();
        int daemonAction;

        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL){

            if(action == MotionEvent.ACTION_DOWN){
                daemonAction = RemoteButtonHandlerDaemon.BUTTON_EVENT_DOWN;
            } else {
                daemonAction = RemoteButtonHandlerDaemon.BUTTON_EVENT_UP;
            }

            Handler daemonHandler = remoteButtonDaemon.getButtonEventMessageHandler();
            String lircName = baseDevice.getLircName();
            String buttonName = v instanceof RemoteButton ? ((RemoteButton)v).getLircName() : ((RemoteImageButton) v).getLircName();

            Bundle buttonPressBundle = new Bundle();
            buttonPressBundle.putString(RemoteButtonHandlerDaemon.REMOTE_NAME_KEY, lircName);
            buttonPressBundle.putString(RemoteButtonHandlerDaemon.BUTTON_NAME_KEY, buttonName);
            buttonPressBundle.putInt(RemoteButtonHandlerDaemon.BUTTON_EVENT_KEY, daemonAction);
            Message msg = daemonHandler.obtainMessage();
            msg.setData(buttonPressBundle);
            daemonHandler.sendMessage(msg);
            return false;
        }


        return false;
    }

}
