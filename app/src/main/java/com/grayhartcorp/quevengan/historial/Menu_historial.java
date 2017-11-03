package com.grayhartcorp.quevengan.historial;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.Menu_p;
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.Suceso;
import com.grayhartcorp.quevengan.carreras.Carreras;
import com.grayhartcorp.quevengan.empresa.Empresa;

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
import java.util.Calendar;

public class Menu_historial extends AppCompatActivity {

    float initialX, initialY;

    private static final int Date_id = 0;
    TextView toolbarTitle,tv_mensaje_error;
    int dia,mes,anio;



    private Servicio hilo_pedido;

    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;


    public Suceso suceso;
    Calendar c_hoy;


    ListView lista_historial;
    ArrayList<CHistorial> historial = new ArrayList<CHistorial>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_historial);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        Calendar c = Calendar.getInstance();
        c_hoy=Calendar.getInstance();

        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH)+1;
        dia = c.get(Calendar.DAY_OF_MONTH);
        toolbarTitle= (TextView) findViewById(R.id.toolbarTitle);
        tv_mensaje_error=(TextView)findViewById(R.id.tv_mensaje_historial);

        if(c_hoy.get(Calendar.YEAR)==anio && c_hoy.get(Calendar.MONTH)+1==mes && c_hoy.get(Calendar.DAY_OF_MONTH)==dia) {
            toolbarTitle.setText("Hoy");
        }
        else
        {
            toolbarTitle.setText(dia + " de " + mes_numero_a_letra(mes) + " del " + anio);
        }

        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("menu","Click");  // DO SOMETHING HERE
                showDialog(Date_id);
            }
        });





        lista_historial=(ListView)findViewById(R.id.lista_historial);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //   cargar_historial_en_la_lista();

        lista_historial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CHistorial hi=new CHistorial();
                hi=historial.get(i);
                mensaje(hi);

            }
        });

        actualizar(dia,mes,anio);
    }


    //metodos para los dialogos de hora y fecha
    protected Dialog onCreateDialog(int id) {

        // Get the calander
        Calendar c = Calendar.getInstance();

        // From calander get the year, month, day, hour, minute
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH);
        dia = c.get(Calendar.DAY_OF_MONTH);

        switch (id) {
            case Date_id:

                // Open the datepicker dialog
                return new DatePickerDialog(Menu_historial.this, date_listener, anio,mes, dia);

        }
        return null;
    }



    // Date picker dialog
    DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // store the data in one string and set it to text
            dia=day;
            mes=month+1;
            anio=year;


            if(c_hoy.get(Calendar.YEAR)==anio && c_hoy.get(Calendar.MONTH)+1==mes && c_hoy.get(Calendar.DAY_OF_MONTH)==dia) {
                toolbarTitle.setText("Hoy");
            }
            else
            {
                toolbarTitle.setText(dia + " de " + mes_numero_a_letra(mes) + " del " + anio);
            }

            actualizar(dia,mes,anio);
        }
    };

    public String mes_numero_a_letra(int i)
    {
        String mes="";
        switch (i)
        {
            case 1: mes="Enero"; break;
            case 2: mes="Febrero"; break;
            case 3: mes="Marzo"; break;
            case 4: mes="Abril"; break;
            case 5: mes="Mayo"; break;
            case 6: mes="Junio"; break;
            case 7: mes="Julio"; break;
            case 8: mes="Agosto"; break;
            case 9: mes="Septiembre"; break;
            case 10: mes="Octubre"; break;
            case 11: mes="Noviembre"; break;
            case 12: mes="Diciembre"; break;
        }

        return  mes;
    }




    public void mensaje(CHistorial historial)
    {
        Intent i=new Intent(this,Carreras.class);

        i.putExtra("id_pedido",String.valueOf(historial.getId_pedido()));
        startActivity(i);
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
                actualizar(dia,mes,anio);
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

    public void actualizar(int dia,int mes,int anio) {
        SharedPreferences perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        if (perfil.getString("administrador", "0").equals("1") == true) {

            SharedPreferences empresa = getSharedPreferences("empresa", Context.MODE_PRIVATE);
            hilo_pedido = new Servicio();
            String ip = getString(R.string.servidor);
            hilo_pedido.execute(ip + "frmPedido.php?opcion=lista_pedido_por_id_empresa_por_fecha", "2",  empresa.getString("id", "0"), String.valueOf(dia), String.valueOf(mes), String.valueOf(anio));// parametro que recibe el doinbackground
            Log.i("Item", "actualizar!");

        } else {

            SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
            String id = prefe.getString("id_usuario", "");
            hilo_pedido = new Servicio();
            String ip = getString(R.string.servidor);
            hilo_pedido.execute(ip + "frmPedido.php?opcion=lista_pedido_por_id_usuario_por_fecha", "1", id, String.valueOf(dia), String.valueOf(mes), String.valueOf(anio));// parametro que recibe el doinbackground
            Log.i("Item", "actualizar!");
        }
    }




    // comenzar el servicio con el Historial....
    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
            if(isCancelled()==false) {

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
                        jsonParam.put("dia", params[3]);
                        jsonParam.put("mes", params[4]);
                        jsonParam.put("anio", params[5]);

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
                            vaciar_historial();
                            if (suceso.getSuceso().equals("1")) {
                                JSONArray usu = respuestaJSON.getJSONArray("historial");
                                for (int i = 0; i < usu.length(); i++) {
                                    int id_usuario = Integer.parseInt(usu.getJSONObject(i).getString("id_usuario"));
                                    int id_pedido = Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                    int id_moto = Integer.parseInt(usu.getJSONObject(i).getString("id_moto"));
                                    int calificacion = Integer.parseInt(usu.getJSONObject(i).getString("calificacion"));
                                    int tipo_pedido = Integer.parseInt(usu.getJSONObject(i).getString("tipo_pedido"));
                                    String mensaje = usu.getJSONObject(i).getString("mensaje");
                                    String fecha = usu.getJSONObject(i).getString("fecha");
                                    String fecha_llegado = usu.getJSONObject(i).getString("fecha_llegado");
                                    int estado = Integer.parseInt(usu.getJSONObject(i).getString("estado"));
                                    double latitud = Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                    double longitud = Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                    String nombre = usu.getJSONObject(i).getString("nombre");
                                    String apellido = usu.getJSONObject(i).getString("apellido");
                                    String celular = usu.getJSONObject(i).getString("celular");
                                    String marca = usu.getJSONObject(i).getString("marca");
                                    String placa = usu.getJSONObject(i).getString("placa");
                                    int estado_moto = Integer.parseInt(usu.getJSONObject(i).getString("estado_moto"));
                                    double monto_total =Double.parseDouble(usu.getJSONObject(i).getString("monto_total"));
                                    String nombre_direccion = usu.getJSONObject(i).getString("nombre_direccion");
                                    String detalle_direccion = usu.getJSONObject(i).getString("detalle_direccion");
                                    String hora = usu.getJSONObject(i).getString("hora");
                                    int puntuacion = Integer.parseInt(usu.getJSONObject(i).getString("puntuacion"));

                                    cargar_lista_en_historial(id_usuario, id_pedido, id_moto, calificacion, tipo_pedido, mensaje, fecha, fecha_llegado, estado, latitud, longitud, nombre, apellido, celular, marca, placa, estado_moto, monto_total, nombre_direccion, detalle_direccion,hora,"",puntuacion);
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
                        jsonParam.put("id_empresa", params[2]);
                        jsonParam.put("dia", params[3]);
                        jsonParam.put("mes", params[4]);
                        jsonParam.put("anio", params[5]);

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
                            vaciar_historial();
                            if (suceso.getSuceso().equals("1")) {
                                JSONArray usu = respuestaJSON.getJSONArray("historial");
                                for (int i = 0; i < usu.length(); i++) {
                                    int id_usuario = Integer.parseInt(usu.getJSONObject(i).getString("id_usuario"));
                                    int id_pedido = Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                    int id_moto = Integer.parseInt(usu.getJSONObject(i).getString("id_moto"));
                                    int calificacion = Integer.parseInt(usu.getJSONObject(i).getString("calificacion"));
                                    int tipo_pedido = Integer.parseInt(usu.getJSONObject(i).getString("tipo_pedido"));
                                    String mensaje = usu.getJSONObject(i).getString("mensaje");
                                    String fecha = usu.getJSONObject(i).getString("fecha");
                                    String fecha_llegado = usu.getJSONObject(i).getString("fecha_llegado");
                                    int estado = Integer.parseInt(usu.getJSONObject(i).getString("estado"));
                                    double latitud = Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                    double longitud = Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                    String nombre = usu.getJSONObject(i).getString("nombre");
                                    String apellido = usu.getJSONObject(i).getString("apellido");
                                    String celular = usu.getJSONObject(i).getString("celular");
                                    String marca = usu.getJSONObject(i).getString("marca");
                                    String placa = usu.getJSONObject(i).getString("placa");
                                    int estado_moto = Integer.parseInt(usu.getJSONObject(i).getString("estado_moto"));
                                    double monto_total =Double.parseDouble(usu.getJSONObject(i).getString("monto_total"));
                                    String nombre_direccion = usu.getJSONObject(i).getString("nombre_direccion");
                                    String detalle_direccion = usu.getJSONObject(i).getString("detalle_direccion");
                                    String hora = usu.getJSONObject(i).getString("hora");
                                    String nombre_usuario = usu.getJSONObject(i).getString("nombre_usuario");
                                    int puntuacion = Integer.parseInt(usu.getJSONObject(i).getString("puntuacion"));

                                    cargar_lista_en_historial(id_usuario, id_pedido, id_moto, calificacion, tipo_pedido, mensaje, fecha, fecha_llegado, estado, latitud, longitud, nombre, apellido, celular, marca, placa, estado_moto, monto_total, nombre_direccion, detalle_direccion,hora,nombre_usuario,puntuacion);
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

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                cargar_historial_en_la_lista();
                tv_mensaje_error.setText("");
            }
            else if(s.equals("2"))
            {
                cargar_historial_en_la_lista();
                tv_mensaje_error.setText(suceso.getMensaje());
            }
            else
            {
                cargar_historial_en_la_lista();
                tv_mensaje_error.setText("Error: Al conectar con el servidor.");
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
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        Items_historial adaptador;
        if (perfil.getString("administrador", "0").equals("1") == true) {
            adaptador = new Items_historial(this,historial,perfil.getString("nombre_empresa",""),Integer.parseInt(perfil.getString("administrador", "0")),getLayoutInflater(),getString(R.string.servidor));
        } else {
            adaptador = new Items_historial(this,historial,perfil.getString("nombre_empresa",""),0,getLayoutInflater(),getString(R.string.servidor));
        }


        lista_historial.setAdapter(adaptador);

    }

    private void cargar_lista_en_historial(int id_usuario,
                                           int id_pedido,
                                           int id_moto,
                                           int calificacion,
                                           int tipo_pedido,
                                           String mensaje,
                                           String fecha,
                                           String fecha_llegado,
                                           int estado,
                                           double latitud,
                                           double longitud,
                                           String nombre,
                                           String apellido,
                                           String celular,
                                           String marca,
                                           String placa,
                                           int estado_moto,
                                           double monto_total,
                                           String nombre_direccion,
                                           String detalle_direccion,
                                           String hora,
                                           String nombre_usuario,
                                           int puntuacion)
    {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();

        registro.put("id_usuario",String.valueOf(id_usuario));
        registro.put("id",String.valueOf(id_pedido));
        registro.put("id_moto",String.valueOf(id_moto));
        registro.put("calificacion",String.valueOf(calificacion));
        registro.put("tipo_pedido",String.valueOf(tipo_pedido));
        registro.put("mensaje",mensaje);
        registro.put("fecha",fecha);
        registro.put("fecha_llegado",fecha_llegado);
        registro.put("estado",String.valueOf(estado));
        registro.put("latitud",String.valueOf(latitud));
        registro.put("longitud",String.valueOf(longitud));
        registro.put("nombre",nombre);
        registro.put("apellido",apellido);
        registro.put("celular",celular);
        registro.put("marca",marca);
        registro.put("placa",placa);
        registro.put("estado_moto",String.valueOf(estado_moto));
        registro.put("monto_total",String.valueOf(monto_total));
        registro.put("nombre_direccion",nombre_direccion);
        registro.put("detalle_direccion",detalle_direccion);
        registro.put("hora",hora);
        registro.put("nombre_usuario",nombre_usuario);
        registro.put("puntuacion",String.valueOf(puntuacion));

        bd.insert("pedido", null, registro);
        bd.close();
    }
    private void cargar_historial_en_la_lista()
    {
        historial= new ArrayList<CHistorial>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from pedido ", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {

                int id_pedido=Integer.parseInt(fila.getString(0));
                int id_usuario=Integer.parseInt(fila.getString(1));
                int id_moto=Integer.parseInt(fila.getString(2));
                int calificacion=Integer.parseInt(fila.getString(3));
                int tipo_pedido=Integer.parseInt(fila.getString(4));
                String mensaje=fila.getString(5);
                String fecha=fila.getString(6);
                String fecha_llegado=fila.getString(7);
                int estado=Integer.parseInt(fila.getString(8));
                double latitud=Double.parseDouble(fila.getString(9));
                double longitud=Double.parseDouble(fila.getString(10));
                String nombre=fila.getString(11);
                String apellido=fila.getString(12);
                String celular=fila.getString(13);
                String marca=fila.getString(14);
                String placa=fila.getString(15);
                int estado_moto=Integer.parseInt(fila.getString(16));
                double monto_total=Double.parseDouble(fila.getString(17));
                String nombre_direccion=fila.getString(18);
                String detalle_direccion=fila.getString(19);
                String hora=fila.getString(20);
                String nombre_usuario=fila.getString(21);
                int puntuacion=Integer.parseInt(fila.getString(22));

                CHistorial hi =new CHistorial(id_usuario,id_pedido,id_moto,calificacion,tipo_pedido,mensaje,fecha,fecha_llegado,estado,latitud,longitud,nombre,apellido,celular,marca, placa,estado_moto,monto_total,nombre_direccion,detalle_direccion,hora,nombre_usuario,puntuacion);
                historial.add(hi);
            } while(fila.moveToNext());

        }

        bd.close();
        actualizar_lista();
    }

    private void vaciar_historial()
    {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from pedido");
        db.close();
        Log.e("sqlite ", "vaciar historial");
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


    @Override
    public boolean onTouchEvent(MotionEvent event) {

            int action = event.getActionMasked();
String TAG="MOUSE";
            switch (action) {

                case MotionEvent.ACTION_DOWN:
                    initialX = event.getX();
                    initialY = event.getY();

                    Log.d(TAG, "Action was DOWN");
                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "Action was MOVE");
                    break;

                case MotionEvent.ACTION_UP:
                    float finalX = event.getX();
                    float finalY = event.getY();

                    Log.d(TAG, "Action was UP");

                    if (initialX < finalX) {
                        Log.d(TAG, "Left to Right swipe performed");
                        Calendar c = Calendar.getInstance();
                        c.set(anio,mes-1,dia);
                        c.add(Calendar.DAY_OF_YEAR,-1);


                        // From calander get the year, month, day, hour, minute
                        anio = c.get(Calendar.YEAR);
                        mes = c.get(Calendar.MONTH)+1;
                        dia = c.get(Calendar.DAY_OF_MONTH);

                        if(c_hoy.get(Calendar.YEAR)==anio && c_hoy.get(Calendar.MONTH)+1==mes && c_hoy.get(Calendar.DAY_OF_MONTH)==dia) {
                            toolbarTitle.setText("Hoy");
                        }
                        else
                        {
                            toolbarTitle.setText(dia + " de " + mes_numero_a_letra(mes) + " del " + anio);
                        }

                        actualizar(dia,mes,anio);
                    }

                    if (initialX > finalX) {
                        Log.d(TAG, "Right to Left swipe performed");

                        Calendar c = Calendar.getInstance();
                        c.set(anio,mes-1,dia);
                        c.add(Calendar.DAY_OF_YEAR,1);


                        // From calander get the year, month, day, hour, minute
                        anio = c.get(Calendar.YEAR);
                        mes = c.get(Calendar.MONTH)+1;
                        dia = c.get(Calendar.DAY_OF_MONTH);

                        if(c_hoy.get(Calendar.YEAR)==anio && c_hoy.get(Calendar.MONTH)+1==mes && c_hoy.get(Calendar.DAY_OF_MONTH)==dia) {
                            toolbarTitle.setText("Hoy");
                        }
                        else
                        {
                            toolbarTitle.setText(dia + " de " + mes_numero_a_letra(mes) + " del " + anio);
                        }


                        actualizar(dia,mes,anio);
                    }

                    if (initialY < finalY) {
                        Log.d(TAG, "Up to Down swipe performed");
                    }

                    if (initialY > finalY) {
                        Log.d(TAG, "Down to Up swipe performed");
                    }

                    break;

                case MotionEvent.ACTION_CANCEL:
                    Log.d(TAG,"Action was CANCEL");
                    break;

                case MotionEvent.ACTION_OUTSIDE:
                    Log.d(TAG, "Movement occurred outside bounds of current screen element");
                    break;

            }

        return super.onTouchEvent(event);
    }
}
