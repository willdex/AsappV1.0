package com.grayhartcorp.quevengan.notificacion;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.grayhartcorp.quevengan.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by ROMAN on 24/11/2016.
 */

public class MyNotificationManager {

    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;

    private Context mCtx;


  //  Notification.Builder builder = new Notification.Builder(mContext);
    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    //el método mostrará una notificación grande con una imagen
    //los parámetros son título para el título del mensaje, mensaje para el texto del mensaje,
    //url de la imagen grande y una intención que se abrirá
    //cuando toque en la notificación


    //el método mostrará una pequeña notificación
    //los parámetros son título para el título del mensaje,
    //mensaje para el texto del mensaje y una intención que se abrirá
    //cuando toque en la notificación
    public void notificacion_con_activity(String title, String message, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//sonido

        //Uri sonido = Uri.parse("android.resource://"+ this.mCtx.getPackageName() + "/" + R.raw.LLEGO);
        Uri sonido = Uri.parse("android.resource://"+ this.mCtx.getPackageName() + "/" + R.raw.vocina);
        long[] pattern = new long[]{4000,1000,4000};//vibracion

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setSound(sonido)//sonido
                .setVibrate(pattern)//vibracion
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);

    }
    public void notificacion_sin_activity(String title, String message) {

        //Uri sonido = Uri.parse("android.resource://"+ this.mCtx.getPackageName() + "/" + R.raw.LLEGO);
        Uri sonido = Uri.parse("android.resource://"+ this.mCtx.getPackageName() + "/" + R.raw.vocina);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//sonido
        long[] pattern = new long[]{4000,1000,4000};//vibracion

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setSound(sonido)//sonido
                .setVibrate(pattern)//vibracion
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);

    }

    public void notificacion_pedido_aceptado_activity(String title, String message,Intent intent) {

       // Uri sonido = Uri.parse("android.resource://"+ this.mCtx.getPackageName() + "/" + R.raw.ARRANQUE);
        Uri sonido = Uri.parse("android.resource://"+ this.mCtx.getPackageName() + "/" + R.raw.arranque);


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//sonido

        long[] pattern = new long[]{4000,1000,4000};//vibracion

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setSound(sonido)//sonido
                .setVibrate(pattern)//vibracion
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
    }
    public void notificacion_sonido_default_activity(String title, String message) {


        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//sonido
        long[] pattern = new long[]{4000,1000,4000};//vibracion

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setSound(defaultSound)//sonido
                .setVibrate(pattern)//vibracion
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);

    }
    public void notificacion_sonido_default_activity(String title, String message,Intent intent) {

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//sonido
        long[] pattern = new long[]{4000,1000,4000};//vibracion

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setSound(defaultSound)//sonido
                .setVibrate(pattern)//vibracion
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);

    }
    public void notificacion_sonido_default_sin_activity(String title, String message) {

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//sonido
        long[] pattern = new long[]{4000,1000,4000};//vibracion

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setSound(defaultSound)//sonido
                .setVibrate(pattern)//vibracion
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);

    }

    public void notificacion_sonido_default_llego_la_moto_sin_activity(String title, String message) {

          Uri sonido = Uri.parse("android.resource://"+ this.mCtx.getPackageName() + "/" + R.raw.vocina);
        long[] pattern = new long[]{4000,1000,4000};//vibracion

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setSound(sonido)//sonido
                .setVibrate(pattern)//vibracion
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);

    }
    public void notificacion_error_activity(String title, String message,Intent intent) {

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Uri defaultSound = Uri.parse("android.resource://"
                + this.mCtx.getPackageName() + "/" + R.raw.ringtone_error);//sonido

        long[] pattern = new long[]{4000,1000,4000};//vibracion

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setSound(defaultSound)//sonido
                .setVibrate(pattern)//vibracion
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);

    }

    public void notificacion_silencioso() {

        Vibrator v=(Vibrator) mCtx.getSystemService(VIBRATOR_SERVICE);
        v.vibrate(1000);

    }



    //El método devolverá Bitmap desde una URL de imagen
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
