package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;

public class MainActivity extends AppCompatActivity {

    private ListView lvTareas;
    private ProyectoDAO proyectoDAO;
    private Cursor cursor;
    private TareaCursorAdapter tca;
    private EjemploPermisos ejemploPermisos;
    public static final int RESULT_BACK = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejemploPermisos = new EjemploPermisos();
                ejemploPermisos.askForContactPermission();
                Intent intActAlta= new Intent(MainActivity.this,AltaTareaActivity.class);
                intActAlta.putExtra("ID_TAREA", 0);
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
        Log.d("LAB05-MAIN","en resume");
        proyectoDAO = new ProyectoDAO(MainActivity.this);
        proyectoDAO.open();
        cursor = proyectoDAO.listaTareas(1);
        Log.d("LAB05-MAIN","mediol "+cursor.getCount());

        tca = new TareaCursorAdapter(MainActivity.this,cursor,proyectoDAO);
        lvTareas.setAdapter(tca);
        Log.d("LAB05-MAIN","fin resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LAB05-MAIN","on pausa");

        if(cursor!=null) cursor.close();
        if(proyectoDAO!=null) proyectoDAO.close();
        Log.d("LAB05-MAIN","fin on pausa");

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
            case R.id.action_settings: return true;

            case R.id.action_buscar_tareas:{
                Intent intActBuscar= new Intent(MainActivity.this,BuscarTareasActivity.class);
                startActivity(intActBuscar);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
