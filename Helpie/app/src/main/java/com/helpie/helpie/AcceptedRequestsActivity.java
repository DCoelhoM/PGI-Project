package com.helpie.helpie;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AcceptedRequestsActivity extends AppCompatActivity {

    private LinearLayout active;
    private LinearLayout ended;

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_requests);

        active = (LinearLayout) findViewById(R.id.active_layout);
        ended = (LinearLayout) findViewById(R.id.ended_layout);

        back = (Button) findViewById(R.id.back);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        API r = new API();
        String result = r.listAcceptedRequests(SaveSharedPreference.getID(AcceptedRequestsActivity.this));
        JSONObject obj = null;
        try {
            obj = new JSONObject(result);
            if (obj.getInt("success") == 1) {
                JSONArray requests = obj.getJSONArray("requests");
                JSONObject req;

                int dpValue = 5; // margin in dips
                float d = AcceptedRequestsActivity.this.getResources().getDisplayMetrics().density;
                int margin = (int)(dpValue * d);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, margin, 0, 0);

                for (int i = 0; i < requests.length(); i++) {
                    req=requests.getJSONObject(i);
                    String state = req.getString("state");

                    Button bt = new Button(AcceptedRequestsActivity.this);
                    bt.setText(req.getString("title"));
                    bt.setId(req.getInt("id"));
                    bt.setLayoutParams(params);
                    bt.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v) {
                            onRequestPressed(v);
                        }
                    });

                    if (state.equals("accepted")){
                        bt.setBackgroundResource(R.drawable.active_request);
                        active.addView(bt);
                    } else if (state.equals("ended")){
                        bt.setBackgroundResource(R.drawable.ended_request);
                        ended.addView(bt);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Não foram encontrados pedidos!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException ex) {
            Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcceptedRequestsActivity.this, RequestsMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onRequestPressed(View v) {
        int id = v.getId();

        Intent intent = new Intent(AcceptedRequestsActivity.this, DetailedAcceptedRequestsInfoActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AcceptedRequestsActivity.this, RequestsMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
