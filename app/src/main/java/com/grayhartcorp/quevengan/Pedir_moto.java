package com.grayhartcorp.quevengan;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class Pedir_moto extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    int id,i;
    double latitud,longitud;
    String nombre,detalle;
    ProgressBar cargando;
    Handler handle=new Handler();
    TextView numero;
    Button cancelar;
    boolean enviar_pedido=true;
    LinearLayout linea_cancelar;




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_moto);
        numero=(TextView)findViewById(R.id.numero);
        cancelar=(Button)findViewById(R.id.cancelar);
        linea_cancelar=(LinearLayout)findViewById(R.id.linear_cancelar);



        try{
            Bundle bundle=getIntent().getExtras();
            id=Integer.parseInt(bundle.getString("id_direccion"));
            latitud=Double.parseDouble(bundle.getString("latitud"));
            longitud=Double.parseDouble(bundle.getString("longitud"));
            nombre=bundle.getString("nombre");
            detalle=bundle.getString("detalle");
        }catch (Exception e)
        {
         finish();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
/*
        i=0;
        cargando=(ProgressBar)findViewById(R.id.cargando);

      new Thread(new Runnable() {
            @Override
            public void run() {

                while (i<100&&enviar_pedido==true)
                {
                    i=i+1;
                    handle.post(new Runnable() {
                        @Override
                        public void run() {
                            cargando.setProgress(i);
                            numero.setText(i+"/"+cargando.getMax());
                            if(i==100)
                            {  mensaje();}
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

        bk_cancelar.setOnClickListener(this);
         */
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        try {
           Marker marker= mMap.addMarker(new MarkerOptions().position(new LatLng(latitud,longitud)).title(nombre).snippet(detalle));

            //agregaranimacion al mover la camara...
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitud,longitud))      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            marker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }catch (Exception e)
        {finish();}
    }
    public  void mensaje()
    {
       linea_cancelar.setVisibility(View.INVISIBLE);
        Toast.makeText(this,"terminado",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cancelar:
                enviar_pedido=false;
               linea_cancelar.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
