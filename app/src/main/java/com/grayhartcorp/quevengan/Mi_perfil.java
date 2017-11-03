package com.grayhartcorp.quevengan;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.empresa.Empresa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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
import java.util.HashMap;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Mi_perfil extends AppCompatActivity implements View.OnClickListener {
    EditText nombre,apellido,celular;
    ImageView perfil;
    ImageButton back;
    Button bt_editar;
    String nombre_imagen="";
    int camara=0;// es para saber si la imagen fue sacada de galeria o de una camara.. o sino de descargada del servidor.

    private FrameLayout mRlView;
    private static String APP_DIRECTORY = "Asapp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";
    private String TEMPORAL_PICTURE_NAME="perfil.jpg";
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private String mPath;
    private Uri path;




    Suceso suceso;



    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.mi_perfil);



        getSupportActionBar().hide();
        nombre = (EditText) findViewById(R.id.nombre);
        apellido = (EditText) findViewById(R.id.apellido);
        celular = (EditText) findViewById(R.id.celular);
        perfil=(ImageView) findViewById(R.id.perfil);
        mRlView = (FrameLayout) findViewById(R.id.rl_view);
        back=(ImageButton)findViewById(R.id.back);
        bt_editar=(Button)findViewById(R.id.bt_editar);


        SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        nombre.setText(prefe.getString("nombre", ""));
        apellido.setText(prefe.getString("apellido", ""));
        celular.setText(prefe.getString("celular", ""));

        //imagen_en_vista(perfil);


        perfil.setOnClickListener(this);


        if(mayRequestStoragePermission())
            perfil.setEnabled(true);
        else
           perfil.setEnabled(false);

        restaurar(perfil);


        back.setOnClickListener(this);
        bt_editar.setOnClickListener(this);


        SharedPreferences perfil1=getSharedPreferences("perfil",MODE_PRIVATE);
        String url_imagen_motista=getString(R.string.servidor) + "frmMoto.php?opcion=get_imagen_usuario&id_usuario="+perfil1.getString("id_usuario","");
        String st_nombre=perfil1.getString("id_usuario","")+"_"+perfil1.getString("nombre","")+".jpg";
        nombre_imagen=st_nombre;
        if(existe_perfil(st_nombre)==false)
        {
            getImage(url_imagen_motista,st_nombre);
        }

    }

    public  void itemp(Object obj,String p)
    {
        Toast.makeText(this,"obj:"+obj+"   :"+p,Toast.LENGTH_LONG).show();
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
        }else if(view.getId()==R.id.back)
        {
            onBackPressed();
        }
        else if(view.getId()==R.id.bt_editar)
        {
            if(bt_editar.getText().toString().equals("confirmar")) {
                SharedPreferences perfil = getSharedPreferences("perfil", MODE_PRIVATE);
                String id = perfil.getString("id_usuario", "");
                if (nombre.getText().toString().length() >= 3 && apellido.getText().toString().length() >= 3) {
                    Servicio_perfil hilo_perfil = new Servicio_perfil();
                    hilo_perfil.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=modificar_nombre_apellido", "1", id, nombre.getText().toString(), apellido.getText().toString());// parametro que recibe el doinbackground
                } else {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("Por favor ingrese los datos correctamente.");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {

                        }
                    });
                    dialogo1.show();
                }

            }else if(bt_editar.getText().toString().equals("editar"))
            {
                bt_editar.setText("confirmar");
                nombre.setEnabled(true);
                apellido.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //ubicacion de la imagen
            SharedPreferences sperfil=getSharedPreferences("perfil",MODE_PRIVATE);
            Bitmap img_cargar;
            Servicio hilo ;
            String uploadImage;

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
                    //subir imagen de perfil al servidor...
                    img_cargar=ReducirImagen_b(bitmap,200,200);
                    hilo = new Servicio();
                    uploadImage = getStringImage(img_cargar);
                    hilo.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=insertar_imagen", "1",sperfil.getString("id_usuario",""),uploadImage);// parametro que recibe el doinbackground




                    //Convertir Bitmap a Drawable.
                    Drawable dw = new BitmapDrawable(getResources(), bitmap);
                    //se edita la imagen para ponerlo en circulo.
                    imagen_circulo(dw,perfil);
                    //guardar en SharePrefences
                    camara=1;

                    guardar(mPath);

                    break;
                case SELECT_PICTURE:
                     path = data.getData();
                    try {//convertir Uri a BitMap
                        Bitmap tempBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(path));



                        //imagen cuadrado
                        tempBitmap=imager_cuadrado(tempBitmap);

                        //subir imagen de perfil al servidor...
                        img_cargar=ReducirImagen_b(tempBitmap,200,200);
                        hilo = new Servicio();
                        uploadImage = getStringImage(img_cargar);
                        hilo.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=insertar_imagen", "1",sperfil.getString("id_usuario",""),uploadImage);// parametro que recibe el doinbackground





                        //Convert bitmap to drawable
                        Drawable dw1 = new BitmapDrawable(getResources(), tempBitmap);
                        //Conveertir Imagen a Circulo
                        imagen_circulo(dw1,perfil);
                        //guardar en SharePrefences
                        camara=2;
                        guardar(path.toString());
                    }
                    catch (Exception e)
                    {

                    }
                    // perfil.setImageURI(path);
                  //  perfil.setImageDrawable(d);
                    break;

            }
        }

    }
    public void guardar(String direccion_imagen) {
        SharedPreferences preferencias=getSharedPreferences("perfil",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("imagen_perfil",direccion_imagen);
        editor.putString("camara",String.valueOf(camara));
        // 1: es foto de camara.
        // 2: es foto de galeria.
        editor.commit();
    }


    public void imagen_en_vista(ImageView imagen)
    { Drawable dw;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + "Asapp/Imagen"
                + File.separator + nombre_imagen;//.jpg


        File newFile = new File(mPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);
        //Convertir Bitmap a Drawable.
        dw = new BitmapDrawable(getResources(), bitmap);
        //se edita la imagen para ponerlo en circulo.

        if( bitmap==null)
        { dw = getResources().getDrawable(R.mipmap.ic_perfil);}

        imagen_circulo(dw,imagen);
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

    public void restaurar(ImageView perfil){
        SharedPreferences preferencias=getSharedPreferences("perfil",Context.MODE_PRIVATE);
        String direccion=preferencias.getString("imagen_perfil", "");
        camara=Integer.parseInt(preferencias.getString("camara","0"));
        // 1: es foto de camara.
        // 2: es foto de galeria.
        switch (camara)
        {
            case 0:
                   break;
            case 1: Bitmap bitmap= BitmapFactory.decodeFile(direccion);
                   Drawable dw1 = new BitmapDrawable(getResources(), bitmap);
                    imagen_circulo(dw1,perfil);
                   break;
            case 2:
               try {
                   Bitmap tempBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(direccion)));
                   //Convert bitmap to drawable
                   Drawable dw2 = new BitmapDrawable(getResources(), tempBitmap);
                   //Conveertir Imagen a Circulo
                   imagen_circulo(dw2, perfil);
               }catch (Exception e)
               {

               }
                   break;
        }



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
                    + File.separator + nombre_imagen;

            File newFile = new File(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
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

    public Bitmap imager_cuadrado(Bitmap originalBitmap)
    {
        if (originalBitmap.getWidth() > originalBitmap.getHeight()){
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        }else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }
        return originalBitmap;
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

    public void imagen_circulo(Drawable id_imagen,ImageView  imagen)
    {
        Bitmap originalBitmap = ((BitmapDrawable) id_imagen).getBitmap();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()){
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        }else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }

//creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

//asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getWidth());

        imagen.setImageDrawable(roundedDrawable);
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



    public class Servicio extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
// subir imagen de perfil al servidor..
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
                    jsonParam.put("imagen", params[3]);
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
               mensaje(suceso.getMensaje());
            }
            else  if(s.equals("2"))
            {
                mensaje(suceso.getMensaje());
            }
            else
            {
                mensaje("Error: Al conectar con el servidor.");
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

    public void mensaje(String mensaje)
    {
        Toast toast =Toast.makeText(this,mensaje,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }


    /*Inicio de obtencion de datos..*/

    //descargar imagenes de perfil de los contactos..
    private void getImage(String id,String nombre)//
    {this.nombre_imagen=nombre;
        class GetImage extends AsyncTask<String,Void,Bitmap> {
            ImageView bmImage;
            String nombre;


            public GetImage(ImageView bmImage,String nombre) {
                this.bmImage = bmImage;
                this.nombre=nombre;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                {
                    Drawable dw = getResources().getDrawable(R.mipmap.ic_perfil);
                    bmImage.setImageDrawable(dw);
                }
                else
                {
                    RoundedBitmapDrawable roundedDrawable =RoundedBitmapDrawableFactory.create(getResources(),bitmap);

                    //asignamos el CornerRadius
                    roundedDrawable.setCornerRadius(bitmap.getHeight());

                    bmImage.setImageDrawable(roundedDrawable);

                    guardar_en_memoria(bitmap,nombre);
                }


            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = strings[0];//hace consulta ala Bd para recurar la imagen

                Bitmap mIcon = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage(perfil,nombre);
        gi.execute(id);
    }


    private void guardar_en_memoria(Bitmap bitmapImage,String nombre)
    {
        File file=null;
        FileOutputStream fos = null;
        try {
            String APP_DIRECTORY = "Asapp/";//nombre de directorio
            String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";//nombre de la carpeta
            file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            File mypath=new File(file,nombre);//nombre del archivo imagen

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
    public boolean existe_perfil(String nombre)
    { boolean sw_carrera=false;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + "Asapp/Imagen"
                + File.separator +nombre;


        File newFile = new File(mPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);

        if( bitmap!=null)
        {

            sw_carrera=true;

            RoundedBitmapDrawable roundedDrawable =RoundedBitmapDrawableFactory.create(getResources(),bitmap);

            //asignamos el CornerRadius
            roundedDrawable.setCornerRadius(bitmap.getHeight());

            perfil.setImageDrawable(roundedDrawable);
        }

        return sw_carrera;
    }
    /*Fin de obtencion de datos..*/




    // modificar NOMBRE y APELLIDOS....
    public class Servicio_perfil extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

            if(isCancelled()==false && alertDialog.isShowing()==true) {
                //modificar Nombre y Apellido.
                devuelve = "-1";
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
                        jsonParam.put("nombre", params[3]);
                        jsonParam.put("apellido", params[4]);
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
                                SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
                                SharedPreferences.Editor editor=perfil.edit();
                                editor.putString("nombre",params[3]);
                                editor.putString("apellido",params[4]);
                                editor.commit();

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
            }else
            {
                devuelve="-1";
            }

            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog


            preparar_progres_dialogo("Asapp","Modificando los datos . . .");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if(s.equals("1"))
            {
                mensaje(suceso.getMensaje());
                bt_editar.setText("editar");
                nombre.setEnabled(false);
                apellido.setEnabled(false);

            }else
            if(s.equals("2"))
            {
                mensaje(suceso.getMensaje());

            }
            else if(s.equals("-1"))
            {
                mensaje("Se cancelo el pedido.");
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
