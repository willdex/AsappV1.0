package com.grayhartcorp.quevengan.menu_motista.pedidos;

/**
 * Created by ELIO on 28/10/2016.
 */

public class CPedidos {
    private int id;
    private int id_usuario;
    private int id_moto;
    private int calificacion;
    private int tipo_pedido;
    private String mensaje;
    private String fecha;
    private String fecha_llegado;
    private int estado;
    private double latitud;
    private double longitud;
    private String nombre_cliente;
    private String monto_total;
    private String hora;
    private String nombre_direccion;
    private String nombre_empresa;
    private int puntuacion;
    public CPedidos()
    {

    }

    public CPedidos(int id,int id_usuario,int id_moto,int calificacion,int tipo_pedido,String mensaje,String fecha,int estado,double latitud,double longitud,String nombre_cliente,String monto_total,String hora,String nombre_direccion,String nombre_empresa,int puntuacion)
    {
        this.id=id;
        this.id_usuario=id_usuario;
        this.id_moto=id_moto;
        this.calificacion=calificacion;
        this.tipo_pedido=tipo_pedido;
        this.mensaje=mensaje;
        this.fecha=fecha;
        this.estado=estado;
        this.latitud=latitud;
        this.longitud=longitud;
        this.nombre_cliente=nombre_cliente;
        this.fecha_llegado="";
        this.setMonto_total(monto_total);
        this.setHora(hora);
        this.setNombre_direccion(nombre_direccion);
        this.setNombre_empresa(nombre_empresa);
        this.setPuntuacion(puntuacion);
    }
    public void set_fecha_llegado(String fecha_llegado)
    {
        this.fecha_llegado=fecha_llegado;
    }

    public int getId() {
        return id;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public int getId_moto() {
        return id_moto;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public int getTipo_pedido() {
        return tipo_pedido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public String getFecha_llegado() {
        return fecha_llegado;
    }

    public int getEstado() {
        return estado;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public String getMonto_total() {
        return monto_total;
    }

    public void setMonto_total(String monto_total) {
        this.monto_total = monto_total;
    }

    public String getNombre_direccion() {
        return nombre_direccion;
    }

    public void setNombre_direccion(String nombre_direccion) {
        this.nombre_direccion = nombre_direccion;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
