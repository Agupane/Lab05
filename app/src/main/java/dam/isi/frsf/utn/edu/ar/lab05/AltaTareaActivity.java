package dam.isi.frsf.utn.edu.ar.lab05;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

public class AltaTareaActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,Button.OnClickListener{
    private Spinner spinnerListaUsuarios;
    private ArrayAdapter adapterListaUsuarios;
    private List<Usuario> listaUsuarios;
    private ProyectoDAO proyectoDAO;
    private SeekBar sbPrioridad;
    private Integer horasEstimadas;
    private Prioridad prioridad;
    private EditText etDescripcionTarea,etHorasEstimadas;
    private String descripcionTarea;
    private Button btnGuardar,btnCancelar;
    private Tarea nuevaTarea;
    private Usuario usuarioSeleccionado;
    private Proyecto proyectoSeleccionado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_tarea);
        cargarComponentes();
        listaUsuarios=proyectoDAO.listarUsuarios();

        adapterListaUsuarios = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listaUsuarios);
        adapterListaUsuarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListaUsuarios.setAdapter(adapterListaUsuarios);

        sbPrioridad.setOnSeekBarChangeListener(this);
        sbPrioridad.setMax(3);
        btnCancelar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
    }

    /**
     * Inicializa todas las variables
     */
    private void cargarComponentes()
    {
        etDescripcionTarea = (EditText) findViewById(R.id.etDescripcion);
        etHorasEstimadas = (EditText) findViewById(R.id.etHorasEstimadas);
        sbPrioridad = (SeekBar) findViewById(R.id.seekBarPrioridad);
        spinnerListaUsuarios = (Spinner) findViewById(R.id.spinnerUsuarios);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        prioridad = new Prioridad();
        listaUsuarios = new ArrayList();
        proyectoDAO = new ProyectoDAO(this);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        int intPrioridad = seekBar.getProgress();
        intPrioridad++;
        prioridad.setPrioridad(Integer.toString(intPrioridad));  // Como la prioridad es de 1 a 4 y el seekbar de 0 a 3, lo aumento en 1
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnGuardar:
            {
                accionBotonGuardar();
                break;
            }
            case R.id.btnCancelar:
            {
                accionBotonCancelar();
                break;
            }
            default:
            {
                break;
            }
        }
    }
    private void accionBotonGuardar()
    {
        horasEstimadas = Integer.getInteger( String.valueOf(etHorasEstimadas.getText()) );
        descripcionTarea = String.valueOf(etDescripcionTarea.getText());
        usuarioSeleccionado = (Usuario) spinnerListaUsuarios.getSelectedItem();
        proyectoSeleccionado = (Proyecto) proyectoDAO.getProyecto(0);
        nuevaTarea = new Tarea(false,horasEstimadas,0,false,proyectoSeleccionado,prioridad,usuarioSeleccionado);
        proyectoDAO.nuevaTarea(nuevaTarea);
    }
    private void accionBotonCancelar()
    {

    }
}
