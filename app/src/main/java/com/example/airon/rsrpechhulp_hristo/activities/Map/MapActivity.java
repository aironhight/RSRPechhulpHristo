package com.example.airon.rsrpechhulp_hristo.activities.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.airon.rsrpechhulp_hristo.R;
import com.example.airon.rsrpechhulp_hristo.activities.Main.MainActivity;
import com.example.airon.rsrpechhulp_hristo.activities.Map.adaptors.InfoWindowAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, android.view.View.OnClickListener, DialogInterface.OnDismissListener{

    private GoogleMap map;
    private LocationManager locationManager;
    private double lastKnownLongtitude, lastKnownLatitude;
    private Marker locationMarker;
    private Geocoder geocoder;
    private List<Address> deviceAddress;
    private ProgressBar gpsLoading;
    private RelativeLayout callButton;
    private LinearLayout homeUpButton;
    private CustomDialog callDialogWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_map);
        setSupportActionBar(toolbar);


        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.label_activity_map);
        homeUpButton = (LinearLayout)findViewById(R.id.toolbar_home_up);
        homeUpButton.setOnClickListener(this);

        gpsLoading = (ProgressBar) findViewById(R.id.clockProgressBar);
        gpsLoading.setVisibility(View.VISIBLE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(locationServicesEnabled() && networkAvailable()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            geocoder = new Geocoder(this, Locale.getDefault());
        }

        //Initializes the call button if the device is not a tablet
        if(!isTablet(MapActivity.this)) {
            callButton = (RelativeLayout)findViewById(R.id.btn_map_call);
            callButton.setOnClickListener(this);
        }

        callDialogWindow = new CustomDialog(MapActivity.this); //Initialize the custom dialog window for calling
        callDialogWindow.setOnDismissListener(this); //set a dismiss listener
    }


    /**
     * Checks if all the necessary services are enabled on.
     */
    @Override
    protected void onResume() {
        super.onResume();

        //check if location services are permitted
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //...make a request if they're not
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationServicesEnabled(); //check if the location services are enabled...
        networkAvailable();// check if there is network connection (wifi / mobile data)

    }


    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //update the lastKnownLongtitude and lastKnownLatitude on every location change
            lastKnownLongtitude = location.getLongitude();
            lastKnownLatitude = location.getLatitude();

            //update the location marker and camera after updating the lastKnownLongtitude and lastKnownLatitude coordinates
            updateLocationMarker();
            updateCamera();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            locationServicesEnabled();
            networkAvailable();
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        locationServicesEnabled();
        networkAvailable();

        //check if location services are permitted
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //...make a request if they're not
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        //request location updates every second, with a minimum distance of 5 meters
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
    }


    /**
     * Updates the location of the device, setting the marker to the current Lattitude/Longtitude position.
     * On initial startup it requests location updates from the location manager
     */
    private void updateLocationMarker() {
        LatLng currentLocation = new LatLng(lastKnownLatitude, lastKnownLongtitude);

        try {
            deviceAddress = geocoder.getFromLocation(lastKnownLatitude, lastKnownLongtitude, 1);
            if (deviceAddress.size() > 0 && map != null) {
                map.clear();
                map.setInfoWindowAdapter(new InfoWindowAdapter(MapActivity.this));

                locationMarker = map.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .snippet(deviceAddress.get(0).getAddressLine(0)));


                locationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_mini)); //change the location marker's model
                locationMarker.showInfoWindow();

                deviceAddress.remove(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "Exception in locationServicesEnabled method" + e.getMessage());
        }
    }

    /**
     * Moves the camera to the current location of the device and sets the progressbar visibility to GONE.
     */
    private void updateCamera() {
        if (map != null) {
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastKnownLatitude, lastKnownLongtitude))); //Moves the camera to the current location
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLatitude, lastKnownLongtitude), 16)); //Zooms the camera so that the street's names are visible
            gpsLoading.setVisibility(View.GONE); // Disables the Progressbar
        }
    }


    /**
     * Checks if the location services are enabled. Returns true if enabled and false otherwise.
     * If the location services are disabled a popup dialog is shown asking the user to enable them
     * @return true if location services are enabled, false otherwise
     */
    private boolean locationServicesEnabled() {
        try {
            //Checks if GPS is enabled
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                displayServiceRequestDialog("location");

                return false;
            }
        } catch (Exception ex) {
            Log.e("Exception", "Exception in locationServicesEnabled method" + ex.getMessage());
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * Checks if there is internet connection. Returns true if there is and false otherwise.
     * If there is no internet connection a popup dialog is shown asking the user to enable wifi.
     * @return true if internet connection is available, false otherwise
     */
    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            displayServiceRequestDialog("network");
            return false;
        }

        return true;
    }

    /**
     * Displays a dialog asking the user to turn on the device's location services.
     * @param service The required service
     */
    private void displayServiceRequestDialog(String service) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if(service.equals("location")) {
            dialog.setMessage(R.string.gps_network_not_enabled);
            dialog.setTitle(R.string.gps_network_not_enabled_title);
            dialog.setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent); //Redirects to Location service settings
                }
            });
        } else if( service.equals("network")) {
            dialog.setMessage(R.string.no_internet_connection);
            dialog.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    networkAvailable();
                }
            });
        }

        dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                onBackPressed(); //Sends the user to the main activity
            }
        });
        dialog.show();
    }

    /**
     * Returns if the device is tablet or not. Source code from Google I/O
     * @param context The activity that checks
     * @return true if the device is a tablet
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onClick(View v) {
        if(v == callButton) {
            callDialogWindow.show(); //show the custom dialog
            callButton.setVisibility(View.INVISIBLE); //make the call button invisible

            Window window = callDialogWindow.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM; //attaches the dialog window to the bottom of the screen
            wlp.alpha = (float) 0.85; //makes it slightly transparent
            window.setAttributes(wlp);
        }

        else if(v == homeUpButton) {
            onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    if(locationServicesEnabled() && networkAvailable()) {
                        updateLocationMarker();
                        updateCamera();
                    }
                } else {
                    //permission denied - close the app
                    onDestroy();
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();//kills the current activity
        startActivity(new Intent(getApplicationContext(), MainActivity.class)); //starts the main activity
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        //makes the call button visible after the dialog has been dismissed
        callButton.setVisibility(View.VISIBLE);
    }
}

