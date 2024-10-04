package com.example.racehub;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.transition.TransitionManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class calendarFragment extends Fragment implements OnMapReadyCallback {

    private RequestQueue requestQueue;
    private ListView upcomingRaces;
    private MapView mMapView;
    private double raceLocLat;
    private double raceLocLong;
    private SwitchCompat mapSwitch;
    private ListView upcomingRaceListView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        Context context = getContext();
        if (context == null) return v;

        mMapView = v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(calendarFragment.this);

        requestQueue = Volley.newRequestQueue(getContext());
        String seasonUrl = "https://ergast.com/api/f1/current.json";

        upcomingRaces = v.findViewById(R.id.upcoming_races_list);
        List<String> upcomingRacesList = new ArrayList<String>();

        JsonObjectRequest seasonJsonObject = new JsonObjectRequest(
                Request.Method.GET,
                seasonUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            for (int i = 0; i < response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").length(); i++) {
                                String raceDateString = response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getString("date");
                                LocalDate raceDate = LocalDate.parse(raceDateString);
                                if (raceDate.isEqual(LocalDate.now()) || raceDate.isAfter(LocalDate.now())) {
                                    String raceName = response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getString("raceName");
                                    raceName += (" - " + raceDate.toString());
                                    upcomingRacesList.add(raceName);
                                }
                                if (i == 0){
                                    raceLocLat = Double.parseDouble(response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getJSONObject("Circuit").getJSONObject("Location").getString("lat"));
                                    raceLocLong = Double.parseDouble(response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getJSONObject("Circuit").getJSONObject("Location").getString("long"));
                                }
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, upcomingRacesList);
                            upcomingRaces.setAdapter(arrayAdapter);

                        } catch (JSONException e) {
                            Log.d("ERROR MSG", e.getMessage());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR MSG", error.getMessage());
                    }
                }
        );


        mapSwitch = v.findViewById(R.id.mapSwitch);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);

        //size change from https://stackoverflow.com/questions/51420888/set-percent-width-constraintlayout-android-programmatically by user WavyGravy accessed on 17/04/2021
        // Get the constraint layout of the parent constraint view.
        ConstraintLayout mConstraintLayout = v.findViewById(R.id.fragment_calendar);

        // Define a constraint set that will be used to modify the constraint layout parameters of the child.
        ConstraintSet mConstraintSet = new ConstraintSet();

        // Start with a copy the original constraints.
        mConstraintSet.clone(mConstraintLayout);

        boolean mapIsOn = sharedPreferences.getBoolean("MAP_ON", true);
        if(mapIsOn) {
            turnOnMap(mConstraintLayout, mConstraintSet, sharedPreferences);
            mapSwitch.setChecked(true);
        } else {
            turnMapOff(mConstraintLayout, mConstraintSet, sharedPreferences);
            mapSwitch.setChecked(false);
        }

        mapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    turnOnMap(mConstraintLayout, mConstraintSet, sharedPreferences);
                } else {
                    turnMapOff(mConstraintLayout, mConstraintSet, sharedPreferences);
                }
            }
        });

        requestQueue.add(seasonJsonObject);
        return v;
    }

    private void turnMapOff(ConstraintLayout mConstraintLayout, ConstraintSet mConstraintSet, SharedPreferences sharedPreferences) {

        mMapView.setVisibility(View.GONE);
        mConstraintSet.constrainPercentHeight(R.id.upcoming_races_list, 0.92F);
        TransitionManager.beginDelayedTransition(mConstraintLayout);
        mConstraintSet.applyTo(mConstraintLayout);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("MAP_ON", false);
        editor.apply();
    }

    private void turnOnMap(ConstraintLayout mConstraintLayout, ConstraintSet mConstraintSet, SharedPreferences sharedPreferences) {
        mMapView.setVisibility(View.VISIBLE);
        mConstraintSet.constrainPercentHeight(R.id.upcoming_races_list, 0.475F);
        TransitionManager.beginDelayedTransition(mConstraintLayout);
        mConstraintSet.applyTo(mConstraintLayout);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("MAP_ON", true);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        LatLng nextRaceLocation = new LatLng(MainActivity.raceLocLat, MainActivity.raceLocLong);
        googleMap.addMarker(new MarkerOptions().position(nextRaceLocation).title(MainActivity.raceNameShort));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nextRaceLocation));

        //map zoom adapted from https://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager by user arshu and nbro accessed 14/04/2021
        CameraPosition cameraPosition = new CameraPosition.Builder().target(nextRaceLocation).zoom(14).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
