package com.grayhartcorp.quevengan.historial;

/**
 * Created by ELIO on 05/10/2016.
 */
public class CHistorial {
    private int id_usuario;
    private int id_pedido;
    private int id_moto;
    private int calificacion;
    private int tipo_pedido;
    private String mensaje;
    private String fecha;
    private String fecha_llegado;
    private int estado;
    private double latitud;
    private double longitud;
    private String nombre;
    private String apellido;
    private String celular;
    private String marca;
    private String placa;
    private int estado_moto;
    private double monto_total;
    private String nombre_direccion;
    private String detalle_direccion;
    private String hora;
    private String nombre_usuario;
    private int puntuacion;

    public CHistorial()
    {
        super();

         id_usuario=0;
         id_pedido=0;
         id_moto=0;
         calificacion=0;
         tipo_pedido=0;
         mensaje="";
         fecha="00/00/0000";
         fecha_llegado="00/00/0000";;
         estado=0;
         latitud=0;
         longitud=0;
         nombre="";
         apellido="";
         celular="";
         marca="";
         placa="";
         estado_moto=0;
        setMonto_total(0);
         setNombre_direccion("");
        setDetalle_direccion("");
        setHora("");
        setNombre_usuario("");
        setPuntuacion(0);
    }
    public CHistorial(
             int id_usuario,
             int id_pedido,
             int id_moto,
             int calificacion,
             int tipo_pedido,
             String mensaje,
             String fecha,
             String fecha_llegado,
             int estado,
             double latitud,
             double longitud,
             String nombre,
             String apellido,
             String celular,
             String marca,
             String placa,
             int estado_moto,
             double monto_total,
             String nombre_direccion,
             String detalle_direccion,
    String hora,
             String nombre_usuario,
             int puntuacion)
    {
        super();
        this.id_usuario=id_usuario;
        this.id_pedido=id_pedido;
        this.id_moto=id_moto;
        this.calificacion=calificacion;
        this.tipo_pedido=tipo_pedido;
        this.mensaje=mensaje;
        this.fecha=fecha;
        this.fecha_llegado=fecha_llegado;
        this.estado=estado;
        this.latitud=latitud;
        this.longitud=longitud;
        this.nombre=nombre;
        this.apellido=apellido;
        this.celular=celular;
        this.marca=marca;
        this.placa=placa;
        this.estado_moto=estado_moto;
        this.setMonto_total(monto_total);
        this.setNombre_direccion(nombre_direccion);
        this.setDetalle_direccion(detalle_direccion);
this.setHora(hora);
        this.setNombre_usuario(nombre_usuario);
        this.setPuntuacion(puntuacion);
    }


    public int getId_usuario() {
        return id_usuario;
    }

    public int getId_pedido() {
        return id_pedido;
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

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCelular() {
        return celular;
    }

    public String getMarca() {
        return marca;
    }

    public String getPlaca() {
        return placa;
    }

    public int getEstado_moto() {
        return estado_moto;
    }
    public double get_monto_total() {
        return getMonto_total();
    }

    public double getMonto_total() {
        return monto_total;
    }

    public void setMonto_total(double monto_total) {
        this.monto_total = monto_total;
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

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
