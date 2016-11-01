package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.Exception.UsuarioException;
import dam.isi.frsf.utn.edu.ar.lab05.MyApplication;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by Agustin on 10/30/2016.
 */
public class UsuarioDAO {
    private static UsuarioDAO ourInstance = new UsuarioDAO();
    private static final int MODO_PERSISTENCIA_MIXTA = 2;  // Los datos se almacenan en la api rest y en local
    private static final int MODO_PERSISTENCIA_LOCAL = 1;  // Los datos se almacenan solamente en la bdd local
    private static final int MODO_PERSISTENCIA_REMOTA = 0; // Los datos se almacenan solamente en la nube
    private static int MODO_PERSISTENCIA_CONFIGURADA; // Como default es remota y local

    private static ProyectoOpenHelper dbHelper;
    private static ProyectoApiRest daoApiRest= ProyectoApiRest.getInstance();
    private static boolean usarApiRest = false;
    private static Context context;

    private SQLiteDatabase db;
    private List<Usuario> listaUsuarios;



    private UsuarioDAO() {
        MODO_PERSISTENCIA_CONFIGURADA = MODO_PERSISTENCIA_MIXTA;
        context=MyApplication.getAppContext();
        dbHelper = new ProyectoOpenHelper(context);
    }
    public static UsuarioDAO getInstance(){
        return ourInstance;
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
     * SE FIJA SI EXISTE POR EL NOMBRE, DADO QUE EL ID NUNCA VA A ESTAR SETEADO POR SALIR DE CONTACTOS
     * TODO AGREGAR INFO DE LA LISTA DE CONTACTOS AL USUARIO PARA RECONOCERLO UNIVOCAMENTE SIN ID
     * @param nuevoUsuario
     */
    public Usuario guardarUsuario(Usuario nuevoUsuario) throws UsuarioException {
        Usuario usuarioActualizado = nuevoUsuario;
        daoApiRest= ProyectoApiRest.getInstance();
        switch (MODO_PERSISTENCIA_CONFIGURADA) {
            case MODO_PERSISTENCIA_LOCAL: {
                usuarioActualizado = guardarUsuarioLocal(nuevoUsuario);
                break;
            }
            case MODO_PERSISTENCIA_REMOTA: {
                usuarioActualizado = guardarUsuarioRemoto(nuevoUsuario);
                break;
            }
            case MODO_PERSISTENCIA_MIXTA: {
                usuarioActualizado = guardarUsuarioLocal(nuevoUsuario);
                usuarioActualizado = guardarUsuarioRemoto(nuevoUsuario);
                break;
            }
            default: {
                throw new UsuarioException("Se produjo un error al persistir el usuario");
            }
        }
        return usuarioActualizado;
    }

    /**
     * Almacena el usuario en la bd local y devuelve el usuario con su id
     * Si el usuario existia devuelve el mismo usuario y no hace nada
     * Si se produce un error lanza una exception
     * @param nuevoUsuario
     * @return
     */
    private Usuario guardarUsuarioLocal(Usuario nuevoUsuario) throws UsuarioException {
       // if(getUsuario(nuevoUsuario.getId())==null) {
        /* SI SE CUMPLE ESTA CONDICION ES PORQUE EL USUARIO PROVIENE DE LA LISTA DE CONTACTOS (NO TIENE ID)*/
        if(nuevoUsuario.getId()== null && nuevoUsuario.getNombre()!=null){
            /** Como no se puede usar id para ver si existe entonces revisa que el usuario con el email y nombre no existan y entonces lo crea */
            if(!existeUsuario(nuevoUsuario.getCorreoElectronico())){
                ContentValues datosAGuardar = new ContentValues();
                datosAGuardar.put(ProyectoDBMetadata.TablaUsuariosMetadata.MAIL, nuevoUsuario.getCorreoElectronico());
                datosAGuardar.put(ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO, nuevoUsuario.getNombre());
                open(true);
                try {
                    long idFilaNuevoUsuario;
                    idFilaNuevoUsuario = db.insert(ProyectoDBMetadata.TABLA_USUARIOS, null, datosAGuardar);
                    nuevoUsuario.setId((int) idFilaNuevoUsuario);
                }
                catch (Exception e) {
                    throw new UsuarioException("Se produjo un error al persistir el usuario");
                }
            }
        }
        return nuevoUsuario;
    }

    private Usuario guardarUsuarioRemoto(Usuario nuevoUsuario) throws UsuarioException {
        daoApiRest.guardarUsuario(nuevoUsuario);
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

    /**
     * Devuelve el usuario con el id de parametro
     * @param idUsuario
     * @return
     */
    public Usuario getUsuario(Integer idUsuario) throws UsuarioException {
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
            e.printStackTrace();
            throw new UsuarioException("Usuario no encontrado");
        }
        return usuario;
    }

    /**
     * Devuelve el usuario con el mail parametro en la bdd local
     * @param email
     * @return
     * @throws UsuarioException
     */
    public Usuario getUsuarioPorEmail(String email) throws UsuarioException {
        Usuario usuario = null;
        try {
            open(false);
            Cursor result = db.rawQuery("SELECT " + ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO +" , "+ ProyectoDBMetadata.TablaUsuariosMetadata._ID + " FROM " + ProyectoDBMetadata.TABLA_USUARIOS+ " WHERE " + ProyectoDBMetadata.TablaUsuariosMetadata.MAIL+" = " + email, null);
            result.moveToFirst();
            usuario = new Usuario();
            usuario.setId(result.getInt(1));
            usuario.setNombre(result.getString(0));
            usuario.setCorreoElectronico(email);
            result.close();
        }
        catch (Exception e) {
            //   e.printStackTrace();
            throw new UsuarioException("Usuario no encontrado");
        }
        return usuario;
    }

    /**
     * Verifica si el usuario con el email parametros existe en la bd local y en la remota
     * Si existe en una y no en la otra lo sincroniza - TODO ESTO
     * @param email
     * @return
     */
    public boolean existeUsuario(String email){
        Usuario usuarioBuscadoLocal,usuarioBuscadoRemoto=null;
        usuarioBuscadoLocal = existeUsuarioLocal(email);
        usuarioBuscadoRemoto = existeUsuarioRemoto(email);
        boolean encontrado=false;
        if(usuarioBuscadoLocal!=null)
        {
            encontrado=true;
            if(usuarioBuscadoRemoto!=null){

            }
            else{
                /** Sincronizar con la nube */
            }
        }
        else{
            if(usuarioBuscadoRemoto!=null){
                /**Sincronizar con bdd local */
                encontrado=true;
            }
        }
        return encontrado;
    }

    /**
     * Si existe el usuario en la bdd local con ese nombre y ese email lo devuelve,
     * de lo contrario devuelve null
     * @param email
     * @return
     */
    private Usuario existeUsuarioLocal(String email){
        Usuario buscado = null;
        try {
            buscado = getUsuarioPorEmail(email);
        }
        catch (UsuarioException e) {

        }
        return buscado;
    }
    /**
     * Si existe el usuario en la bdd rest con ese nombre y ese email lo devuelve,
     * de lo contrario devuelve null
     * @param email
     * @return
     */
    private Usuario existeUsuarioRemoto(String email){
        Usuario buscado = null;
      //  daoApiRest.buscarUsuario(nombre,email);
        return buscado;
    }

}
