package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.TareaDAO;

public class ListarTareasProyectoActivity extends AppCompatActivity {

    private ListView lvTareas;
    private ProyectoDAO proyectoDAO;
    private TareaDAO tareaDAO;
    private Cursor cursor;
    private TareaCursorAdapter tca;
    private EjemploPermisos ejemploPermisos;
    private int idProyecto = -1;
    public static final int RESULT_BACK = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_tareas_proyecto);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
     //   setSupportActionBar(toolbar);
        idProyecto = getIntent().getIntExtra("ID_PROYECTO",idProyecto);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejemploPermisos = new EjemploPermisos();
                ejemploPermisos.askForContactPermission(ListarTareasProyectoActivity.this);
                Intent intActAlta= new Intent(ListarTareasProyectoActivity.this,AltaTareaActivity.class);
                intActAlta.putExtra("ID_TAREA", 0);
                intActAlta.putExtra("ID_PROYECTO",idProyecto);
                startActivityForResult(intActAlta,0);
            }
        });
        lvTareas = (ListView) findViewById(R.id.listaTareas);
        lvTareas.setClickable(true);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        switch(requestCode)
        {

            case 0: // Alta tarea
            {
                switch(resultCode)
                {
                    case RESULT_OK:
                    {
                        this.tca.changeCursor();
                        Toast.makeText(getApplicationContext(),"La operacion de alta se realizo exitosamente",Toast.LENGTH_LONG).show();
                        break;
                    }
                    case RESULT_CANCELED:
                    {
                        Toast.makeText(getApplicationContext(),"La operacion de alta no se pudo llevar a cabo, intente mas tarde",Toast.LENGTH_LONG).show();
                        break;
                    }
                    case RESULT_BACK:
                    {
                        break;
                    }
                    default: {break;}
                }
            }
            case 1: // Editar actividad
            {
                switch(resultCode)
                {
                    case RESULT_OK:
                    {
                        this.tca.changeCursor();

                        Toast.makeText(getApplicationContext(),"La operacion de edicion se realizo exitosamente",Toast.LENGTH_LONG).show();
                        break;
                    }
                    case RESULT_CANCELED:
                    {
                        Toast.makeText(getApplicationContext(),"La operacion de edicion no se pudo llevar a cabo, intente mas tarde",Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {break;}
                }
            }
            default: {break;}
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        tareaDAO = TareaDAO.getInstance();
        tareaDAO.open();
        cursor = tareaDAO.listaTareas(idProyecto);
        tca = new TareaCursorAdapter(ListarTareasProyectoActivity.this,cursor,tareaDAO);
        lvTareas.setAdapter(tca);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cursor!=null) cursor.close();
        if(proyectoDAO!=null) proyectoDAO.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.action_settings: {
                Toast.makeText(getApplicationContext(),"Esta opcion no esta disponible ",Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.action_buscar_tareas:{
                Intent intActBuscar= new Intent(ListarTareasProyectoActivity.this,BuscarTareasActivity.class);
                startActivity(intActBuscar);
                break;
            }
            case R.id.consultar_proyectos:{
                Intent intActConsultarProyectos = new Intent(ListarTareasProyectoActivity.this,ListarProyectosActivity.class);
                startActivity(intActConsultarProyectos);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public int getIdProyecto(){
        return this.idProyecto;
    }

}
