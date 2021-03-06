package com.helpie.helpie;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

public class CreateRequestActivity extends AppCompatActivity {

    private int total_it=0;

    private EditText title;
    private EditText description;
    private ArrayList<EditText> items;
    private Spinner locations;

    private TextView deadline;
    private DatePickerDialog deadline_date_picker;
    private TimePickerDialog deadline_time_picker;
    private Calendar deadline_date_time;
    SimpleDateFormat dateFormat_PT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private Button plus;
    private Button minus;
    private Button create;
    private Button back;

    LinkedList<String> loc_array = new LinkedList<>();
    LinkedList<Integer> loc_array_id = new LinkedList<>();

    private TextView max_helpers_view;
    private EditText max_helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        final LinearLayout layout = (LinearLayout) findViewById(R.id.v_layout);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);

        items = new ArrayList<>();

        locations = (Spinner) findViewById(R.id.locations);

        plus = (Button) findViewById(R.id.plus);
        minus = (Button) findViewById(R.id.minus);

        create = (Button) findViewById(R.id.create);
        back = (Button) findViewById(R.id.back);

        final String user_type = SaveSharedPreference.getType(CreateRequestActivity.this);
        max_helpers_view = (TextView) findViewById(R.id.max_helpers_textView);
        max_helpers = (EditText) findViewById(R.id.max_helpers);
        if(user_type.equals("voluntary")){
            max_helpers_view.setVisibility(View.VISIBLE);
            max_helpers.setVisibility(View.VISIBLE);
        }

        deadline = (TextView) findViewById(R.id.deadline);
        deadline_date_time  = Calendar.getInstance();
        SpannableString data_content = new SpannableString(dateFormat_PT.format(deadline_date_time.getTime()));
        data_content.setSpan(new UnderlineSpan(), 0, data_content.length(), 0);
        deadline.setText(data_content);

        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deadline_time_picker = new TimePickerDialog(CreateRequestActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        deadline_date_time.set(deadline_date_time.get(Calendar.YEAR), deadline_date_time.get(Calendar.MONTH), deadline_date_time.get(Calendar.DAY_OF_MONTH),hourOfDay,minute);
                        SpannableString data_content = new SpannableString(dateFormat_PT.format(deadline_date_time.getTime()));
                        data_content.setSpan(new UnderlineSpan(), 0, data_content.length(), 0);
                        deadline.setText(data_content);
                    }

                },deadline_date_time.get(Calendar.HOUR_OF_DAY),deadline_date_time.get(Calendar.MINUTE),true);
                deadline_time_picker.show();

                deadline_date_picker = new DatePickerDialog(CreateRequestActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        deadline_date_time.set(year, monthOfYear, dayOfMonth);
                        SpannableString data_content = new SpannableString(dateFormat_PT.format(deadline_date_time.getTime()));
                        data_content.setSpan(new UnderlineSpan(), 0, data_content.length(), 0);
                        deadline.setText(data_content);
                    }
                },deadline_date_time.get(Calendar.YEAR), deadline_date_time.get(Calendar.MONTH), deadline_date_time.get(Calendar.DAY_OF_MONTH));
                deadline_date_picker.show();
            }
        });


        //GET LOCATIONS
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        API r = new API();
        String result = r.myLocations(SaveSharedPreference.getID(CreateRequestActivity.this));
        JSONObject obj = null;

        try {
            obj = new JSONObject(result);
            if (obj.getInt("success")==1) {
                JSONArray locs = obj.getJSONArray("locations");
                JSONObject loc;
                for (int i = 0; i < locs.length(); i++) {
                    loc=locs.getJSONObject(i);
                    loc_array.addLast(loc.getString("name"));
                    loc_array_id.addLast(loc.getInt("id"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, loc_array);
                locations.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "Não possui nenhuma localização guardada! Guarde primeiro a sua localização!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CreateRequestActivity.this, RequestsMenuActivity.class);
                startActivity(intent);
                finish();
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
                if(total_it>0){
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
                String m = max_helpers.getText().toString().trim();

                if(!t.isEmpty() && !desc.isEmpty() && ((user_type.equals("voluntary") && !m.isEmpty() && m.matches("[0-9]+")) || user_type.equals("normal")) && confirmItems()) {
                    ArrayList<String> item_list = new ArrayList<String>();
                    for (int i = 0; i < items.size(); i++) {
                        item_list.add(items.get(i).getText().toString().trim());
                    }

                    int max = 1;
                    if (user_type.equals("voluntary")){
                        max = Integer.valueOf(m);
                    }

                    int loc_id = loc_array_id.get(locations.getSelectedItemPosition());

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    API r = new API();
                    String result = r.createRequest(SaveSharedPreference.getID(CreateRequestActivity.this), t, desc, loc_id, item_list, deadline_date_time.getTime(), max);
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(result);
                        if (obj.getInt("success") == 1) {
                            Toast.makeText(getApplicationContext(), "Pedido criado com sucesso!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CreateRequestActivity.this, RequestsMenuActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException ex) {
                        Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_LONG).show();
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

    public boolean confirmItems(){
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getText().toString().trim().isEmpty()){
                Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateRequestActivity.this, RequestsMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
