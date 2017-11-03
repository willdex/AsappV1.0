package com.grayhartcorp.quevengan;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Lista_contactos_grupo extends AppCompatActivity {
    private String[] grupo = {"Edgar Elio", "Neli Cruz", "Gustavo Arancibia", "Jhony Ortega"};
    ListView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos_grupo);
        lista=(ListView)findViewById(R.id.lista_contactos_grupo);

        DisplayMetrics dm=new DisplayMetrics() ;
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int ancho=dm.widthPixels;
        int alto=dm.heightPixels;
        getWindow().setLayout((int)(ancho*5),(int)(alto*5));


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, grupo);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                itemp(lista.getItemAtPosition(posicion), grupo[posicion]);
            }

        });

    }
    public void itemp(Object oj,String dato)
    {
        Toast.makeText(this,"Población de " + oj + " es " +dato,Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
