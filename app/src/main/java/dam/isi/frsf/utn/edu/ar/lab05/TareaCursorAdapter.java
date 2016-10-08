package dam.isi.frsf.utn.edu.ar.lab05;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
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
    private Button btnEliminar,btnFinalizar,btnEditar;
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

        btnFinalizar = (Button)   view.findViewById(R.id.tareaBtnFinalizada);
        btnEditar = (Button)   view.findViewById(R.id.tareaBtnEditarDatos);
        btnEliminar = (Button) view.findViewById(R.id.tareaBtnEliminar);
        btnEstado = (ToggleButton) view.findViewById(R.id.tareaBtnTrabajando);

        nombre.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.TAREA)));
        Integer horasAsigandas = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS));
        tiempoAsignado.setText(Integer.toString(horasAsigandas*60)+" ");

        minutosTrabajados = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS));
        tiempoTrabajado.setText(" "+minutosTrabajados);
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

        btnEstado.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEstado.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.tareaBtnTrabajando:
            {
                accionBotonTrabajar(v);
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
    private void accionBotonTrabajar(View v) {
        final Integer idTarea= (Integer) v.getTag();
        if(!btnEstado.isChecked())
        {
            tArranqueTrabajo= System.currentTimeMillis();
            btnEstado.setChecked(true);
            Toast.makeText(contexto,"Ahora estas trabajando",Toast.LENGTH_SHORT).show();
        }
        else
        {
            tFinalTrabajo=System.currentTimeMillis();
            tiempoTrabajado=tFinalTrabajo-tArranqueTrabajo;
            minutosTrabajados = (int) (( TimeUnit.MILLISECONDS.toSeconds(tiempoTrabajado) )/5);
           // minutosTrabajados = ( (int) ( (tiempoTrabajado/(1000) )%60) )/5; // Pasa de milisegundos a segundos y despues divido por 5 para pasarlo a minutos
            myDao.ActualizarMinutosTrabajados(idTarea,minutosTrabajados);
            minutosTrabajados = null;
            tiempoTrabajado = null;
            tArranqueTrabajo = null;
            tFinalTrabajo = null;
            btnEstado.setChecked(false);
            changeCursor();
            Toast.makeText(contexto,"Dejaste de trabajar",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Accion que se ejecuta cuando se presiona el boton editar datos
     * TODO Terminar de armar el editar y devolver un resultado
     */
    private void accionBotonEditarDatos(View view) {
        final Integer idTarea= (Integer) view.getTag();
        Intent intEditarAct = new Intent(contexto,AltaTareaActivity.class);
        intEditarAct.putExtra("ID_TAREA",idTarea);
        intEditarAct.putExtra("RESULT_CODE",1);
        ((Activity) contexto).startActivityForResult(intEditarAct,1);

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
                changeCursor(); // Con este mensaje se actualiza el cursor

            }
        });
        backGroundUpdate.start();
        Toast.makeText(contexto,"La tarea se marco como finalizada",Toast.LENGTH_SHORT).show();
    }

    /**
     * Actualiza el cursor (renueva la lista)
     */
    public void changeCursor()
    {
        handlerRefresh.sendEmptyMessage(1);
    }

    /**
     * Handler que permite actualizar el cursor
     */
    Handler handlerRefresh = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            TareaCursorAdapter.this.changeCursor(myDao.listaTareas(1));
        }
    };
    /**
     * Accion que se ejecuta cuando se presiona el boton eliminar
     */
    private void accionBotonEliminar(View v){
        final Integer idTarea= (Integer) v.getTag();
        myDao.borrarTarea(idTarea);
        handlerRefresh.sendEmptyMessage(1);
        Toast.makeText(contexto,"La tarea se elimino exitosamente",Toast.LENGTH_LONG).show();
    }

}

