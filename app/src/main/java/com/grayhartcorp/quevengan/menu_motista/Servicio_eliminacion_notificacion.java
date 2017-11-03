package com.grayhartcorp.quevengan.menu_motista;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.grayhartcorp.quevengan.Constants;
import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.Servicio_pedido;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.Suceso;
import com.grayhartcorp.quevengan.direccion.CDireccion;

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

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Servicio_eliminacion_notificacion extends IntentService {
    String fecha,hora;
    private static final String TAG = Servicio_pedido.class.getSimpleName();


    public Servicio_eliminacion_notificacion( ) {

        super("Servicio_eliminacion_notificacion");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            fecha= intent.getExtras().getString("fecha");
            hora= intent.getExtras().getString("hora");
            final String action = intent.getAction();
            if (Constants.ACTION_RUN_ISERVICE.equals(action)) {
                handleActionRun();
            }
        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun() {
        try {
for(int i=1;i<=3;i++) {
    Thread.sleep(60000);
}
            int id=get_id_por_fecha_hora(fecha,hora);
            vaciar_notificacion(id);
            // Quitar de primer plano
            stopForeground(true);
            // si nuestro estado esta en 2 o mayor .. quiere decir que no nuestro pedido se finalizo o sino se cancelo... sin nninguna carrera...

                stopService(new Intent(this,Servicio_eliminacion_notificacion.class));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Log.e("servicio notificacion:","Servicio destruido...");

        // Emisión para avisar que se terminó el servicio
        Intent localIntent = new Intent(Constants.ACTION_PROGRESS_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }


    public void vaciar_notificacion(int id) {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    "easymoto", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("notificacion", "id<="+id+" AND tipo=2", null);
            db.close();
            Log.e("sqlite ", "vaciar todas las notificacion mayor a=" + id);
        } catch (Exception e)
        {
            Log.e("sqlite ", "Error : vaciar todas las notificacion mayor " + id+" . "+e);
        }
    }

    public int get_id_por_fecha_hora(String fecha,String hora)
    {
        int id=0;

try{
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "easymoto", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select id from notificacion where fecha='"+fecha+"' and hora='"+hora+"'", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
                id=Integer.parseInt(fila.getString(0));

        } else {
            Log.e("Eliminacion de la notificacion","No hay registrados");
        }
        bd.close();

    }catch (Exception e)
    {

    }
        return id;
    }

}

