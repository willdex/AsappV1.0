package com.grayhartcorp.quevengan.notificacion;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ROMAN on 24/11/2016.
 */

public class MyVolley {

    //Como necesitamos realizar la solicitud de http varias veces
    //por eso, estamos definiendo un patrón singleton para volley para manejar las solicitudes de red.

    private static MyVolley mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private MyVolley(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyVolley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyVolley(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null) {
            // getApplicationContext () es clave, evita que se escape el
            // Activity o BroadcastReceiver si alguien pasa una entrada.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
