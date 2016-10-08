package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

public class ListarTareasBuscadasActivity extends AppCompatActivity {
    private ProyectoDAO proyectoDAO;
    private ArrayAdapter listaTareasBuscadasAdapter;
    private ListView lvTareasBuscadas;
    private ArrayList<Tarea> listaTareasBuscadas;
    private Integer minutosDesviadosBuscados;
    private Boolean buscarTareasTerminadas;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_tareas_buscadas);
        proyectoDAO = new ProyectoDAO(this);
        lvTareasBuscadas = (ListView) findViewById(R.id.lvTareasResultado);
        intent = getIntent();
        minutosDesviadosBuscados = intent.getIntExtra("MinutosDesvioBuscados",1);
        buscarTareasTerminadas = intent.getBooleanExtra("TareaTerminada",false);

        listaTareasBuscadas = (ArrayList) proyectoDAO.listarDesviosPlanificacion(buscarTareasTerminadas, minutosDesviadosBuscados);
        listaTareasBuscadasAdapter = new TareasBuscadasAdapter(ListarTareasBuscadasActivity.this, listaTareasBuscadas);
        lvTareasBuscadas.setAdapter(listaTareasBuscadasAdapter);
    }
}
