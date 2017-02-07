package com.example.agustin.tpfinal.Utils;

import com.example.agustin.tpfinal.VistasAndControllers.MapaActivity;

/**
 * Created by Agustin on 02/06/2017.
 */

public final class ConstantsMenuNavegacion {
    public static final Integer INDICE_MENU_ALARMA = 0;
    public static final Integer INDICE_MENU_ESTACIONAR_AQUI = 1;
    public static final Integer INDICE_MENU_LIMPIAR = 2;

    /** TODO - AGREGAR EL NUMERO DEL INDICE CORRECTO, LOS QUE FIGURAN ACA NO SE SI SON CORRECTOS */
    public static final Integer INDICE_MENU_RESERVAS = 3;
    public static final Integer INDICE_MENU_PREFERENCIAS = 4;

    public static MapaActivity mapaActivity;

    public void setMapaActivityInstance(MapaActivity mapa){
        mapaActivity = mapa;
    }
    public MapaActivity getMapaActivityInstance(){
        return mapaActivity;
    }
}
