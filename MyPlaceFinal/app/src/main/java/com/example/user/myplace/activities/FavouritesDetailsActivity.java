package com.example.user.myplace.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.myplace.Place;
import com.example.user.myplace.R;

/**
 * Created by user on 31/12/2016.
 */

public class FavouritesDetailsActivity extends AppCompatActivity {

    private TextView fName, fLocation, fDistanceText;
    private ImageView fPic;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_list_item);

        sp = getSharedPreferences("PlaceSp", MODE_PRIVATE);
        fName = (TextView) findViewById(R.id.textView_fav_name);
        fLocation = (TextView) findViewById(R.id.textView_fav_address);
        fDistanceText = (TextView) findViewById(R.id.textView_fav_distance);
        float distance = sp.getFloat("distance", 0);
        fPic = (ImageView) findViewById(R.id.imageView_list_item_fav);

        Place place = getIntent().getParcelableExtra("saved_place");
        if (place != null) {
            fName.setText(place.getName());
            fLocation.setText(place.getAddress());
            String dis = Float.valueOf(distance).toString();
            fDistanceText.setText(dis);
            fPic.setImageBitmap(place.getPic());
        }
    }
}
