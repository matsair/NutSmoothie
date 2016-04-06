package de.nutboyz.nutsmoothie.Map;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import de.nutboyz.nutsmoothie.GPS.gpsService;
import de.nutboyz.nutsmoothie.R;
import de.nutboyz.nutsmoothie.commons.NutLocation;
import de.nutboyz.nutsmoothie.database.LocationDataSource;


/**
 * Created by Jan on 29.03.16.
 */
public class Google_Map extends AppCompatActivity
        implements OnMapReadyCallback, OnMapLongClickListener, OnMyLocationButtonClickListener,inputDialog_Interface {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button btn_save_location;
    private GoogleMap google_map;
    private Marker last_marker;
    private ArrayList<NutLocation> selectedLocations;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    //ServiceConnection for the service
    private ServiceConnection GpsService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //GeoServiceBinder binder = (GeoServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        //Start Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn_save_location = (Button) findViewById(R.id.btn_map_getlocation);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        try {
            this.google_map = map;

            bindService(new Intent(getApplicationContext(),gpsService.class)
                    ,GpsService,
                    Context.BIND_AUTO_CREATE);

            loadLocations();
            //Set Listener
            google_map.setOnMapLongClickListener(this);
            google_map.setOnMyLocationButtonClickListener(this);
            btn_save_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        FragmentManager manager = getSupportFragmentManager();
                        inputDialog myDialog = new inputDialog();
                        myDialog.show(manager, "test");

                    }catch (Exception e)
                    {
                        e.getMessage();
                    }
                }
            });

            //Set Map Type
            google_map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            //Set current Location
            if (checkPermissions()) {
                google_map.setMyLocationEnabled(true);
            }

           /* //Get Location Manager
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                *//***
                 * Handle Location Change Event
                 *
                 * @param location
                 *//*
                @Override
                public void onLocationChanged(Location location) {
                    try {
                        LatLng new_position = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new_position)
                                .zoom(20)
                                .bearing(180)
                                .tilt(0)
                                .build();
                        //add marker with the current location
                        google_map.addMarker(new MarkerOptions()
                                .position(new_position)
                                .title("Current Location"));
                        google_map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

                    } catch (Exception e) {
                        e.getMessage();
                    }
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
            };*/
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /***
     * Loads all the stored locations out of the database and marks them on the map
     */
    private void loadLocations() {
        try {
            //ToDo: Load Locations from the database and mark them on the card
            selectedLocations = new ArrayList<NutLocation>();
            LocationDataSource database = new LocationDataSource(getApplicationContext());
            database.open();
            selectedLocations = database.getAllLocations();
            database.close();

            for(int i = 0; i < selectedLocations.size(); i++)
            {
                last_marker = google_map.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(
                                        selectedLocations.get(i).getLatitude(),
                                        selectedLocations.get(i).getLongitude()))
                                .title(selectedLocations.get(i).getName()));
            }
        }catch (Exception e)
        {
            e.getMessage();
        }
    }

    /***
     * Checks if the permissions are given
     *
     * @return
     */
    private boolean checkPermissions() {
        //Check Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Request Permission if not granted
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET}, 10);

                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    /***
     * Check the result of the requested permissions
     *
     * @param requestCode
     * @param permission
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //call the onClick method of the button if permissions are granted

                } else {
                    // ToDo create AlertDialog
                }
        }
    }

    /***
     * Long Click Handler for the map
     *
     * @param point
     */
    @Override
    public void onMapLongClick(LatLng point) {
        //ToDo: Handling of the selected Location
        last_marker = google_map.addMarker(new MarkerOptions()
                .position(point)
                .title("Selected Location"));
    }

    /***
     * Just moving from the map position to the last gps position
     */
    @Override
    public boolean onMyLocationButtonClick() {
        try {
            if (checkPermissions()) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if(locationManager.getLastKnownLocation("gps") != null) {
                    LatLng new_position = new LatLng(locationManager.getLastKnownLocation("gps").getLatitude(),
                            locationManager.getLastKnownLocation("gps").getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new_position)
                            .zoom(18)
                            .bearing(180)
                            .tilt(0)
                            .build();
                    google_map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                }
                else if(locationManager.getLastKnownLocation("wifi") != null)
                {
                    LatLng new_position = new LatLng(locationManager.getLastKnownLocation("wifi").getLatitude(),
                            locationManager.getLastKnownLocation("gps").getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new_position)
                            .zoom(20)
                            .bearing(180)
                            .tilt(0)
                            .build();
                    google_map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                }else
                {
                    //AlertDialog.Builder noLocationDialog = new AlertDialog.Builder(getApplicationContext());
                    /*noLocationDialog.setTitle("No Location")
                            .setMessage("Unfortunately, there is not GPS fix available.")
                            .setNeutralButton("OK",null)
                            .create()
                            .show();*/
                }
            }
        }catch (Exception e)
        {
            e.getMessage();
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Google_Map Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://de.nutboyz.nutsmoothie/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Google_Map Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://de.nutboyz.nutsmoothie/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /***
     * Interface is called, when the dialog closes via "OK"
     * Stores the selected location in the database
     * @param data Contains the title of the location name
     */
    @Override
    public void save_data(String data) {
        try {
            last_marker.setTitle(data);

            //adds the new location to the location list
            selectedLocations.add(new NutLocation());
            int location_size = selectedLocations.size() - 1;
            selectedLocations.get(location_size).setLongitude(last_marker.getPosition().longitude);
            selectedLocations.get(location_size).setLatitude(last_marker.getPosition().latitude);
            selectedLocations.get(location_size).setId(selectedLocations.size());
            selectedLocations.get(location_size).setName(data);

            //store the last location of the list in the database
            LocationDataSource database = new LocationDataSource(getApplicationContext());
            database.open();
            database.addLocation(selectedLocations.get(location_size));
            database.close();
        }catch (Exception e)
        {
            e.getMessage();
        }
    }
}
