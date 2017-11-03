package com.grayhartcorp.quevengan;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.menu_motista.Menu_motista;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Pedido_us extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback ,GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,OnMarkerClickListener,
        LocationListener {

    private static final int LOCATION_REQUEST_CODE = 1;

    JSONObject rutas=null;

int distancia=0;
private Suceso suceso;
    private TextView nombre_moto,celular,tv_tiempo,tv_notificacion;
    private String snombre,scelular,sid_moto,smarca,splaca,sid_pedido,sid_usuario,detalle,latitud,longitud;
    ImageView imagen_moto;

    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;


    Button cancelar;
    LinearLayout ll_cancelar;
    String nombre_imagen="";

    LinearLayout ll_notificacion;




    boolean sw2=true,marcado_sw=false;//marcado_sw : es para verificar si ya se marco la ruta.

    LatLng ultima_ubicacion = new LatLng(0, 0);

    boolean hay_carrera=false;



    private GoogleMap mMap;

    LatLng myPosition=new LatLng(0,0);

    LinearLayout.LayoutParams cero = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
    LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;

    private GoogleApiClient apiClient;
    private LocationRequest locRequest;
    Marker marker_moto,marker_usuario;
    PolylineOptions polylineOptions = new PolylineOptions();
    Polyline polyline;


    @Override
    public void onBackPressed() {
        SharedPreferences ped = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        if(ped.getString("id_pedido","").length()<=0) {
            startActivity(new Intent(this,Menu_p.class));
           // super.onBackPressed();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }

        getSupportActionBar().hide();

        setContentView(R.layout.activity_pedido_us);
        nombre_moto = (TextView) findViewById(R.id.nombre_moto);
        celular = (TextView) findViewById(R.id.celular);
        tv_tiempo=(TextView)findViewById(R.id.tv_tiempo);
        tv_notificacion=(TextView)findViewById(R.id.tv_notificacion);

        imagen_moto=(ImageView)findViewById(R.id.imagen_moto);

        cancelar=(Button)findViewById(R.id.cancelar);
        ll_cancelar=(LinearLayout)findViewById(R.id.Ll_cancelar);

        ll_notificacion=(LinearLayout)findViewById(R.id.ll_notificacion);

        cancelar.setOnClickListener(this);


        ll_notificacion.setOnClickListener(this);







        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_u);
        mapFragment.getMapAsync(this);




        //verificamos si se esta recibiendo los parametros necesarios para este formulario...
        try{
            Bundle intro=getIntent().getExtras();
            snombre=intro.getString("nombre");
            scelular=intro.getString("celular");
            sid_moto=intro.getString("id_moto");
            smarca=intro.getString("marca");
            splaca=intro.getString("placa");
            sid_pedido=intro.getString("id_pedido");

            SharedPreferences prefe=getSharedPreferences("ultimo_pedido",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=prefe.edit();
            editor.putString("id_pedido",sid_pedido);
            editor.commit();




            //Ontemer imagen de perfil del motista...
            String url_imagen_motista=getString(R.string.servidor) + "frmMoto.php?opcion=get_imagen&id_moto="+sid_moto;
            String st_nombre=""+sid_moto+"_"+splaca+".jpg";
            if(carrera_en_vista(imagen_moto,st_nombre)==false)
            {
                getImage(url_imagen_motista,st_nombre);
            }
            //fin de la obtencion de la foto de perfil del motista.

        }catch (Exception e)
        {
            startActivity(new Intent(getApplicationContext(),Menu_p.class));
        }
        // fin ... si no se recibe los parametros correspondientes se cerrara la funcion...








//cargar mapa
        //Construcción cliente API Google
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        enableLocationUpdates();
        correcto();
    }






    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onStop() {
        Log.e("stado PEDIDO_US","stop");
        set_estado(0);
        super.onStop();
    }



    @Override
    protected void onPause() {
        Log.e("stado PEDIDO_US","Pausa");
        set_estado(0);
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.e("stado PEDIDO_US","start");
        set_estado(1);
        enableLocationUpdates();
        correcto();
        super.onStart();
    }


    @Override
    protected void onRestart() {
        Log.e("stado PEDIDO_US","restart");
        set_estado(1);
        super.onRestart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

            case R.id.cancelar:
                SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                double lat1 = Double.parseDouble(prefe.getString("latitud", ""));
                double lon1 = Double.parseDouble(prefe.getString("longitud", ""));

                SharedPreferences moto = getSharedPreferences("puntos_pedido", MODE_PRIVATE);
                double lat_moto = Double.parseDouble(moto.getString("latitud", ""));
                double lon_moto = Double.parseDouble(moto.getString("longitud", ""));

                 distancia=getDistancia(lat1,lon1,lat_moto,lon_moto);
                if(distancia<=500)
                {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("ATENCION!");
                    dialogo1.setMessage("Su Moto esta a "+ Html.fromHtml("<b>"+distancia+" mt.</b><br>¿Esta seguro que desea cancelar?"));
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            confirmar_cancelacion(distancia);
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {

                        }
                    });
                    dialogo1.show();
                }
                else
                {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Asapp");
                    dialogo1.setMessage("¿Desea cancelar el pedido?.");
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            confirmar_cancelacion(distancia);
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {

                        }
                    });
                    dialogo1.show();

                }
                break;


            case R.id.ll_notificacion:
                SharedPreferences s_notificacion = getSharedPreferences("notificacion_pedido", MODE_PRIVATE);
                SharedPreferences.Editor editar_notificacion=s_notificacion.edit();
                editar_notificacion.putInt("estado",1);
                editar_notificacion.commit();
                ll_notificacion.setVisibility(View.INVISIBLE);
                break;

        }
    }

    private void confirmar_cancelacion(int distancia) {
        SharedPreferences sh_pedido = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);

        Servicio hilo_moto = new Servicio();
        if(sh_pedido.getString("id_pedido","").equals("")==false) {
            hilo_moto.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido", "3", sh_pedido.getString("id_pedido", ""),String.valueOf(distancia));// parametro que recibe el doinbackground
        }
        else
        {
            startActivity(new Intent(getApplicationContext(),Menu_p.class));
        }
    }

    private boolean verificar_datos(String id_usuario, String sid_pedido, String sid_moto, String detalle, String latitud, String longitud) {
       return (id_usuario.length()>0  && sid_pedido.length()>0 && sid_moto.length()>0 && detalle.length()>0&&latitud.length()!=0 &&longitud.length()!=0);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Controles UI
        // Controles UI


            View mapView = (View) getSupportFragmentManager().findFragmentById(R.id.map_u).getView();
//bicacion del button de Myubicacion de el fragento..


            View btbrujula = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("5"));
            RelativeLayout.LayoutParams pb = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pb.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            pb.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            pb.setMargins(0, 0, 20, 0);
            btbrujula.setLayoutParams(pb);





        polyline= mMap.addPolyline(polylineOptions.width(15).color(-16776961));

       marker_moto= mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto))
                .position(new LatLng(0,0))
                .anchor((float) 0.5, (float) 0.5)
                .flat(true)
                .rotation(0));

        marker_usuario=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point))
                    .anchor((float) 0.5, (float) 0.8)
                    .flat(true)
                    .position(new LatLng(0,0)));

        marker_moto.setVisible(false);
        marker_usuario.setVisible(false);






        SharedPreferences puntos_pedido=getSharedPreferences("puntos_pedido",MODE_PRIVATE);
        int estado=puntos_pedido.getInt("estado",0);
        if(estado==0)
        {
            pintar_recorrido_pedido();
        }
        else if(estado==1)
        {
            cancelar.setLayoutParams(cero);
            pintar_recorrido_carrera();
        }





      //  if(sw2==true && ultima_ubicacion.latitude!=0 && ultima_ubicacion.longitude!=0)
        if(ultima_ubicacion.latitude!=0 && ultima_ubicacion.longitude!=0)
        {
            //agregaranimacion al mover la camara...
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ultima_ubicacion)      // Sets the center of the map to Mountain View
                    .zoom(16)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else
        {   try {
                SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                double lat1 = Double.parseDouble(prefe.getString("latitud", ""));
                double lon1 = Double.parseDouble(prefe.getString("longitud", ""));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(lat1, lon1))      // Sets the center of the map to Mountain View
                        .zoom(16)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }catch (Exception e)
            {}
        }


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(this.marker_moto))
        {
            this.marker_moto.showInfoWindow();
        }
        return false;
    }


    // comenzar el servicio con el motista....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
            if(isCancelled()==false && alertDialog.isShowing()==true) {
                devuelve = "-1";
                //VERIFICAR SI TIENE CARRERAS  ....
                if (params[1] == "1") { //mandar JSON metodo post ENVIAR LA CONFIRMACION DEL LLEGADO DEL MOTISTA.,,,
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


//HACE EL CLICK CON LA CONFIRMACION DE QUE LLEGO EL MOTISTA HASTA DONDE ESTA....
                if (params[1] == "2") { //mandar JSON metodo post ENVIAR LA CONFIRMACION DEL LLEGADO DEL MOTISTA.,,,
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

                //CANCELAR PEDIDO
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
                        jsonParam.put("id_pedido", params[2]);
                        jsonParam.put("distancia", params[3]);


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
                                devuelve = "4";
                            } else {
                                devuelve = "3";
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
            }
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog

            preparar_progres_dialogo("Asapp","Autenticando. . .");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);
            if(s.equals("1"))
            {
                Intent carrera= new Intent(Pedido_us.this,Carrera_us.class);
                carrera.putExtra("id_pedido",sid_pedido);
                carrera.putExtra("nombre",snombre);
                carrera.putExtra("celular",scelular);
                carrera.putExtra("id_moto",sid_moto);
                carrera.putExtra("marca",smarca);
                carrera.putExtra("placa",splaca);
                carrera.putExtra("id_pedido",sid_pedido);
                startActivity(carrera);
            }

             else if(s.equals("3"))
            {
                Toast.makeText(Pedido_us.this,suceso.getMensaje(),Toast.LENGTH_SHORT).show();

            }
            else if(s.equals("4"))
            {//se cancelo con exito el pedido
                Toast.makeText(getApplicationContext(),suceso.getMensaje(),Toast.LENGTH_SHORT).show();
                try {
                    SharedPreferences ped = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                    SharedPreferences.Editor editar1 = ped.edit();
                    editar1.putString("nombre_moto", "");
                    editar1.putString("celular", "");
                    editar1.putString("id_moto", "");
                    editar1.putString("marca", "");
                    editar1.putString("placa","");
                    editar1.putString("color", "");
                    editar1.putString("latitud", "");
                    editar1.putString("longitud", "");
                    editar1.putString("id_pedido", "");
                    editar1.commit();
                   startActivity(new Intent(getApplicationContext(),Menu_p.class));
                }catch (Exception e)
                {

                }
            }
             else if(s.equals("2"))
             {
                 Toast.makeText(getApplicationContext(),suceso.getMensaje(),Toast.LENGTH_SHORT).show();
                 startActivity(new Intent(getApplicationContext(),Menu_p.class));
             }
             else if(s.equals("-1"))
             {
                 mensaje_error("Error: Al conectar con el servidor.");

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



    public void pintar_recorrido_pedido() {
       boolean sw=true;
        try {
           int id = Integer.parseInt(sid_pedido);
       }catch (Exception e)
       {
           sw=false;
       }
        if(sw==true && marcado_sw==false) {

            marcado_sw=true;
            try {
                SharedPreferences moto = getSharedPreferences("puntos_pedido", MODE_PRIVATE);
                double lat_moto = Double.parseDouble(moto.getString("latitud", ""));
                double lon_moto = Double.parseDouble(moto.getString("longitud", ""));

                ultima_ubicacion = new LatLng(lat_moto,lon_moto);
              marker_moto.setPosition(new LatLng(lat_moto,lon_moto));
                marker_moto.setVisible(true);

                    SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                    double lat = Double.parseDouble(prefe.getString("latitud", ""));
                    double lon = Double.parseDouble(prefe.getString("longitud", ""));
                    marker_usuario.setPosition(new LatLng(lat, lon));
                    marker_usuario.setVisible(true);
                //agregaranimacion al mover la camara...




                        Servicio_moto_ruta_mas_corta servicio_ruta = new Servicio_moto_ruta_mas_corta();
                        // servicio_ruta.execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat_moto + "," + lon_moto + "&destination=" + lat + "," + lon + "&mode=driving&key=AIzaSyAHb4Vvkjhq1xI-pYF2A1kZ-MPVhjkEofo", "4");// parametro que recibe el doinbackground
                        servicio_ruta.execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat_moto + "," + lon_moto + "&destination=" + lat + "," + lon + "&mode=driving", "4");// parametro que recibe el doinbackground




            }
            catch (Exception e)
            {
                marcado_sw=false;

            }


        }

    }



    public LatLng ultimo_registro( int id_pedido) {
        SharedPreferences puntos=getSharedPreferences("puntos_pedido",MODE_PRIVATE);
        double lat=Double.parseDouble(puntos.getString("latitud",""));
        double lon=Double.parseDouble(puntos.getString("longitud",""));
        LatLng punto=new LatLng(lat,lon);
      return punto;
    }



    public void correcto() {
        Intent intent = new Intent(getApplicationContext(), Servicio_pedido.class);
        intent.setAction(com.grayhartcorp.quevengan.Constants.ACTION_RUN_ISERVICE);
        startService(intent);

       nombre_moto.setText(snombre);
        celular.setText(scelular);

    }

    public  int getDistancia(double lat_a,double lon_a, double lat_b, double lon_b){
        long  Radius = 6371000;
        double dLat = Math.toRadians(lat_b-lat_a);
        double dLon = Math.toRadians(lon_b-lon_a);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) * Math.sin(dLon /2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (int) (Radius * c);
    }



    public void pintar_recorrido_carrera() {
        boolean sw=true;
        boolean sw_punto=false;
        tv_tiempo.setText("");
        cancelar.setLayoutParams(cero);
        try {
            int id = Integer.parseInt(sid_pedido);
        }catch (Exception e)
        {
            sw=false;
        }
        if(sw==true) {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "easymoto", null, 1);
            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = null;
            int numero =1;
            String auxiliar="";
            fila = bd.rawQuery("select * from puntos_carrera where id_pedido=" + sid_pedido + " ORDER BY fecha ASC ", null);

            try {
                //limpiamos el mapa...

                LatLng punto = new LatLng(0, 0);
                PolylineOptions polylineOptions = new PolylineOptions();
                mMap.clear();

                if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
                    do {
                        double lat = Double.parseDouble(fila.getString(2));
                        double lon = Double.parseDouble(fila.getString(3));
                        String id_carrera=fila.getString(1);
                        punto = new LatLng(lat, lon);
                        polylineOptions.add(punto);
                        if(auxiliar.equals(id_carrera)==false)
                        {   auxiliar=id_carrera;
                           Marker marker_punto= mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_fin))
                                    .position(punto)
                                    .anchor((float)0.5,(float)0.5)
                                    .flat(true)
                                    .rotation(0)
                                    .title(""+numero));
                            marker_punto.showInfoWindow();
                            numero++;
                        }
                        sw_punto=true;

                    } while (fila.moveToNext());
                }
                if(sw_punto==true) {
                    bd.close();
                    Resources res=getResources();
                    mMap.addPolyline(polylineOptions.width(15).color(res.getColor(R.color.colorPrimary)));

                    hay_carrera=true;
                    ultima_ubicacion=punto;

                    marker_moto= mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto))
                            .position(punto)
                            .anchor((float) 0.5, (float) 0.5)
                            .flat(true)
                            .rotation(0));

                    marker_moto.setVisible(true);
                    marker_usuario.setVisible(false);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(punto)      // Sets the center of the map to Mountain View
                            .zoom(16)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



                }
            } catch (Exception e) {

            }
        }

    }

    public void mensaje(String mensaje)
    {
        Toast toast =Toast.makeText(this,mensaje,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }


    //obtener ubicacion...

    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setFastestInterval(1500);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest locSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locRequest)
                        .build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        apiClient, locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        Log.i(LOGTAG, "Configuración correcta");
                        startLocationUpdates();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.i(LOGTAG, "Se requiere actuación del usuario");
                            status.startResolutionForResult(Pedido_us.this, PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(LOGTAG, "Error al intentar solucionar configuración de ubicación");
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(LOGTAG, "No se puede cumplir la configuración de ubicación necesaria");
                        break;
                }
            }
        });
    }



    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(Pedido_us.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, Pedido_us.this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services

        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
    }

    private void updateUI(Location loc) {

        if (loc != null) {
            myPosition=new LatLng(loc.getLatitude(),loc.getLongitude());
            double latitude = loc.getLatitude();
            double  longitude = loc.getLatitude();
            LatLng latLng = new LatLng(latitude, longitude);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                Log.e(LOGTAG, "Permiso denegado");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PETICION_CONFIG_UBICACION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOGTAG, "El usuario no ha realizado los cambios de configuración necesarios");

                        break;
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(LOGTAG, "Recibida nueva ubicación!");

        //Mostramos la nueva ubicación recibida
        updateUI(location);

        cargar_los_puntos_de_pedido();

        set_estado(1);
       //Mostrar el ultimo mensaje de la notificacion en el Textview de la notificacion....
        mostrar_notificacion(tv_notificacion,ll_notificacion);

        mostrar_notificacion_de_finalizacion_de_pedido();

    }

    private void cargar_los_puntos_de_pedido() {

        SharedPreferences prefe=getSharedPreferences("ultimo_pedido",Context.MODE_PRIVATE);
        int sw=0;

        if(prefe.getString("id_pedido","").length()>=1) {





            SharedPreferences puntos_pedido=getSharedPreferences("puntos_pedido",MODE_PRIVATE);
            int estado=puntos_pedido.getInt("estado",0);
            if(estado==0)
            {

                pintar_recorrido_pedido();

            }
            else if(estado==1)
            {
                pintar_recorrido_carrera();
            }




            sw=1;


        }
        else
        {  if(sw==1) {
            mensaje("Se ha finalizado su pedido.");
            startActivity(new Intent(getApplicationContext(),Menu_p.class));
            sw=2;
        }
        }
    }


    // comenzar el servicio con el motista....
    public class Servicio_moto_ruta_mas_corta extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
            //Obtener el camino mas corto. para llegar mas rapido ..
            if (params[1] == "4") {
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
                        rutas= new JSONObject(result.toString());//Creo un JSONObject a partir del

                        devuelve="7";
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
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("7"))
            {
                dibujar_ruta(rutas);
            }
            else
            {
                marcado_sw=false;
                Log.e("Ruta  :","Error: Al conectar con el servidor.");
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

    public void dibujar_ruta(JSONObject jObject){
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        boolean sw_punto=false;
        LatLng punto=new LatLng(0,0);

        PolylineOptions polylineOptions = new PolylineOptions();

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            double lat=((LatLng)list.get(l)).latitude;
                            double lon=((LatLng)list.get(l)).longitude;
                            punto = new LatLng(lat, lon);
                            polylineOptions.add(punto);


                            sw_punto=true;

                        }
                    }

                    tv_tiempo.setText((String)((JSONObject)((JSONObject)jLegs.get(j)).get("duration")).get("text"));

                }


            }


            //dibujar las lineas
            Resources res=getResources();
            mMap.clear();
            mMap.addPolyline(polylineOptions.width(15).color(res.getColor(R.color.colorPrimary)));
            marker_moto= mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto))
                    .position(ultima_ubicacion)
                    .anchor((float) 0.5, (float) 0.5)
                    .flat(true)
                    .rotation(0));

            SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
            double lat = Double.parseDouble(prefe.getString("latitud", ""));
            double lon = Double.parseDouble(prefe.getString("longitud", ""));

            marker_usuario= mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point))
                    .position(new LatLng(lat, lon))
                    .anchor((float) 0.5, (float) 0.5)
                    .flat(true)
                    .rotation(0));
            marker_moto.setVisible(true);
            marker_usuario.setVisible(true);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ultima_ubicacion)      // Sets the center of the map to Mountain View
                    .zoom(16)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));




            marcado_sw=false;
              } catch (JSONException e) {
            e.printStackTrace();
            marcado_sw=false;
        }catch (Exception e){
            marcado_sw=false;
        }

    }



    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
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

    private void set_estado(int i) {
        SharedPreferences estado=getSharedPreferences("actividad",MODE_PRIVATE);
        SharedPreferences.Editor editor=estado.edit();
        editor.putInt("estado",i);
        editor.commit();
    }

    //Obtener la imagen del motista..
    private void getImage(String id,String nombre)//
    {this.nombre_imagen=nombre;
        class GetImage extends AsyncTask<String,Void,Bitmap> {
            ImageView bmImage;
            String nombre;


            public GetImage(ImageView bmImage,String nombre) {
                this.bmImage = bmImage;
                this.nombre=nombre;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                {
                    Drawable d = getResources().getDrawable(R.mipmap.ic_perfil);
                    Bitmap mIcon = drawableToBitmap(d);
                    bmImage.setImageBitmap(mIcon);
                }
                else
                {
                    bmImage.setImageBitmap(bitmap);
                    bmImage.setAdjustViewBounds(true);
                    bmImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    bmImage.setPadding(0, 0, 0, 0);
                    guardar_en_memoria(bitmap,nombre);
                }


            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = strings[0];//hace consulta ala Bd para recurar la imagen

                Bitmap mIcon = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage(imagen_moto,nombre);
        gi.execute(id);
    }

    private void guardar_en_memoria(Bitmap bitmapImage,String nombre)
    {
        File file=null;
        FileOutputStream fos = null;
        try {
            String APP_DIRECTORY = "Asapp/";//nombre de directorio
            String MEDIA_DIRECTORY = APP_DIRECTORY + "motista";//nombre de la carpeta
            file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            File mypath=new File(file,nombre);//nombre del archivo imagen

            boolean isDirectoryCreated = file.exists();//pregunto si esxiste el directorio creado
            if(!isDirectoryCreated)
                isDirectoryCreated = file.mkdirs();

            if(isDirectoryCreated) {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean carrera_en_vista(ImageView imagen,String nombre)
    { boolean sw_carrera=false;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + "Asapp/motista"
                + File.separator +nombre;


        File newFile = new File(mPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);

        if( bitmap!=null)
        {
            imagen.setImageBitmap(bitmap);
            imagen.setAdjustViewBounds(true);
            imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imagen.setPadding(0, 0, 0, 0);
            sw_carrera=true;
        }

        return sw_carrera;
    }
// fin de la obtencion de imagen del motista.

    public void mostrar_notificacion(TextView notificacion,LinearLayout ll_notificacion)
    {
        try {
            SharedPreferences s_pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
            SharedPreferences s_notificacion = getSharedPreferences("notificacion_pedido", MODE_PRIVATE);

            int id_pedido=Integer.parseInt(s_pedido.getString("id_pedido",""));
            int id_pedido_notificacion=Integer.parseInt(s_notificacion.getString("id_pedido",""));
            int estado=s_notificacion.getInt("estado",0);

            if(id_pedido==id_pedido_notificacion && estado==0)
            {
               notificacion.setText(s_notificacion.getString("mensaje",""));
                ll_notificacion.setVisibility(View.VISIBLE);
            }

        }catch (Exception e)
        {

        }
    }

    public void mostrar_notificacion_de_finalizacion_de_pedido()
    {
        try {
            SharedPreferences s_pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);

            String id_pedido=s_pedido.getString("id_pedido","");
            String estado=s_pedido.getString("estado","0");
            String monto_total=s_pedido.getString("monto_total","0 bs");

            if(id_pedido.equals("")==true && estado.equals("2")==true)
            {
                SharedPreferences.Editor editor=s_pedido.edit();
                editor.putString("estado","0");
                editor.putString("monto_total","0");
                editor.commit();



                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pedido Finalizado");
                builder.setMessage("Total "+monto_total);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        confirmar_cancelacion(distancia);
                    }
                });
                builder.create();
                builder.setCancelable(false);
                builder.show();
            }

        }catch (Exception e)
        {

        }
    }


    public void preparar_progres_dialogo(String titulo,String mensaje)
    {
        builder_dialogo = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();

        final View dialoglayout = inflater.inflate(R.layout.popup_progress_dialog, null);
        final TextView Tv_titulo = (TextView) dialoglayout.findViewById(R.id.tv_titulo);
        final TextView Tv_mensaje = (TextView) dialoglayout.findViewById(R.id.tv_mensaje);
        final ImageView im_icono=(ImageView)dialoglayout.findViewById(R.id.im_icono);
        im_icono.setBackgroundResource(R.drawable.animacion_icono);
        AnimationDrawable frameAnimation = (AnimationDrawable) im_icono.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
        Tv_mensaje.setText(mensaje);
        Tv_titulo.setText(titulo);
        builder_dialogo.setView(dialoglayout);
        alertDialog=builder_dialogo.create();
    }
}
