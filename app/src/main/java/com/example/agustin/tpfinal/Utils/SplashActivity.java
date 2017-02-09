package com.example.agustin.tpfinal.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.agustin.tpfinal.VistasAndControllers.MapaActivity;

/**
 * Created by Nahuel SG on 09/02/2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MapaActivity.class);
        startActivity(intent);
        finish();
    }
}