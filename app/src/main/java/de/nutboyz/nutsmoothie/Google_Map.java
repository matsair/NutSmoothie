package de.nutboyz.nutsmoothie;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


/**
 * Created by Jan on 29.03.16.
 */
public class Google_Map extends AppCompatActivity
        implements OnMapReadyCallback, OnMapLongClickListener, OnMyLocationButtonClickListener {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button btn_get_location;
    private GoogleMap google_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        //Start Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn_get_location = (Button) findViewById(R.id.btn_map_getlocation);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.google_map = map;

        //Set Listener
        google_map.setOnMapLongClickListener(this);
        google_map.setOnMyLocationButtonClickListener(this);

        //Set Map Type
        google_map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Set current Location
        if(checkPermissions()) {
            google_map.setMyLocationEnabled(true);
        }

        //Get Location Manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            /***
             * Handle Location Change Event
             * @param location
             */
            @Override
            public void onLocationChanged(Location location) {
                try {
                    LatLng new_position = new LatLng(location.getLatitude(),location.getLongitude());
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
                    google_map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000,null);

                }catch (Exception e)
                {
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
        };

        checkPermissions();
    }

    /***
     * Checks if the permissions are given
     * @return
     */
    private boolean checkPermissions()
    {
        //Check Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Request Permission if not granted
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET}, 10);

                return false;
            } else
            {
                return true;
            }
        }
        return true;
    }

    /***
     * Check the result of the requested permissions
     * @param requestCode
     * @param permission
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults)
    {
        switch(requestCode){
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //call the onClick method of the button if permissions are granted
                    configureButton();
                }
        }
    }

    /***
     * Called if the permission is granted:
     * Returns the current location of the user
     */
    private void configureButton() {
        btn_get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions()) {
                    locationManager.requestLocationUpdates("gps", 10000, 10, locationListener);
                }
            }
        });
    }

    /***
     * Long Click Handler for the map
     * @param point
     */
    @Override
    public void onMapLongClick(LatLng point) {
        //ToDo: Handling of the selected Location
        google_map.addMarker(new MarkerOptions()
                .position(point)
                .title("Selected Location"));
    }

    /***
     * Long Click Handler for the map
     */
    @Override
    public boolean onMyLocationButtonClick() {
        //ToDo: Move to the current location
        if(checkPermissions()) {
            locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
        }
        return true;
    }
}
