package com.grayhartcorp.quevengan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grayhartcorp.quevengan.contactos.Registro_usuario;

public class Validacion extends AppCompatActivity implements View.OnClickListener{
    SharedPreferences datos_perfil;
    TextView mensaje,equivocado,enviar_mensaje;
    EditText codigo;
    Button siguiente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validacion);
        mensaje=(TextView)findViewById(R.id.mensaje);
        codigo=(EditText)findViewById(R.id.codigo);
        equivocado=(TextView)findViewById(R.id.equivocado);
        enviar_mensaje=(TextView)findViewById(R.id.enviar_mensaje);
        //sharePreferences sirve para guardar datos en la memoria del telefono....
        SharedPreferences datos_perfil=getSharedPreferences("perfil", Context.MODE_PRIVATE);

        mensaje.setText("Por favor ingrese el codigo de confirmacion que enviamos por SMS al +591 "+datos_perfil.getString("celular","").toString());
        // getSupporActionBar es para colocar el back en la cabecera.
        getSupportActionBar().setTitle("Verificar +591 "+datos_perfil.getString("celular","").toString());

  enviar_mensaje.setOnClickListener(this);
  equivocado.setOnClickListener(this);
    }
    public void Registrar_usuario(View v)
    {
        if(codigo.getText().toString().length()>=4)
        {
            SharedPreferences preferencias=getSharedPreferences("perfil",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferencias.edit();
            editor.putString("codigo",codigo.getText().toString());
            editor.putString("proceso","1");
            editor.commit();
            Intent f = new Intent(this, Registro_usuario.class);
            startActivity(f);
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Importante");
            builder.setMessage("El Codigo que Ingreso es Incorrecto.Por favor intèntelo de nuevo.");
            builder.setPositiveButton("OK",null);
            builder.create();
            builder.show();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.enviar_mensaje:
                Toast.makeText(this,"Enviar mensaje",Toast.LENGTH_SHORT).show();
            break;
            case R.id.equivocado:
                SharedPreferences preferencias=getSharedPreferences("perfil",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferencias.edit();
                editor.putString("codigo","");
                editor.putString("celular","");
                editor.commit();
                finish();
                break;
        }
    }
}
