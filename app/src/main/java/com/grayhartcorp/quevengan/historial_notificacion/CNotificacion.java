package com.grayhartcorp.quevengan.historial_notificacion;

/**
 * Created by ELIO on 15/11/2016.
 */

public class CNotificacion {



    private int id;
    private String titulo;
    private String mensaje ;
    private String cliente ;
    private String id_pedido ;
    private String nombre ;
    private String empresa ;
    private String direccion ;
    private String latitud ;
    private String longitud;
    private String tipo;
    private String fecha ;
    private String hora;
    private int leido;
    private String nombre_direccion;
    private String detalle_direccion;

    public CNotificacion()
    {

    }
    public CNotificacion(int id,
                         String titulo,
             String mensaje ,
             String cliente ,
             String id_pedido ,
             String nombre ,
             String latitud ,
             String longitud,
             String tipo,
             String fecha ,
             String hora,
             int leido
    )
    {
        this.id=id;
        this.setTitulo(titulo);
        this.setMensaje(mensaje);
        this.setCliente(cliente);
        this.setId_pedido(id_pedido);
        this.setNombre(nombre);
        this.setLatitud(latitud);
        this.setLongitud(longitud);
        this.setTipo(tipo);
        this.setFecha(fecha);
        this.setHora(hora);
        this.setLeido(leido);
        this.empresa="";
        this.direccion="";
        this.setNombre_direccion("");
        this.setDetalle_direccion("");

    }




    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(String id_pedido) {
        this.id_pedido = id_pedido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getLeido() {
        return leido;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setLeido(int leido) {
        this.leido = leido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombre_direccion() {
        return nombre_direccion;
    }

    public void setNombre_direccion(String nombre_direccion) {
        this.nombre_direccion = nombre_direccion;
    }

    public String getDetalle_direccion() {
        return detalle_direccion;
    }

    public void setDetalle_direccion(String detalle_direccion) {
        this.detalle_direccion = detalle_direccion;
    }
}
