package com.example.racehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static double raceLocLong;
    public static String raceNameShort;
    public static double raceLocLat;
    public static final String SHARED_PREFS = "shared_prefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new homeFragment()).commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //first opening preferences from https://stackoverflow.com/questions/28926335/android-make-a-first-time-use-screen by user Bene accessed 16/04/2021
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean firstOpened = sharedPreferences.getBoolean("FIRST_OPEN", true);
        if(firstOpened) {
            Intent intent = new Intent(this, tutorialActivity.class);
            startActivity(intent);

            editor.putBoolean("FIRST_OPEN", false);
            editor.apply();
        }

    }

    //    navigation adapted from https://www.youtube.com/watch?v=tPV8xA7m-iw&ab_channel=CodinginFlow accessed 12/04/2021
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            //animation adapted from https://stackoverflow.com/questions/4932462/animate-the-transition-between-fragments by user Hiren Patel accessed 16/04/2021
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new homeFragment();
                    if (getVisibleFragment() instanceof calendarFragment) {
                        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                        transaction.replace(R.id.fragment_container, selectedFragment);
                        transaction.commit();
                    } else if (getVisibleFragment() instanceof favFragment) {
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                    transaction.replace(R.id.fragment_container, selectedFragment);
                    transaction.commit();
                    }
                    break;

                case R.id.nav_calendar:
                    selectedFragment = new calendarFragment();
                    if (getVisibleFragment() instanceof homeFragment) {
                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        transaction.replace(R.id.fragment_container, selectedFragment);
                        transaction.commit();
                    } else if (getVisibleFragment() instanceof favFragment) {
                        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                        transaction.replace(R.id.fragment_container, selectedFragment);
                        transaction.commit();
                    }
                    break;

                case R.id.nav_fav:
                    selectedFragment = new favFragment();
                    if (getVisibleFragment() instanceof homeFragment) {
                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        transaction.replace(R.id.fragment_container, selectedFragment);
                        transaction.commit();
                    } else if (getVisibleFragment() instanceof calendarFragment) {
                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        transaction.replace(R.id.fragment_container, selectedFragment);
                        transaction.commit();
                    }
                    break;
            }
            return true;
        }
    };


    //Finding shown fragment adapted from https://stackoverflow.com/questions/32715230/how-to-get-the-current-visible-fragment-in-android/32715283 by user Anoop M Maddasseri
    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

}