package com.example.agustin.tpfinal.VistasAndControllers;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private Location ubicacionActual;
    /** Dao que almacena ubicacion de Estacionamientos */
    private static final EstacionamientoDAO estacionamientoDAO = EstacionamientoDAO.getInstance();
    /** Tag usado por el LOG    */
    private static final String TAG = "ServicioUbicacion";
    /** Boton que permite switchear entre la lista de lugares y el mapa */
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_lugares);

        fab = (FloatingActionButton) findViewById(R.id.fabmap);
        fab.setOnClickListener((View.OnClickListener) this);

        //Aca hago el primer try para intentar leer el archivo que tiene la lista de estacionamientos
        //Si no existe, lo creo, lo lleno (harcodeado..!) y lo leo
        try {
            Estacionamientos = estacionamientoDAO.llenarEstacionamientos(this);
        } catch (EstacionamientoException e) {
            //TODO manejar la excepcion
            String msgLog = "No se pudo leer el archivo";
            Log.v(TAG,msgLog);
            try {
                estacionamientoDAO.inicializarListaEstacionamientos(this);
                Estacionamientos = estacionamientoDAO.llenarEstacionamientos(this);
            } catch (EstacionamientoException e1) {
                msgLog = "Hubo un error al crear el archivo con la lista de Estacionamientos.";
                Log.v(TAG,msgLog);
            }
        }

        ubicacionActual = getIntent().getParcelableExtra("ubicacionActual");

        listaEst = (ListView) findViewById(R.id.listLugares);
        adapterEst = new EstacionamientoAdapter(this, Arrays.asList(Estacionamientos), ubicacionActual);
        listaEst.setAdapter(adapterEst);
        setTitle("Listado de Estacionamientos");
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
