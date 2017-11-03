package com.grayhartcorp.quevengan.notificacion;

/**
 * Created by ROMAN on 24/11/2016.
 */

public class EndPoints
{
        //archivos php a llamar
    public static final String URL_REGISTER_DEVICE = "http://192.168.0.32/fcmMulti/RegisterDevice.php";
    public static final String URL_SEND_SINGLE_PUSH = "http://192.168.0.32/fcmMulti/sendSinglePush.php";
    public static final String URL_SEND_MULTIPLE_PUSH = "http://192.168.0.32/fcmMulti/sendMultiplePush.php";
    public static final String URL_FETCH_DEVICES = "http://192.168.0.32/fcmMulti/GetRegisteredDevices.php";
}
