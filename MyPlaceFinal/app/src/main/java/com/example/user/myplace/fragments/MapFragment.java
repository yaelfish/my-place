package com.example.user.myplace.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.myplace.PlacesAdapter;
import com.example.user.myplace.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.MODE_PRIVATE;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private LatLng placelatLng, myLatLng;
    private MapReceiver mapReceiver;
    private Marker marker;
    private Circle circle;
    private float placeLat, placeLng;
    private SharedPreferences sp;
    private SharedPreferences defaultSP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inflate the layout for this fragment, connect and show map
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //get map
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        // create receiver
         mapReceiver = new MapReceiver();

        // create filter
        IntentFilter filter = new IntentFilter(getString(R.string.map_broadcast));

        // register the receiver
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mapReceiver, filter);

        //connect to my custom created sp
        sp = getContext().getSharedPreferences("PlaceSp", MODE_PRIVATE);
        return view;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // implement the interface, making the map clickable
        mMap.setOnMapClickListener(this);

        // check if we have permission for location
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        // change the map type (satellite, normal, hybrid)
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        placelatLng = new LatLng(sp.getFloat("ClickedPlaceLat", 34.65f), sp.getFloat("ClickedPlaceLng",32.25f));
        myLatLng = new LatLng(sp.getFloat("mylat", 34.65f),  sp.getFloat("mylng",32.25f));
        String name = sp.getString("place_name", "Going Places!");

        // if last marker exists - remove it
        if(marker != null)
            marker.remove();

        // if last circle exists - remove it
        if(circle != null)
            circle.remove();

        // add marker
        marker = mMap.addMarker(new MarkerOptions().position(placelatLng).title(name).alpha(0.7f));

        // add circle
        circle = mMap.addCircle(new CircleOptions()
                .center(placelatLng)
                .radius(200).fillColor(Color.parseColor("#644FC4F6"))
                .strokeColor(Color.BLUE));

        // animate the camera
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placelatLng, 15));
    }

    public class MapReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            placeLat = sp.getFloat("ClickedPlaceLat", 34.65f);
            placeLng = sp.getFloat("ClickedPlaceLng", 32.25f);
            String name = sp.getString("place_name", "Going Places!");

            placelatLng = new LatLng(placeLat, placeLng);

            // if last marker exists - remove it
            if(marker != null)
                marker.remove();

            if(circle != null)
                circle.remove();

            // add marker
            marker = mMap.addMarker(new MarkerOptions().position(placelatLng).title(name).alpha(0.7f));

            // add circle
            circle = mMap.addCircle(new CircleOptions()
                    .center(placelatLng)
                    .radius(200).fillColor(Color.parseColor("#644FC4F6"))
                    .strokeColor(Color.BLUE));

            // animate the camera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placelatLng, 15));
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // show the clicked location with a Toast
        Toast.makeText(this.getContext(), "Lat: " + String.format("%.3f", latLng.latitude ) + ", Lon: " + String.format("%.3f",  latLng.longitude ), Toast.LENGTH_SHORT).show();

        // calculate the distance from my place to clicked location and show with a Toast
        double distance = PlacesAdapter.distance(myLatLng.latitude, latLng.latitude, myLatLng.longitude, latLng.longitude, 0, 0);
        //connect to the default sp
        defaultSP = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String units =defaultSP.getString("units_key", "km");
        if (units.equals("miles")) {
            //convert distance to miles
            distance = distance * 0.621371;
        }
        Toast.makeText(this.getContext(), "Distance from my spot: " + String.format("%.2f", distance ) , Toast.LENGTH_SHORT).show();
    }

}
