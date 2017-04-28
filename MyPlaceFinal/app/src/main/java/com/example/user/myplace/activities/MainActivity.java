package com.example.user.myplace.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.user.myplace.fragments.MapFragment;
import com.example.user.myplace.PlacesAdapter;
import com.example.user.myplace.PlacesDBHelper;
import com.example.user.myplace.PowerConnectionReceiver;
import com.example.user.myplace.R;
import com.example.user.myplace.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private MapFragment mfrag;
    private SearchFragment sfrag;
    private LocationManager locationManager;
    private PowerConnectionReceiver powerReceiver;
    private PlacesDBHelper helper;
    private SharedPreferences sp;

    public boolean tablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initiate receiver for charge device check
        powerReceiver = new PowerConnectionReceiver();

        // register receiver & adding according actions
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(powerReceiver, ifilter);

        tablet = getResources().getBoolean(R.bool.is_tablet);

        if(tablet==false) {
            // Check that the activity is using the layout version with the fragment container FrameLayout
            if (findViewById(R.id.root_container) != null) {

                // However, if being restored from a previous state, then no need to do anything and should return or else fragments could overlap.
                if (savedInstanceState == null) {

                    // Create new Fragments to be placed in the activity layout
                    sfrag = new SearchFragment();
                    mfrag = new MapFragment();

                    // Add search fragment to the front of 'fragment container' FrameLayout, hide map frag
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.root_container, sfrag, "search")
                            .add(R.id.root_container, mfrag, "map")
                            .hide(mfrag)
                            .commit();
                }
            }

        } else {  // if is a tablet
            if (savedInstanceState == null) {
                sfrag = new SearchFragment();
                mfrag = new MapFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.search_container, sfrag, "search")
                        .add(R.id.map_container, mfrag, "map")
                        .commit();
            }
        }

        // get the location manager service
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // initiate my  custom shared preferences
        sp = getSharedPreferences("PlaceSp", MODE_PRIVATE);
        // initiate the PlacesDBHelper
        helper = new PlacesDBHelper(this);

        // runtime permission check (used in Android 6 and above)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if no permission to location ask permission from the user (the built in dialog)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
            return;
        }
        // start listening to location
        // 3000 means every 3 seconds, 50 means every 50 meters moved
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 50, this);
    }

    // create the options menu
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate the menu resource file into menu object
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // what to do when user clicks on menu items
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // in case user chose delete
            case R.id.item_delete_favs:
                try {
                    helper.deleteAllFavPlaces();
                } catch (NullPointerException e) {
                    Toast.makeText(this, "There are no places in your favourites list yet", Toast.LENGTH_SHORT).show();
                }
                break;

            // Launch preference screen
            case R.id.item_settings:
                Intent intentPref = new Intent(this, PreferencesActivity.class);
                startActivity(intentPref);
                break;

            // Launch Favourites activity
            case R.id.item_menu_favs:
                Intent intentFavs = new Intent(this, Favourites.class);
                startActivity(intentFavs);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // track changing location
    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        sp.edit().putFloat("mylat", (float) latitude).apply();
        sp.edit().putFloat("mylng", (float) longitude).apply();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    // disconnect receiver for charge device check
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(powerReceiver);
    }
}

