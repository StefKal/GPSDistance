package edu.stlawu.cs450fall18_hw3_gpsdistance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;

import java.util.List;
import java.util.Observable;

public class LocationHandler extends Observable
        implements LocationListener {

    Activity act;

    LocationManager lm;

    public LocationHandler(Activity act) {
        this.act = act;

        if (this.act.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this.act,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity2.REQUEST_ASK_FINE_LOCATION
            );
        }
        initializeLocationManager();
    }

    public void initializeLocationManager() {
        lm = (LocationManager)this.act.getApplicationContext().getSystemService(act.LOCATION_SERVICE);

        if (act.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 500, 0, this);
            lm.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER, 500, 0, this);
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 500, 0, this);

            // I don't think you are supposed to manually call onLocationChanged
            setChanged();
            notifyObservers(getLastKnownLocation());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setChanged();
        notifyObservers(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    /**
     * Borrowed from StackOverflow
     * http://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null
     * @return
     */
    private Location getLastKnownLocation() {

        List<String> providers = lm.getProviders(true);
        Location bestLocation = null;
        if (act.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            for (String provider : providers) {
                Location l = lm.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }


}
