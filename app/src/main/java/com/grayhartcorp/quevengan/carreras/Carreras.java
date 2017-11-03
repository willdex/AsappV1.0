package com.grayhartcorp.quevengan.carreras;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
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
import java.util.ArrayList;

/**
 * Created by ELIO on 15/11/2016.
 */


public class Carreras extends AppCompatActivity {
    ListView lista_carrera;
    ArrayList<CCarrera> carrera;

    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;

    private Servicio hilo_carrera;
    private Suceso suceso;
    int id_pedido;
    Bundle save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.save=savedInstanceState;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_carrera_listview);
        lista_carrera = (ListView) findViewById(R.id.lista_carreras);
try{
    Bundle bundle = getIntent().getExtras();
    id_pedido = Integer.parseInt(bundle.getString("id_pedido"));
   cargar_carrera_en_la_lista(id_pedido);
}catch (Exception e)
{
  //  finish();
}
        lista_carrera.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
             /*   CCarrera hi=new CCarrera();
                hi=carrera.get(i);
                mensaje(hi);
*/
            }
        });


actualizar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actualizar:
        //por el lado del Perfil del usuario.
               actualizar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tarifa, menu);
        return true;
    }

    public void actualizar()
    {
        SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String id=prefe.getString("id_usuario","");
        if(prefe.getString("login_usuario","").equals("1")==true && (id.equals("")==false))
        {

            hilo_carrera = new Servicio();
            hilo_carrera.execute(getString(R.string.servidor) + "frmCarrera.php?opcion=lista_de_carrera_por_usuario", "1", id,String.valueOf(this.id_pedido));// parametro que recibe el doinbackground
            Log.e("Item", "actualizar!.. usuario");

        }
        else if(prefe.getString("login_moto","").equals("1")==true)
        {
            id=prefe.getString("id_moto","");
            hilo_carrera = new Servicio();
            hilo_carrera.execute(getString(R.string.servidor) + "frmCarrera.php?opcion=lista_de_carrera_por_moto", "2", id,String.valueOf(this.id_pedido));// parametro que recibe el doinbackground

            Log.e("Item", "actualizar!.. moto");
        }

    }

    // comenzar el servicio con el motista....
    public class Servicio extends AsyncTask<String, Integer, String> {


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
                    jsonParam.put("id_usuario",params[2]);
                    jsonParam.put("id_pedido",params[3]);

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
                        vaciar_carrera(id_pedido);
                        if (suceso.getSuceso().equals("1")) {
                            JSONArray usu = respuestaJSON.getJSONArray("carrera");

                            for (int i = 0; i < usu.length(); i++) {
                                String id=usu.getJSONObject(i).getString("id");
                                double latitud_inicio=Double.parseDouble(usu.getJSONObject(i).getString("latitud_inicio"));
                                double longitud_inicio=Double.parseDouble(usu.getJSONObject(i).getString("longitud_inicio"));
                                double latitud_fin=Double.parseDouble(usu.getJSONObject(i).getString("latitud_fin"));
                                double longitud_fin=Double.parseDouble(usu.getJSONObject(i).getString("longitud_fin"));
                                double monto=Double.parseDouble(usu.getJSONObject(i).getString("monto"));
                                String detale_inicio=usu.getJSONObject(i).getString("detalle_inicio");
                                String detalle_fin=usu.getJSONObject(i).getString("detalle_fin");
                                String distancia=usu.getJSONObject(i).getString("distancia");
                                String fecha_inicio=usu.getJSONObject(i).getString("fecha_inicio");
                                String fecha_fin=usu.getJSONObject(i).getString("fecha_fin");
                                String opciones=usu.getJSONObject(i).getString("opciones");
                                String id_pedido=usu.getJSONObject(i).getString("id_pedido");
                                String id_usuario=usu.getJSONObject(i).getString("id_usuario");
                                String id_moto=usu.getJSONObject(i).getString("id_moto");
                                String ruta=usu.getJSONObject(i).getString("ruta");
                                cargar_lista_en_carrera( id, latitud_inicio, longitud_inicio, latitud_fin, longitud_fin, monto, detale_inicio, detalle_fin, distancia, fecha_inicio, fecha_fin, opciones, id_pedido, id_usuario, id_moto,ruta);


                            }
                            Log.e("Carrera","Finalizo de cargar las carreras.");

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
                    jsonParam.put("id_moto",params[2]);
                    jsonParam.put("id_pedido",params[3]);


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
                        vaciar_carrera(id_pedido);
                        if (suceso.getSuceso().equals("1")) {
                            JSONArray usu = respuestaJSON.getJSONArray("carrera");
                            for (int i = 0; i < usu.length(); i++) {
                                String id=usu.getJSONObject(i).getString("id");
                                double latitud_inicio=Double.parseDouble(usu.getJSONObject(i).getString("latitud_inicio"));
                                double longitud_inicio=Double.parseDouble(usu.getJSONObject(i).getString("longitud_inicio"));
                                double latitud_fin=Double.parseDouble(usu.getJSONObject(i).getString("latitud_fin"));
                                double longitud_fin=Double.parseDouble(usu.getJSONObject(i).getString("longitud_fin"));
                                double monto=Double.parseDouble(usu.getJSONObject(i).getString("monto"));
                                String detale_inicio=usu.getJSONObject(i).getString("detalle_inicio");
                                String detalle_fin=usu.getJSONObject(i).getString("detalle_fin");
                                String distancia=usu.getJSONObject(i).getString("distancia");
                                String fecha_inicio=usu.getJSONObject(i).getString("fecha_inicio");
                                String fecha_fin=usu.getJSONObject(i).getString("fecha_fin");
                                String opciones=usu.getJSONObject(i).getString("opciones");
                                String id_pedido=usu.getJSONObject(i).getString("id_pedido");
                                String id_usuario=usu.getJSONObject(i).getString("id_usuario");
                                String id_moto=usu.getJSONObject(i).getString("id_moto");
                                String ruta=usu.getJSONObject(i).getString("ruta");
                                cargar_lista_en_carrera( id, latitud_inicio, longitud_inicio, latitud_fin, longitud_fin, monto, detale_inicio, detalle_fin, distancia, fecha_inicio, fecha_fin, opciones, id_pedido, id_usuario, id_moto,ruta);


                            }
                            Log.e("Carrera","Finalizo de cargar las carreras.");


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
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog



            preparar_progres_dialogo("Asapp","Cargando las rutas.");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                cargar_carrera_en_la_lista(id_pedido);
            } else if(s.equals("2"))
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

    private void cargar_lista_en_carrera(String id, double latitud_inicio, double longitud_inicio, double latitud_fin, double longitud_fin, double monto, String detale_inicio, String detalle_fin, String distancia, String fecha_inicio, String fecha_fin, String opciones, String id_pedido, String id_usuario, String id_moto,String ruta) {


        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
                registro.put("id",id);
                registro.put("detale_inicio",detale_inicio);
                registro.put("latitud_inicio",latitud_inicio);
                registro.put("longitud_inicio",longitud_inicio);
                registro.put("detalle_fin",detalle_fin);
                registro.put("latitud_fin",latitud_fin);
                registro.put("longitud_fin",longitud_fin);
                registro.put("distancia",distancia);
                registro.put("opciones",opciones);
                registro.put("fecha_inicio",fecha_inicio);
                registro.put("fecha_fin",fecha_fin);
                registro.put("id_pedido",id_pedido);
                registro.put("id_usuario",id_usuario);
                registro.put("id_moto",id_moto);
                registro.put("monto",monto);
                registro.put("ruta",ruta);
        bd.insert("carrera", null, registro);
        bd.close();
    }

    public void cargar_lista_en_ruta( double latitud, double longitud,String id_pedido,String id_carrera,int numero ) {


        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("latitud",latitud);
        registro.put("longitud",longitud);
        registro.put("id_pedido",id_pedido);
        registro.put("id_carrera",id_carrera);
        registro.put("numero",String.valueOf(numero));
        bd.insert("puntos_carrera", null, registro);
        bd.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void actualizar_lista() {

        Items_carreras adaptador = new Items_carreras(Carreras.this,save,this, carrera);
        lista_carrera.setAdapter(adaptador);



    }



    public void cargar_carrera_en_la_lista( int id_pedido) {
        carrera = new ArrayList<CCarrera>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select id,latitud_inicio,longitud_inicio,latitud_fin,longitud_fin,detale_inicio,detalle_fin,distancia,fecha_inicio,fecha_fin,opciones,id_pedido,id_usuario,id_moto,monto,ruta from carrera where id_pedido="+id_pedido+" ORDER BY id ASC ", null);
int numero=1;
        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

           /*
            "id integer," +
                "detale_inicio text," +
                "latitud_inicio decimal(13,7)," +
                "longitud_inicio decimal(13,7)," +
                "detalle_fin text," +
                "latitud_fin decimal(13,7) default 0," +
                "longitud_fin decimal(13,7) default 0," +
                "distancia decimal(10,3)," +
                "opciones integer," +
                "fecha_inicio text," +
                "fecha_fin text," +
                "id_pedido integer," +
                "id_usuario integer," +
                "id_moto integer," +
                "monto decimal(10,2)," +
                "ruta text" +
           */


            do {
                CCarrera hi = new CCarrera(Integer.parseInt(fila.getString(0)), Double.parseDouble(fila.getString(1)), Double.parseDouble(fila.getString(2)), Double.parseDouble(fila.getString(3)), Double.parseDouble(fila.getString(4)),String.valueOf(fila.getString(5)),String.valueOf(fila.getString(6)),String.valueOf(fila.getString(7)),String.valueOf(fila.getString(8)),String.valueOf(fila.getString(9)),Integer.parseInt(fila.getString(10)),Integer.parseInt(fila.getString(11)),Integer.parseInt(fila.getString(12)),Integer.parseInt(fila.getString(13)),Double.parseDouble(fila.getString(14)),String.valueOf(fila.getString(15)),String.valueOf(numero));
                carrera.add(hi);
                numero++;
            } while (fila.moveToNext());

        } else
            Toast.makeText(this, "No hay registrados",
                    Toast.LENGTH_SHORT).show();

        bd.close();
        actualizar_lista();
    }

    public void vaciar_carrera(int id_pedido) {

        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    "easymoto", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("carrera", "id_pedido="+id_pedido, null);
            db.close();
            Log.e("sqlite ", "vaciar todas las carrera id_pedido=" + id_pedido);
        } catch (Exception e)
        {
            Log.e("sqlite ", "Error : vaciar todas las carreras por id_pedido= " + id_pedido+" . "+e);
        }
    }
    public void vaciar_ruta() {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from puntos_carrera");
        db.close();
        Log.e("sqlite ", "vaciar Puntos de carrera");
    }

    public void mensaje(CCarrera historial)
    {

        Intent i=new Intent(Carreras.this,Detalle_carrera.class);
        i.putExtra("id_carrera",String.valueOf(historial.getId()));
        i.putExtra("latitud_inicio",String.valueOf(historial.getLatitud_inicio()));
        i.putExtra("longitud_inicio",String.valueOf(historial.getLongitud_inicio()));
        i.putExtra("latitud_fin",String.valueOf(historial.getLatitud_fin()));
        i.putExtra("longitud_fin",String.valueOf(historial.getLongitud_fin()));
        i.putExtra("monto",String.valueOf(historial.getMonto()));
        i.putExtra("detalle_inicio",String.valueOf(historial.getDetale_inicio()));
        i.putExtra("detalle_fin",String.valueOf(historial.getDetalle_fin()));
        i.putExtra("distancia",String.valueOf(historial.getDistancia()));
        i.putExtra("fecha_inicio",String.valueOf(historial.getFecha_inicio()));
        i.putExtra("fecha_fin",String.valueOf(historial.getFecha_fin()));
        i.putExtra("opciones",String.valueOf(historial.getOpciones()));
        i.putExtra("id_usuario",String.valueOf(historial.getId_usuario()));
        i.putExtra("id_moto",String.valueOf(historial.getId_moto()));
        i.putExtra("id_ruta",String.valueOf(historial.getRuta()));
        startActivity(i);
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

