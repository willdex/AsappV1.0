package com.grayhartcorp.quevengan.carreras;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.VectorEnabledTintResources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.sinch.sanalytics.client.HttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;






/**
 * Created by ELIO on 28/10/2016.
 */

public  class Items_carreras extends BaseAdapter  {

    String url_pagina;
    String nombre;
    protected Activity activity;
    protected ArrayList<CCarrera> items;
    private Context mContext;
    Bundle savedInstanceState;
    ImageView im_mapa;

    CCarrera ped;
    TextView tv_numero;

    public Items_carreras(Context c,Bundle b,Activity activity, ArrayList<CCarrera> items) {
        this.activity = activity;
        this.items = items;
        this.savedInstanceState = b;
        this.mContext=c;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<CCarrera> Pedidos) {
        for (int i = 0; i < Pedidos.size(); i++) {
            items.add(Pedidos.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_carreras, null);
        }
        ped = items.get(position);



        TextView fecha = (TextView) v.findViewById(R.id.fecha);
        TextView monto = (TextView) v.findViewById(R.id.monto);
         tv_numero = (TextView) v.findViewById(R.id.tv_numero);
        ImageView im_mapa=(ImageView)v.findViewById(R.id.im_mapa);
        this.im_mapa=im_mapa;

        //poner en el String todos los puntos registrados.....
        boolean sw=true;

        try {
            int id = ped.getId_pedido();
        }catch (Exception e)
        {
            sw=false;
        }
        if(sw==true) {
            fecha.setText(ped.getFecha_fin());
            monto.setText(ped.getMonto()+" Bs.");

            String st_mapa=ped.getRuta();

            String st_nombre=""+ped.getId_pedido()+"_"+ped.getId()+".jpg";
            if(carrera_en_vista(im_mapa,st_nombre)==false)
            {
                getImage(st_mapa,st_nombre);
            }


           /*
                WebView mapa=(WebView) v.findViewById(R.id.mapa);
                int ancho=mapa.getRootView().getWidth();
        mapa.getSettings().setJavaScriptEnabled(true);
        mapa.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mapa.getSettings().setLoadWithOverviewMode(true); mapa.getSettings().setUseWideViewPort(true);

              String inicio="markers=color:red|label:I";
        String fin="";
        String recorrido="path=color:0x0000ff|weight:5";

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(activity, "easymoto", null, 1);
            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = null;
            int numero = 1;
            String auxiliar = "";
            String sql="select * from puntos_carrera where id_pedido=" + ped.getId_pedido() + " and id_carrera=" + ped.getId() + " ORDER BY numero ASC ";
            fila = bd.rawQuery(sql, null);

            try {

                if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
                    double lat1 = Double.parseDouble(fila.getString(2));
                    double lon1 = Double.parseDouble(fila.getString(3));
                    inicio = inicio + "|" + lat1 + "," + lon1;
                    do {
                        double lat = Double.parseDouble(fila.getString(2));
                        double lon = Double.parseDouble(fila.getString(3));
                        String dato=lat+","+lon;
                        if(auxiliar.equals(dato)==false) {
                            auxiliar=dato;
                            recorrido = recorrido + "|" + lat + "," + lon;
                            fin = "|" + lat + "," + lon;
                            sw_punto = true;
                        }
                    } while (fila.moveToNext());
                }
                if (sw_punto == true) {
                    fin = "markers=color:blue|label:F" + fin;
                    //String st_mapa="https://maps.googleapis.com/maps/api/staticmap?size=600x300&scale=2&maptype=roadmap&"+inicio+"&"+fin+"&"+recorrido;
                    String st_mapa=ped.getRuta();
                    //   mapa.loadUrl(st_mapa);
                    //mostrar_mapa(st_mapa,im_mapa,ped.getId_pedido()+"_"+ped.getId()+".jpg");
                   String st_nombre=""+ped.getId_pedido()+"_"+ped.getId()+".jpg";
                    if(carrera_en_vista(im_mapa,st_nombre)==false)
                    {
                        getImage(st_mapa,st_nombre);
                    }




            } catch (Exception e) {

            }
            */


        }




        return v;
    }



    private void getImage(String id,String nombre)//
    {this.nombre=nombre;
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
                { }
                else
                {
                   bmImage.setImageBitmap(bitmap);
                    bmImage.setAdjustViewBounds(true);
                    bmImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    bmImage.setPadding(0, 0, 0, 0);
                    guardar_en_memoria(bitmap,nombre);
                    tv_numero.setText(ped.getNumero());
                    tv_numero.setVisibility(View.VISIBLE);
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

        GetImage gi = new GetImage(im_mapa,nombre);
        gi.execute(id);
    }

    private void guardar_en_memoria(Bitmap bitmapImage,String nombre)
    {
        File file=null;
        FileOutputStream fos = null;
        try {
            String APP_DIRECTORY = "Asapp/";//nombre de directorio
            String MEDIA_DIRECTORY = APP_DIRECTORY + "historial";//nombre de la carpeta
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

    public boolean carrera_en_vista(ImageView imagen,String nombre)
    { boolean sw_carrera=false;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + "Asapp/historial"
                + File.separator +nombre;


        File newFile = new File(mPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);

        if( bitmap!=null)
        {
            imagen.setImageBitmap(bitmap);
            imagen.setAdjustViewBounds(true);
            imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imagen.setPadding(0, 0, 0, 0);

            tv_numero.setText(ped.getNumero());
            tv_numero.setVisibility(View.VISIBLE);

            sw_carrera=true;
        }

        return sw_carrera;
    }


}