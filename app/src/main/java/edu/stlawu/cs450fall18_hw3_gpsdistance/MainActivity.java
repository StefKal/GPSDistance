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

import java.text.DecimalFormat;
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

    private TextView insta_velocity, avg_velocity, totalDist_tv;
    private Double distance, totalDist;
    private double vel_pnts, avgVel;
    private float velocity;

    private long start_time, end_time, time_elapsed, first_start_time;

    boolean first = true;

    DecimalFormat df_dist = new DecimalFormat("0.00m");
    DecimalFormat df_total_dist = new DecimalFormat("Total Distance: 0.00m");
    DecimalFormat df_vel = new DecimalFormat("Instant Velocity: 0.00m/s");
    DecimalFormat df_avg_vel = new DecimalFormat("Average Velocity: 0.00m/s");
    DecimalFormat df_vel_pnts = new DecimalFormat("0.00m/s");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkpoint_btn = findViewById(R.id.checkpoint_btn);
        SV_vertical_layout = findViewById(R.id.SV_vertical_layout);
        insta_velocity = findViewById(R.id.insta_velo);
        avg_velocity = findViewById(R.id.avg_velo);
        totalDist_tv = findViewById(R.id.total_dist);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ASK_FINE_LOCATION
            );
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
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
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
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

        initialLayout();

        totalDist = 0.00;
        location = getLastKnownLocation();
        nextLat = location.getLatitude();
        nextLon = location.getLongitude();
        distance = 0.00;

        prevLat = nextLat;
        prevLon = nextLon;

        checkpoint_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (first) {
                    first_start_time = System.currentTimeMillis();
                }

                first = false;

                start_time = System.currentTimeMillis();

                //Toast.makeText(MainActivity.this, String.valueOf(start_time), Toast.LENGTH_LONG).show();

                if(prevLat == nextLat && prevLon == nextLon) {
                    distance = 0.00;
                    vel_pnts = 0.00;
                    totalDist += distance;
                    totalDist_tv.setText("Total Distance: ".concat(String.valueOf(Math.round(totalDist*100.0)/100.0)).concat("m"));
                    createLayout();
                } else {
                    totalDist += distance;
                    totalDist_tv.setText("Total Distance: ".concat(String.valueOf(Math.round(totalDist*100.0)/100.0)).concat("m"));
                    createLayout();

                    prevLat = nextLat;
                    prevLon = nextLon;

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

        tv_dist.setText(String.valueOf(df_dist.format(distance)));
        tv_lat.setText(String.valueOf(nextLat));
        tv_lon.setText(String.valueOf(nextLon));
        tv_veloc.setText(String.valueOf(df_vel_pnts.format(vel_pnts)));
        insta_velocity.setText(df_vel.format(velocity));
        avg_velocity.setText(df_avg_vel.format(avgVel));




        aLayout.addView(tv_lat);
        aLayout.addView(tv_lon);
        aLayout.addView(tv_dist);
        aLayout.addView(tv_veloc);

        SV_vertical_layout.addView(aLayout);
    }

    private void initialLayout(){
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

        tv_dist.setText("Distance");
        tv_lat.setText("Latitude");
        tv_lon.setText("Longitude");
        tv_veloc.setText("Velocity Between points");


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

        end_time = System.currentTimeMillis();
        time_elapsed = end_time - start_time;
        time_elapsed = time_elapsed / 1000; // get milliseconds to seconds

        distance = getDist(prevLat, prevLon, nextLat, nextLon);
        vel_pnts =  distance/time_elapsed; // m/s
        velocity = location.getSpeed();

        avgVel = totalDist/(end_time - first_start_time);

        Toast.makeText(MainActivity.this, "Lat: " + nextLat +
                " Lon: " + nextLon + " dist:" + distance + " vel:" + vel_pnts + " inst vel:" + velocity, Toast.LENGTH_LONG).show();



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
        return 6372800 * c;
    }
}