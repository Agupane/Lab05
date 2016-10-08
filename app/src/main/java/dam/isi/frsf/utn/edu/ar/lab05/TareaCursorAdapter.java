package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.concurrent.TimeUnit;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;

/**
 * Created by mdominguez on 06/10/16.
 */
public class TareaCursorAdapter extends CursorAdapter implements View.OnClickListener{
    private LayoutInflater inflador;
    private ProyectoDAO myDao;
    private Context contexto;
    private ToggleButton btnEstado;
    private Long tArranqueTrabajo,tFinalTrabajo,tiempoTrabajado;
    private Button btnEliminar;
    private Integer minutosTrabajados;
    public TareaCursorAdapter (Context contexto, Cursor c, ProyectoDAO dao) {
        super(contexto, c, false);
        myDao= dao;
        this.contexto = contexto;
    }

    @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vista = inflador.inflate(R.layout.fila_tarea,viewGroup,false);
        return vista;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        //obtener la posicion de la fila actual y asignarla a los botones y checkboxes
        int pos = cursor.getPosition();
        this.contexto=context;
        // Referencias UI.
        TextView nombre= (TextView) view.findViewById(R.id.tareaTitulo);
        TextView tiempoAsignado= (TextView) view.findViewById(R.id.tareaMinutosAsignados);
        TextView tiempoTrabajado= (TextView) view.findViewById(R.id.tareaMinutosTrabajados);
        TextView prioridad= (TextView) view.findViewById(R.id.tareaPrioridad);
        TextView responsable= (TextView) view.findViewById(R.id.tareaResponsable);
        CheckBox finalizada = (CheckBox)  view.findViewById(R.id.tareaFinalizada);

        final Button btnFinalizar = (Button)   view.findViewById(R.id.tareaBtnFinalizada);
        final Button btnEditar = (Button)   view.findViewById(R.id.tareaBtnEditarDatos);
        btnEliminar = (Button) view.findViewById(R.id.tareaBtnEliminar);
        btnEstado = (ToggleButton) view.findViewById(R.id.tareaBtnTrabajando);
        btnEstado.setOnClickListener(this);
        nombre.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.TAREA)));
        Integer horasAsigandas = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS));
        tiempoAsignado.setText(Integer.toString(horasAsigandas*60)+" ");

        Integer minutosAsignados = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS));
        tiempoTrabajado.setText(" "+minutosAsignados);
        String p = cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS));
        prioridad.setText(p);
        responsable.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS)));
        finalizada.setChecked(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA))==1);
        finalizada.setTextIsSelectable(false);

        btnEliminar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEliminar.setOnClickListener(this);

        btnEditar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEditar.setOnClickListener(this);

        btnFinalizar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnFinalizar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.tareaBtnTrabajando:
            {
                accionBotonTrabajar();
                break;
            }
            case R.id.tareaBtnEditarDatos:
            {
                accionBotonEditarDatos(v);
                break;
            }
            case R.id.tareaBtnFinalizada:
            {
                accionBotonFinalizada(v);
                break;
            }
            case R.id.tareaBtnEliminar:
            {
                accionBotonEliminar(v);
                break;
            }
        }

    }

    /**
     * Accion que se ejecuta cuando se presiona el boton de trabajar
     */
    private void accionBotonTrabajar() {
        if(!btnEstado.isChecked())
        {
            tArranqueTrabajo= System.currentTimeMillis();
        }
        else
        {
            tFinalTrabajo=System.currentTimeMillis();
            tiempoTrabajado=tFinalTrabajo-tArranqueTrabajo;
            minutosTrabajados = ( (int) ( (tiempoTrabajado/(1000) )%60) )/5; // Pasa de milisegundos a segundos y despues divido por 5 para pasarlo a minutos
            guardarTiempoEnBd();
        }
    }

    /**
     * Accion que se ejecuta cuando se presiona el boton editar datos
     */
    private void accionBotonEditarDatos(View view) {
        final Integer idTarea= (Integer) view.getTag();
        Intent intEditarAct = new Intent(contexto,AltaTareaActivity.class);
        intEditarAct.putExtra("ID_TAREA",idTarea);
        contexto.startActivity(intEditarAct);
    }

    /**
     * Accion que se ejecuta cuando se presiona el boton finalizada
     */
    private void accionBotonFinalizada(View view){
        final Integer idTarea= (Integer) view.getTag();
        Thread backGroundUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("LAB05-MAIN","finalizar tarea : --- "+idTarea);
                myDao.finalizar(idTarea);
            }
        });
        backGroundUpdate.start();
    }

    /**
     * Accion que se ejecuta cuando se presiona el boton eliminar
     */
    private void accionBotonEliminar(View v){
        final Integer idTarea= (Integer) v.getTag();
        myDao.borrarTarea(idTarea);
        ((MainActivity) contexto).changeCursor();
        System.out.println("Eliminando tarea");
    }
    // TODO Implementar
    private void guardarTiempoEnBd()
    {

    }
}

