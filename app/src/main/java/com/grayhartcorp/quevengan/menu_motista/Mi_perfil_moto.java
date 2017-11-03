package com.grayhartcorp.quevengan.menu_motista;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.Suceso;

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

public class Mi_perfil_moto extends AppCompatActivity implements View.OnClickListener{
    ImageButton back;
    EditText nombre,apellido,ci,celular,email,marca,modelo,placa,direccion,telefono,referencia;
    TextView credito;
    Switch sw_estado;
    Suceso suceso;

    AlertDialog.Builder builder_dialogo;
    AlertDialog alertDialog;


    Servicio_moto hilo_moto;
    ImageView perfil;

    Bitmap imagen_original=null;

    private static String APP_DIRECTORY = "Asapp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_perfil_moto);
        back=(ImageButton)findViewById(R.id.back);
        nombre=(EditText)findViewById(R.id.nombre);
        apellido=(EditText)findViewById(R.id.apellido);
        ci=(EditText)findViewById(R.id.ci);
        celular=(EditText)findViewById(R.id.celular);
        email=(EditText)findViewById(R.id.email);
        marca=(EditText)findViewById(R.id.marca);
        modelo=(EditText)findViewById(R.id.modelo);
        placa=(EditText)findViewById(R.id.placa);
        direccion=(EditText)findViewById(R.id.direccion);
        telefono=(EditText)findViewById(R.id.telefono);
        referencia=(EditText)findViewById(R.id.referencia);
        credito=(TextView) findViewById(R.id.credito);
        sw_estado=(Switch)findViewById(R.id.sw_estado);
        perfil=(ImageView)findViewById(R.id.perfil);

        cargar_datos_ala_vista();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        sw_estado.setOnClickListener(this);


           imagen_en_vista(perfil);



        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
       getImage(perfil.getString("id_moto",""));
    }
    public void cargar_datos_ala_vista()
    {
        SharedPreferences preferencias=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String snombre=preferencias.getString("nombre","");
        nombre.setText(snombre);
        apellido.setText(preferencias.getString("apellido",""));
        ci.setText(preferencias.getString("ci",""));
        celular.setText(preferencias.getString("celular",""));
        email.setText(preferencias.getString("email",""));
        marca.setText(preferencias.getString("marca",""));
        modelo.setText(preferencias.getString("modelo",""));
        placa.setText(preferencias.getString("placa",""));
        direccion.setText(preferencias.getString("direccion",""));
        referencia.setText(preferencias.getString("referencia",""));
        credito.setText(preferencias.getString("credito","0"));
        if(preferencias.getString("estado","0").equals("1"))
        {
            sw_estado.setChecked(true);
        }
        else
        {
            sw_estado.setChecked(false);
        }


    }

    public void imagen_en_vista(ImageView imagen)
    { Drawable dw;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                + File.separator + "perfil.jpg";


        File newFile = new File(mPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);
        //Convertir Bitmap a Drawable.
        dw = new BitmapDrawable(getResources(), bitmap);
        //se edita la imagen para ponerlo en circulo.

        if( bitmap==null)
        { dw = getResources().getDrawable(R.mipmap.ic_perfil);}

        imagen_circulo(dw,imagen);
    }
    public void imagen_circulo(Drawable id_imagen, ImageView imagen) {
        Bitmap originalBitmap = ((BitmapDrawable) id_imagen).getBitmap();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        } else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }

//creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

//asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getWidth());

        imagen.setImageDrawable(roundedDrawable);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.sw_estado:
                boolean b=sw_estado.isChecked();
                hilo_moto = new Servicio_moto();
                SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
                String id=prefe.getString("id_moto", "");
                if(b==true) {
                    hilo_moto.execute(getString(R.string.servidor) + "frmMoto.php?opcion=set_estado", "1",id,"1");// parametro que recibe
                }
                else
                {
                    hilo_moto.execute(getString(R.string.servidor) + "frmMoto.php?opcion=set_estado", "1",id,"0");// parametro que recibe
                }
                break;
        }
    }


    // comenzar el servicio con el motista....
    public class Servicio_moto extends AsyncTask<String,Integer,String> {


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
                    jsonParam.put("id_moto", params[2]);
                    jsonParam.put("estado", params[3]);

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

                        suceso =new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

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
            preparar_progres_dialogo("Asapp","Autenticando. . .");
            builder_dialogo.setCancelable(true);
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            alertDialog.dismiss();//ocultamos proggress dialog
            Log.e("respuesta del servidor=", "" + s);
            if(s.equals("1"))
            {


                Toast.makeText(getApplicationContext(),suceso.getMensaje(),Toast.LENGTH_SHORT).show();
            }else if(s.equals("2"))
            {
                sw_estado.setChecked(!sw_estado.isChecked());
                Toast.makeText(getApplicationContext(),suceso.getMensaje(),Toast.LENGTH_SHORT).show();
            }
            else
            {   sw_estado.setChecked(!sw_estado.isChecked());
                mensaje_error("Error: Al conectar con el servidor.");

            }
            SharedPreferences prefe=getSharedPreferences("perfil",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=prefe.edit();
            if(sw_estado.isChecked())
            {
                editor.putString("estado","1");
            }
            else
            {
                editor.putString("estado","0");
            }
            editor.commit();



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


    private void getImage(String id)//
    {
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ImageView bmImage;


            public GetImage(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                Drawable dw;
                if(bitmap!=null) {
                    bmImage.setImageBitmap(bitmap);
                    dw = new BitmapDrawable(getResources(), bitmap);
                }
                else
                {    String mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY + File.separator + "perfil.jpg";
                    bitmap = BitmapFactory.decodeFile(mPath);
                    if( bitmap==null)
                    { dw = getResources().getDrawable(R.mipmap.ic_perfil); }
                    else
                    { dw = new BitmapDrawable(getResources(), bitmap); }

                }
                //Convertir Bitmap a Drawable.

                //se edita la imagen para ponerlo en circulo.
                imagen_circulo(dw,perfil);
                imagen_original=drawableToBitmap(dw);
                guardar_en_memoria(imagen_original);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = getString(R.string.servidor)+"frmMoto.php?opcion=get_imagen&id_moto="+strings[0];//hace consulta ala Bd para recurar la imagen


                Drawable d = getResources().getDrawable(R.mipmap.ic_perfil);
                Bitmap mIcon = drawableToBitmap(d);

                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);

                    if(mIcon==null) {
                        mIcon = drawableToBitmap(d);
                    }
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    String mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY + File.separator + "perfil.jpg";
                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    if( bitmap!=null)
                    { mIcon = bitmap; }


                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage(perfil);
        gi.execute(id);
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
