package com.grayhartcorp.quevengan;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.notificacion.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Registrar_perfil extends AppCompatActivity implements  View.OnClickListener{
    private static String APP_DIRECTORY = "Asapp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";

    ProgressDialog pDialog;
    String token="";

    ImageView perfil;
    String sid_usuario="";

    int camara=0;// es para saber si la imagen fue sacada de galeria o de una camara.. o sino de descargada del servidor.
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private String mPath;
    private Uri path;

    private LinearLayout mRlView;

    Button siguiente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_registrar_perfil);
        mRlView = (LinearLayout) findViewById(R.id.rl_view);
        siguiente=(Button)findViewById(R.id.siguiente);
        perfil=(ImageView)findViewById(R.id.perfil);

        perfil.setOnClickListener(this);
        siguiente.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.perfil)
        {final CharSequence[] options={"Tomar foto","Elegir de galeria","Cancelar"};
            final AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Elige una opcion");
            builder.setItems(options, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(options[i]=="Tomar foto")
                    {
                        openCamara();
                    }else if(options[i]=="Elegir de galeria")
                    {
                        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent.createChooser(intent,"Seleccione app de imagen"),SELECT_PICTURE);

                    }else if (options[i]=="Cancelar")
                    {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.show();
        }
        else
            if(view.getId()==R.id.siguiente)
            {
                Bitmap imagen = ((BitmapDrawable)perfil.getDrawable()).getBitmap();
                guardar_en_memoria(imagen);
                imagen=ReducirImagen_b(imagen,200,200);
               String uploadImage = getStringImage(imagen);

                SharedPreferences persona=getSharedPreferences("usuario",MODE_PRIVATE);
                String snombre = persona.getString("nombre","");
                String sapellido =persona.getString("apellido","");
                String scelular =  persona.getString("celular","").substring(4,12);
                String semail = persona.getString("email","");

                ObtenerServiciosWeb hilo = new ObtenerServiciosWeb();

                if(token=="" || token==null){
                    if (getToken()==false) {
                        Toast.makeText(this, "Token no Generado..", Toast.LENGTH_LONG).show();
                    }
                }
                if (VerLogindato(snombre, sapellido,scelular,semail)==true&& token !=null) {
                    hilo.execute(getString(R.string.servidor)+"frmUsuario.php?opcion=insertar_usuario", "1", snombre, sapellido,scelular,semail,token,uploadImage);// parametro que recibe el doinbackground
                } else {
                    errorlogin();
                }

            }
    }

    public void errorlogin(){ // vibra y muestra un toast de un error
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        mensaje_error("Error: Verifique los datos que ingreso.");

    }
    public boolean VerLogindato(String nombre, String apellido,String telefono,String email){

        if (nombre.equals("")|| apellido.equals("")||telefono.equals("")){
            Log.e("Perfil", "Chequee los datos ingresados.");
            return false;
        } else {
            return true;
        }
    }
    //almacenando token en el servidor mysql
    public boolean getToken() {
        token = SharedPrefManager.getInstance(this).getDeviceToken();
        return (token!=null);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){


            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });

                    //Convertir MPath a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);




                    //imagen cuadrado
                    bitmap=imager_cuadrado(bitmap);

                    perfil.setImageBitmap(bitmap);
                    camara=1;
                    break;
                case SELECT_PICTURE:
                    path = data.getData();
                    try {//convertir Uri a BitMap
                        Bitmap tempBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(path));



                        //imagen cuadrado
                        tempBitmap=imager_cuadrado(tempBitmap);

                        //subir imagen de perfil al servidor...
                        tempBitmap=ReducirImagen_b(tempBitmap,200,200);
                       perfil.setImageBitmap(tempBitmap);


                        camara=2;
                    }
                    catch (Exception e)
                    {

                    }

                    break;

            }
        }

    }
    public static Bitmap ReducirImagen_b( Bitmap BitmapOrg, int w, int h) {

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        // calculamos el escalado de la imagen destino
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // para poder manipular la imagen
        // debemos crear una matriz
        Matrix matrix = new Matrix();
        // Cambiar el tamaño del mapa de bits
        matrix.postScale(scaleWidth, scaleHeight);
        // volvemos a crear la imagen con los nuevos valores
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }

    public Bitmap imager_cuadrado(Bitmap originalBitmap)
    {
        if (originalBitmap.getWidth() > originalBitmap.getHeight()){
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        }else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }
        return originalBitmap;
    }
    /*Ahora que se tiene la imagen cargado en mapa de bits.
   Vamos a convertir este mapa de bits a cadena de base64
   este método es para convertir este mapa de bits a la cadena de base64*/
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private boolean mayRequestStoragePermission() {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(mRlView, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPath = savedInstanceState.getString("file_path");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                perfil.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void openCamara() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if(isDirectoryCreated){
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";

            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;

            File newFile = new File(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }
    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }


    public class ObtenerServiciosWeb extends AsyncTask<String,Integer,String> {


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
                    jsonParam.put("nombre", params[2]);
                    jsonParam.put("apellido", params[3]);
                    jsonParam.put("telefono", params[4]);
                    jsonParam.put("email", params[5]);
                    jsonParam.put("token", params[6]);
                    jsonParam.put("imagen", params[6]);

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

                            sid_usuario= respuestaJSON.getString("id_usuario") ;

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
            pDialog = new ProgressDialog(Registrar_perfil.this);
            pDialog.setMessage("Autenticando ....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                LoginExito();
            } else if(s.equals("2")) {
                mensaje_error("Tenemos unos problemas al registrar.");
            }
            else
            {
                mensaje_error( "Error: Al conectar con el Servidor.");
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

    private void LoginExito() {
        SharedPreferences persona=getSharedPreferences("usuario",MODE_PRIVATE);
        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor editor=usuario.edit();
        editor.putString("id_usuario",sid_usuario);
        editor.putString("nombre",persona.getString("nombre",""));
        editor.putString("apellido",persona.getString("apellido",""));
        editor.putString("celular", persona.getString("celular",""));
        editor.putString("email", persona.getString("email",""));
        editor.putString("id_empresa", "");
        editor.putString("usuario", "1");
        editor.putString("login_usuario", "0");
        editor.commit();

        startActivity(new Intent(getApplicationContext(),Bienvenido.class));

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
