package com.indra.indra;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        //// Needs code to show the my devices fragment on startup ////
        /*
        if(savedInstanceState == null){
        ..... Open My Devices Fragment ....
        If the savedInstanceState is null then that means that the app is being loaded for the first time
        }

         */

    }


    /**
     * Overrides the default back button behavior. If the navigation menu is open and the Android
     * back button is pressed, the navigation menu will close. Otherwise it will do its normal behavior.
     */
    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
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
                transaction.replace(R.id.fragment_container, new RemoteFragment()).commit();
                break;
            case R.id.nav_my_devices:
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
}
