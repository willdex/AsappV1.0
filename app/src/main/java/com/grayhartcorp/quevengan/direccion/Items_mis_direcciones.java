package com.grayhartcorp.quevengan.direccion;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.historial.CHistorial;

import java.util.ArrayList;

public class Items_mis_direcciones extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CDireccion> items;
    public  Context context;


    public Items_mis_direcciones (Context context,Activity activity, ArrayList<CDireccion> items) {
        this.activity = activity;
        this.items = items;
        this.context=context;
    }

    @Override
    public int getCount() {
        return items.size();
    }
    public  LayoutInflater getLayoutInflater()
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
            v = inf.inflate(R.layout.items_mis_direcciones, null);
        }

        CDireccion dir = items.get(position);




/*
        ContactFragment fragmen1=new ContactFragment();
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
       fragmentTransaction.replace(R.id.container_wrapper, fragmen1);
        fragmentTransaction.commit();
*/
        /*

        BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "Recived",
                        Toast.LENGTH_LONG).show();
                ContactFragment fragment2 = new ContactFragment();
                android.app.FragmentManager fragmentManager = activity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction();
                fragmentTransaction.replace(R.id.container_wrapper, fragment2);
                fragmentTransaction.commit();
            }
        };
*/

        TextView direccion = (TextView) v.findViewById(R.id.direccion);
        TextView nombre=(TextView)v.findViewById(R.id.nombre);
        nombre.setText(dir.getNombre());
        if(nombre.getText().toString().equals(""))
        {
            nombre.setText("Sin nombre");
        }

        direccion.setText(dir.getDetalle());



        return v;
    }


}
