package com.grayhartcorp.quevengan.menu_motista;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.Suceso;

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

public class Servicio_cargar_punto extends Service {

    PowerManager.WakeLock wakeLock;
    Suceso suceso;

    @Override
    public boolean stopService(Intent name) {

        return super.stopService(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    double latitud_a;
    double longitud_a;


    private LocationManager locationManager;
    boolean sw_distancia_cercana = false, sw_distancia_llego_la_moto = false;

    public Servicio_cargar_punto() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        // Toast.makeText(getApplicationContext(), "Service Created",
        // Toast.LENGTH_SHORT).show();

        Log.e("Google", "Service Created");

    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

        Log.e("Google", "Service Started");

        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);
        } catch (Exception e) {

        }
    }

    private LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

            Log.e("Google", "Location Changed");

            if (location == null)
                return;

            if (isConnectingToInternet(getApplicationContext())) {

                try {
                    Log.e("latitude", location.getLatitude() + "");
                    Log.e("longitude", location.getLongitude() + "");

// si esta en camino hace un pedido.. el id_carrera se lo va a colocar con un -1
                    SharedPreferences pedido = getSharedPreferences("pedido_en_curso", Context.MODE_PRIVATE);
                    SharedPreferences configuracion = getSharedPreferences("configuracion", Context.MODE_PRIVATE);

                    int estado = 0;
                    try {
                        estado = Integer.parseInt(pedido.getString("estado", "0"));
                    } catch (Exception e) {
                        estado = 0;
                    }
                    String id_pedido = pedido.getString("id_pedido", "");
                    // Bucle de simulación de pedido cuando tiene estado del pedido=0  o sino con un estado=1 cuando tiene carreras...

                    SharedPreferences perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
                    String id = perfil.getString("id_moto", "");
                    int estado_perfil=0;
                    try{
                        estado_perfil=Integer.parseInt(perfil.getString("estado","0"));
                    }catch (Exception e)
                    {
                     estado_perfil=0;
                    }


                    //verificamos el estado del motista estaactivo o inactivo.
                    if(estado_perfil==0)
                    {
                        locationManager.removeUpdates(listener);
                    }



                    SharedPreferences carrera = getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
                    String id_carrera = carrera.getString("id", "-1");
                    int numero = carrera.getInt("numero", 0);
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();
                    latitud_a = latitud;
                    longitud_a = longitud;

                    if(estado>=1)
                    {
                        sw_distancia_llego_la_moto = false;
                        sw_distancia_cercana = false;
                    }
                    if (id.equals("") == false && latitud != 0 && longitud != 0 && estado >= 0 && estado < 2) {
                        try {
                            //verificamos si avanzo 5 metros. para subir la nueva ubicacion....
                            SharedPreferences punto = getSharedPreferences("mi ubicacion", Context.MODE_PRIVATE);
                            double latitud_fin = Double.parseDouble(punto.getString("latitud", "0"));
                            double longitud_fin = Double.parseDouble(punto.getString("longitud", "0"));
                            int distancia = getDistancia(latitud, longitud, latitud_fin, longitud_fin);

                            if (distancia >= 5) {

                                if (estado == 0) {
                                    Servicio hilo_traking = new Servicio();
                                    hilo_traking.execute(getString(R.string.servidor) + "frmMoto.php?opcion=set_ubicacion_punto", "1", id, String.valueOf(latitud), String.valueOf(longitud), id_pedido, "0", "0");// parametro que recibe el doinbackground
                                    try {
                                        double latitud_pedido = Double.parseDouble(pedido.getString("latitud", "0"));
                                        double longitud_pedido = Double.parseDouble(pedido.getString("longitud", "0"));
                                        int distancia_notificacion = getDistancia(latitud, longitud, latitud_pedido, longitud_pedido);
                                        if (distancia_notificacion < configuracion.getInt("distancia_llego_la_moto", 50) && sw_distancia_llego_la_moto == false && pedido.getInt("notificacion_distancia_llego_la_moto", 0) == 0) {
                                            sw_distancia_llego_la_moto = true;
                                            notificacion_llego_la_moto();
                                        } else if (distancia_notificacion < configuracion.getInt("distancia_cercana", 500) && sw_distancia_cercana == false && sw_distancia_llego_la_moto == false && pedido.getInt("notificacion_distancia_cercana", 0) == 0) {
                                            sw_distancia_cercana = true;
                                            notificacion_estoy_cerca();
                                        }

                                    } catch (Exception e) {

                                    }
                                } else {
                                    Servicio hilo_traking = new Servicio();
                                    hilo_traking.execute(getString(R.string.servidor) + "frmMoto.php?opcion=set_ubicacion_punto", "1", id, String.valueOf(latitud), String.valueOf(longitud), id_pedido, id_carrera, String.valueOf(numero));// parametro que recibe el doinbackground

                                    SharedPreferences car = getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = car.edit();
                                    int num = carrera.getInt("numero", 0);
                                    num++;
                                    editor.putInt("numero", num);
                                    editor.commit();
                                }

                            }
                        } catch (Exception e) {
                            SharedPreferences punto = getSharedPreferences("mi ubicacion", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = punto.edit();
                            editor.putString("latitud", "0");
                            editor.putString("longitud", "0");
                            editor.commit();
                        }


                    }



                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        try {
            wakeLock.release();
        } catch (Exception e) {

        }


    }

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }


    // comenzar el servicio con el motista para el traking. de latitud y longitud....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
// set_ubicacion_punto  ----- cargar punto de ubicacion....
            if (params[1] == "1") {
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
                    jsonParam.put("id_moto", params[2]);
                    jsonParam.put("latitud", params[3]);
                    jsonParam.put("longitud", params[4]);
                    jsonParam.put("id_pedido", params[5]);
                    jsonParam.put("id_carrera", params[6]);
                    jsonParam.put("numero", params[7]);

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

                        suceso =new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {

                            devuelve="1";
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
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SharedPreferences punto=getSharedPreferences("mi ubicacion",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=punto.edit();
            editor.putString("latitud",String.valueOf(latitud_a));
            editor.putString("longitud",String.valueOf(longitud_a));
            editor.commit();
            Log.e("se guardo la ubicacion=", "" + s);
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

    //enviar notificacion al usuario cuando tiene un pedido.
    // 1: cuando este cerca.. eviara una notificacion diciendo que el pedido esta cerca....
    // 2: cuando ya llego el taxista al lugar donde se pedido la moto.
    public class Servicio_notificacion_pedido extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

//envia notificaicon el motista.. con un mensaje de que ya esta cerca....
            if (params[1] == "1") {
                devuelve = "";
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

                        suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                            devuelve = "1";
                        } else {
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


//envia notificaicon el motista.. cuando ya llego al lugar del pedido....
            if (params[1] == "2") {
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

                        suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));

                        if (suceso.getSuceso().equals("1")) {
                              devuelve = "3";
                        }
                        else
                        {
                            devuelve="4";
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


            Log.e("respuesta del servidor=", "" + s);
            if (s.equals("1")) {
                sw_distancia_cercana=true;
                SharedPreferences ped=getSharedPreferences("pedido_en_curso",MODE_PRIVATE);
                SharedPreferences.Editor editor=ped.edit();
                editor.putInt("notificacion_distancia_cercana",1);
                editor.commit();
            } else if (s.equals("2")) {
                sw_distancia_cercana=false;
            } else if (s.equals("3")) {
                sw_distancia_llego_la_moto=true;
                SharedPreferences ped=getSharedPreferences("pedido_en_curso",MODE_PRIVATE);
                SharedPreferences.Editor editor=ped.edit();
                editor.putInt("notificacion_distancia_llego_la_moto",1);
                editor.commit();
            } else if (s.equals("4")) {
                sw_distancia_llego_la_moto=false;
            }
            super.onPostExecute(s);
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

    public  int getDistancia(double lat_a,double lon_a, double lat_b, double lon_b){
        long  Radius = 6371000;
        double dLat = Math.toRadians(lat_b-lat_a);
        double dLon = Math.toRadians(lon_b-lon_a);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) * Math.sin(dLon /2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (int) (Radius * c);
    }

    public int get_distancia_cercana() {
        int distancia=0;
        SharedPreferences configuracion= getSharedPreferences("configuracion",MODE_PRIVATE);
        distancia=configuracion.getInt("distancia_cercaba",500);
        return distancia;
    }

    public int get_distancia_llego_la_moto() {
        int distancia=0;
        SharedPreferences configuracion= getSharedPreferences("configuracion",MODE_PRIVATE);
        distancia=configuracion.getInt("distancia_llego_la_moto",50);
        return distancia;
    }

    public void notificacion_estoy_cerca()
    {
        try {
            SharedPreferences pedido = getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
            Servicio_notificacion_pedido hilo_moto = new Servicio_notificacion_pedido();
            hilo_moto.execute(getString(R.string.servidor) + "frmPedido.php?opcion=estoy_cerca", "1", pedido.getString("id_pedido", ""));// parametro que recibe
        } catch (Exception e) {

        }
    }
    public void notificacion_llego_la_moto()
    {
        try {
            SharedPreferences pedido = getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
            Servicio_notificacion_pedido hilo_moto = new Servicio_notificacion_pedido();
            hilo_moto.execute(getString(R.string.servidor) + "frmPedido.php?opcion=notificacion_llego_la_moto", "2", pedido.getString("id_pedido", ""));// parametro que recibe
        } catch (Exception e) {

        }
    }
}