package com.grayhartcorp.quevengan.historial;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.grayhartcorp.quevengan.R;
import com.grayhartcorp.quevengan.SqLite.AdminSQLiteOpenHelper;
import com.grayhartcorp.quevengan.Suceso;

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
import java.util.ArrayList;

public class Items_historial extends BaseAdapter  {

    protected Activity activity;
    protected ArrayList<CHistorial> items;
    String nombre_empresa;
    int administrador=0;

    LayoutInflater inflater;
    Suceso suceso;
    ImageView im_me_gusta;


    String servidor;



    public Items_historial (Activity activity, ArrayList<CHistorial> items,String nombre_empresa,int administrado,LayoutInflater inflater,String ip_servidor) {
        this.activity = activity;
        this.items = items;
        this.nombre_empresa=nombre_empresa;
        this.administrador=administrado;
        this.inflater=inflater;
        this.servidor=ip_servidor;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<CHistorial> Historial) {
        for (int i = 0; i < Historial.size(); i++) {
            items.add(Historial.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.items_historial, null);
        }

        final CHistorial dir = items.get(position);



        TextView tv_nombre_direccion = (TextView) v.findViewById(R.id.tv_nombre_direccion);
        TextView tv_nombre_empresa = (TextView) v.findViewById(R.id.tv_nombre_empresa);
        TextView tv_hora = (TextView) v.findViewById(R.id.tv_fecha);
        TextView tv_nombre_motista = (TextView) v.findViewById(R.id.tv_nombre_motista);
        TextView tv_monto_total = (TextView) v.findViewById(R.id.tv_monto_total);
        im_me_gusta=(ImageView)v.findViewById(R.id.im_me_gusta);


        im_me_gusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                puntuacion(dir.getId_pedido());
            }
        });


        if(dir.getPuntuacion()==0)
        {
            im_me_gusta.setImageResource(R.mipmap.ic_manito_neutro);

        }
        else if(dir.getPuntuacion()==1)
        {
            im_me_gusta.setImageResource(R.mipmap.ic_me_gusta);
        }else if(dir.getPuntuacion()==2)
        {
            im_me_gusta.setImageResource(R.mipmap.ic_no_me_gusta);
        }


       if(administrador==1)
       {
           tv_nombre_direccion.setText(dir.getNombre_usuario());
       }else
       {
           tv_nombre_direccion.setText(dir.getNombre_direccion());
       }

        tv_nombre_empresa.setText(nombre_empresa);
        tv_hora.setText(dir.getHora());
        if(dir.getEstado()==2)
        {
            tv_monto_total.setText(String.valueOf(dir.getMonto_total()+" Bs."));
            tv_monto_total.setTextColor(Color.parseColor("#4e0034"));
            tv_monto_total.setBackgroundResource(R.drawable.bk_correcto);
        }
        else if(dir.getEstado()==3)
        {
            tv_monto_total.setText("CANCELADO");
            tv_monto_total.setTextColor(Color.RED);
            tv_monto_total.setBackgroundResource(R.drawable.bk_cancelar);
        }

        tv_nombre_motista.setText(dir.getNombre()+" "+dir.getApellido());


        return v;
    }




    public void puntuacion(final int id_pedido)
    {

        final AlertDialog.Builder dialogo = new AlertDialog.Builder(activity);

        final View dialoglayout = inflater.inflate(R.layout.puntuacion, null);
        final Button Bt_cancelar = (Button) dialoglayout.findViewById(R.id.bt_cancelar);
        final ImageButton Bt_me_gusta = (ImageButton) dialoglayout.findViewById(R.id.bt_me_gusta);
        final ImageButton Bt_no_me_gusta = (ImageButton) dialoglayout.findViewById(R.id.bt_no_me_gusta);

        dialogo.setView(dialoglayout);
        dialogo.setCancelable(false);


        final AlertDialog finalDialogo =dialogo.create();
        finalDialogo.show();
        Bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalDialogo.dismiss();
            }
        });
        Bt_me_gusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Servicio_puntuacion servicio_verificar_pedido=new Servicio_puntuacion();
                servicio_verificar_pedido.execute(servidor + "frmPedido.php?opcion=set_puntuacion", "1",String.valueOf(id_pedido),"1");// parametro que recibe

                finalDialogo.dismiss();
            }
        });
        Bt_no_me_gusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Servicio_puntuacion servicio_verificar_pedido=new Servicio_puntuacion();
                servicio_verificar_pedido.execute(servidor + "frmPedido.php?opcion=set_puntuacion", "1",String.valueOf(id_pedido),"2");// parametro que recibe

                finalDialogo.dismiss();
            }
        });

    }


    public class Servicio_puntuacion extends AsyncTask<String, Integer, String> {

int id_pedido=0;
        int puntuacion=0;
        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";
            if(isCancelled()==false) {
                //verificar si tiene pedido o carrera en curso.,,,,
                if (params[1] == "1") {
                    try {
                        HttpURLConnection urlConn;

                        url = new URL(cadena);
                        urlConn = (HttpURLConnection) url.openConnection();
                        urlConn.setDoInput(true);
                        urlConn.setDoOutput(true);
                        urlConn.setUseCaches(false);
                        urlConn.setRequestProperty("Content-Type", "application/json");
                        urlConn.setRequestProperty("Accept", "application/json");
                        urlConn.connect();

                        //se crea el objeto JSON
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("id_pedido", params[2]);
                        jsonParam.put("puntuacion", params[3]);
                        id_pedido=Integer.parseInt(params[2]);
                        puntuacion=Integer.parseInt(params[3]);

                        //Envio los prametro por metodo post
                        OutputStream os = urlConn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        writer.write(jsonParam.toString());
                        writer.flush();
                        writer.close();

                        int respuesta = urlConn.getResponseCode();

                        StringBuilder result = new StringBuilder();

                        if (respuesta == HttpURLConnection.HTTP_OK) {

                            String line;
                            BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                            while ((line = br.readLine()) != null) {
                                result.append(line);
                            }


                            JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del

                            suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));

                            if (suceso.getSuceso().equals("1")) {

                                devuelve = "1";
                            } else {
                                devuelve = "2";
                            }

                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("respuesta del servidor=", "" + s);
            if (s.equals("1")) {
                update_puntuacion(id_pedido,puntuacion);
            } else if (s.equals("2")) {

            }




        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }


    }

    public void update_puntuacion(int id_pedido,int puntuacion) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(activity,
                "easymoto", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("UPDATE pedido SET puntuacion='"+puntuacion+"' WHERE id='"+id_pedido+"'");
        db.close();
        Log.e("sqlite ", "vaciar todo");

        if(puntuacion==1)
        {
            im_me_gusta.setImageResource(R.mipmap.ic_me_gusta);
        }else if(puntuacion==2)
        {
            im_me_gusta.setImageResource(R.mipmap.ic_no_me_gusta);
        }
    }




}
