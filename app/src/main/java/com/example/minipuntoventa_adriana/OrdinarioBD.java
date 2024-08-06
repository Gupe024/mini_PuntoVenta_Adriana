package com.example.minipuntoventa_adriana;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OrdinarioBD extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ordinario.db";
    private static final int DATABASE_VERSION = 2;

    public OrdinarioBD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_PRODUCTOS = "CREATE TABLE productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "precio REAL," +
                "cantidad_disponible INTEGER," +
                "imagen BLOB," +
                "fecha TEXT)";
        db.execSQL(CREATE_TABLE_PRODUCTOS);

        String CREATE_TABLE_VENTA = "CREATE TABLE venta (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_producto INTEGER," +
                "cantidad INTEGER," +
                "precio REAL," +
                "importe REAL)";
        db.execSQL(CREATE_TABLE_VENTA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE productos ADD COLUMN cantidad_disponible INTEGER DEFAULT 0");
        }
    }
}
