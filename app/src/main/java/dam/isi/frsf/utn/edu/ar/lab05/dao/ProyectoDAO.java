package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.Exception.ProyectoException;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoDAO {
    private static final int MODO_PERSISTENCIA_MIXTA = 2;  // Los datos se almacenan en la api rest y en local
    private static final int MODO_PERSISTENCIA_LOCAL = 1;  // Los datos se almacenan solamente en la bdd local
    private static final int MODO_PERSISTENCIA_REMOTA = 0;
    private static final int MODO_PERSISTENCIA_CONFIGURADA = MODO_PERSISTENCIA_MIXTA;// Los datos se almacenan solamente en la nube
    private static ProyectoOpenHelper dbHelper;
    private final static ProyectoApiRest daoApiRest = ProyectoApiRest.getInstance();
    private static boolean usarApiRest;
    private static ProyectoDAO ourInstance = new ProyectoDAO();
    private static Context context;

    private SQLiteDatabase db;
    private List<Usuario> listaUsuarios;

    private ProyectoDAO(){

    }

    public static ProyectoDAO getInstance(){
        usarApiRest=true;
        return ourInstance;
    }

    public void open(){
        this.open(false);
    }

    public void open(Boolean toWrite){
        if(dbHelper==null){dbHelper = new ProyectoOpenHelper(context);}
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
        switch(MODO_PERSISTENCIA_CONFIGURADA){
            case MODO_PERSISTENCIA_LOCAL:{
                borrarProyectoLocal(idProyecto);
                break;
            }
            case MODO_PERSISTENCIA_REMOTA:{
                borrarProyectoRemoto(idProyecto);
                break;
            }
            case MODO_PERSISTENCIA_MIXTA:{
                borrarProyectoLocal(idProyecto);
                borrarProyectoRemoto(idProyecto);
                break;
            }
        }
    }

    private void borrarProyectoLocal(int idProyecto) throws ProyectoException {
        try{
            String[] args = {String.valueOf(idProyecto)};
            open(true);
            db.delete(ProyectoDBMetadata.TABLA_PROYECTO, "_id=?", args);
            daoApiRest.borrarProyecto(idProyecto);
        }
        catch(Exception e){
            throw new ProyectoException("El proyecto no pudo ser eliminado");
        }
    }

    private void borrarProyectoRemoto(int idProyecto) throws ProyectoException {
        daoApiRest.borrarProyecto(idProyecto);
    }

    public void nuevoProyecto(Proyecto nuevoProyecto) throws ProyectoException {
        switch(MODO_PERSISTENCIA_CONFIGURADA){
            case MODO_PERSISTENCIA_LOCAL:{
                nuevoProyectoLocal(nuevoProyecto);
                break;
            }
            case MODO_PERSISTENCIA_REMOTA:{
                nuevoProyectoRemoto(nuevoProyecto);
                break;
            }
            case MODO_PERSISTENCIA_MIXTA:{
                nuevoProyectoLocal(nuevoProyecto);
                nuevoProyectoRemoto(nuevoProyecto);
                break;
            }
        }
    }

    private void nuevoProyectoLocal(Proyecto nuevoProyecto) throws ProyectoException {
        try{
            ContentValues datosAGuardar = new ContentValues();
            datosAGuardar.put(ProyectoDBMetadata.TablaProyectoMetadata.TITULO, nuevoProyecto.getNombre());
            open(true);
            db.insert(ProyectoDBMetadata.TABLA_PROYECTO, null, datosAGuardar);
        }
        catch(Exception e){
            throw new ProyectoException("El proyecto no pudo ser creado");
        }
    }

    private void nuevoProyectoRemoto(Proyecto nuevoProyecto) throws ProyectoException {
        daoApiRest.crearProyecto(nuevoProyecto);
    }

    public void actualizarProyecto(Proyecto p) throws ProyectoException {
        switch(MODO_PERSISTENCIA_CONFIGURADA){
            case MODO_PERSISTENCIA_LOCAL:{
                actualizarProyectoLocal(p);
                break;
            }
            case MODO_PERSISTENCIA_REMOTA:{
                actualizarProyectoRemoto(p);
                break;
            }
            case MODO_PERSISTENCIA_MIXTA:{
                actualizarProyectoLocal(p);
                actualizarProyectoRemoto(p);
                break;
            }
        }
    }

    private void actualizarProyectoLocal(Proyecto p) throws ProyectoException {
        try{
            ContentValues datosAGuardar = new ContentValues();
            datosAGuardar.put(ProyectoDBMetadata.TablaProyectoMetadata.TITULO, p.getNombre());
            open(true);
            db.update(ProyectoDBMetadata.TABLA_PROYECTO, datosAGuardar, ProyectoDBMetadata.TablaProyectoMetadata._ID + "=" + p.getId(), null);
        }
        catch(Exception e){
            throw new ProyectoException("El proyecto no se pudo actualizar");
        }
    }

    private void actualizarProyectoRemoto(Proyecto p) throws ProyectoException{
        daoApiRest.actualizarProyecto(p);
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

    public void setContext(Context c){
        this.context=c;
        dbHelper = new ProyectoOpenHelper(c);
    }
}
