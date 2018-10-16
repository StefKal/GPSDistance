package edu.stlawu.cs450fall18_hw3_gpsdistance;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class MainActivity2
        extends AppCompatActivity
        implements Observer {

    // textviews
    private LocationHandler location;
    private ScrollView scrollView;



    private Double prevLat, prevLon, nextLat, nextLon;



    final public static int REQUEST_ASK_FINE_LOCATION = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.scrollView = findViewById(R.id.myScrollView);
       // this.SV_vertical_layout = findViewById(R.id.SV_vertical_layout);



        this.location = new LocationHandler(this);
        this.location.addObserver(this);

    }

    @Override
    public void update(Observable observable, Object o) {

        if (observable instanceof LocationHandler) {
            Location l = (Location) o;
            prevLat = l.getLatitude();
            prevLon = l.getLongitude();

            Toast.makeText(MainActivity2.this, "Lat: " + prevLat +
                    " Lon: " + prevLon, Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, Objects.requireNonNull(grantResults));

        if (requestCode == REQUEST_ASK_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.i("INFO: ", "Permission not granted for location.");
            }
            else {
                Log.i("INFO: ", "Permission granted for location.");
                //this.location.initializeLocationManager();
            }
    }


}