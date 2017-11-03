package com.grayhartcorp.quevengan.menu_motista;



import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.contactos.Constants;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.grayhartcorp.quevengan.historial_notificacion.Notificacion;

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
import com.grayhartcorp.quevengan.R;

import android.content.res.Resources;
import android.os.BatteryManager;


import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.PolylineOptions;
import com.grayhartcorp.quevengan.Inicio;
import com.grayhartcorp.quevengan.Suceso;
import com.grayhartcorp.quevengan.menu_motista.pedidos.Menu_historial_pedido;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


import static android.support.design.widget.NavigationView.*;
import static android.support.v7.app.AlertDialog.*;

public class Menu_motista extends AppCompatActivity
        implements OnNavigationItemSelectedListener,OnMapReadyCallback,View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,LocationListener {

    boolean sw_verificando_pedidos=false;

    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
    LinearLayout.LayoutParams mat = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;

    private GoogleApiClient apiClient;

    private LocationRequest locRequest;


    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private TextView ubicacion, tv_nombre, tv_nombre_empresa;
    String direccion_empresa;

    private Button iniciar_carrera, finalizar_carrera, finalizar_todo, estado;

    int sw_acercar_a_mi_ubicacion;
    int fetchType = Constants.USE_ADDRESS_LOCATION;
    private static String APP_DIRECTORY = "Asapp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";
    boolean marcado_sw = false;
    boolean pedido = false;
    boolean sw_carrera_primera = false;
    ImageView imagen_usuario;
    ImageView im_opcion;

    String nombre_imagen="";



    Servicio_moto hilo_moto, hilo_moto2, hilo_moto3, hilo_moto4;
    Suceso suceso;
    int sw = 0;


    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;


    Builder dialogo1;
    LinearLayout lpedido, Ll_direccion_empresa;


    double latitud, longitud;

    JSONObject rutas = null;

    private Thread th_pedido;

    boolean sw_distancia_cercana=false,sw_distancia_llego_la_moto=false;
   Intent server_cargar_punto;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }


        setContentView(R.layout.activity_menu_motista);

        ubicacion = (TextView) findViewById(R.id.ubicacion);
        tv_nombre = (TextView) findViewById(R.id.tv_nombre);
        tv_nombre_empresa = (TextView) findViewById(R.id.tv_nombre_empresa);
        direccion_empresa = "";


        iniciar_carrera = (Button) findViewById(R.id.iniciar_carrera);
        finalizar_carrera = (Button) findViewById(R.id.finalizar_carrera);
        finalizar_todo = (Button) findViewById(R.id.finalizar_todo);
        estado = (Button) findViewById(R.id.estado);

        lpedido = (LinearLayout) findViewById(R.id.lpedido);
        Ll_direccion_empresa = (LinearLayout) findViewById(R.id.Ll_direccion_empresa);

        imagen_usuario=(ImageView)findViewById(R.id.imagen_usuario);
        im_opcion=(ImageView)findViewById(R.id.im_opcion);


        dialogo1 = new Builder(this);


        verificar_login();
        sw_acercar_a_mi_ubicacion = 0;

        server_cargar_punto=new Intent(getBaseContext(), Servicio_cargar_punto.class);
        //verificamos si tiene un pedido. en curso....
        try {
            Bundle bundle = getIntent().getExtras();
            int id_pedido = Integer.parseInt(bundle.getString("id_pedido"));
            cargar_datos_de_pedido_textview();
            pedido = true;
            marcado_sw = false;
            vista_button(false, true, false);

        } catch (Exception e) {
            vista_button(true, false, false);

            startService(server_cargar_punto);

        }
        verificar_su_pedido();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //fim


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //eventos de onclick
        iniciar_carrera.setOnClickListener(this);
        finalizar_carrera.setOnClickListener(this);
        finalizar_todo.setOnClickListener(this);


// switch para habilitar y desabilitar tu estado...
        estado.setOnClickListener(this);
        im_opcion.setOnClickListener(this);


        // Filtro de acciones que serán alertadas
        IntentFilter filter = new IntentFilter(
                com.grayhartcorp.quevengan.Constants.ACTION_RUN_ISERVICE);
        filter.addAction(com.grayhartcorp.quevengan.Constants.ACTION_PROGRESS_EXIT);

        // Crear un nuevo ResponseReceiver
        ResponseReceiver receiver =
                new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver,
                filter);


        //Construcción cliente API Google
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


      //  enableLocationUpdates();
        estado_de_la_bateria();
/*
        th_pedido = new Thread(new Runnable() {
            public void run() {
                SharedPreferences pe = getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
                for (int i = 0; get_estado() == 1 && pe.getString("id_pedido", "").equals("") == true; i++) {
                    verificar_notificacion_pedido();
                    estado_de_la_bateria();
                }
            }
        });
        */

  /*obtner imagen*/



    }

    private void verificar_su_pedido() {
        SharedPreferences pe = getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
        if (pe.getString("id_pedido", "").equals("") == false) {
            pedido = true;
            SharedPreferences carrera = getSharedPreferences("carrera_en_curso", MODE_PRIVATE);
            if (carrera.getString("id", "").equals("") == false || carrera.getString("id", "").equals("") == false || pe.getString("estado", "").equals("1") == true ) {
                vista_button(false, false, true);
            } else {
                vista_button(false, true, false);
            }
        } else {
            pedido = false;
            sw_distancia_cercana=false;
            sw_distancia_llego_la_moto=false;
            vista_button(true, false, false);

        }

    }


    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.historial) {
            drawer.closeDrawer(GravityCompat.START);

            startActivity(new Intent(Menu_motista.this,Menu_historial_pedido.class));

        } else if (id == R.id.notificacion) {
            drawer.closeDrawer(GravityCompat.START);

            startActivity(new Intent(Menu_motista.this, Notificacion.class));

        } else if (id == R.id.salir) {
            hilo_moto = new Servicio_moto();
            SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
            String id_moto = prefe.getString("id_moto", "");
            hilo_moto.execute(getString(R.string.servidor) + "frmMoto.php?opcion=cerrar_sesion", "3", id_moto);// parametro que recibe

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cerrar_sesion() {
        SharedPreferences datos_perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = datos_perfil.edit();
        editor.putString("nombre", "");
        editor.putString("apellido", "");
        editor.putString("ci", "");
        editor.putString("celular", "");
        editor.putString("email", "");
        editor.putString("marca", "");
        editor.putString("modelo", "");
        editor.putString("placa", "");
        editor.putString("direccion", "");
        editor.putString("telefono", "");
        editor.putString("referencia", "");
        editor.putString("codigo", "");
        editor.putString("credito", "");
        editor.putString("estado", "");
        editor.putString("login", "");
        editor.putString("moto", "0");
        editor.putString("id_moto", "");
        editor.putString("id_perfil", "");
        editor.putString("login_usuario", "0");
        editor.putString("login_moto", "0");
        editor.commit();
        finish();
        vaciar_toda_la_base_de_datos();
        startActivity(new Intent(this, Inicio.class));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        cargar_datos_de_pedido_textview();

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            View mapView = (View) getSupportFragmentManager().findFragmentById(R.id.map).getView();
        //ubicacion del button de Myubicacion de el fragento..
            View btnMyLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.setMargins(0, 0, 0, 0);
            btnMyLocation.setLayoutParams(params);

            View btbrujula = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("5"));
            RelativeLayout.LayoutParams pb = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pb.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            pb.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            pb.setMargins(0, 0, 20, 0);
            btbrujula.setLayoutParams(pb);


        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }

        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //  Toast.makeText(this,"distancia:"+getDistance(-17.8096367,-63.162204,-17.7998418,-63.168239),Toast.LENGTH_LONG).show();
            case R.id.im_opcion:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.estado:

                hilo_moto = new Servicio_moto();
                SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
                String id = prefe.getString("id_moto", "");
                if (estado.getText().toString().equals("inactivo") == true) {
                    hilo_moto.execute(getString(R.string.servidor) + "frmMoto.php?opcion=set_estado", "1", id, "1");// parametro que recibe
                } else {
                    hilo_moto.execute(getString(R.string.servidor) + "frmMoto.php?opcion=set_estado", "1", id, "0");// parametro que recibe
                }
                verificar_login();

                break;

            case R.id.iniciar_carrera:
                SharedPreferences car = getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = car.edit();
                editor.putInt("numero", 0);
                editor.commit();

                Servicio_carrera hilo_continuar = new Servicio_carrera();
                String detalle = "Inicio del pedido.";
                SharedPreferences preferencias = getSharedPreferences("pedido_en_curso", Context.MODE_PRIVATE);
                String id_pedido = preferencias.getString("id_pedido", "");
                String id_usuario = preferencias.getString("id_usuario", "");
                SharedPreferences perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
                String id_moto = perfil.getString("id_moto", "");
                hilo_continuar.execute(getString(R.string.servidor) + "frmCarrera.php?opcion=continuar_con_nueva_carrera", "3", String.valueOf(latitud), String.valueOf(longitud), detalle, id_usuario, id_pedido, id_moto);// parametro que recibe el doinbackground

                //share preferences  inicio de la primera carrera.
                set_primera_carrera(0);
                break;
            case R.id.finalizar_carrera:
                try{
                    mMap.clear();
                }catch (Exception e)
                {

                }
                try {

                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                        dialogo1.setTitle("Asapp");
                        dialogo1.setMessage("¿Finalizar y agregar nuevo destino?");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                SharedPreferences pedido = getSharedPreferences("pedido_en_curso", Context.MODE_PRIVATE);
                                String id_pedido = pedido.getString("id_pedido", "");
                                String id_usuario = pedido.getString("id_usuario", "");
                                SharedPreferences perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
                                String id_moto = perfil.getString("id_moto", "");
                                SharedPreferences preferencias = getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
                                String id_carrera = preferencias.getString("id", "");
                                String detalle = "";

                                aceptar(String.valueOf(latitud), String.valueOf(longitud), detalle,  "1", id_pedido, id_usuario, id_moto, id_carrera);
                            }
                        });
                        dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {

                            }
                        });
                        dialogo1.show();

                } catch (Exception e) {
                    mensaje("Tuvimos problemas al finalizar la carrera");
                }


                break;
            case R.id.finalizar_todo:
                try {

                    //obtenemos la distancia en linea recta entro 2 puntos...

                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                        dialogo1.setTitle("Asapp");
                        dialogo1.setMessage("¿Finalizar carrera?");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                SharedPreferences pedido = getSharedPreferences("pedido_en_curso", Context.MODE_PRIVATE);
                                String id_pedido = pedido.getString("id_pedido", "");
                                String id_usuario = pedido.getString("id_usuario", "");
                                SharedPreferences perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
                                String id_moto = perfil.getString("id_moto", "");
                                SharedPreferences preferencias = getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
                                String id_carrera = preferencias.getString("id", "");
                                String detalle = " ";


                                aceptar_finalizar_pedido(String.valueOf(latitud), String.valueOf(longitud), detalle, "1", id_pedido, id_usuario, id_moto, id_carrera);
                            }
                        });
                        dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {

                            }
                        });
                        dialogo1.show();

                } catch (Exception e) {
                    mensaje("Tuvimos problemas al finalizar el pedido.");
                }
                break;

        }
    }

    private void aceptar_finalizar_pedido(String latitud, String longitud, String detalle, String opcion, String id_pedido, String id_usuario, String id_moto, String id_carrera) {
        Servicio_carrera hilo_carrera = new Servicio_carrera();
        hilo_carrera.execute(getString(R.string.servidor) + "frmCarrera.php?opcion=terminar_carrera_pedido", "4", latitud, longitud, detalle, opcion, id_pedido, id_usuario, id_moto, id_carrera);// parametro que recibe el doinbackground
    }


    // comenzar el servicio con el motista....
    public class Servicio_moto extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//cargar estado.....
            if (params[1] == "1") {
                devuelve = "-1";
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
                    jsonParam.put("estado", params[3]);

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

                            devuelve = "2";
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


            //Cerrar sesion.....
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
                    jsonParam.put("id_moto", params[2]);

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
//brin para notificar que ya esta llegando la moto....
            if (params[1] == "4") {
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
                            devuelve = "9";
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




//finalizar
            if (params[1] == "6") {
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
                            devuelve = "10";
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
            preparar_progres_dialogo("Asapp","Autenticando. . .");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("respuesta del servidor=", "" + s);
            if (s.equals("1")) {
                vista_button(true, false, false);
                mensaje(suceso.getMensaje());
            } else if (s.equals("2")) {
                SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefe.edit();


                if (estado.getText().toString().equals("activo") == true) {
                    estado.setText("inactivo");
                    editor.putString("estado", "0");


                } else {
                    estado.setText("activo");
                    editor.putString("estado", "1");


                }
                editor.commit();

                vista_button(true, false, false);
            } else if (s.equals("3")) {
                cargar_datos_de_pedido_textview();
                Toast.makeText(getApplicationContext(), suceso.getMensaje(), Toast.LENGTH_SHORT).show();
            } else if (s.equals("4")) {
                mMap.clear();
                vista_button(true, false, false);
            } else if (s.equals("5")) {
                cerrar_sesion();
            } else if (s.equals("6")) {
                Toast.makeText(getApplicationContext(), suceso.getMensaje(), Toast.LENGTH_SHORT).show();
            } else if (s.equals("-1"))//cuando no tiene conexion a la base de datos .. solo SW_
            {
                mensaje_error("Error: Al conectar con el servidor.");
            } else if (s.equals("8")) {
                Toast.makeText(getApplicationContext(), suceso.getMensaje(), Toast.LENGTH_SHORT).show();
            } else if (s.equals("9")) {
                mensaje(suceso.getMensaje());
            } else if (s.equals("10")) {
                mensaje(suceso.getMensaje());
                vista_button(true, false, false);
            } else {
                mensaje_error("Error al conectar con el servidor.");
            }
            SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefe.edit();
            if (estado.getText().toString().equals("activo") == true) {
                editor.putString("estado", "1");

                startService(server_cargar_punto);
            } else {
                editor.putString("estado", "0");

                stopService(server_cargar_punto);


            }
            editor.commit();


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
    public class Servicio_verificar_pedido extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
if(isCancelled()==false) {
    //verificar si tiene pedido o carrera en curso.,,,,
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
            jsonParam.put("id_moto", params[2]);

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
                //codigo que actualiza el credito....
                String credito=respuestaJSON.getString("credito");
                actualizar_credito(credito);

                if (suceso.getSuceso().equals("1")) {
                    JSONArray usu = respuestaJSON.getJSONArray("pedido");

                    String id_pedido = usu.getJSONObject(0).getString("id");
                    String id_usuario = usu.getJSONObject(0).getString("id_usuario");
                    String calificacion = usu.getJSONObject(0).getString("calificacion");
                    String tipo_pedido = usu.getJSONObject(0).getString("tipo_pedido");
                    String fecha = usu.getJSONObject(0).getString("fecha");
                    String mensaje = usu.getJSONObject(0).getString("mensaje");
                    String estado = usu.getJSONObject(0).getString("estado");
                    String nombre_usuario = usu.getJSONObject(0).getString("nombre_usuario");
                    String empresa = usu.getJSONObject(0).getString("empresa");
                    String nombre_direccion = usu.getJSONObject(0).getString("nombre_direccion");
                    String detalle_direccion = usu.getJSONObject(0).getString("detalle_direccion");
                    double latitud = Double.parseDouble(usu.getJSONObject(0).getString("latitud"));
                    double longitud = Double.parseDouble(usu.getJSONObject(0).getString("longitud"));
                    //iniciar el servicio de pedido en curso


                    cargar_pedido_en_curso(id_pedido, id_usuario, calificacion, tipo_pedido, mensaje, fecha, estado, latitud, longitud, nombre_usuario, empresa, nombre_direccion, detalle_direccion, 0);
                    if (respuestaJSON.getString("carrera").equals("") == false) {
                        JSONArray car = respuestaJSON.getJSONArray("carrera");
                        String id_carrera = car.getJSONObject(0).getString("id");
                        String id_pedido_carrera = car.getJSONObject(0).getString("id_pedido");
                        cargar_carrera_en_curso(id_carrera, id_pedido_carrera);


                    } else {


                        cargar_carrera_en_curso("", "");
                    }


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


}
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog
            preparar_progres_dialogo("Asapp","Verificando si tíene pedido.");
            builder_dialogo.setCancelable(true);
            //alertDialog.show();       //Se comentó para ocultar dialogo

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog

            Log.e("respuesta del servidor=", "" + s);
            if (s.equals("1")) {

                pedido = true;
                marcado_sw = false;
                cargar_datos_de_pedido_textview();
             //   vista_button(false, true, false);

            } else if (s.equals("2")) {
                mMap.clear();
                vista_button(true, false, false);
                cargar_pedido_en_curso("", "", "", "", "", "", "", 0, 0, "","","","",0);
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

    private void actualizar_credito(String credito) {
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor editor=perfil.edit();
        editor.putString("credito",credito);
        editor.commit();
    }

    // comenzar el servicio con el motista....
    public class Servicio_moto_ruta_mas_corta extends AsyncTask<String, Integer, String> {


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
                        rutas = new JSONObject(result.toString());//Creo un JSONObject a partir del

                        devuelve = "7";
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
            if (s.equals("7")) {
                dibujar_ruta(rutas);
            } else {
                marcado_sw=false;
                Log.e("Ruta  :", "Error: Al conectar con el servidor.");
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


    private void cargar_datos_de_pedido_textview() {
        SharedPreferences prefe = getSharedPreferences("pedido_en_curso", Context.MODE_PRIVATE);
        String id_pedido = prefe.getString("id_pedido", "");

        if (id_pedido.equals("") == false) {
            double lat = Double.parseDouble(prefe.getString("latitud", ""));
            double lon = Double.parseDouble(prefe.getString("longitud", ""));


            try {

                Marker marker = this.mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point))
                        .anchor((float) 0.5, (float) 0.5)
                        .flat(true)
                        .position(new LatLng(lat, lon))
                        .title(prefe.getString("nombre_direccion", ""))
                        .snippet(prefe.getString("detalle_direccion", "")));
                marker.showInfoWindow();

//mover la camara del mapa hacia el pedido.....
                /*
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
                //agregaranimacion al mover la camara...
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(lat, lon))      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                */
            } catch (Exception e) {

            }


            SharedPreferences carrera = getSharedPreferences("carrera_en_curso", MODE_PRIVATE);
            if (carrera.getString("id", "").equals("") == false ||carrera.getString("id", "0").equals("0") == false) {
                vista_button(false, false, true);
            } else {
                vista_button(false, true, false);
            }


        } else {

            //poner en 0 altura de Linear layout LPEDIDO de pedidos...

            vista_button(true, false, false);
        }
        verificar_su_pedido();
    }

    private void cargar_pedido_en_curso(String id_pedido, String id_usuario, String calificacion, String tipo_pedido, String mensaje, String fecha, String estado, double latitud, double longitud, String nombre_usuario,String empresa,String nombre_direccion,String detalle_direccion,int cancelado) {

        SharedPreferences preferencias = getSharedPreferences("pedido_en_curso", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("id_pedido", id_pedido);
        editor.putString("id_usuario", id_usuario);
        editor.putString("calificacion", calificacion);
        editor.putString("tipo_pedido", tipo_pedido);
        editor.putString("mensaje", mensaje);
        editor.putString("fecha", fecha);
        editor.putString("latitud", String.valueOf(latitud));
        editor.putString("longitud", String.valueOf(longitud));
        editor.putString("nombre_usuario", nombre_usuario);
        editor.putString("empresa", empresa);
        editor.putString("nombre_direccion", nombre_direccion);
        editor.putString("detalle_direccion", detalle_direccion);
        editor.putString("estado", estado);
        editor.putInt("cancelado", 0);
        editor.commit();

    }

    private void cargar_carrera_en_curso(String id_carrera, String id_pedido) {

        SharedPreferences preferencias = getSharedPreferences("carrera_en_curso", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("id", String.valueOf(id_carrera));
        editor.putString("id_pedido", String.valueOf(id_pedido));

        editor.commit();

    }

    public void verificar_login() {
        SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);

        if (prefe.getString("login_moto", "0").equals("0") == true) {
            cerrar_sesion();
        }

        SharedPreferences f = getSharedPreferences("fecha", Context.MODE_PRIVATE);
        int a = Integer.parseInt(f.getString("anio", "0"));
        int m = Integer.parseInt(f.getString("mes", "0"));
        int d = Integer.parseInt(f.getString("dia", "0")); //Fecha anterior

        final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día
        java.util.Date hoy = new Date(); //Fecha de hoy
        Calendar calendar = new GregorianCalendar(a, m - 1, d);
        java.sql.Date fecha = new java.sql.Date(calendar.getTimeInMillis());
        long diferencia = (hoy.getTime() - fecha.getTime()) / MILLSECS_PER_DAY;
        if (diferencia > 3) {
            cerrar_sesion();
        }

    }

    public void vaciar_toda_la_base_de_datos() {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from tarifa");
        db.execSQL("delete from pedido");
        db.execSQL("delete from direccion");
        db.execSQL("delete from carrera");
        db.close();
        Log.e("sqlite ", "vaciar todo");
    }


    public void dibujar_ruta(JSONObject jObject) {
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        boolean sw_punto = false;
        LatLng punto = new LatLng(0, 0);
        PolylineOptions polylineOptions = new PolylineOptions();

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            double lat = ((LatLng) list.get(l)).latitude;
                            double lon = ((LatLng) list.get(l)).longitude;
                            punto = new LatLng(lat, lon);
                            polylineOptions.add(punto);
                            sw_punto = true;

                        }
                    }

                }
            }
            marcado_sw = false;

            //dibujar las lineas

            if (sw_punto == true) {
                mMap.clear();
                punto = new LatLng(latitud, longitud);
                Resources res=getResources();

                mMap.addPolyline(polylineOptions.width(15).color(res.getColor(R.color.colorPrimary)));
                //agregaranimacion al mover la camara...
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(punto)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                SharedPreferences prefe = getSharedPreferences("pedido_en_curso", Context.MODE_PRIVATE);
                double lat = Double.parseDouble(prefe.getString("latitud", ""));
                double lon = Double.parseDouble(prefe.getString("longitud", ""));

                Marker marker = this.mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point))
                        .anchor((float) 0.5, (float) 0.5)
                        .flat(true)
                        .position(new LatLng(lat, lon))
                        .title(prefe.getString("nombre_direccion", ""))
                        .snippet(prefe.getString("detalle_direccion", "")));
                marker.showInfoWindow();
            }


        } catch (JSONException e) {
            e.printStackTrace();
            marcado_sw = false;
        } catch (Exception e) {
            marcado_sw = false;
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Broadcast receiver que recibe las emisiones desde los servicios
    private class ResponseReceiver extends BroadcastReceiver {

        // Sin instancias
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {


                case com.grayhartcorp.quevengan.Constants.ACTION_RUN_ISERVICE:
                    // placa.setText(intent.getIntExtra(com.grayhartcorp.quevengan.Constants.EXTRA_PROGRESS, -1) + "");
                    break;

                case com.grayhartcorp.quevengan.Constants.ACTION_PROGRESS_EXIT:
                    //   marca.setText("Progreso");
                    break;
            }
        }
    }



    // funciones ppara iniciar y terminar una carrera....
    public void
    aceptar(String latitud, String longitud, String detalle, String opcion, String id_pedido, String id_usuario, String id_moto, String id_carrera) {
        // enviamos los parametros para cerrar una carrera.....
        Servicio_carrera hilo_carrera = new Servicio_carrera();
        hilo_carrera.execute(getString(R.string.servidor) + "frmCarrera.php?opcion=terminar_carrera", "2", latitud, longitud, detalle, opcion, id_pedido, id_usuario, id_moto, id_carrera);// parametro que recibe el doinbackground
    }

    //servicio de carrera
    public class Servicio_carrera extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
//finalizar una carrera...
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
                    jsonParam.put("latitud", params[2]);
                    jsonParam.put("longitud", params[3]);
                    jsonParam.put("detalle", params[4]);
                    jsonParam.put("opciones", params[5]);
                    jsonParam.put("id_pedido", params[6]);
                    jsonParam.put("id_usuario", params[7]);
                    jsonParam.put("id_moto", params[8]);
                    jsonParam.put("id_carrera", params[9]);


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
                            SharedPreferences guardar_id_carrera = getSharedPreferences("carrera_en_curso", MODE_PRIVATE);
                            SharedPreferences.Editor editar = guardar_id_carrera.edit();
                            editar.putString("id", respuestaJSON.getString("id_carrera"));
                            editar.commit();

                            devuelve = "3";
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
//iniciar carrea.
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

                            SharedPreferences guardar_id_carrera = getSharedPreferences("carrera_en_curso", MODE_PRIVATE);
                            SharedPreferences.Editor editar = guardar_id_carrera.edit();
                            editar.putString("id", respuestaJSON.getString("id_carrera"));
                            editar.commit();
                            SharedPreferences pedido = getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pedido.edit();
                            editor.putString("estado", "1");
                            editor.commit();


                            devuelve = "5";
                        } else {
                            devuelve = "6";
                        }


                    } else {
                        devuelve = "6";
                    }




                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            //finalizar carrera y tambien la finalizacion de todo el pedido....
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
                    jsonParam.put("latitud", params[2]);
                    jsonParam.put("longitud", params[3]);
                    jsonParam.put("detalle", params[4]);
                    jsonParam.put("opciones", params[5]);
                    jsonParam.put("id_pedido", params[6]);
                    jsonParam.put("id_usuario", params[7]);
                    jsonParam.put("id_moto", params[8]);
                    jsonParam.put("id_carrera", params[9]);


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

                        // vacia los datos que estan registrados en nuestra basemm de datos SQLite..

                        if (suceso.getSuceso().equals("1")) {
                            //obtenemos el ultimo credito...
                           actualizar_credito(respuestaJSON.getString("credito"));

                           devuelve = "7";
                        } else {
                            devuelve = "8";
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
            preparar_progres_dialogo("Asapp","Autenticando. . .");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("2")) {
                mensaje(suceso.getMensaje());
            } else if (s.equals("3")) {
                mensaje(suceso.getMensaje());
            } else if (s.equals("5")) {
                try{
                    mMap.clear();
                }catch (Exception e)
                {

                }
                vista_button(false, false, true);
            } else if (s.equals("6")) {
                mensaje(suceso.getMensaje());
            }else if (s.equals("7")) {
                mensaje(suceso.getMensaje());
                SharedPreferences pedido_en_curso=getSharedPreferences("pedido_en_curso",MODE_PRIVATE);
                SharedPreferences carrera_en_curso=getSharedPreferences("carrera_en_curso",MODE_PRIVATE);
                SharedPreferences.Editor editar_carrera=carrera_en_curso.edit();
                SharedPreferences.Editor editar_pedido=pedido_en_curso.edit();

                editar_carrera.putString("id","");
                editar_pedido.putString("id_pedido","");
                editar_carrera.commit();
                editar_pedido.commit();

                vista_button(true,false,false);
                set_estado(1);
            }else if (s.equals("8")) {
                mensaje_error(suceso.getMensaje());
            }

            else {
                mensaje("Error al conectar con el servidor..");
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

    public int getDistancia(double lat_a, double lon_a, double lat_b, double lon_b) {
        long Radius = 6371000;
        double dLat = Math.toRadians(lat_b - lat_a);
        double dLon = Math.toRadians(lon_b - lon_a);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (int) (Radius * c);
    }






    public String convertir_numero(String numero) {
        double decimal = Double.parseDouble(numero);
        if (decimal >= 1000) {
            decimal = decimal / 1000;
            numero = decimal + " km.";
        } else
            numero = numero + " mt.";
        return String.valueOf(numero);
    }

    public void mensaje(String mensaje) {
        Toast toast = Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public void marcar_ruta(boolean sw) {
        try {
            if (sw == true) {

                //buscamos una ruta para el motista     SOLO CO ACCESO A INTERNET

                SharedPreferences prefe1 = getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
                double lat = Double.parseDouble(prefe1.getString("latitud", ""));
                double lon = Double.parseDouble(prefe1.getString("longitud", ""));

                SharedPreferences p_moto = getSharedPreferences("punto_moto_ruta", MODE_PRIVATE);
                double latitud_moto=0;
                double longitud_moto=0;
                try {
                    latitud_moto = Double.parseDouble(p_moto.getString("latitud", "0"));
                    longitud_moto = Double.parseDouble(p_moto.getString("longitud", "0"));
                }catch (Exception e)
                {

                }
 int distancia=getDistancia(latitud,longitud,latitud_moto,longitud_moto);

                if(marcado_sw==false && distancia>5) {
                    marcado_sw=true;
                    Servicio_moto_ruta_mas_corta servicio_ruta = new Servicio_moto_ruta_mas_corta();
                    //servicio_ruta.execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + latitud + "," + longitud + "&destination=" + lat + "," + lon + "&mode=driving&key=AIzaSyAHb4Vvkjhq1xI-pYF2A1kZ-MPVhjkEofo", "4");// parametro que recibe el doinbackground
                    servicio_ruta.execute("https://maps.googleapis.com/maps/api/directions/json?origin=" + latitud + "," + longitud + "&destination=" + lat + "," + lon + "&mode=driving", "4");// parametro que recibe el doinbackground
                    SharedPreferences p_moto1 = getSharedPreferences("punto_moto_ruta", MODE_PRIVATE);
                    SharedPreferences.Editor editor=p_moto1.edit();
                    editor.putString("latitud",String.valueOf(latitud));
                    editor.putString("longitud",String.valueOf(longitud));
                    editor.commit();
                }
            }

        } catch (Exception e) {

        }
    }

    public void imagen_circulo(Drawable id_imagen, ImageView imagen) {
        Bitmap originalBitmap = ((BitmapDrawable) id_imagen).getBitmap();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        } else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }

//creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

//asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getWidth());

        imagen.setImageDrawable(roundedDrawable);
    }


    public void imagen_en_vista(ImageView imagen,String nombre) {
        Drawable dw;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                + File.separator + nombre;


        File newFile = new File(mPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);
        //Convertir Bitmap a Drawable.
        dw = new BitmapDrawable(getResources(), bitmap);
        //se edita la imagen para ponerlo en circulo.

        if (bitmap == null) {
            dw = getResources().getDrawable(R.mipmap.ic_perfil);
        }

        imagen_circulo(dw, imagen);
    }

    public void vista_button(boolean estado, boolean iniciar_carrera, boolean lpedido) {

        SharedPreferences prefe = getSharedPreferences("pedido_en_curso", Context.MODE_PRIVATE);

        this.lpedido.setLayoutParams(parms);
        this.estado.setLayoutParams(parms);
        this.iniciar_carrera.setLayoutParams(parms);
        this.Ll_direccion_empresa.setVisibility(View.INVISIBLE);
        sw_carrera_primera = false;
        im_opcion.setVisibility(View.INVISIBLE);


        Toolbar toolbar;
        ActionBarDrawerToggle toggle;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerOpened(View drawerView) {
                verificar_login();
                // carga los datos de su perfil. al momento de deslizar el menu. de perfil....
                TextView nombre,tv_credito;
                nombre = (TextView) drawerView.findViewById(R.id.nombre_completo);
                tv_credito = (TextView) drawerView.findViewById(R.id.tv_credito);
                ImageView perfil = (ImageView) drawerView.findViewById(R.id.perfil);


                SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
                nombre.setText(prefe.getString("nombre", "") + " " + prefe.getString("apellido", ""));
                tv_credito.setText(prefe.getString("credito","0")+"Bs.");


                imagen_en_vista(perfil,prefe.getString("id_moto","")+"_"+prefe.getString("placa","")+".jpg");
                mMap.clear();

                perfil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        descargas_imagen_moto_click();
                    }
                });

            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        if (estado == true) {
            Resources res = getResources();
            this.estado.setLayoutParams(mat);



            //cargar_pedido_en_curso("", "", "", "", "", "", "", 0, 0, "","","","",0);
            SharedPreferences perfil = getSharedPreferences("perfil", MODE_PRIVATE);
            if (perfil.getString("estado", "").equals("1")) {
                this.estado.setText("activo");

                this.estado.setBackgroundColor(res.getColor(R.color.colorPrimary));
                this.estado.setTextColor(res.getColor(R.color.colorPrimaryDark));

            } else {
                this.estado.setText("inactivo");
                this.estado.setBackgroundColor(res.getColor(R.color.colorPrimaryDark));
                this.estado.setTextColor(res.getColor(R.color.colorPrimary));
              //  stopService(new Intent(getApplicationContext(), Servicio_cargar_punto.class));
            }
            this.estado.setLayoutParams(mat);
            this.iniciar_carrera.setLayoutParams(parms);
            this.Ll_direccion_empresa.setVisibility(View.INVISIBLE);
            im_opcion.setVisibility(View.VISIBLE);
            toggle.setDrawerIndicatorEnabled(true);
            try {
                mMap.clear();
            } catch (Exception e) {
            }
            getSupportActionBar().show();

        } else if (iniciar_carrera == true) {


            this.iniciar_carrera.setLayoutParams(mat);
            this.Ll_direccion_empresa.setVisibility(View.VISIBLE);

            cargar_imagen_del_usuario();


            toggle.setDrawerIndicatorEnabled(false);
            sw_carrera_primera = true;

            this.tv_nombre.setText(prefe.getString("nombre_usuario", ""));
            this.tv_nombre_empresa.setText(prefe.getString("empresa", ""));
            getSupportActionBar().hide();

        } else if (lpedido) {

            this.lpedido.setLayoutParams(mat);
            toggle.setDrawerIndicatorEnabled(false);
            this.Ll_direccion_empresa.setVisibility(View.VISIBLE);
            this.tv_nombre.setText(prefe.getString("nombre_usuario", ""));
            this.tv_nombre_empresa.setText(prefe.getString("empresa", ""));
            getSupportActionBar().hide();

        }
        //poner en 0 altura de Linear layout LPEDIDO de pedidos...

    }

    private void cargar_imagen_del_usuario() {
SharedPreferences pedido=getSharedPreferences("pedido_en_curso",MODE_PRIVATE);

        //Ontemer imagen de perfil del usuario...
        String url_imagen_usuario=getString(R.string.servidor) + "frmMoto.php?opcion=get_imagen_usuario&id_usuario="+pedido.getString("id_usuario","");
        String st_nombre=""+pedido.getString("id_usuario","")+"_"+pedido.getString("nombre_usuario","")+".jpg";
        if(carrera_en_vista(imagen_usuario,st_nombre)==false)
        {
            getImage(url_imagen_usuario,st_nombre);
        }
        //fin de la obtencion de la foto de perfil del usuario.
    }


    //codigo para obtener mi ubicaicon.... INICIO


    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setFastestInterval(1000);
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
                            status.startResolutionForResult(Menu_motista.this, PETICION_CONFIG_UBICACION);
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

    private void disableLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                apiClient, this);

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(Menu_motista.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        apiClient, locRequest, Menu_motista.this);
            } catch (Exception e) {
            }
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

            ubicacion.setText("(" + String.valueOf(loc.getLatitude()) + "," + String.valueOf(loc.getLongitude()) + ")");
            latitud = loc.getLatitude();
            longitud = loc.getLongitude();

            SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
            String id = prefe.getString("id_moto", "");
            String lat = String.valueOf(String.valueOf(loc.getLatitude()));
            String lon = String.valueOf(String.valueOf(loc.getLongitude()));
            ubicacion.setText("p(" + lat + "," + lon + ")");


            if (sw_acercar_a_mi_ubicacion == 0) {
                //mover la camara del mapa a mi ubicacion.,,
                try {
                    //agregaranimacion al mover la camara...
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(loc.getLatitude(), loc.getLongitude()))      // Sets the center of the map to Mountain View
                            .zoom(15)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } catch (Exception e) {

                }
                sw_acercar_a_mi_ubicacion = 1;
            }

            /*if(pedido==true && marcado_sw==true)
            {
                marcar_ruta(marcado_sw);
            }
            */


//verificamos si tiene un pedido en curso y si es la primera carrea que se va a registrar.
            //para habilitar el botton de Inciar carrera tiene que estar a 50 metros cerca del lugar del pedido..
            if (sw_carrera_primera == true) {
                marcar_ruta(pedido);

                try {
                    SharedPreferences pedido = getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
                    double lat1 = Double.parseDouble(pedido.getString("latitud", ""));
                    double lon1 = Double.parseDouble(pedido.getString("longitud", ""));
                    int distancia = getDistancia(lat1, lon1, latitud, longitud);

                    SharedPreferences cerca = getSharedPreferences("configuracion", MODE_PRIVATE);
                    int limite=50;

                    try{limite=cerca.getInt("distancia_llego_la_moto",50);}
                    catch (Exception e)
                    { limite=50;}

                    if (distancia < limite) {
                        iniciar_carrera.setEnabled(true);

                    } else {
                        iniciar_carrera.setEnabled(false);

                    }


                } catch (Exception e) {

                }
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon))));
            }
            cargar_datos_de_pedido_textview();


        } else {
            ubicacion.setText("desconocido");
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
                        // btnActualizar.setChecked(false);
                        break;
                }
                break;
        }
    }


    @Override
    protected void onStop() {
        Log.e("stado Menu_motisa", "stop");
        set_estado(0);

        super.onStop();

    }


    @Override
    protected void onStart() {
        Log.e("stado Menu_motisa", "start");
        set_estado(1);

            verificar_si_tiene_pedido();


        descargas_imagen_moto();

        marcar_ruta(pedido);
        enableLocationUpdates();
        super.onStart();
    }

    private void descargas_imagen_moto() {
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        String url_imagen_motista=getString(R.string.servidor) + "frmMoto.php?opcion=get_imagen&id_moto="+perfil.getString("id_moto","");
        String st_nombre=perfil.getString("id_moto","")+"_"+perfil.getString("placa","")+".jpg";
        if(existe_perfil(st_nombre)==false)
        {
            getImage(url_imagen_motista,st_nombre);
        }
    }
    private void descargas_imagen_moto_click() {
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        String url_imagen_motista=getString(R.string.servidor) + "frmMoto.php?opcion=get_imagen&id_moto="+perfil.getString("id_moto","");
        String st_nombre=perfil.getString("id_moto","")+"_"+perfil.getString("placa","")+".jpg";

            getImage(url_imagen_motista,st_nombre);
    }

    @Override
    protected void onResume() {
        Log.e("stado Menu_motisa", "resume");
        set_estado(1);

        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.e("stado Menu_motisa", "restart");
        set_estado(1);
        super.onRestart();
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(LOGTAG, "Recibida nueva ubicación!");
        SharedPreferences pe = getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
        if( get_estado() == 1 && pe.getString("id_pedido", "").equals("") == true) {
            verificar_notificacion_pedido();
            estado_de_la_bateria();
        }
        //Mostramos la nueva ubicación recibida
        updateUI(location);
        pedido_cancelado();

        set_estado(1);
    }
    //codigo para obtener mi ubicaicon.... FIN

    public void verificar_notificacion_pedido() {
        try {
            SharedPreferences ped = getSharedPreferences("ultima_notificacion_pedido", MODE_PRIVATE);
            int est=0;
            try{
                est=ped.getInt("estado", 0);
            }catch (Exception e)
            {
                est=0;
            }
            if (est == 1) {
              //  onStop();
                Intent moto = new Intent(getApplicationContext(), Notificacion_pedido_moto.class);
                moto.putExtra("id_pedido", ped.getString("id_pedido", ""));
                moto.putExtra("nombre", ped.getString("nombre", ""));
                moto.putExtra("empresa", ped.getString("empresa", ""));
                moto.putExtra("latitud", ped.getString("latitud", ""));
                moto.putExtra("longitud", ped.getString("longitud", ""));
                moto.putExtra("nombre_direccion", ped.getString("nombre_direccion", ""));
                moto.putExtra("detalle_direccion", ped.getString("detalle_direccion", ""));



                SharedPreferences.Editor editar = ped.edit();
                editar.putString("id_pedido","");
                editar.putString("nombre","");
                editar.putString("empresa", "");
                editar.putString("direccion", "");
                editar.putString("latitud", "");
                editar.putString("longitud", "");
                editar.putString("nombre_direccion", "");
                editar.putString("detalle_direccion", "");
                editar.putInt("estado", 0);
                editar.commit();


                startActivity(moto);

            }

        } catch (Exception e) {

        }
    }

    public void mensaje_error(String mensaje) {

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


    public void estado_de_la_bateria() {
        if (en_pedido() == false) {
            try {


                TextView tv_bateria;

                String charging = "";
                String dato = "";
                int level, scale;
                float batteryPct;
// Get resources reference

// Get values TextViews
                tv_bateria = (TextView) findViewById(R.id.tv_bateria);


                // Get battery's status
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = registerReceiver(null, ifilter);
// Check if the battery is charging or is charged?
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                        || status == BatteryManager.BATTERY_STATUS_FULL;
// Update UI status
                dato = "Status:" + Integer.toString(status);
// Update UI charging
                if (isCharging) {
                    charging = "yes ";
// Get charging method
                    int chargePlug = batteryStatus.getIntExtra(
                            BatteryManager.EXTRA_PLUGGED, -1);
                    boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                    // boolean acCharge = chargePlug ==
                    // BatteryManager.BATTERY_PLUGGED_AC;
                    if (usbCharge) {
                        charging = charging + " " + "USB";
                    } else {
                        charging = charging + " " + "AC";
                    }


                } else {
                    charging = "NO";

                }
                dato = dato + " ,Charging:" + charging;
// Update UI level
                level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                batteryPct = 100 * level / (float) scale;
                tv_bateria.setText(dato + "." + "Nivel:" + level + ".SCALE:" + scale + " " + Float.toString(batteryPct) + "%");

                if (batteryPct <= 20) {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("ASAPP");
                    dialogo1.setMessage("Necesitas Carga tu Celular.");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            aceptar_cargar();
                        }
                    });

                    dialogo1.show();
                    onStop();


                }
            } catch (Exception e) {
            }
        }
    }

    private void aceptar_cargar() {
        finish();
    }

    private void set_estado(int i) {
        if (get_estado() != i) {
            SharedPreferences estado = getSharedPreferences("actividad", MODE_PRIVATE);
            SharedPreferences.Editor editor = estado.edit();
            editor.putInt("estado", i);
            editor.commit();
            if (i == 1) {
                /*
                try {

                    th_pedido.start();
                } catch (Exception e) {
                    th_pedido = new Thread(new Runnable() {
                        public void run() {
                            SharedPreferences pe = getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
                            for (int i = 0; get_estado() == 1 && pe.getString("id_pedido", "").equals("") == true; i++) {
                                    verificar_notificacion_pedido();
                                    estado_de_la_bateria();
                            }
                        }
                    });
                    th_pedido.start();
                }
                */
            }
        }
    }

    private int get_estado() {
        SharedPreferences estado = getSharedPreferences("actividad", MODE_PRIVATE);
        int est=0;
        try{
            est=estado.getInt("estado",0);
        }catch (Exception e)
        {
            est=0;
        }
        return est;
    }

    private boolean en_pedido() {
        SharedPreferences pe = getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
        return (pe.getString("id_pedido", "").equals("") == false);
    }

    public void verificar_si_tiene_pedido()
    {
        SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String id_moto = prefe.getString("id_moto", "");
        Servicio_verificar_pedido servicio_verificar_pedido=new Servicio_verificar_pedido();
        servicio_verificar_pedido.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedido_por_id_moto", "2", id_moto);// parametro que recibe
    }
    public void pedido_cancelado()
    {
        SharedPreferences pedido=getSharedPreferences("pedido_en_curso",MODE_PRIVATE);
        int cancelado=0;
        try{cancelado=pedido.getInt("cancelado",0);}
        catch (Exception e)
        {cancelado=0;}
        if(cancelado==1) {
            SharedPreferences.Editor editor=pedido.edit();
            editor.putInt("cancelado",0);
            editor.commit();
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("Su pedido a sido cancelado");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    aceptar_cancelar();
                }
            });
            dialogo1.show();
        }

    }

    private void aceptar_cancelar() {
       startActivity(new Intent(this,Menu_motista.class));
    }


    //Obtener la imagen del motista..
    private void getImage(String id,String nombre)//
    {this.nombre_imagen=nombre;
        class GetImage extends AsyncTask<String,Void,Bitmap> {

            String nombre;


            public GetImage(String nombre) {

                this.nombre=nombre;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                {
                }
                else
                {
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

        GetImage gi = new GetImage(nombre);
        gi.execute(id);
    }

    private void guardar_en_memoria(Bitmap bitmapImage,String nombre)
    {
        File file=null;
        FileOutputStream fos = null;
        try {
            String APP_DIRECTORY = "Asapp/";//nombre de directorio
            String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";//nombre de la carpeta
            file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            File mypath=new File(file,nombre);//nombre del archivo imagen

            boolean isDirectoryCreated = file.exists();//pregunto si esxiste el directorio creado
            if(!isDirectoryCreated)
                isDirectoryCreated = file.mkdirs();

            if(isDirectoryCreated && bitmapImage !=null) {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean existe_perfil(String nombre)
    { boolean sw_carrera=false;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + "Asapp/Imagen"
                + File.separator +nombre;


        File newFile = new File(mPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);

        if( bitmap!=null)
        {
            sw_carrera=true;
        }

        return sw_carrera;
    }

    public void set_primera_carrera(int cantidad)
    {
        /**
         *0= sin inicio su primera carrera.
         *1= si finalizo su primera carrera.
         */
        SharedPreferences primera=getSharedPreferences("primera_carrera",MODE_PRIVATE);
        SharedPreferences.Editor editor=primera.edit();
        editor.putInt("cantidad",cantidad);
        editor.commit();
    }
    public int get_primera_carrera()
    {
        /**
         *0= sin inicio su primera carrera.
         *1= si finalizo su primera carrera.
         */
        int cantidad=0;
        SharedPreferences primera=getSharedPreferences("primera_carrera",MODE_PRIVATE);
       try{
           cantidad=primera.getInt("cantidad",0);
       }catch (Exception e)
       {
           cantidad=0;
       }
        return cantidad;
    }

    public boolean carrera_en_vista(ImageView imagen,String nombre)
    { boolean sw_carrera=false;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + "Asapp/Imagen"
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
