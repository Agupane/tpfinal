package com.example.agustin.tpfinal.Utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.agustin.tpfinal.Dao.UbicacionVehiculoEstacionadoDAO;
import com.example.agustin.tpfinal.Exceptions.UbicacionVehiculoException;
import com.example.agustin.tpfinal.Modelo.UbicacionVehiculoEstacionado;
import com.example.agustin.tpfinal.R;
import com.example.agustin.tpfinal.VistasAndControllers.MapaActivity;
import com.google.android.gms.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Agustin on 02/03/2017.
 */

public class AlarmEstacionamientoReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmEstReceiver";
    private static final Map<String,Marker> mapaMarcadores = MapaActivity.mapaMarcadores;
    private static UbicacionVehiculoEstacionado ubicacionEstacionamiento;
    private static Marker markerEstacionamiento;
    private static Context context;
    /** Id con el que se identifica una notificacion de otra */
    private static Integer idNotificacion = 1;

    /** Dao que almacena ubicacion de vehiculos estacionados */
    private static final UbicacionVehiculoEstacionadoDAO ubicacionVehiculoDAO = UbicacionVehiculoEstacionadoDAO.getInstance();

    private static NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String s = intent.getAction();
        if (s.equals(ConstantsNotificaciones.ACCION_GENERAR_ALARMA)) {
            String msg,idMarcador;
            idMarcador = intent.getStringExtra("idMarcador");
            markerEstacionamiento = mapaMarcadores.get(idMarcador);
            ubicacionEstacionamiento = (UbicacionVehiculoEstacionado) markerEstacionamiento.getTag();
            idNotificacion = 1;
            generarNotificacion();
            msg = context.getResources().getString(R.string.alarmEstacionamientoGenerada);
            Log.d(TAG, msg);
        }
        else if (s.equals(ConstantsNotificaciones.ACCION_NOTIFICACION_IGNORAR_ALARMA)) {
            posponerAlarma();
        }
        else if (s.equals(ConstantsNotificaciones.ACCION_NOTIFICACION_SALIR_ESTACIONAMIENTO)) {
            salirEstacionamiento();
        }

    }

    /**
     * Genera una notificacion
     * TODO - ACOMODAR PARA GENERAR LA ALARMA PORQUE ES MUY CORTO (DEBERIA DE SER UNA HORA DESDE EL MOMENTO EN DONDE SE ESTACIONA O SE
     * POSPONE LA ALARMA, MIRAR ESE NUMERO EN ConstansNotificaciones
     */
    private void generarNotificacion(){
        String tiempoDeIngreso;
        String textoDefault = context.getResources().getString(R.string.notificacionAlarmaEstTexto);
        String titulo = context.getResources().getString(R.string.notificacionAlarmaEstTitulo);

        /* Creo el texto a mostrar en caso de que se pueda generar una notificacion big text */
        StringBuilder texto = new StringBuilder();
        texto.append("Su vehiculo se encuentra estacionado desde las ");
        Date date = new Date(ubicacionEstacionamiento.getHoraIngreso());
        tiempoDeIngreso = new SimpleDateFormat("HH:mm").format(date);
        texto.append(tiempoDeIngreso);

        /* Creo los botones de la notificacion */
        Intent ignorarAlarmaIntent = new Intent(context,AlarmEstacionamientoReceiver.class);
        ignorarAlarmaIntent.setAction(String.valueOf(ConstantsNotificaciones.ACCION_NOTIFICACION_IGNORAR_ALARMA));
        PendingIntent piIgnorarAlarma = PendingIntent.getBroadcast(context, 0, ignorarAlarmaIntent, 0);

        Intent salirEstacionamientoIntent = new Intent(context,AlarmEstacionamientoReceiver.class);
        salirEstacionamientoIntent.setAction(String.valueOf(ConstantsNotificaciones.ACCION_NOTIFICACION_SALIR_ESTACIONAMIENTO));
        PendingIntent piSalirEstacionamiento = PendingIntent.getBroadcast(context, 0, salirEstacionamientoIntent, 0);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.marker_estacionamiento)
                        .setContentTitle(titulo)
                        .setContentText(textoDefault)
                        .setAutoCancel(true)
                        /* Genera la notifcacion en forma de big text, si el dispositivo es menor a 4.1, esto no se genera */
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                        /* TODO - CONFIGURAR ICONOS (NO ESTAN SIENDO VISIBLES) */
                        .addAction (R.drawable.ic_ignorar_24dp,
                                getString(R.string.btnIgnorarAlarma), piIgnorarAlarma)
                        .addAction (R.drawable.ic_parking_24dp,
                                getString(R.string.btnSalirEstacionamiento), piSalirEstacionamiento);


/*
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MapaActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MapaActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        */

         mNotificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(idNotificacion, mBuilder.build());

    }

    private String getString(Integer idString){
        return context.getResources().getString(idString);
    }

    /**
     * Programa una nueva alarma para el marcador
     */
    private void posponerAlarma(){
        String msg = context.getResources().getString(R.string.alarmEstacionamientoPospuesta);
        mNotificationManager.cancel(idNotificacion);
        idNotificacion = 2;
        UbicacionVehiculoEstacionado ubicacionVehiculo = (UbicacionVehiculoEstacionado) markerEstacionamiento.getTag();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmEstacionamientoReceiver.class);
        intent.putExtra("idMarcador",markerEstacionamiento.getId());
        intent.setAction(String.valueOf(ConstantsNotificaciones.ACCION_GENERAR_ALARMA));
        Integer idPendingIntent = ubicacionVehiculo.getId();
        PendingIntent pi = PendingIntent.getBroadcast(context,idPendingIntent,intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,ConstantsNotificaciones.TIEMPO_CONFIGURADO_ALARMA,pi);
        Log.v(TAG,msg);
    }

    /**
     * Marca la salida de la ubicacion del estacionamiento
     */
    private void salirEstacionamiento(){
        /*
        String msg = context.getResources().getString(R.string.parkLoggerInicioSalidaEstacionamiento);
        Log.v(TAG,msg);
        UbicacionVehiculoEstacionado ubicacionVehiculo = (UbicacionVehiculoEstacionado) markerEstacionamiento.getTag();
        ubicacionVehiculo.setHoraEgreso(System.currentTimeMillis());
        try{
            eliminarAlarma(ubicacionVehiculo);
            actualizarUbicacionPersistida(ubicacionVehiculo);
            markerEstacionamiento.remove();
            mNotificationManager.cancel(idNotificacion);
            Toast.makeText(context,msg,Toast.LENGTH_LONG);
        }
        catch (UbicacionVehiculoException e) {
            msg = context.getResources().getString(R.string.errorProducidoIntenteNuevamente);
            Toast.makeText(context, msg, Toast.LENGTH_LONG);
        }
        markerUltimoEstacionamiento = null;
        estCalle = null;
        this.lugarEstacionamientoGuardado = false;
        (menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_ESTACIONAR_AQUI)).setTitle(estacionarAqui);
        menuLateral.getItem(ConstantsMenuNavegacion.INDICE_MENU_LIMPIAR).setEnabled(false);
        */
        MapaActivity.mapaActivityInstance.marcarSalidaEstacionamiento(markerEstacionamiento);
    }
    /**
     * Actualiza la informacion en disco del objeto ubicacion vehiculo
     * @param estCalle
     */
    private void actualizarUbicacionPersistida(UbicacionVehiculoEstacionado estCalle) throws UbicacionVehiculoException {
        String msg = context.getResources().getString(R.string.parkLoggerInicioActualizacionUbicacion);
        Log.v(TAG,msg);
        ubicacionVehiculoDAO.actualizarUbicacionVehiculoEstacionado(estCalle,context);
    }

    private void eliminarAlarma(UbicacionVehiculoEstacionado ubicacionVehiculo){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmEstacionamientoReceiver.class);
        //  intent.putExtra("idMarcador",markerEstacionamiento.getId());
        //  intent.setAction(String.valueOf(ConstantsNotificaciones.ACCION_GENERAR_ALARMA));
        Integer idPendingIntent = ubicacionVehiculo.getId();
        PendingIntent pi = PendingIntent.getBroadcast(context,idPendingIntent,intent,0);
        alarmManager.cancel(pi);
    }
}
