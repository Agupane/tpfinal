package com.example.agustin.tpfinal.Ejemplos;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.Exception.ProyectoException;
import dam.isi.frsf.utn.edu.ar.lab05.Exception.RestException;
import dam.isi.frsf.utn.edu.ar.lab05.Exception.TareaException;
import dam.isi.frsf.utn.edu.ar.lab05.Exception.UsuarioException;
import dam.isi.frsf.utn.edu.ar.lab05.RestClient;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by Agustin on 10/20/2016.
 */

public class ProyectoApiRest {
    private final static UsuarioDAO usuarioDao= UsuarioDAO.getInstance();
    private final static TareaDAO tareaDao = TareaDAO.getInstance();
    private static ProyectoApiRest ourInstance = new ProyectoApiRest();

    public ProyectoApiRest(){

    }
    public static ProyectoApiRest getInstance(){
        return ourInstance;
    }
    /**
     * Crea el proyecto pasado por parametro en la nube
     * @param p
     */
    public void crearProyecto(Proyecto p) throws ProyectoException {
        JSONObject jsonNuevoProyecto = new JSONObject();
        RestClient cliRest = new RestClient();
        try {
            jsonNuevoProyecto.put("nombre",p.getNombre());
            cliRest.crear(jsonNuevoProyecto,"proyectos");
        }
        catch (Exception e) {
            throw new ProyectoException("Los proyectos no pudieron ser encontrados");
        }
    }

    /**
     * Borra el proyecto con el id parametro existente en la nube
     * @param id
     */
    public void borrarProyecto(Integer id) throws ProyectoException {
        RestClient cliRest = new RestClient();
        try {
            cliRest.borrar(id, "proyectos");
        }
        catch(Exception e){
            throw new ProyectoException("Los proyectos no pudieron ser encontrados");
        }
    }

    /**
     * Actualiza los datos del proyecto parametro en la nube
     * @param p
     */
    public void actualizarProyecto(Proyecto p) throws ProyectoException {
        JSONObject jsonNuevoProyecto = new JSONObject();
        RestClient cliRest = new RestClient();
        try {
            jsonNuevoProyecto.put("id",p.getId());
            jsonNuevoProyecto.put("nombre",p.getNombre());
            cliRest.actualizar(jsonNuevoProyecto,"proyectos/"+p.getId());
        }
        catch (Exception e) {
            throw new ProyectoException("El proyecto no pudo ser actualizado");
        }
    }

    /**
     * Devuelve todos los proyectos existente en la nube en formato de lista
     * @return
     */
    public List<Proyecto> listarProyectos(){
        return null;
    }

    /**
     * Devuelve todos los proyectos en formato de cursor, para poder utilizarlo en una listview
     * @return
     */
    public Cursor getCursorProyectos() throws ProyectoException {
        MatrixCursor mc = new MatrixCursor(new String[] {ProyectoDBMetadata.TablaProyectoMetadata._ID,ProyectoDBMetadata.TablaProyectoMetadata.TITULO});
        int id;
        String nombre;
        try {
            JSONArray listaProyectos = buscarProyectos();
            /** Transforma el json array en un cursor reconocible por el cursor adapter */
            for (int i = 0; i < listaProyectos.length(); i++) {
                JSONObject proyectoAux = null;
                proyectoAux = listaProyectos.getJSONObject(i);
                // extract the properties from the JSONObject and use it with the addRow() method below
                id = proyectoAux.getInt("id");
                nombre = proyectoAux.getString("nombre");
                mc.addRow(new Object[]{id, nombre});
            }
        }
        catch(Exception e){
            throw new ProyectoException("Los proyectos no pudieron ser encontrados");
        }
        return mc;
    }

    /**
     * Devuelve todos los proyectos existentes en la nube
     * @return
     */
    private JSONArray buscarProyectos() throws ProyectoException {
        RestClient cliRest = new RestClient();
        JSONArray proyectos =null;
        try {
            proyectos = cliRest.getByAll("proyectos");
        }
        catch(Exception e){
            throw new ProyectoException("Los proyectos no pudieron ser encontrados");
        }
        return proyectos;
    }

    /**
     * Devuelve el proyecto con el id pasado por parametro en la nube
     * @param id
     * @return
     */
    public Proyecto buscarProyecto(Integer id) throws ProyectoException {
        RestClient cliRest = new RestClient();
        Proyecto proyecto =null;
        try {
            JSONObject t = cliRest.getById(id,"proyectos");
            proyecto = new Proyecto(t.getInt("id"),t.getString("nombre"));
        }
        catch (JSONException e) {
            throw new ProyectoException("El proyecto no pudo ser encontrado");
        }
        catch(RestException e){
            throw new ProyectoException("El proyecto no pudo ser encontrado");
        }
        return proyecto;
    }

    /**
     * Guarda el usuario guardado por parametro en la nube
     * Si el usuario ya existia no hace nada
     * @param nuevoUsuario
     */
    public void guardarUsuario (Usuario nuevoUsuario) throws UsuarioException {
        JSONObject jsonNuevoUsuario = new JSONObject();
        RestClient cliRest = new RestClient();
        try {
            jsonNuevoUsuario.put("id",nuevoUsuario.getId());
            jsonNuevoUsuario.put("nombre",nuevoUsuario.getNombre());
            jsonNuevoUsuario.put("correoElectronico",nuevoUsuario.getCorreoElectronico());
            cliRest.crear(jsonNuevoUsuario,"usuarios");
        }
        catch (JSONException e) {
            throw new UsuarioException("El usuario no pudo ser guardado");
        }
        catch(RestException e){
            throw new UsuarioException("El usuario no pudo ser guardado");
        }
    }

    public void guardarTarea(Tarea nuevaTarea) throws TareaException{
        JSONObject jsonNuevaTarea = new JSONObject();
        RestClient cliRest = new RestClient();
        try {
            jsonNuevaTarea.put("id",nuevaTarea.getId());
            jsonNuevaTarea.put("descripcion",nuevaTarea.getDescripcion());
            jsonNuevaTarea.put("horasEstimadas",nuevaTarea.getHorasEstimadas());
            jsonNuevaTarea.put("minutosTrabajados",nuevaTarea.getMinutosTrabajados());
            jsonNuevaTarea.put("finalizada",nuevaTarea.getFinalizada());
            jsonNuevaTarea.put("proyectoId",nuevaTarea.getProyecto().getId());
            jsonNuevaTarea.put("prioridadId",nuevaTarea.getPrioridad().getId());
            jsonNuevaTarea.put("usuarioId",nuevaTarea.getResponsable().getId());
            cliRest.crear(jsonNuevaTarea,"tareas");
        }
        catch (JSONException e) {
            throw new TareaException("La tarea no pudo ser guardada");
        }
        catch(RestException e){
            throw new TareaException("La tarea no pudo ser guardada");
        }
    }

    public Cursor getCursorTareas(Integer idProyecto) throws TareaException {
        MatrixCursor mc = new MatrixCursor(new String[] {ProyectoDBMetadata.TablaTareasMetadata._ID,ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,
        ProyectoDBMetadata.TablaTareasMetadata.TAREA,ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS,ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS});
        int id,horasEstimadas,minutosTrabajados,prioridadId,usuarioId,intFinalizada;
        String descripcion;
        boolean finalizada;
        Usuario usuario;
        Prioridad prioridad;
        try {
            JSONArray listaTareas = buscarTareas();
            /** Transforma el json array en un cursor reconocible por el cursor adapter */
            for (int i = 0; i < listaTareas.length(); i++) {
                JSONObject tareaAux = null;
                tareaAux = listaTareas.getJSONObject(i);

                /** TODO CAMBIAR ESTO PARA HACER EL QUERY DIRECTAMENTE, NOSE COMO HACERLO ASI QUE PUSE ESTE FILTRO
                 * AGREGA SOLO LAS TAREAS PERTENECIENTES AL PROYECTO
                 */
                if(tareaAux.getInt("proyectoId")==idProyecto) {

                    // extract the properties from the JSONObject and use it with the addRow() method below
                    id = tareaAux.getInt("id");
                    horasEstimadas = tareaAux.getInt("horasEstimadas");
                    minutosTrabajados = tareaAux.getInt("minutosTrabajados");

                    prioridadId = tareaAux.getInt("prioridadId");
                    prioridad = tareaDao.getPrioridad(prioridadId);
                    usuarioId = tareaAux.getInt("usuarioId");
                    usuario = usuarioDao.getUsuario(usuarioId);
                    descripcion = tareaAux.getString("descripcion");
                    finalizada = new Integer(1).equals(tareaAux.get("finalizada"));
                    if (finalizada) {
                        intFinalizada = 1;
                    } else {
                        intFinalizada = 0;
                    }
                    mc.addRow(new Object[]{id, horasEstimadas, minutosTrabajados, descripcion, intFinalizada, usuario.getNombre(), prioridad.getPrioridad()});
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            throw new TareaException("No se encontraron tareas asociadas al proyecto");
        }
        return mc;
    }

    /**
     * Devuelve el proyecto con el id pasado por parametro en la nube
     * @param id
     * @return
     */
    public Tarea getTarea(Integer id) throws TareaException {
        RestClient cliRest = new RestClient();
        Tarea tarea =null;
        int horasEstimadas,minutosTrabajados,prioridadId,usuarioId;
        String descripcion;
        boolean finalizada;
        Usuario usuario;
        Prioridad prioridad;
        Proyecto proyecto;
        try {
            /* Obtengo tarea en formato json*/
            JSONObject tareaAux = cliRest.getById(id,"tareas");

            /* Obtengo parametros y los cargo en la tarea */
            horasEstimadas = tareaAux.getInt("horasEstimadas");
            minutosTrabajados = tareaAux.getInt("minutosTrabajados");
            prioridadId = tareaAux.getInt("prioridadId");
            prioridad = tareaDao.getPrioridad(prioridadId);
            usuarioId = tareaAux.getInt("usuarioId");
            usuario = usuarioDao.getUsuario(usuarioId);
            descripcion = tareaAux.getString("descripcion");
            proyecto = buscarProyecto(tareaAux.getInt("proyectoId"));
            finalizada = new Integer(1).equals(tareaAux.get("finalizada"));

            tarea = new Tarea(id,finalizada,horasEstimadas,minutosTrabajados,proyecto,prioridad,usuario,descripcion);
        }
        catch(UsuarioException e){
            throw new TareaException("La tarea no pudo ser encontrada");
        }
        catch (JSONException e) {
            throw new TareaException("La tarea no pudo ser encontrada");
        }
        catch(RestException e){
            throw new TareaException("La tarea no pudo ser encontrada");
        } catch (ProyectoException e) {
            throw new TareaException("La tarea no pudo ser encontrada");
        }
        return tarea;
    }

    /**
     * Devuelve todas las tareas existentes en la nube
     * @return
     */
    private JSONArray buscarTareas() throws TareaException {
        RestClient cliRest = new RestClient();
        JSONArray proyectos =null;
        try {
            proyectos = cliRest.getByAll("tareas");
        }
        catch(Exception e){
            throw new TareaException("Las tareas no pudieron ser encontrados");
        }
        return proyectos;
    }

    /**
     * Borra la tarea con el id parametro existente en la nube
     * @param id
     */
    public void borrarTarea(Integer id) throws TareaException {
        RestClient cliRest = new RestClient();
        try {
            cliRest.borrar(id, "tareas");
        }
        catch(Exception e){
            throw new TareaException("La tarea no pudo ser eliminada");
        }
    }

    /**
     * Actualiza los datos de la tarea parametro en la nube
     * @param tarea
     */
    public void actualizarTarea(Tarea tarea) throws TareaException {
        JSONObject jsonNuevaTarea = new JSONObject();
        RestClient cliRest = new RestClient();
        try {
            jsonNuevaTarea.put("id",tarea.getId());
            jsonNuevaTarea.put("descripcion",tarea.getDescripcion());
            jsonNuevaTarea.put("horasEstimadas",tarea.getHorasEstimadas());
            jsonNuevaTarea.put("minutosTrabajados",tarea.getMinutosTrabajados());
            jsonNuevaTarea.put("finalizada",tarea.getFinalizada());
            jsonNuevaTarea.put("proyectoId",tarea.getProyecto().getId());
            jsonNuevaTarea.put("prioridadId",tarea.getPrioridad().getId());
            jsonNuevaTarea.put("usuarioId",tarea.getResponsable().getId());

            cliRest.actualizar(jsonNuevaTarea,"tareas/"+tarea.getId());
        }
        catch (Exception e) {
            throw new TareaException("La tarea no pudo ser actualizada");
        }
    }

    public void actualizarMinutosTrabajados(Integer idTarea,Integer minutosTrabajados) throws TareaException {
        Tarea tareaParaActualizar = getTarea(idTarea);
        tareaParaActualizar.setMinutosTrabajados(minutosTrabajados);
        actualizarTarea(tareaParaActualizar);
    }

    public void finalizarTarea(Integer idTarea) throws TareaException{
        JSONObject jsonNuevaTarea = new JSONObject();
        RestClient cliRest = new RestClient();
        Tarea tareaAEditar = getTarea(idTarea);
        try {
            jsonNuevaTarea.put("finalizada",1);
            /** TODO - CAMBIAR ESTO, CONSULTAR COMO HACER UN UPDATE SIN TENER QUE PEDIR TODO EL OBJETO */

            jsonNuevaTarea.put("descripcion",tareaAEditar.getDescripcion());
            jsonNuevaTarea.put("horasEstimadas",tareaAEditar.getHorasEstimadas());
            jsonNuevaTarea.put("minutosTrabajados",tareaAEditar.getMinutosTrabajados());
            jsonNuevaTarea.put("proyectoId",tareaAEditar.getProyecto().getId());
            jsonNuevaTarea.put("prioridadId",tareaAEditar.getPrioridad().getId());
            jsonNuevaTarea.put("usuarioId",tareaAEditar.getResponsable().getId());

            cliRest.actualizar(jsonNuevaTarea,"tareas/"+idTarea);
        }
        catch (Exception e) {
            throw new TareaException("La tarea no pudo ser finalizada");
        }
    }
}