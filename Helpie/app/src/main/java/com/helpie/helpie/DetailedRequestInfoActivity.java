package com.helpie.helpie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DetailedRequestInfoActivity extends AppCompatActivity {

    private JSONObject request;
    private TextView info;
    private TextView title;
    private TextView description;
    private TextView items_list;
    private TextView deadline;
    private TextView owner;
    private TextView feedback;

    private Button accept;
    private Button back;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_request_info);


        final LinearLayout layout = (LinearLayout) findViewById(R.id.v_layout);

        info = (TextView) findViewById((R.id.info));
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        deadline = (TextView) findViewById(R.id.deadline);
        items_list = (TextView) findViewById(R.id.items_list);
        owner = (TextView) findViewById((R.id.owner));
        feedback = (TextView) findViewById(R.id.feedback);

        accept = (Button) findViewById(R.id.accept);
        back = (Button) findViewById(R.id.back);

        Bundle data = getIntent().getExtras();
        if(data != null){
            try {
                request = new JSONObject(data.getString("request"));
                id = request.getInt("id");

                if (request.get("type").equals("voluntary")){
                    info.setVisibility(View.VISIBLE);
                }

                title.setText(("Título: " + request.getString("title")));

                description.setText(("Descrição: " + request.getString("description")));

                JSONArray i_list= request.getJSONArray("list");
                int list_size = i_list.length();
                if (list_size>0) {
                    items_list.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < list_size; i++) {
                        TextView item = new TextView(DetailedRequestInfoActivity.this);
                        item.setTextColor(Color.rgb(255, 255, 255));
                        item.setText((" - " + i_list.getString(i)));
                        layout.addView(item);
                    }
                }

                deadline.setText(("Data Limite: " + request.getString("deadline")));

                owner.setText(("Criado por: " + request.getString("owner")));

                feedback.setText(("Avaliação do Utilizador: " + request.getString("feedback") + "/5"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailedRequestInfoActivity.this)
                        .setTitle("Aceitar pedido")
                        .setMessage("Tem a certeza que quer aceitar este pedido?")
                        .setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedRequestInfoActivity.this, NearbyRequestsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailedRequestInfoActivity.this, NearbyRequestsActivity.class);
        startActivity(intent);
        finish();
    }
}
