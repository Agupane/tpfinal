package com.example.agustin.tpfinal;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FetchAddressIntentService extends IntentService {
    private Location ubicacionAObtenerAddress;
    private Double latitude,longitude;
    private String errorMessage;
    protected ResultReceiver mReceiver;
    private final String TAG = "AddressService";
    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            List<Address> addresses = null;
            ubicacionAObtenerAddress = intent.getParcelableExtra(ConstantsAddresses.LOCATION_DATA_EXTRA);
            mReceiver = intent.getParcelableExtra(ConstantsAddresses.RECEIVER);
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            longitude = ubicacionAObtenerAddress.getLongitude();
            latitude = ubicacionAObtenerAddress.getLatitude();
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, ConstantsAddresses.CANT_MAXIMA_RESULTADOS);
                if(addresses == null){
                    errorMessage = getString(R.string.no_address_found);
                    Log.e(TAG, errorMessage);
                    deliverResultToReceiver(ConstantsAddresses.FAILURE_RESULT, errorMessage);
                }
            }
            catch (IOException ioException) {
                // Catch network or other I/O problems.
                errorMessage = getString(R.string.service_not_available);
                Log.e(TAG, errorMessage, ioException);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                errorMessage = getString(R.string.invalid_lat_long_used);
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + latitude +
                        ", Longitude = " +
                        longitude, illegalArgumentException);
            }
            if (addresses.size()  == 0) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
                deliverResultToReceiver(ConstantsAddresses.FAILURE_RESULT, errorMessage);
            }
            else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                Log.i(TAG, getString(R.string.address_found));
                deliverResultToReceiver(ConstantsAddresses.SUCCESS_RESULT,
                        TextUtils.join(System.getProperty("line.separator"),
                                addressFragments));
            }
        }
    }
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsAddresses.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }


}
