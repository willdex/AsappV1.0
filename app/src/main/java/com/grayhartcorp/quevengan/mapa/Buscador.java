package com.grayhartcorp.quevengan.mapa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.grayhartcorp.quevengan.Menu_p;
import com.grayhartcorp.quevengan.Pedir_moto;
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.direccion.CDireccion;
import com.grayhartcorp.quevengan.direccion.Items_mis_direcciones;
import com.grayhartcorp.quevengan.direccion.Registrar_direccion;
import com.grayhartcorp.quevengan.menu_motista.Carrera;
import com.grayhartcorp.quevengan.tarifa.Tarifa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buscador extends AppCompatActivity implements SearchView.OnQueryTextListener,View.OnClickListener{
ListView lista_buscar;


    LinearLayout linear_casa,linear_trabajo;
    ArrayList<CDireccion> direccion ;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.agregar:
                Intent i=new Intent(this,Registrar_direccion.class);
                startActivity(i);
                return true;
            case R.id.actualizar:
                cargar_direccion_en_la_lista("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
//se agregar la cabecera. con su busqueda
        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buscador);
        lista_buscar=(ListView)findViewById(R.id.lista_busqueda);
        linear_casa=(LinearLayout)findViewById(R.id.linear_casa);
        linear_trabajo=(LinearLayout)findViewById(R.id.linear_trabajo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cargar_direccion_en_la_lista("");


// evento de onclick en la Lista de Busqueda ...
        lista_buscar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CDireccion hi=new CDireccion();
                hi=direccion.get(i);
                mensaje(hi);

            }
        });

      linear_casa.setOnClickListener(this);
      linear_trabajo.setOnClickListener(this);
    }




    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        cargar_direccion_en_la_lista( newText);

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linear_casa:
                Intent i=new Intent(this,Registrar_direccion.class);
                i.putExtra("nombre","CASA");
                startActivity(i);
                break;
            case R.id.linear_trabajo:
                Intent in=new Intent(this,Registrar_direccion.class);
                in.putExtra("nombre","TRABAJO");
                startActivity(in);
                break;
        }
    }

    public void mensaje(CDireccion direccion)
    {
        Toast.makeText(this,"Direccion :"+direccion.getDetalle(),Toast.LENGTH_SHORT).show();
        Intent carrerra=new Intent(this,Pedir_moto.class);
        carrerra.putExtra("id_direccion",String.valueOf(direccion.getId()));
        carrerra.putExtra("nombre",direccion.getNombre());
        carrerra.putExtra("detalle",direccion.getDetalle());
        carrerra.putExtra("latitud",String.valueOf(direccion.getLatitud()));
        carrerra.putExtra("longitud",String.valueOf(direccion.getLongitud()));
        startActivity(carrerra);
    }
/*
    public void cargar_direccion_en_la_lista(String nombre)
    {
        direccion= new ArrayList<CDireccion>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
      Cursor fila = bd.rawQuery("SELECT id ,detalle,latitud, longitud,id_empresa,id_usuario,CASE nombre WHEN '' THEN 'Sin nombre' ELSE nombre  END nombre FROM direccion WHERE nombre LIKE '%"+nombre+"%' ORDER BY id DESC", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {
                CDireccion hi =new CDireccion(Integer.parseInt(fila.getString(0)),fila.getString(6),fila.getString(1),Double.parseDouble(fila.getString(2)),Double.parseDouble(fila.getString(3)),fila.getString(4), fila.getString(5));
                direccion.add(hi);
            } while(fila.moveToNext());

        } else
            Toast.makeText(this, "No hay registrados" ,
                    Toast.LENGTH_SHORT).show();

        bd.close();
     Items_mis_direcciones adaptador = new Items_mis_direcciones(getApplicationContext(),this,direccion);
     lista_buscar.setAdapter(adaptador);
    }
*/

    public void cargar_direccion_en_la_lista(String nombre)
    {
        direccion= new ArrayList<CDireccion>();
        SharedPreferences empresa =getSharedPreferences("empresa",MODE_PRIVATE);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("SELECT id ,detalle,latitud, longitud,id_empresa,id_usuario,CASE nombre WHEN '' THEN 'Sin nombre' ELSE nombre  END nombre FROM direccion WHERE id_empresa="+empresa.getString("id","-1")+" and nombre LIKE '%"+nombre+"%' ORDER BY id DESC", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {
                CDireccion hi =new CDireccion(Integer.parseInt(fila.getString(0)),fila.getString(6),fila.getString(1),Double.parseDouble(fila.getString(2)),Double.parseDouble(fila.getString(3)),fila.getString(4), fila.getString(5));
                direccion.add(hi);
            } while(fila.moveToNext());

    } else
            Toast.makeText(this, "No hay registrados" ,
                    Toast.LENGTH_SHORT).show();

        bd.close();
        Items_mis_direcciones adaptador = new Items_mis_direcciones(getApplicationContext(),this,direccion);
        lista_buscar.setAdapter(adaptador);
    }



}
