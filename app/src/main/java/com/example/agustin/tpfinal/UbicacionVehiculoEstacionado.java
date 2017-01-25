package com.example.agustin.tpfinal;

import android.location.Address;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

/**
 * Created by Agustin on 01/24/2017.
 */

public abstract class UbicacionVehiculoEstacionado {
    protected Address direccion;
    protected Long horaIngreso,horaEgreso;
    public UbicacionVehiculoEstacionado(Location ubicacion) {
        direccion = new Address(Locale.getDefault());
        direccion.setLongitude(ubicacion.getLongitude());
        direccion.setLatitude(ubicacion.getLatitude());

    }

    public Address getDireccion() {
        return direccion;
    }

    public void setDireccion(Address direccion) {
        this.direccion = direccion;
    }

    /**
     * Devuelve latitud y longitud del estacionamiento
     * @return
     */
    public LatLng getCoordenadas(){
        LatLng latLng = new LatLng(direccion.getLatitude(),direccion.getLongitude());
        return latLng;
    }

    public Long getHoraEgreso() {
        return horaEgreso;
    }

    public void setHoraEgreso(Long horaEgreso) {
        this.horaEgreso = horaEgreso;
    }

    public Long getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(Long horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    /** Devuelve el tiempo en minutos que el vehiculo estuvo en el estacionamiento
     * Si el vehiculo aun sigue estacionado devuelve -1
     */
    public long calcularTiempoEstacionado(){
        long minutos,diferencia;
        if(horaEgreso!=null){
            diferencia = horaIngreso-horaEgreso;
            minutos = diferencia/1000;
            minutos = minutos % 60;
        }
        else{
            minutos = -1;
        }
        return minutos;
    }
}
