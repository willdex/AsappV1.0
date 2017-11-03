package com.grayhartcorp.quevengan.direccion;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.grayhartcorp.quevengan.Integrantes_empresa;
import com.grayhartcorp.quevengan.Menu_p;
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.Suceso;
import com.grayhartcorp.quevengan.menu_motista.Carrera;
import com.grayhartcorp.quevengan.tarifa.CTarifa;
import com.grayhartcorp.quevengan.tarifa.Items_tarifa;
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
import java.util.ArrayList;

public class Mis_direcciones extends AppCompatActivity {

    ListView lista_direccion;
    ArrayList<CDireccion> direccion = new ArrayList<CDireccion>();
    private Servicio hilo_mis_direcciones;

    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;

    public Suceso suceso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_mis_direcciones);
        lista_direccion=(ListView)findViewById(R.id.lista_direccion);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    cargar_direccion_en_la_lista();




        lista_direccion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CDireccion hi=new CDireccion();
                hi=direccion.get(i);
                mensaje(hi);

            }
        });
    }

    public void mensaje(CDireccion direccion)
    {
        Toast.makeText(this,"Direccion :"+direccion.getDetalle(),Toast.LENGTH_SHORT).show();
        Intent carrerra=new Intent(this,Carrera.class);
        carrerra.putExtra("detalle",direccion.getDetalle());
        carrerra.putExtra("latitud",direccion.getLatitud());
        carrerra.putExtra("longitud",direccion.getLongitud());
        startActivity(carrerra);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actualizar:
                SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
                String id=prefe.getString("id_usuario","");
                hilo_mis_direcciones = new Servicio();
                String ip=getString(R.string.servidor);
                hilo_mis_direcciones.execute(ip+"frmDireccion.php?opcion=get_direccion_por_id_usuario", "1",id);// parametro que recibe el doinbackground
                Log.i("Item", "actualizar!");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tarifa,menu);
        return true;
    }

    // comenzar el servicio con el direcciones....
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
                        // vacia los datos que estan registrados en nuestra base de datos SQLite..
                        vaciar_direccion();
                        if (suceso.getSuceso().equals("1")) {
                            JSONArray usu=respuestaJSON.getJSONArray("direccion");
                            for (int i=0;i<usu.length();i++)
                            {
                                int id=Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                String detalle=usu.getJSONObject(i).getString("detalle");
                                String id_empresa=usu.getJSONObject(i).getString("id_empresa");
                                String nombre=usu.getJSONObject(i).getString("nombre");
                                String id_usuario=usu.getJSONObject(i).getString("id_usuario");
                                double latitud=Double.parseDouble(usu.getJSONObject(i).getString("latitud"));
                                double longitud=Double.parseDouble(usu.getJSONObject(i).getString("longitud"));

                                cargar_lista_en_direccion(id,nombre, detalle,latitud,longitud,id_empresa,id_usuario);
                            }

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
                cargar_direccion_en_la_lista();
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


    private void actualizar_lista() {
        Items_mis_direcciones adaptador = new Items_mis_direcciones(getApplicationContext(),this,direccion);
        lista_direccion.setAdapter(adaptador);
    }

    private void cargar_lista_en_direccion(int id,String nombre,String detalle,double latitud,double longitud,String id_empresa,String id_usuario)
    {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("id", id);
        registro.put("nombre",nombre);
        registro.put("detalle",detalle);
        registro.put("latitud", latitud);
        registro.put("longitud", longitud);
        registro.put("id_empresa", id_empresa);
        bd.insert("direccion", null, registro);
        bd.close();
    }
    private void cargar_direccion_en_la_lista()
    {
        direccion= new ArrayList<CDireccion>();
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    "easymoto", null, 1);
            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = bd.rawQuery("select * from direccion", null);

            if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

                do {
                    CDireccion hi = new CDireccion(Integer.parseInt(fila.getString(0)), fila.getString(6), fila.getString(1), Double.parseDouble(fila.getString(2)), Double.parseDouble(fila.getString(3)), fila.getString(4), fila.getString(5));
                    direccion.add(hi);
                } while (fila.moveToNext());

            } else
                Toast.makeText(this, "No hay registrados",
                        Toast.LENGTH_SHORT).show();

            bd.close();
            actualizar_lista();
        }catch (Exception e)
        {}
    }

    private void vaciar_direccion()
    {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from direccion");
        db.close();
        Log.e("sqlite ", "vaciar direccion");
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