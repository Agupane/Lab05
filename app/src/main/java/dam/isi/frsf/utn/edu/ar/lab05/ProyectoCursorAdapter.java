package dam.isi.frsf.utn.edu.ar.lab05;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
public class ProyectoCursorAdapter extends CursorAdapter implements View.OnClickListener{
    private LayoutInflater inflador;
    private ProyectoDAO myDao;
    private Context contexto;
    private Button btnEliminar,btnEditar;

    public ProyectoCursorAdapter(Context contexto, Cursor c, ProyectoDAO dao) {
        super(contexto, c, false);
        myDao= dao;
        this.contexto = contexto;
    }

    @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vista = inflador.inflate(R.layout.fila_proyecto,viewGroup,false);
        return vista;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        //obtener la posicion de la fila actual y asignarla a los botones y checkboxes'

        int pos = cursor.getPosition();
        this.contexto=context;
        // Referencias UI.
        TextView nombreProyecto= (TextView) view.findViewById(R.id.tvNombreProyecto);

        btnEditar = (Button)   view.findViewById(R.id.proyectoBtnEditar);
        btnEliminar = (Button) view.findViewById(R.id.proyectoBtnEliminar);

        nombreProyecto.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaProyectoMetadata.TITULO)));

        btnEliminar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEliminar.setOnClickListener(this);

        btnEditar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEditar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {

        switch(v.getId())
        {
            case R.id.proyectoBtnEditar:
            {
                accionBotonEditarDatos(v);
                break;
            }
            case R.id.proyectoBtnEliminar:
            {
                accionBotonEliminar(v);
                break;
            }
        }

    }

    /**
     * Accion que se ejecuta cuando se presiona el boton editar
     * TODO Terminar de armar el editar y devolver un resultado
     */
    private void accionBotonEditarDatos(View view) {
        final Integer idProyecto= (Integer) view.getTag();
        Intent intEditarAct = new Intent(contexto,AltaProyectoActivity.class);
        intEditarAct.putExtra("ID_PROYECTO",idProyecto);
        intEditarAct.putExtra("RESULT_CODE",1);
        ((Activity) contexto).startActivityForResult(intEditarAct,1);
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
            ProyectoCursorAdapter.this.changeCursor(myDao.getCursorProyectos());
        }
    };
    /**
     * Accion que se ejecuta cuando se presiona el boton eliminar
     */
    private void accionBotonEliminar(View v){
        final Integer idProyecto = (Integer) v.getTag();
        myDao.borrarProyecto(idProyecto);
        handlerRefresh.sendEmptyMessage(1);
        Toast.makeText(contexto,"El proyecto se elimino exitosamente",Toast.LENGTH_LONG).show();

    }

}

