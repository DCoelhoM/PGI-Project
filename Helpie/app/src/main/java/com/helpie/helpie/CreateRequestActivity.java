package com.helpie.helpie;

import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateRequestActivity extends AppCompatActivity {

    private int total_it=0;

    private EditText title;
    private EditText description;
    private ArrayList<EditText> items;
    private Spinner locations;
    private DatePicker deadline_date;
    private TimePicker deadline_time;

    private Button plus;
    private Button minus;
    private Button create;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.v_layout);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        items = new ArrayList<>();
        locations = (Spinner) findViewById(R.id.locations);
        deadline_date = (DatePicker) findViewById(R.id.datePicker);
        deadline_time = (TimePicker) findViewById(R.id.timePicker);
        deadline_time.setIs24HourView(true);

        plus = (Button) findViewById(R.id.plus);
        minus = (Button) findViewById(R.id.minus);

        create = (Button) findViewById(R.id.create);
        back = (Button) findViewById(R.id.back);

        //GET LOCATIONS
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        API r = new API();
        String result = r.myLocations(SaveSharedPreference.getID(CreateRequestActivity.this));
        JSONObject obj = null;

        ArrayList<String> loc_array = new ArrayList<>();
        try {
            obj = new JSONObject(result);
            if (obj.getInt("success")==1) {
                JSONArray locs = obj.getJSONArray("locations");
                JSONObject loc;
                for (int i = 0; i < locs.length(); i++) {
                    loc=locs.getJSONObject(i);
                    loc_array.add("(" + String.valueOf(loc.getInt("id")) + ") -> " + loc.getString("name"));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, loc_array);
                    locations.setAdapter(adapter);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Não possui nenhuma localização guardada!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException ex) {
            Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
        }

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_it++;

                EditText it = new EditText(CreateRequestActivity.this);
                it.setTextColor(Color.rgb(255, 255, 255));
                it.setFilters(new InputFilter[] {new InputFilter.LengthFilter(32)});
                items.add(it);
                layout.addView(it);
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(total_it>1){
                    total_it--;
                    layout.removeView(items.get(items.size() - 1));
                    items.remove(items.size()-1);
                }
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = title.getText().toString().trim();
                String desc = description.getText().toString().trim();
                ArrayList<String> item_list = new ArrayList<String>();
                for (int i=0; i < items.size();i++){
                    item_list.add(items.get(i).getText().toString().trim());
                }
                String selected_location = String.valueOf(locations.getSelectedItem());
                int loc_id = Integer.valueOf(selected_location.substring(1,selected_location.indexOf(")")));
                String deadline_date_time = String.valueOf(deadline_date.getYear()) + "-" + String.valueOf(deadline_date.getMonth()) + "-" + String.valueOf(deadline_date.getDayOfMonth())+ " " + String.valueOf(deadline_time.getCurrentHour()) + ":" + String.valueOf(deadline_time.getCurrentMinute());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Date deadline = new Date();
                try {
                    deadline = dateFormat.parse(deadline_date_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                API r = new API();
                String result = r.createRequest(SaveSharedPreference.getID(CreateRequestActivity.this), t, desc, loc_id, item_list, deadline);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(result);
                    if (obj.getInt("success")==1) {
                        Toast.makeText(getApplicationContext(), "Pedido criado com sucesso!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CreateRequestActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException ex) {
                    Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateRequestActivity.this, RequestsMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
