package dam.isi.frsf.utn.edu.ar.lab05;

import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

public class AltaTareaActivity extends AppCompatActivity{
    private Spinner spinnerListaUsuarios;
    private ArrayAdapter adapterListaUsuarios;
    private List<Usuario> listaUsuarios;
    private ProyectoDAO proyectoDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_tarea);
        listaUsuarios = new ArrayList();
        proyectoDAO = new ProyectoDAO(this);

        listaUsuarios=proyectoDAO.listarUsuarios();

        spinnerListaUsuarios = (Spinner) findViewById(R.id.spinnerUsuarios);
        adapterListaUsuarios = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,listaUsuarios);
        adapterListaUsuarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListaUsuarios.setAdapter(adapterListaUsuarios);

    }

}
