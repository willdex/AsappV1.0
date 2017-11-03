package com.grayhartcorp.quevengan.tarifa;

/**
 * Created by ELIO on 01/11/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.grayhartcorp.quevengan.R;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.direccion.CDireccion;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by ELIO on 28/10/2016.
 */

public class Items_tarifa extends BaseAdapter {


    protected Activity activity;
    protected ArrayList<CTarifa> items;
    public Items_tarifa (Activity activity, ArrayList<CTarifa> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<CTarifa> Tarifa) {
        for (int i = 0; i < Tarifa.size(); i++) {
            items.add(Tarifa.get(i));
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
            v = inf.inflate(R.layout.item_tarifa, null);
        }

        CTarifa ped = items.get(position);

        TextView distancia = (TextView) v.findViewById(R.id.distancia);
        TextView monto = (TextView) v.findViewById(R.id.monto);
        distancia.setText(convertir_numero(String.valueOf(ped.getDistancia())));
        monto.setText(String.valueOf(ped.getMonto())+" bs.");

        return v;
    }
    public String convertir_numero(String numero)
    {double decimal=Double.parseDouble(numero);
        if(decimal>=1000)
        {
            decimal=decimal/1000;
            numero=decimal+" km.";
        }
        else
         numero=numero+" mt.";
        return String.valueOf(numero);
    }

}
