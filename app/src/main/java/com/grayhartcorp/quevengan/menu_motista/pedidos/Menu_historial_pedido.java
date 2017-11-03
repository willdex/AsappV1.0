package com.grayhartcorp.quevengan.menu_motista.pedidos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.Suceso;
import com.grayhartcorp.quevengan.carreras.Carreras;

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

public class Menu_historial_pedido extends AppCompatActivity {


    float initialX, initialY;

    private static final int Date_id = 0;
    TextView toolbarTitle;
    int dia,mes,anio;



    private Servicio hilo_pedido;

    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;


    public Suceso suceso;
    Calendar c_hoy;
    TextView tv_mensaje_historial;


    ListView lista_historial;
    ArrayList<CPedidos> pedidos = new ArrayList<CPedidos>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimary_back));

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
        tv_mensaje_historial=(TextView)findViewById(R.id.tv_mensaje_historial);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //   cargar_historial_en_la_lista();

        lista_historial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CPedidos hi=new CPedidos();
                hi=pedidos.get(i);
                enviar_datos(hi);

            }
        });

         actualizar(dia,mes,anio);

    }
    /*
        private void cargar_la_lista() {
            SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
            if(perfil.getString("id_moto","").equals("")==false) {
                hilo_pedido = new Servicio_pedido();
                hilo_pedido.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedidos", "1", perfil.getString("id_moto",""));// parametro que recibe el doinbackground
            }
            else
            {
                finish();
            }
            }

    */
    public void mensaje(CPedidos pedidos)
    {
       // Toast.makeText(this,"pedidos :"+pedidos.getNombre_cliente(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
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
                return new DatePickerDialog(this, date_listener, anio,mes, dia);

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



    public void actualizar(int dia,int mes,int anio) {
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        if(perfil.getString("id_moto","").equals("")==false) {
            hilo_pedido = new Servicio();
            // hilo_pedido.execute(getString(R.string.servidor) + "frmPedido.php?opcion=get_pedidos", "1", perfil.getString("id_moto",""));// parametro que recibe el doinbackground
            hilo_pedido.execute(getString(R.string.servidor)  + "frmPedido.php?opcion=lista_pedido_por_id_moto_por_fecha", "1", perfil.getString("id_moto",""), String.valueOf(dia), String.valueOf(mes), String.valueOf(anio));// parametro que recibe el doinbackground

        }
        else
        {
            finish();
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
                        jsonParam.put("id_moto", params[2]);
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
                            pedidos=new ArrayList<CPedidos>();
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
                                    String monto_total =usu.getJSONObject(i).getString("monto_total");
                                    String nombre_direccion = usu.getJSONObject(i).getString("nombre_direccion");
                                    String detalle_direccion = usu.getJSONObject(i).getString("detalle_direccion");
                                    String hora = usu.getJSONObject(i).getString("hora");
                                    String nombre_usuario = usu.getJSONObject(i).getString("nombre_usuario");
                                    String nombre_empresa = usu.getJSONObject(i).getString("empresa");
                                    int puntuacion = Integer.parseInt(usu.getJSONObject(i).getString("puntuacion"));


                                    CPedidos hi = new CPedidos(id_pedido, id_usuario, id_moto, calificacion, tipo_pedido, mensaje, fecha, estado, latitud, longitud, nombre_usuario, monto_total,hora,nombre_direccion,nombre_empresa,puntuacion);
                                    pedidos.add(hi);
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

                Items_pedidos adaptador = new Items_pedidos(Menu_historial_pedido.this,pedidos,getLayoutInflater());
                lista_historial.setAdapter(adaptador);
                tv_mensaje_historial.setText("");


            }
            else if(s.equals("2"))
            {
                tv_mensaje_historial.setText(suceso.getMensaje());
            }
            else
            {
               tv_mensaje_historial.setText("Error: Al conectar con el servidor.");
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

    /*

        // comenzar el servicio con el motista....
        public class Servicio_pedido extends AsyncTask<String,Integer,String> {


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
                            String error = respuestaJSON.getString("suceso");// suceso es el campo en el Json
                            pedidos= new ArrayList<CPedidos>();
                            if (error.equals("1")) {
                                JSONArray usu=respuestaJSON.getJSONArray("pedido");
                                for (int i=0;i<usu.length();i++)
                                {
                                    int id=Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                    int id_usuario=Integer.parseInt(usu.getJSONObject(i).getString("id_usuario"));
                                    int id_moto=Integer.parseInt(usu.getJSONObject(i).getString("id_moto"));
                                    int calificacion=Integer.parseInt(usu.getJSONObject(i).getString("calificacion"));
                                    int tipo_pedido=Integer.parseInt(usu.getJSONObject(i).getString("tipo_pedido"));
                                    String mensaje=usu.getJSONObject(i).getString("mensaje");
                                    String fecha=usu.getJSONObject(i).getString("fecha");
                                    int estado=Integer.parseInt(usu.getJSONObject(i).getString("estado"));
                                    double latitud=Double.parseDouble(usu.getJSONObject(i).getString("latitud"));
                                    double longitud=Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                    String nombre_completo=usu.getJSONObject(i).getString("nombre_usuario");
                                    String monto=usu.getJSONObject(i).getString("monto");
                                    if(estado>=2) {
                                        CPedidos hi = new CPedidos(id, id_usuario, id_moto, calificacion, tipo_pedido, mensaje, fecha, estado, latitud, longitud, nombre_completo, monto);
                                        pedidos.add(hi);
                                    }
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
                    correcto();
                }
                else if(s.equals("2"))
                {
                    Toast.makeText(Pedidos.this,"No tienes ningun pedido.",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(Pedidos.this,"Error: Verifique los dastos que ingreso.",Toast.LENGTH_SHORT).show();
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


        private void correcto() {
            Items_pedidos adaptador = new Items_pedidos(this,pedidos);
            lista_pedidos.setAdapter(adaptador);
            lista_pedidos.setOnItemClickListener(new ListClick());

        }
    */
    public class ListClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, final int position, long arg3) {
            // TODO Auto-generated method stub

            CPedidos hi=new CPedidos();
            hi=pedidos.get(position);

            if(hi.getEstado()==2)
            {enviar_datos(hi);}





        }

    }

    public void enviar_datos(CPedidos hi)
    {
        Intent menu = new Intent(this,Carreras.class );
        menu.putExtra("id",String.valueOf(hi.getId()));
        menu.putExtra("id_pedido",String.valueOf(hi.getId()));
        menu.putExtra("id_usuario",String.valueOf(hi.getId_usuario()));
        menu.putExtra("id_moto",String.valueOf(hi.getId_moto()));
        menu.putExtra("calificacion",String.valueOf(hi.getCalificacion()));
        menu.putExtra("tipo_pedido",String.valueOf(hi.getTipo_pedido()));
        menu.putExtra("mensaje",String.valueOf(hi.getMensaje()));
        menu.putExtra("fecha",String.valueOf(hi.getFecha()));
        menu.putExtra("estado",String.valueOf(hi.getEstado()));
        menu.putExtra("latitud",String.valueOf(hi.getLatitud()));
        menu.putExtra("longitud",String.valueOf(hi.getLongitud()));
        menu.putExtra("nombre_usuario",String.valueOf(hi.getNombre_cliente()));
        menu.putExtra("monto_total",String.valueOf(hi.getMonto_total()));

        startActivity(menu);
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
