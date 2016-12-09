package com.helpie.helpie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailedRequestInfoActivity extends AppCompatActivity {

    private JSONObject request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_request_info);

        Bundle data = getIntent().getExtras();
        if(data != null){
            try {
                request = new JSONObject(data.getString("request"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
