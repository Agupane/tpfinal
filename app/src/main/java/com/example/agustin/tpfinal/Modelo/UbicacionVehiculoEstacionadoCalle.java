package com.example.agustin.tpfinal.Modelo;

import android.location.Location;

/**
 * Created by Agustin on 01/24/2017.
 */

/**
 * Clase que almacena informacion acerca de un aparcamiento callejero
 */
public class UbicacionVehiculoEstacionadoCalle extends UbicacionVehiculoEstacionado {

    public UbicacionVehiculoEstacionadoCalle(Location ubicacion) {
        super(ubicacion);
    }
    /** Devuelve el nombre de la calle en donde se encuentra estacionado
     * en formato nombre calle - numero - nombre ciudad
     * @return
     */
    public String getTitulo(){
        StringBuilder titulo = new StringBuilder();
        titulo.append(direccion.getAddressLine(0));
        titulo.append(", ");
        titulo.append(direccion.getLocality());
        if(titulo.length()>0) {
            return titulo.toString();
        }
        if(direccion != null){
            return direccion.getLatitude()+" "+direccion.getLongitude();
        }
        return "Vehiculo";
    }
}
