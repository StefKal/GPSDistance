package edu.stlawu.cs450fall18_hw3_gpsdistance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MainActivity extends Activity implements LocationListener {


    LocationManager locationManager;
    private Button checkpoint_btn;
    final private int REQUEST_ASK_FINE_LOCATION = 999;
    Location location;
    private LinearLayout SV_vertical_layout;

    private Double prevLat;
    private Double prevLon;
    private Double nextLat;
    private Double nextLon;



    private Double velocity;
    private Double distance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkpoint_btn = findViewById(R.id.checkpoint_btn);
        SV_vertical_layout = findViewById(R.id.SV_vertical_layout);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ASK_FINE_LOCATION
            );
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ASK_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
                }
                catch(SecurityException e) {
                    return;  // should not get here
                }
                catch(Exception e) {
                    return;  // should not get here
                }
            }
            else {
                return; // :-( permission not granted
            }

    }

    @Override
    protected void onResume() {
        super.onResume();

        prevLat = null;
        prevLon = null;
        location = getLastKnownLocation();
        checkpoint_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (location != null) {
                    if(prevLat == null && prevLon == null) {
                        prevLon = location.getLongitude();
                        prevLat = location.getLatitude();
                        createLayout();
                    }else{
                        prevLat = nextLat;
                        prevLon = nextLon;
                        createLayout();
                    }
                    nextLat = location.getLatitude();
                    nextLon = location.getLongitude();
                    distance = getDist(prevLat, prevLon, nextLat, nextLon);
                }
            }
        });


    }

    /**
     * Borrowed from StackOverflow
     * http://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null
     * @return
     */
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);


                if (l == null) {
                    continue;
                }
                if (bestLocation == null
                        || l.getAccuracy() < bestLocation.getAccuracy()) {

                    bestLocation = l;
                }
            }
            if (bestLocation == null) {
                return null;
            }
        }
            return bestLocation;

    }

    private void createLayout(){
        LinearLayout aLayout = new LinearLayout(MainActivity.this);
        aLayout.setOrientation(LinearLayout.HORIZONTAL);


        TextView tv_lon = new TextView(MainActivity.this);
        TextView tv_lat = new TextView(MainActivity.this);
        TextView tv_dist = new TextView(MainActivity.this);
        TextView tv_veloc = new TextView(MainActivity.this);

        tv_dist.setPadding(50,50, 70, 50);
        tv_lon.setPadding(50,50, 70, 50);
        tv_lat.setPadding(50,50, 70, 50);
        tv_veloc.setPadding(50, 50, 0, 50);

        tv_dist.setText(String.valueOf(distance));
        tv_lat.setText(String.valueOf(nextLat));
        tv_lon.setText(String.valueOf(nextLon));
        tv_veloc.setText(String.valueOf(location.getSpeed()));


        aLayout.addView(tv_lat);
        aLayout.addView(tv_lon);
        aLayout.addView(tv_dist);
        aLayout.addView(tv_veloc);

        SV_vertical_layout.addView(aLayout);
    }


    @Override
    public void onLocationChanged(Location location) {
        nextLat = location.getLatitude();
        nextLon = location.getLongitude();

        Toast.makeText(MainActivity.this, "Lat: " + nextLat +
                " Lon: " + nextLon, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    /**
     * Borrowed from https://rosettacode.org/wiki/Haversine_formula#Java
     * @param prevLat
     * @param prevLon
     * @param nextLat
     * @param nextLon
     * @return
     */
    private static Double getDist(double prevLat, double prevLon, double nextLat, double nextLon) {
        double dLat = Math.toRadians(nextLat - prevLat);
        double dLon = Math.toRadians(nextLon - prevLon);
        prevLat = Math.toRadians(prevLat);
        nextLat = Math.toRadians(nextLat);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(prevLat) * Math.cos(nextLat);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6372.8 * c;
    }
}