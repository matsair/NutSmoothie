package de.nutboyz.nutsmoothie.Map;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import de.nutboyz.nutsmoothie.R;
import de.nutboyz.nutsmoothie.commons.NutLocation;
import de.nutboyz.nutsmoothie.database.LocationDataSource;


/**
 * Created by Jan on 29.03.16.
 */
public class Google_Map extends AppCompatActivity
        implements OnMapReadyCallback, OnMapLongClickListener, OnMyLocationButtonClickListener,inputDialog_Interface {

    private LocationManager locationManager;
    private Location lastLocation;
    private Button btn_save_location;
    private GoogleMap google_map;
    private Marker last_marker;
    private ArrayList<NutLocation> selectedLocations;
    private IntentFilter mIntentFilter;
    static public final String mBroadcastDialog = "de.nutboyz.nutsmoothie.string";
    static public final String mNewLocation = "de.nutboyz.nutsmoothie.newLocation";


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


   /*Service Bind to MainActivity

    *  *//***
     * The Service connection for retrieving the GPS Location
     * @Author: Jan
     *//*
    private ServiceConnection GpsService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };*/


    /***
     * Broadcast Receiver to receive the provider disabled message
     * @Autho: Jan
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals(mBroadcastDialog)) {
                    AlertDialog.Builder noProvider = new AlertDialog.Builder(context);
                    noProvider.setTitle("No GPS Provider enabled")
                            .setMessage("In order to receive location updates, you need to enable the GPS provider.")
                            .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent2 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent2);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
                else if(intent.getAction().equals(mNewLocation))
                {
                    double[] coordinates = intent.getDoubleArrayExtra("GPS");
                    LatLng new_position = new LatLng(coordinates[1],coordinates[0]);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new_position)
                            .zoom(18)
                            .bearing(180)
                            .tilt(0)
                            .build();
                    google_map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                }

            } catch (Exception e) {
                e.getMessage();
            }
        }
    };

    @Override
    /***
     * Register BroadcastReceiver again to check GPS status
     * @Author: Jan
     */
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /***
     * Unregister BroadcastReceiver
     * @Author: Jan
     */
    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    /***
     * Creates the Broadcastreceiver
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastDialog);
        mIntentFilter.addAction(mNewLocation);

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


            /* Service bind to Main Actitvity
            bindService(new Intent(getApplicationContext(),gpsService.class)
                    ,GpsService,
                    Context.BIND_AUTO_CREATE);*/

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
            int check = checkCallingOrSelfPermission("gps");
            google_map.setMyLocationEnabled(true);

        } catch (Exception e) {
            e.getMessage();
        }
    }

    /***
     * Loads all the stored locations out of the database and marks them on the map
     * @Author: Jan
     */
    private void loadLocations() {
        try {
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
     * Long Click Handler for the map, sets the marker
     * @param point
     * @Author: Jan
     */
    @Override
    public void onMapLongClick(LatLng point) {

        last_marker = google_map.addMarker(new MarkerOptions()
                .position(point)
                .title("Selected Location"));
    }

    /***
     * Just moving from the map position to the last gps position
     * @Author: Jan
     */
    @Override
    public boolean onMyLocationButtonClick() {
        try {
                int check = checkCallingOrSelfPermission("gps");
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
                    AlertDialog.Builder noLocationDialog = new AlertDialog.Builder(this);
                    noLocationDialog.setTitle("No Location")
                            .setMessage("Unfortunately, there is no GPS fix available. Please wait a moment.")
                            .setNeutralButton("OK",null)
                            .create()
                            .show();
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
            finish();

        }catch (Exception e)
        {
            e.getMessage();
        }
    }
}
