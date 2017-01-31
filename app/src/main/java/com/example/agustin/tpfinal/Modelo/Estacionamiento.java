package com.example.agustin.tpfinal.Modelo;

/**
 * Created by Nahuel SG on 30/01/2017.
 */

import com.google.android.gms.maps.model.LatLng;

public class Estacionamiento {
    private String nombreEstacionamiento;
    private LatLng posicionEstacionamiento;
    private String direccionEstacionamiento;
    private String tarifaEstacionamiento;


    public void Estacionamiento(){

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

    public LatLng getPosicionEstacionamiento() {
        return posicionEstacionamiento;
    }

    public void setPosicionEstacionamiento(LatLng posicionEstacionamiento) {
        this.posicionEstacionamiento = posicionEstacionamiento;
    }

    public String getDireccionEstacionamiento() {
        return direccionEstacionamiento;
    }

    public void setDireccionEstacionamiento(String direccionEstacionamiento) {
        this.direccionEstacionamiento = direccionEstacionamiento;
    }

    public String getTarifaEstacionamiento() {
        return tarifaEstacionamiento;
    }

    public void setTarifaEstacionamiento(String tarifaEstacionamiento) {
        this.tarifaEstacionamiento = tarifaEstacionamiento;
    }
}
