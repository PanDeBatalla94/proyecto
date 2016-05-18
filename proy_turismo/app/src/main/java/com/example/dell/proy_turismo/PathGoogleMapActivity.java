package com.example.dell.proy_turismo;

/**
 * Created by DELL on 27/04/2016.
 */


import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class PathGoogleMapActivity extends FragmentActivity {


    private static  LatLng LOWER_MANHATTAN;
    private static LatLng BROOKLYN_BRIDGE;

    GoogleMap googleMap;
    final String TAG = "PathGoogleMapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras();
        String s = extras.getString("latlong");


        String[] separated = s.split(",");
        String s1 = separated[0];
        String s2 = separated[1];
        s1 = s1.trim();
        double f1= Double.parseDouble(s1);
        double f2= Double.parseDouble(s2);
        BROOKLYN_BRIDGE = new LatLng(f1,f2);



        GPSTracker mGPS = new GPSTracker(this);


        if(mGPS.canGetLocation ){
            mGPS.getLocation();
            //text.setText("Lat"+mGPS.getLatitude()+"Lon"+mGPS.getLongitude());
            //final LatLng LOWER_MANHATTAN = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());

            LOWER_MANHATTAN = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());
        }else{
            //text.setText("Unabletofind");
            System.out.println("Unable");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_google_map);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();

        MarkerOptions options = new MarkerOptions();
        options.position(LOWER_MANHATTAN);
        options.position(BROOKLYN_BRIDGE);

        googleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BROOKLYN_BRIDGE,
                13));
        addMarkers();

    }

    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true|"
                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
                + BROOKLYN_BRIDGE.longitude;
        String OriDest = "origin="+LOWER_MANHATTAN.latitude+","+LOWER_MANHATTAN.longitude+"&destination="+BROOKLYN_BRIDGE.latitude+","+BROOKLYN_BRIDGE.longitude;

        String sensor = "sensor=false";
        String params = OriDest+"&%20"+waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;


        System.out.print(url);
        return url;

    }

    private void addMarkers() {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
                    .title("First Point"));
            googleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
                    .title("Second Point"));

        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();


                List<HashMap<String, String>> path = routes.get(i);
                                if(path==null) break;

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }
}

