package com.grayhartcorp.quevengan;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Un {@link IntentService} que simula un proceso en primer plano
 * <p>
 */

public class Servicio_pedido extends IntentService {
    Suceso suceso;
    String sid_pedido;
    int estado=0;
    private static final String TAG = Servicio_pedido.class.getSimpleName();


    public Servicio_pedido() {
        super("Servicio_pedido");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.ACTION_RUN_ISERVICE.equals(action)) {
                handleActionRun();
            }
        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun() {
        try {

            SharedPreferences prefe=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);

            sid_pedido=prefe.getString("id_pedido","0");

            // Bucle de simulación de pedido cuando tiene estado del pedido=0
            for (int i = 1; estado==0 && sid_pedido.equals("0")==false; i++) {

                    Servicio_punto servicio_punto = new Servicio_punto();
                    servicio_punto.execute(getString(R.string.servidor) + "frmMoto.php?opcion=obtener_ubicacion_por_id_pedido", "1",sid_pedido);
                    // parametro que recibe el doinbackground

                Log.d(TAG, i + ""); // Logueo


                // Retardo de 1 segundo en la iteración
                Thread.sleep(1500);
            }

            // Bucle de simulación cuando tiene carreras... carrera es cuando tiene el estado=1
            for (int i = 1; estado==1 && sid_pedido.equals("0")==false; i++) {


                Servicio_punto servicio_punto = new Servicio_punto();
                servicio_punto.execute(getString(R.string.servidor) + "frmMoto.php?opcion=obtener_ubicacion_por_id_pedido_carrera", "2", sid_pedido);
                // parametro que recibe el doinbackground

                Log.d(TAG, i + ""); // Logueo

                // Retardo de 1 segundo en la iteración
                Thread.sleep(1500);
            }


            // Quitar de primer plano
            stopForeground(true);
            // si nuestro estado esta en 2 o mayor .. quiere decir que no nuestro pedido se finalizo o sino se cancelo... sin nninguna carrera...
            if(estado>1)
            {
                SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                SharedPreferences.Editor editor=pedido.edit();
                editor.putString("id_pedido","");
                editor.putString("estado","");
                editor.commit();
                stopService(new Intent(this,Servicio_pedido.class));

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
      //  Toast.makeText(this, "Servicio destruido...", Toast.LENGTH_SHORT).show();

        // Emisión para avisar que se terminó el servicio
        Intent localIntent = new Intent(Constants.ACTION_PROGRESS_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }


    // comenzar el servicio. para obtener el punto de la ultima ubicacion..
    public class Servicio_punto extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//obtener datos del pedido en curso.....
            if (params[1] == "1") { //mandar JSON metodo post para login
                try {
                    HttpURLConnection urlConn;

                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");
                    urlConn.connect();

                    //se crea el objeto JSON
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id_pedido", params[2]);

                    //Envio los prametro por metodo post
                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();

                    int respuesta = urlConn.getResponseCode();

                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {

                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }


                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            JSONArray dato = respuestaJSON.getJSONArray("punto");
                            if (dato.getString(0).toString().length() > 8) {
                                double latitud = Double.parseDouble(dato.getJSONObject(0).getString("latitud"));
                                double longitud = Double.parseDouble(dato.getJSONObject(0).getString("longitud"));
                                estado=Integer.parseInt(dato.getJSONObject(0).getString("estado"));
                                cargar_puntos_en_tabla(latitud, longitud);
                                devuelve = "1";
                            } else
                            {
                                devuelve = "2";
                            }


                        } else  {
                            devuelve = "2";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //obtener datos de la carrera en curso.....
            if (params[1] == "2") { //mandar JSON metodo post para login
                try {
                    HttpURLConnection urlConn;

                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");
                    urlConn.connect();

                    //se crea el objeto JSON
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id_pedido", params[2]);

                    //Envio los prametro por metodo post
                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();

                    int respuesta = urlConn.getResponseCode();

                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {

                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }


                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            JSONArray dato = respuestaJSON.getJSONArray("punto");
                            if (dato.getString(0).toString().length() > 8) {
                                double latitud = Double.parseDouble(dato.getJSONObject(0).getString("latitud"));
                                double longitud = Double.parseDouble(dato.getJSONObject(0).getString("longitud"));
                                String id_carrera = dato.getJSONObject(0).getString("id_carrera");
                                estado=Integer.parseInt(dato.getJSONObject(0).getString("estado"));
                                cargar_puntos_en_tabla_carrera(latitud, longitud,id_carrera);
                                devuelve = "3";
                            } else
                            {
                                devuelve = "4";
                            }


                        } else  {
                            devuelve = "4";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //obtener el estado del pedido......
            if (params[1] == "3") { //mandar JSON metodo post para login
                try {
                    HttpURLConnection urlConn;
                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");
                    urlConn.connect();
                    //se crea el objeto JSON
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id_pedido", params[2]);
                    //Envio los prametro por metodo post
                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();
                    int respuesta = urlConn.getResponseCode();
                    StringBuilder result = new StringBuilder();
                    if (respuesta == HttpURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));
                        if (suceso.getSuceso().equals("1")) {
                            estado=Integer.parseInt(respuestaJSON.getString("estado"));
                            devuelve="5";
                        }
                        else  {
                            devuelve = "6";
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("onPostExcute=", "" + s);
            if(s.equals("1"))
            {
                SharedPreferences prefe=getSharedPreferences("ultimo_pedido",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=prefe.edit();
                editor.putString("punto_optenido","true");
                editor.commit();

            }
            else
            if(s.equals("4"))
            {
                Servicio_punto servicio_estado = new Servicio_punto();
                servicio_estado.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_estado_pedido", "3",sid_pedido);
                // parametro que recibe el doinbackground
            }
            else
            {
                SharedPreferences prefe=getSharedPreferences("ultimo_pedido",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=prefe.edit();
                editor.putString("punto_optenido","false");
                editor.commit();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

    }


    private void cargar_puntos_en_tabla(double latitud,double longitud) {
try {
    SharedPreferences puntos=getSharedPreferences("puntos_pedido",MODE_PRIVATE);
    SharedPreferences.Editor editor=puntos.edit();
    editor.putInt("id_pedido",Integer.parseInt(sid_pedido));
    editor.putString("latitud",String.valueOf(latitud));
    editor.putString("longitud",String.valueOf(longitud));
    editor.putInt("estado",estado);
    editor.commit();
    SharedPreferences ultimo_pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
    SharedPreferences.Editor editor_pedido=ultimo_pedido.edit();
    editor.putInt("estado",estado);
    editor_pedido.commit();

}catch (Exception e)
{

}
    }


    private void cargar_puntos_en_tabla_carrera(double latitud,double longitud,String id_carrera) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("id_carrera", id_carrera);
        registro.put("latitud",String.valueOf(latitud));
        registro.put("longitud",String.valueOf(longitud));
        registro.put("id_pedido",sid_pedido);
        bd.insert("puntos_carrera", null, registro);
        bd.close();
    }
}

