package de.nutboyz.nutsmoothie.GPS;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.util.ArrayList;

import de.nutboyz.nutsmoothie.R;
import de.nutboyz.nutsmoothie.commons.NutLocation;
import de.nutboyz.nutsmoothie.database.LocationDataSource;

/**
 * Created by Jan on 05.04.16.
 */
public class gpsService extends Service {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private ArrayList<NutLocation> nutLocations;
    private static final int MIN_ZEIT = 50000;
    private static final int MIN_DISTANZ = 10;
    private final IBinder mGpsBinder = new GeoServiceBinder();

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onCreate() {
        super.onCreate();

        //load all the stored nutLocations
        loadLocations();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                evaluateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

                //Toast.makeText(gpsService.this, "Status changed: provider:" + provider + " status: " +status
                        //, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(gpsService.this, "Provider enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(gpsService.this, "Provider disabled", Toast.LENGTH_SHORT).show();
            }
        };

        //starts the provider
        starteProvider();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else
        {
            locationManager.requestLocationUpdates("gps", 5000, 10, locationListener);
        }

    }

    /***
     * Evaluates the current locations and compares them with the locations stored in the database
     * @param location
     */
    private void evaluateLocation(Location location) {
        loadLocations();
        try {
            //go through all locations and collect the stored locations in the area of 10 meters
            for (int i = 0; i < nutLocations.size(); i++) {
                if (location.distanceTo(nutLocations.get(i).getLocation()) < 30) {
                    int color = getResources().getColor(R.color.notif_color);
                    //ToDo Send Notification of
                        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        builder.setContentTitle("You received a stored Location")
                                .setColor(color)
                                .setContentText("You reached the location: " +nutLocations.get(i).getName());

                        Notification notif = builder.build();
                        NotificationManagerCompat.from(getApplicationContext()).notify(i,notif);*/
                    Toast.makeText(gpsService.this,
                            "You are reaching the location: " + nutLocations.get(i).getName() + " with accurancy of: " + location.getAccuracy() + " m",
                            Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    /***
     * starts the provider for the best current location
     */
    private void starteProvider()
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
        String provider = locationManager.getBestProvider(locationCriteria,true);
        if(locationManager.PASSIVE_PROVIDER.equalsIgnoreCase(provider))
        {
            provider = locationManager.GPS_PROVIDER;
        }
        Toast.makeText(gpsService.this, "Selected provider: " + provider, Toast.LENGTH_SHORT).show();
        locationManager.requestLocationUpdates(provider,MIN_ZEIT,MIN_DISTANZ,locationListener);
    }

    /***
     * loads all nutLocations out of the database and stores them into nutLocations
     */
    private void loadLocations() {
        LocationDataSource database = new LocationDataSource(getApplicationContext());
        database.open();
        nutLocations = database.getAllLocations();
        database.close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mGpsBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    private class GeoServiceBinder extends Binder {
        public gpsService getService()
        {
            return gpsService.this;
        }
    }
}
