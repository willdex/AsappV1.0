package com.grayhartcorp.quevengan.empresa;

/**
 * Created by elisoft on 29-11-16.
 */

public class CEmpresa {
    private int id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String razon_social;
    private String nit;
    private double latitud;
    private double longitud;

    public CEmpresa()
    {
        id=0;
         nombre="";
         direccion="";
         telefono="";
         razon_social="";
         nit="";
         latitud=0;
         longitud=0;
    }
    public CEmpresa(   int id,
            String nombre,
            String direccion,
            String telefono,
            String razon_social,
            String nit,
            double latitud,
            double longitud)
    {
        this.id=id;
        this.nombre=nombre;
        this.direccion=direccion;
        this.telefono=telefono;
        this.razon_social=razon_social;
        this.nit=nit;
        this.latitud=latitud;
        this.longitud=longitud;
    }


    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public String getNit() {
        return nit;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}
