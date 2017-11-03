package com.grayhartcorp.quevengan.contactos;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grayhartcorp.quevengan.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class Lista_contactos extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CContacto> items;
    String nombre;
    ImageView im_perfil;
    String url="";
    int usuario=0;



    public Lista_contactos (Activity activity, ArrayList<CContacto> items,String url,int usuario ) {
        this.activity = activity;
        this.items = items;
        this.url=url;
        this.usuario=usuario;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<CContacto> Contacto) {
        for (int i = 0; i < Contacto.size(); i++) {
            items.add(Contacto.get(i));
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
            v = inf.inflate(R.layout.activity_lista_contactos, null);
        }

        CContacto dir = items.get(position);

        TextView nombre = (TextView) v.findViewById(R.id.category);
        nombre.setText(dir.getNombre());

        TextView celular = (TextView) v.findViewById(R.id.texto);
        celular.setText(dir.getNumero());



        return v;
    }





}