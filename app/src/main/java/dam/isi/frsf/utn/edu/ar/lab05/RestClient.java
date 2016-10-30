package dam.isi.frsf.utn.edu.ar.lab05;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import dam.isi.frsf.utn.edu.ar.lab05.Exception.RestException;

/**
 * Created by Agustin on 10/20/2016.
 */

public class RestClient {

    private final String IP_SERVER = "10.0.2.2";
    private final String PORT_SERVER = "4000";
    private final String TAG_LOG = "LAB06";

    /**
     * Devuelve el JSON existente en la url destino que tenga la id parametro y se encuentre
     * En el path parametro
     * @param id id del objeto a buscar
     * @param path path donde se encuentra el objeto a buscar
     * @return
     */
    public JSONObject getById(Integer id,String path) throws RestException {
        JSONObject resultado = null;
        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL("http://"+IP_SERVER+":"+PORT_SERVER+"/"+path+"/"+id);
            Log.d("TAG_LOG",url.getPath()+ " --> "+url.toString());
            urlConnection= (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            InputStreamReader isw = new InputStreamReader(in);
            StringBuilder sb = new StringBuilder();

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                sb.append(current);
                data = isw.read();
            }
            Log.d("TAG_LOG",url.getPath()+ " --> "+sb.toString());
            resultado = new JSONObject(sb.toString());
        }
        catch (IOException e) {
            Log.e("TEST-ARR",e.getMessage(),e);
            throw new RestException(e.getMessage());
            //e.printStackTrace();
        }
        catch (JSONException e) {
            throw new RestException(e.getMessage());
            //e.printStackTrace();
        }
        finally {
            if(urlConnection!=null) urlConnection.disconnect();
        }
        return resultado;
    }

    /**
     * Devuelve un array de JSON con todos los objetos existentes en el path parametro
     * @param path path donde se encuentran los objetos a buscar
     * @return
     */
    public JSONArray getByAll(String path) throws RestException {
        JSONArray resultado = null;
        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL("http://"+IP_SERVER+":"+PORT_SERVER+"/"+path);
            Log.d("TAG_LOG",url.getPath()+ " --> "+url.toString());
            urlConnection= (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            InputStreamReader isw = new InputStreamReader(in);
            StringBuilder sb = new StringBuilder();

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                sb.append(current);
                data = isw.read();
            }
            Log.d("TAG_LOG",url.getPath()+ " --> "+sb.toString());
            resultado = new JSONArray(sb.toString());
        }
        catch (IOException e) {
            Log.e("TEST-ARR",e.getMessage(),e);
            throw new RestException(e.getMessage());
           // e.printStackTrace();
        }
        catch (JSONException e) {
            throw new RestException(e.getMessage());
           // e.printStackTrace();
        }
        finally {
            if(urlConnection!=null) urlConnection.disconnect();
        }
        return resultado;
    }

    /**
     * Crea un objeto pasado por parametro en la url indicada
     * @param objeto objeto json a crear
     * @param path path donde se encuentra el objeto
     */
    public void crear(JSONObject objeto,String path) throws RestException {
        try{
            String str= objeto.toString();
            byte[] data=str.getBytes("UTF-8");
            Log.d("Creando objeto","datos---> "+str);
            crearHttpConnectionParaCrearOActualizar(data,"POST",path);
        }
        catch(MalformedURLException e){
           // e.printStackTrace();
            throw new RestException(e.getMessage());
        }
        catch(IOException e){
          //  e.printStackTrace();
            throw new RestException(e.getMessage());
        }
    }
    /**
     * Actualiza el objeto pasado por parametro en la url indicada
     * @param objeto
     * @param path
     */
    public void actualizar(JSONObject objeto, String path) throws RestException {
        try {
            String str = objeto.toString();
            byte[] data = str.getBytes("UTF-8");
            Log.d("Actualizando", "datos a actualizar ---> " + str);
            crearHttpConnectionParaCrearOActualizar(data, "PUT", path);
        }
        catch(MalformedURLException e){
          //  e.printStackTrace();
            throw new RestException(e.getMessage());
        }
        catch(IOException e){
         //   e.printStackTrace();
            throw new RestException(e.getMessage());
        }
    }

    /**
     * Borra el objeto de id pasada en el path indicado
     * @param id id del objeto a borrar
     * @param path path en donde se encuentra el objeto
     */
    public void borrar(Integer id,String path) throws RestException {
        try {
            path=path+"/"+id;
            Log.d("Borrando objeto", "id: "+id+" path:"+path);
            crearHttpConnectionParaBusquedaOEliminacion("DELETE", path);
        }
        catch(MalformedURLException e){
       //     e.printStackTrace();
            throw new RestException(e.getMessage());
        }
        catch(IOException e){
        //    e.printStackTrace();
            throw new RestException(e.getMessage());
        }
    }

    /**
     * Metodo que permite generalizar las solicitudes POST y PUT
     * Si el objeto existe lo actualiza, de lo contrario lo crea
     * Recibe los datos en bytes y un tipo de solicitud y lo envia al destino path
     * si el tipo de request no es POST PUT O DELETE, ignora lanza una
     * @param datosAEnviar bytes de datos que se deseen enviar MalformedURLException
     * @param tipoDeRequest puede ser POST PUT O DELETE
     * @param path url a donde enviar los datos
     */
    private void crearHttpConnectionParaCrearOActualizar(byte[] datosAEnviar, String tipoDeRequest, String path) throws MalformedURLException {
        tipoDeRequest=tipoDeRequest.toUpperCase();
        if(tipoDeRequest.equals("POST") || tipoDeRequest.equals("PUT")){
            HttpURLConnection urlConnection=null;
            try{
                URL url = new URL("http://"+IP_SERVER+":"+PORT_SERVER+"/"+path);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod(tipoDeRequest);
                urlConnection.setFixedLengthStreamingMode(datosAEnviar.length);
                urlConnection.setRequestProperty("Content-Type","application/json");

                DataOutputStream flujoSalida = new DataOutputStream(urlConnection.getOutputStream());
                flujoSalida.write(datosAEnviar);
                flujoSalida.flush();
                flujoSalida.close();
                Log.d("HTTP-Connection","Respuesta a solicitud "+tipoDeRequest+": "+urlConnection.getResponseMessage());
            }
            catch(IOException e){
                e.printStackTrace();
            }
            finally{
                if(urlConnection!=null){ urlConnection.disconnect();}
            }
        }
        else{
            throw new MalformedURLException("El tipo de request solicitado no es POST o PUT");
        }
    }

    private void crearHttpConnectionParaBusquedaOEliminacion(String tipoDeRequest, String path) throws MalformedURLException {
        tipoDeRequest=tipoDeRequest.toUpperCase();
        if(tipoDeRequest.equals("GET") || tipoDeRequest.equals("DELETE")){
            HttpURLConnection urlConnection=null;
            try{
                URL url = new URL("http://"+IP_SERVER+":"+PORT_SERVER+"/"+path);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod(tipoDeRequest);
                urlConnection.setRequestProperty("Content-Type","application/json");

                /*
                DataOutputStream flujoSalida = new DataOutputStream(urlConnection.getOutputStream());
                flujoSalida.write(datosAEnviar);
                flujoSalida.flush();
                flujoSalida.close();
                */
                Log.d("HTTP-Connection","Respuesta a solicitud "+tipoDeRequest+": "+urlConnection.getResponseMessage());
            }
            catch(IOException e){

            }
            finally{
                if(urlConnection!=null){ urlConnection.disconnect();}
            }
        }
        else{
            throw new MalformedURLException("El tipo de request solicitado no es GET O DELETE");
        }
    }
}