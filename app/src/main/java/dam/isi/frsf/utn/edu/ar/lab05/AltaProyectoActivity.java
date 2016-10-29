package dam.isi.frsf.utn.edu.ar.lab05;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

public class AltaProyectoActivity extends AppCompatActivity implements Button.OnClickListener{
    private EditText etNombreProyecto;
    private String nombreProyecto;
    private Button btnCrearProyecto;
    private Usuario usuarioSeleccionado;
    private Proyecto proyectoAEditar;
    private Integer idProyectoAEditar;
    private ProyectoDAO proyectoDAO;
    private boolean edicion; // Indica si estoy editando un proyecto o si estoy creandolo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_proyecto);
        cargarComponentes();

        edicion = false;


        if( (getIntent().getIntExtra("RESULT_CODE",2)) == 1) // Significa que soy una activity de editar
        {
            btnCrearProyecto.setText("Editar proyecto");
            idProyectoAEditar = (getIntent().getIntExtra("ID_PROYECTO",1));
            proyectoAEditar = proyectoDAO.getProyecto(idProyectoAEditar);
            if(proyectoAEditar !=null) {
                edicion=true;
                etNombreProyecto.setText(proyectoAEditar.getNombre());
            }
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
        proyectoDAO = new ProyectoDAO(this);
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

        }
        catch(Exception e)
        {
            setResult(RESULT_CANCELED);
        }
         if(!nombreProyecto.isEmpty()) // Si no hay datos vacio continuo
          {
              Proyecto nuevoProyecto;
              nuevoProyecto = new Proyecto(nombreProyecto);
              if(edicion) // Si estoy editando un proyecto
              {
                  nuevoProyecto.setId(idProyectoAEditar);
                  proyectoDAO.actualizarProyecto(nuevoProyecto);
              }
              else // Estoy dando de alta un proyecto
              {
                  proyectoDAO.nuevoProyecto(nuevoProyecto);
              }
                setResult(RESULT_OK);
          }
          else
          {
              Toast.makeText(this.getBaseContext(),"Por favor, complete todos los datos.",Toast.LENGTH_LONG).show();
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
