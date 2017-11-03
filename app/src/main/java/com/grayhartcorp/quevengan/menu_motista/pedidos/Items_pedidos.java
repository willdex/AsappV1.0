package com.grayhartcorp.quevengan.menu_motista.pedidos;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.Suceso;
import com.grayhartcorp.quevengan.direccion.CDireccion;
import com.grayhartcorp.quevengan.menu_motista.Menu_motista;

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
import java.util.StringTokenizer;

/**
 * Created by ELIO on 28/10/2016.
 */

public class Items_pedidos  extends BaseAdapter {


    protected Activity activity;
    protected ArrayList<CPedidos> items;
    LayoutInflater inflater;
    Suceso suceso;
    ImageView im_me_gusta;

    public Items_pedidos (Activity activity, ArrayList<CPedidos> items,LayoutInflater inflater) {
        this.activity = activity;
        this.items = items;
        this.inflater=inflater;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<CPedidos> Pedidos) {
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
            v = inf.inflate(R.layout.item_pedidos, null);
        }

        CPedidos ped = items.get(position);

        TextView tv_nombre_direccion = (TextView) v.findViewById(R.id.tv_nombre_direccion);
        TextView tv_nombre_empresa = (TextView) v.findViewById(R.id.tv_nombre_empresa);
        TextView tv_hora = (TextView) v.findViewById(R.id.tv_fecha);
        TextView tv_nombre_motista = (TextView) v.findViewById(R.id.tv_nombre_motista);
        TextView tv_monto_total = (TextView) v.findViewById(R.id.tv_monto_total);
        im_me_gusta=(ImageView)v.findViewById(R.id.im_me_gusta);


        tv_nombre_direccion.setText(ped.getNombre_cliente());
        tv_nombre_empresa.setText(ped.getNombre_empresa());
        tv_nombre_motista.setText(ped.getNombre_direccion());
        tv_hora.setText(ped.getHora());

        if(ped.getPuntuacion()==0)
        {
            im_me_gusta.setImageResource(R.mipmap.ic_manito_neutro);
        }
        else if(ped.getPuntuacion()==1)
        {
            im_me_gusta.setImageResource(R.mipmap.ic_me_gusta);
        }else if(ped.getPuntuacion()==2)
        {
            im_me_gusta.setImageResource(R.mipmap.ic_no_me_gusta);
        }

        if(ped.getEstado()==2)
        {
            tv_monto_total.setText(String.valueOf(ped.getMonto_total()+" Bs."));
            tv_monto_total.setTextColor(Color.parseColor("#4e0034"));
            tv_monto_total.setBackgroundResource(R.drawable.bk_correcto);
        }
        else if(ped.getEstado()==3)
        {
            tv_monto_total.setText("CANCELADO");
            tv_monto_total.setTextColor(Color.RED);
            tv_monto_total.setBackgroundResource(R.drawable.bk_cancelar);
        }





        return v;
    }


    }