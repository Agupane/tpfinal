package com.example.agustin.tpfinal.Dao;

import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.util.Log;

import com.example.agustin.tpfinal.Exceptions.FileSaverException;
import com.example.agustin.tpfinal.Exceptions.UbicacionVehiculoException;
import com.example.agustin.tpfinal.Modelo.UbicacionVehiculoEstacionado;
import com.example.agustin.tpfinal.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Agustin on 01/25/2017.
 */

public class UbicacionVehiculoEstacionadoDAO {
    private static UbicacionVehiculoEstacionadoDAO ourInstance = new UbicacionVehiculoEstacionadoDAO();
    private static final int MODO_PERSISTENCIA_MIXTA = 2;  // Los datos se almacenan en la api rest y en local
    private static final int MODO_PERSISTENCIA_LOCAL = 1;  // Los datos se almacenan solamente en la bdd local
    private static final int MODO_PERSISTENCIA_REMOTA = 0; // Los datos se almacenan solamente en la nube
    private static int MODO_PERSISTENCIA_CONFIGURADA = MODO_PERSISTENCIA_MIXTA; // Como default es mixta
    private static boolean usarApiRest = false; // default true
    private static final FileSaverHelper fileSaver = FileSaverHelper.getInstance(); // Clase que se encarga del almacenamiento local
    private static final String TAG = "UbicacionVehiculoDAO";
    private static final String UBICACION_VEHICULO_FILENAME = "ubicacionVehiculo.json";
    private Context context;

    private static Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private UbicacionVehiculoEstacionadoDAO(){ }

    public static UbicacionVehiculoEstacionadoDAO getInstance(){
        MODO_PERSISTENCIA_CONFIGURADA = MODO_PERSISTENCIA_LOCAL;
        return ourInstance;
    }

    /**
     * Retorna el objeto ubicacion vehiculo estacionado con el id parametro
     * @param idUbicacionVehiculo
     * @return
     * @throws UbicacionVehiculoException
     */
    public UbicacionVehiculoEstacionado getUbicacionVehiculo(int idUbicacionVehiculo,Context context) throws UbicacionVehiculoException {
        this.context = context;
        UbicacionVehiculoEstacionado nuevaUbicacion = null;
        try {
            /** TODO modificar y poner un switch como el de borrado */
            if (usarApiRest) {
           //     nuevaUbicacion = daoApiRest.getUbicacionVehiculo(idUbicacionVehiculo);
            }
            else {
                /*
                Cursor resultadoUbicacion;

                open(false);
                resultadoTareas = db.rawQuery("SELECT * " + " FROM " + ProyectoDBMetadata.TABLA_TAREAS + " WHERE " + ProyectoDBMetadata.TablaTareasMetadata._ID + " = " + idTarea, null);
                resultadoTareas.moveToFirst();

                String descripcion = resultadoTareas.getString(1);
                Integer horasEstimadas = resultadoTareas.getInt(2);
                Integer minutosTrabajados = resultadoTareas.getInt(3);
                Prioridad prioridad = getPrioridad(resultadoTareas.getInt(4));
                Usuario responsable = daoUsuario.getUsuario(resultadoTareas.getInt(5));
                Proyecto proyecto = daoProyecto.getProyecto(resultadoTareas.getInt(6));
                Boolean finalizada;
                if (resultadoTareas.getInt(7) == 0) {
                    finalizada = false;
                } else {
                    finalizada = true;
                }
                nuevaTarea = new Tarea(idTarea, finalizada, horasEstimadas, minutosTrabajados, proyecto, prioridad, responsable, descripcion);
                resultadoTareas.close();
                */
            }
        }
        catch(Exception e)
        {
            String msg = String.valueOf(R.string.ubicacionVehiculoBuscadoNotFound);
            throw new UbicacionVehiculoException(msg);
        }
        return nuevaUbicacion;
    }

    /**
     * Borra el vehiculo estacionado
     * @param idUbicacionVehiculo
     * @throws UbicacionVehiculoException
     */
    public void borrarUbicacionVehiculo(int idUbicacionVehiculo,Context context) throws UbicacionVehiculoException {
        this.context = context;
        switch(MODO_PERSISTENCIA_CONFIGURADA){
            case MODO_PERSISTENCIA_LOCAL:{
                borrarUbicacionVehiculoLocal(idUbicacionVehiculo);
                break;
            }
            case MODO_PERSISTENCIA_REMOTA:{
                borrarUbicacionVehiculoRemoto(idUbicacionVehiculo);
                break;
            }
            case MODO_PERSISTENCIA_MIXTA:{
                borrarUbicacionVehiculoLocal(idUbicacionVehiculo);
                borrarUbicacionVehiculoRemoto(idUbicacionVehiculo);
                break;
            }
        }
    }
    private void borrarUbicacionVehiculoLocal(int idUbicacionVehiculo) throws UbicacionVehiculoException {
        try {
            /*
            String[] args = {String.valueOf(idTarea)};
            open(true);
            db.delete(ProyectoDBMetadata.TABLA_TAREAS, "_id=?", args);
            */
        }
        catch(Exception e){
            String msg = String.valueOf(R.string.ubicacionVehiculoEliminadoNotFound);
            throw new UbicacionVehiculoException("La tarea no pudo ser eliminada");
        }
    }

    private void borrarUbicacionVehiculoRemoto(int idUbicacionVehiculo) throws UbicacionVehiculoException {
       // daoApiRest.borrarUbicacionVehiculo(idUbicacionVehiculo);
    }

    /**
     * Actualiza la informacion del objeto ubicacionVehiculoEstacionado pasado en la base de datos
     * @param ubicacionVehiculo
     * @throws UbicacionVehiculoException
     */
    public void actualizarUbicacionVehiculoEstacionado(UbicacionVehiculoEstacionado ubicacionVehiculo,Context context) throws UbicacionVehiculoException {
        this.context = context;
        switch(MODO_PERSISTENCIA_CONFIGURADA){
            case MODO_PERSISTENCIA_LOCAL:{
                actualizarUbicacionVehiculoLocal(ubicacionVehiculo);
                break;
            }
            case MODO_PERSISTENCIA_REMOTA:{
                actualizarUbicacionVehiculoRemoto(ubicacionVehiculo);
                break;
            }
            case MODO_PERSISTENCIA_MIXTA:{
                actualizarUbicacionVehiculoLocal(ubicacionVehiculo);
                actualizarUbicacionVehiculoRemoto(ubicacionVehiculo);
                break;
            }
        }
    }
    /**
     * Actualiza la ubicacion del objeto ubicacionVehiculoEstacionado en la base de datos local
     * @param ubicacionVehiculo
     * @throws UbicacionVehiculoException
     */
    private void actualizarUbicacionVehiculoLocal(UbicacionVehiculoEstacionado ubicacionVehiculo) throws UbicacionVehiculoException{
       // Gson gson = new Gson();

        String jsonStringVehiculo = gson.toJson(ubicacionVehiculo);
        JSONObject vehiculo = new JSONObject();
        JSONObject baseDeDatos = new JSONObject();
        String jsonStringBaseDeDatos;
        String msg;
        try {
            vehiculo = new JSONObject(jsonStringVehiculo);
            jsonStringBaseDeDatos = fileSaver.getArchivo(UBICACION_VEHICULO_FILENAME,context);
            baseDeDatos = new JSONObject(jsonStringBaseDeDatos);
            /** TODO Esto hay que cambiarlo a futuro en el caso de que hagamos herencia con los tipos de estacionamientos */
            JSONArray estacionamientos = baseDeDatos.getJSONArray("estacionamientosCalle");
            actualizarArrayEstacionamientos(estacionamientos,ubicacionVehiculo);


            jsonStringBaseDeDatos = baseDeDatos.toString();
            System.out.println("item actualizado: "+jsonStringBaseDeDatos);
            fileSaver.usarEscrituraInterna(true);
            fileSaver.guardarArchivo(jsonStringBaseDeDatos,UBICACION_VEHICULO_FILENAME,context);
        }
        catch (FileSaverException e) {
            Log.v(TAG,e.getMessage());
        }
        catch (JSONException e){
            msg = context.getResources().getString(R.string.fileSaverErrorEscrituraLocal);
            Log.v(TAG,msg);
            throw new UbicacionVehiculoException(msg);
        }
    }
    /**
     * Actualiza la ubicacion del objeto ubicacionVehiculoEstacionado en la base de datos local
     * @param ubicacionVehiculo
     * @throws UbicacionVehiculoException
     */
    private void actualizarUbicacionVehiculoRemoto(UbicacionVehiculoEstacionado ubicacionVehiculo) throws UbicacionVehiculoException {
    //    daoApiRest.actualizarTarea(t);
    }

    /**
     * Parsea el JSON ARRAY para buscar el objeto correspondiente y actualizarlo
     * @param estacionamientos
     * @param vehiculo
     */
    private void actualizarArrayEstacionamientos(JSONArray estacionamientos, UbicacionVehiculoEstacionado vehiculo) throws JSONException {
        JSONObject iterador = new JSONObject();
        JSONObject objetoDireccion;
        for(int i = 0; i<estacionamientos.length();i++){
            iterador = (JSONObject) estacionamientos.get(i);
            if(vehiculo.getHoraIngreso().compareTo((Long) iterador.get("horaIngreso")) == 0 ) // los "id" son iguales
            {
                if(vehiculo.getIdUsuario() != null) {
                    iterador.put("idUsuario", vehiculo.getIdUsuario());
                }
                if(vehiculo.getHoraEgreso() != null ){
                    iterador.put("horaEgreso",vehiculo.getHoraEgreso());
                }
                if(vehiculo.getHoraIngreso() != null){
                    iterador.put("horaIngreso",vehiculo.getHoraIngreso());
                }
                if(vehiculo.getDireccion() != null){
                    Address direccion = vehiculo.getDireccion();
                    objetoDireccion = new JSONObject(gson.toJson(direccion));
                    iterador.put("direccion",objetoDireccion);
                }
                return;
            }
        }
    }

    /**
     * Persiste una ubicacion vehiculo nueva
     * @param ubicacionVehiculo
     * @throws UbicacionVehiculoException
     */
    public void guardarOActualizarUbicacionVehiculo(UbicacionVehiculoEstacionado ubicacionVehiculo, Context context) throws UbicacionVehiculoException {
        this.context = context;
        switch(MODO_PERSISTENCIA_CONFIGURADA){
            case MODO_PERSISTENCIA_LOCAL:{
                guardarUbicacionVehiculoLocal(ubicacionVehiculo);
                break;
            }
            case MODO_PERSISTENCIA_REMOTA:{
                guardarUbicacionVehiculoRemoto(ubicacionVehiculo);
                break;
            }
            case MODO_PERSISTENCIA_MIXTA:{
                guardarUbicacionVehiculoLocal(ubicacionVehiculo);
                guardarUbicacionVehiculoRemoto(ubicacionVehiculo);
                break;
            }
        }
    }

    private void guardarUbicacionVehiculoLocal(UbicacionVehiculoEstacionado ubicacionVehiculo) throws UbicacionVehiculoException {
        //Gson gson = new Gson();
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        String jsonStringVehiculo = gson.toJson(ubicacionVehiculo);
        JSONObject vehiculo = new JSONObject();
        JSONObject baseDeDatos = new JSONObject();
        String jsonStringBaseDeDatos;
        String msg;
        try {
            vehiculo = new JSONObject(jsonStringVehiculo);
            jsonStringBaseDeDatos = fileSaver.getArchivo(UBICACION_VEHICULO_FILENAME,context);
            baseDeDatos = new JSONObject(jsonStringBaseDeDatos);
            /** TODO Esto hay que cambiarlo a futuro en el caso de que hagamos herencia con los tipos de estacionamientos */
            baseDeDatos.getJSONArray("estacionamientosCalle").put(vehiculo);
            jsonStringBaseDeDatos = baseDeDatos.toString();
            fileSaver.usarEscrituraInterna(true);
            System.out.println("item nuevo"+jsonStringBaseDeDatos);
            fileSaver.guardarArchivo(jsonStringBaseDeDatos,UBICACION_VEHICULO_FILENAME,context);
        }
        catch (FileSaverException e) {
            Log.v(TAG,e.getMessage());
        }
        catch (JSONException e){
            msg = context.getResources().getString(R.string.fileSaverErrorEscrituraLocal);
            Log.v(TAG,msg);
            throw new UbicacionVehiculoException(msg);
        }
    }

    private void guardarUbicacionVehiculoRemoto(UbicacionVehiculoEstacionado ubicacionVehiculo) throws UbicacionVehiculoException {
       // daoApiRest.guardarTarea(t);
    }

    /**
     * Devuelve una lista con todas las ubicaciones de los vehiculos estacionados
     * del usuario id
     * @param idUsuario id del usuario al que se desea listar sus ubicaciones
     * @return
     */
    public List listarUbicacionVehiculoEstacionado(Integer idUsuario,Context context) throws UbicacionVehiculoException {
        this.context = context;
      //  Cursor cursorTareas=null;
        try{
            if(usarApiRest){
             //   cursorTareas=daoApiRest.listarUbicacionVehiculoEstacionado();
              //  cursorTareas.moveToFirst();
            }
            else {
                /*
                cursorTareas = db.rawQuery("SELECT " + ProyectoDBMetadata.TablaProyectoMetadata._ID + " FROM " + ProyectoDBMetadata.TABLA_PROYECTO, null);
                //  Integer idPry= 0;
                Integer idPry = idProyecto;
                /*
                if(cursorPry.moveToFirst()){
                    idPry=cursorPry.getInt(0);
                }
                */
                /*
                Cursor cursor = null;
                Log.d("Listar Tareas", "PROYECTO ID: " + idPry.toString() + " - " + _SQL_TAREAS_X_PROYECTO);
                cursor = db.rawQuery(_SQL_TAREAS_X_PROYECTO, new String[]{idPry.toString()});
                return cursor;
                */
            }
        }
        catch(Exception e){
            String msg = String.valueOf(R.string.listaUbicacionVehiculosNotFound);
            throw new UbicacionVehiculoException(msg);
        }
     //   return cursorTareas;
        return null;
    }

    /**
     * Devuelve la ultima ubicacion en donde el usuario idUsuario estaciono el vehiculo
     * @param idUsuario
     * @param context
     * @return
     */
    /** TODO - TERMINAR */
    public UbicacionVehiculoEstacionado getUltimaUbicacionVehiculo(Integer idUsuario,Context context){
        this.context = context;
        String msg,jsonString;
        UbicacionVehiculoEstacionado ubVehiculo = null;
        Gson gson;
        try {
            jsonString = fileSaver.getArchivo(UBICACION_VEHICULO_FILENAME,context);
            gson = new Gson();
            //ubVehiculo = gson.fromJson(jsonString,UbicacionVehiculoEstacionado.class);

            System.out.println("pepino cosmico");
            System.out.println(jsonString);
            try {
                JSONObject jOb = new JSONObject(jsonString);
//                System.out.println(jOb.getJSONObject("idUsuario:0"));
               // System.out.println("String: "+jsonString);
               // System.out.println(jOb);
    //            System.out.println(jOb.getJSONObject("idUsuario:0"));
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
        catch (FileSaverException e) {
            msg = e.getMessage();
            Log.v(TAG,msg);
        }
        return ubVehiculo;
    }
}
