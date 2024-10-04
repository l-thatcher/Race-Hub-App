package com.example.racehub;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//Pop up adapted from https://www.youtube.com/watch?v=fn5OlqQuOCk&ab_channel=FilipVujovic accessed 15/04/2021
public class popupActivity extends Activity {

    private TextView titleText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popup);

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.75),(int)(height*.65));

        String selectedItem = getIntent().getStringExtra("selectedItem");

        titleText = findViewById(R.id.profileTitleText);

        if (selectedItem.contains("Mercedes")){
            selectedItem = "Mercedes";
        } else if (selectedItem.contains("Red Bull")){
            selectedItem = "Red Bull";
        } else if (selectedItem.contains("McLaren")){
            selectedItem = "McLaren";
        } else if (selectedItem.contains("Ferrari")){
            selectedItem = "Ferrari";
        } else if (selectedItem.contains("AlphaTauri")){
            selectedItem = "AlphaTauri";
        } else if (selectedItem.contains("Aston Martin")){
            selectedItem = "Aston Martin";
        } else if (selectedItem.contains("Alfa Romeo")){
            selectedItem = "Alfa Romeo";
        } else if (selectedItem.contains("Alpine")){
            selectedItem = "Alpine";
        } else if (selectedItem.contains("Williams")){
            selectedItem = "Williams";
        } else if (selectedItem.contains("Haas")){
            selectedItem = "Haas";
        } else if (selectedItem.contains("Lewis")){
            selectedItem = "Lewis Hamilton";
        } else if (selectedItem.contains("Max")){
            selectedItem = "Max Verstappen";
        } else if (selectedItem.contains("Valtteri")){
            selectedItem = "Valtteri Bottas ";
        } else if (selectedItem.contains("Lando")){
            selectedItem = "Lando Norris";
        } else if (selectedItem.contains("Sergio")){
            selectedItem = "Sergio (Checko) Pérez";
        } else if (selectedItem.contains("Charles")){
            selectedItem = "Charles Leclerc";
        } else if (selectedItem.contains("Daniel")){
            selectedItem = "Daniel Ricciardo";
        } else if (selectedItem.contains("Carlos")){
            selectedItem = "Carlos Sainz";
        } else if (selectedItem.contains("Yuki")){
            selectedItem = "Yuki Tsunoda";
        } else if (selectedItem.contains("Lance")){
            selectedItem = "Lance Stroll";
        } else if (selectedItem.contains("Kimi")){
            selectedItem = "Kimi Räikkönen";
        } else if (selectedItem.contains("Antonio")){
            selectedItem = "Antonio Giovinazzi";
        } else if (selectedItem.contains("Esteban")){
            selectedItem = "Esteban Ocon";
        } else if (selectedItem.contains("George")){
            selectedItem = "George Russell";
        } else if (selectedItem.contains("Sebastian")){
            selectedItem = "Sebastian Vettel";
        } else if (selectedItem.contains("Mick")){
            selectedItem = "Mick Schumacher";
        } else if (selectedItem.contains("Pierre")){
            selectedItem = "Pierre Gasly";
        } else if (selectedItem.contains("Nicholas")){
            selectedItem = "Nicholas Latifi";
        } else if (selectedItem.contains("Fernando")){
            selectedItem = "Fernando Alonso";
        } else if (selectedItem.contains("Nikita")){
            selectedItem = "Nikita Mazepin";
        }

        titleText.setText(selectedItem);

    }
}
