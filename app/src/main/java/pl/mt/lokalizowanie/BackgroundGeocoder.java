package pl.mt.lokalizowanie;


import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.List;

public class BackgroundGeocoder extends IntentService {

    public BackgroundGeocoder() {
        super(BackgroundGeocoder.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver resultReceiver = intent.getParcelableExtra(getString(R.string.result_receiver_key));
        Location myLocation = intent.getParcelableExtra(getString(R.string.location_key));
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                Bundle bundle = new Bundle();
                try {
                    bundle.putString("Address", addresses.get(0).getAddressLine(0));
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
                resultReceiver.send(1, bundle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
