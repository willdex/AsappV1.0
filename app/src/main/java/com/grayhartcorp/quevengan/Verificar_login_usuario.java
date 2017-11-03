package com.grayhartcorp.quevengan;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.verification.CodeInterceptionException;
import com.sinch.verification.Config;
import com.sinch.verification.InitiationResult;
import com.sinch.verification.InvalidInputException;
import com.sinch.verification.ServiceErrorException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import java.util.ArrayList;
import java.util.List;

public class Verificar_login_usuario extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,View.OnClickListener {

    private static final String TAG = "VerificationActivity";

    private final String APPLICATION_KEY = "b6688b99-7116-4511-b075-16decd541663";

    private Verification mVerification;
    private boolean mIsSmsVerification;
    private boolean mShouldFallback = true;
    private String mPhoneNumber;

    private static final String[] SMS_PERMISSIONS = { android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.ACCESS_NETWORK_STATE };
    private static final String[] FLASHCALL_PERMISSIONS = { android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_NETWORK_STATE };

    TextView mensaje,enviar_mensaje,equivocado;
    EditText inputCode;
    Button codeInputButton;
    int i;
    ProgressBar cargando;
    Handler handle=new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_verificar_login_usuario);
        enviar_mensaje=(TextView)findViewById(R.id.enviar_mensaje);
        equivocado=(TextView)findViewById(R.id.equivocado);
        inputCode=(EditText)findViewById(R.id.inputCode);
        codeInputButton=(Button)findViewById(R.id.codeInputButton);
        cargando=(ProgressBar)findViewById(R.id.cargando);

        Intent intent = getIntent();
        if (intent != null) {
            mPhoneNumber = intent.getStringExtra(Facilitar_permiso.INTENT_PHONENUMBER);
            final String method = intent.getStringExtra(Facilitar_permiso.INTENT_METHOD);
            mIsSmsVerification = method.equalsIgnoreCase(Facilitar_permiso.SMS);

            requestPermissions();
        } else {
            Log.e(TAG, "The provided intent is null.");
            finish();
        }
        mensaje=(TextView)findViewById(R.id.mensaje);
        mensaje.setText("Por favor ingrese el codigo de confirmacion que enviamos por SMS al "+mPhoneNumber);

        enviar_mensaje.setOnClickListener(this);
        codeInputButton.setOnClickListener(this);
        equivocado.setOnClickListener(this);

        progress_en_proceso();
        getSupportActionBar().setTitle("Verificar "+mPhoneNumber);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void requestPermissions() {
        List<String> missingPermissions;
        String methodText;

        if (mIsSmsVerification) {
            missingPermissions = getMissingPermissions(SMS_PERMISSIONS);
            methodText = "SMS";
        } else {
            missingPermissions = getMissingPermissions(FLASHCALL_PERMISSIONS);
            methodText = "calls";
        }

        if (missingPermissions.isEmpty()) {
            createVerification();
        } else {
            if (needPermissionsRationale(missingPermissions)) {
                Toast.makeText(this, "This application needs permissions to read your " + methodText + " to automatically verify your "
                        + "phone, you may disable the permissions once you have been verified.", Toast.LENGTH_LONG)
                        .show();
            }
            ActivityCompat.requestPermissions(this,
                    missingPermissions.toArray(new String[missingPermissions.size()]),
                    0);
        }
    }

    private void createVerification() {
        // It is important to pass ApplicationContext to the Verification config builder as the
        // verification process might outlive the activity.
        Config config = SinchVerification.config()
                .applicationKey(APPLICATION_KEY)
                .context(getApplicationContext())
                .build();
        TextView messageText = (TextView) findViewById(R.id.textView);

        VerificationListener listener = new Verificar_login_usuario.MyVerificationListener();

        if (mIsSmsVerification) {
            messageText.setText(R.string.sending_sms);
            mVerification = SinchVerification.createSmsVerification(config, mPhoneNumber, listener);
            mVerification.initiate();
        } else {
            messageText.setText(R.string.flashcalling);
            mVerification = SinchVerification.createFlashCallVerification(config, mPhoneNumber, listener);
            mVerification.initiate();
        }



    }

    private boolean needPermissionsRationale(List<String> permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Proceed with verification after requesting permissions.
        // If the verification SDK fails to intercept the code automatically due to missing permissions,
        // the VerificationListener.onVerificationFailed(1) method will be executed with an instance of
        // CodeInterceptionException. In this case it is still possible to proceed with verification
        // by asking the user to enter the code manually.
        createVerification();
    }

    private List<String> getMissingPermissions(String[] requiredPermissions) {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }



    private void hideProgressAndShowMessage(int message) {

        TextView messageText = (TextView) findViewById(R.id.textView);
        messageText.setText(message);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.codeInputButton)
        { startActivity(new Intent(this,Registrar_perfil.class));
            //verificar_codigo();
        }
        else if(v.getId()==R.id.enviar_mensaje)
        {
            progress_en_proceso();
            requestPermissions();
        }
        else if(v.getId()==R.id.equivocado)
        {
            onBackPressed();
        }
    }
    public void verificar_codigo()
    {
        String code =inputCode.getText().toString();
        if (!code.isEmpty()) {
            if (mVerification != null) {
                mVerification.verify(code);

                TextView messageText = (TextView) findViewById(R.id.textView);
                messageText.setText("Verification in progress");

            }
        }
    }


    class MyVerificationListener implements VerificationListener {

        @Override
        public void onInitiated(InitiationResult result) {
            Log.d(TAG, "Initialized!");


        }

        @Override
        public void onInitiationFailed(Exception exception) {
            Log.e(TAG, "Verification initialization failed: " + exception.getMessage());
            hideProgressAndShowMessage(R.string.failed);

            if (exception instanceof InvalidInputException) {
                // Incorrect number provided
            } else if (exception instanceof ServiceErrorException) {
                // Verification initiation aborted due to early reject feature,
                // client callback denial, or some other Sinch service error.
                // Fallback to other verification method here.

                if (mShouldFallback) {
                    mIsSmsVerification = !mIsSmsVerification;
                    if (mIsSmsVerification) {
                        Log.i(TAG, "Falling back to sms verification.");
                    } else {
                        Log.i(TAG, "Falling back to flashcall verification.");
                    }
                    mShouldFallback = false;
                    // Initiate verification with the alternative method.
                    requestPermissions();
                }
            } else {
                // Other system error, such as UnknownHostException in case of network error
            }
        }

        @Override
        public void onVerified() {
            Log.d(TAG, "Verified!");

            hideProgressAndShowMessage(R.string.verified);

            startActivity(new Intent(getApplicationContext(),Registrar_perfil.class));

          /*
            SharedPreferences preferencias=getSharedPreferences("perfil", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferencias.edit();
            editor.putString("celular",  mPhoneNumber.substring(4,12));
            editor.putString("proceso","1");
            editor.commit();

            Intent f = new Intent(getApplicationContext(), Registro_usuario.class);
            startActivity(f);
            */
// inviar parametros de numero telefonico...
        }

        @Override
        public void onVerificationFailed(Exception exception) {
            Log.e(TAG, "Verification failed: " + exception.getMessage());
            if (exception instanceof CodeInterceptionException) {
                // Automatic code interception failed, probably due to missing permissions.
                // Let the user try and enter the code manually.

            } else {
                hideProgressAndShowMessage(R.string.failed);
            }
        }
    }

    public void obtener_codigo()
    {
        try {
            Uri smsUri = Uri.parse("content://sms/inbox");
            Cursor cursor = getContentResolver().query(smsUri, null, null, null, null);
             /* Moving To First */
            if (!cursor.moveToFirst()) { /* false = cursor is empty */
                return;
            }
            for (int k = 0; k < cursor.getColumnCount() && !cursor.getString(2).equals("+46769446575"); k++) {
                cursor.moveToNext();
            }
            if (cursor.getString(2).equals("+46769446575")) {
                inputCode.setText(obtener_codigo(cursor.getString(12)));
            }
            cursor.close();
        }catch (Exception e)
        {
        }
    }

    public String obtener_codigo(String texto)
    {String codigo="";
        for (int i=0;i<texto.length();i++)
        {
            if(es_numero(String.valueOf(texto.charAt(i))))
            {
                codigo+=texto.charAt(i);
            }
        }
        return codigo;
    }
    public boolean es_numero(String numero)
    {
        try{
            Long.parseLong(numero);
        }catch (Exception e)
        {
            return false;
        }
        return true;
    }


    public  void progress_en_proceso()
    {

        i=0;
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (i<100)
                {
                    i=i+1;

                    handle.post(new Runnable() {
                        @Override
                        public void run() {
                            cargando.setProgress(i);
                            if(i==50)
                            {
                                obtener_codigo();

                            }
                            if(i==80)
                            {
                                verificar_codigo();
                            }

                        }
                    });
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
