package com.grayhartcorp.quevengan.historial_notificacion;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grayhartcorp.quevengan.R;

import java.util.ArrayList;

/**
 * Created by ELIO on 28/10/2016.
 */

public  class Items_notificacion extends BaseAdapter  {

    protected Activity activity;
    protected ArrayList<CNotificacion> items;
    private Context mContext;
    Bundle savedInstanceState;

    public Items_notificacion(Context c, Bundle b, Activity activity, ArrayList<CNotificacion> items) {
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

    public void addAll(ArrayList<CNotificacion> Pedidos) {
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
            v = inf.inflate(R.layout.items_notificaciones, null);
        }
        CNotificacion ped = items.get(position);




      TextView titulo= (TextView) v.findViewById(R.id.titulo);
        TextView mensaje = (TextView) v.findViewById(R.id.mensaje);
        TextView fecha = (TextView) v.findViewById(R.id.fecha);
        TextView hora = (TextView) v.findViewById(R.id.hora);
        TextView palomita=(TextView)v.findViewById(R.id.palomita);

        titulo.setText(ped.getTitulo());
        mensaje.setText(ped.getMensaje());
        fecha.setText(ped.getFecha());
        hora.setText(ped.getHora());
        if(ped.getLeido()==0)
        {
            palomita.setVisibility(View.VISIBLE);
        }
        else
        {
            palomita.setVisibility(View.INVISIBLE);
        }


        return v;
    }



}