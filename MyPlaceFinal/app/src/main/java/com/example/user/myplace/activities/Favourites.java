package com.example.user.myplace.activities;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.user.myplace.FavsAdapter;
import com.example.user.myplace.Place;
import com.example.user.myplace.PlacesDBHelper;
import com.example.user.myplace.R;

import java.util.ArrayList;

public class Favourites extends AppCompatActivity {

    private PlacesDBHelper helper;
    private FavsAdapter favsAdapter;
    private ArrayList<Place> favPlaces;
    private RecyclerView favsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites);

        favPlaces = new ArrayList<>();
        favsAdapter = new FavsAdapter(this, favPlaces);
        helper = new PlacesDBHelper(this);

        // recycler
        favsList = (RecyclerView) findViewById(R.id.favs_list);
        favsList.setLayoutManager(new LinearLayoutManager(this));
        favsList.setAdapter(favsAdapter);

        // check phone orientation and change the RecyclerLayout
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            favsList.setLayoutManager(new LinearLayoutManager(this));
        } else {
            favsList.setLayoutManager(new LinearLayoutManager(this));
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        favsAdapter.clearAllFavs();
        favsAdapter.addAllFavs(helper.getAllFavs());
        favsAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
