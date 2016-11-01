package dam.isi.frsf.utn.edu.ar.lab05;

import android.database.CursorJoiner;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.Exception.TareaException;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.TareaDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.UsuarioDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

public class AltaTareaActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,Button.OnClickListener{
    private Spinner spinnerListaUsuarios;
    private ArrayAdapter adapterListaUsuarios;
    private List<Usuario> listaUsuarios;
    private TareaDAO tareaDAO;
    private UsuarioDAO usuarioDAO;
    private ProyectoDAO proyectoDAO;
    private EjemploContactos ejemploContactos;
    private SeekBar sbPrioridad;
    private Integer horasEstimadas,intPrioridad;
    private Integer idProyecto =-1;
    private Prioridad prioridad;
    private EditText etDescripcionTarea,etHorasEstimadas;
    private String descripcionTarea;
    private Button btnGuardar,btnCancelar;
    private Tarea nuevaTarea,tareaAEditar;
    private Usuario usuarioSeleccionado;
    private Proyecto proyectoSeleccionado;
    private Integer idTareaAEditar;
    private boolean edicion;
    private EjemploPermisos gestorPermisos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_tarea);
        if (android.os.Build.VERSION.SDK_INT > 9) // PERMITE ABRIR CONEXION A INTERNET
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        cargarComponentes();
         //listaUsuarios=proyectoDAO.listarUsuarios();
        listaUsuarios = ejemploContactos.listarContactos(); // Obtiene los contactos de la lista de contactos
        edicion = false;
        idProyecto = getIntent().getIntExtra("ID_PROYECTO",idProyecto);
        if( (getIntent().getIntExtra("RESULT_CODE",2)) == 1) // Significa que soy una activity de editar
        {
            idTareaAEditar = (getIntent().getIntExtra("ID_TAREA",1));
            /* Si hay problemas para encontrar la tarea a editar, vuelvo a la pantalla de listar tareas y aviso del problema */
            try {
                tareaAEditar = tareaDAO.getTarea(idTareaAEditar);
            }
            catch (TareaException e) {
                setResult(ListarTareasProyecto.RESULT_CANCELED);
                finish();
            }
            if(tareaAEditar !=null) {
                edicion=true;
                etDescripcionTarea.setText(tareaAEditar.getDescripcion());
                etHorasEstimadas.setText(Integer.toString( tareaAEditar.getHorasEstimadas() ));
                sbPrioridad.setProgress(Integer.valueOf(tareaAEditar.getPrioridad().getPrioridad()));
                swapListas();
            }
        }
        adapterListaUsuarios = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listaUsuarios);
        adapterListaUsuarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListaUsuarios.setAdapter(adapterListaUsuarios);

        sbPrioridad.setOnSeekBarChangeListener(this);
        sbPrioridad.setMax(3);
        intPrioridad = (sbPrioridad.getProgress() + 1);

        btnCancelar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);

        gestorPermisos = new EjemploPermisos();
        gestorPermisos.askForInternetPermission(this);
    }

    /**
     * // TODO Consultar y editar esto de como se ve la lista de edicion Hacerlo bien, esto esta hardcodeado asi nomas
     *
     * Pone al usuario dueño de la tarea al inicio de la fila
     */
    private void swapListas()
    {
        Usuario duenio = tareaAEditar.getResponsable();
        String usuarioDuenio = duenio.getNombre();
        if(usuarioDuenio.equals("lucas"))
        {
            listaUsuarios.remove(1);
            listaUsuarios.add(0,duenio);
           // listaUsuarios.set(0,duenio);
        }

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
        proyectoDAO = ProyectoDAO.getInstance();
        proyectoDAO.setContext(this);
        usuarioDAO = UsuarioDAO.getInstance();
        tareaDAO = TareaDAO.getInstance();
        ejemploContactos = new EjemploContactos(this);
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
        intPrioridad++; // Como la prioridad es de 1 a 4 y el seekbar de 0 a 3, lo aumento en 1
        this.intPrioridad = intPrioridad;
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
        try {
            horasEstimadas = Integer.parseInt(String.valueOf(etHorasEstimadas.getText()));
            descripcionTarea = String.valueOf(etDescripcionTarea.getText());
            usuarioSeleccionado = (Usuario) spinnerListaUsuarios.getSelectedItem();
            proyectoSeleccionado = proyectoDAO.getProyecto(idProyecto);
            prioridad = tareaDAO.getPrioridad(intPrioridad);
            if(descripcionTarea!=null && !descripcionTarea.isEmpty() && horasEstimadas!=null && usuarioSeleccionado!=null && prioridad!=null) // Si no hay datos vacio continuo
            {
                usuarioSeleccionado = usuarioDAO.guardarUsuario(usuarioSeleccionado); // SI EL USUARIO NO EXISTIA LO GUARDA
                nuevaTarea = new Tarea(false,horasEstimadas,0,false,proyectoSeleccionado,prioridad,usuarioSeleccionado,descripcionTarea);
                if(edicion) // Si estoy editando una tarea
                {
                    nuevaTarea.setId(idTareaAEditar);
                    tareaDAO.actualizarTarea(nuevaTarea);
                }
                else // Estoy dando de alta una tarea
                {
                    tareaDAO.nuevaTarea(nuevaTarea);
                }
                setResult(RESULT_OK);
            }
            else
            {
                Toast.makeText(this.getBaseContext(),"Por favor, complete todos los datos.",Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            setResult(RESULT_CANCELED);
        }
        finally{
            finish();
        }
    }
    private void accionBotonCancelar()
    {
        setResult(3);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(3);
        super.onBackPressed();
    }
}
