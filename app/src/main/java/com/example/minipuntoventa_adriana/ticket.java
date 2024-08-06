package com.example.minipuntoventa_adriana;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ticket extends AppCompatActivity {

    private TextView textViewTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        textViewTicket = findViewById(R.id.text_view_ticket);

        mostrarResumenVenta();
    }

    private void mostrarResumenVenta() {
        OrdinarioBD dbHelper = new OrdinarioBD(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT id_producto, cantidad, precio, importe FROM venta", null);
        StringBuilder ticketContent = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {
                String idProducto = cursor.getString(cursor.getColumnIndexOrThrow("id_producto"));
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                double importe = cursor.getDouble(cursor.getColumnIndexOrThrow("importe"));

                ticketContent.append("ID Producto: ").append(idProducto)
                        .append(", Cantidad: ").append(cantidad)
                        .append(", Precio: ").append(precio)
                        .append(", Importe: ").append(importe)
                        .append("\n");
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        Intent intent = getIntent();
        double totalVenta = intent.getDoubleExtra("totalVenta", 0);
        ticketContent.append("\nTotal Venta: ").append(totalVenta);

        textViewTicket.setText(ticketContent.toString());
    }
}
