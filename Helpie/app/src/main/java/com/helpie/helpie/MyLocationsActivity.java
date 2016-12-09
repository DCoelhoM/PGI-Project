package com.helpie.helpie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class MyLocationsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GPSTracker gps;
    private GoogleMap mMap;
    private String userMarkerID;
    Hashtable<Integer, JSONObject> locations_info = new Hashtable<>();
    Hashtable<String, Integer> markers_info = new Hashtable<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_locations);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {

            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gps = new GPSTracker(MyLocationsActivity.this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            LatLng userPos = new LatLng(latitude, longitude);
            Marker pos = mMap.addMarker(new MarkerOptions().position(userPos).title("Você está aqui").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            userMarkerID=pos.getId();

            mMap.moveCamera(CameraUpdateFactory.newLatLng(userPos));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
            mMap.setOnMarkerClickListener(this);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(final Marker marker) {
                    if(!marker.getId().equals(userMarkerID)) {
                        final int loc_id = markers_info.get(marker.getId());
                        //JSONObject loc = locations_info.get(loc_id);

                        new AlertDialog.Builder(MyLocationsActivity.this)
                                .setTitle("Apagar localização")
                                .setMessage("Tem a certeza que quer apagar esta localização?")
                                .setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                        API r = new API();
                                        String result = r.deleteLocation(loc_id);
                                        JSONObject obj = null;
                                        try {
                                            obj = new JSONObject(result);
                                            if (obj.getInt("success")==1) {
                                                marker.remove();
                                                Toast.makeText(getApplicationContext(), "Localização removida com sucesso!", Toast.LENGTH_LONG).show();

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
                }}
            );

            listLocations(mMap);

        }else{
            gps.showSettingsAlert();
        }
    }

    public void listLocations(GoogleMap mMap){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        API r = new API();
        String result = r.myLocations(SaveSharedPreference.getID(MyLocationsActivity.this));
        JSONObject obj;
        try {
            obj = new JSONObject(result);
            if (obj.getInt("success") == 1) {
                JSONArray locations = obj.getJSONArray("locations");
                JSONObject loc;
                int id;
                for (int i = 0; i < locations.length(); i++) {
                    loc = locations.getJSONObject(i);
                    id = loc.getInt("id");
                    addLocationMarker(mMap, id, loc);
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
        }
    }

    public void addLocationMarker(GoogleMap mMap, int id, JSONObject loc){
        String name;
        Double latitude;
        Double longitude;
        LatLng point;
        try {
            name = loc.getString("name");
            latitude = loc.getDouble("latitude");
            longitude = loc.getDouble("longitude");
            point = new LatLng(latitude, longitude);

            Marker help = mMap.addMarker(new MarkerOptions().position(point).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            markers_info.put(help.getId(),id);
            locations_info.put(id,loc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyLocationsActivity.this, LocationsMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
