package com.grayhartcorp.quevengan.SqLite;

/**
 * Created by elisoft on 07-11-16.
 */import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table direccion(" +
                    "id integer," +
                    "detalle text," +
                    "latitud decimal(13,7)," +
                    "longitud decimal(13,7)," +
                    "id_empresa integer," +
                    "id_usuario integer,"+
                    "nombre text)");
        db.execSQL("create table tarifa(" +
                "id integer, " +
                "distancia decimal(10,3)," +
                " monto decimal(10,2))");
        db.execSQL("create table pedido(" +
                "id integer, " +
                "id_usuario integer," +
                "id_moto integer," +
                "calificacion integer," +
                "tipo_pedido integer," +
                "mensaje text," +
                "fecha text," +
                "fecha_llegado text," +
                "estado integer," +
                "latitud decimal(13,7)," +
                "longitud decimal(13,7)," +
                "nombre text," +
                "apellido text," +
                "celular text ," +
                "marca text," +
                "placa text," +
                "estado_moto integer default 0," +
                "monto_total decimal(13,2) default 0," +
                "nombre_direccion text," +
                "detalle_direccion text," +
                "hora text," +
                "nombre_usuario text," +
                "puntuacion integer" +
                ")");
        db.execSQL("create table carrera(" +
                "id integer," +
                "detale_inicio text," +
                "latitud_inicio decimal(13,7)," +
                "longitud_inicio decimal(13,7)," +
                "detalle_fin text," +
                "latitud_fin decimal(13,7) default 0," +
                "longitud_fin decimal(13,7) default 0," +
                "distancia decimal(10,3)," +
                "opciones integer," +
                "fecha_inicio text," +
                "fecha_fin text," +
                "id_pedido integer," +
                "id_usuario integer," +
                "id_moto integer," +
                "monto decimal(10,2)," +
                "ruta text" +
                ")");

        //cuarga los puntos de recorrido de los Carreras...
        db.execSQL("create table puntos_carrera(" +
                "fecha timestamp default CURRENT_TIMESTAMP," +
                "id_carrera integer," +
                "latitud decimal(13,7)," +
                "longitud decimal(13,7)," +
                "id_pedido integer ," +
                "numero integer default 0,"+
                "primary key(id_carrera,latitud,longitud,id_pedido))");
        //creamos una tabla de contactos de los integrantes de la empresa... // esto solo es para el administrador de una empresa/
        db.execSQL("create table contacto(" +
                "id integer not null," +
                "nombre text not null," +
                "telefono text not null)");
        db.execSQL("create table notificacion(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT not null," +
                "titulo text," +
                "mensaje text," +
                "cliente text," +
                "id_pedido text," +
                "nombre text,"+
                "latitud text," +
                "longitud text," +
                "tipo text," +
                "fecha text," +
                "hora text," +
                "leido integer default 0," +
                "empresa text," +
                "direccion text," +
                "nombre_direccion text," +
                "detalle_direccion text" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
        db.execSQL("drop table if exists direccion");
        db.execSQL("create table direccion(id integer,detalle text,latitud decimal(13,7),longitud decimal(13,7),id_empresa text,id_usuario text,nombre text)");
        db.execSQL("drop table if exists tarifa");
        db.execSQL("create table tarifa(id integer, distancia decimal(10,3), monto decimal(10,2))");
        db.execSQL("drop table if exists pedido");
        db.execSQL("create table pedido(" +
                "id integer, " +
                "id_usuario integer," +
                "id_moto integer," +
                "calificacion integer," +
                "tipo_pedido integer," +
                "mensaje text," +
                "fecha text," +
                "fecha_llegado text," +
                "estado integer," +
                "latitud decimal(13,7)," +
                "longitud decimal(13,7)," +
                "nombre text," +
                "apellido text," +
                "celular text ," +
                "marca text," +
                "placa text," +
                "estado_moto integer default 0," +
                "monto_total decimal(13,2) default 0," +
                "nombre_direccion text," +
                "detalle_direccion text," +
                "hora text," +
                "nombre_usuario text," +
                "puntuacion integer" +
                ")");
        db.execSQL("drop table if exists carrera");
        db.execSQL("create table carrera(" +
                "id integer," +
                "detale_inicio text," +
                "latitud_inicio decimal(13,7)," +
                "longitud_inicio decimal(13,7)," +
                "detalle_fin text," +
                "latitud_fin decimal(13,7) default 0," +
                "longitud_fin decimal(13,7) default 0," +
                "distancia decimal(10,3)," +
                "opciones integer," +
                "fecha_inicio text," +
                "fecha_fin text," +
                "id_pedido integer," +
                "id_usuario integer," +
                "id_moto integer," +
                "monto decimal(10,2)," +
                "ruta text" +
                ")");


        //verificacmos si tenemos una tabla de contactos.-..
       // esto solo es para el administrador de una empresa/
        db.execSQL("drop table if exists contacto");
        db.execSQL("create table contacto(" +
                "id integer not null," +
                "nombre text not null," +
                "telefono text not null)");

        db.execSQL("drop table if exists puntos_carrera");
        //cuarga los puntos de recorrido de los Carreras...
        db.execSQL("create table puntos_carrera(" +
                "fecha timestamp default CURRENT_TIMESTAMP," +
                "id_carrera integer," +
                "latitud decimal(13,7)," +
                "longitud decimal(13,7)," +
                "id_pedido integer ," +
                "numero integer default 0,"+
                "primary key(id_carrera,latitud,longitud,id_pedido))");
        db.execSQL("drop table if exists notificacion");
        db.execSQL("create table notificacion(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT not null," +
                "titulo text," +
                "mensaje text," +
                "cliente text," +
                "id_pedido text," +
                "nombre text,"+
                "latitud text," +
                "longitud text," +
                "tipo text," +
                "fecha text," +
                "hora text," +
                "leido integer default 0," +
                "empresa text," +
                "direccion text," +
                "nombre_direccion text," +
                "detalle_direccion text" +
                ")");
    }
}

