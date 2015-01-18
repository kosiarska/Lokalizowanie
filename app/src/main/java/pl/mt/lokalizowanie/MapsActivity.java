package pl.mt.lokalizowanie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private static final int LOCATION_SETTINGS_REQUEST = 100;

    private ResultReceiver addressResultReceiver = new ResultReceiver(new Handler(Looper.getMainLooper())) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            //todo : add facebook share logic
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        ButterKnife.inject(this);
        setUpMapIfNeeded();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        checkLocationProviders();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == LOCATION_SETTINGS_REQUEST) {
            checkLocationProviders();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.share_location)
    protected void shareLocation() {
        for (String enabledProvider : locationManager.getProviders(true)) {
            // get location as soon as possible
            locationManager.requestLocationUpdates(enabledProvider, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            locationManager.removeUpdates(this);

            Intent intent = new Intent(this, BackgroundGeocoder.class);
            intent.putExtra(getString(R.string.location_key), location);
            intent.putExtra(getString(R.string.result_receiver_key), addressResultReceiver);
            startService(intent);
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

    private void checkLocationProviders() {
        boolean gpsEnabled, networkEnabled;

        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled && !networkEnabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    startActivityForResult(myIntent, LOCATION_SETTINGS_REQUEST);
                }
            });
            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.show();
        }
    }
}
