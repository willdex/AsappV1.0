package com.grayhartcorp.quevengan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.empresa.Empresa;
import com.grayhartcorp.quevengan.menu_motista.Menu_motista;
import com.grayhartcorp.quevengan.notificacion.SharedPrefManager;
import com.sinch.verification.CodeInterceptionException;
import com.sinch.verification.Config;
import com.sinch.verification.InitiationResult;
import com.sinch.verification.InvalidInputException;
import com.sinch.verification.ServiceErrorException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
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
import java.util.Calendar;
import java.util.List;

public class Confirmar_sms extends AppCompatActivity implements View.OnClickListener {
private String celular;
private String tipo;
Button codeInputButton;
   TextView enviar_mensaje;
    EditText inputCode;
    TextView mensaje;
    String token="";
    JSONArray perfil;
    Suceso suceso;

    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;


    Mensaje alerta=new Mensaje("");


    private static final String TAG = "VerificationActivity";
    private final String APPLICATION_KEY = "b6688b99-7116-4511-b075-16decd541663";
    private Verification mVerification;
    private boolean mShouldFallback = true;
    private static final String[] SMS_PERMISSIONS = {
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.ACCESS_NETWORK_STATE };
    ProgressBar cargando;
    Handler handle=new Handler();

    int i=0;
    private boolean mIsSmsVerification;
    public static final String SMS = "sms";
    private String mPhoneNumber;
    TextView tv_tiempo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimary_back));

        setContentView(R.layout.activity_confirmar_sms);
        mensaje=(TextView)findViewById(R.id.mensaje);
        codeInputButton=(Button)findViewById(R.id.codeInputButton);
        inputCode=(EditText)findViewById(R.id.inputCode);
        cargando=(ProgressBar)findViewById(R.id.cargando);
        enviar_mensaje=(TextView)findViewById(R.id.enviar_mensaje);
        tv_tiempo=(TextView)findViewById(R.id.tv_tiempo);

        enviar_mensaje.setVisibility(View.INVISIBLE);

        inputCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (verificar_codigo(s)) {
                    codeInputButton.setEnabled(true);
                    inputCode.setTextColor(Color.BLACK);
                } else {
                    codeInputButton.setEnabled(false);
                    inputCode.setTextColor(Color.RED);
                }
            }
        });


        try{
            Bundle bundle=getIntent().getExtras();
            celular=""+bundle.getString("celular");
            tipo=bundle.getString("tipo");
            if(tipo.equals("moto"))
            {
                mensaje.setText("Por favor Introduce tu PIN");
            }
            else if(tipo.equals("usuario"))
            {
                mPhoneNumber = "+591"+celular;
                final String method ="sms";
                mIsSmsVerification = method.equalsIgnoreCase("sms");
                requestPermissions();
                progress_en_proceso();
                getSupportActionBar().setTitle("Verificar "+mPhoneNumber);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }




        }catch (Exception e)
        {
            //finish();
        }


        codeInputButton.setOnClickListener(this);
enviar_mensaje.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.codeInputButton) {
            token = SharedPrefManager.getInstance(this).getDeviceToken();
            if (token != null && token != "") {

                if(tipo.equals("moto")) {
                    Servicio hilo_moto = new Servicio();
                    hilo_moto.execute(getString(R.string.servidor) + "frmMoto.php?opcion=get_moto", "1", celular, inputCode.getText().toString(), token);// parametro que recibe el doinbackground
                }
                else if (tipo.equals("usuario"))
                {
                    token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
                    if (token != null && token != "") {

                        Servicio_cargar_datos hilo_cargar = new Servicio_cargar_datos();
                        hilo_cargar.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=get_perfil", "1", celular, token,inputCode.getText().toString());// parametro que recibe el doinbackground
                    }
                    else
                    {
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getApplicationContext());
                        dialogo1.setTitle("Vamos a verificar el número de telefono");
                        dialogo1.setMessage("No tiene token de acceso.  \n por favor vuelva a intentar mas tarde. \n para generar el Token ncesita tener instalado el Google Play Service.");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {

                            }
                        });
                        dialogo1.show();
                    }

                   //                                                                                                                                                                                                     verificar_codigo();



                   // Servicio_cargar_datos  hilo_cargar = new Servicio_cargar_datos();
                   // hilo_cargar.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=get_perfil", "1", celular);// parametro que recibe el doinbackground

                }

            } else
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
        else if(v.getId()==R.id.enviar_mensaje)
        {
            enviar_mensaje.setVisibility(View.INVISIBLE);
            progress_en_proceso();
            requestPermissions();
        }
    }

    // comenzar el servicio con el motista....
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

                            perfil = respuestaJSON.getJSONArray("perfil");

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

            }else if(s.equals("2"))
            {
                mensaje_error(suceso.getMensaje());
            }
            else
            {
               mensaje_error("Error: Verifique los dastos que ingreso.");
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

    private void login_motista() {

        Intent i = new Intent(this,Menu_motista.class );

        SharedPreferences preferencias=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
     try {
         editor.putString("id_moto", perfil.getJSONObject(0).getString("id"));
         editor.putString("nombre", perfil.getJSONObject(0).getString("nombre"));
         editor.putString("apellido",perfil.getJSONObject(0).getString("apellido"));
         editor.putString("ci", perfil.getJSONObject(0).getString("ci"));
         editor.putString("celular", perfil.getJSONObject(0).getString("celular"));
         editor.putString("email",perfil.getJSONObject(0).getString("email"));
         editor.putString("marca",perfil.getJSONObject(0).getString("marca"));
         editor.putString("modelo",perfil.getJSONObject(0).getString("modelo"));
         editor.putString("placa",perfil.getJSONObject(0).getString("placa"));
         editor.putString("direccion",perfil.getJSONObject(0).getString("direccion"));
         editor.putString("telefono", perfil.getJSONObject(0).getString("telefono"));
         editor.putString("referencia", perfil.getJSONObject(0).getString("referencia"));
         editor.putString("codigo", perfil.getJSONObject(0).getString("codigo"));
         editor.putString("credito", perfil.getJSONObject(0).getString("credito"));
         editor.putString("estado", perfil.getJSONObject(0).getString("estado"));
         editor.putString("login", perfil.getJSONObject(0).getString("login"));
         editor.putString("moto", "1");
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

         startActivity(i);
         finish();
         //finish;
     }catch (Exception e)
     {

     }



    }



    private boolean verificar_codigo(CharSequence s) {
        boolean sw=false;
        try{
            int numero=Integer.parseInt(s.toString());
            if(numero>=0000 && numero<=9999)
            {
                sw=true;
            }
        }catch (Exception e)
        {
            sw=false;
        }
        return sw;
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


    //USUARIO

    private void requestPermissions() {
        List<String> missingPermissions;
        String methodText;

        if (mIsSmsVerification) {
            missingPermissions = getMissingPermissions(SMS_PERMISSIONS);
            methodText = "SMS";

            if (missingPermissions.isEmpty()) {
                createVerification();
            } else {
                if (needPermissionsRationale(missingPermissions)) {
                    Toast.makeText(this, "This application needs permissions to read your " + methodText + " to automatically verify your "
                            + "phone, you may disable the permissions once you have been verified.", Toast.LENGTH_LONG)
                            .show();
                }
                ActivityCompat.requestPermissions(this,
                        missingPermissions.toArray(new String[missingPermissions.size()]),
                        0);
            }
        }


    }


    //USUARIO
    private void createVerification() {
        // It is important to pass ApplicationContext to the Verification config builder as the
        // verification process might outlive the activity.
        Config config = SinchVerification.config()
                .applicationKey(APPLICATION_KEY)
                .context(getApplicationContext())
                .build();
        TextView messageText = (TextView) findViewById(R.id.textView);

        VerificationListener listener = new Confirmar_sms.MyVerificationListener();

        if (mIsSmsVerification) {
            messageText.setText(R.string.sending_sms);
            mVerification = SinchVerification.createSmsVerification(config, mPhoneNumber, listener);
            mVerification.initiate();
        } else {
            messageText.setText(R.string.flashcalling);
            mVerification = SinchVerification.createFlashCallVerification(config, mPhoneNumber, listener);
            mVerification.initiate();
        }



    }
    //USUARIO
    private boolean needPermissionsRationale(List<String> permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }
    //USUARIO
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Proceed with verification after requesting permissions.
        // If the verification SDK fails to intercept the code automatically due to missing permissions,
        // the VerificationListener.onVerificationFailed(1) method will be executed with an instance of
        // CodeInterceptionException. In this case it is still possible to proceed with verification
        // by asking the user to enter the code manually.
        createVerification();
    }
    //USUARIO
    private List<String> getMissingPermissions(String[] requiredPermissions) {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }
    //USUARIO
    private void hideProgressAndShowMessage(int message) {

        TextView messageText = (TextView) findViewById(R.id.textView);
        messageText.setText(message);
    }

    //USUARIO
    public void verificar_codigo()
    {
        String code =inputCode.getText().toString();
        if (!code.isEmpty()) {
            if (mVerification != null) {
                mVerification.verify(code);

                TextView messageText = (TextView) findViewById(R.id.textView);
                messageText.setText("Verification in progress");

            }
        }
    }

    //USUARIO
    class MyVerificationListener implements VerificationListener {

        @Override
        public void onInitiated(InitiationResult result) {
            Log.d(TAG, "Initialized!");


        }

        @Override
        public void onInitiationFailed(Exception exception) {
            Log.e(TAG, "Verification initialization failed: " + exception.getMessage());
            hideProgressAndShowMessage(R.string.failed);

            if (exception instanceof InvalidInputException) {
                // Incorrect number provided
            } else if (exception instanceof ServiceErrorException) {
                // Verification initiation aborted due to early reject feature,
                // client callback denial, or some other Sinch service error.
                // Fallback to other verification method here.

                if (mShouldFallback) {
                    mIsSmsVerification = !mIsSmsVerification;
                    if (mIsSmsVerification) {
                        Log.i(TAG, "Falling back to sms verification.");
                    } else {
                        Log.i(TAG, "Falling back to flashcall verification.");
                    }
                    mShouldFallback = false;
                    // Initiate verification with the alternative method.
                    requestPermissions();
                }
            } else {
                // Other system error, such as UnknownHostException in case of network error
            }
        }

        @Override
        public void onVerified() {
            Log.d(TAG, "Verified!");

            hideProgressAndShowMessage(R.string.verified);


            token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
            if (token != null && token != "") {

                Servicio_cargar_datos hilo_cargar = new Servicio_cargar_datos();
                hilo_cargar.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=get_perfil", "1", celular, token);// parametro que recibe el doinbackground
            }
            else
            {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getApplicationContext());
                dialogo1.setTitle("Vamos a verificar el número de telefono");
                dialogo1.setMessage("No tiene token de acceso.  \n por favor vuelva a intentar mas tarde. \n para generar el Token ncesita tener instalado el Google Play Service.");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {

                    }
                });
                dialogo1.show();
            }

          /*
            SharedPreferences preferencias=getSharedPreferences("perfil", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferencias.edit();
            editor.putString("celular",  mPhoneNumber.substring(4,12));
            editor.putString("proceso","1");
            editor.commit();

            Intent f = new Intent(getApplicationContext(), Registro_usuario.class);
            startActivity(f);
            */
// inviar parametros de numero telefonico...
        }

        @Override
        public void onVerificationFailed(Exception exception) {
            Log.e(TAG, "Verification failed: " + exception.getMessage());
            if (exception instanceof CodeInterceptionException) {
                // Automatic code interception failed, probably due to missing permissions.
                // Let the user try and enter the code manually.

            } else {
                hideProgressAndShowMessage(R.string.failed);
            }
        }
    }
    //USUARIO
    public void obtener_codigo()
    {
        try {

            Uri smsUri = Uri.parse("content://sms/inbox");
            Cursor cursor = getContentResolver().query(smsUri, null, null, null, null);
             /* Moving To First */
            if (!cursor.moveToFirst()) { /* false = cursor is empty */
                return;
            }
            for (int k = 0; k < cursor.getColumnCount() && !cursor.getString(2).equals("+46769446575"); k++) {
                cursor.moveToNext();
            }
            if (cursor.getString(2).equals("+46769446575")) {
                inputCode.setText(obtener_codigo(cursor.getString(12)));
            }
            cursor.close();
        }catch (Exception e)
        {

        }
    }
    //USUARIO
    public String obtener_codigo(String texto)
    {String codigo="";
        for (int i=0;i<texto.length();i++)
        {
            if(es_numero(String.valueOf(texto.charAt(i))))
            {
                codigo+=texto.charAt(i);
            }
        }
        return codigo;
    }
    //USUARIO
    public boolean es_numero(String numero)
    {
        try{
            Long.parseLong(numero);
        }catch (Exception e)
        {
            return false;
        }
        return true;
    }

    //USUARIO
    public  void progress_en_proceso()
    {

        i=0;
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (i<59)
                {
                    i=i+1;

                    handle.post(new Runnable() {
                        @Override
                        public void run() {
                            cargando.setProgress(i);

                            if(i==60)
                            {
                                tv_tiempo.setText("01:00");
                            }
                            else
                            {
                                tv_tiempo.setText("00:"+i);
                            }

                            if( i==10||i==30||i==50)
                            {
                                obtener_codigo();

                            }
                            if(i==20||i==40 || i==58)
                            {
                                verificar_codigo();
                            }
                            if(i>=55)
                            {
                                enviar_mensaje.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                    try{

                        Thread.sleep(1000);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }
    //USUARIO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }



    ///OBTENER DATOS DEL USUARIO..

    public class Servicio_cargar_datos extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

            if (params[1] == "1") { //mandar JSON metodo post para login
                try {
                    HttpURLConnection urlConn;

                    DataOutputStream printout;
                    DataOutputStream input;

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
                    jsonParam.put("telefono", params[2]);
                    jsonParam.put("token", params[3]);
                    jsonParam.put("codigo", params[4]);

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
                        if (error.equals("1")) {
                            JSONArray empress = respuestaJSON.getJSONArray("empresa");

                            SharedPreferences prefe=getSharedPreferences("perfil",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=prefe.edit();
                            editor.putString("nombre",respuestaJSON.getString("nombre"));
                            editor.putString("apellido",respuestaJSON.getString("apellido"));
                            editor.putString("celular", celular);
                            editor.putString("email",respuestaJSON.getString("email"));
                            editor.putString("id_usuario", respuestaJSON.getString("id"));
                            editor.putString("id_empresa", respuestaJSON.getString("id_empresa"));
                            editor.putString("nombre_empresa",empress.getJSONObject(0).getString("nombre"));
                            editor.putString("usuario", "1");
                            editor.putString("login_usuario", "1");
                            editor.commit();

                        try {
                            JSONArray dato = respuestaJSON.getJSONArray("empresa");
                            if (dato.length() > 0) {
                                //registramos con un ->1 si es administrador,....
                                SharedPreferences empresa = getSharedPreferences("perfil", MODE_PRIVATE);
                                SharedPreferences.Editor editar = empresa.edit();
                                editar.putString("administrador", "1");
                                editar.commit();

                                SharedPreferences empresa_s = getSharedPreferences("empresa", MODE_PRIVATE);
                                SharedPreferences.Editor editar_r = empresa_s.edit();
                                editar_r.putString("id", dato.getJSONObject(0).getString("id"));
                                editar_r.putString("nombre", dato.getJSONObject(0).getString("nombre"));
                                editar_r.putString("direccion", dato.getJSONObject(0).getString("direccion"));
                                editar_r.putString("telefono", dato.getJSONObject(0).getString("telefono"));
                                editar_r.putString("razon_social", dato.getJSONObject(0).getString("razon_social"));
                                editar_r.putString("nit", dato.getJSONObject(0).getString("nit"));
                                editar_r.putString("latitud", dato.getJSONObject(0).getString("latitud"));
                                editar_r.putString("longitud", dato.getJSONObject(0).getString("longitud"));
                                editar_r.commit();

                            } else {
                            }
                        }catch (Exception e)
                        {

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
                SharedPreferences p=getSharedPreferences("perfil",MODE_PRIVATE);

                startActivity(new Intent( getApplicationContext(),Menu_p.class));
                finish();
            }
            else if(s.equals("2"))
            {
                Toast.makeText(getApplicationContext(),suceso.getMensaje(),Toast.LENGTH_SHORT).show();
            }
            else
            {
                mensaje_error("Error al conectar con el servidor.");
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



