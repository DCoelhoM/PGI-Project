package com.helpie.helpie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class OptionsActivity extends AppCompatActivity {

    private SeekBar distance;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        distance = (SeekBar) findViewById(R.id.distance);
        back = (Button) findViewById(R.id.back);

        distance.setProgress((SaveSharedPreference.getDistance(OptionsActivity.this)/5 - 1));
        Log.d("DEBUG", String.valueOf(distance.getProgress()));

        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("DEBUG 3", String.valueOf(distance.getProgress()));
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                SaveSharedPreference.setDistance(OptionsActivity.this,((seekBar.getProgress()+1)*5));
                Log.d("DEBUG 2", String.valueOf(distance.getProgress()));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionsActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OptionsActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
