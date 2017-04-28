package com.example.user.myplace;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myplace.fragments.MapFragment;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 29/12/2016.
 */

public class FavsAdapter extends RecyclerView.Adapter<FavsAdapter.FavHolder>   {

    private Context context;
    private ArrayList<Place> favs;
    private SharedPreferences sp;
    private SharedPreferences defaultSP;
    private float myLat, myLong;


    public FavsAdapter(Context context, ArrayList<Place> favs) {
        this.context = context;
        this.favs = favs;
        sp = context.getSharedPreferences("PlaceSp", MODE_PRIVATE);
        defaultSP = PreferenceManager.getDefaultSharedPreferences(this.context);
        myLat = sp.getFloat("mylat", 0);
        myLong = sp.getFloat("mylng", 0);
    }

    @Override
    public FavHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faves_list_item, null);
        return new FavHolder(view);
    }

    @Override
    public void onBindViewHolder(FavHolder holder, int position) {
        Place place = favs.get(position);
        holder.bind(place);
    }

    @Override
    public int getItemCount() {
        return favs.size();
    }


    public void addAllFavs(ArrayList<Place> favs) {
        this.favs.addAll(favs);
        notifyDataSetChanged();

    }

    public void clearAllFavs() {
        if (favs != null){
            favs.clear();
        }
        notifyDataSetChanged();
    }


    public void addFav(Place place) {
        favs.add(place);
        notifyDataSetChanged();
    }

    public class FavHolder extends RecyclerView.ViewHolder {

        private ImageView picture;
        private TextView textName, textLocation, textDistance;
        private Place p;

        public FavHolder(View itemView) {
            super(itemView);

            picture = (ImageView) itemView.findViewById(R.id.imageView_list_item_fav);
            textName = (TextView) itemView.findViewById(R.id.textView_fav_name);
            textLocation = (TextView) itemView.findViewById(R.id.textView_fav_address);
            textDistance = (TextView) itemView.findViewById(R.id.textView_fav_distance);

        }

        public void bind(Place place) {
            this.p = place;

            double distance =PlacesAdapter.distance(myLat, place.getLat(),myLong,place.getLng(),0,0);
            String units =defaultSP.getString("units_key", "km");
            if (units.equals("miles")) {
                distance = distance * 0.621371;
            }
            textName.setText(place.getName());
            textLocation.setText(place.getAddress());
            textDistance.setText((String.format("%.2f",distance)+" "+units));
            picture.setImageBitmap(place.getPic());

        }

    }


}
