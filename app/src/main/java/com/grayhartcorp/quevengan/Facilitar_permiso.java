package com.grayhartcorp.quevengan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.verification.Config;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.VerificationListener;

import java.util.ArrayList;
import java.util.List;

public class Facilitar_permiso extends AppCompatActivity implements View.OnClickListener {
Button siguiente;
    public static final String SMS = "sms";
    public static final String INTENT_PHONENUMBER = "phonenumber";
    public static final String INTENT_METHOD = "method";
    boolean permiso=false;

    private static final String[] SMS_PERMISSIONS = { android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.ACCESS_NETWORK_STATE };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilitar_permiso);
        siguiente=(Button)findViewById(R.id.siguiente);

        siguiente.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.siguiente)
        {



            final AlertDialog.Builder dialogo = new AlertDialog.Builder(this);

            final LayoutInflater inflater = getLayoutInflater();

            final View dialoglayout = inflater.inflate(R.layout.popup_dialogo_aceptar_cancelar, null);
            final TextView Tv_titulo = (TextView) dialoglayout.findViewById(R.id.tv_titulo);
            final TextView Tv_mensaje = (TextView) dialoglayout.findViewById(R.id.tv_mensaje);
            final Button Bt_aceptar = (Button) dialoglayout.findViewById(R.id.bt_aceptar);
            final Button Bt_cancelar = (Button) dialoglayout.findViewById(R.id.bt_cancelar);



            Tv_mensaje.setText("PERMISO PARA SMS");
            Tv_titulo.setText("PERMISO");
            Bt_aceptar.setText("SI");
            Bt_cancelar.setText("NO");
            dialogo.setView(dialoglayout);
            dialogo.setCancelable(false);


            final AlertDialog finalDialogo =dialogo.create();
            finalDialogo.show();
            Bt_aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aceptar();
                    finalDialogo.dismiss();
                }
            });
            Bt_cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalDialogo.dismiss();
                }
            });

        }
    }

    private void aceptar() {
        SharedPreferences dato=getSharedPreferences("usuario",MODE_PRIVATE);
        if(dato.getString("celular","").equals("")==false){
        openActivity(dato.getString("celular",""),SMS);
        }
        else
        {
            mensaje("Por favor vuelva a ingresar su numero de celular");
            startActivity(new Intent(this,Login_usuario.class));
        }

    }
    private void openActivity(String phoneNumber, String method) {
        Intent verification = new Intent(this, Verificar_login_usuario.class);
        verification.putExtra(INTENT_PHONENUMBER, phoneNumber);
        verification.putExtra(INTENT_METHOD, method);
        startActivity(verification);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    public void mensaje(String mensaje)
    {
        Toast toast =Toast.makeText(this,mensaje,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

}
