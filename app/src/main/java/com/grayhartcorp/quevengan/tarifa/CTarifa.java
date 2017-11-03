package com.grayhartcorp.quevengan.tarifa;

/**
 * Created by ELIO on 01/11/2016.
 */

public class CTarifa {
    private int id;
    private double distancia;
    private double monto;
    private String fecha;
    public CTarifa()
    {}
    public  CTarifa(int id,double distancia,double monto)
    {
        this.id=id;
        this.distancia=distancia;
        this.monto=monto;
    }

    public  CTarifa(int id,double distancia,double monto,String fecha)
    {
        this.id=id;
        this.distancia=distancia;
        this.monto=monto;
        this.fecha=fecha;

    }

    public int getId() {
        return id;
    }

    public double getDistancia() {
        return distancia;
    }

    public double getMonto() {
        return monto;
    }
    public String getFecha()
    {
        return fecha;
    }
}
