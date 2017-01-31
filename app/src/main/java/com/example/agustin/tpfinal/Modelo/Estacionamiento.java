package com.example.agustin.tpfinal.Modelo;

/**
 * Created by Nahuel SG on 30/01/2017.
 */

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

public class Estacionamiento {
    private String nombreEstacionamiento;
    private Address direccionEstacionamiento;
    private String tarifaEstacionamiento;
    private Boolean eliminado;

    public void Estacionamiento(){
        this.eliminado = false;
    }

    /**
     * Getters y Setters
     */
    public String getNombreEstacionamiento() {
        return nombreEstacionamiento;
    }

    public void setNombreEstacionamiento(String nombreEstacionamiento) {
        this.nombreEstacionamiento = nombreEstacionamiento;
    }

    /**
     * Devuelve la latitud y longitud del estacionamiento
     * @return
     */
    public LatLng getPosicionEstacionamiento() {
        LatLng latLng = new LatLng(direccionEstacionamiento.getLatitude(),direccionEstacionamiento.getLongitude());
        return latLng;
    }

    public void setPosicionEstacionamiento(LatLng posicionEstacionamiento) {
        direccionEstacionamiento.setLatitude(posicionEstacionamiento.latitude);
        direccionEstacionamiento.setLongitude(posicionEstacionamiento.longitude);
    }

    public Address getDireccionEstacionamiento() {
        return direccionEstacionamiento;
    }

    public void setDireccionEstacionamiento(Address direccionEstacionamiento) {
        this.direccionEstacionamiento = direccionEstacionamiento;
    }

    public String getTarifaEstacionamiento() {
        return tarifaEstacionamiento;
    }

    public void setTarifaEstacionamiento(String tarifaEstacionamiento) {
        this.tarifaEstacionamiento = tarifaEstacionamiento;
    }

    public Boolean getEliminado(){
        return this.eliminado;
    }

    public void setEliminado(Boolean eliminado)
    {
        this.eliminado = eliminado;
    }
}
