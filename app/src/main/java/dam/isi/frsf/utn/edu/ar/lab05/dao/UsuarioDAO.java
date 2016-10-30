package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.ProyectoApiRest;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by Agustin on 10/30/2016.
 */
public class UsuarioDAO {

    private static final int MODO_PERSISTENCIA_MIXTA = 2;  // Los datos se almacenan en la api rest y en local
    private static final int MODO_PERSISTENCIA_LOCAL = 1;  // Los datos se almacenan solamente en la bdd local
    private static final int MODO_PERSISTENCIA_REMOTA = 0; // Los datos se almacenan solamente en la nube
    private static int MODO_PERSISTENCIA_CONFIGURADA; // Como default es remota
    private ProyectoOpenHelper dbHelper;
    private SQLiteDatabase db;
    private List<Usuario> listaUsuarios;
    private ProyectoApiRest daoApiRest;
    private static boolean usarApiRest;
    private Context context;


    public UsuarioDAO(Context c) {
        MODO_PERSISTENCIA_CONFIGURADA = MODO_PERSISTENCIA_REMOTA;
        this.context=c;
        this.dbHelper = new ProyectoOpenHelper(c);
        this.daoApiRest = new ProyectoApiRest();
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
     * Si el usuario existe no hace nada, si no existe lo guarde
     * Devuelve el usuario pasado por parametro pero actualizado con el ID (si este no existia)
     * @param nuevoUsuario
     */
    public Usuario guardarUsuario(Usuario nuevoUsuario)
    {
        if(getUsuario(nuevoUsuario.getId())==null) {
            ContentValues datosAGuardar = new ContentValues();
            datosAGuardar.put(ProyectoDBMetadata.TablaUsuariosMetadata.MAIL, nuevoUsuario.getCorreoElectronico());
            datosAGuardar.put(ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO, nuevoUsuario.getNombre());
            open(true);
            try {
                long idFilaNuevoUsuario;
                idFilaNuevoUsuario = db.insert(ProyectoDBMetadata.TABLA_USUARIOS, null, datosAGuardar);
                nuevoUsuario.setId((int) idFilaNuevoUsuario);
                daoApiRest.guardarUsuario(nuevoUsuario);
            }
            catch (Exception e) {
                System.out.println("BD Exploto en el insert de usuario");
            }
        }
        return nuevoUsuario;
    }

    public List<Usuario> listarUsuarios(){
        Usuario nuevoUsuario;
        listaUsuarios = new ArrayList<>();
        try
        {
            open(false);
            Cursor result = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaUsuariosMetadata._ID+","+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO+","+ProyectoDBMetadata.TablaUsuariosMetadata.MAIL+ " FROM "+ProyectoDBMetadata.TABLA_USUARIOS,null);
            while (result.moveToNext())
            {
                // Primer parameto el id, segundo el nombre y tercero el email
                nuevoUsuario = new Usuario(result.getInt(0), result.getString(1), result.getString(2));
                listaUsuarios.add(nuevoUsuario);
            }
            result.close();
        }
        catch(Exception e)
        {
            System.out.println("Exploto la bd al listar usuarios");
        }
        finally
        {
            return listaUsuarios;
        }
    }

    public Usuario getUsuario(Integer idUsuario){
        Usuario usuario = null;
        try {
            open(false);
            Cursor result = db.rawQuery("SELECT " + ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO +" , "+ ProyectoDBMetadata.TablaUsuariosMetadata.MAIL + " FROM " + ProyectoDBMetadata.TABLA_USUARIOS+ " WHERE " + ProyectoDBMetadata.TablaUsuariosMetadata._ID+" = " + idUsuario, null);
            result.moveToFirst();
            usuario = new Usuario();
            usuario.setId(idUsuario);
            usuario.setNombre(result.getString(0));
            usuario.setCorreoElectronico(result.getString(1));
            result.close();

        }
        catch (Exception e) {
            System.out.println("Error al buscar un usuario");
            usuario = null;
        }
        finally {
            return usuario;
        }
    }


}
