package com.example.agustin.tpfinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.util.Map;

/**
 * Created by Agustin on 11/19/2016.
 */

public class InfoWindowsAdapter implements GoogleMap.InfoWindowAdapter {
    private final View myContentsView;
    private Context context;
    private LayoutInflater linf;
    private Map<Marker,Reclamo> listaReclamos;
    public InfoWindowsAdapter(Context c){
        this.context=c;

        linf = LayoutInflater.from(context);
        myContentsView = linf.inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.etTituloVerReclamo));
        tvTitle.setText(marker.getTitle());
        ImageView imageView = ((ImageView)myContentsView.findViewById(R.id.ivVerFotoReclamo));
        //TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
        //tvSnippet.setText(marker.getSnippet());

        File fotoMarker = listaReclamos.get(marker).getFoto();
        if(fotoMarker != null){
            Bitmap imageBitmap = BitmapFactory.decodeFile(fotoMarker.getAbsolutePath());
            imageView.setImageBitmap(imageBitmap);
        }

        return myContentsView;
    }

    public void setListaReclamos(Map<Marker,Reclamo> listaReclamos){
        this.listaReclamos=listaReclamos;
    }
}
