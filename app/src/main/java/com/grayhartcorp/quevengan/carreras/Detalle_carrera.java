package com.grayhartcorp.quevengan.carreras;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
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
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;

public class Detalle_carrera extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView nombre_moto,marca;
   int id_carrera,distancia,opciones,id_usuario,id_moto,id_tarifa;
    String detalle_inicio,detalle_fin;
    TextView fecha_inicio,direccion,direccion_fin,direccion_inicio,monto,nombre_completo;
    double latitud_inicio,longitud_inicio,latitud_fin,longitud_fin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_carrera);
        fecha_inicio=(TextView)findViewById(R.id.fecha_inicio);
        direccion=(TextView)findViewById(R.id.direccion);
        direccion_inicio=(TextView)findViewById(R.id.direccion_inicio);
        direccion_fin=(TextView)findViewById(R.id.direccion_fin);
        monto=(TextView)findViewById(R.id.monto);
        nombre_completo=(TextView)findViewById(R.id.nombre_completo);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try{
            Bundle bundle=getIntent().getExtras();
            id_carrera=Integer.parseInt(bundle.getString("id_carrera",""));
            latitud_inicio=Double.parseDouble( bundle.getString("latitud_inicio",""));
            longitud_inicio=Double.parseDouble(bundle.getString("longitud_inicio",""));
            latitud_fin=Double.parseDouble(bundle.getString("latitud_fin",""));
            longitud_fin=Double.parseDouble( bundle.getString("longitud_fin",""));
            monto.setText(bundle.getString("monto","")+" bs.");
            direccion_inicio.setText(bundle.getString("detalle_inicio",""));
            direccion_fin.setText(bundle.getString("detalle_fin",""));
            bundle.getString("distancia","");
            fecha_inicio.setText(bundle.getString("fecha_inicio",""));
            bundle.getString("fecha_fin","");
            bundle.getString("opciones","");
            bundle.getString("id_usuario","");
            bundle.getString("id_moto","");
            bundle.getString("id_tarifa","");
            direccion.setText(direccion_fin.getText());

        }catch (Exception e)
        {
            finish();
        }
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
        LatLng inicio = new LatLng(latitud_inicio,longitud_inicio);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_inicio))
                .position(inicio)
                .anchor((float)0.5,(float)0.5)
                .flat(true)
                .rotation(0)
        .title("Inicio"));
        LatLng fin = new LatLng(latitud_fin,longitud_fin);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_fin))
                .position(fin)
                .anchor((float)0.5,(float)0.5)
                .flat(true)
                .rotation(0)
                 .title("Fin"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(fin)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(10)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = null;
        int numero = 1;
        String auxiliar = "";
        fila = bd.rawQuery("select * from puntos_carrera where  id_carrera=" + id_carrera + " ORDER BY numero ASC ", null);
        boolean sw_punto=false;
        try {
            LatLng punto = new LatLng(0, 0);
            PolylineOptions polylineOptions = new PolylineOptions();
            if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
                do {
                double lat = Double.parseDouble(fila.getString(2));
                double lon = Double.parseDouble(fila.getString(3));
                punto = new LatLng(lat, lon);
                polylineOptions.add(punto);
                    sw_punto=true;
                } while (fila.moveToNext());
            }
            if (sw_punto == true) {
               mMap.addPolyline(polylineOptions.width(15).color(-16776961));
            }

        } catch (Exception e) {

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
