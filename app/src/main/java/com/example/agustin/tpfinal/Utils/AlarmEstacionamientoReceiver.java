package com.example.agustin.tpfinal.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.agustin.tpfinal.Modelo.UbicacionVehiculoEstacionado;
import com.example.agustin.tpfinal.R;
import com.example.agustin.tpfinal.VistasAndControllers.MapaActivity;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

/**
 * Created by Agustin on 02/03/2017.
 */

public class AlarmEstacionamientoReceiver extends BroadcastReceiver {

    private static final String TAG = "alarmEstReceiver";
    private static final Map<String,Marker> mapaMarcadores = MapaActivity.mapaMarcadores;
    private static UbicacionVehiculoEstacionado ubicacionEstacionamiento;
    private static Marker marcadorEstacionamiento;

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg,idMarcador;
        idMarcador = intent.getStringExtra("idMarcador");
        marcadorEstacionamiento = mapaMarcadores.get(idMarcador);
        ubicacionEstacionamiento = (UbicacionVehiculoEstacionado) marcadorEstacionamiento.getTag();
        /** TODO - GENERAR NOTIFICACION */ 
        msg = context.getResources().getString(R.string.alarmEstacionamientoGenerada);
        Log.d(TAG,msg);
    }
}
