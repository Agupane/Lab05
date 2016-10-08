package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

/**
 * Created by Agustin on 10/8/2016.
 */

public class TareasBuscadasAdapter extends ArrayAdapter{
    private LayoutInflater inflater;
    private Context contexto;
    private TextView tvDescripcionTarea,tvTareaResponsable,tvTareaPrioridad,tvMinutosAsignados,tvMinutosTrabajados;
    private CheckBox cbTareaFinalizada;
    public TareasBuscadasAdapter(Context context, List<Tarea> items) {
        super(context, R.layout.fila_tarea_buscada,items);
        inflater = LayoutInflater.from(context);
        contexto=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null)
        {
            row = inflater.inflate(R.layout.fila_tarea_buscada, parent, false);
        }
        cargarVariables(row,position);
        return (row);
    }
    private void cargarVariables(View row,int position)
    {
        tvDescripcionTarea = (TextView) row.findViewById(R.id.tvDescripcionTareaBuscada);
        tvMinutosAsignados = (TextView) row.findViewById(R.id.tareaMinutosAsignadosBuscada);
        tvMinutosTrabajados = (TextView) row.findViewById(R.id.tareaMinutosTrabajadosBuscada);
        tvTareaPrioridad = (TextView) row.findViewById(R.id.tareaPrioridadBuscada);
        tvTareaResponsable = (TextView) row.findViewById(R.id.tareaResponsableBuscada);
        cbTareaFinalizada = (CheckBox) row.findViewById(R.id.tareaFinalizadaBuscada);
        try {
            Tarea tarea = (Tarea) this.getItem(position);
            tvDescripcionTarea.setText(tarea.getDescripcion());
            tvTareaPrioridad.setText(tarea.getPrioridad().getPrioridad());
            tvTareaResponsable.setText(tarea.getResponsable().getNombre());
            cbTareaFinalizada.setChecked(tarea.getFinalizada());
            tvMinutosTrabajados.setText(Integer.toString(tarea.getMinutosTrabajados()));
            tvMinutosAsignados.setText(Integer.toString(tarea.getHorasEstimadas()));
        }
        catch(Exception e)
        {
            System.out.println("Error al cargar tareas buscadas");
        }
    }
}
