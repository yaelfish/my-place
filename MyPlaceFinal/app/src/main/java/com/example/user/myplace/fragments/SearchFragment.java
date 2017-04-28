package com.example.user.myplace.fragments;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.user.myplace.MyIntentService;
import com.example.user.myplace.Place;
import com.example.user.myplace.PlacesAdapter;
import com.example.user.myplace.PlacesDBHelper;
import com.example.user.myplace.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private EditText searchText;
    private PlacesAdapter adapter;
    private RecyclerView placeList;
    private PlacesDBHelper helper;
    private ArrayList<Place> places;
    private Intent service;
    ProgressDialog progressDialog;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        searchText = (EditText) v.findViewById(R.id.editText_search);
        places = new ArrayList<>();
        adapter = new PlacesAdapter(this.getContext(), places);
        helper = new PlacesDBHelper(getContext());

        // recycler
        placeList = (RecyclerView) v.findViewById(R.id.list);
        placeList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        placeList.setAdapter(adapter);

        v.findViewById(R.id.btn_search).setOnClickListener(this);
        v.findViewById(R.id.btn_location).setOnClickListener(this);

        // create receiver
        PlaceReceiver receiver = new PlaceReceiver();
        // create filter with action
        IntentFilter filter = new IntentFilter(MyIntentService.SEARCH_END_ACTION);
        // register the receiver
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(receiver, filter);

        // check phone orientation and change the RecyclerLayout
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            placeList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        } else {
            placeList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.clear();
        adapter.addAll(helper.getAllPlaces());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        //create intent service
        service = new Intent(this.getActivity(), MyIntentService.class);

        //create progress dialog for the waiting, so the user won't get despaired
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setTitle(R.string.progress_dialog_title);
        progressDialog.setMessage(getString(R.string.progress_dialog_message));
        progressDialog.setIcon(R.drawable.loading);
        progressDialog.show();


        // clicking the buttons
        switch (view.getId()) {

            // search everything around me
            case R.id.btn_location:
                service.putExtra("search_key", "");
                break;

            // search with given key words
            case R.id.btn_search:
                String searchStr = searchText.getText().toString().replace(" ", "%20");
                service.putExtra("search_key", searchStr);
                break;
        }
        //get user location from shared preferences and send it to the service with intent
        float lat = getContext().getSharedPreferences("PlaceSp", MODE_PRIVATE).getFloat("mylat", 34.65f);
        float lng = getContext().getSharedPreferences("PlaceSp", MODE_PRIVATE).getFloat("mylng", 32.25f);
        service.putExtra("mylat",lat );
        service.putExtra("mylng",lng );

        if ( places != null) {
            adapter.clear();
            helper.deleteAllPlaces();
        }
        // start service
        getActivity().startService(service);
    }

    public class PlaceReceiver extends BroadcastReceiver {
        @Override
        // on getting back results from service through receiver
        public void onReceive(Context context, Intent intent) {

            if(progressDialog != null) {
                progressDialog.dismiss();
            }
            places.addAll(helper.getAllPlaces());
            adapter.notifyDataSetChanged();
        }
    }
}