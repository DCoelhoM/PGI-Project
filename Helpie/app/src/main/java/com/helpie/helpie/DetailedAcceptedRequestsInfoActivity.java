package com.helpie.helpie;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailedAcceptedRequestsInfoActivity extends AppCompatActivity {

    private JSONObject request;

    private TextView state;
    private TextView title;
    private TextView description;
    private TextView owner;
    private TextView location;
    private TextView items_list;
    private TextView created;
    private TextView deadline;
    private TextView feedback;
    private TextView helper;
    private TextView feedback_helper;

    private Button give_feedback;
    private Button directions;
    private Button back;

    private int id;

    private String latitude,longitude;

    private String owner_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_accepted_requests_info);

        final LinearLayout layout = (LinearLayout) findViewById(R.id.v_layout);

        state = (TextView) findViewById(R.id.state);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        owner = (TextView) findViewById(R.id.owner);
        location = (TextView) findViewById(R.id.location);
        created = (TextView) findViewById(R.id.created);
        deadline = (TextView) findViewById(R.id.deadline);
        items_list = (TextView) findViewById(R.id.items_list);
        feedback = (TextView) findViewById(R.id.feedback);
        helper = (TextView) findViewById(R.id.helper);
        feedback_helper = (TextView) findViewById(R.id.feedback_helper);

        give_feedback = (Button) findViewById(R.id.give_feedback);
        directions = (Button) findViewById(R.id.directions);
        back = (Button) findViewById(R.id.back);

        Bundle data = getIntent().getExtras();
        if(data != null){
            id = data.getInt("id");

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            API r = new API();
            String result = r.requestInfo(id);
            try {
                request = new JSONObject(result);
                if (request.getInt("success") == 1) {
                    String s =  request.getString("state");
                    if (s.equals("accepted")){
                        state.setText("Estado: Aceite");
                    } else{
                        state.setText("Estado: Terminado");
                    }

                    title.setText(("Título: " + request.getString("title")));

                    description.setText(("Descrição: " + request.getString("description")));

                    owner_name = request.getString("owner");
                    owner.setText(("Criado por: " + owner_name));

                    location.setText(("Localização: " + request.getString("location")));
                    latitude = request.getString("latitude");
                    longitude = request.getString("longitude");

                    JSONArray i_list= request.getJSONArray("list");
                    int list_size = i_list.length();
                    if (list_size>0) {
                        items_list.setVisibility(View.VISIBLE);
                        layout.setVisibility(View.VISIBLE);
                        for (int i = 0; i < list_size; i++) {
                            TextView item = new TextView(DetailedAcceptedRequestsInfoActivity.this);
                            item.setTextColor(Color.rgb(255, 255, 255));
                            item.setText((" - " + i_list.getString(i)));
                            layout.addView(item);
                        }
                    }

                    created.setText(("Data de Criação: " + request.getString("created")));

                    deadline.setText(("Data Limite: " + request.getString("deadline")));

                    if (s.equals("accepted") || s.equals("ended")){
                        helper.setText(("Ajudante: " + request.getString("helper")));
                        helper.setVisibility(View.VISIBLE);
                    }
                    if (s.equals("ended")){
                        feedback.setText(("Avaliação do Utilizador: " + request.getString("feedback") + "/5"));
                        feedback.setVisibility(View.VISIBLE);
                        feedback_helper.setText(("Avaliação do Ajudante: " + request.getString("feedback_helper") + "/5"));
                        feedback_helper.setVisibility(View.VISIBLE);
                        if (request.getString("feedback").equals("n")){
                            give_feedback.setVisibility(View.VISIBLE);
                        }
                    }

                    if(s.equals("accepted")){
                        directions.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException ex) {
                Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
            }
        }
        final View alertview = ((LayoutInflater) DetailedAcceptedRequestsInfoActivity.this.getLayoutInflater()).inflate(R.layout.layout_feedback, null);
        final RatingBar rat = (RatingBar) alertview.findViewById(R.id.ratingBar);
        rat.setNumStars(5);

        give_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailedAcceptedRequestsInfoActivity.this)
                        .setView(alertview)
                        .setTitle(("Avaliar " + owner_name))
                        .setMessage("Avalie o utilizador de 0 a 5.")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int value = Math.round(rat.getRating());

                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                API r = new API();
                                String result = r.giveFeedbackOwner(id, value);
                                JSONObject obj = null;
                                try {
                                    obj = new JSONObject(result);
                                    if (obj.getInt("success")==1) {
                                        Toast.makeText(getApplicationContext(), ("Utilizador " + owner_name + " avaliado com sucesso!"), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(DetailedAcceptedRequestsInfoActivity.this, AcceptedRequestsActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException ex) {
                                    Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(0)
                        .show();
            }
        });

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(("http://maps.google.com/maps?daddr=" + latitude + "," + longitude)));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try {
                    startActivity(intent);
                } catch(ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(("http://maps.google.com/maps?daddr=" + latitude + "," + longitude)));
                        startActivity(unrestrictedIntent);
                    } catch(ActivityNotFoundException innerEx) {
                        Toast.makeText(getApplicationContext(), "Por favor instale o Google Maps ou outra parecida.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedAcceptedRequestsInfoActivity.this, AcceptedRequestsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailedAcceptedRequestsInfoActivity.this, AcceptedRequestsActivity.class);
        startActivity(intent);
        finish();
    }
}
