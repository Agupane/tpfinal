package com.example.agustin.tpfinal;

import android.location.Address;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Agustin on 01/24/2017.
 */

public abstract class UbicacionVehiculoEstacionado {
    private Location ubicacion;
    private Address direccion;
    private Long horaIngreso,horaEgreso;
    public UbicacionVehiculoEstacionado(Location ubicacion) {
        this.ubicacion = ubicacion;

    }

    public Address getDireccion() {
        return direccion;
    }

    public void setDireccion(Address direccion) {
        this.direccion = direccion;
    }

    public Location getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Location ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * Devuelve latitud y longitud del estacionamiento
     * @return
     */
    public LatLng getCoordenadas(){
        LatLng latLng = new LatLng(ubicacion.getLatitude(),ubicacion.getLongitude());
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
}
