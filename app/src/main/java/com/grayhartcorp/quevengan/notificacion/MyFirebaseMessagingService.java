package com.grayhartcorp.quevengan.notificacion;

import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grayhartcorp.quevengan.Actualizar_aplicacion;
import com.grayhartcorp.quevengan.Constants;
import com.grayhartcorp.quevengan.Menu_p;
import com.grayhartcorp.quevengan.Pedido_us;
import com.grayhartcorp.quevengan.Servicio_pedido;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.menu_motista.Menu_motista;
import com.grayhartcorp.quevengan.menu_motista.Notificacion_pedido_moto;
import com.grayhartcorp.quevengan.menu_motista.Servicio_eliminacion_notificacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ROMAN on 24/11/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private Vibrator vibrator;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {

            //envio de ultima ubicacion del motista


            Log.e(TAG, "Message data payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

        }
    }

    private void sendPushNotification(JSONObject json) {
        Log.e("Notificacion","Notificacion recibida");

        SharedPreferences notificacion_pedido=getSharedPreferences("notificacion_pedido",MODE_PRIVATE);
        SharedPreferences.Editor editar_notificacion=notificacion_pedido.edit();

        SharedPreferences preferencias=getSharedPreferences("pedido_en_curso", MODE_PRIVATE);
        SharedPreferences.Editor editor_pedido_en_curso=preferencias.edit();

        SharedPreferences pedido_ultimo = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editar_ultimo_pedido = pedido_ultimo.edit();



        //LA ACTIVIDAD TIENE COMO ATRIBUTOS (ESTADO) que nos identifica si el usuario esta logeado o no en la aplicacion.
        SharedPreferences actividad=getSharedPreferences("actividad",MODE_PRIVATE);


        // opcionalmente podemos mostrar el json en log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            // obtener los datos de json
            JSONObject data = json.getJSONObject("data");

            // análisis de datos json
            String title = data.getString("title");
            String message = data.getString("message");
            String cliente = data.getString("cliente");
            String id_pedido = data.getString("id_pedido");
            String nombre = data.getString("nombre");
            String empresa = data.getString("empresa");

            String latitud = data.getString("latitud");
            String longitud = data.getString("longitud");
            String tipo = data.getString("tipo");
            String fecha = data.getString("fecha");
            String hora = data.getString("hora");
           // String monto_total=data.getString("monto_total");

            String nombre_empresa="";
            String direccion_empresa="";
            String nombre_direccion=data.getString("nombre_direccion");
            String detalle_direccion=data.getString("detalle_direccion");

            JSONArray pedido=null;
            JSONArray js_empresa=null;
            if(empresa.equals("")==false)
            {
                js_empresa=data.getJSONArray("empresa");
              nombre_empresa=  js_empresa.getJSONObject(0).getString("nombre");
              direccion_empresa=  js_empresa.getJSONObject(0).getString("direccion");
            }
            if(data.getString("pedido").equals("")==false)
            {
                pedido=data.getJSONArray("pedido");
            }

            cargar_notificacion(title,message,cliente,id_pedido,nombre,latitud,longitud,tipo,fecha,hora,nombre_empresa,direccion_empresa,nombre_direccion,detalle_direccion);


                MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

switch (tipo)
{
    case "1":
        //usuario
        //se iniciar el servicio de obtencion de coordenadas de la moto...
        // notificacion de Pedido aceptado....




        //verificamos si esta logeado o no.  LOGEADO=1
        Intent usuario = new Intent(getApplicationContext(),Pedido_us.class);
//verificamos si esta logeado o no.  LOGEADO=1

        int est12=0;
        try{est12=actividad.getInt("estado",0);}
        catch (Exception e)
        {est12=0;}
        if(est12==0) {
            mNotificationManager.notificacion_pedido_aceptado_activity(title, message, usuario);
        }else{
            mNotificationManager.notificacion_silencioso();
        }




        editar_notificacion.putString("mensaje",message);
        editar_notificacion.putString("tipo",tipo);
        editar_notificacion.putString("id_pedido",id_pedido);
        editar_notificacion.putInt("estado",0);
        editar_notificacion.commit();
        try {
            editar_ultimo_pedido.putString("nombre_moto", pedido.getJSONObject(0).getString("nombre_moto"));
            editar_ultimo_pedido.putString("celular", pedido.getJSONObject(0).getString("celular"));
            editar_ultimo_pedido.putString("id_moto", pedido.getJSONObject(0).getString("id_moto"));
            editar_ultimo_pedido.putString("marca", pedido.getJSONObject(0).getString("marca"));
            editar_ultimo_pedido.putString("placa", pedido.getJSONObject(0).getString("placa"));
            editar_ultimo_pedido.putString("color", pedido.getJSONObject(0).getString("color"));
            editar_ultimo_pedido.putString("latitud", pedido.getJSONObject(0).getString("latitud"));
            editar_ultimo_pedido.putString("longitud", pedido.getJSONObject(0).getString("longitud"));
            editar_ultimo_pedido.putString("estado", "0");
            editar_ultimo_pedido.putString("id_pedido", id_pedido);
            editar_ultimo_pedido.commit();


            Intent intent = new Intent(getApplicationContext(), Servicio_pedido.class);
            intent.setAction(Constants.ACTION_RUN_ISERVICE);
            startService(intent);
        }catch (Exception e)
        {

        }

        break;
    case "2":
//moto
        //NOTIFICACION DE PEDIDO DE MOTO EN EL MOSTISTA..
        // crear una intención para la notificación

        Intent moto = new Intent(getApplicationContext(),Notificacion_pedido_moto.class);
        moto.putExtra("id_pedido",id_pedido);
        moto.putExtra("nombre",nombre);
        moto.putExtra("empresa",nombre_empresa);
        moto.putExtra("direccion",direccion_empresa);
        moto.putExtra("latitud",latitud);
        moto.putExtra("longitud",longitud);
        moto.putExtra("nombre_direccion",nombre_direccion);
        moto.putExtra("detalle_direccion",detalle_direccion);
        try {
            SharedPreferences ped = getSharedPreferences("ultima_notificacion_pedido", MODE_PRIVATE);
            SharedPreferences.Editor editar = ped.edit();
            editar.putString("id_pedido",id_pedido);
            editar.putString("nombre",nombre);
            editar.putString("empresa", nombre_empresa);
            editar.putString("direccion", direccion_empresa);
            editar.putString("latitud", latitud);
            editar.putString("longitud", longitud);
            editar.putString("nombre_direccion",nombre_direccion);
            editar.putString("detalle_direccion",detalle_direccion);
            editar.putInt("estado", 1);
            editar.commit();

            Intent notificacion = new Intent(getApplicationContext(), Servicio_eliminacion_notificacion.class);
            notificacion.putExtra("hora", hora);
            notificacion.putExtra("fecha", fecha);
            notificacion.setAction(com.grayhartcorp.quevengan.Constants.ACTION_RUN_ISERVICE);
            startService(notificacion);

        }catch (Exception e)
        {

        }

            mNotificationManager.notificacion_con_activity(title, message, moto);


        break;
    case "3":
        //enviar notificacion sin acccion.
        break;
    case  "4":
        //notificacion oculpa para los motistas...(obtener ubicacion)
        break;
    case  "5":
        //notificacion para el usuario.. de parte del motista.
        //PEDIDO FINALIADO CON EXITO..

        editar_notificacion.putString("mensaje",message);
        editar_notificacion.putString("tipo",tipo);
        editar_notificacion.putString("id_pedido",id_pedido);
        editar_notificacion.putInt("estado",0);
        editar_notificacion.commit();

        Intent usuari = new Intent(getApplicationContext(),Menu_p.class);



        //verificamos si esta logeado o no.  LOGEADO=1
        int est2=0;
        try{est2=actividad.getInt("estado",0);}
        catch (Exception e)
        {est2=0;}
        if(est2==0) {
            mNotificationManager.notificacion_con_activity(title, message, usuari);
        }
        else
        {
            mNotificationManager.notificacion_silencioso();
        }

        eliminar_notificacion_pedidos();





        try {

            editar_ultimo_pedido.putString("nombre_moto", "");
            editar_ultimo_pedido.putString("celular", "");
            editar_ultimo_pedido.putString("id_moto", "");
            editar_ultimo_pedido.putString("marca", "");
            editar_ultimo_pedido.putString("placa","");
            editar_ultimo_pedido.putString("color", "");
            editar_ultimo_pedido.putString("latitud", "");
            editar_ultimo_pedido.putString("longitud", "");
            editar_ultimo_pedido.putString("id_pedido", "");
            editar_ultimo_pedido.putString("estado", "2");
            //editar_ultimo_pedido.putString("monto_total", monto_total);
            editar_ultimo_pedido.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        break;
    case  "6":
        SharedPreferences actualizacion=getSharedPreferences("actualizacion",MODE_PRIVATE);
        SharedPreferences.Editor editar=actualizacion.edit();
        editar.putString("version_nueva",data.getString("version"));
        editar.putString("mensaje",data.getString("message"));
        editar.commit();
        Intent actualizar = new Intent(getApplicationContext(),Actualizar_aplicacion.class);
        mNotificationManager.notificacion_con_activity(title, message, actualizar);
        //notificacion para todos los usuarios.. dando un mensaje para que todos actualizen su aplicacion.
        break;

    case  "7":
        //cuando se le agrego a una empresa...
        SharedPreferences user=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor edit=user.edit();
        edit.putString("id_empresa",data.getString("id_empresa"));
        edit.commit();
        Intent us = new Intent(getApplicationContext(),Menu_p.class);
        mNotificationManager.notificacion_con_activity(title, message, us);
        break;
    case  "8":

        Intent usuario1 = new Intent(getApplicationContext(),Menu_p.class);
        mNotificationManager.notificacion_con_activity(title, message, usuario1);
        break;
    case  "9":
        // pedido cancelado.. la notificacion llegara al Motista
        try {

            editor_pedido_en_curso.putString("id_pedido","");
            editor_pedido_en_curso.putString("id_usuario","");
            editor_pedido_en_curso.putString("calificacion","");
            editor_pedido_en_curso.putString("tipo_pedido","");
            editor_pedido_en_curso.putString("mensaje","");
            editor_pedido_en_curso.putString("fecha","");
            editor_pedido_en_curso.putString("latitud","");
            editor_pedido_en_curso.putString("longitud","");
            editor_pedido_en_curso.putString("nombre_usuario","");
            editor_pedido_en_curso.putString("empresa","");
            editor_pedido_en_curso.putString("nombre_direccion","");
            editor_pedido_en_curso.putString("detalle_direccion","");
            editor_pedido_en_curso.putString("estado","");
            editor_pedido_en_curso.putInt("cancelado",1);
            editor_pedido_en_curso.commit();

        }catch (Exception e)
        {

        }
        Intent moto1 = new Intent(getApplicationContext(),Menu_motista.class);
        mNotificationManager.notificacion_error_activity(title, message, moto1);

        eliminar_notificacion_pedidos();
        break;
    case"10":
        //la moto esta cerca..
//verificamos si esta logeado o no.  LOGEADO=1

        int est10=0;
        try{est10=actividad.getInt("estado",0);}
        catch (Exception e)
        {est10=0;}
        if(est10==0) {
            mNotificationManager.notificacion_sonido_default_sin_activity(title, message);
        }
        else
        {

            mNotificationManager.notificacion_silencioso();
        }





        editar_notificacion.putString("mensaje","Su Moto esta cerca.");
        editar_notificacion.putString("tipo",tipo);
        editar_notificacion.putString("id_pedido",id_pedido);
        editar_notificacion.putInt("estado",0);
        editar_notificacion.commit();
        break;
    case"11":
        //llego la moto a la ubicacion del pedido de Moto..
//verificamos si esta logeado o no.  LOGEADO=1


        int est11=0;
        try{est11=actividad.getInt("estado",0);}
        catch (Exception e)
        {est11=0;}
        if(est11==0) {
            mNotificationManager.notificacion_sonido_default_llego_la_moto_sin_activity(title, message);
        }
        else
        {
            mNotificationManager.notificacion_silencioso();
        }



        editar_notificacion.putString("mensaje","Su Moto ha llegado.");
        editar_notificacion.putString("tipo",tipo);
        editar_notificacion.putString("id_pedido",id_pedido);
        editar_notificacion.putInt("estado",0);
        editar_notificacion.commit();
        break;
    case"12":
        //SE FINALIZO LA CARRERA....

//verificamos si esta logeado o no.  LOGEADO=1

        int est1=0;
        try{est1=actividad.getInt("estado",0);}
        catch (Exception e)
        {est1=0;}
        if(est1==0) {
            mNotificationManager.notificacion_sonido_default_sin_activity(title, message);
        }
        else
        {

            mNotificationManager.notificacion_silencioso();
        }





        editar_notificacion.putString("mensaje",message);
        editar_notificacion.putString("tipo",tipo);
        editar_notificacion.putString("id_pedido",id_pedido);
        editar_notificacion.putInt("estado",0);
        editar_notificacion.commit();
        break;

    case "20":
//NOTIFICACION DEL CPANEL..
        mNotificationManager.notificacion_sonido_default_activity(title, message);
        break;

}





        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    public void eliminar_notificacion_pedidos() {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    "easymoto", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("notificacion", "tipo=1", null);
            db.delete("notificacion", "tipo=10", null);
            db.delete("notificacion", "tipo=11", null);
            db.delete("notificacion", "tipo=12", null);
            db.close();
            Log.e("sqlite ", "vaciar todas las notificacion del usuario." );
        } catch (Exception e)
        {
            Log.e("sqlite ", "Error : vaciar todas las notificacion del usuario. "+e);
        }
    }

    private void cargar_notificacion(String title, String message, String cliente, String id_pedido, String nombre, String latitud, String longitud, String tipo, String fecha, String hora,String empresa,String direccion_empresa,String nombre_direccion,String detalle_direccion) {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "easymoto", null, 1);

            SQLiteDatabase bd = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("titulo", title);
            registro.put("mensaje", message);
            registro.put("cliente", cliente);
            registro.put("nombre", nombre);
            registro.put("tipo", tipo);
            registro.put("fecha", fecha);
            registro.put("hora", hora);
            registro.put("latitud", latitud);
            registro.put("longitud", longitud);
            registro.put("id_pedido", id_pedido);
            registro.put("empresa", empresa);
            registro.put("direccion", direccion_empresa);
            registro.put("leido", "0");
            registro.put("nombre_direccion", nombre_direccion);
            registro.put("detalle_direccion", detalle_direccion);
            bd.insert("notificacion", null, registro);
            bd.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

}