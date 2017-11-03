package com.grayhartcorp.quevengan.menu_motista;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grayhartcorp.quevengan.Menu_p;
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.Suceso;
import com.grayhartcorp.quevengan.tarifa.CTarifa;
import com.grayhartcorp.quevengan.tarifa.Tarifa;

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
import java.text.DecimalFormat;

public class Carrera  extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {


    private GoogleMap mMap;
    TextView ubicacion,nombre_usuario,mensaje;
    Button iniciar_carrera,terminar_carrera,finalizar_todo;
    ImageButton brin;
    ProgressDialog pDialog;
    Suceso suceso;
    Servicio hilo_carrera;
    int id_carrera;
    public LocationManager locationManager;
    public LocationListener locationListener;
    int sw_acercar_a_mi_ubicacion;
    AlertDialog.Builder dialogo1 ;
    double latitud,longitud;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrera_mapa);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ubicacion=(TextView)findViewById(R.id.ubicacion);
        nombre_usuario=(TextView)findViewById(R.id.nombre_usuario);
        mensaje=(TextView)findViewById(R.id.mensaje);
        brin=(ImageButton)findViewById(R.id.brin);
        iniciar_carrera=(Button)findViewById(R.id.iniciar_carrera);
        terminar_carrera=(Button)findViewById(R.id.terminar_carrera);
        finalizar_todo=(Button)findViewById(R.id.finalizar_todo);

        sw_acercar_a_mi_ubicacion=0;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //fim

terminar_carrera.setOnClickListener(this);
        finalizar_todo.setOnClickListener(this);

    try {
        Bundle bundle = getIntent().getExtras();
        id_carrera = Integer.parseInt(bundle.getString("id_pedido"));
        hilo_carrera=new Servicio();
        hilo_carrera.execute(getString(R.string.servidor)+"frmCarrera.php?opcion=get_carrera_en_curso", "1",String.valueOf(id_carrera));// parametro que recibe el doinbackground

    }catch (Exception e)
    {
        finish();
    }






        // localizacion automatica
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {



                latitud=location.getLatitude();
                longitud=location.getLongitude();
                ubicacion.setText("p(" +latitud + "," + longitud + ")");
                if(sw_acercar_a_mi_ubicacion==0) {
                    //mover la camara del mapa a mi ubicacion.,,
                    try {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                        //agregaranimacion al mover la camara...
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to Mountain View
                                .zoom(15)                   // Sets the zoom
                                .bearing(0)                // Sets the orientation of the camera to east
                                .tilt(20)                   // Sets the tilt of the camera to 30 degrees
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    } catch (Exception e) {

                    }
                    sw_acercar_a_mi_ubicacion=1;
                }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Toast.makeText(getApplicationContext(),"StatusChanged"+s,Toast.LENGTH_LONG).show();
              //  verificar_login();
            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(getApplicationContext(),"Provider enable"+s,Toast.LENGTH_LONG).show();
             //   verificar_login();
            }

            @Override
            public void onProviderDisabled(String s) {

                dialogo1.setTitle("Importante");
                dialogo1.setMessage("Es necesario activar su GPS.");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Toast.makeText(getApplicationContext(),"Provider disable",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);


                    }
                });

                dialogo1.show();

            }









        };

        //necesario para la iteraccion  necesaria para obtener la ubicacion,...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
            return;
        } else {
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }

        }


    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id)
        {
            case R.id.brin:

                break;
            case R.id.iniciar_carrera:
                final CharSequence[] options={"Continuar con una nueva Carrera","Cancelar"};
                final AlertDialog.Builder builder= new AlertDialog.Builder(this);
                builder.setTitle("Elige una opción");
                builder.setItems(options, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(options[i]=="Continuar con una nueva Carrera")
                        {
                            Servicio hilo_continuar=new Servicio();
                            String detalle="Inicio del pedido.";
                            SharedPreferences preferencias = getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
                            String id_pedido = preferencias.getString("id_pedido", "");
                            String id_usuario = preferencias.getString("id_usuario", "");
                            String id_moto = preferencias.getString("id_moto", "");
                            hilo_continuar.execute(getString(R.string.servidor)+"frmCarrera.php?opcion=continuar_con_nueva_carrera", "3",String.valueOf(latitud),String.valueOf(longitud),detalle,id_usuario,id_pedido,id_moto);// parametro que recibe el doinbackground


                        }else if (options[i]=="Cancelar")
                        {
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();
                break;
            case R.id.terminar_carrera:
                SharedPreferences preferencias=getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
                double lat_ini=Double.parseDouble(preferencias.getString("latitud_inicio","0"));
                double lon_ini=Double.parseDouble(preferencias.getString("longitud_inicio","0"));
               //obtenemos la distancia en linea recta entro 2 puntos...
                int distancia=getDistancia(lat_ini,lon_ini,latitud,longitud);


               String dist=convertir_numero(String.valueOf(distancia));
                //verificamos si tiene o no una lista de tarifario.....
                //hacemos los calculos.. en base al monto establecido en la tarifa...
                SharedPreferences tarifa=getSharedPreferences("tarifa",MODE_PRIVATE);
                int monto_establecido=Integer.parseInt(tarifa.getString("monto","0"));
                int distancia_establecido=Integer.parseInt(tarifa.getString("distancia","0"));
                distancia=distancia/distancia_establecido;
               final int monto=distancia*monto_establecido;


                if(tarifa.getString("id","0").equals("0")==false) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("El monto a pagar por la distancia de " + dist+ " es de " +monto + " (bs).");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {

                            SharedPreferences preferencias = getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
                            String id_pedido = preferencias.getString("id_pedido", "");
                            String id_usuario = preferencias.getString("id_usuario", "");
                            String id_moto = preferencias.getString("id_moto", "");
                            String id_carrera = preferencias.getString("id", "");
                            String opcion = "1";
                            double lat_ini = Double.parseDouble(preferencias.getString("latitud_inicio", "0"));
                            double lon_ini = Double.parseDouble(preferencias.getString("longitud_inicio", "0"));
                            int distancia = getDistancia(lat_ini, lon_ini, latitud,longitud);
                            String detalle = "MI direccion";


                            SharedPreferences pref = getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("detalle_fin", detalle);
                            editor.putString("latitud_fin", String.valueOf(latitud));
                            editor.putString("longitud_fin", String.valueOf(longitud));
                            editor.putString("distancia", String.valueOf(distancia));
                            editor.putString("opciones", opcion);
                            editor.commit();
                            SharedPreferences tarifa=getSharedPreferences("tarifa",MODE_PRIVATE);
                            aceptar(String.valueOf(latitud), String.valueOf(longitud), detalle, distancia, opcion, id_pedido, id_usuario, id_moto, id_carrera,tarifa.getString("id","0"), String.valueOf(monto));
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            finish();
                        }
                    });
                    dialogo1.show();
                    }
                                else
                {
                    //si no tiene una lista de tarifario nos vamos a la actividad de Tarifa
                    Toast toast =Toast.makeText(this,"No tiene cargada la tarifa",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                   onBackPressed();
                }


                break;
            case R.id.finalizar_todo:

                break;
        }
    }

   public  void aceptar(String latitud, String longitud, String detalle, int distancia, String opcion, String id_pedido, String id_usuario, String id_moto, String id_carrera, String id_tarifa, String monto) {
       // enviamos los parametros para cerrar una carrera.....
        hilo_carrera=new Servicio();
        hilo_carrera.execute(getString(R.string.servidor)+"frmCarrera.php?opcion=terminar_carrera", "2",latitud,longitud,detalle,String.valueOf(distancia),opcion,id_pedido,id_usuario,id_moto,id_carrera,String.valueOf(id_tarifa),String.valueOf(monto));// parametro que recibe el doinbackground
    }


    // comenzar el servicio con el motista....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

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
                    jsonParam.put("id_pedido",params[2]);

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
                        // vacia los datos que estan registrados en nuestra base de datos SQLite..

                        if (suceso.getSuceso().equals("1")) {
                            JSONArray carrera=respuestaJSON.getJSONArray("carrera");
                            JSONArray inicio=respuestaJSON.getJSONArray("inicio");
                            JSONArray fin=respuestaJSON.getJSONArray("fin");

                            String id_carrera=carrera.getJSONObject(0).getString("id");
                            String detalle_inicio=inicio.getJSONObject(0).getString("detalle");
                            String latitud_inicio=inicio.getJSONObject(0).getString("latitud");
                            String longitud_inicio=inicio.getJSONObject(0).getString("longitud");
                            String detalle_fin="";
                            String latitud_fin="0";
                            String longitud_fin="0";

                            if(fin.toString().length()>5)
                            {
                                detalle_fin=fin.getJSONObject(0).getString("detalle");
                                latitud_fin=fin.getJSONObject(0).getString("latitud");
                                longitud_fin=fin.getJSONObject(0).getString("longitud");
                            }

                            String distancia=carrera.getJSONObject(0).getString("distancia");
                            String opciones=carrera.getJSONObject(0).getString("opciones");
                            String fecha_inicio=carrera.getJSONObject(0).get("fecha1").toString();
                            String fecha_fin=carrera.getJSONObject(0).get("fecha2").toString();
                            String id_pedido=carrera.getJSONObject(0).getString("id_pedido");
                            String id_usuario=carrera.getJSONObject(0).getString("id_usuario");
                            String id_moto=carrera.getJSONObject(0).getString("id_moto");
                            String id_tarifa=carrera.getJSONObject(0).getString("id_tarifa");
                            String monto=carrera.getJSONObject(0).getString("monto");


                            cargar_en_share_las_carreras( id_carrera, detalle_inicio, latitud_inicio, longitud_inicio, detalle_fin, latitud_fin, longitud_fin, distancia, opciones,fecha_inicio,fecha_fin, id_pedido, id_usuario, id_moto, id_tarifa, monto);

                            devuelve="1";
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
                    jsonParam.put("latitud",params[2]);
                    jsonParam.put("longitud",params[3]);
                    jsonParam.put("detalle",params[4]);
                    jsonParam.put("distancia",params[5]);
                    jsonParam.put("opciones",params[6]);
                    jsonParam.put("id_pedido",params[7]);
                    jsonParam.put("id_usuario",params[8]);
                    jsonParam.put("id_moto",params[9]);
                    jsonParam.put("id_carrera",params[10]);
                    jsonParam.put("id_tarifa",params[11]);
                    jsonParam.put("monto",params[12]);

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
                        // vacia los datos que estan registrados en nuestra base de datos SQLite..

                        if (suceso.getSuceso().equals("1")) {


                            devuelve="3";
                        } else {
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

            if (params[1] == "3") {
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
                    jsonParam.put("latitud", params[2]);
                    jsonParam.put("longitud", params[3]);
                    jsonParam.put("detalle", params[4]);
                    jsonParam.put("id_usuario", params[5]);
                    jsonParam.put("id_pedido", params[6]);
                    jsonParam.put("id_moto", params[7]);


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
                        // vacia los datos que estan registrados en nuestra base de datos SQLite..


                        if (suceso.getSuceso().equals("1")) {

                            SharedPreferences guardar_id_carrera=getSharedPreferences("id_carrera",MODE_PRIVATE);
                            SharedPreferences.Editor editar=guardar_id_carrera.edit();
                            editar.putString("id",respuestaJSON.getString("id"));
                            editar.commit();

                            devuelve = "5";
                        } else {
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
            pDialog = new ProgressDialog(Carrera.this);
            pDialog.setMessage("Autenticando ....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                SharedPreferences carrera=getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
                cargar_carrera_en_la_lista();
            }
            else if(s.equals("2"))
            {
                mensaje(suceso.getMensaje());
            }
            else if(s.equals("3"))
            {
               mensaje(suceso.getMensaje());
            }
            else if(s.equals("5")||s.equals("6"))
            {
                mensaje(suceso.getMensaje());
            }
            else
            {
                mensaje_error("Error al conectar con el servidor..");
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
    public  int getDistancia(double lat_a,double lon_a, double lat_b, double lon_b){
        long  Radius = 6371000;
        double dLat = Math.toRadians(lat_b-lat_a);
        double dLon = Math.toRadians(lon_b-lon_a);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) * Math.sin(dLon /2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (int) (Radius * c);
    }
    public void cargar_en_share_las_carreras(String id_carrera,String detalle_inicio,String latitud_inicio,String longitud_inicio,String detalle_fin,String latitud_fin,String longitud_fin,String distancia,String opciones,String fecha_inicio,String fecha_fin,String id_pedido,String id_usuario,String id_moto,String id_tarifa,String monto)
    {
        SharedPreferences preferencias=getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("id",id_carrera);
        editor.putString("detale_inicio",detalle_inicio);
        editor.putString("latitud_inicio",latitud_inicio);
        editor.putString("longitud_inicio",longitud_inicio);
        editor.putString("detalle_fin",detalle_fin);
        editor.putString("latitud_fin",latitud_fin);
        editor.putString("longitud_fin",longitud_fin);
        editor.putString("distancia",distancia);
        editor.putString("opciones",opciones);
        editor.putString("fecha_inicio",fecha_inicio);
        editor.putString("fecha_fin",fecha_fin);
        editor.putString("id_pedido",id_pedido);
        editor.putString("id_usuario",id_usuario);
        editor.putString("id_moto",id_moto);
        editor.putString("id_tarifa",id_tarifa);
        editor.putString("monto",monto);
        editor.commit();
    }
    public void cargar_carrera_en_la_lista()
    {

        mMap.clear();

        SharedPreferences carrera=getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
        carrera.getString("id","0");
        carrera.getString("detale_inicio","");
        carrera.getString("latitud_inicio","0");
        carrera.getString("longitud_inicio","0");
        carrera.getString("detalle_fin","");
        carrera.getString("latitud_fin","0");
        carrera.getString("longitud_fin","0");
        carrera.getString("distancia","0");
        carrera.getString("opciones","");
        carrera.getString("fecha_inicio","");
        carrera.getString("fecha_fin","");
        carrera.getString("id_pedido","0");
        carrera.getString("id_usuario","0");
        carrera.getString("id_moto","0");
        carrera.getString("id_tarifa","0");
        carrera.getString("monto","0");
        if(carrera.getString("id","0").equals("0")==false)
        {
            double lat=0;
            double lon=0;
            if( carrera.getString("latitud_inicio","0").equals("0")==false)
            {
                lat=Double.parseDouble(carrera.getString("latitud_inicio","0"));
                lon=Double.parseDouble(carrera.getString("longitud_inicio","0"));
                this.mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .title("INICIO")
                        .snippet( carrera.getString("detale_inicio","")+"\n"+ carrera.getString("fecha_inicio","")));
            }
            if( carrera.getString("latitud_fin","0").equals("0")==false)
            {
                lat=Double.parseDouble(carrera.getString("latitud_fin","0"));
                lon=Double.parseDouble(carrera.getString("longitud_fin","0"));
                this.mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .title("FIN y INICIO de la siguiente carrera.")
                        .snippet( carrera.getString("detale_fin","")+"\n"+ carrera.getString("fecha_fin","")));
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
            //agregaranimacion al mover la camara...
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lon))      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            SharedPreferences prefe=getSharedPreferences("pedido_en_curso", Context.MODE_PRIVATE);
            String id_pedido= prefe.getString("id_pedido","");

            if(id_pedido.equals("")==false) {
                this.nombre_usuario.setText(prefe.getString("nombre_usuario", ""));
                this.mensaje.setText(prefe.getString("mensaje", ""));
            }
        }
    }

    public String convertir_numero(String numero)
    {double decimal=Double.parseDouble(numero);
        if(decimal>=1000)
        {
            decimal=decimal/1000;
            numero=decimal+" km.";
        }
        else
            numero=numero+" mt.";
        return String.valueOf(numero);
    }

    public void mensaje(String mensaje)
    {
        Toast toast =Toast.makeText(this,mensaje,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public void mensaje_error(String mensaje)
    {

        final AlertDialog.Builder dialogo = new AlertDialog.Builder(this);

        final LayoutInflater inflater = getLayoutInflater();

        final View dialoglayout = inflater.inflate(R.layout.popup_dialogo_aceptar, null);
        final TextView Tv_titulo = (TextView) dialoglayout.findViewById(R.id.tv_titulo);
        final TextView Tv_mensaje = (TextView) dialoglayout.findViewById(R.id.tv_mensaje);
        final Button Bt_aceptar = (Button) dialoglayout.findViewById(R.id.bt_aceptar);



        Tv_mensaje.setText(mensaje);
        Tv_titulo.setText("Importante");
        Bt_aceptar.setText("OK");
        dialogo.setView(dialoglayout);
        dialogo.setCancelable(false);


        final AlertDialog finalDialogo =dialogo.create();
        finalDialogo.show();
        Bt_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalDialogo.dismiss();
            }
        });
    }
}
