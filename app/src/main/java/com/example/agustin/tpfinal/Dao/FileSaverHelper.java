package com.example.agustin.tpfinal.Dao;

/**
 * Created by Agustin on 01/25/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.agustin.tpfinal.Exceptions.FileSaverException;
import com.example.agustin.tpfinal.R;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase que permite guardar informacion en almacenamiento interno o externo aislando el comportamiento
 */
public class FileSaverHelper {
    private static FileSaverHelper ourInstance = new FileSaverHelper();
    private static final int MEMORIA_INTERNA = 0;
    private static final int MEMORIA_EXTERNA = 1;
    private static final String TAG = "FileSaverHelper";
    /** Indica el tipo de guardado por default */
    private static int TIPO_ESCRITURA = MEMORIA_INTERNA;
    private Context contexto;

    public static FileSaverHelper getInstance() {
        return ourInstance;
    }

    private FileSaverHelper() {
    }

    /**
     * Permite modificar el modo de almacenamiento
     * @param usarDiscoInterno
     */
    public void usarEscrituraInterna(Boolean usarDiscoInterno){
        if(usarDiscoInterno){
            TIPO_ESCRITURA = MEMORIA_INTERNA;
        }
        else {
            TIPO_ESCRITURA = MEMORIA_EXTERNA;
        }
    }

    /**
     * Recibe un objeto JSON y lo almacena en disco interno o externo segun lo configurado default
     * @param objeto objeto JSON a almacenar
     * @param nombre nombre que se le quiere asignar al archivo
     * @param context contexto desde donde se llama a guardar el archivo (necesario para guardar)
     * @throws FileSaverException
     */
    public void guardarArchivo(JSONObject objeto, String nombre,Context context) throws FileSaverException{
        switch(TIPO_ESCRITURA){
            case MEMORIA_EXTERNA:{
                contexto = context;
                guardarArchivoMemoriaExterna(objeto,nombre);
                break;
            }
            case MEMORIA_INTERNA:{
                contexto=context;
                guardarArchivoMemoriaInterna(objeto,nombre);
                break;
            }
            default:
                String msg = String.valueOf(R.string.fileSaverErrorEscrituraLocal);
                throw new FileSaverException(msg);
        }
    }

    /**TODO implementar metodo */
    private void guardarArchivoMemoriaExterna(JSONObject objeto, String nombre) throws FileSaverException{

    }

    /**
     * Recibe un objeto JSON y lo almacena en memoria interna
     * @param objeto
     * @throws FileSaverException
     */
    private void guardarArchivoMemoriaInterna(JSONObject objeto,String nombre) throws FileSaverException{
        String msg = String.valueOf(R.string.fileSaverErrorEscrituraLocal);
        String fileName=nombre;
        FileOutputStream mOutput;
        if(objeto!=null) {
            try {
                mOutput = contexto.openFileOutput(fileName+".json", Activity.MODE_PRIVATE);
                mOutput.write(objeto.toString().getBytes());
                mOutput.flush();
                mOutput.close();
                msg = String.valueOf(R.string.fileSaverAlmacenExitoso);
                Log.v(TAG,msg);
            }
            catch(FileNotFoundException e){
                throw new FileSaverException(msg);
            }
            catch(IOException e){
                throw new FileSaverException(msg);
            }
        }
        else{
            throw new FileSaverException(msg);
        }
    }
}
