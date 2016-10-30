package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
    public void crearProyecto(Proyecto p){
        JSONObject jsonNuevoProyecto = new JSONObject();
        RestClient cliRest = new RestClient();
        try {
            jsonNuevoProyecto.put("nombre",p.getNombre());
            cliRest.crear(jsonNuevoProyecto,"proyectos");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Borra el proyecto con el id parametro existente en la nube
     * @param id
     */
    public void borrarProyecto(Integer id){
        RestClient cliRest = new RestClient();
        cliRest.borrar(id,"proyectos");
    }

    /**
     * Actualiza los datos del proyecto parametro en la nube
     * @param p
     */
    public void actualizarProyecto(Proyecto p){
        JSONObject jsonNuevoProyecto = new JSONObject();
        RestClient cliRest = new RestClient();
        try {
            jsonNuevoProyecto.put("id",p.getId());
            jsonNuevoProyecto.put("nombre",p.getNombre());
            cliRest.actualizar(jsonNuevoProyecto,"proyectos/"+p.getId());
        }
        catch (JSONException e) {
            e.printStackTrace();
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
    public Cursor getCursorProyectos(){
        MatrixCursor mc = new MatrixCursor(new String[] {ProyectoDBMetadata.TablaProyectoMetadata._ID,ProyectoDBMetadata.TablaProyectoMetadata.TITULO});
        int id;
        String nombre;
        JSONArray listaProyectos = buscarProyectos();
        /** Transforma el json array en un cursor reconocible por el cursor adapter */
        for (int i = 0; i < listaProyectos.length(); i++) {
            JSONObject proyectoAux = null;
            try {
                proyectoAux = listaProyectos.getJSONObject(i);
                // extract the properties from the JSONObject and use it with the addRow() method below
                id = proyectoAux.getInt("id");
                nombre = proyectoAux.getString("nombre");
                mc.addRow(new Object[]{id, nombre});
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return mc;
    }

    /**
     * Devuelve todos los proyectos existentes en la nube
     * @return
     */
    private JSONArray buscarProyectos(){
        RestClient cliRest = new RestClient();
        JSONArray proyectos = cliRest.getByAll("proyectos");
        return proyectos;
    }

    /**
     * Devuelve el proyecto con el id pasado por parametro en la nube
     * @param id
     * @return
     */
    public Proyecto buscarProyecto(Integer id){
        RestClient cliRest = new RestClient();
        JSONObject t = cliRest.getById(1,"proyectos");
        // transformar el objeto JSON a proyecto y retornarlo
        return null;
    }

    /**
     * Guarda el usuario guardado por parametro en la nube
     * @param nuevoUsuario
     */
    public void guardarUsuario (Usuario nuevoUsuario){
        JSONObject jsonNuevoUsuario = new JSONObject();
        RestClient cliRest = new RestClient();
        try {
            jsonNuevoUsuario.put("id",nuevoUsuario.getId());
            jsonNuevoUsuario.put("nombre",nuevoUsuario.getNombre());
            jsonNuevoUsuario.put("correoElectronico",nuevoUsuario.getCorreoElectronico());
            cliRest.crear(jsonNuevoUsuario,"usuarios");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

}