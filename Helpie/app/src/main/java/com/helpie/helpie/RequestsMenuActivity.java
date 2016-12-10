package com.helpie.helpie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RequestsMenuActivity extends AppCompatActivity {

    private Button createrequest;
    private Button nearbyrequests;
    private Button myrequests;
    private Button acceptedrequests;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_menu);

        createrequest = (Button) findViewById(R.id.createrequest);
        nearbyrequests = (Button) findViewById(R.id.nearbyrequests);
        myrequests = (Button) findViewById(R.id.myrequests);
        acceptedrequests = (Button) findViewById(R.id.acceptedrequests);
        back = (Button) findViewById(R.id.back);

        createrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsMenuActivity.this, CreateRequestActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nearbyrequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsMenuActivity.this, NearbyRequestsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        myrequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsMenuActivity.this, MyRequestsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        acceptedrequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsMenuActivity.this, AcceptedRequestsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsMenuActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RequestsMenuActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
