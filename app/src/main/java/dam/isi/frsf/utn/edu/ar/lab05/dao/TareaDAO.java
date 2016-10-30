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
 * Created by Agustin on 10/30/2016.
 */
public class TareaDAO {
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


    private static final int MODO_PERSISTENCIA_MIXTA = 2;  // Los datos se almacenan en la api rest y en local
    private static final int MODO_PERSISTENCIA_LOCAL = 1;  // Los datos se almacenan solamente en la bdd local
    private static final int MODO_PERSISTENCIA_REMOTA = 0; // Los datos se almacenan solamente en la nube
    private static int MODO_PERSISTENCIA_CONFIGURADA; // Como default es remota
    private ProyectoOpenHelper dbHelper;
    private SQLiteDatabase db;
    private List<Usuario> listaUsuarios;
    private ProyectoApiRest daoApiRest;
    private UsuarioDAO daoUsuario;
    private ProyectoDAO daoProyecto;
    private static boolean usarApiRest;
    private Context context;


    public TareaDAO(Context c) {
        MODO_PERSISTENCIA_CONFIGURADA = MODO_PERSISTENCIA_REMOTA;
        this.context=c;
        this.dbHelper = new ProyectoOpenHelper(c);
        this.daoApiRest = new ProyectoApiRest();
        this.daoProyecto = new ProyectoDAO(c);
        this.daoUsuario = new UsuarioDAO(c);
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
            Usuario responsable = daoUsuario.getUsuario(resultadoTareas.getInt(5));
            Proyecto proyecto = daoProyecto.getProyecto(resultadoTareas.getInt(6));
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
                Usuario responsable = daoUsuario.getUsuario(resultadoTareas.getInt(5));
                Proyecto proyecto = daoProyecto.getProyecto(resultadoTareas.getInt(6));
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

    public void ActualizarMinutosTrabajados(Integer idTarea, int minutosTrabajados)
    {
        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,minutosTrabajados);
        try {
            open(true);
            db.execSQL("UPDATE " + ProyectoDBMetadata.TABLA_TAREAS + " SET MINUTOS_TRABAJDOS = MINUTOS_TRABAJDOS + "+minutosTrabajados+" WHERE _ID = "+idTarea);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Exploto la bd al actualizar los minutos");
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

    /**
     * Devuelve el objeto prioridad  con el id parametro
     * @param idPrioridad
     * @return
     */
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
}
