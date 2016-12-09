package com.helpie.helpie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LocationsMenuActivity extends AppCompatActivity {

    private Button save;
    private Button mylocations;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_menu);


        save = (Button) findViewById(R.id.save);
        mylocations = (Button) findViewById(R.id.mylocations);
        back = (Button) findViewById(R.id.back);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationsMenuActivity.this, SaveLocationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mylocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationsMenuActivity.this, MyLocationsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationsMenuActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LocationsMenuActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
