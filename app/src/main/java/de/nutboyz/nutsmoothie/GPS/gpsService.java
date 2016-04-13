package de.nutboyz.nutsmoothie.GPS;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;

import de.nutboyz.nutsmoothie.Map.Google_Map;
import de.nutboyz.nutsmoothie.R;
import de.nutboyz.nutsmoothie.commons.NutLocation;
import de.nutboyz.nutsmoothie.database.LocationDataSource;

/**
 * Created by Jan on 05.04.16.
 */
public class gpsService extends Service {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private Location lastLocation;
    private int locationRange = 200; //Range when Notification is sent out
    private String locprovider;
    private ArrayList<NutLocation> nutLocations;
    private static final int MIN_ZEIT = 1000*10; //2 Min
    private static final int MIN_DISTANZ = 100; // or 100 Meter
    private final IBinder mGpsBinder = new GeoServiceBinder();
    private boolean debug = true;
    private Intent mBroadcastReceiver;

    @Override
    /***
     * Starts the location listener and selects the best provider
     * @author: Jan
     */
    public void onCreate() {
        try {
            super.onCreate();

            mBroadcastReceiver = new Intent();
            mBroadcastReceiver.setAction(Google_Map.mBroadcastDialog);
            mBroadcastReceiver.setAction(Google_Map.mNewLocation);
            //load all the stored nutLocations
            loadLocations();

            //selects the provider
            selectProvider();
            int check = checkCallingOrSelfPermission(locprovider);

            //retrieve the last known location
            if (lastLocation == null && locationManager.getLastKnownLocation(locprovider) != null) {
                lastLocation = locationManager.getLastKnownLocation(locprovider);
            }

            //locationListener zum Abfragen der Position
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if(debug) {
                        Toast.makeText(gpsService.this, "Accurancy: " + location.getAccuracy(), Toast.LENGTH_SHORT).show();
                    }
                    //Check new location
                    if (isBetterLocation(location, lastLocation)) {
                        evaluateLocation(location);
                        mBroadcastReceiver.setAction(Google_Map.mNewLocation);
                        mBroadcastReceiver.putExtra("GPS",new double[] {location.getLongitude(),location.getLatitude()});
                        sendBroadcast(mBroadcastReceiver);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    //test
                    if(debug) {
                        if (status == LocationProvider.OUT_OF_SERVICE) {
                            Toast.makeText(gpsService.this, "new Status:  OUT_OF_SERVICE and provider " + provider, Toast.LENGTH_SHORT).show();
                        } else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                            Toast.makeText(gpsService.this, "new Status:  TEMPORARILY_UNAVAILABE and provider " + provider, Toast.LENGTH_SHORT).show();
                        } else if (status == LocationProvider.AVAILABLE) {
                            Toast.makeText(gpsService.this, "new Status:  AVAILABLE and provider " + provider, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onProviderEnabled(String provider) {
                    if(debug) {
                        Toast.makeText(gpsService.this, "Provider enabled", Toast.LENGTH_SHORT).show();
                    }
                    //wähle neuen provider
                    selectProvider();
                    int check = checkCallingOrSelfPermission(locprovider);
                    //starte location tracking
                    locationManager.requestLocationUpdates(locprovider, MIN_ZEIT, MIN_DISTANZ, locationListener);
                }

                @Override
                public void onProviderDisabled(final String provider) {
                    try {
                        mBroadcastReceiver.setAction(Google_Map.mBroadcastDialog);
                        sendBroadcast(mBroadcastReceiver);
                    } catch (Exception e)
                    {
                        e.getMessage();
                    }
                }
            };
            locationManager.requestLocationUpdates(locprovider, MIN_ZEIT, MIN_DISTANZ, locationListener);
        }catch (Exception e)
        {
            e.getMessage();
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
                double distanz = location.distanceTo(nutLocations.get(i).getLocation());
                if (distanz <= locationRange) {

                    int color = getResources().getColor(R.color.notif_color);
                    int icon = R.drawable.ic_notif_location_reached;
                    String title = "You reached a stored Location";
                    String text = "You reached the location: " +nutLocations.get(i).getName();
                    //Build the notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                    builder.setContentTitle(title)
                            .setColor(color)
                            .setSmallIcon(icon)
                            .setContentText(text);
                    Intent resultIntent = new Intent(this, Google_Map.class);

                    // Because clicking the notification opens a new ("special") activity, there's
                    // no need to create an artificial back stack.
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(
                                    this,
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    builder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotifMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    mNotifMgr.notify(nutLocations.get(i).getId(), builder.build());

                    //ToDo retrieve tasks related to location, think about loc 1:n task relation
                    Toast.makeText(gpsService.this,
                            "You are reaching the location: " + nutLocations.get(i).getName() + " with accurancy of: " + location.getAccuracy() + " m"  + " and distance " +  String.valueOf(distanz),
                            Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    /***
     * selects the provider for the best current location
     */
    private void selectProvider()
    {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            final Criteria locationCriteria = new Criteria();
            locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
            locationCriteria.setPowerRequirement(Criteria.POWER_LOW);
            locprovider = locationManager.getBestProvider(locationCriteria, true);
            //String provider = locationManager.PASSIVE_PROVIDER;
            if (locationManager.PASSIVE_PROVIDER.equalsIgnoreCase(locprovider)) {
                locprovider = locationManager.PASSIVE_PROVIDER;
            }
            Toast.makeText(gpsService.this, "Selected provider: " + locprovider, Toast.LENGTH_SHORT).show();

        } catch (Exception e)
        {
            e.getMessage();
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        long TimeDelta = 1000*60*2;
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TimeDelta;
        boolean isSignificantlyOlder = timeDelta < -TimeDelta;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
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
    /***
     * Callback if the service is created.
     * @author: Jan
      */
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
