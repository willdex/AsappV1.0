package com.grayhartcorp.quevengan;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Carrera_us extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback {

    private String snombre,scelular,sid_moto,smarca,splaca,sid_pedido,sid_carrera,sid_usuario;
    private GoogleMap mMap;
    Suceso suceso;



    public LocationManager locationManager;
    public LocationListener locationListener;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_carrera_us);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        //verificamos si se esta recibiendo los parametros necesarios para este formulario...
        try{
            Bundle intro=getIntent().getExtras();
            snombre=intro.getString("nombre");
            scelular=intro.getString("celular");
            sid_moto=intro.getString("id_monto");
            smarca=intro.getString("marca");
            splaca=intro.getString("placa");
            sid_pedido=intro.getString("id_pedido");

        }catch (Exception e)
        {
            finish();
        }
        // fin ... si no se recibe los parametros correspondientes se cerrara la funcion...






// localizacion automatica
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                SharedPreferences prefe=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                int sw=0;
                if(prefe.getString("id_pedido","").length()>=1) {
                    pintar_recorrido();
                    sw=1;
                }
                else
                {  if(sw==1) {
                    mensaje("Se ha finalizado su pedido.");
                    finish();
                   sw=2;
                }
                }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
            return;
        } else {
            locationManager.requestLocationUpdates("gps", 3000, 0, locationListener);
        }
        //fin de la locatizaocion automatica...












    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onClick(View view) {

    }





    public void pintar_recorrido() {
        boolean sw=true;
        boolean sw_punto=false;
        try {
            int id = Integer.parseInt(sid_pedido);
        }catch (Exception e)
        {
            sw=false;
        }
        if(sw==true) {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "easymoto", null, 1);
            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = null;
            int numero =1;
            String auxiliar="";
            fila = bd.rawQuery("select * from puntos_carrera where id_pedido=" + sid_pedido + " ORDER BY fecha ASC ", null);

            try {
                 mMap.clear();
                LatLng punto = new LatLng(0, 0);
                PolylineOptions polylineOptions = new PolylineOptions();
                if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
                    do {
                        double lat = Double.parseDouble(fila.getString(2));
                        double lon = Double.parseDouble(fila.getString(3));
                        String id_carrera=fila.getString(1);
                        punto = new LatLng(lat, lon);
                        polylineOptions.add(punto);
                        if(auxiliar.equals(id_carrera)==false)
                        {   auxiliar=id_carrera;
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_fin))
                                    .position(punto)
                                    .anchor((float)0.5,(float)0.5)
                                    .flat(true)
                                    .rotation(0)
                                    .title("#"+numero));
                            numero++;
                        }
                        sw_punto=true;

                    } while (fila.moveToNext());
                }
                if(sw_punto==true) {
                    bd.close();
                    mMap.addPolyline(polylineOptions.width(15).color(-16776881));

                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto))
                            .position(punto)
                            .anchor((float)0.5,(float)0.5)
                            .flat(true)
                            .rotation(0));
                    //agregaranimacion al mover la camara...
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(punto)      // Sets the center of the map to Mountain View
                            .zoom(18)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(10)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                     mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            } catch (Exception e) {

            }
        }

    }

    public void mensaje(String mensaje)
    {
        Toast toast =Toast.makeText(this,mensaje,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }



}
