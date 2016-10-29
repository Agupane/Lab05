package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

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
        lvProyectos = (ListView) findViewById(R.id.lvProyecto);
        lvProyectos.setClickable(true);
        //TODO BORRAR ESTO
        /*
        Proyecto p = new Proyecto(1,"nacionalYPopular");
        proyectoDAO = new ProyectoDAO(this);
        listaProyectos = new ArrayList<Proyecto>();
        listaProyectos.add(p);
        listaProyectosAdapter = new ProyectosBuscadosAdapter(ListarProyectosActivity.this, listaProyectos);
        lvProyectos.setAdapter(listaProyectosAdapter);
        */

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
      //  Log.d("LAB05-MAIN","en resume");
        proyectoDAO = new ProyectoDAO(ListarProyectosActivity.this);
        proyectoDAO.open();
        cursor = proyectoDAO.getCursorProyectos();

        Log.d("LAB05-MAIN","Cant proyectos: "+cursor.getCount());

        pca = new ProyectoCursorAdapter(ListarProyectosActivity.this,cursor,proyectoDAO);

        lvProyectos.setAdapter(pca);
   //     Log.d("LAB05-MAIN","fin resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
       // Log.d("LAB05-MAIN","on pausa");

        if(cursor!=null) cursor.close();
        if(proyectoDAO!=null) proyectoDAO.close();
     //   Log.d("LAB05-MAIN","fin on pausa");

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
