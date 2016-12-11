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

public class MyRequestsActivity extends AppCompatActivity {

    private LinearLayout active;
    private LinearLayout accepted;
    private LinearLayout ended;
    private LinearLayout canceled;

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        active = (LinearLayout) findViewById(R.id.active_layout);
        accepted = (LinearLayout) findViewById(R.id.accepted_layout);
        ended = (LinearLayout) findViewById(R.id.ended_layout);
        canceled = (LinearLayout) findViewById(R.id.canceled_layout);

        back = (Button) findViewById(R.id.back);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        API r = new API();
        String result = r.listMyRequests(SaveSharedPreference.getID(MyRequestsActivity.this));
        JSONObject obj = null;
        try {
            obj = new JSONObject(result);
            if (obj.getInt("success") == 1) {
                JSONArray requests = obj.getJSONArray("requests");
                JSONObject req;

                int dpValue = 5; // margin in dips
                float d = MyRequestsActivity.this.getResources().getDisplayMetrics().density;
                int margin = (int)(dpValue * d);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, margin, 0, 0);

                for (int i = 0; i < requests.length(); i++) {
                    req=requests.getJSONObject(i);
                    String state = req.getString("state");

                    Button bt = new Button(MyRequestsActivity.this);
                    bt.setText(req.getString("title"));
                    bt.setId(req.getInt("id"));
                    bt.setLayoutParams(params);
                    bt.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v) {
                            onRequestPressed(v);
                        }
                    });

                    if (state.equals("active")){
                        bt.setBackgroundResource(R.drawable.active_request);
                        active.addView(bt);
                    } else if (state.equals("accepted")){
                        bt.setBackgroundResource(R.drawable.accepted_request);
                        accepted.addView(bt);
                    } else if (state.equals("ended")){
                        bt.setBackgroundResource(R.drawable.ended_request);
                        ended.addView(bt);
                    } else {
                        bt.setBackgroundResource(R.drawable.canceled_request);
                        canceled.addView(bt);
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
                Intent intent = new Intent(MyRequestsActivity.this, RequestsMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onRequestPressed(View v) {
        int id = v.getId();

        Intent intent = new Intent(MyRequestsActivity.this, DetailedMyRequestsInfoActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyRequestsActivity.this, RequestsMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
