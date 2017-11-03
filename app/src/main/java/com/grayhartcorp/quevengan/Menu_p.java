package com.grayhartcorp.quevengan;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import android.support.v7.app.AlertDialog;

import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Gravity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.grayhartcorp.quevengan.direccion.CDireccion;
import com.grayhartcorp.quevengan.direccion.Registrar_direccion;
import com.grayhartcorp.quevengan.direccion.lista_direccion_pedido.Direccion_item;
import com.grayhartcorp.quevengan.empresa.Empresa;


import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.grayhartcorp.quevengan.historial.Menu_historial;
import com.grayhartcorp.quevengan.historial_notificacion.Notificacion;
import com.grayhartcorp.quevengan.tarifa.Tarifa;


import android.location.Address;

import android.os.Handler;
import android.os.ResultReceiver;

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


public class Menu_p extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,LocationListener {

LinearLayout ll_mapa;

    private static final int LOCATION_REQUEST_CODE = 1;
    private static String APP_DIRECTORY = "Asapp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";

    AlertDialog.Builder dialogo1 ;
    public TextView ubicacion;
    private GoogleMap mMap;
    //ProgressDialog pDialog;
    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;

    private LocationRequest locRequest;

    TextView ultima_direccion;

    Button pedi_moto;

    int sw_acercar_a_mi_ubicacion;
    boolean sw_verificar_si_tiene_pedido=false,sw_cancelar_pedido=true;

int itemp=-1;

    LatLng myPosition;
    private JSONArray puntos_moto;
    private Suceso suceso;
    private int sw_iteraccion;
    private Servicio hilo_moto;
    Servicio_pedir_moto hilo_moto_pedido;


    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;

    private GoogleApiClient apiClient;


    private LatLng latLng;
    private int interval = 1000;
    private boolean firstTime = true;
    private TextView locationText;
    private Context mContext;
    private String TAG = "Mapa_moto";
    ProgressDialog pUbicacion;
    JSONArray JSdirecciones=null;
    ArrayList<CDireccion> direccion ;



    LinearLayout ll_llamar;
    LinearLayout.LayoutParams cero = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
    LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
ImageView im_opcion;

    private Thread th_pedido;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;

    String nombre_imagen="";

    LinearLayout ll_flotante;
    TextView tv_mensaje_pedido;
    ImageView im_rodar_pedido;
    Button bt_cancelar_pedido;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


         //   Window w = getWindow(); // in Activity's onCreate() for instance
             
        //    w.setFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);



          }
        //changing statusbar


        setContentView(R.layout.activity_menu_p);


//inicio layout flotante....
        ll_flotante=(LinearLayout)findViewById(R.id.ll_flotante);
        tv_mensaje_pedido=(TextView)findViewById(R.id.tv_mensaje_pedido);
        im_rodar_pedido=(ImageView)findViewById(R.id.im_rodar_pedido);
        bt_cancelar_pedido=(Button)findViewById(R.id.bt_cancelar_pedido);
//fin layout flotante


        ubicacion = (TextView) findViewById(R.id.ubicacion);

         ultima_direccion = (TextView) findViewById(R.id.ultima_ubicacion);


        ll_llamar=(LinearLayout)findViewById(R.id.Ll_llamar);

        ll_mapa=(LinearLayout)findViewById(R.id.ll_mapa);

        sw_acercar_a_mi_ubicacion = 0;
        sw_iteraccion = 0;

        im_opcion=(ImageView)findViewById(R.id.im_opcion);


        dialogo1= new AlertDialog.Builder(this);
        //veerificar_ login usuario----
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        if(perfil.getString("login_usuario","0").equals("0")) {
            startActivity(new Intent(this, Inicio.class));
        }

        SharedPreferences.Editor editar=perfil.edit();
        editar.putString("proceso","");
        editar.commit();




        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//Codigo del menu desplegable ...
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerOpened(View drawerView) {
                if(sw_cancelar_pedido==true) {
                    // carga los datos de su perfil. al momento de deslizar el menu. de perfil....
                    TextView nombre, empresa;
                    ImageView perfil;
                    nombre = (TextView) drawerView.findViewById(R.id.nombre_completo);
                    perfil = (ImageView) drawerView.findViewById(R.id.perfil);
                    empresa = (TextView) drawerView.findViewById(R.id.empresa);

                    SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
                    nombre.setText(prefe.getString("nombre", "") + " " + prefe.getString("apellido", ""));
                    empresa.setText(prefe.getString("nombre_empresa", ""));

                    imagen_en_vista(perfil, prefe.getString("id_usuario", "") + "_" + prefe.getString("nombre", "") + ".jpg");

                    perfil.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //cerrar el menu lateral
                            drawer.closeDrawer(GravityCompat.START);

                            Intent ii = new Intent(getApplicationContext(), Mi_perfil.class);
                            startActivity(ii);
                        }
                    });
                }
                else
                {
                    drawer.closeDrawer(GravityCompat.START);
                }

            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //click de los Items del menu desplegable..
        navigationView.setNavigationItemSelectedListener(this);


        /// atributos cargador para el Mapa


        pedi_moto = (Button) findViewById(R.id.pedir_moto);




        pedi_moto.setOnClickListener(this);
        ultima_direccion.setOnClickListener(this);
        im_opcion.setOnClickListener(this);
        bt_cancelar_pedido.setOnClickListener(this);

        /*
        lista_direccion.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {
                // TODO Auto-generated method stub
                SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
                if(perfil.getString("administrador","0").equals("1")==true && JSdirecciones.length()==position )
                {


                    if(perfil.getString("id_empresa","").equals("")==false && perfil.getString("id_empresa","").equals("null")==false && perfil.getString("administrador","0").equals("1")==true ) {
                        ll_lista_direccion.setLayoutParams(cero);
                        ultima_direccion.setText("Ubicación actual");
                        Intent buscador = new Intent(getApplicationContext(), Registrar_direccion.class);
                        buscador.putExtra("id_empresa",perfil.getString("id_empresa",""));
                        startActivity(buscador);
                    }else
                    {
                        mensaje("No tienes permiso para agregar Dirección.");
                    }
                }
                else {
                    try {
                        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
                        String titulo=JSdirecciones.getJSONObject(position).getString("nombre");
                        String detalle=usuario.getString("nombre","")+" "+usuario.getString("apellido","");
                        LatLng punto = new LatLng(Double.parseDouble(JSdirecciones.getJSONObject(position).getString("latitud")), Double.parseDouble(JSdirecciones.getJSONObject(position).getString("longitud")));
                        mMap.clear();
                        Marker marker= mMap.addMarker(new MarkerOptions()
                                .position(punto)
                                .title(titulo));


                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(punto)      // Sets the center of the map to Mountain View
                                .zoom(16)                   // Sets the zoom
                                .bearing(0)                // Sets the orientation of the camera to east
                                .tilt(10)                   // Sets the tilt of the camera to 30 degrees
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        itemp=position;

                        marker.showInfoWindow();

                        ll_llamar.setLayoutParams(parent);
                        ll_lista_direccion.setLayoutParams(cero);
                        ultima_direccion.setText(JSdirecciones.getJSONObject(position).getString("nombre"));

                    }
                    catch (Exception e)
                    {
                        itemp=-1;
                    }
                }





            }

        });
        */
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


        verificar_tipo_de_usuario(false);




        th_pedido = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; get_estado_pedido() ==0 && i<180 && sw_verificar_si_tiene_pedido==false && sw_cancelar_pedido==false; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        sw_verificar_si_tiene_pedido=true;
                        verificar_si_tiene_pedido();
                    }
                });

            }

        });


        ll_mapa.setOnClickListener(this);
        ll_flotante.setVisibility(View.INVISIBLE);




  /*obtner imagen*/

    }

    @Override
    protected void onStop() {
        Log.e("stado MENU_P","stop");
        set_estado(0);
        super.onStop();

    }

    @Override
    protected void onStart() {
        if(sw_verificar_si_tiene_pedido==false) {
            sw_verificar_si_tiene_pedido=true;
            verificar_si_tiene_pedido_2();
        }
        Log.e("stado MENU_P","start");



        SharedPreferences perfil1=getSharedPreferences("perfil",MODE_PRIVATE);
        String url_imagen_motista=getString(R.string.servidor) + "frmMoto.php?opcion=get_imagen_usuario&id_usuario="+perfil1.getString("id_usuario","");
        String st_nombre=perfil1.getString("id_usuario","")+"_"+perfil1.getString("nombre","")+".jpg";
        if(existe_perfil(st_nombre)==false)
        {
            getImage(url_imagen_motista,st_nombre);
        }


        enableLocationUpdates();
        super.onStart();
    }

    @Override
    protected void onResume() {
        if(sw_verificar_si_tiene_pedido==false) {
            sw_verificar_si_tiene_pedido=true;
            verificar_si_tiene_pedido_2();
        }
        Log.e("stado MENU_P","resumet");

        super.onResume();
    }

    @Override
    protected void onRestart() {
        if(sw_verificar_si_tiene_pedido==false) {
            sw_verificar_si_tiene_pedido=true;
            verificar_si_tiene_pedido_2();
        }
        Log.e("stado MENU_P","restart");

        super.onRestart();
    }
    @Override
    public void onBackPressed() {
        if(sw_cancelar_pedido==true) {

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    }


    public void imagen_en_vista(ImageView imagen,String nombre)
    { Drawable dw;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + "Asapp/Imagen"
                + File.separator + nombre;


        File newFile = new File(mPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);
        //Convertir Bitmap a Drawable.
        dw = new BitmapDrawable(getResources(), bitmap);
        //se edita la imagen para ponerlo en circulo.

        if( bitmap==null)
        { dw = getResources().getDrawable(R.mipmap.ic_perfil);}

        imagen_circulo(dw,imagen);
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






    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        if (id == R.id.notificacion) {
            //cerrar el menu lateral
            drawer.closeDrawer(GravityCompat.START);

            Intent ii = new Intent(this, Notificacion.class);
            startActivity(ii);
        }else
        if (id == R.id.soporte) {
            //cerrar el menu lateral
            drawer.closeDrawer(GravityCompat.START);


            SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
            String[] to = {  "edgarq354@gmail.com" };
            String[] cc = { perfil.getString("email","anonimo")};
            enviar(to, cc, "Asapp",
                    "Esto es un email enviado desde un usuario de Asapp");

        } else if (id == R.id.historial) {
            drawer.closeDrawer(GravityCompat.START);
            //  FragmentManager fragmentManager=getSupportFragmentManager();
            // fragmentManager.beginTransaction().replace(R.id.content_frame,new FHistorial()).commit();
            startActivity(new Intent(this, Menu_historial.class));
        }  else if (id == R.id.salir) {

            SharedPreferences datos_perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = datos_perfil.edit();
            editor.putString("nombre", "");
            editor.putString("apellido", "");
            editor.putString("celular", "");
            editor.putString("codigo", "");
            editor.putString("id_usuario", "");
            editor.putString("id_empresa", "");
            editor.putString("usuario", "1");
            editor.putString("moto","0");
            editor.putString("login_usuario", "0");
            editor.putString("login_moto", "0");
            editor.putString("administrador", "0");
            editor.commit();

            SharedPreferences empresa=getSharedPreferences("empresa",MODE_PRIVATE);
            SharedPreferences.Editor editar=empresa.edit();
            editar.putString("id","");
            editar.putString("nombre","");
            editar.putString("direccion","");
            editar.putString("telefono","");
            editar.putString("razon_social","");
            editar.putString("nit","");
            editar.putString("latitud","");
            editar.putString("longitud","");
            editar.commit();

            finish();
            vaciar_toda_la_base_de_datos();
            startActivity(new Intent(this, Inicio.class));
        }
        else if(id==R.id.empresa)
        {//cerrar el menu lateral
            drawer.closeDrawer(GravityCompat.START);

            //verificamos si tiene una empresa registrada.
            SharedPreferences perfil=getSharedPreferences("perfil",Context.MODE_PRIVATE);
            if(perfil.getString("administrador","0").equals("1")==true)
            {

                SharedPreferences empresa=getSharedPreferences("empresa",Context.MODE_PRIVATE);
                Intent pedido=new Intent(Menu_p.this,Empresa.class);
                pedido.putExtra("id",empresa.getString("id","0"));
                pedido.putExtra("nombre",empresa.getString("nombre",""));
                pedido.putExtra("direccion",empresa.getString("direccion",""));
                pedido.putExtra("telefono",empresa.getString("telefono",""));
                pedido.putExtra("razon_social",empresa.getString("razon_social",""));
                pedido.putExtra("nit",empresa.getString("nit",""));
                pedido.putExtra("latitud",empresa.getString("latitud",""));
                pedido.putExtra("longitud",empresa.getString("longitud",""));
                startActivity(pedido);

            }
            else
            {
                hilo_moto = new Servicio();
                hilo_moto.execute(getString(R.string.servidor) + "frmEmpresa.php?opcion=get_administrador", "6",perfil.getString("id_usuario",""));// parametro que recibe el doinbackground

                mensaje("No tienes ninguna empresa registrada.");
            }

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void abrir_el_pedido() {

        SharedPreferences prefe = getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
        int estado=0;

        try{estado=prefe.getInt("estado",0);}
        catch (Exception e)
        { estado=0;}

        if(estado<2) {

            SharedPreferences datos_perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);

            hilo_moto = new Servicio();
            hilo_moto.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedido_por_id_usuario", "9", datos_perfil.getString("id_usuario",""));// parametro que recibe el doinbackground


        }
    }


    @Override
    public boolean onSupportNavigateUp() {


        return super.onSupportNavigateUp();
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


    public void restaurar(ImageView perfil) {
        SharedPreferences preferencias = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String direccion = preferencias.getString("imagen_perfil", "");
        int camara = Integer.parseInt(preferencias.getString("camara", "0"));
        // 1: es foto de camara.
        // 2: es foto de galeria.
        switch (camara) {
            case 0:
                break;
            case 1:
                Bitmap bitmap = BitmapFactory.decodeFile(direccion);
                Drawable dw1 = new BitmapDrawable(getResources(), bitmap);
                imagen_circulo(dw1, perfil);
                break;
            case 2:
                try {
                    Bitmap tempBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(direccion)));
                    //Convert bitmap to drawable
                    Drawable dw2 = new BitmapDrawable(getResources(), tempBitmap);
                    //Conveertir Imagen a Circulo
                    imagen_circulo(dw2, perfil);
                } catch (Exception e) {

                }
                break;
        }


    }

    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public void mostrar_adrres(double lat, double lon) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.

        } catch (IllegalArgumentException illegalArgumentException) {

        }

//INICIO ............ solo con internet........

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            //error. o no tiene datos recolectados...
        } else {
            Address address = addresses.get(0);
            //  ArrayList<String> addressFragments = new ArrayList<String>();
            // addressFragments.add(address.getAddressLine(1));

            String sub = "";
            if (address.getFeatureName() != null) {
                sub = sub + address.getFeatureName() + " | ";
            }
            if (addresses.get(0).getThoroughfare() != null) {
                sub = sub + addresses.get(0).getThoroughfare() + " | ";
            }
            if (address.getAddressLine(0) != null) {
                sub = sub + address.getAddressLine(0) + " | ";
            }
            ultima_direccion.setText(sub);

        }

// FIN de solo con internet,,,,,,,,,,,,,,,,,,,,...................
    }

    public void guardar_adrres_memoria_local(double lat, double lon) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.

        } catch (IllegalArgumentException illegalArgumentException) {

        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            //error. o no tiene datos recolectados...
        } else {
            Address address = addresses.get(0);
            SharedPreferences preferencias = getSharedPreferences("perfil", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("nombre_direccion", address.getFeatureName());
            editor.putString("numero_direccion", address.getCountryName());
            editor.putString("indicacion_direccion", addresses.get(0).getThoroughfare());
            editor.putString("direccion", address.getAddressLine(0));
            editor.putString("c_direccion", "1");
            editor.commit();

        }
    }


    @Override
    public void onClick(View view) {
        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
        switch (view.getId()) {
            case R.id.im_opcion:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.ll_mapa:
                ultima_direccion.setText("Ubicación actual");
                itemp=-1;
                break;
            case R.id.pedir_moto:

                flotante_pedir(true);
                SharedPreferences puntos_pedido=getSharedPreferences("puntos_pedido",MODE_PRIVATE);
                SharedPreferences.Editor editor=puntos_pedido.edit();
                editor.putInt("estado",0);
                editor.commit();


                if(usuario.getString("id_empresa","").equals("")==false || usuario.getString("id_empresa","").equals("null")==false) {
                    hilo_moto_pedido = new Servicio_pedir_moto();

                    //dibuja en el mapa las moto que estan cerca...
                    //hilo_moto.execute(getString(R.string.servidor)+"frmMoto.php?opcion=get_motos_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
// USUARIO QUE ES ADMINISTRADOR...
                 if(usuario.getString("id_empresa", "").equals("") == false && usuario.getString("id_empresa", "").equals("null") == false && usuario.getString("administrador", "").equals("1")==true )
                {




                        if(itemp>=0)
                        {
                            try {
                                SharedPreferences per = getSharedPreferences("perfil", MODE_PRIVATE);
                                String id = per.getString("id_usuario", "");
                                String nombre = per.getString("nombre", "");
                                nombre = nombre + " " + per.getString("apellido", "");
                                hilo_moto_pedido.execute(getString(R.string.servidor) + "frmPedido.php?opcion=pedir_moto", "7", id, String.valueOf(JSdirecciones.getJSONObject(itemp).getString("latitud")), String.valueOf(JSdirecciones.getJSONObject(itemp).getString("longitud")), nombre,per.getString("id_empresa", ""));// parametro que recibe el doinbackground
                            } catch (Exception e) {
                                flotante_pedir(false);
                                mensaje("Por favor active su GPS para realizar pedidos.");
                            }
                            //     ll_llamar.setVisibility(View.INVISIBLE);
                        }

                    }
                 else
                 if (usuario.getString("id_empresa", "").equals("") == false && usuario.getString("id_empresa", "").equals("null") == false && itemp>=0) {
                     // USUARIO QUE NO ES ADMINISTRADOR.

                     try {
                         SharedPreferences per = getSharedPreferences("perfil", MODE_PRIVATE);
                         String id = per.getString("id_usuario", "");
                         String nombre = per.getString("nombre", "");
                         nombre = nombre + " " + per.getString("apellido", "");
                         hilo_moto_pedido.execute(getString(R.string.servidor) + "frmPedido.php?opcion=pedir_moto", "7", id, String.valueOf(JSdirecciones.getJSONObject(itemp).getString("latitud")), String.valueOf(JSdirecciones.getJSONObject(itemp).getString("longitud")), nombre,per.getString("id_empresa", ""));// parametro que recibe el doinbackground
                     } catch (Exception e) {
                         flotante_pedir(false);
                         mensaje("Por favor active su GPS para realizar pedidos.");
                     }
                     ll_llamar.setLayoutParams(cero);
                     //  ll_llamar.setVisibility(View.INVISIBLE);

                 } else {
                     flotante_pedir(false);
                     mensaje("Necesitas formar parte de una Empresa.");
                 }


                }
                else
                {
                   flotante_pedir(false);
                }



            break;



            case R.id.ultima_ubicacion:
                if(sw_cancelar_pedido==true) {
                    if (usuario.getString("id_empresa", "").equals("") == false && usuario.getString("id_empresa", "").equals("null") == false) {

                        Servicio_direccion servicio = new Servicio_direccion();
                        servicio.execute(getString(R.string.servidor) + "frmDireccion.php?opcion=get_direccion_por_id_empresa", "1", usuario.getString("id_empresa", ""));// parametro que recibe el doinbackground
                    } else {
                        mensaje("Necesitas formar parte de una Empresa.");
                    }
                }

                break;

            case R.id.bt_cancelar_pedido:
                flotante_pedir(false);
                break;
        }
    }

    private void flotante_pedir(boolean b) {
        Animation  giro= AnimationUtils.loadAnimation(this,R.anim.rotar);
        if(b==true)
        {
            sw_cancelar_pedido=false;
        ll_flotante.setVisibility(View.VISIBLE);
        giro.reset();
        im_rodar_pedido.startAnimation(giro);
        }
        else
        {
            sw_cancelar_pedido=true;
            this.ll_flotante.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            View mapView = (View) getSupportFragmentManager().findFragmentById(R.id.map).getView();
//bicacion del button de Myubicacion de el fragento..
            View btnMyLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.setMargins(0, 0, 100, 0);
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


    //clase de AddresResulReciever para obtener los datos de Adresss,, latitud y longitud
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationText.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationText.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            }
        }
    }

    public void cargar_puntos(int id,String nombre,String apellido,String celular,String marca,String placa, double lat,double lon)
    {
        try {


            LatLng punto = new LatLng(lat, lon);
            Marker marker = this.mMap.addMarker(new MarkerOptions()
                    .position(punto)
                    .title(nombre+" "+apellido)
                    .snippet("Celular:"+celular+",marca:"+marca+",placa:"+placa));

           this.mMap.addMarker(new MarkerOptions().position(punto));
       /*
        mMap.clear();

         LatLngBounds UBICACION = new LatLngBounds(new LatLng(lat-0.005,lon-0.005), new LatLng(lat+0.005, lon+0.005));
         mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(UBICACION, 0));
           */

        }
        catch (Exception e)
        {

        }
    }
    public void agregar_en_mapa_ubicaciones_de_moto()
    {  try {
        for (int i = 0; i < puntos_moto.length(); i++) {
            int id_moto = Integer.parseInt(puntos_moto.getJSONObject(i).getString("id"));
            double lat = Double.parseDouble(puntos_moto.getJSONObject(i).getString("latitud"));
            double lon = Double.parseDouble(puntos_moto.getJSONObject(i).getString("longitud"));
            String nombre =puntos_moto.getJSONObject(i).getString("nombre");
            String apellido =puntos_moto.getJSONObject(i).getString("apellido");
            String celular =puntos_moto.getJSONObject(i).getString("celular");
            String marca =puntos_moto.getJSONObject(i).getString("marca");
            String placa =puntos_moto.getJSONObject(i).getString("placa");

            cargar_puntos(id_moto,nombre,apellido,celular,marca,placa,lat,lon);

        }
    }catch (Exception e)
    {

    }

    }


    // comenzar el servicio para la conexion con la base de datos.....
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
                    jsonParam.put("latitud", params[2]);
                    jsonParam.put("longitud", params[3]);
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
                            puntos_moto=respuestaJSON.getJSONArray("moto");
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
            //cargar Direccion de Casa
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
                    jsonParam.put("detalle",params[2]);
                    jsonParam.put("latitud",params[3]);
                    jsonParam.put("longitud",params[4]);
                    jsonParam.put("id_usuario",params[5]);
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
                            devuelve="3";
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
            //cargar Direccion de Oficina
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
                    jsonParam.put("detalle",params[2]);
                    jsonParam.put("latitud",params[3]);
                    jsonParam.put("longitud",params[4]);
                    jsonParam.put("id_usuario",params[5]);
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
                            devuelve="3";
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
            //cargar Direccion de Trabajo
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
                    jsonParam.put("detalle",params[2]);
                    jsonParam.put("latitud",params[3]);
                    jsonParam.put("longitud",params[4]);
                    jsonParam.put("id_usuario",params[5]);
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
                            devuelve="3";
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

            // verificar si tiene un pedido que aun no ha finalizado....
            //obtener datos del pedido en curso.....
            if (params[1] == "5") { //mandar JSON metodo post para login
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

                            JSONArray dato=respuestaJSON.getJSONArray("pedido");
                           String  snombre=dato.getJSONObject(0).getString("nombre_moto");
                           String  scelular=dato.getJSONObject(0).getString("celular");
                           String  sid_moto=dato.getJSONObject(0).getString("id_moto");
                           String smarca=dato.getJSONObject(0).getString("marca");
                           String splaca=dato.getJSONObject(0).getString("placa");
                           String sid_pedido=dato.getJSONObject(0).getString("id_pedido");

                        onStop();
                            Intent pedido=new Intent(Menu_p.this,Pedido_us.class);
                            pedido.putExtra("nombre",snombre);
                            pedido.putExtra("celular",scelular);
                            pedido.putExtra("id_moto",sid_moto);
                            pedido.putExtra("marca",smarca);
                            pedido.putExtra("placa",splaca);
                            pedido.putExtra("id_pedido",sid_pedido);

                            Intent intent = new Intent(getApplicationContext(), Servicio_pedido.class);
                            intent.setAction(com.grayhartcorp.quevengan.Constants.ACTION_RUN_ISERVICE);
                            startService(intent);

                            startActivity(pedido);

                            devuelve="4";
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

//obtener datos de Empresa
            if (params[1] == "6") { //mandar JSON metodo post para login
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
                    jsonParam.put("id_usuario", params[2]);

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
                            JSONArray dato=respuestaJSON.getJSONArray("empresa");
                            //registramos con un ->1 si es administrador,....
                            SharedPreferences empresa=getSharedPreferences("perfil",MODE_PRIVATE);
                            SharedPreferences.Editor editar=empresa.edit();
                            editar.putString("administrador","1");
                            editar.commit();


                            Intent pedido=new Intent(Menu_p.this,Empresa.class);
                            pedido.putExtra("id",dato.getJSONObject(0).getString("id"));
                            pedido.putExtra("nombre",dato.getJSONObject(0).getString("nombre"));
                            pedido.putExtra("direccion",dato.getJSONObject(0).getString("direccion"));
                            pedido.putExtra("telefono",dato.getJSONObject(0).getString("telefono"));
                            pedido.putExtra("razon_social",dato.getJSONObject(0).getString("razon_social"));
                            pedido.putExtra("nit",dato.getJSONObject(0).getString("nit"));
                            pedido.putExtra("latitud",dato.getJSONObject(0).getString("latitud"));
                            pedido.putExtra("longitud",dato.getJSONObject(0).getString("longitud"));
                            startActivity(pedido);

                            devuelve="5";
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



            //verificar si tiene un pedido habilitado.
            if (params[1] == "9") {
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
                    jsonParam.put("id_usuario", params[2]);

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
                            JSONArray array=respuestaJSON.getJSONArray("pedido");

                            SharedPreferences ped = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                            SharedPreferences.Editor editar = ped.edit();
                            editar.putString("nombre_moto", array.getJSONObject(0).getString("nombre_moto"));
                            editar.putString("celular", array.getJSONObject(0).getString("celular"));
                            editar.putString("id_moto", array.getJSONObject(0).getString("id_moto"));
                            editar.putString("marca", array.getJSONObject(0).getString("marca"));
                            editar.putString("placa", array.getJSONObject(0).getString("placa"));
                            editar.putString("color", array.getJSONObject(0).getString("color"));
                            editar.putString("latitud", array.getJSONObject(0).getString("latitud"));
                            editar.putString("longitud", array.getJSONObject(0).getString("longitud"));
                            editar.putString("id_pedido",  array.getJSONObject(0).getString("id_pedido"));
                            editar.commit();

                            devuelve="7";
                        } else  {
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


            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           //ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                mMap.clear();
                agregar_en_mapa_ubicaciones_de_moto();
            }
            else
            if(s.equals("3")||s.equals("5")||s.equals("4") )
            {
                Toast.makeText(Menu_p.this,suceso.getMensaje(),Toast.LENGTH_SHORT).show();
            }
            else  if(s.equals("2"))
            {
                Toast.makeText(Menu_p.this,suceso.getMensaje(),Toast.LENGTH_SHORT).show();
            }else if(s.equals("6"))
            {
                cargar_lista_en_direccion();
            }else if(s.equals("7"))
            {

                SharedPreferences ped = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                onStop();
                Intent pedido=new Intent(Menu_p.this,Pedido_us.class);
                pedido.putExtra("nombre",ped.getString("nombre_moto",""));
                pedido.putExtra("celular",ped.getString("celular",""));
                pedido.putExtra("id_moto",ped.getString("id_moto",""));
                pedido.putExtra("marca",ped.getString("marca",""));
                pedido.putExtra("placa",ped.getString("placa",""));
                pedido.putExtra("id_pedido",ped.getString("id_pedido",""));

                Intent intent = new Intent(getApplicationContext(), Servicio_pedido.class);
                intent.setAction(com.grayhartcorp.quevengan.Constants.ACTION_RUN_ISERVICE);
                startService(intent);

                startActivity(pedido);

            }
            else
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

    // comenzar con el servicio de otencion de la lista de direcciones.
    public class Servicio_direccion extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

if(isCancelled()==false && alertDialog.isShowing() ==true) {
    //cargar direcciones de la empresa.
    devuelve = "-1";
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
            jsonParam.put("id_empresa", params[2]);

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

                    JSdirecciones = respuestaJSON.getJSONArray("direccion");
                    devuelve = "6";
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
            //para el progres Dialog
            preparar_progres_dialogo("Asapp","Obteniendo dirección");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog

            Log.e("onPostExcute=", "" + s);
            //ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

              if(s.equals("3"))
            {
                Toast.makeText(Menu_p.this,suceso.getMensaje(),Toast.LENGTH_SHORT).show();
            }else if(s.equals("6"))
            {
                cargar_lista_en_direccion();
            }
            else  if(s.equals("-1"))
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
    // comenzar el servicio pedir motista....
    public class Servicio_pedir_moto extends AsyncTask<String,Integer,String> {
        String id_pedido="";

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

            if(isCancelled()==false && sw_cancelar_pedido==false) {
                //enviar pedir moto..
                if (params[1] == "7") {
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
                        jsonParam.put("id_usuario", params[2]);
                        jsonParam.put("latitud", params[3]);
                        jsonParam.put("longitud", params[4]);
                        jsonParam.put("nombre", params[5]);
                        jsonParam.put("id_empresa", params[6]);
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
                               // id_pedido=respuestaJSON.getString("id_pedido"); //modifico willy esta linea
                                devuelve = "3";
                            } else if (suceso.getSuceso().equals("3")) {

                                //3= ya tiene un pedido en camino;
                                devuelve = "1";
                            } else if(suceso.getSuceso().equals("2")) {
                                devuelve = "2";
                            }
                            else
                            {devuelve = "5";
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
            }else
            {
                devuelve="-1";
            }

            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog
            /*
            preparar_progres_dialogo("Asapp","Llamando Moto . . .");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
            */
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           // alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if(s.equals("3"))
            {
                enviar_notificaciones_de_pedido(id_pedido);
              //  mensaje(suceso.getMensaje());
                try {
                    tv_mensaje_pedido.setText("Buscando  Motista . .");
                    flotante_pedir(true);
                    th_pedido.start();
                } catch (Exception e) {
                    th_pedido = new Thread(new Runnable() {
                        public void run() {
                            for (int i = 0; get_estado_pedido() ==0 && i<180 && sw_verificar_si_tiene_pedido==false && sw_cancelar_pedido==false; i++) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    sw_verificar_si_tiene_pedido=true;
                                    verificar_si_tiene_pedido();
                                }
                            });

                        }

                    });

                    th_pedido.start();
                }
            }else
            if(s.equals("1"))
            {
                flotante_pedir(false);
                mensaje_con_pedido(suceso.getMensaje());
                //levantar el servicion para versi un motist acepto su solicitud
            }
            else if(s.equals("-1"))
            {
                flotante_pedir(true);
                mensaje_error("Se cancelo el pedido.");
            }
            else if(s.equals("2"))
            {   flotante_pedir(false);
                mensaje_error(suceso.getMensaje());
            }
            else
            {
                flotante_pedir(false);
                mensaje_error("Error al conectar con el Servidor.");
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
    // comenzar el servicio pedir motista....
    public class Servicio_de_notificaciones_de_pedido extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

                //enviar notificaicon de pedidos de moto..
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
                        jsonParam.put("id_usuario", params[2]);
                        jsonParam.put("nombre", params[3]);
                        jsonParam.put("id_pedido", params[4]);
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
                            }
                            else
                            {   devuelve = "2";
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
            /*
            preparar_progres_dialogo("Asapp","Llamando Moto . . .");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
            */
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if(s.equals("1")) {
                //  mensaje(suceso.getMensaje());
            }
            else
            {

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
    private void mensaje_con_pedido(String mensaje) {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage(mensaje);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                verificar_si_tiene_pedido_2();

            }
        });
        dialogo1.show();
    }


    public void vaciar_toda_la_base_de_datos() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from tarifa");
        db.execSQL("delete from pedido");
        db.execSQL("delete from direccion");
        db.execSQL("delete from carrera");
        db.execSQL("delete from notificacion");
        db.close();
        Log.e("sqlite ", "vaciar todo");
    }
    public void mensaje(String mensaje)
    {
        Toast toast =Toast.makeText(this,mensaje,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }





    public class Servicio_verificar_pedido extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";


if(isCancelled()==false && alertDialog.isShowing()==true) {

    //verificar si tiene un pedido habilitado.
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
            jsonParam.put("id_usuario", params[2]);

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
                    JSONArray array = respuestaJSON.getJSONArray("pedido");

                    SharedPreferences ped = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                    SharedPreferences.Editor editar = ped.edit();
                    editar.putString("nombre_moto", array.getJSONObject(0).getString("nombre_moto"));
                    editar.putString("celular", array.getJSONObject(0).getString("celular"));
                    editar.putString("id_moto", array.getJSONObject(0).getString("id_moto"));
                    editar.putString("marca", array.getJSONObject(0).getString("marca"));
                    editar.putString("placa", array.getJSONObject(0).getString("placa"));
                    editar.putString("color", array.getJSONObject(0).getString("color"));
                    editar.putString("latitud", array.getJSONObject(0).getString("latitud"));
                    editar.putString("longitud", array.getJSONObject(0).getString("longitud"));
                    editar.putString("id_pedido", array.getJSONObject(0).getString("id_pedido"));
                    editar.putString("estado", array.getJSONObject(0).getString("estado"));
                    editar.commit();

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


            preparar_progres_dialogo("Asapp","Verificando si tiene pedido.");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("respuesta del servidor=", "" + s);

            if(s.equals("1"))
            {
                SharedPreferences ped = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                onStop();
                Intent pedido=new Intent(Menu_p.this,Pedido_us.class);
                pedido.putExtra("nombre",ped.getString("nombre_moto",""));
                pedido.putExtra("celular",ped.getString("celular",""));
                pedido.putExtra("id_moto",ped.getString("id_moto",""));
                pedido.putExtra("marca",ped.getString("marca",""));
                pedido.putExtra("placa",ped.getString("placa",""));
                pedido.putExtra("id_pedido",ped.getString("id_pedido",""));


                Intent intent = new Intent(getApplicationContext(), Servicio_pedido.class);
                intent.setAction(com.grayhartcorp.quevengan.Constants.ACTION_RUN_ISERVICE);
                startService(intent);
                flotante_pedir(false);
                startActivity(pedido);
            }
            else  if(s.equals("2"))
            {
                //Toast.makeText(Menu_p.this,suceso.getMensaje(),Toast.LENGTH_SHORT).show();
                set_estado(1);
            }
            else {
                set_estado(1);
            }
            sw_verificar_si_tiene_pedido=false;


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

    // comenzar el servicio para la conexion con la base de datos.....
    public class Servicio_temporal extends AsyncTask<String,Integer,String> {


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
                    jsonParam.put("id_usuario", params[2]);
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
                          String  id_pedido=respuestaJSON.getString("id_pedido");

                            SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=prefe.edit();
                            editor.putString("id_pedido",id_pedido);
                            editor.commit();

                            Intent intent = new Intent(getApplicationContext(), Servicio_pedido.class);
                            intent.setAction(com.grayhartcorp.quevengan.Constants.ACTION_RUN_ISERVICE);
                            startService(intent);
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
            //ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                Toast.makeText(Menu_p.this,suceso.getMensaje(),Toast.LENGTH_SHORT).show();
            }
            else  if(s.equals("2"))
            {
                Toast.makeText(Menu_p.this,suceso.getMensaje(),Toast.LENGTH_SHORT).show();
            }
            else
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

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }



    private void enviar(String[] to, String[] cc,
                        String asunto, String mensaje) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Email "));
    }



    private void cargar_lista_en_direccion()
    {



        direccion= new ArrayList<CDireccion>();
        SharedPreferences empresa =getSharedPreferences("empresa",MODE_PRIVATE);

        for (int i=0;i<JSdirecciones.length();i++)
        {
            try {
            //el id_usuario nos va a servir para ver si es administrador.
                // si es administrador el valor sera=1.
                // si no es administrador se colocara=0
                CDireccion hi =new CDireccion(Integer.parseInt(JSdirecciones.getJSONObject(i).getString("id")),JSdirecciones.getJSONObject(i).getString("nombre"),JSdirecciones.getJSONObject(i).getString("detalle"),Double.parseDouble(JSdirecciones.getJSONObject(i).getString("latitud")),Double.parseDouble(JSdirecciones.getJSONObject(i).getString("longitud")),JSdirecciones.getJSONObject(i).getString("id_empresa"),JSdirecciones.getJSONObject(i).getString("id_usuario"));
                direccion.add(hi);
            }
            catch (Exception e)
            {
                Log.e("lista","error"+e);
            }
        }

        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        if(perfil.getString("administrador","0").equals("1")==true)
        {
            CDireccion hi =new CDireccion(-1,"Agregar dirección","",0,0,perfil.getString("id_empresa",""), perfil.getString("id_usuario",""));
            direccion.add(hi);
        }
        else {
            // direccion = new String[JSdirecciones.length()];
        }


        verificar_tipo_de_usuario(true);






        final AlertDialog.Builder builder_lista = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();

        final View dialoglayout = inflater.inflate(R.layout.fragment_lista_direccion, null);
        final ListView lv_lista = (ListView) dialoglayout.findViewById(R.id.lista);

        Direccion_item adaptador = new Direccion_item(getApplicationContext(),this,direccion);
        lv_lista.setAdapter(adaptador);


        builder_lista.setView(dialoglayout);
        final AlertDialog alert = builder_lista.create();




        lv_lista.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {
                // TODO Auto-generated method stub
                SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
                if(perfil.getString("administrador","0").equals("1")==true && JSdirecciones.length()==position )
                {


                    if(perfil.getString("id_empresa","").equals("")==false && perfil.getString("id_empresa","").equals("null")==false && perfil.getString("administrador","0").equals("1")==true ) {
                        ultima_direccion.setText("Ubicación actual");
                        Intent buscador = new Intent(getApplicationContext(), Registrar_direccion.class);
                        buscador.putExtra("id_empresa",perfil.getString("id_empresa",""));
                        startActivity(buscador);
                    }else
                    {
                        mensaje("No tienes permiso para agregar Dirección.");
                    }
                }
                else {
                    try {
                        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
                        String titulo=JSdirecciones.getJSONObject(position).getString("nombre");
                        String detalle=usuario.getString("nombre","")+" "+usuario.getString("apellido","");
                        LatLng punto = new LatLng(Double.parseDouble(JSdirecciones.getJSONObject(position).getString("latitud")), Double.parseDouble(JSdirecciones.getJSONObject(position).getString("longitud")));
                        mMap.clear();
                        Marker marker= mMap.addMarker(new MarkerOptions()
                                .position(punto)
                                .title(titulo));


                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(punto)      // Sets the center of the map to Mountain View
                                .zoom(16)                   // Sets the zoom
                                .bearing(0)                // Sets the orientation of the camera to east
                                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        itemp=position;

                        marker.showInfoWindow();

                        ll_llamar.setLayoutParams(parent);
                        ultima_direccion.setText(JSdirecciones.getJSONObject(position).getString("nombre"));


                    }
                    catch (Exception e)
                    {
                        itemp=-1;
                    }
                }

            alert.dismiss();


            }

        });


        alert.show();



    }

    private AlertDialog cargar_lista(ArrayList<CDireccion> direccion) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

           final CharSequence[] items = new CharSequence[direccion.size()];
            for (int i=0;i<direccion.size();i++)
            {
                items[i]=direccion.get(1).toString();
            }

            builder.setTitle("Lista de direcciones")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Seleccionaste:" + items[which],
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

            return builder.create();
    }

    public  void verificar_tipo_de_usuario(boolean sw_click)
    {
        SharedPreferences perfil=getSharedPreferences("perfil",Context.MODE_PRIVATE);


        if(perfil.getString("administrador","0").equals("1")==true && sw_click==true)
        {
            ll_llamar.setLayoutParams(parent);
        }
        else if(perfil.getString("id_empresa","").equals("")==false && sw_click==true)
        {
            ll_llamar.setLayoutParams(cero);
        }
        else if(sw_click==false && perfil.getString("administrador","0").equals("1")==true)
        {
            ll_llamar.setLayoutParams(cero);
        }
        else if(sw_click==false)
        {

            ll_llamar.setLayoutParams(cero);
        }
    }

    //codigo para obtener mi ubicaicon.... INICIO


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
                            status.startResolutionForResult(Menu_p.this, PETICION_CONFIG_UBICACION);
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
        if (ActivityCompat.checkSelfPermission(Menu_p.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, Menu_p.this);
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
            ubicacion.setText("(" + String.valueOf(loc.getLatitude())+","+String.valueOf(loc.getLongitude())+")");
            myPosition = new LatLng(loc.getLatitude(),loc.getLongitude());

            // cargamos los puntos de las motos en nuestro mapa....
            if (sw_acercar_a_mi_ubicacion == 0) {
                //mover la camara del mapa a mi ubicacion.,,
                try {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                    //agregaranimacion al mover la camara...
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(loc.getLatitude(),loc.getLongitude()))     // Sets the center of the map to Mountain View
                            .zoom(16)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } catch (Exception e) {

                }
                sw_acercar_a_mi_ubicacion = 1;
            }


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
    public void onLocationChanged(Location location) {

        Log.i(LOGTAG, "Recibida nueva ubicación!");
     //Mostramos la nueva ubicación recibida
        updateUI(location);

    }
    //codigo para obtener mi ubicaicon.... FIN

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
            if (get_estado() != i) {
                SharedPreferences estado = getSharedPreferences("actividad", MODE_PRIVATE);
                SharedPreferences.Editor editor = estado.edit();
                editor.putInt("estado", i);
                editor.commit();
                if (i == 1) {
                    try {

                        th_pedido.start();
                    } catch (Exception e) {
                        th_pedido = new Thread(new Runnable() {
                            public void run() {
                                for (int i = 0; get_estado_pedido() ==0 && i<180 && sw_verificar_si_tiene_pedido==false && sw_cancelar_pedido==false; i++) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        sw_verificar_si_tiene_pedido=true;
                                        verificar_si_tiene_pedido();
                                    }
                                });

                            }

                        });
                        th_pedido.start();
                    }
                }
            }
    }
    private int get_estado() {
        SharedPreferences estado=getSharedPreferences("actividad",MODE_PRIVATE);
        int est=0;
        try{
            est=estado.getInt("estado",0);
        }catch (Exception e)
        {
            est=0;
        }
        return est;
    }
    private int get_estado_pedido() {
        SharedPreferences estado=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
        int est=0;
        try{
            est=Integer.parseInt(estado.getString("id_pedido","0"));
        }catch (Exception e)
        {
            est=0;
        }
        return est;
    }
    public void verificar_si_tiene_pedido()
    {
        try {
            SharedPreferences ped = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
            int id_pedido=Integer.parseInt(ped.getString("id_pedido",""));
            int estado=0;

            try{estado=ped.getInt("estado",0);}
            catch (Exception e)
            { estado=0;}

            if(id_pedido>0 && estado<2)
            {
                onStop();
                Intent pedido=new Intent(Menu_p.this,Pedido_us.class);
                pedido.putExtra("nombre",ped.getString("nombre_moto",""));
                pedido.putExtra("celular",ped.getString("celular",""));
                pedido.putExtra("id_moto",ped.getString("id_moto",""));
                pedido.putExtra("marca",ped.getString("marca",""));
                pedido.putExtra("placa",ped.getString("placa",""));
                pedido.putExtra("id_pedido",ped.getString("id_pedido",""));

                Intent intent = new Intent(getApplicationContext(), Servicio_pedido.class);
                intent.setAction(com.grayhartcorp.quevengan.Constants.ACTION_RUN_ISERVICE);
                startService(intent);
                flotante_pedir(false);

                startActivity(pedido);

            }

        }catch (Exception e)
        {
            flotante_pedir(false);
        }
        sw_verificar_si_tiene_pedido=false;
    }
    public void verificar_si_tiene_pedido_2()
    {
        SharedPreferences datos_perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        Servicio_verificar_pedido servicio_verificar_pedido = new Servicio_verificar_pedido();
        servicio_verificar_pedido.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedido_por_id_usuario", "1", datos_perfil.getString("id_usuario",""));// parametro que recibe el doinbackground

    }



    //Obtener la imagen del motista..

    /*Inicio de obtencion de datos..*/

    private void getImage(String id,String nombre)
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
    /*Fin de obtencion de datos..*/

   public void preparar_progres_dialogo(String titulo,String mensaje)
   {
       builder_dialogo = new AlertDialog.Builder(this);
       final LayoutInflater inflater = getLayoutInflater();

       final View dialoglayout = inflater.inflate(R.layout.popup_progress_dialog, null);
       final TextView Tv_titulo = (TextView) dialoglayout.findViewById(R.id.tv_titulo);
       final TextView Tv_mensaje = (TextView) dialoglayout.findViewById(R.id.tv_mensaje);
       final ImageView  im_icono=(ImageView)dialoglayout.findViewById(R.id.im_icono);
       im_icono.setBackgroundResource(R.drawable.animacion_icono);
       AnimationDrawable frameAnimation = (AnimationDrawable) im_icono.getBackground();

       // Start the animation (looped playback by default).
       frameAnimation.start();
       Tv_mensaje.setText(mensaje);
       Tv_titulo.setText(titulo);
       builder_dialogo.setView(dialoglayout);
       alertDialog=builder_dialogo.create();
   }

    public void ocultar_flotante()
    {
        ll_flotante.setVisibility(View.INVISIBLE);
    }
    public void enviar_notificaciones_de_pedido(String id_pedido)
    {
        SharedPreferences per = getSharedPreferences("perfil", MODE_PRIVATE);
        String id = per.getString("id_usuario", "");
        String nombre = per.getString("nombre", "");
        nombre = nombre + " " + per.getString("apellido", "");
        Servicio_de_notificaciones_de_pedido servicio_1=new Servicio_de_notificaciones_de_pedido();
        servicio_1.execute(getString(R.string.servidor) + "frmPedido.php?opcion=enviar_notificacion_de_pedido", "1",id,nombre,id_pedido );
    }
}
