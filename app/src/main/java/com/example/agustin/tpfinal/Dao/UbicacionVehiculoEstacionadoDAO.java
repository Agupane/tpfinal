package com.example.agustin.tpfinal.Dao;

import android.support.annotation.Nullable;

import com.example.agustin.tpfinal.Exceptions.UbicacionVehiculoException;
import com.example.agustin.tpfinal.Modelo.UbicacionVehiculoEstacionado;
import com.example.agustin.tpfinal.R;

import java.util.List;

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
  //  private static ProyectoOpenHelper dbHelper = new ProyectoOpenHelper(MyApplication.getAppContext());
  //  private static ProyectoApiRest daoApiRest = new ProyectoApiRest();
   // private static final UsuarioDAO daoUsuario = UsuarioDAO.getInstance();
   // private static ProyectoDAO daoProyecto = ProyectoDAO.getInstance();
   // private SQLiteDatabase db;
   // private List<Usuario> listaUsuarios;
    private UbicacionVehiculoEstacionadoDAO(){ }

    public static UbicacionVehiculoEstacionadoDAO getInstance(){
        MODO_PERSISTENCIA_CONFIGURADA = MODO_PERSISTENCIA_LOCAL;
        return ourInstance;
    }

    public UbicacionVehiculoEstacionado getUbicacionVehiculo(int idUbicacionVehiculo) throws UbicacionVehiculoException {
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

    public void borrarUbicacionVehiculo(int idUbicacionVehiculo) throws UbicacionVehiculoException {
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
    public void actualizarUbicacionVehiculoEstacionado(UbicacionVehiculoEstacionado ubicacionVehiculo) throws UbicacionVehiculoException {
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
      /*
        try{
            ContentValues datosAGuardar = new ContentValues();
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS, t.getHorasEstimadas());
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS, 0);
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA, t.getDescripcion());
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD, t.getPrioridad().getId());
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE, t.getResponsable().getId());
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO, t.getProyecto().getId());
            open(true);
            db.update(ProyectoDBMetadata.TABLA_TAREAS, datosAGuardar, ProyectoDBMetadata.TablaTareasMetadata._ID + "=" + t.getId(), null);
        }
        catch(Exception e){
            throw new TareaException("La tarea no pudo ser actualizada, intente nuevamente");
        }
          */
    }
    /**
     * Actualiza la ubicacion del objeto ubicacionVehiculoEstacionado en la base de datos local
     * @param ubicacionVehiculo
     * @throws UbicacionVehiculoException
     */
    private void actualizarUbicacionVehiculoRemoto(UbicacionVehiculoEstacionado ubicacionVehiculo) throws UbicacionVehiculoException {
    //    daoApiRest.actualizarTarea(t);
    }
    public void nuevaUbicacionVehiculo(UbicacionVehiculoEstacionado ubicacionVehiculo) throws UbicacionVehiculoException {
        switch(MODO_PERSISTENCIA_CONFIGURADA){
            case MODO_PERSISTENCIA_LOCAL:{
                nuevaUbicacionVehiculoLocal(ubicacionVehiculo);
                break;
            }
            case MODO_PERSISTENCIA_REMOTA:{
                nuevaUbicacionVehiculoRemoto(ubicacionVehiculo);
                break;
            }
            case MODO_PERSISTENCIA_MIXTA:{
                nuevaUbicacionVehiculoLocal(ubicacionVehiculo);
                nuevaUbicacionVehiculoRemoto(ubicacionVehiculo);
                break;
            }
        }
    }

    private void nuevaUbicacionVehiculoLocal(UbicacionVehiculoEstacionado ubicacionVehiculo) throws UbicacionVehiculoException {
        /*
        try{
            ContentValues datosAGuardar = new ContentValues();
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS, t.getHorasEstimadas());
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS, 0);
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA, t.getDescripcion());
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD, t.getPrioridad().getId());
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE, t.getResponsable().getId());
            datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO, t.getProyecto().getId());
            open(true);
            db.insert(ProyectoDBMetadata.TABLA_TAREAS, null, datosAGuardar);
        }
        catch(Exception e){
            throw new TareaException("La tarea no pudo ser creada");
        }
        */
    }

    private void nuevaUbicacionVehiculoRemoto(UbicacionVehiculoEstacionado ubicacionVehiculo) throws UbicacionVehiculoException {
       // daoApiRest.guardarTarea(t);
    }

    /**
     * Devuelve una lista con todas las ubicaciones de los vehiculos estacionados
     * @return
     */
    public List listarUbicacionVehiculoEstacionado() throws UbicacionVehiculoException {
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
}
