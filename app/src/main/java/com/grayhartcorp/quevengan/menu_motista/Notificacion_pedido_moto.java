package com.grayhartcorp.quevengan.menu_motista;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.RadioGroup;

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

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;

import android.widget.PopupWindow;
import android.view.MotionEvent;
import android.support.v4.view.MotionEventCompat;

public class Notificacion_pedido_moto extends FragmentActivity implements OnMapReadyCallback ,View.OnClickListener,OnMarkerClickListener {


        public static final String DEBUG_TAG = "GesturesActivity";
    LayoutInflater layoutInflater;

    private GoogleMap mMap;
    int id_pedido=0;
    Button aceptar;
    TextView tv_nombre,tv_empresa;
    double latitud,longitud;
    String nombre;
    String empresa,direccion;
    String nombre_direccion,detalle_direccion;
    Suceso suceso;

    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;




    View popupView;
    PopupWindow popupWindow;

    Button btn_Cerrar;

    Marker  marker;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,Menu_motista.class));
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_notificacion_pedido_moto);
        aceptar=(Button)findViewById(R.id.aceptar);
        tv_nombre=(TextView)findViewById(R.id.tv_nombre);
        tv_empresa=(TextView)findViewById(R.id.tv_empresa);



        aceptar.setOnClickListener(this);


        try{
            Bundle bundle=getIntent().getExtras();
            id_pedido=Integer.parseInt(bundle.getString("id_pedido"));
            nombre=bundle.getString("nombre");
            empresa=bundle.getString("empresa");
            direccion=bundle.getString("direccion");
            latitud=Double.parseDouble (bundle.getString("latitud"));
            longitud=Double.parseDouble (bundle.getString("longitud"));
            nombre_direccion=bundle.getString("nombre_direccion");
            detalle_direccion=bundle.getString("detalle_direccion");
            tv_nombre.setText(nombre);
            tv_empresa.setText(empresa);

                SharedPreferences ped = getSharedPreferences("ultima_notificacion_pedido", MODE_PRIVATE);
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

        }catch (Exception e)
        {
            finish();
        }



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /*
    * Manipulacion de Touch
    * */





    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Log.d(DEBUG_TAG, "La accion ha sido ABAJO");
                return true;
            case (MotionEvent.ACTION_MOVE):
                Log.d(DEBUG_TAG, "La acción ha sido MOVER");
                return true;
            case (MotionEvent.ACTION_UP):
                /*
                Log.d(DEBUG_TAG, "La acción ha sido ARRIBA");

                layoutInflater =(LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                popupView = layoutInflater.inflate(R.layout.popup, null);
                popupWindow = new PopupWindow(popupView,RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);

                btn_Cerrar = (Button)popupView.findViewById(R.id.bt_aceptar);
                btn_Cerrar.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }});
                    */

              //  popupWindow.showAsDropDown(btn_Abrir_Popup, 50, 0);
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(DEBUG_TAG, "La accion ha sido CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(DEBUG_TAG,
                        "La accion ha sido fuera del elemento de la pantalla");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        LatLng sydney = new LatLng(latitud,longitud);
       marker =mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title(nombre_direccion)
                .snippet(detalle_direccion)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point))
                .anchor((float)0.5,(float)0.5)
                .flat(true)
                .rotation(0));

        marker.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //agregaranimacion al mover la camara...
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitud,longitud))      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.aceptar)
        {
            SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);

          Servicio servicio=new Servicio();
           servicio.execute(getString(R.string.servidor)+"frmPedido.php?opcion=aceptar_pedido", "1",String.valueOf(id_pedido),perfil.getString("id_moto",""));// parametro que recibe el doinbackground

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(this.marker))
        {
            this.marker.showInfoWindow();
        }

        return  false;

    }


    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

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
                    jsonParam.put("id_moto", params[3]);

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

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        // StringBuilder pasando a cadena.                    }

                        SystemClock.sleep(950);

                        //Accedemos a vector de resultados.
                        String error = respuestaJSON.getString("suceso");// suceso es el campo en el Json
                        suceso=new Suceso(error,respuestaJSON.getString("mensaje"));
                        if (error.equals("1")) {



                            JSONArray usu=respuestaJSON.getJSONArray("pedido");
                            String id_pedido=usu.getJSONObject(0).getString("id");
                            String id_usuario=usu.getJSONObject(0).getString("id_usuario");
                            String calificacion=usu.getJSONObject(0).getString("calificacion");
                            String tipo_pedido=usu.getJSONObject(0).getString("tipo_pedido");
                            String fecha=usu.getJSONObject(0).getString("fecha");
                            String mensaje=usu.getJSONObject(0).getString("mensaje");
                            String estado=usu.getJSONObject(0).getString("estado");
                            String nombre_usuario=usu.getJSONObject(0).getString("nombre_usuario");
                            String latitud=usu.getJSONObject(0).getString("latitud");
                            String longitud=usu.getJSONObject(0).getString("longitud");
                            String empresa=usu.getJSONObject(0).getString("empresa");
                            String nombre_direccion=usu.getJSONObject(0).getString("nombre_direccion");
                            String detalle_direccion=usu.getJSONObject(0).getString("detalle_direccion");
                            //iniciar el servicio de pedido en curso
                           // startService(new Intent(getApplicationContext(), Servicio_cargar_punto.class));
                            cargar_pedido_en_curso(id_pedido,id_usuario,calificacion,tipo_pedido,mensaje,fecha,estado,latitud,longitud,nombre_usuario,empresa,nombre_direccion,detalle_direccion);


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
            preparar_progres_dialogo("Asapp","Autenticando. . .");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {

                Intent i=new Intent(getApplicationContext(),Menu_motista.class);
                i.putExtra("id_pedido",String.valueOf(id_pedido));
               // startService(new Intent(getApplicationContext(),Servicio_cargar_punto.class));
                startActivity(i);
                finish();
            }
            else if(s.equals("2"))
            {
                mensaje_error(suceso.getMensaje());
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

    public void mensaje(String mensaje)
    {
        Toast toast =Toast.makeText(this,mensaje,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void cargar_pedido_en_curso(String id_pedido, String id_usuario, String calificacion, String tipo_pedido, String mensaje, String fecha, String estado, String latitud, String longitud, String nombre_usuario,String empresa,String nombre_direccion,String detalle_direccion) {

        SharedPreferences preferencias=getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("id_pedido",id_pedido);
        editor.putString("id_usuario",id_usuario);
        editor.putString("calificacion",calificacion);
        editor.putString("tipo_pedido",tipo_pedido);
        editor.putString("mensaje",mensaje);
        editor.putString("fecha",fecha);
        editor.putString("latitud",latitud);
        editor.putString("longitud",longitud);
        editor.putString("nombre_usuario",nombre_usuario);
        editor.putString("empresa",empresa);
        editor.putString("nombre_direccion",nombre_direccion);
        editor.putString("detalle_direccion",detalle_direccion);
        editor.putString("detalle_direccion",detalle_direccion);
        editor.putInt("notificacion_distancia_cercana",0);
        editor.putInt("notificacion_distancia_llego_la_moto",0);


        editor.putString("estado",estado);
        editor.commit();

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

    public void mostrar_popup()
    {

    }

    private void set_estado(int i) {
        SharedPreferences estado=getSharedPreferences("actividad",MODE_PRIVATE);
        SharedPreferences.Editor editor=estado.edit();
        editor.putInt("estado",i);
        editor.commit();
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
