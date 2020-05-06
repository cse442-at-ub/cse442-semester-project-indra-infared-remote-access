package com.indra.indra.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.indra.indra.MainActivity;
import com.indra.indra.R;
import com.indra.indra.models.RemoteButtonModel;
import com.indra.indra.models.RemoteModel;
import com.indra.indra.ui.buttons.RemoteButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddDeviceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddDeviceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private TextView remoteConfigText;

    // TODO: Rename and change types of parameters
    private String deviceName;


    private OnFragmentInteractionListener mListener;

    public AddDeviceFragment(String deviceN) {
        // Required empty public constructor
        deviceName = deviceN;
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param deviceName name of device being added
     * @return A new instance of fragment AddDeviceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddDeviceFragment newInstance(String deviceName) {
        AddDeviceFragment fragment = new AddDeviceFragment(deviceName);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, deviceName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deviceName = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedFragment = inflater.inflate(R.layout.fragment_add_device, container, false);

        remoteConfigText = inflatedFragment.findViewById(R.id.configText);
        final Button bAddDevice = inflatedFragment.findViewById(R.id.addDevice);


            //send search text to server
            Socket clientSocket = ((MainActivity) getActivity()).getClientSocket();

            HashMap<String, String> jsonMap = new HashMap<>();
            String[] inputs = deviceName.split("\\s+");

              jsonMap.put("brand", inputs[0]);
        jsonMap.put("model", inputs[1]);
        jsonMap.put("id", clientSocket.id());
        jsonMap.put("ipAddress", ((MainActivity) getActivity()).getRaspberryPiIP());
        jsonMap.put("username", ((MainActivity) getActivity()).getCurrentUser());

        JSONObject message = new JSONObject(jsonMap);
        clientSocket.emit("file_request", message.toString());
        Log.d("FileSearch", "Request emitted");

            //recieve response
            clientSocket.on("file_response", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("FileSearch", "Recieved response from server");
                    try {
                        JSONObject jo = (JSONObject) args[0];
                        JSONObject contents = (JSONObject) jo.get("file_contents");
                        final String lircFileName = (String) ((JSONArray) contents.get("name")).get(0);
                        JSONObject buttonsList = (JSONObject) contents.get("buttons");
                        final ArrayList<RemoteButtonModel> rbuttons = new ArrayList<RemoteButtonModel>();

                        Iterator<String> keysItr = buttonsList.keys();
                        while (keysItr.hasNext()) {
                            String key = keysItr.next();
                            String dName = getDisplayNameFromLIRCName(key);
                            rbuttons.add(new RemoteButtonModel(dName, key, 000, 000));
                        }

                        Log.d("FileSearch", "Finished processing response");
                        updateRemoteConfig(lircFileName, rbuttons);
                        bAddDevice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                RemoteModel rm = new RemoteModel(deviceName, lircFileName);
                                rm.setButtonModels(rbuttons);
                                ((MainActivity) getActivity()).getDb().insertDeviceToDatabase(rm);
                                getActivity().runOnUiThread(new Runnable() { //asks UI thread to change UI so handler thread does not conflict
                                    @Override
                                    public void run() {
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.replace(R.id.fragment_container, new MyDevicesFragment());
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                });
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        ((MainActivity) getActivity()).setMenuItemChecked(R.id.nav_add_device);
        return inflatedFragment;
    }

    private String getDisplayNameFromLIRCName(String key) {
        String dName;

        if (key.contains("KEY_")){
            dName = key.replaceFirst("KEY_", "");
        } else if(key.contains("BTN_")){
            dName = key.replaceFirst("BTN_", "");
        } else {
            return key;
        }


        dName = dName.replace("_", " ");

        switch (dName){
            case "CHANNELDOWN":
                dName = "CH. DOWN";
                break;
            case "CHANNELUP":
                dName = "CH. UP";
                break;
            case "VOLUMEUP":
                dName = "VOL. UP";
                break;
            case "VOLUMEDOWN":
                dName = "VOL. DOWN";
        }
        return dName;
    }

    public void updateRemoteConfig(final String lircFileN, final ArrayList<RemoteButtonModel> rb)
    {
        getActivity().runOnUiThread(new Runnable() { //asks UI thread to change UI so handler thread does not conflict
            @Override
            public void run() {
                String display = "Device name: " + lircFileN + "\n" + "Buttons: " + "\n";
                for(RemoteButtonModel each : rb) {
                    display = display + each.getDisplayName() + "\n";
                }
                remoteConfigText.setText(display);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
