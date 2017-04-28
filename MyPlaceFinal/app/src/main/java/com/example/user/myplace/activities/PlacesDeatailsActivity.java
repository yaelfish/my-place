package com.example.user.myplace.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.myplace.Place;
import com.example.user.myplace.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by user on 24/12/2016.
 */

public class PlacesDeatailsActivity extends AppCompatActivity {

    private TextView name, location, distanceText;
    private ImageView pic;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_list_item);

        sp = getSharedPreferences("PlaceSp", MODE_PRIVATE);
        name = (TextView) findViewById(R.id.textView_place_name);
        location = (TextView) findViewById(R.id.textView_place_address);
        distanceText = (TextView) findViewById(R.id.textView_place_distance);
        float distance = sp.getFloat("distance", Float.parseFloat("unknown"));
        pic = (ImageView) findViewById(R.id.imageView_list_item);

        Place place = getIntent().getParcelableExtra("place");
        if (place != null) {
            name.setText(place.getName());
            location.setText(place.getAddress());
            String dis = Float.valueOf(distance).toString();
            distanceText.setText(dis);
            pic.setImageBitmap(place.getPic());
        }
    }
}
