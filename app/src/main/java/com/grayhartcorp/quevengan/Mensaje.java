package com.grayhartcorp.quevengan;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by ELIO on 05/01/2017.
 */

public class Mensaje {
    String mensaje;
    Mensaje(String mensaje)
    {
        this.mensaje=mensaje;
    }
    void show(Context context,String mensaje)
    {
        Toast toast =Toast.makeText(context,mensaje,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
