package com.indra.indra;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.indra.indra.fragments.AddDeviceFragment;
import com.indra.indra.fragments.BasicDeviceFragment;
import com.indra.indra.fragments.MyDevicesFragment;
import com.indra.indra.fragments.RemoteFragment;
import com.indra.indra.fragments.ToolbarFragment;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.indra.indra.objects.BaseDeviceClass;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   AddDeviceFragment.OnFragmentInteractionListener{

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Socket clientSocket;
    private String ip,port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ip = "12.0.0.1";
        ip = "192.168.1.4";
//        port = "6969";
        port = "8000";
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
            clientSocket = IO.socket("http://" + ip + ":" + port);
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

    public boolean socketSendToServer(String message) {
        if(!clientSocket.connected()) {
            clientSocket.close();
            if(!connectToServer()) { //attempt reconnect, it failed
                Log.d("Connection Alerts", "Failed to send message, socket not connected");
                return false;
            }
        }
        //reaching this point means socket is connected and ready to transmit
        clientSocket.send(message);
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
                transaction.replace(R.id.fragment_container, new BasicDeviceFragment(new BaseDeviceClass(getString(R.string.living_room_tv), "SamsungBN59-01054A"), R.layout.fragment_default_tv_remote)).commit();
                break;
            case R.id.nav_my_devices:
                transaction.replace(R.id.fragment_container, new MyDevicesFragment()).commit();
                break;
            case R.id.nav_add_device:
                ToolbarFragment fragment = new ToolbarFragment();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
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


@Override
    public void onFragmentInteraction(Uri uri){
        //you can leave this empty
    }


    public Socket getClientSocket(){
        return clientSocket;
    }

}
