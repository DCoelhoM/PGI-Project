package com.helpie.helpie;


import android.content.Intent;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
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


public class NearbyRequestsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GPSTracker gps;
    private GoogleMap mMap;
    private String userMarkerID;
    Hashtable<Integer, JSONObject> requests_info = new Hashtable<>();
    Hashtable<String, Integer> markers_info = new Hashtable<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_requests);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        //if (locationProviders == null || locationProviders.equals("")) {
        //    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        //}
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gps = new GPSTracker(NearbyRequestsActivity.this);
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
                public void onInfoWindowClick(Marker marker) {
                    if(!marker.getId().equals(userMarkerID)) {
                        int req_id = markers_info.get(marker.getId());
                        JSONObject req = requests_info.get(req_id);

                        Intent intent = new Intent(NearbyRequestsActivity.this, DetailedRequestInfoActivity.class);
                        intent.putExtra("request", req.toString());
                        startActivity(intent);
                        finish();
                    }
                }}
            );

            findRequests(userPos);

        }else{
            gps.showSettingsAlert();
        }
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    public void addRequestMarker(int id, JSONObject req){
        String title;
        Double latitude;
        Double longitude;
        LatLng point;
        try {
            title = req.getString("title");
            latitude = req.getDouble("latitude");
            longitude = req.getDouble("longitude");
            point = new LatLng(latitude, longitude);
            Marker help;
            if (req.getString("type").equals("normal")) {
                 help = mMap.addMarker(new MarkerOptions().position(point).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            } else {
                help = mMap.addMarker(new MarkerOptions().position(point).title(("[Voluntariado]"+title)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
            }
            markers_info.put(help.getId(),id);
            requests_info.put(id,req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void findRequests(LatLng userPos){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        API r = new API();
        String result = r.nearbyRequests(SaveSharedPreference.getID(NearbyRequestsActivity.this), userPos.latitude,userPos.longitude,SaveSharedPreference.getDistance(NearbyRequestsActivity.this));
        JSONObject obj;
        try {
            obj = new JSONObject(result);
            if (obj.getInt("success") == 1) {
                JSONArray requests = obj.getJSONArray("requests");
                JSONObject req;
                int id;
                for (int i = 0; i < requests.length(); i++) {
                    req = requests.getJSONObject(i);
                    id = req.getInt("id");
                    addRequestMarker(id, req);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Não foram encontrados pedidos!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NearbyRequestsActivity.this, RequestsMenuActivity.class);
        startActivity(intent);
        finish();
    }
}


