package com.grayhartcorp.quevengan.historial_notificacion;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.Pedido_us;
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.menu_motista.Notificacion_pedido_moto;


import java.util.ArrayList;

/**
 * Created by ELIO on 15/11/2016.
 */


public class Notificacion extends AppCompatActivity {
    ListView lista_carrera;
    ArrayList<CNotificacion> carrera;
    private ProgressDialog pDialog;
    int id_pedido;
    Bundle save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.save=savedInstanceState;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_notificacion);
        lista_carrera = (ListView) findViewById(R.id.lista_notificacion);

        lista_carrera.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(carrera.get(i).getTipo().equals("1"))
                {
                   pedido_usuario();
                }else if(carrera.get(i).getTipo().equals("2"))
                {
                    notificacion_pedido_movil(carrera.get(i).getId_pedido(),carrera.get(i).getNombre(),carrera.get(i).getLatitud(),carrera.get(i).getLongitud(),carrera.get(i).getEmpresa(),carrera.get(i).getDireccion(),carrera.get(i).getNombre_direccion(),carrera.get(i).getDetalle_direccion());

                }
             modificacion(String.valueOf(carrera.get(i).getId()));

            }
        });



        cargar_carrera_en_la_lista();

    }

    private void notificacion_pedido_movil(String id_pedido, String nombre, String latitud, String longitud,String empresa,String direccion,String nombre_direccion,String detalle_direccion) {

        Intent moto = new Intent(getApplicationContext(),Notificacion_pedido_moto.class);
        moto.putExtra("id_pedido",id_pedido);
        moto.putExtra("nombre",nombre);
        moto.putExtra("latitud",latitud);
        moto.putExtra("longitud",longitud);
        moto.putExtra("empresa",empresa);
        moto.putExtra("direccion",direccion);
        moto.putExtra("nombre_direccion",nombre_direccion);
        moto.putExtra("detalle_direccion",detalle_direccion);
        startActivity(moto);
    }

    private void pedido_usuario() {
        Intent usuario = new Intent(getApplicationContext(),Pedido_us.class);
        startActivity(usuario);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                cargar_carrera_en_la_lista( );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mis__contactos, menu);
        return true;
    }


    public void modificacion(String id) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("leido", "1");
        int cant = bd.update("notificacion", registro, "id=" +id, null);
        bd.close();

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void actualizar_lista() {

        Items_notificacion adaptador = new Items_notificacion(Notificacion.this,save,this, carrera);
        lista_carrera.setAdapter(adaptador);

    }



    public void cargar_carrera_en_la_lista( ) {
        carrera = new ArrayList<CNotificacion>();
try {
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
            "easymoto", null, 1);
    SQLiteDatabase bd = admin.getWritableDatabase();
    Cursor fila = bd.rawQuery("select * from notificacion  ORDER BY id DESC ", null);

    if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
        Log.e("id", fila.getString(0) + " leido=" + fila.getString(11));
        int id = Integer.parseInt(fila.getString(0));
        int leido = Integer.parseInt(fila.getString(11));
        do {
            CNotificacion hi = new CNotificacion(id, fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5), fila.getString(6), fila.getString(7), fila.getString(8), fila.getString(9), fila.getString(10), leido);
            hi.setEmpresa(fila.getString(12));
            hi.setDireccion(fila.getString(13));
            hi.setNombre_direccion(fila.getString(14));
            hi.setDetalle_direccion(fila.getString(15));
            carrera.add(hi);
        } while (fila.moveToNext());

    } else
        Toast.makeText(this, "No hay registrados",
                Toast.LENGTH_SHORT).show();

    bd.close();
    actualizar_lista();
}catch (Exception e)
{

}
    }







}

