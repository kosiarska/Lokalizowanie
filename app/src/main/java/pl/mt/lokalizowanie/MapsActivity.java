package pl.mt.lokalizowanie;

import android.app.Activity;
import android.content.Context;
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
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import pl.mt.lokalizowanie.fragments.FacebookShareFragment;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private static final int LOCATION_SETTINGS_REQUEST = 100;
    private Location location;
    private SweetAlertDialog sweetAlertDialog;

    private ResultReceiver addressResultReceiver = new ResultReceiver(new Handler(Looper.getMainLooper())) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String address = resultData.getString(getString(R.string.address_result));
            if (!TextUtils.isEmpty(address)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new FacebookShareFragment()).commit();
            }
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
        updateLocation();
    }

    private void updateLocation() {
        for (String enabledProvider : locationManager.getProviders(true)) {
            // get location as soon as possible
            locationManager.requestLocationUpdates(enabledProvider, 0, 0, this);
        }
    }

    private void setUpMap(double lat, double lng) {
        LatLng position = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(position).title(getString(R.string.my_location)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14.0f));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        if (sweetAlertDialog != null) {
            sweetAlertDialog.cancel();
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
            //  if (mMap != null) {
            //     setUpMap();
            // }
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.share_location)
    protected void shareLocation() {
        if (location == null) {
            sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.dialog_color));
            sweetAlertDialog.setTitleText(getString(R.string.loading));
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();
            return;
        }
        Intent intent = new Intent(this, BackgroundGeocoder.class);
        intent.putExtra(getString(R.string.location_key), location);
        intent.putExtra(getString(R.string.result_receiver_key), addressResultReceiver);
        startService(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;
            locationManager.removeUpdates(this);
            setUpMap(location.getLatitude(), location.getLongitude());
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

            sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)

                    .setContentText(getResources().getString(R.string.gps_network_not_enabled))
                    .setCancelText(getString(R.string.Cancel))
                    .setConfirmText(getResources().getString(R.string.open_location_settings))
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                            finish();
                        }
                    }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent myIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                            startActivityForResult(myIntent, LOCATION_SETTINGS_REQUEST);
                        }
                    });
            sweetAlertDialog.show();
        }
    }

}
