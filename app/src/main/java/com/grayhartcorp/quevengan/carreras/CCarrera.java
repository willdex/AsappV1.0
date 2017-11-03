package com.grayhartcorp.quevengan.carreras;

/**
 * Created by ELIO on 15/11/2016.
 */

public class CCarrera {

    private int id;
    private double latitud_inicio;
    private double longitud_inicio;
    private double latitud_fin;
    private double longitud_fin;
    private double monto;
    private String detale_inicio;
    private String detalle_fin;
    private String distancia;
    private String fecha_inicio;
    private String fecha_fin ;
    private int opciones;
    private int id_pedido;
    private int id_usuario;
    private int id_moto;
    private String ruta;
    private String numero;

    public CCarrera()
    {

    }
    public CCarrera(int id,double latitud_inicio,double longitud_inicio,double latitud_fin,double longitud_fin,String detale_inicio,String detalle_fin,String distancia,String fecha_inicio,String fecha_fin,int opciones,int id_pedido,int id_usuario,int id_moto,double monto,String ruta,String numero)
    {
        this.id=id;
        this.latitud_inicio=latitud_inicio;
        this.longitud_inicio=longitud_inicio;
        this.latitud_fin=latitud_fin;
        this.longitud_fin=longitud_fin;
        this.monto=monto;
        this.detale_inicio=detale_inicio;
        this.detalle_fin=detalle_fin;
        this.distancia=distancia;
        this.fecha_inicio=fecha_inicio;
        this.fecha_fin=fecha_fin;
        this.opciones=opciones;
        this.id_pedido=id_pedido;
        this.id_usuario=id_usuario;
        this.id_moto=id_moto;

        this.setNumero(numero);
        this.setRuta(ruta);
    }




    public int getId() {
        return id;
    }

    public double getLatitud_inicio() {
        return latitud_inicio;
    }

    public double getLongitud_inicio() {
        return longitud_inicio;
    }

    public double getLatitud_fin() {
        return latitud_fin;
    }

    public double getLongitud_fin() {
        return longitud_fin;
    }

    public double getMonto() {
        return monto;
    }

    public String getDetale_inicio() {
        return detale_inicio;
    }

    public String getDetalle_fin() {
        return detalle_fin;
    }

    public String getDistancia() {
        return distancia;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public int getOpciones() {
        return opciones;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public int getId_moto() {
        return id_moto;
    }


    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
