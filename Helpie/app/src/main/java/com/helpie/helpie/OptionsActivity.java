package com.helpie.helpie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


public class OptionsActivity extends AppCompatActivity {

    private SeekBar distance;
    private TextView actual_value;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        distance = (SeekBar) findViewById(R.id.distance);
        actual_value = (TextView) findViewById(R.id.value_textView);
        back = (Button) findViewById(R.id.back);

        distance.setProgress((SaveSharedPreference.getDistance(OptionsActivity.this)/5 - 1));
        actual_value.setText(("Distância actual: " + String.valueOf(SaveSharedPreference.getDistance(OptionsActivity.this)) + "km"));

        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                actual_value.setText(("Distância actual: " + String.valueOf(((seekBar.getProgress()+1)*5)) + "km"));
                SaveSharedPreference.setDistance(OptionsActivity.this,((seekBar.getProgress()+1)*5));
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
