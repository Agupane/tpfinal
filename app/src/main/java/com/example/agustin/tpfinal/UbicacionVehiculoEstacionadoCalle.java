package com.example.agustin.tpfinal;

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
     * @return
     */
    public String getTitulo(){
//        return getDireccion().toString();
        return "ASDA";
    }
}
