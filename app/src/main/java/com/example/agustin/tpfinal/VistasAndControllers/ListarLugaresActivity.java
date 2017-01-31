package com.example.agustin.tpfinal.VistasAndControllers;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ListView;

import com.example.agustin.tpfinal.Modelo.Estacionamiento;
import com.example.agustin.tpfinal.R;
import com.example.agustin.tpfinal.Utils.EstacionamientoAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

/**
 * Created by Nahuel SG on 31/01/2017.
 */

public class ListarLugaresActivity extends AppCompatActivity {
    private EstacionamientoAdapter adapterEst;
    private ListView listaEst;
    private Estacionamiento[] Estacionamientos;
    private Button verMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_lugares);

        llenarEstacionamientos();
        listaEst = (ListView) findViewById(R.id.listLugares);
        adapterEst = new EstacionamientoAdapter(this, Arrays.asList(Estacionamientos));
        listaEst.setAdapter(adapterEst);
        setTitle("Listados de Estacionamientos");
    }


    private void llenarEstacionamientos(){
        Estacionamientos = new Estacionamiento[3];
        Estacionamientos[0] = new Estacionamiento();
        Estacionamientos[0].setDireccionEstacionamiento("DIRECCIÓN: Terminal Belgrano");
        Estacionamientos[0].setNombreEstacionamiento("NOMBRE: El Estacionamiento 0");
        Estacionamientos[0].setTarifaEstacionamiento("TARIFA: $30/hs");
        Estacionamientos[0].setPosicionEstacionamiento(new LatLng(-31.642935, -60.700636));

        Estacionamientos[1] = new Estacionamiento();
        Estacionamientos[1].setDireccionEstacionamiento("DIRECCIÓN: Rivadavia 3176");
        Estacionamientos[1].setNombreEstacionamiento("NOMBRE: El Estacionamiento 1");
        Estacionamientos[1].setTarifaEstacionamiento("TARIFA: $20/hs");
        Estacionamientos[1].setPosicionEstacionamiento(new LatLng(-31.639896, -60.702384));


        Estacionamientos[2] = new Estacionamiento();
        Estacionamientos[2].setDireccionEstacionamiento("DIRECCIÓN: La Rioja y 25 de Mayo");
        Estacionamientos[2].setNombreEstacionamiento("NOMBRE: El Estacionamiento 2");
        Estacionamientos[2].setTarifaEstacionamiento("TARIFA: Te cobramos dos huevos");
        Estacionamientos[2].setPosicionEstacionamiento(new LatLng(-31.646182, -60.705633));

    }


}
