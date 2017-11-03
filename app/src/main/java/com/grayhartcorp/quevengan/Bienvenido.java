package com.grayhartcorp.quevengan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Bienvenido extends AppCompatActivity implements View.OnClickListener {
TextView nombre;
    Button siguiente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_bienvenido);
        nombre=(TextView)findViewById(R.id.nombre_usuario);
        siguiente=(Button)findViewById(R.id.siguiente);


        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        nombre.setText(perfil.getString("nombre","")+" "+perfil.getString("apellido",""));
        siguiente.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.siguiente)
        {
            SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
            SharedPreferences.Editor editor=usuario.edit();
            editor.putString("id_empresa", "");
            editor.putString("usuario", "1");
            editor.putString("login_usuario", "1");
            editor.commit();
            Intent intent = new Intent(this,Menu_p.class);
            startActivity(intent);
            finish();
        }
    }
}
