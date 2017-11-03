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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
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

import com.grayhartcorp.quevengan.Menu_p;
import com.grayhartcorp.quevengan.Pedir_moto;
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.Suceso;
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

public class Direccion_em extends AppCompatActivity implements SearchView.OnQueryTextListener{
    ListView lista_buscar;
    Suceso suceso;

    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;


    ArrayList<CDireccion> direccion ;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        SharedPreferences empresa=getSharedPreferences("empresa", Context.MODE_PRIVATE);
        switch (item.getItemId()) {

            case R.id.agregar:
                Intent i=new Intent(this,Registrar_direccion.class);
                String id_=empresa.getString("id","0");
                i.putExtra("id_empresa",id_);
                startActivity(i);
                return true;
            case R.id.actualizar:
               actualizar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        actualizar();
        super.onStart();
    }

    private void actualizar() {
        SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String id_empresa=prefe.getString("id_empresa","");
        Servicio servicio= new Servicio();
        servicio.execute(getString(R.string.servidor)+"frmDireccion.php?opcion=get_direccion_por_id_empresa", "1",id_empresa);// parametro que recibe el doinbackground

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_direccion, menu);
//se agregar la cabecera. con su busqueda
        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_direccion_em);
        lista_buscar=(ListView)findViewById(R.id.lista_busqueda);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

// evento de onclick en la Lista de Busqueda ...
        lista_buscar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CDireccion hi=new CDireccion();
                hi=direccion.get(i);
                mensaje(hi);

            }
        });

    }




    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        cargar_direccion_en_la_lista( newText);

        return false;
    }


    public void mensaje(CDireccion direccion)
    {
        //Intent carrerra=new Intent(this,Pedir_moto.class);
        Intent carrerra=new Intent(this,Editar_direccion.class);
        carrerra.putExtra("id_direccion",String.valueOf(direccion.getId()));
        carrerra.putExtra("nombre",direccion.getNombre());
        carrerra.putExtra("detalle",direccion.getDetalle());
        carrerra.putExtra("latitud",String.valueOf(direccion.getLatitud()));
        carrerra.putExtra("longitud",String.valueOf(direccion.getLongitud()));
        startActivity(carrerra);
    }

    public void cargar_direccion_en_la_lista(String nombre)
    {
        direccion= new ArrayList<CDireccion>();
        SharedPreferences empresa =getSharedPreferences("empresa",MODE_PRIVATE);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("SELECT id ,detalle,latitud, longitud,id_empresa,id_usuario,CASE nombre WHEN '' THEN 'Sin nombre' ELSE nombre  END nombre FROM direccion WHERE id_empresa="+empresa.getString("id","-1")+" and nombre LIKE '%"+nombre+"%' ORDER BY id DESC", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {
                CDireccion hi =new CDireccion(Integer.parseInt(fila.getString(0)),fila.getString(6),fila.getString(1),Double.parseDouble(fila.getString(2)),Double.parseDouble(fila.getString(3)),fila.getString(4), fila.getString(5));
                direccion.add(hi);
            } while(fila.moveToNext());

        } else
            Toast.makeText(this, "No hay registrados" ,
                    Toast.LENGTH_SHORT).show();

        bd.close();
        Items_mis_direcciones adaptador = new Items_mis_direcciones(getApplicationContext(),this,direccion);
        lista_buscar.setAdapter(adaptador);
    }



    // comenzar el servicio con el direcciones....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
            if(isCancelled()==false && alertDialog.isShowing()==true) {

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
                                // vacia los datos que estan registrados en nuestra base de datos SQLite..
                                vaciar_direccion();
                                JSONArray usu = respuestaJSON.getJSONArray("direccion");
                                for (int i = 0; i < usu.length(); i++) {
                                    int id = Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                    String detalle = usu.getJSONObject(i).getString("detalle");
                                    String id_empresa = usu.getJSONObject(i).getString("id_empresa");
                                    String nombre = usu.getJSONObject(i).getString("nombre");
                                    String id_usuario = usu.getJSONObject(i).getString("id_usuario");
                                    double latitud = Double.parseDouble(usu.getJSONObject(i).getString("latitud"));
                                    double longitud = Double.parseDouble(usu.getJSONObject(i).getString("longitud"));

                                    cargar_lista_en_direccion(id, nombre, detalle, latitud, longitud, id_empresa, id_usuario);
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
                cargar_direccion_en_la_lista("");
            }
            else if(s.equals("2"))
            {
                Toast.makeText(getApplicationContext(),suceso.getMensaje(),Toast.LENGTH_SHORT).show();
                cargar_direccion_en_la_lista("");
            }
            else
            { cargar_direccion_en_la_lista( "");
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



    private void vaciar_direccion()
    {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from direccion");
        db.close();
        Log.e("sqlite ", "vaciar direccion");
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
        registro.put("id_empresa",id_empresa);
        bd.insert("direccion", null, registro);
        bd.close();
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
