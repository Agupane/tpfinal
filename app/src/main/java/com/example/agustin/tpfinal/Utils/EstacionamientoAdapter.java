package com.example.agustin.tpfinal.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.agustin.tpfinal.Modelo.Estacionamiento;
import com.example.agustin.tpfinal.R;
import com.example.agustin.tpfinal.VistasAndControllers.MapaActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nahuel SG on 31/01/2017.
 */

public class EstacionamientoAdapter extends BaseAdapter {
    private List<Estacionamiento> listaEstacionamiento;
    private LayoutInflater inflater;
    private View row;
    private Button reserva, verMapa;
    private TextView nombre, direccion, horarios, tarifa;
    private Context mCont;


    public EstacionamientoAdapter(Context context, List<Estacionamiento> listaEstac)
    {
        super();
        this.listaEstacionamiento = new ArrayList<Estacionamiento>();
        this.listaEstacionamiento.addAll(listaEstac);
        inflater = LayoutInflater.from(context);
        mCont = context;
    }
    @Override
    public int getCount() { return listaEstacionamiento.size() ;    }

    @Override
    public Object getItem(int position) {   return listaEstacionamiento.get(position);    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        row = convertView;
        if(row == null)
        {
            row = inflater.inflate(R.layout.row_lista_estacionamiento,parent,false);

        }
        cargarVariables();
        llenarAdapter(position);
        return(row);
    }
    /**
     * Inicializa la fila de listview correspondiente con los componentes
     */
    private void cargarVariables()
    {
        reserva = (Button) row.findViewById(R.id.botonReservar);
        verMapa = (Button) row.findViewById(R.id.botonVerEnMapa);
        reserva.setFocusable(false);
        //reserva.setClickable(false);
        verMapa.setFocusable(false);
        //verMapa.setClickable(false);

        nombre = (TextView) row.findViewById(R.id.tvNombreEstacionamiento);
        direccion = (TextView) row.findViewById(R.id.tvDireccionEstacionamiento);
        horarios = (TextView) row.findViewById(R.id.tvHorarioEstacionamiento);
        tarifa = (TextView) row.findViewById(R.id.textViewTarifa);

    }
    /**
     * Carga los componentes de la fila position del listview
     * @param position
     */
    private void llenarAdapter(final int position) {
        nombre.setText(((Estacionamiento)this.getItem(position)).getNombreEstacionamiento() );
        direccion.setText((CharSequence) ((Estacionamiento)this.getItem(position)).getDireccionEstacionamiento());
        horarios.setText(((Estacionamiento)this.getItem(position)).getHorarios());
        tarifa.setText(((Estacionamiento)this.getItem(position)).getTarifaEstacionamiento() );

        verMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idView = v.getId();
                if(idView == R.id.botonVerEnMapa){
                    //TODO borrar Sysout
                    System.out.println("(ADAPTER) Tocaste VER MAPA de la pos: " + position);

                    //Lanza un intent, mCont es el contexto que contiene el adapter
                    // Enviar la LatLng del lugar... TODO esto
                    LatLng latLngToMap = listaEstacionamiento.get(position).getPosicionEstacionamiento();
                    //Luego de tomar la latLng del estacionamiento la guardo en un double[]
                    double[] latLngToSend = new double[2];
                    latLngToSend[0] = latLngToMap.latitude;
                    latLngToSend[1] = latLngToMap.longitude;
                    //Hago el intent y envio el dato
                    mCont.startActivity((new Intent(mCont, MapaActivity.class))
                            .putExtra("latLng",latLngToSend)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        });

        reserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idView = v.getId();
                if(idView == R.id.botonReservar){
                    System.out.println("(ADAPTER) Tocaste RESERVAR de la pos: " + position);

                    // TODO Reserva
                    //reservar(position);
                }
            }
        });

    }

    /**
     * Carga un estacionamiento
     *
     */
    public void agregarEstacionamiento(Estacionamiento nuevoEstac,Context context)
    {
        listaEstacionamiento.add(nuevoEstac);
        notifyDataSetChanged();

    }


}