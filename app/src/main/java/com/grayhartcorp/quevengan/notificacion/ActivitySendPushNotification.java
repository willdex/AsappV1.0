package com.grayhartcorp.quevengan.notificacion;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.grayhartcorp.quevengan.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitySendPushNotification extends AppCompatActivity implements View.OnClickListener {//RadioGroup.OnCheckedChangeListener,

    private Button buttonSendPush;

    private ProgressDialog progressDialog;

    private boolean isSendAllChecked;
    private List<String> devices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_push_notification);


        buttonSendPush = (Button) findViewById(R.id.buttonSendPush);


        devices = new ArrayList<>();

        buttonSendPush.setOnClickListener(this);

        loadRegisteredDevices();
    }

    // método para cargar todos los dispositivos de la base de datos en el Spinner
    private void loadRegisteredDevices() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Buscando Dispositivos...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, EndPoints.URL_FETCH_DEVICES,//obtiene los dispositivos
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray jsonDevices = obj.getJSONArray("devices");

                                for (int i = 0; i < jsonDevices.length(); i++) {
                                    JSONObject d = jsonDevices.getJSONObject(i);
                                    devices.add(d.getString("email"));
                                }

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        ActivitySendPushNotification.this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        devices);

                               // spinner.setAdapter(arrayAdapter);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

        };
        MyVolley.getInstance(this).addToRequestQueue(stringRequest);

    }

    // este método enviará el push
    // de aquí llamaremos al metodo sendMultiple(todos los dispositivo)o sendSingle(un dispositivo)

    private void sendPush(){
        sendMultiplePush();
    }

    private void sendMultiplePush(){//enviar a todos los dispositivos


        progressDialog.setMessage("Enviando.. Notificacion");
        progressDialog.show();

       StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_MULTIPLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        Toast.makeText(ActivitySendPushNotification.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void sendSinglePush(){//enviar a un dispositivo seleccionado

       // final String title = editTextTitle.getText().toString();

        //final String message = editTextMessage.getText().toString();
       // final String image = editTextImage.getText().toString();
      //  final String email = spinner.getSelectedItem().toString();

        progressDialog.setMessage("Enviando Notificacion");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(ActivitySendPushNotification.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
          //  @Override
           protected Map<String, String> getParams() throws AuthFailureError {
               Map<String, String> params = new HashMap<>();
               return params;
          }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);

    }


    @Override
    public void onClick(View view) {
        //calling the method send push on button click
        sendPush();
    }
}
