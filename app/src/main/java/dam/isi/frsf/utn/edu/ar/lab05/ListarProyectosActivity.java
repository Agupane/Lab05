package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import dam.isi.frsf.utn.edu.ar.lab05.Exception.ProyectoException;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;

public class ListarProyectosActivity extends AppCompatActivity {

    private ArrayAdapter listaProyectosAdapter;
    private ListView lvProyectos;
    private ArrayList<Proyecto> listaProyectos;
    private ProyectoDAO proyectoDAO;
    private Cursor cursor;
    private ProyectoCursorAdapter pca;
    private Intent intent;
    public static final int RESULT_BACK = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_proyectos);
          Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
          setSupportActionBar(toolbar);
        if (android.os.Build.VERSION.SDK_INT > 9) // PERMITE ABRIR CONEXION A INTERNET
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        lvProyectos = (ListView) findViewById(R.id.lvProyecto);
        lvProyectos.setClickable(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listar_proyectos, menu);
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
          //  case R.id.action_settings: return true;
            case R.id.action_nuevo_proyecto:{
                Intent intAltaProyecto= new Intent(ListarProyectosActivity.this,AltaProyectoActivity.class);
                startActivityForResult(intAltaProyecto,0);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        proyectoDAO = ProyectoDAO.getInstance();
        proyectoDAO.setContext(ListarProyectosActivity.this);
        proyectoDAO.open();
        try {
            cursor = proyectoDAO.getCursorProyectos();
        }
        catch(ProyectoException e){
            cursor = null;
        }
        pca = new ProyectoCursorAdapter(ListarProyectosActivity.this,cursor,proyectoDAO);
        lvProyectos.setAdapter(pca);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cursor!=null) cursor.close();
        if(proyectoDAO!=null) proyectoDAO.close();

    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        switch(requestCode)
        {
            case 0: // Alta proyecto
            {
                switch(resultCode)
                {
                    case RESULT_OK:
                    {
                        pca.changeCursor();
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
            case 1: // Editar proyecto
            {
                switch(resultCode)
                {
                    case RESULT_OK:
                    {
                        pca.changeCursor();
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

}
