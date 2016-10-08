package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoDAO {

    private static final String _SQL_TAREAS_X_PROYECTO = "SELECT "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata._ID+" as "+ProyectoDBMetadata.TablaTareasMetadata._ID+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.TAREA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD +
            ", "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD +" as "+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE +
            ", "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO +" as "+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS+
            " FROM "+ProyectoDBMetadata.TABLA_PROYECTO + " "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+", "+
            ProyectoDBMetadata.TABLA_USUARIOS + " "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+", "+
            ProyectoDBMetadata.TABLA_PRIORIDAD + " "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+", "+
            ProyectoDBMetadata.TABLA_TAREAS + " "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+
            " WHERE "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+"."+ProyectoDBMetadata.TablaProyectoMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE+" = "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD+" = "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = ?";

    private ProyectoOpenHelper dbHelper;
    private SQLiteDatabase db;
    private List<Usuario> listaUsuarios;

    public ProyectoDAO(Context c){
        this.dbHelper = new ProyectoOpenHelper(c);
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

    public Cursor listaTareas(Integer idProyecto){
        Cursor cursorPry = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaProyectoMetadata._ID+ " FROM "+ProyectoDBMetadata.TABLA_PROYECTO,null);
        Integer idPry= 0;
        if(cursorPry.moveToFirst()){
            idPry=cursorPry.getInt(0);
        }
        Cursor cursor = null;
        Log.d("LAB05-MAIN","PROYECTO : _"+idPry.toString()+" - "+ _SQL_TAREAS_X_PROYECTO);
        cursor = db.rawQuery(_SQL_TAREAS_X_PROYECTO,new String[]{idPry.toString()});
        return cursor;
    }

    public void nuevaTarea(Tarea t)
    {
        ContentValues datosAGuardar = new ContentValues();
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,0);
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD,t.getPrioridad().getId());
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE,t.getResponsable().getId());
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata. PROYECTO,t.getProyecto().getId());
        open(true);
        try {
            db.insert(ProyectoDBMetadata.TABLA_TAREAS,null,datosAGuardar);
            System.out.println("Guardo una tarea");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("BD Exploto en el insert");
        }
    }

    public void actualizarTarea(Tarea t){

    }

    public void borrarTarea(Tarea t){

    }

    /**
     * Borra tarea por id
     * @param idTarea
     */
    public void borrarTarea(int idTarea)
    {
        String[] args = { String.valueOf(idTarea) };
        try
        {
            open(true);
            db.delete(ProyectoDBMetadata.TABLA_TAREAS,"_id=?", args);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Bd exploto al eliminar tarea");
        }

    }

    public List<Prioridad> listarPrioridades(){
        return null;
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
            System.out.println("Exploto la bd al abrirla");
        }
        finally
        {
            return listaUsuarios;
        }

    }

    public void finalizar(Integer idTarea){
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,1);
        SQLiteDatabase mydb =dbHelper.getWritableDatabase();
        mydb.update(ProyectoDBMetadata.TABLA_TAREAS, valores, "_id=?", new String[]{idTarea.toString()});
       // mydb.close();
    }

    public List<Tarea> listarDesviosPlanificacion(Boolean soloTerminadas,Integer desvioMaximoMinutos){
        // retorna una lista de todas las tareas que tardaron más (en exceso) o menos (por defecto)
        // que el tiempo planificado.
        // si la bandera soloTerminadas es true, se busca en las tareas terminadas, sino en todas.
        Cursor resultadoTareas;
        List<Tarea> listaTareasDesviadas = new ArrayList<>();
        Tarea nuevaTarea;
        if(soloTerminadas)
        {
            resultadoTareas= db.rawQuery("SELECT * FROM "+ProyectoDBMetadata.TABLA_TAREAS+" WHERE "+ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA +" = "+1,null);
        }
        else
        {
            resultadoTareas= db.rawQuery("SELECT * FROM "+ProyectoDBMetadata.TABLA_TAREAS,null);
        }
        while (resultadoTareas.moveToNext())
        {
            // Primer parameto el id, segundo el nombre y tercero el email
            Integer idTarea = resultadoTareas.getInt(0);
            String descripcion = resultadoTareas.getString(1);
            Integer horasEstimadas = resultadoTareas.getInt(2);
            Integer minutosTrabajados = resultadoTareas.getInt(3);
            Prioridad prioridad = getPrioridad(resultadoTareas.getInt(4));
            Usuario responsable = getUsuario(resultadoTareas.getInt(5));
            Proyecto proyecto = getProyecto(resultadoTareas.getInt(6));
            Boolean finalizada = soloTerminadas;
            nuevaTarea = new Tarea(idTarea,finalizada,horasEstimadas,minutosTrabajados,proyecto,prioridad,responsable,descripcion);
            listaTareasDesviadas.add(nuevaTarea);
        }
        return listaTareasDesviadas;
    }
    public Prioridad getPrioridad (Integer idPrioridad) {
        Prioridad prioridad = new Prioridad();
        try {
            open(false);
            Cursor result = db.rawQuery("SELECT " + ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD + " FROM " + ProyectoDBMetadata.TABLA_PRIORIDAD + " WHERE " + ProyectoDBMetadata.TablaPrioridadMetadata._ID+" = " + idPrioridad, null);
            result.moveToFirst();
            prioridad.setId(idPrioridad);
            prioridad.setPrioridad((result.getString(0)));
            result.close();

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error al buscar una prioridad");
        }
        finally {
            return prioridad;
        }
    }
    public Usuario getUsuario(Integer idUsuario){
        Usuario usuario = new Usuario();
        try {
            open(false);
            Cursor result = db.rawQuery("SELECT " + ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO +" , "+ ProyectoDBMetadata.TablaUsuariosMetadata.MAIL + " FROM " + ProyectoDBMetadata.TABLA_USUARIOS+ " WHERE " + ProyectoDBMetadata.TablaUsuariosMetadata._ID+" = " + idUsuario, null);
            result.moveToFirst();
            usuario.setId(idUsuario);
            usuario.setNombre(result.getString(0));
            usuario.setCorreoElectronico(result.getString(1));
            result.close();

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error al buscar un usuario");
        }
        finally {
            return usuario;
        }
    }

    /**
     * Retorna el proyecto con id idProyecto
     * @param idProyecto
     * @return
     */
    public Proyecto getProyecto(int idProyecto)
    {
        Proyecto nuevoProyecto = new Proyecto();
        try
        {
            open(false);
            Cursor result = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaProyectoMetadata.TITULO + " FROM "+ProyectoDBMetadata.TABLA_PROYECTO+" WHERE "+ProyectoDBMetadata.TablaProyectoMetadata._ID+" = "+idProyecto,null);
            result.moveToFirst();
            nuevoProyecto.setId(idProyecto);
            nuevoProyecto.setNombre(result.getString(0));
            result.close();
        }
        catch(Exception e)
        {
            System.out.println("Exploto la bd al abrirla");

        }
        finally
        {
            return nuevoProyecto;
        }
    }
    public void ActualizarMinutosTrabajados(Integer idTarea, int minutosTrabajados)
    {
        ContentValues valores = new ContentValues();
        int filasModificadas=0;
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,minutosTrabajados);
        try {
            open(true);

            //filasModificadas = db.update(ProyectoDBMetadata.TABLA_TAREAS, valores, "_ID="+idTarea, null);
            db.execSQL("UPDATE " + ProyectoDBMetadata.TABLA_TAREAS + " SET MINUTOS_TRABAJDOS = MINUTOS_TRABAJDOS + "+minutosTrabajados+" WHERE _ID = "+idTarea);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Exploto la bd al actualizar los minutos");
        }
    }

}
