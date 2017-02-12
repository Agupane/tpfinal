package com.example.agustin.tpfinal.VistasAndControllers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.agustin.tpfinal.Modelo.Estacionamiento;
import com.example.agustin.tpfinal.R;
import com.example.agustin.tpfinal.Utils.AlarmEstacionamientoReceiver;
import com.example.agustin.tpfinal.Utils.ConstantsNotificaciones;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nahuel SG on 12/02/2017.
 */

public class ReservarActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView nombre, direccion;
    private Button botonConfirmar;
    private int position;
    private Estacionamiento estacionamientoReserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar);

        //TODO refactorizar: mejorar el manejo para obtener el objeto Estacionamiento (quizás pasarlo en el Intent)
        position = getIntent().getIntExtra("indice", 0);
        estacionamientoReserva = ListarLugaresActivity.Estacionamientos[position];

        nombre = (TextView) findViewById(R.id.tv_nombreEstac_reservar);
        nombre.setText(estacionamientoReserva.getNombreEstacionamiento());
        direccion = (TextView) findViewById(R.id.tv_direccionEstac_reservar);
        direccion.setText(estacionamientoReserva.getDireccionEstacionamiento());
        botonConfirmar = (Button) findViewById(R.id.button_confirmar_reserva);
        botonConfirmar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == botonConfirmar.getId()) {
            agregarAlarma(estacionamientoReserva, position);
            //generar reserva
            //intent a la lista o OnBackPressed?
            super.onBackPressed();
        }
    }


    private void agregarAlarma(Estacionamiento estacionamiento, int position){
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmEstacionamientoReceiver.class);
        Marker markerEstacionamiento = buscarMarker(estacionamiento.getPosicionEstacionamiento());
        intent.putExtra("idMarcador",markerEstacionamiento.getId());
        Date date = new Date();
        intent.putExtra("horaReserva", new SimpleDateFormat("HH:mm").format(date));
        intent.putExtra("nombreEstac", estacionamiento.getNombreEstacionamiento());
        intent.setAction(String.valueOf(ConstantsNotificaciones.ACCION_GENERAR_RESERVA));
        Integer idPendingIntent = position;
        PendingIntent pi = PendingIntent.getBroadcast(this,idPendingIntent,intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ConstantsNotificaciones.TIEMPO_CONFIGURADO_ALARMA_RESERVA,pi);
    }

    /**
     *  Busca un Marker por su posicion dentro del Map-mapaMarcadores
     * @param posicion
     * @return retorna el Marker o Null, si no lo encuentra
     */
    private Marker buscarMarker(LatLng posicion){
        for(Marker aux : MapaActivity.mapaMarcadores.values()) {
            if (aux.getPosition().equals(posicion)) {
                return aux;
            }
        }
        return null;
    }
}
