package com.example.user.myplace;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MyIntentService extends IntentService {

    private SharedPreferences sp;
    public SharedPreferences defaultSP;
    PlacesDBHelper helper;
    Context context;


    public static final String SEARCH_END_ACTION = "com.example.user.myplace.search_end_action";
    private static final String PLACE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=%s&keyword=%s&key=AIzaSyC39vBwNiLWYxGax8H7P9WDDy8HRg2Rs-I";
    private static final String PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=AIzaSyC39vBwNiLWYxGax8H7P9WDDy8HRg2Rs-I";

    public MyIntentService() {
        super("MyIntentService");
    }


    // this method runs on new thread
    @Override
    protected void onHandleIntent(Intent intent) {

        helper = new PlacesDBHelper(this);
        context = this.context;
        sp = getSharedPreferences("PlaceSp",MODE_PRIVATE);
        defaultSP = PreferenceManager.getDefaultSharedPreferences(this);
        ArrayList<Place> placesArray = new ArrayList<>();

        String searchKey = intent.getStringExtra("search_key");

        float mylat = intent.getFloatExtra("mylat", 32.0631864f);
        float mylng =intent.getFloatExtra("mylng", 34.7906493f);
        float placeLat, placeLng = 0;

        float radius = Float.parseFloat(defaultSP.getString("radius_key", "3"));

        String units = defaultSP.getString("units_key", "km");
        radius*=1000;
        if(units.equals("miles")){
            radius*=0.621371;
        }
        String name = null;
        String address = null;
        Bitmap pic = null;
        double distance = 0;

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();

        URL url = null;

        try {
            url = new URL(String.format(PLACE_URL, mylat, mylng, radius, searchKey));
            connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Toast.makeText(this, R.string.offline_message, Toast.LENGTH_LONG).show();
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();

            }

            JSONObject root = new JSONObject(builder.toString());
            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject placeObj = results.getJSONObject(i);
                name = placeObj.getString("name");
                address = placeObj.getString("vicinity");
                placeLat = (float) placeObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                placeLng = (float) placeObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                String photo_reference = "";
                if (placeObj.has("photos")) {
                    photo_reference = placeObj.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                    String photo = String.format(PHOTO_URL, photo_reference);
                    URL urlPic = new URL(photo);

                    connection = (HttpURLConnection) urlPic.openConnection();
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                    // convert the bytes stream from the web into a Bitmap object
                    pic = BitmapFactory.decodeStream(connection.getInputStream());
                }
                Place place = new Place (name, address, placeLat, placeLng, pic);
                placesArray.add(place);
            }
           // helper.deleteAllPlaces();
            helper.addAllPlaces(placesArray);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // send intent with broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SEARCH_END_ACTION));

    }

}





