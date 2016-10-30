package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.Exception.ProyectoException;
import dam.isi.frsf.utn.edu.ar.lab05.Exception.UsuarioException;
import dam.isi.frsf.utn.edu.ar.lab05.RestClient;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by Agustin on 10/20/2016.
 */

public class ProyectoApiRest {


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
     * TODO IMPLEMENTAR
     * Devuelve el proyecto con el id pasado por parametro en la nube
     * @param id
     * @return
     */
    public Proyecto buscarProyecto(Integer id) throws ProyectoException {
        RestClient cliRest = new RestClient();
        JSONObject t = cliRest.getById(1,"proyectos");
        Proyecto proyecto =null;
        try {
            proyecto = new Proyecto(t.getInt("id"),t.getString("nombre"));
        }
        catch (JSONException e) {
            throw new ProyectoException("El proyecto no pudo ser encontrado");
        }
        return proyecto;
    }

    /**
     * Guarda el usuario guardado por parametro en la nube
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
    }

}