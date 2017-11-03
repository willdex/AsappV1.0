package com.grayhartcorp.quevengan.contactos;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Html;
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

public class Contactos_local extends AppCompatActivity implements  SearchView.OnQueryTextListener{
    ListView lista_buscar;
    Suceso suceso;
    JSONArray JS_contactos;

    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;


    ArrayList<CContacto> contacto ;

    private String id_usuario,id_empresa;
    String nombre,celular;
    private int item;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actualizar:
                cargar_json_en_lista("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contactos, menu);
//se agregar la cabecera. con su busqueda
        //se agregar la cabecera. con su busqueda
        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

       return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        cargar_json_en_lista( newText);

        return false;
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

        setContentView(R.layout.activity_agregar_contactos);
        lista_buscar=(ListView)findViewById(R.id.lista_busqueda);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

// evento de onclick en la Lista de Busqueda ...
        lista_buscar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                item=i;

                SharedPreferences prefe=getSharedPreferences("empresa", Context.MODE_PRIVATE);
                id_empresa=prefe.getString("id","");

                nombre=contacto.get(i).getNombre();
                celular=contacto.get(i).getNumero();
                int lon=celular.length();
                if(lon>=8) {
                    celular = celular.substring(lon - 8, lon);
                }

                final AlertDialog.Builder dialogo = new AlertDialog.Builder(Contactos_local.this);

                final LayoutInflater inflater = getLayoutInflater();

                final View dialoglayout = inflater.inflate(R.layout.popup_dialogo_aceptar_cancelar, null);
                final TextView Tv_titulo = (TextView) dialoglayout.findViewById(R.id.tv_titulo);
                final TextView Tv_mensaje = (TextView) dialoglayout.findViewById(R.id.tv_mensaje);
                final Button Bt_aceptar = (Button) dialoglayout.findViewById(R.id.bt_aceptar);
                final Button Bt_cancelar = (Button) dialoglayout.findViewById(R.id.bt_cancelar);



                Tv_mensaje.setText(Html.fromHtml("<font>¿Agregar <b>"+contacto.get(i).getNombre()+"</b>  con número de celular <b>"+celular+"</b> como usuario de su Empresa?</font>"));
                Tv_titulo.setText("Asapp");
                Bt_aceptar.setText("SI");
                Bt_cancelar.setText("NO");
                dialogo.setView(dialoglayout);
                dialogo.setCancelable(false);


                final AlertDialog finalDialogo =dialogo.create();
                finalDialogo.show();
                Bt_aceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Servicio servicio=new Servicio();
                        servicio.execute(getString(R.string.servidor)+"frmUsuario.php?opcion=insertar_usuario_por_administrador", "1",nombre,celular,id_empresa);
                        finalDialogo.dismiss();
                    }
                });
                Bt_cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         finalDialogo.dismiss();
                    }
                });

            }
        });
        cargar_json_en_lista("");
    }






    // comenzar el servicio con el direcciones....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "-1";
if(isCancelled()==false && alertDialog.isShowing()==true) {

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
            jsonParam.put("nombre", params[2]);
            jsonParam.put("telefono", params[3]);
            jsonParam.put("id_empresa", params[4]);

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
                    id_usuario=respuestaJSON.getString("id_usuario");
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
}
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog

            preparar_progres_dialogo("Asapp","Registrando contacto como usuario de la empresa.");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

           if(s.equals("2"))
            {
                mensaje_error(suceso.getMensaje());
            }
            else if(s.equals("3"))
            {
                cargar_lista_en_contacto(Integer.parseInt(id_usuario),nombre,celular);
            }
            else if(s.equals("")==true)
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

    private void cargar_lista_en_contacto(int id, String nombre, String telefono) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("id", id);
        registro.put("nombre",nombre);
        registro.put("telefono",telefono);
        bd.insert("contacto", null, registro);
        bd.close();
       onBackPressed();
    }
    public  void cargar_json_en_lista(String texto)
        {
            contacto= new ArrayList<CContacto>();
        String[] projeccion = new String[] { ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE };
        String selectionClause = ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND LENGTH("+
                ContactsContract.CommonDataKinds.Phone.NUMBER+")>=8 AND "+
                ContactsContract.Data.DISPLAY_NAME +" LIKE '%"+texto+"%' OR "+
                ContactsContract.CommonDataKinds.Phone.NUMBER+" LIKE '%"+texto+"%' AND "+
                ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC LIMIT 30";

        Cursor c = getApplicationContext().getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                projeccion,
                selectionClause,
                null,
                sortOrder);



        while(c.moveToNext()){
            Drawable myDrawable = getResources().getDrawable(R.mipmap.ic_perfil);
            CContacto con = new CContacto(""+c.getString(0),c.getString(1),c.getString(2),myDrawable);
            boolean sw=false;
            int lon=c.getString(2).length();
            if(lon>=8)
            { if(existe_contacto(c.getString(2).substring(lon-8,lon))==false) {
                try {

                    int numero = Integer.parseInt(c.getString(2).substring(lon - 8, lon));
                    sw = true;
                } catch (Exception e) {
                    sw = false;
                }
                if (sw == true) {
                    contacto.add(con);
                }
               }
            }

        }
            c.close();
            Lista_contactos adaptador = new Lista_contactos(this,contacto,getString(R.string.servidor),0);
            lista_buscar.setAdapter(adaptador);


        /*contactsCursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,   // URI de contenido para los contactos
                projection,                        // Columnas a seleccionar
                selectionClause                    // Condición del WHERE
                selectionArgs,                     // Valores de la condición
                sortOrder);                        // ORDER BY columna [ASC|DESC]*/


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

    public boolean existe_contacto(String telefono)
    {
        boolean sw_existe=false;

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from contacto where telefono='"+telefono+"' ", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
            sw_existe=true;
        } else
        {
            sw_existe=false;
        }
        bd.close();
        return  sw_existe;
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
