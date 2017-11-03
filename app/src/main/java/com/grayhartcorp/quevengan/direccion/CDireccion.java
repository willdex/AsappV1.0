package com.grayhartcorp.quevengan.direccion;

/**
 * Created by ELIO on 05/10/2016.
 */
public class CDireccion {
    private int id;
    private String detalle;
    private String nombre;
    private String id_empresa;
    private String id_usuario;
    private double latitud;
    private double longitud;

    public CDireccion(){
    super();
        this.setId(0);
        this.setLongitud(0);
        this.setLatitud(0);
        this.detalle="";
        this.nombre="";
        this.id_empresa="";
        this.id_usuario="";
    }
    public CDireccion(int id, String detalle, double longitud, double latitud,String id_empresa,String id_usuario){
        super();
        this.setId(id);
        this.setLongitud(longitud);
        this.setLatitud(latitud);
        this.detalle=detalle;
        this.id_usuario=id_usuario;
        this.id_empresa=id_empresa;
        this.nombre="";
    }
    public CDireccion(int id, String nombre,String detalle,double latitud,double longitud,String id_empresa,String id_usuario){
        super();
        this.setId(id);
        this.nombre=nombre;
        this.setLongitud(longitud);
        this.setLatitud(latitud);
        this.detalle=detalle;
        this.id_usuario=id_usuario;
        this.id_empresa=id_empresa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getId_empresa() {
        return id_empresa;
    }

    public String  getId_usuario() {
        return id_usuario;
    }

    public String getNombre() {
        return nombre;
    }
}
