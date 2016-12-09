package com.helpie.helpie;

import android.content.Intent;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;


public class SaveLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GPSTracker gps;
    private GoogleMap mMap;

    private Double longitude;
    private Double latitude;

    private EditText name;
    private Button back;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {

            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        name = (EditText) findViewById(R.id.name);
        save = (Button) findViewById(R.id.save);
        back = (Button) findViewById(R.id.back);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = name.getText().toString().trim();



                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                API r = new API();
                String result = r.saveLocation(SaveSharedPreference.getID(SaveLocationActivity.this),n,longitude,latitude);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(result);
                    if (obj.getInt("success")==1) {
                        Toast.makeText(getApplicationContext(), "Localização guardada com sucesso!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SaveLocationActivity.this, LocationsMenuActivity.class);
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
                Intent intent = new Intent(SaveLocationActivity.this, LocationsMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gps = new GPSTracker(SaveLocationActivity.this);
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            LatLng userPos = new LatLng(latitude, longitude);
            Marker pos = mMap.addMarker(new MarkerOptions().position(userPos).title("Você está aqui").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(userPos));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
            mMap.setOnMarkerClickListener(this);
        }else{
            gps.showSettingsAlert();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SaveLocationActivity.this, LocationsMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
