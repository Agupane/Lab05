package dam.isi.frsf.utn.edu.ar.lab05;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

public class AltaProyectoActivity extends AppCompatActivity implements Button.OnClickListener{
    private EditText etNombreProyecto;
    private String nombreProyecto;
    private Button btnCrearProyecto;
    private Usuario usuarioSeleccionado;
    private Proyecto nuevoProyecto;
    private Integer idProyectoAEditar;
    private ProyectoDAO proyectoDAO;
    private boolean edicion; // Indica si estoy editando un proyecto o si estoy creandolo
    private static final int ID_PROYECTO_DEFAULT = -1; // SINO ENCONTRO UN ID DE PROYECTO ENTONCES ESTE ES EL VALOR DEFAULT
    private static final int ID_RESULT_CODE_DEFAULT =-1; // ID DEFAULT EN EL CASO DE QUE NO SE ENCUENTREN DATOS DEL INTENT
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_proyecto);
        cargarComponentes();
        edicion = false;
        nuevoProyecto = new Proyecto();

        if( (getIntent().getIntExtra("RESULT_CODE",ID_RESULT_CODE_DEFAULT)) == 1) // Significa que soy una activity de editar
        {
            btnCrearProyecto.setText("Editar proyecto"); // Por default el boton se llama "Crear proyecto"
            idProyectoAEditar = (getIntent().getIntExtra("ID_PROYECTO",ID_PROYECTO_DEFAULT));
            nuevoProyecto.setId(idProyectoAEditar);
            edicion=true;
            etNombreProyecto.setText(getIntent().getStringExtra("NOMBRE_PROYECTO"));
        }

        btnCrearProyecto.setOnClickListener(this);
    }

    /**
     * Inicializa todas las variables
     */
    private void cargarComponentes()
    {
        etNombreProyecto = (EditText) findViewById(R.id.etNombreProyecto);
        btnCrearProyecto = (Button) findViewById(R.id.btnCrearProyecto);
        proyectoDAO = ProyectoDAO.getInstance();
        proyectoDAO.setContext(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnCrearProyecto:
            {
                accionBotonCrearProyecto();
                break;
            }
            default:
            {
                break;
            }
        }
    }
    private void accionBotonCrearProyecto()
    {
        try {
            nombreProyecto = String.valueOf(etNombreProyecto.getText());
            if(!nombreProyecto.isEmpty()) // Si no hay datos vacio continuo
            {
                if(edicion) // Si estoy editando un proyecto
                {
                    nuevoProyecto.setNombre(nombreProyecto);
                    proyectoDAO.actualizarProyecto(nuevoProyecto);
                }
                else // Estoy dando de alta un proyecto
                {
                    nuevoProyecto.setNombre(nombreProyecto);
                    proyectoDAO.nuevoProyecto(nuevoProyecto);
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
            setResult(RESULT_CANCELED);
        }
        finish();
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
