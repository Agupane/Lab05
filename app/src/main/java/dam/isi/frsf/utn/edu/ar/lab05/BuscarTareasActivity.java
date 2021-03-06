package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

public class BuscarTareasActivity extends AppCompatActivity {
    private Button botonBuscar;
   // private EditText etMinDesviados;
    private Switch switchTareaTerminada;
    private Integer minutosDesviadosBuscados;
    private Boolean buscarTareasTerminadas;
    private NumberPicker etMinDesviados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_tareas);
        botonBuscar = (Button) findViewById(R.id.bBuscar);
        switchTareaTerminada = (Switch) findViewById(R.id.switchTareaTerminada);
        //etMinDesviados = (EditText) findViewById(R.id.etMinDesviados);
        etMinDesviados = (NumberPicker) findViewById(R.id.etMinDesviados);
        etMinDesviados.setMaxValue(999);
        etMinDesviados.setMinValue(1);



        buscarTareasTerminadas = false;
        switchTareaTerminada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buscarTareasTerminadas = isChecked;
            }
        });
        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String desvio = etMinDesviados.getText().toString();
               // minutosDesviadosBuscados = Integer.valueOf(desvio);
                minutosDesviadosBuscados = etMinDesviados.getValue();
                if(minutosDesviadosBuscados !=null) {
                    Intent intActAlta= new Intent(BuscarTareasActivity.this,ListarTareasBuscadasActivity.class);
                    intActAlta.putExtra("MinutosDesvioBuscados", minutosDesviadosBuscados);
                    intActAlta.putExtra("TareaTerminada",buscarTareasTerminadas);
                    startActivityForResult(intActAlta,1);

                }
            }
        });
    }

}
