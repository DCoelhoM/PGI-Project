package com.helpie.helpie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

public class DetailedMyRequestsInfoActivity extends AppCompatActivity {

    private JSONObject request;

    private TextView state;
    private TextView title;
    private TextView description;
    private TextView location;
    private TextView items_list;
    private TextView created;
    private TextView deadline;
    private TextView feedback;
    private TextView helper;
    private TextView feedback_helper;

    private Button give_feedback;
    private Button cancel;

    private int id;

    private String helper_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_my_requests_info);

        final LinearLayout layout = (LinearLayout) findViewById(R.id.v_layout);

        state = (TextView) findViewById(R.id.state);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        location = (TextView) findViewById(R.id.location);
        created = (TextView) findViewById(R.id.created);
        deadline = (TextView) findViewById(R.id.deadline);
        items_list = (TextView) findViewById(R.id.items_list);
        feedback = (TextView) findViewById(R.id.feedback);
        helper = (TextView) findViewById(R.id.helper);
        feedback_helper = (TextView) findViewById(R.id.feedback_helper);

        give_feedback = (Button) findViewById(R.id.give_feedback);
        cancel = (Button) findViewById(R.id.cancel);

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
                    if (s.equals("active")){
                        state.setText("Estado: Ativo");
                    } else if (s.equals("accepted")){
                        state.setText("Estado: Aceite");
                    } else if (s.equals("ended")){
                        state.setText("Estado: Terminado");
                    } else {
                        state.setText("Estado: Cancelado");
                    }

                    title.setText(("Título: " + request.getString("title")));

                    description.setText(("Descrição: " + request.getString("description")));

                    location.setText(("Localização: " + request.getString("location")));


                    JSONArray i_list= request.getJSONArray("list");
                    int list_size = i_list.length();
                    if (list_size>0) {
                        items_list.setVisibility(View.VISIBLE);
                        layout.setVisibility(View.VISIBLE);
                        for (int i = 0; i < list_size; i++) {
                            TextView item = new TextView(DetailedMyRequestsInfoActivity.this);
                            item.setTextColor(Color.rgb(255, 255, 255));
                            item.setText((" - " + i_list.getString(i)));
                            layout.addView(item);
                        }
                    }

                    created.setText(("Data de Criação: " + request.getString("created")));

                    deadline.setText(("Data Limite: " + request.getString("deadline")));

                    if (s.equals("accepted") || s.equals("ended")){
                        helper_name = request.getString("helper");
                        helper.setText(("Ajudante: " + helper_name));
                        helper.setVisibility(View.VISIBLE);
                    }
                    if (s.equals("ended")){
                        feedback.setText(("Avaliação do Utilizador: " + request.getString("feedback") + "/10"));
                        feedback.setVisibility(View.VISIBLE);
                        feedback_helper.setText(("Avaliação do Ajudante: " + request.getString("feedback_helper") + "/10"));
                        feedback_helper.setVisibility(View.VISIBLE);
                        if (request.getString("feedback_helper").equals("n")){
                            give_feedback.setVisibility(View.VISIBLE);
                        }
                    }

                    if(s.equals("active")){
                        cancel.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException ex) {
                Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
            }
        }
        give_feedback.setOnClickListener(new View.OnClickListener() {
            LayoutInflater inflater = DetailedMyRequestsInfoActivity.this.getLayoutInflater();

            View alertview = ((LayoutInflater) DetailedMyRequestsInfoActivity.this.getLayoutInflater()).inflate(R.layout.layout_feedback, null);
            RatingBar rat = (RatingBar) alertview.findViewById(R.id.ratingBar);


            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailedMyRequestsInfoActivity.this)
                        .setView(alertview)
                        .setTitle(("Avaliar " + helper_name))
                        .setMessage("TEXTO AQUI")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                API r = new API();
                                String result = r.acceptRequest(SaveSharedPreference.getID(DetailedRequestInfoActivity.this), id);
                                JSONObject obj = null;
                                try {
                                    obj = new JSONObject(result);
                                    if (obj.getInt("success")==1) {
                                        Toast.makeText(getApplicationContext(), "Pedido aceite com sucesso!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(DetailedRequestInfoActivity.this, MainMenuActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException ex) {
                                    Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                                }*/
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

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailedMyRequestsInfoActivity.this, MyRequestsActivity.class);
        startActivity(intent);
        finish();
    }
}
