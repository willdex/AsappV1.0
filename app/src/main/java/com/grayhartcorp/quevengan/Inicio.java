package com.grayhartcorp.quevengan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.contactos.Registro_usuario;
import com.grayhartcorp.quevengan.menu_motista.Menu_motista;
import com.grayhartcorp.quevengan.notificacion.SharedPrefManager;


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
import java.util.Calendar;


public class Inicio extends AppCompatActivity implements View.OnClickListener{
    SharedPreferences datos_perfil,dato_moto;
    EditText celular,codigo;
    TextInputLayout tilTelefono;
    Button registrar, entrar;
    ImageView icono;
    TextView textView;
    Context context;


    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;

    private Servicio_moto hilo_moto;
    Suceso suceso;

    @Override
    protected void onDestroy() {
        Log.e("Inicio","destroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.e("Inicio","resume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e("Inicio","pause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e("Inicio","stop");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.e("Inicio","start");
        super.onStart();

    }

    String snombre,sapellido,sci,scelular,semail,smarca,smodelo,splaca,sdireccion,stelefono,sreferencia,scodigo,scredito,sid_moto,sestado,slogin;
    String token="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context=this;
        /*********************************************/
        setContentView(R.layout.presentacion);      //R.layout.presentacion
        saltar_animacion();
        /*********************************************/
        icono=(ImageView)findViewById(R.id.icono);
        textView=(TextView)findViewById(R.id.textView);


        //  verificar_version();

        //si no tiene actualizaciones.. continua con el inicio.. normal...
        dato_moto=getSharedPreferences("perfil", Context.MODE_PRIVATE);


        if(dato_moto.getString("login_usuario","0").equals("1")) {
            startActivity(new Intent(this, Menu_p.class));
            finish();
        }else if(dato_moto.getString("login_moto","0").equals("1"))
        {
            startActivity(new Intent(this, Menu_motista.class));
            finish();
        }
        else if(dato_moto.getString("celular","")!="" &&dato_moto.getString("proceso","")=="1" &&  dato_moto.getString("login_usuario","0").equals("0"))
        {
            startActivity(new Intent(this,Registro_usuario.class));
            finish();
        }



        getSupportActionBar().hide();





//inicio de animacion con un tiempo de espera de 1 segundo. y una ves terminado.salta al segundo 2  y al 3.
       /* new CountDownTimer(1000,10) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {


                new CountDownTimer(1000,10) {
                    @Override
                    public void onTick(long l) {
                        int i=950-(int)l;
                        textView.setText(i+"");
                        icono.getLayoutParams().height =i ;
                        icono.getLayoutParams().width = i;
                        icono.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                        new CountDownTimer(1000,10) {
                            @Override
                            public void onTick(long l) {
                                int i=(int)l;
                                textView.setText(i+"");
                                icono.getLayoutParams().height = i;
                                icono.getLayoutParams().width = i;
                                icono.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFinish() {
                                setContentView(R.layout.presentacion);
                                saltar_animacion();
                            }
                        }.start();
                    }
                }.start();

            }
        }.start();*/
        startActivity(new Intent(this,Login.class));
        finish();
    }
    public void saltar_animacion()
    {     //salta a la presentacion
        registrar=(Button)findViewById(R.id.registrar);
        entrar=(Button)findViewById(R.id.entrar);
        registrar.setOnClickListener(this);
        entrar.setOnClickListener(this);
    }

    public void verificar_version()
    {
        SharedPreferences actualizacion=getSharedPreferences("actualizacion",MODE_PRIVATE);
        int antigua=actualizacion.getInt("version_antigua",0);
        int nueva=actualizacion.getInt("version_nueva",0);
        if(antigua<nueva)
        {
            startActivity(new Intent(this,Actualizar_aplicacion.class));
        }
    }
    public void validacion()
    {   icono=(ImageView)findViewById(R.id.icono);

        try {
            int numero = Integer.parseInt(celular.getText().toString());


            if (celular.getText().toString().length() >= 8 && numero >= 60000000 && numero <= 79999999) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Vamos a verificar el número de telefono");
                dialogo1.setMessage(""+celular.getText()+" \n¿Es Correcto este número o quieres modificarlo?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        siguiente();
                    }
                });
                dialogo1.setNegativeButton("EDITAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                    }
                });
                dialogo1.show();


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Importante");
                builder.setMessage("Número Invalido.\n" +
                        " por favor ingrese un numero valido.");
                builder.setPositiveButton("OK", null);
                builder.create();
                builder.show();
                tilTelefono.setError("Numero invalido");
            }
        }

        catch (Exception e)
        {

        }
    }

    public void siguiente()
    {   datos_perfil=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = datos_perfil.edit();
        editor.putString("celular", celular.getText().toString());
        editor.commit();
        startActivity(new Intent(getApplicationContext(), Validacion.class));
        finish();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.registrar:
                startActivity(new Intent(this,Registrar_nombre.class));
                finish();
           /*  startActivity(new Intent(this,Login_registrar.class));
               finish();

               setContentView(R.layout.activity_inicio);
               getSupportActionBar().show();
               getSupportActionBar().setDisplayHomeAsUpEnabled(true);
               celular=(EditText)findViewById(R.id.celular);
               siguiente=(Button)findViewById(R.id.siguiente);
               siguiente.setOnClickListener(this);
*/

                break;
            case R.id.entrar:
                startActivity(new Intent(this,Login.class));
                finish();
              /*
                setContentView(R.layout.login_motista);
                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                celular=(EditText)findViewById(R.id.celular);
                codigo=(EditText)findViewById(R.id.codigo);
                siguiente=(Button)findViewById(R.id.siguiente_m);
                siguiente.setOnClickListener(this);
                */
                break;



            case R.id.siguiente:
                validacion();
                break;
            case R.id.siguiente_m:

                validacion_motista();
                break;
        }

    }

    private void validacion_motista() {
        hilo_moto = new Servicio_moto();
        String scelular=celular.getText().toString();
        String scodigo=codigo.getText().toString();

        token=SharedPrefManager.getInstance(this).getDeviceToken();
        if(token!=null|| token=="")
        {
            hilo_moto.execute(getString(R.string.servidor)+"frmMoto.php?opcion=get_moto", "1",scelular,scodigo,token);// parametro que recibe el doinbackground
        }
        else
        {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Vamos a verificar el número de telefono");
            dialogo1.setMessage("No tiene token de acceso.  \n por favor vuelva a intentar mas tarde. \n para generar el Token ncesita tener instalado el Google Play Service.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                }
            });
            dialogo1.show();

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        setContentView(R.layout.presentacion);
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        registrar=(Button)findViewById(R.id.registrar);
        entrar=(Button)findViewById(R.id.entrar);
        registrar.setOnClickListener(this);
        entrar.setOnClickListener(this);
        return true;
    }



    // comenzar el servicio con el motista....
    public class Servicio_moto extends AsyncTask<String,Integer,String> {


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
                    jsonParam.put("celular", params[2]);
                    jsonParam.put("codigo", params[3]);
                    jsonParam.put("token",params[4]);


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
                        suceso =new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));
                        //Accedemos a vector de resultados.
                        if (suceso.getSuceso().equals("1")) {
                            JSONArray perfil= respuestaJSON.getJSONArray("perfil");
                            sid_moto=perfil.getJSONObject(0).getString("id");
                            snombre=perfil.getJSONObject(0).getString("nombre");
                            sapellido=perfil.getJSONObject(0).getString("apellido");
                            sci=perfil.getJSONObject(0).getString("ci");
                            scelular=perfil.getJSONObject(0).getString("celular");
                            semail=perfil.getJSONObject(0).getString("email");
                            smarca=perfil.getJSONObject(0).getString("marca");
                            smodelo=perfil.getJSONObject(0).getString("modelo");
                            splaca=perfil.getJSONObject(0).getString("placa");
                            sdireccion=perfil.getJSONObject(0).getString("direccion");
                            stelefono=perfil.getJSONObject(0).getString("telefono");
                            sreferencia=perfil.getJSONObject(0).getString("referencia");
                            scodigo=perfil.getJSONObject(0).getString("codigo");
                            scredito=perfil.getJSONObject(0).getString("credito");
                            sestado=perfil.getJSONObject(0).getString("estado");
                            slogin=perfil.getJSONObject(0).getString("login");

                            try {
                                JSONArray tarifa = respuestaJSON.getJSONArray("tarifa");
                                String id = tarifa.getJSONObject(0).getString("id");
                                String monto = tarifa.getJSONObject(0).getString("monto");
                                String distancia = tarifa.getJSONObject(0).getString("distancia");
                                actualizar_tarifa(id, monto, distancia);
                            }catch (Exception e)
                            {

                            }

                            // obtenemos la tarifa.. .
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

            preparar_progres_dialogo("Asapp","Autenticando ....");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                login_motista();
                mensaje(suceso.getMensaje());
            }else if(s.equals("2"))
            {
                mensaje(suceso.getMensaje());
            }
            else
            {
                Toast.makeText(Inicio.this,"Error: Verifique los datos que ingreso.",Toast.LENGTH_SHORT).show();
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

    private void actualizar_tarifa(String id, String monto, String distancia) {
        SharedPreferences tarifa=getSharedPreferences("tarifa",MODE_PRIVATE);
        SharedPreferences.Editor editor=tarifa.edit();
        editor.putString("id",id);
        editor.putString("monto",monto);
        editor.putString("distancia",distancia);
        editor.commit();
    }


    public void errorlogin(String s){ // vibra y muestra un toast de un error
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast toast1 = Toast.makeText(getApplicationContext(), "Error: Verifique los datos que ingreso->"+s,
                Toast.LENGTH_SHORT);
        toast1.show();
    }
    private void getImage(String id)//
    {
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;

            public  GetImage() {

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                loading.dismiss();
                guardar_en_memoria(bitmap);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Inicio.this,"Descargando Imagen","Espere Porfavor...",true,true);
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = getString(R.string.servidor)+"frmMoto.php?opcion=get_imagen&id_moto="+strings[0];//hace consulta ala Bd para recurar la imagen
                Drawable d = getResources().getDrawable(R.mipmap.ic_perfil);
                Bitmap mIcon = drawableToBitmap(d);
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage();
        gi.execute(id);
    }
    private void login_motista() {

        Intent i = new Intent(this,Menu_motista.class );

        SharedPreferences preferencias=getSharedPreferences("perfil",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("id_moto",sid_moto);
        editor.putString("nombre",snombre);
        editor.putString("apellido",sapellido);
        editor.putString("ci",sci);
        editor.putString("celular",scelular);
        editor.putString("email",semail);
        editor.putString("marca",smarca);
        editor.putString("modelo",smodelo);
        editor.putString("placa",splaca);
        editor.putString("direccion",sdireccion);
        editor.putString("telefono",stelefono);
        editor.putString("referencia",sreferencia);
        editor.putString("codigo",scodigo);
        editor.putString("credito",scredito);
        editor.putString("estado",sestado);
        editor.putString("login",slogin);
        editor.putString("moto","1");
        editor.putString("login_moto", "1");
        editor.commit();

        //fecha.....
        Calendar calendario = Calendar.getInstance();

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH)+1;
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        SharedPreferences fecha=getSharedPreferences("fecha",Context.MODE_PRIVATE);
        SharedPreferences.Editor fecha1=fecha.edit();
        fecha1.putString("dia",String.valueOf(dia));
        fecha1.putString("mes",String.valueOf(mes));
        fecha1.putString("anio",String.valueOf(anio));
        fecha1.commit();


//finish;

        startActivity(i);
        finish();
    }

    private void guardar_en_memoria(Bitmap bitmapImage)
    {
        File file=null;
        FileOutputStream fos = null;
        try {
            String APP_DIRECTORY = "Asapp/";//nombre de directorio
            String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";//nombre de la carpeta
            file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            File mypath=new File(file,"perfil.jpg");//nombre del archivo imagen

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

    public void mensaje(String mensaje)
    {
        Toast toast =Toast.makeText(this,mensaje,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

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


}