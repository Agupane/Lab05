package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

/**
 * Created by Agustin on 10/8/2016.
 */

public class ProyectosBuscadosAdapter extends ArrayAdapter{
    private LayoutInflater inflater;
    private Context contexto;
    private TextView tvNombreProyecto;
    public ProyectosBuscadosAdapter(Context context, List<Proyecto> items) {
        super(context, R.layout.activity_listar_proyectos,items);
        inflater = LayoutInflater.from(context);
        contexto=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null)
        {
            row = inflater.inflate(R.layout.fila_proyecto, parent, false);
        }
        cargarVariables(row,position);
        return (row);
    }

    private void cargarVariables(View row,int position)
    {
        tvNombreProyecto = (TextView) row.findViewById(R.id.tvNombreProyecto);

        try {
            Proyecto proyecto = (Proyecto) this.getItem(position);
            tvNombreProyecto.setText(proyecto.getNombre());
        }
        catch(Exception e)
        {
            System.out.println("Error al cargar tareas buscadas");
        }
    }
}
