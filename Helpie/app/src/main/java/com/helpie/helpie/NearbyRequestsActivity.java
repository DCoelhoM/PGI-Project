package com.helpie.helpie;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;


public class NearbyRequestsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public static final int maxRadius = 10;
    GPSTracker gps;
    private GoogleMap mMap;
    private int counter = 0;
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

        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {

            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gps = new GPSTracker(NearbyRequestsActivity.this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            LatLng actualplace1 = new LatLng(latitude, longitude);

            //Marker aux = addMarker(mMap, actualplace1, 0, "You're Here!");
            //userID = aux.getId();

            LatLng userPos = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userPos));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
            mMap.setOnMarkerClickListener(this);

            findRequests(mMap, userPos);

        }else{
            gps.showSettingsAlert();
        }

    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        int req_id = markers_info.get(marker.getId());
        JSONObject req = requests_info.get(req_id);


        Intent intent = new Intent(NearbyRequestsActivity.this, DetailedRequestInfoActivity.class);
        intent.putExtra("request", req.toString());
        startActivity(intent);
        finish();

        return true;

        /*
        if(counter == 0){
            marker.showInfoWindow();
            counter ++;
            id = marker.getId();
        }
        else{
            if(id.equals(marker.getId()) && !userID.equals(marker.getId())){
                String items = "";
                Integer idList = aux.get(marker.getId());
                Intent intent = new Intent(NearbyRequestsActivity.this, null); //TODO DetailedListActivity.class
                try {
                    JSONArray jlists = obj.getJSONArray("lists");
                    for (int k=0;k<jlists.length();k++){
                        if (jlists.getJSONObject(k).getInt("id")==idList) {
                            intent.putExtra("List", jlists.getJSONObject(k).getString("items"));
                            break;
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong shit!", Toast.LENGTH_LONG).show();
                }
                intent.putExtra("idList", idList);
                startActivity(intent);
                finish();
            }
            else{
                marker.showInfoWindow();
                id = marker.getId();
            }
            counter = 0;
        }
        return true;
        */
    }

    /*public void confirmAction(final String markerID){
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to take this Request?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        acceptRequest(markerID);
                    }
                }).create().show();
    }*/
    /*public void acceptRequest(String markerID, Integer idList){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        API r = new API();
        String result = r.acceptList("carlos@gmail.com", idList);
        JSONObject obj = null;
        try {
            obj = new JSONObject(result);
            if (obj.getInt("success") == 1) {
                Toast.makeText(getApplicationContext(), "Request accepted!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
            else
                Toast.makeText(getApplicationContext(), "Somethin went wrong!", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
        }
    }*/
    public void addRequestMarker(GoogleMap mMap, int id, JSONObject req){
        String title;
        Double latitude;
        Double longitude;
        LatLng point;
        try {
            title = req.getString("title");
            latitude=req.getDouble("latitude");
            longitude=req.getDouble("longitude");
            point = new LatLng(latitude, longitude);

            Marker help = mMap.addMarker(new MarkerOptions().position(point).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            markers_info.put(help.getId(),id);
            requests_info.put(id,req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean checkDistance(LatLng userPos, LatLng destPos){
        int Radius = 6371;// radius of earth in Km
        double lat1 = userPos.latitude;
        double lat2 = destPos.latitude;
        double lon1 = userPos.longitude;
        double lon2 = destPos.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        if (kmInDec <= maxRadius)
            return true;
        else
            return false;

    }
    public void findRequests(GoogleMap mMap, LatLng userPos){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        API r = new API();
        String result = r.listRequests(SaveSharedPreference.getID(NearbyRequestsActivity.this));
        JSONObject obj;
        try {
            obj = new JSONObject(result);
            if (obj.getInt("success") == 1) {
                JSONArray requests = obj.getJSONArray("requests");
                JSONObject req;
                int id;
                for (int i = 0; i < requests.length(); i++) {
                    req=requests.getJSONObject(i);
                    id = req.getInt("id");
                    addRequestMarker(mMap, id, req);
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
        }
    }
    public String getStreetName(Double latitude, Double longitude) {
        Geocoder teste = new Geocoder(this, Locale.getDefault());
        StringBuilder strReturnedAddress = new StringBuilder();
        List<Address> addresses = null;
        try {
            addresses = teste.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null) {
            Address returnedAddress = addresses.get(0);

            for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
            }
        }
        return String.valueOf(strReturnedAddress);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NearbyRequestsActivity.this, RequestsMenuActivity.class);
        startActivity(intent);
        finish();
    }
}


