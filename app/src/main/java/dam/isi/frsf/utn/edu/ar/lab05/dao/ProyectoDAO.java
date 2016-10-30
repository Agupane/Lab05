package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.ProyectoApiRest;
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
    private ProyectoApiRest daoApiRest;

    public ProyectoDAO(Context c){
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
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("BD Exploto en el insert de tarea");
        }
    }
    public void actualizarTarea(Tarea t){
        ContentValues datosAGuardar = new ContentValues();
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,0);
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD,t.getPrioridad().getId());
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE,t.getResponsable().getId());
        datosAGuardar.put(ProyectoDBMetadata.TablaTareasMetadata. PROYECTO,t.getProyecto().getId());
        open(true);
        try {
            db.update(ProyectoDBMetadata.TABLA_TAREAS,datosAGuardar,ProyectoDBMetadata.TablaTareasMetadata._ID+"="+t.getId(),null);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("BD Exploto al actualizar tarea");
        }
    }

    public void borrarTarea(Tarea t){

    }
    public Tarea getTarea(int idTarea)
    {
        Tarea nuevaTarea = null;
        Cursor resultadoTareas;
        try
        {
            open(false);
            resultadoTareas = db.rawQuery("SELECT * " + " FROM "+ProyectoDBMetadata.TABLA_TAREAS+" WHERE "+ProyectoDBMetadata.TablaTareasMetadata._ID+" = "+idTarea,null);
            resultadoTareas.moveToFirst();

            String descripcion = resultadoTareas.getString(1);
            Integer horasEstimadas = resultadoTareas.getInt(2);
            Integer minutosTrabajados = resultadoTareas.getInt(3);
            Prioridad prioridad = getPrioridad(resultadoTareas.getInt(4));
            Usuario responsable = getUsuario(resultadoTareas.getInt(5));
            Proyecto proyecto = getProyecto(resultadoTareas.getInt(6));
            Boolean finalizada;
            if(resultadoTareas.getInt(7) == 0 )
            {
                finalizada=false;
            }
            else {
                finalizada = true;
            }
            nuevaTarea = new Tarea(idTarea,finalizada,horasEstimadas,minutosTrabajados,proyecto,prioridad,responsable,descripcion);
            resultadoTareas.close();
        }
        catch(Exception e)
        {

            System.out.println("Exploto la bd al buscar una tarea");

        }
        return nuevaTarea;
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

    public Prioridad getPrioridad (Integer idPrioridad) {
        Prioridad prioridad = new Prioridad();
        try {
            open(false);
            Cursor result = db.rawQuery("SELECT " + ProyectoDBMetadata.TablaPrioridadMetadata._ID + " , "+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD+" FROM " + ProyectoDBMetadata.TABLA_PRIORIDAD + " WHERE " + ProyectoDBMetadata.TablaPrioridadMetadata._ID+" = " + idPrioridad, null);
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

    public void finalizar(Integer idTarea){
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,1);
        SQLiteDatabase mydb =dbHelper.getWritableDatabase();
        mydb.update(ProyectoDBMetadata.TABLA_TAREAS, valores, "_id=?", new String[]{idTarea.toString()});
       // mydb.close();
    }

    public List<Tarea> listarDesviosPlanificacion(Boolean soloTerminadas,Integer desvioMinimo){
        // retorna una lista de todas las tareas que tardaron más (en exceso) o menos (por defecto)
        // que el tiempo planificado.
        // si la bandera soloTerminadas es true, se busca en las tareas terminadas, sino en todas.
        Cursor resultadoTareas;
        List<Tarea> listaTareasDesviadas = new ArrayList<>();
        Tarea nuevaTarea;
        try {
            open(false);
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
                Integer idTarea = resultadoTareas.getInt(0);
                String descripcion = resultadoTareas.getString(1);
                Integer horasEstimadas = resultadoTareas.getInt(2);
                Integer minutosTrabajados = resultadoTareas.getInt(3);
                Prioridad prioridad = getPrioridad(resultadoTareas.getInt(4));
                Usuario responsable = getUsuario(resultadoTareas.getInt(5));
                Proyecto proyecto = getProyecto(resultadoTareas.getInt(6));
                Boolean finalizada;
                if(resultadoTareas.getInt(7) == 0 )
                {
                    finalizada=false;
                }
                else {
                    finalizada = true;
                }
                nuevaTarea = new Tarea(idTarea,finalizada,horasEstimadas,minutosTrabajados,proyecto,prioridad,responsable,descripcion);
                listaTareasDesviadas.add(nuevaTarea);
            }
            listaTareasDesviadas = filtrarTareas(listaTareasDesviadas,desvioMinimo); // Selecciona solo las que tengan el mayor desvio, despues hay que cambiar esto pq es horrible
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Bd exploto al internet buscar desvios");
        }
        finally
        {
            return listaTareasDesviadas;
        }
    }

    /**
     * TODO Implementar la consulta bien y borrar esto, es solo temporal
     * Filtra las tareas segun el numero de desvio, METODO A BORRAR
     * @param listaTareas
     *
     * @return
     */
    private List<Tarea> filtrarTareas(List<Tarea> listaTareas,Integer desvioMin)
    {
        List<Tarea> listaFiltrada = new ArrayList<>();
        Integer minutosTrabajados;
        Integer minutosPlanificados;
        for(Tarea iteradora:listaTareas)
        {
             minutosTrabajados= iteradora.getMinutosTrabajados();
             minutosPlanificados= iteradora.getHorasEstimadas();
            if( (Math.abs(minutosPlanificados - minutosTrabajados) >desvioMin) || (Math.abs(minutosTrabajados - minutosPlanificados) >desvioMin))
            {
                listaFiltrada.add(iteradora);
            }
        }
        return listaFiltrada;
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
            } catch (Exception e) {
                System.out.println("BD Exploto en el insert de usuario");
            }

        }
        return nuevoUsuario;
        */
        return null;
    }

    /**
     * Retorna el proyecto con id idProyecto
     * @param idProyecto
     * @return
     */
    public Proyecto getProyecto(int idProyecto)
    {
        Proyecto nuevoProyecto = null;
        try
        {
            open(false);
            Cursor result = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaProyectoMetadata.TITULO + " FROM "+ProyectoDBMetadata.TABLA_PROYECTO+" WHERE "+ProyectoDBMetadata.TablaProyectoMetadata._ID+" = "+idProyecto,null);
            result.moveToFirst();
            nuevoProyecto = new Proyecto();
            nuevoProyecto.setId(idProyecto);
            nuevoProyecto.setNombre(result.getString(0));
            result.close();

        }
        catch(Exception e)
        {
            System.out.println("Exploto la bd al buscar un proyecto");
            nuevoProyecto = daoApiRest.buscarProyecto(idProyecto);

        }
        return nuevoProyecto;
    }

    /**
     * Borra proyecto por id
     * @param idProyecto
     */
    public void borrarProyecto(int idProyecto)
    {
        String[] args = { String.valueOf(idProyecto) };
        try
        {
            open(true);
            db.delete(ProyectoDBMetadata.TABLA_PROYECTO,"_id=?", args);
            daoApiRest.borrarProyecto(idProyecto);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Bd exploto al eliminar proyecto");
        }
    }
    public void nuevoProyecto(Proyecto nuevoProyecto)
    {
        ContentValues datosAGuardar = new ContentValues();
        datosAGuardar.put(ProyectoDBMetadata.TablaProyectoMetadata.TITULO,nuevoProyecto.getNombre());
        open(true);
        try {
            db.insert(ProyectoDBMetadata.TABLA_PROYECTO,null,datosAGuardar);
            daoApiRest.crearProyecto(nuevoProyecto);
        }
        catch(Exception e)
        {
            System.out.println("BD Exploto en el insert de proyecto");
        }
    }
    public void actualizarProyecto(Proyecto p){
        ContentValues datosAGuardar = new ContentValues();
        datosAGuardar.put(ProyectoDBMetadata.TablaProyectoMetadata.TITULO,p.getNombre());
        open(true);
        try {
            db.update(ProyectoDBMetadata.TABLA_PROYECTO,datosAGuardar,ProyectoDBMetadata.TablaProyectoMetadata._ID+"="+p.getId(),null);
            daoApiRest.actualizarProyecto(p);
        }
        catch(Exception e)
        {
            System.out.println("BD Exploto al actualizar proyecto");
        }
    }
    public List<Proyecto> listarProyectos() {
        Proyecto nuevoProyecto;
        List<Proyecto> listaProyectos = new ArrayList<>();
        try {
            open(false);
            Cursor result = db.rawQuery("SELECT " + ProyectoDBMetadata.TablaProyectoMetadata._ID + "," + ProyectoDBMetadata.TablaProyectoMetadata.TITULO + " FROM " + ProyectoDBMetadata.TABLA_PROYECTO, null);
            while (result.moveToNext()) {
                // Primer parameto el id, segundo el nombre y tercero el email
                nuevoProyecto = new Proyecto(result.getInt(0), result.getString(1));
                listaProyectos.add(nuevoProyecto);
            }
            result.close();
        } catch (Exception e) {
            System.out.println("Exploto la bd al listar proyectos");
        } finally {
            return listaProyectos;
        }
    }
    public Cursor getCursorProyectos(){
            Cursor cursorPry = null;
            cursorPry = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaProyectoMetadata._ID+ " AS _id "+", "+ProyectoDBMetadata.TablaProyectoMetadata.TITULO +" FROM "+ProyectoDBMetadata.TABLA_PROYECTO,null);
      //      cursorPry.moveToFirst();
          //  System.out.println("ID :"+cursorPry.getInt(0));
            //System.out.println("ID :"+cursorPry.getInt(0)+" Nombre "+cursorPry.getString(1));
            return cursorPry;
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
