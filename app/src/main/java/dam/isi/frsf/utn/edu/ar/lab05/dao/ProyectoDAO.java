package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.Exception.ProyectoException;
import dam.isi.frsf.utn.edu.ar.lab05.ProyectoApiRest;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoDAO {
    private static final int MODO_PERSISTENCIA_MIXTA = 2;  // Los datos se almacenan en la api rest y en local
    private static final int MODO_PERSISTENCIA_LOCAL = 1;  // Los datos se almacenan solamente en la bdd local
    private static final int MODO_PERSISTENCIA_REMOTA = 0; // Los datos se almacenan solamente en la nube
    private ProyectoOpenHelper dbHelper;
    private SQLiteDatabase db;
    private List<Usuario> listaUsuarios;
    private ProyectoApiRest daoApiRest;
    private static boolean usarApiRest;

    public ProyectoDAO(Context c){
        this.dbHelper = new ProyectoOpenHelper(c);
        this.daoApiRest = new ProyectoApiRest();
        usarApiRest=true;
    }

    public void open(){
        this.open(false);
    }

    public void open(Boolean toWrite){
        if(toWrite) {
            db = dbHelper.getWritableDatabase();
        }
        else{
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close(){
        db = dbHelper.getReadableDatabase();
    }

    /**
     * Retorna el proyecto con id idProyecto
     * @param idProyecto
     * @return
     */
    public Proyecto getProyecto(int idProyecto) throws ProyectoException
    {
        Proyecto nuevoProyecto = null;
        try {
            if (usarApiRest) {
                nuevoProyecto = daoApiRest.buscarProyecto(idProyecto);
            }
            else {
                open(false);
                Cursor result = db.rawQuery("SELECT " + ProyectoDBMetadata.TablaProyectoMetadata.TITULO + " FROM " + ProyectoDBMetadata.TABLA_PROYECTO + " WHERE " + ProyectoDBMetadata.TablaProyectoMetadata._ID + " = " + idProyecto, null);
                result.moveToFirst();
                nuevoProyecto = new Proyecto();
                nuevoProyecto.setId(idProyecto);
                nuevoProyecto.setNombre(result.getString(0));
                result.close();
            }
        }
        catch (Exception e) {
            throw new ProyectoException("El proyecto no pudo ser encontrado");
        }
        return nuevoProyecto;
    }

    /**
     * Borra proyecto por id
     * @param idProyecto
     */
    public void borrarProyecto(int idProyecto) throws ProyectoException {
        try {
            if (usarApiRest) {
                daoApiRest.borrarProyecto(idProyecto);
            }
            else {
                String[] args = {String.valueOf(idProyecto)};
                open(true);
                db.delete(ProyectoDBMetadata.TABLA_PROYECTO, "_id=?", args);
                daoApiRest.borrarProyecto(idProyecto);
            }
        }
        catch(Exception e){
            throw new ProyectoException("El proyecto no pudo ser eliminado");
        }
    }

    public void nuevoProyecto(Proyecto nuevoProyecto) throws ProyectoException {
        try {
            if (usarApiRest) {
                daoApiRest.crearProyecto(nuevoProyecto);
            }
            else {
                ContentValues datosAGuardar = new ContentValues();
                datosAGuardar.put(ProyectoDBMetadata.TablaProyectoMetadata.TITULO, nuevoProyecto.getNombre());
                open(true);
                db.insert(ProyectoDBMetadata.TABLA_PROYECTO, null, datosAGuardar);
            }
        }
        catch(Exception e){
            throw new ProyectoException("El proyecto no pudo ser creado");
        }
    }

    public void actualizarProyecto(Proyecto p) throws ProyectoException {
        try {
            if (usarApiRest) {
                daoApiRest.actualizarProyecto(p);
            }
            else {
                ContentValues datosAGuardar = new ContentValues();
                datosAGuardar.put(ProyectoDBMetadata.TablaProyectoMetadata.TITULO, p.getNombre());
                open(true);
                db.update(ProyectoDBMetadata.TABLA_PROYECTO, datosAGuardar, ProyectoDBMetadata.TablaProyectoMetadata._ID + "=" + p.getId(), null);
            }
        }
        catch(Exception e){
            throw new ProyectoException("El proyecto no se pudo actualizar");
        }
    }

    /**
     * Devuelve una lista con todos los proyectos, exception sino encuentra nada o se produce un error
     * @return
     */
    public List<Proyecto> listarProyectos() throws ProyectoException {
        Proyecto nuevoProyecto;
        List<Proyecto> listaProyectos = null;
        try {
            if (usarApiRest) {
                listaProyectos = daoApiRest.listarProyectos();
            }
            else {
                listaProyectos = new ArrayList<>();
                open(false);
                Cursor result = db.rawQuery("SELECT " + ProyectoDBMetadata.TablaProyectoMetadata._ID + "," + ProyectoDBMetadata.TablaProyectoMetadata.TITULO + " FROM " + ProyectoDBMetadata.TABLA_PROYECTO, null);
                while (result.moveToNext()) {
                    // Primer parameto el id, segundo el nombre y tercero el email
                    nuevoProyecto = new Proyecto(result.getInt(0), result.getString(1));
                    listaProyectos.add(nuevoProyecto);
                }
                result.close();
            }
        }
        catch(Exception e){
            throw new ProyectoException("No se pudieron encontrar proyectos");
        }
        return listaProyectos;
    }

    /**
     * Devuelve un cursor con todos lo proyectos
     * @return
     */
    public Cursor getCursorProyectos() throws ProyectoException {
        Cursor cursorPry = null;
        try {
            if (usarApiRest) { // busco los proyectos en la nube
                cursorPry = daoApiRest.getCursorProyectos();
            } else { // Saco los proyectos de la bd local
                cursorPry = db.rawQuery("SELECT " + ProyectoDBMetadata.TablaProyectoMetadata._ID + " AS _id " + ", " + ProyectoDBMetadata.TablaProyectoMetadata.TITULO + " FROM " + ProyectoDBMetadata.TABLA_PROYECTO, null);
            }
        }
        catch(Exception e){
            throw new ProyectoException("No se encontraron proyectos");
        }
        return cursorPry;
    }

    /**
     * Desactiva o activa la busqueda de datos en la base de datos local
     * @param usarApiRest
     */
    public void buscarEnLaNube(boolean usarApiRest){
        this.usarApiRest=usarApiRest;
    }
}
