package com.example.agustin.tpfinal;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;


/**
 * Created by Agustin on 01/24/2017.
 */

public class AddressResultReceiver extends ResultReceiver {
    private String mAddressOutput;
    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Display the address string
        // or an error message sent from the intent service.
        mAddressOutput = resultData.getString(ConstantsAddresses.RESULT_DATA_KEY);


        // Show a toast message if an address was found.
        if (resultCode == ConstantsAddresses.SUCCESS_RESULT) {
            System.out.println("ENCONTRE LA DIRECCION!, es "+mAddressOutput);
        }

    }
}
