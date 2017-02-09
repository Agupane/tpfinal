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

import com.example.agustin.tpfinal.Dao.EstacionamientoDAO;
import com.example.agustin.tpfinal.Exceptions.EstacionamientoException;
import com.example.agustin.tpfinal.Modelo.Estacionamiento;
import com.example.agustin.tpfinal.R;
import com.example.agustin.tpfinal.Utils.EstacionamientoAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Nahuel SG on 31/01/2017.
 */

public class ListarLugaresActivity extends AppCompatActivity implements View.OnClickListener {
    private EstacionamientoAdapter adapterEst;
    private ListView listaEst;
    private Estacionamiento[] Estacionamientos;
    /** Dao que almacena ubicacion de Estacionamientos */
    private static final EstacionamientoDAO estacionamientoDAO = EstacionamientoDAO.getInstance();
    /** Boton que permite switchear entre la lista de lugares y el mapa */
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_lugares);

        fab = (FloatingActionButton) findViewById(R.id.fabmap);
        fab.setOnClickListener((View.OnClickListener) this);

        try {
            estacionamientoDAO.inicializarListaEstacionamientos(this);
            llenarEstacionamientos();
        } catch (EstacionamientoException e) {
            //TODO manejar la excepcion
        }

        listaEst = (ListView) findViewById(R.id.listLugares);
        adapterEst = new EstacionamientoAdapter(this, Arrays.asList(Estacionamientos));
        listaEst.setAdapter(adapterEst);
        setTitle("Listados de Estacionamientos");
    }

    private void llenarEstacionamientos(){
        //TODO ATENCION!!!! OJO al índice con el que se inicializó el array, totalmente harcodeado!! (ver estacionamientoDAO.listarEstacionamientosHarcodeados())
        Estacionamientos = new Estacionamiento[3];
        ArrayList<Estacionamiento> estacionamientoList = new ArrayList<Estacionamiento>();
        try {
            estacionamientoList = estacionamientoDAO.listarEstacionamientos(this);
            int i=0;
            for(Estacionamiento e : estacionamientoList){
                System.out.println("---------------"+e.getNombreEstacionamiento());
                Estacionamientos[i] = e;
                i++;
            }
        } catch (EstacionamientoException e) {
            //TODO manejar la excepcion
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == fab.getId()) {
            Intent intent = new Intent(this, MapaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("bandera", "FABMAPA");
            startActivity(intent);
        }
    }
}
