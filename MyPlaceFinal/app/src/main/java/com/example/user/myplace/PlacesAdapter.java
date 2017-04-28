package com.example.user.myplace;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myplace.activities.MainActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jbt on 12/11/2016.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesHolder>{

    protected AlertDialog dialogLongClick;
    private Context context;
    private ArrayList<Place> data = new ArrayList<>();
    private android.support.v4.app.Fragment mfrag;
    private android.support.v4.app.Fragment sfrag;
    private SharedPreferences sp;
    private SharedPreferences defaultSP;
    private int pos;
    private float myLat, myLong;
    private PlacesDBHelper helper;
    public boolean tablet;

    // constructor
    public PlacesAdapter(Context context, ArrayList<Place> data) {
        this.context = context;
        this.data = data;
        helper= new PlacesDBHelper(context);
        sp = context.getSharedPreferences("PlaceSp", MODE_PRIVATE);
        defaultSP = PreferenceManager.getDefaultSharedPreferences(this.context);
        myLat = sp.getFloat("mylat", 0);
        myLong = sp.getFloat("mylng", 0);
    }

    @Override
    public PlacesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.places_list_item, null);
        return new PlacesHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesHolder holder, int position) {
        pos = position;
        Place place = data.get(pos);
        holder.bind(place);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    ////////   ------    Methods --------- //////////

    public void clear() {
        if (data != null){
        data.clear(); }
        // refresh the list because the data was changed
        notifyDataSetChanged();
    }

    public void add(Place place) {
        data.add(place);
        // refresh the list because the data was changed
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Place> data) {
        this.data.addAll(data);
        // refresh the list because the data was changed
        notifyDataSetChanged();

    }


    public class PlacesHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener, AlertDialog.OnClickListener {

        private ImageView picture;
        private TextView textName, textLocation, textDistance;
        private Place place;

        public PlacesHolder(View itemView) {
            super(itemView);

            picture = (ImageView) itemView.findViewById(R.id.imageView_list_item);
            textName = (TextView) itemView.findViewById(R.id.textView_place_name);
            textLocation = (TextView) itemView.findViewById(R.id.textView_place_address);
            textDistance = (TextView) itemView.findViewById(R.id.textView_place_distance);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            picture.setOnClickListener(this);
        }

        public void bind(Place place) {
            this.place = place;
            double distance =distance(myLat, place.getLat(),myLong,place.getLng(),0,0);
            String units =defaultSP.getString("units_key", "km");
            if (units.equals("miles")){
                distance=distance*0.621371;
            }

            textName.setText(place.getName());
            textLocation.setText(place.getAddress());

            textDistance.setText((String.format("%.2f",distance)+" "+units));
            picture.setImageBitmap(place.getPic());
        }



        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imageView_list_item:
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.image_dialog);
                    ImageView image = (ImageView) dialog.findViewById(R.id.imageView_enlarge);
                    image.setImageBitmap(place.getPic());
                    dialog.show();
                    break;

                default:

                    mfrag = ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("map");
                    sfrag = ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("search");

                    float ClickedPlaceLat = (float) this.place.getLat();
                    float ClickedPlaceLng = (float) this.place.getLng();
                    String placeName = place.getName();

                    sp.edit().putFloat("ClickedPlaceLat", (float) ClickedPlaceLat).putFloat("ClickedPlaceLng", (float) ClickedPlaceLng).putString("place_name", placeName).apply();

                    tablet = context.getResources().getBoolean(R.bool.is_tablet);
                    if (tablet==false) { // is phone device
                        ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                .show(mfrag)
                                .hide(sfrag)
                                .addToBackStack("") // so back can be pressed without crashing the app
                                .commit();
                    } else {
                        // tablet - already showing both fragments
                    }

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(context.getString(R.string.map_broadcast)));
                        Toast.makeText(context, "You clicked " + textName.getText(), Toast.LENGTH_SHORT).show();
                        break;

            }
        }
        @Override
        public boolean onLongClick(View v) {

            dialogLongClick = new AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_fav_title)
                    .setMessage(R.string.dialog_fav_message)
                    .setPositiveButton(R.string.share_fav, (DialogInterface.OnClickListener) this)
                    .setNegativeButton(R.string.save_fav, (DialogInterface.OnClickListener) this)
                    .create();
            dialogLongClick.show();
            return true;
        }

        @Override
        public void onClick(DialogInterface dialog, int button) {
            //if (dialogInterface == dialogLongClick) {
            switch (button) {

                case DialogInterface.BUTTON_POSITIVE:

                    // *** Sharing place using intent *** //

                    Intent inshare = new Intent();
                    inshare.setAction(Intent.ACTION_SEND);
                    inshare.setType("text/plain");

                    //Add data to the intent
                    String shareString = context.getString(R.string.share_header) +
                            context.getString(R.string.share_title) + textName.getText() +
                            context.getString(R.string.share_body_address) + textLocation.getText();

                    inshare.putExtra(Intent.EXTRA_TEXT, shareString);
                    context.startActivity(inshare);
                    break;


                case DialogInterface.BUTTON_NEGATIVE:

                // *** SAVE place - add to favourites - check if exist and if so replacing old place with edited one *** //
                    helper.addFav(place);

                    Toast.makeText(context, R.string.saved_fav, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    // method to calculate distance between two locations in meters
    public static double distance(double myLAT, double placeLat, double myLng,
                                  double placeLng, double elMy, double elPlace) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(myLAT -  placeLat);
        double lonDistance = Math.toRadians(myLng - placeLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(placeLat)) * Math.cos(Math.toRadians(myLAT))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c ;

        double height = elPlace - elMy  ;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

        }



