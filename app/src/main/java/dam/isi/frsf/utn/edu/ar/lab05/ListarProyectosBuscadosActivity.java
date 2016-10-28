package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

public class ListarProyectosBuscadosActivity extends AppCompatActivity {
    private ProyectoDAO proyectoDAO;
    private ArrayAdapter listaProyectosAdapter;
    private ListView lvProyectos;
    private ArrayList<Proyecto> listaProyectos;

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_proyectos);
        lvProyectos = (ListView) findViewById(R.id.lvProyecto);
        //TODO BORRAR ESTO
        Proyecto p = new Proyecto(1,"nacionalYPopular");
        proyectoDAO = new ProyectoDAO(this);
        listaProyectos = new ArrayList<Proyecto>();
        listaProyectos.add(p);
        listaProyectosAdapter = new ProyectosBuscadosAdapter(ListarProyectosBuscadosActivity.this, listaProyectos);
        lvProyectos.setAdapter(listaProyectosAdapter);


    }
}
