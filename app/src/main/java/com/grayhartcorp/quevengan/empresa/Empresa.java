package com.grayhartcorp.quevengan.empresa;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.Menu_p;
import com.grayhartcorp.quevengan.Pedido_us;
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.Suceso;
import com.grayhartcorp.quevengan.contactos.Contacto_em;
import com.grayhartcorp.quevengan.direccion.Direccion_em;

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

public class Empresa extends AppCompatActivity implements View.OnClickListener {
   EditText nit,direccion,telefono,razon_social;
    TextView nombre_empresa;

    boolean click_direccion,click_telefono,click_razon_social;
    ImageButton btcontactos,btdireccion;

    Suceso suceso;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_empresa);
        nit=(EditText)findViewById(R.id.nit);
        direccion=(EditText)findViewById(R.id.direccion);
        telefono=(EditText)findViewById(R.id.telefono);
        razon_social=(EditText)findViewById(R.id.razon_social);

        nombre_empresa=(TextView)findViewById(R.id.nombre_empresa);

        btcontactos=(ImageButton)findViewById(R.id.btcontactos);
        btdireccion=(ImageButton)findViewById(R.id.btdirecciones);


        btcontactos.setOnClickListener(this);
        btdireccion.setOnClickListener(this);

        click_direccion=click_telefono=click_razon_social=false;

        try{
            Bundle get=getIntent().getExtras();


            SharedPreferences empresa=getSharedPreferences("empresa",MODE_PRIVATE);
            SharedPreferences.Editor editar=empresa.edit();
            editar.putString("id",get.getString("id"));
            editar.putString("nombre",get.getString("nombre"));
            editar.putString("direccion",get.getString("direccion"));
            editar.putString("telefono",get.getString("telefono"));
            editar.putString("razon_social",get.getString("razon_social"));
            editar.putString("nit",get.getString("nit"));
            editar.putString("latitud",get.getString("latitud"));
            editar.putString("longitud",get.getString("longitud"));
            editar.commit();

            actualizar_edit();

        }catch (Exception e)
        {
            finish();
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void actualizar_edit() {
        SharedPreferences empresa=getSharedPreferences("empresa",MODE_PRIVATE);
        nombre_empresa.setText(empresa.getString("nombre",""));
        nit.setText(empresa.getString("nit",""));
        direccion.setText(empresa.getString("direccion",""));
        telefono.setText(empresa.getString("telefono",""));
        razon_social.setText(empresa.getString("razon_social",""));
    }

    @Override
    public void onClick(View view) {
        SharedPreferences empresa=getSharedPreferences("empresa",MODE_PRIVATE);
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        String sdireccion=this.direccion.getText().toString();
        String stelefono=this.telefono.getText().toString();
        String srazon=this.razon_social.getText().toString();
        switch (view.getId())
        {


            case R.id.btcontactos:
                startActivity(new Intent(this,Contacto_em.class));
                break;
            case R.id.btdirecciones:
                startActivity(new Intent(this, Direccion_em.class));
                break;
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
                    jsonParam.put("id_empresa", params[2]);
                    jsonParam.put("id_administrador", params[3]);
                    jsonParam.put("direccion", params[4]);
                    jsonParam.put("telefono", params[5]);
                    jsonParam.put("razon_social", params[6]);
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
                SharedPreferences  empresa=getSharedPreferences("empresa",MODE_PRIVATE);
                SharedPreferences.Editor editor=empresa.edit();
                editor.putString("direccion",direccion.getText().toString());
                editor.putString("telefono",telefono.getText().toString());
                editor.putString("razon_social",razon_social.getText().toString());
                editor.commit();


            }
            else  if(s.equals("2"))
            {
                mensaje(suceso.getMensaje());
            }
            else
            {
                mensaje_error("Error: Al conectar con el servidor.");
            }
            //actuliza todos los edit...  con el nuevo datos ontenido en el share Preferences,,,,,
            actualizar_edit();

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
