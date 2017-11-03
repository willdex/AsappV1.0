package com.grayhartcorp.quevengan.direccion.lista_direccion_pedido;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.direccion.CDireccion;

import java.util.ArrayList;

/**
 * Created by elisoft on 21-02-17.
 */

public class Direccion_item extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CDireccion> items;
    public Context context;


    public Direccion_item (Context context,Activity activity, ArrayList<CDireccion> items) {
        this.activity = activity;
        this.items = items;
        this.context=context;
    }

    @Override
    public int getCount() {
        return items.size();
    }
    public LayoutInflater getLayoutInflater()
    {
        return  (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<CDireccion> Direccion) {
        for (int i = 0; i < Direccion.size(); i++) {
            items.add(Direccion.get(i));
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
            v = inf.inflate(R.layout.direccion_item, null);
        }

        CDireccion dir = items.get(position);

        TextView nombre=(TextView)v.findViewById(R.id.nombre);

        nombre.setText(dir.getNombre());
        if(nombre.getText().toString().equals(""))
        {
            nombre.setText("Sin nombre");
        }
        if(dir.getId()==-1)
        {
            nombre.setTypeface(null, Typeface.BOLD);
            //agregar direccion....
        }
        else
        {
            nombre.setTypeface(null, Typeface.NORMAL);
        }



        return v;
    }


}
