package com.indra.indra;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.indra.indra.db.DatabaseUtil;
import com.indra.indra.fragments.AccountSettingsFragment;
import com.indra.indra.fragments.AddDeviceFragment;
import com.indra.indra.fragments.BasicDeviceFragment;
import com.indra.indra.fragments.MyDevicesFragment;
import com.indra.indra.fragments.ToolbarFragment;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.indra.indra.models.IpAddressModel;
import com.indra.indra.models.RemoteModel;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   AddDeviceFragment.OnFragmentInteractionListener{

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Socket clientSocket;
    private String ip,port;

    private String currentUser = DatabaseUtil.DEFAULT_USER;
    private String raspberryPiIP;

    DatabaseUtil db;

    private RemoteModel currentRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseUtil(this);

        IpAddressModel model = db.getIpForUser(currentUser);
        raspberryPiIP = model == null ? null : model.getIpAddress();
        Log.i("PI IP ADDRESS", raspberryPiIP == null ? "null" : raspberryPiIP);

//        ip = "12.0.0.1";
//        ip = "192.168.1.4";
//        ip = "cheshire.cse.buffalo.edu";

        ip = "fathomless-brook-21291.herokuapp.com";
//        port = "6969";
//        port = "8000";
//        port= "2680";
//        port = "443";

//        ip = "indra-272100.appspot.com";

        connectToServer();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        this.navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ArrayList<RemoteModel> remotes = db.getDevicesForUser(currentUser);
        currentRemote = remotes.size() == 0 ? null : remotes.get(0);

        //// Needs code to show the my devices fragment on startup ////

        if(savedInstanceState == null){
       // If the savedInstanceState is null then that means that the app is being loaded for the first time
            //Open MyDevicesFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new MyDevicesFragment()).commit();
            setMenuItemChecked(R.id.nav_my_devices);
        }

    }


    private boolean connectToServer() {
        try {
//            clientSocket = IO.socket("https://" + ip + ":" + port);
            clientSocket = IO.socket("https://" + ip);
            clientSocket.connect();

            Log.d("Connection Alerts","Successfully connected to server " + clientSocket.connected());
            return true;
        }
        catch (URISyntaxException e)
        {
            Log.d("Connection Alerts","Failed to connect to server");
            return false;
        }
    }

    public boolean socketSendToServer(String event, String message) {
        if(!clientSocket.connected()) {
            clientSocket.close();
            if(!connectToServer()) { //attempt reconnect, it failed
                Log.d("Connection Alerts", "Failed to send message, socket not connected");
                return false;
            }
        }
        //reaching this point means socket is connected and ready to transmit
        clientSocket.emit(event, message);
        Log.d("Connection Alerts", "Successfully sent message to server");
        return true;
    }
    /**
     * Overrides the default back button behavior. If the navigation menu is open and the Android
     * back button is pressed, the navigation menu will close. Otherwise it will do its normal behavior.
     */
    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            drawer.openDrawer(GravityCompat.START); //changed it so back button forces sidemenu to open, removes potential glitches w back button
            //super.onBackPressed();
        }
    }


    /**
     * Override of the method inherited from the OnNavigationItemSelectedListener interface.
     * Makes it easy to attach a listener to the navigation buttons in the navigation drawer.
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        //Used to switch between fragments in the current activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (menuItem.getItemId()){
            case R.id.nav_remote:
                if(currentRemote == null){
                    Toast.makeText(this, "There is no remote to open. Add a new one with Add New Device menu.", Toast.LENGTH_SHORT).show();
                    return false;
                } else if(raspberryPiIP == null) {
                    TextView msg = null;
                    try {
                        msg = findViewById(R.id.ip_address_display);
                    } catch (Exception e){}

                    editIpAddressDialog(getString(R.string.ip_missing_warning), msg);
                    return false;
                }else {
                    transaction.replace(R.id.fragment_container, new BasicDeviceFragment(currentRemote, R.layout.fragment_basic_device)).commit();
                }
                break;
            case R.id.nav_my_devices:
                transaction.replace(R.id.fragment_container, new MyDevicesFragment()).commit();
                break;
            case R.id.nav_account_settings:
                transaction.replace(R.id.fragment_container, new AccountSettingsFragment()).commit();
                break;
            case R.id.nav_add_device:

                if(raspberryPiIP == null) {
                    TextView msg = null;
                    try {
                        msg = findViewById(R.id.ip_address_display);
                    } catch (Exception e){}

                    editIpAddressDialog(getString(R.string.ip_missing_warning), msg);
                    return false;
                }

                ToolbarFragment fragment = new ToolbarFragment();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.logout:
                // this will clear the back stack and displays no animation on the screen
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    /**
     * Helper method to set a menu item to be checked
     * @param resourceId - The ID of the MenuItem that you would like to be checked
     */
    public void setMenuItemChecked(int resourceId){
        MenuItem checked = navigationView.getCheckedItem();

        if (checked == null || checked.getItemId() != resourceId){
            navigationView.setCheckedItem(resourceId);
        }
    }


    /**
     * Helper method to uncheck all of the menu items.
     */
    public void uncheckAllMenuItems(){
        MenuItem checked = navigationView.getCheckedItem();

        if(checked != null){
            checked.setChecked(false);
        }
    }

    public void lockNavigationDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void unlockNavigationDrawer(){
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

@Override
    public void onFragmentInteraction(Uri uri){
        //you can leave this empty
    }


    public void openDeviceFragment(RemoteModel model){
        setCurrentRemote(model);
        setMenuItemChecked(R.id.nav_remote);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment nextActiveFragment = new BasicDeviceFragment(model, R.layout.fragment_basic_device);
        transaction.replace(R.id.fragment_container, nextActiveFragment).commit();
    }


    public Socket getClientSocket(){
        return clientSocket;
    }

    public DatabaseUtil getDb() {return db; }

    public void setCurrentRemote(RemoteModel currentRemote){ this.currentRemote = currentRemote; }

    public String getRaspberryPiIP() {
        return raspberryPiIP;
    }


    public void editIpAddressDialog(String message, TextView msgBox){
        AlertDialog.Builder editIpDialogBuilder = new AlertDialog.Builder(this);
        editIpDialogBuilder.setTitle("Edit Raspberry Pi IP Address");
        editIpDialogBuilder.setMessage(message);

        final EditText ipInput = new EditText(this);
        ipInput.setInputType(InputType.TYPE_CLASS_PHONE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ipInput.setLayoutParams(lp);

        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; ++i)
            {
                if (!Pattern.compile("[1234567890.]*").matcher(String.valueOf(source.charAt(i))).matches())
                {
                    return "";
                }
            }

            return null;
        };

        ipInput.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(15)});
//        ipInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});

        editIpDialogBuilder.setView(ipInput);
        editIpDialogBuilder.setPositiveButton("Update", (dialog, which) -> {

        });
        editIpDialogBuilder.setNegativeButton("Cancel", ((dialog, which) -> {}));

        AlertDialog editIpDialog = editIpDialogBuilder.show();
        editIpDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String input = ipInput.getText().toString();

            if(Patterns.IP_ADDRESS.matcher(input).matches()){
                raspberryPiIP = input;
                if(msgBox != null){
                    msgBox.setText("IP Address: " + raspberryPiIP);
                }
                db.updateIpRow(input, currentUser);
                editIpDialog.dismiss();
            } else {
                Toast t = Toast.makeText(this, "Input was not a valid IP Address.", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 20);
                t.show();
            }

        });



    }
}
