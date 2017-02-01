package com.example.agustin.tpfinal.VistasAndControllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

public class ListarLugaresActivity extends AppCompatActivity implements View.OnClickListener {
    private EstacionamientoAdapter adapterEst;
    private ListView listaEst;
    private Estacionamiento[] Estacionamientos;
    /** Boton que permite switchear entre la lista de lugares y el mapa */
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_lugares);

        fab = (FloatingActionButton) findViewById(R.id.fabmap);
        fab.setOnClickListener((View.OnClickListener) this);

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
        Estacionamientos[0].setHorarios("HORARIOS: Lun-Dom abierto las 24hs");
        Estacionamientos[0].setTarifaEstacionamiento("TARIFA: $30/hs");
        Estacionamientos[0].setPosicionEstacionamiento(new LatLng(-31.642935, -60.700636));

        Estacionamientos[1] = new Estacionamiento();
        Estacionamientos[1].setDireccionEstacionamiento("DIRECCIÓN: Rivadavia 3176");
        Estacionamientos[1].setNombreEstacionamiento("NOMBRE: El Estacionamiento 1");
        Estacionamientos[1].setHorarios("HORARIOS: Lun-Dom de 8hs a 19hs");
        Estacionamientos[1].setTarifaEstacionamiento("TARIFA: $20/hs");
        Estacionamientos[1].setPosicionEstacionamiento(new LatLng(-31.639896, -60.702384));


        Estacionamientos[2] = new Estacionamiento();
        Estacionamientos[2].setDireccionEstacionamiento("DIRECCIÓN: La Rioja y 25 de Mayo");
        Estacionamientos[2].setNombreEstacionamiento("NOMBRE: El Estacionamiento 2");
        Estacionamientos[2].setHorarios("HORARIOS: Lun-Vie abierto las 24hs, Sáb de 9hs a 18hs");
        Estacionamientos[2].setTarifaEstacionamiento("TARIFA: Te cobramos dos huevos");
        Estacionamientos[2].setPosicionEstacionamiento(new LatLng(-31.646182, -60.705633));

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == fab.getId()) {
            Intent intent = new Intent(this, MapaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
