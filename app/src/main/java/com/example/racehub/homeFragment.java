package com.example.racehub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment {

    private RequestQueue requestQueue;
    private TextView textView;
    private ProgressBar seasonProgressBar;
    private ListView resultsList;
    private ListView driverList;
    private ListView constructorList;
    String nextRace = null;
    String driverSelectedItem = null;
    String TappedItem = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        textView = v.findViewById(R.id.next_race_text_view);
        seasonProgressBar = v.findViewById(R.id.season_progress_bar);

        Context context = getContext();
        if (context == null) return v;

        requestQueue = Volley.newRequestQueue(getContext());
        String seasonUrl = "https://ergast.com/api/f1/current.json";
        String driverUrl = "https://ergast.com/api/f1/current/last/results.json";
        String driverStandingsUrl = "https://ergast.com/api/f1/current/driverStandings.json";
        String constructorStandingsUrl = "https://ergast.com/api/f1/current/constructorStandings.json";

        JsonObjectRequest seasonJsonObject = new JsonObjectRequest(
                Request.Method.GET,
                seasonUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        Integer raceCount = 0;
                        Integer allRaces;

                        try {
                            //Log.d("FULL RESPONSE", response.toString(4));
                            Log.d("TEMP RESPONSE", response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(0).getJSONObject("Circuit").getJSONObject("Location").getString("lat"));
                            allRaces = Integer.parseInt(response.getJSONObject("MRData").getString("total"));
                            for (int i = 0; i < allRaces; i++) {
                                String raceDateString = response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getString("date");
                                LocalDate raceDate = LocalDate.parse(raceDateString);

                                if (raceDate.isBefore(LocalDate.now())){
                                    raceCount += 1;
                                    continue;
                                } else if (raceDate.isEqual(LocalDate.now())){
                                    MainActivity.raceNameShort = response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getString("raceName");
                                    nextRace = (MainActivity.raceNameShort + " is today!");
                                    MainActivity.raceLocLat = Double.parseDouble(response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getJSONObject("Circuit").getJSONObject("Location").getString("lat"));
                                    MainActivity.raceLocLong = Double.parseDouble(response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getJSONObject("Circuit").getJSONObject("Location").getString("long"));
                                    break;
                                } else {
                                    MainActivity.raceNameShort = response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getString("raceName");
                                    nextRace = (MainActivity.raceNameShort + " on " + raceDateString);
                                    MainActivity.raceLocLat = Double.parseDouble(response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getJSONObject("Circuit").getJSONObject("Location").getString("lat"));
                                    MainActivity.raceLocLong = Double.parseDouble(response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(i).getJSONObject("Circuit").getJSONObject("Location").getString("long"));
                                    break;
                                }

                            }
                        } catch (JSONException e) {
                            nextRace = "unknown";
                            allRaces = 23;
                        }
                        textView.setText(nextRace);
                        seasonProgressBar.setMax(allRaces);
                        seasonProgressBar.setProgress(raceCount);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR MSG", error.getMessage());
                    }
                }
        );

        resultsList = v.findViewById(R.id.last_race);
        List<String> driverResults = new ArrayList<String>();

        JsonObjectRequest driverJsonObject = new JsonObjectRequest(
                Request.Method.GET,
                driverUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            for (int i = 0; i < response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(0).getJSONArray("Results").length(); i++) {
                                String driverName = (i+1 + " - " + response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(0).getJSONArray("Results").getJSONObject(i).getJSONObject("Driver").getString("givenName") + " " + response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(0).getJSONArray("Results").getJSONObject(i).getJSONObject("Driver").getString("familyName"));
                                driverName += (" - " + response.getJSONObject("MRData").getJSONObject("RaceTable").getJSONArray("Races").getJSONObject(0).getJSONArray("Results").getJSONObject(i).getString("points"));
                                driverResults.add(driverName);
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, driverResults);
                            resultsList.setAdapter(arrayAdapter);


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


        driverList = v.findViewById(R.id.driver_standings);
        List<String> driverStandings = new ArrayList<String>();

        JsonObjectRequest driverStandingsJsonObject = new JsonObjectRequest(
                Request.Method.GET,
                driverStandingsUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            for (int i = 0; i < response.getJSONObject("MRData").getJSONObject("StandingsTable").getJSONArray("StandingsLists").getJSONObject(0).getJSONArray("DriverStandings").length(); i++) {
                                String driverName = (i+1 + " - " + response.getJSONObject("MRData").getJSONObject("StandingsTable").getJSONArray("StandingsLists").getJSONObject(0).getJSONArray("DriverStandings").getJSONObject(i).getJSONObject("Driver").getString("givenName") + " " + response.getJSONObject("MRData").getJSONObject("StandingsTable").getJSONArray("StandingsLists").getJSONObject(0).getJSONArray("DriverStandings").getJSONObject(i).getJSONObject("Driver").getString("familyName"));
                                driverName += (" - " + response.getJSONObject("MRData").getJSONObject("StandingsTable").getJSONArray("StandingsLists").getJSONObject(0).getJSONArray("DriverStandings").getJSONObject(i).getString("points"));
                                driverStandings.add(driverName);
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, driverStandings);
                            driverList.setAdapter(arrayAdapter);

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


        constructorList = v.findViewById(R.id.constructor_standings);
        List<String> constructorStandings = new ArrayList<String>();

        JsonObjectRequest constructorStandingsJsonObject = new JsonObjectRequest(
                Request.Method.GET,
                constructorStandingsUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            for (int i = 0; i < response.getJSONObject("MRData").getJSONObject("StandingsTable").getJSONArray("StandingsLists").getJSONObject(0).getJSONArray("ConstructorStandings").length(); i++) {
                                String constructorName = (i+1 + " - " + response.getJSONObject("MRData").getJSONObject("StandingsTable").getJSONArray("StandingsLists").getJSONObject(0).getJSONArray("ConstructorStandings").getJSONObject(i).getJSONObject("Constructor").getString("name").replace('_', ' '));
                                constructorName += (" - " + response.getJSONObject("MRData").getJSONObject("StandingsTable").getJSONArray("StandingsLists").getJSONObject(0).getJSONArray("ConstructorStandings").getJSONObject(i).getString("points"));
                                constructorStandings.add(constructorName);
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, constructorStandings);
                            constructorList.setAdapter(arrayAdapter);

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


        resultsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                driverSelectedItem = (String) parent.getItemAtPosition(position);
                openDriverInstagram(driverSelectedItem);
                return true;
            }
        });

        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TappedItem = (String) parent.getItemAtPosition(position);
                openPopupWindow(TappedItem);
            }
        });


        driverList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                driverSelectedItem = (String) parent.getItemAtPosition(position);
                openDriverInstagram(driverSelectedItem);
                return true;
            }
        });

        driverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TappedItem = (String) parent.getItemAtPosition(position);
                openPopupWindow(TappedItem);
            }
        });


        constructorList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                String constructorSelectedItem = (String) parent.getItemAtPosition(position);
                Uri constuctoruri = null;
                if (constructorSelectedItem.contains("Mercedes")){
                    constuctoruri = Uri.parse("http://instagram.com/_u/mercedesamgf1");
                } else if (constructorSelectedItem.contains("Red Bull")){
                    constuctoruri = Uri.parse("http://instagram.com/_u/redbullracing");
                } else if (constructorSelectedItem.contains("McLaren")){
                    constuctoruri = Uri.parse("http://instagram.com/_u/mclaren");
                } else if (constructorSelectedItem.contains("Ferrari")){
                    constuctoruri = Uri.parse("http://instagram.com/_u/scuderiaferrari");
                } else if (constructorSelectedItem.contains("AlphaTauri")){
                    constuctoruri = Uri.parse("http://instagram.com/_u/alphataurif1");
                } else if (constructorSelectedItem.contains("Aston Martin")){
                    constuctoruri = Uri.parse("http://instagram.com/_u/astonmartinf1");
                } else if (constructorSelectedItem.contains("Alfa Romeo")){
                    constuctoruri = Uri.parse("http://instagram.com/_u/alfaromeoracingorlen");
                } else if (constructorSelectedItem.contains("Alpine")){
                    constuctoruri = Uri.parse("http://instagram.com/_u/alpinef1team");
                } else if (constructorSelectedItem.contains("Williams")){
                    constuctoruri = Uri.parse("http://instagram.com/_u/williamsracing");
                } else if (constructorSelectedItem.contains("Haas")){
                    constuctoruri = Uri.parse("http://instagram.com/_u/haasf1team");
                }

                startActivity(new Intent(Intent.ACTION_VIEW, constuctoruri));

                return true;
            }
        });

        constructorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TappedItem = (String) parent.getItemAtPosition(position);
                openPopupWindow(TappedItem);
            }
        });

        requestQueue.add(seasonJsonObject);
        requestQueue.add(driverJsonObject);
        requestQueue.add(driverStandingsJsonObject);
        requestQueue.add(constructorStandingsJsonObject);

        return v;
    }

    private void openPopupWindow(String selectedItem) {
        Intent intent = new Intent(getActivity(), popupActivity.class);
        intent.putExtra("selectedItem", selectedItem);
        startActivity(intent);
    }

    private void openDriverInstagram(String driverSelectedItem) {
        Uri driveruri = null;
        if (driverSelectedItem.contains("Lewis")){
            driveruri = Uri.parse("http://instagram.com/_u/lewishamilton");
        } else if (driverSelectedItem.contains("Max")){
            driveruri = Uri.parse("http://instagram.com/_u/maxverstappen1");
        } else if (driverSelectedItem.contains("Valtteri")){
            driveruri = Uri.parse("http://instagram.com/_u/valtteribottas");
        } else if (driverSelectedItem.contains("Lando")){
            driveruri = Uri.parse("http://instagram.com/_u/landonorris");
        } else if (driverSelectedItem.contains("Sergio")){
            driveruri = Uri.parse("http://instagram.com/_u/schecoperez");
        } else if (driverSelectedItem.contains("Charles")){
            driveruri = Uri.parse("http://instagram.com/_u/charles_leclerc");
        } else if (driverSelectedItem.contains("Daniel")){
            driveruri = Uri.parse("http://instagram.com/_u/danielricciardo");
        } else if (driverSelectedItem.contains("Carlos")){
            driveruri = Uri.parse("http://instagram.com/_u/carlossainz55");
        } else if (driverSelectedItem.contains("Yuki")){
            driveruri = Uri.parse("http://instagram.com/_u/yukitsunoda0511");
        } else if (driverSelectedItem.contains("Lance")){
            driveruri = Uri.parse("http://instagram.com/_u/lance_stroll");
        } else if (driverSelectedItem.contains("Kimi")){
            driveruri = Uri.parse("http://instagram.com/_u/kimimatiasraikkonen");
        } else if (driverSelectedItem.contains("Antonio")){
            driveruri = Uri.parse("http://instagram.com/_u/antogiovinazzi99");
        } else if (driverSelectedItem.contains("Esteban")){
            driveruri = Uri.parse("http://instagram.com/_u/estebanocon");
        } else if (driverSelectedItem.contains("George")){
            driveruri = Uri.parse("http://instagram.com/_u/georgerussell63");
        } else if (driverSelectedItem.contains("Sebastian")){
            driveruri = Uri.parse("http://instagram.com/_u/vettelofficial");
        } else if (driverSelectedItem.contains("Mick")){
            driveruri = Uri.parse("http://instagram.com/_u/mickschumacher");
        } else if (driverSelectedItem.contains("Pierre")){
            driveruri = Uri.parse("http://instagram.com/_u/pierregasly");
        } else if (driverSelectedItem.contains("Nicholas")){
            driveruri = Uri.parse("http://instagram.com/_u/nicholaslatifi");
        } else if (driverSelectedItem.contains("Fernando")){
            driveruri = Uri.parse("http://instagram.com/_u/fernandoalo_oficial");
        } else if (driverSelectedItem.contains("Nikita")){
            driveruri = Uri.parse("http://instagram.com/_u/nikita_mazepin");
        }
        startActivity(new Intent(Intent.ACTION_VIEW, driveruri));
    }
}

