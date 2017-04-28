package com.example.user.myplace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by jbt on 12/13/2016.
 */

public class PlacesDBHelper extends SQLiteOpenHelper {

    public static final String RECENT_TABLE_NAME = "recent";
    public static final String RECENT_COL_ID = "id";
    public static final String RECENT_COL_NAME = "name";
    public static final String RECENT_COL_LAT = "lng";
    public static final String RECENT_COL_LNG = "lat";
    public static final String RECENT_COL_ADDRESS = "address";
    public static final String RECENT_COL_PIC = "pic";

    public static final String FAV_TABLE_NAME = "favourites";
    public static final String FAV_COL_ID = "id";
    public static final String FAV_COL_NAME = "name";
    public static final String FAV_COL_LAT = "lng";
    public static final String FAV_COL_LNG = "lat";
    public static final String FAV_COL_ADDRESS = "address";
    public static final String FAV_COL_PIC = "pic";

    public PlacesDBHelper(Context context) {
        super(context, "places.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // create table
        String sql = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s REAL, %s REAL, %s TEXT, %s BLOB )",
                RECENT_TABLE_NAME, RECENT_COL_ID, RECENT_COL_NAME, RECENT_COL_LAT, RECENT_COL_LNG, RECENT_COL_ADDRESS, RECENT_COL_PIC);
        sqLiteDatabase.execSQL(sql);

        // create second table
        sql = String.format("CREATE TABLE %s (  %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s REAL, %s REAL, %s TEXT, %s BLOB )",
                FAV_TABLE_NAME, FAV_COL_ID, FAV_COL_NAME, FAV_COL_LAT, FAV_COL_LNG, FAV_COL_ADDRESS, FAV_COL_PIC);
        sqLiteDatabase.execSQL(sql);

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

             /*****************
              ****COMMANDS*****
              *****************/

    //        First Table commands

    public void addPlace(Place place, SQLiteDatabase sqLiteDatabase) {

        // map of columns and values in the table
        ContentValues values = new ContentValues();

        // get reference to the database
        sqLiteDatabase = getWritableDatabase();

        values.put(RECENT_COL_NAME, place.getName());
        values.put(RECENT_COL_ADDRESS, place.getAddress());
        values.put(RECENT_COL_LAT, place.getLat());
        values.put(RECENT_COL_LNG, place.getLng());

        if (place.getPic() != null) {
            values.put(RECENT_COL_PIC, saveBitmapToDB(place.getPic()));
        }
        // insert the new place values to the table places
        sqLiteDatabase.insert(RECENT_TABLE_NAME, null, values);

        sqLiteDatabase.close();
    }


    public ArrayList<Place> getAllPlaces() {
        ArrayList<Place> places = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor c = sqLiteDatabase.query(RECENT_TABLE_NAME, null, null, null, null, null, null);

        // loop while there are rows in the cursor
        while (c.moveToNext()) {

            long id = c.getLong(c.getColumnIndex(RECENT_COL_ID));
            String address = c.getString(c.getColumnIndex(RECENT_COL_ADDRESS));
            String name = c.getString(c.getColumnIndex(RECENT_COL_NAME));
            double lat = c.getDouble(c.getColumnIndex(RECENT_COL_LAT));
            double lng = c.getDouble(c.getColumnIndex(RECENT_COL_LNG));
            byte[] blob = c.getBlob(c.getColumnIndex(RECENT_COL_PIC));
            Bitmap pic = null;
            if (blob != null) {
                pic = getImageFromDB(blob);
            }
            places.add(new Place(id, name, address, lat, lng, pic));
        }
        c.close();
        sqLiteDatabase.close();
        return places;
    }

    public void addAllPlaces(ArrayList<Place> placesArray) {
        // get reference to the database
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        for (Place p : placesArray) {
            addPlace(p, sqLiteDatabase);
        }
        // close the database connection
        sqLiteDatabase.close();
    }

    public void deleteAllPlaces() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(RECENT_TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }

    //        Favourites Table


    public void addFav(Place place) {

        // map of columns and values in the table
        ContentValues values = new ContentValues();

        // get reference to the database
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        values.put(FAV_COL_NAME, place.getName());
        values.put(FAV_COL_ADDRESS, place.getAddress());
        values.put(FAV_COL_LAT, place.getLat());
        values.put(FAV_COL_LNG, place.getLng());

        if (place.getPic() != null) {
            values.put(FAV_COL_PIC, saveBitmapToDB(place.getPic()));
        }

        // insert the new place values to the table places
        sqLiteDatabase.insert(FAV_TABLE_NAME, null, values);
    }

    public ArrayList<Place> getAllFavs() {
        ArrayList<Place> favPlaces = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor c = sqLiteDatabase.query(FAV_TABLE_NAME, null, null, null, null, null, null);

        // loop while there are rows in the cursor
        while (c.moveToNext()) {

            long id = c.getLong(c.getColumnIndex(FAV_COL_ID));
            String address = c.getString(c.getColumnIndex(FAV_COL_ADDRESS));
            String name = c.getString(c.getColumnIndex(FAV_COL_NAME));
            double lat = c.getDouble(c.getColumnIndex(FAV_COL_LAT));
            double lng = c.getDouble(c.getColumnIndex(FAV_COL_LNG));
            byte[] blob = c.getBlob(c.getColumnIndex(FAV_COL_PIC));
            Bitmap pic = null;
            if (blob != null) {
                pic = getImageFromDB(blob);
            }
            favPlaces.add(new Place(name, address, lat, lng, pic));
        }
        c.close();
        sqLiteDatabase.close();
        return favPlaces;
    }

    public void deleteAllFavPlaces() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(FAV_TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }

    public void addAllFavPlaces(ArrayList<Place> favsArray) {
        // get reference to the database
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        for (Place p : favsArray) {
            addPlace(p, sqLiteDatabase);
        }
        // close the database connection
        sqLiteDatabase.close();
    }


    // handling picture

    private byte[] saveBitmapToDB(Bitmap picture) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }


    private Bitmap getImageFromDB(byte[] blob) {

        ByteArrayInputStream imageStream = new ByteArrayInputStream(blob);
        return BitmapFactory.decodeStream(imageStream);
    }
}