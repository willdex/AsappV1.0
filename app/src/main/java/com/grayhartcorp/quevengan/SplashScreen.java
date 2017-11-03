package com.grayhartcorp.quevengan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.grayhartcorp.quevengan.contactos.Registro_usuario;
import com.grayhartcorp.quevengan.menu_motista.Menu_motista;

import static android.content.ContentValues.TAG;

public class SplashScreen extends Activity {
    SharedPreferences datos_perfil,dato_moto;
    ImageView im_gh, icon;
    FrameLayout fondo;
    VideoView videoView;
    //AsyncPlayer mAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       /*final Thread myThread = new Thread() {
            @Override
            public void run() {
                setContentView(R.layout.activity_splash_screen);
                im_gh = (ImageView) findViewById(R.id.spgh);
                icon = (ImageView)findViewById(R.id.iconas);
                fondo = (FrameLayout)findViewById(R.id.fondo);
                //fondo.setVisibility(View.INVISIBLE);
                icon.setVisibility(View.INVISIBLE);

                Animation agr, ogr, fgr;
                agr = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.girar);  //Animacion GH
                agr.reset();
                ogr = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotar);  //Animacion Asapp
                ogr.reset();
                fgr = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fondo);  //Animacion del fondo
                fgr.reset();

                im_gh.startAnimation(agr);
                im_gh.setVisibility(View.INVISIBLE);
                fondo.setVisibility(View.VISIBLE);
                icon.setVisibility(View.VISIBLE);
                fondo.startAnimation(fgr);
                icon.startAnimation(ogr);
                //icon.setVisibility(View.INVISIBLE);


                //mAsync = new AsyncPlayer(TAG);
                //mAsync.play(getApplicationContext(),
                        //Uri.parse("android.resource://" +getPackageName()+"/raw/sonido2"),
                        //false,
                        //AudioManager.STREAM_MUSIC);

                try {
                    sleep(8000);
                    Intent intent = new Intent(getApplicationContext(), Login.class);   //AQUI SE CAMBIO Inicio.class por Login.class para saltar activity redundante
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };*/

        //  verificar_version();

        //si no tiene actualizaciones.. continua con el inicio.. normal...
        dato_moto=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        boolean b = true;

        if(dato_moto.getString("login_usuario","0").equals("1")) {
            Intent intent1 = new Intent (this,Menu_p.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            b = false;
            //startActivity(new Intent(this, Menu_p.class));
            finish();
        }else if (dato_moto.getString("login_moto","0").equals("1"))
        {
            //startActivity(new Intent(this, Menu_motista.class));
            Intent intent2 = new Intent (getApplicationContext(),Menu_motista.class);
            startActivity(intent2);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            b = false;
            finish();
        }else if (dato_moto.getString("celular","")!="" &&dato_moto.getString("proceso","")=="1" &&  dato_moto.getString("login_usuario","0").equals("0"))
        {
            //startActivity(new Intent(this,Registro_usuario.class));
            Intent intent3 = new Intent (getApplicationContext(),Registro_usuario.class);
            startActivity(intent3);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            b = false;
            finish();
        }

/*******************************************************************************************************************************/
        if (b) {
            //myThread.start();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            setContentView(R.layout.activity_splash_screen);

            //getSupportActionBar().hide();

            videoView = (VideoView) findViewById(R.id.videoView);

            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.racetemplate);

            videoView.setVideoURI(video);

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (isFinishing())
                        return;

                    startActivity(new Intent(SplashScreen.this, Login.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            });

            videoView.start();
        }
    }
}

