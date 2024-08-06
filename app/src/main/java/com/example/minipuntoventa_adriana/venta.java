package com.example.minipuntoventa_adriana;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class venta extends AppCompatActivity {

    private ListView listViewProductos;
    private Button btnFinalizarVenta;
    private double totalVenta = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        listViewProductos = findViewById(R.id.list_view_productos);
        btnFinalizarVenta = findViewById(R.id.btn_finalizar_venta);

        cargarListaProductos();

        btnFinalizarVenta.setOnClickListener(v -> finalizarVenta());
    }

    private void cargarListaProductos() {
        OrdinarioBD dbHelper = new OrdinarioBD(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT id, nombre, precio FROM productos", null);
        ArrayList<String> productos = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String precio = cursor.getString(cursor.getColumnIndexOrThrow("precio"));
                productos.add("ID: " + id + " - Nombre: " + nombre + " - Precio: " + precio);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productos);
        listViewProductos.setAdapter(adapter);

        listViewProductos.setOnItemClickListener((parent, view, position, id) -> {
            String item = (String) parent.getItemAtPosition(position);
            String idProducto = item.split(" - ")[0].replace("ID: ", "");

            mostrarDialogoCantidad(idProducto);
        });
    }

    private void mostrarDialogoCantidad(String idProducto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresar Cantidad");

        final EditText inputCantidad = new EditText(this);
        inputCantidad.setHint("Cantidad");
        builder.setView(inputCantidad);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String cantidadStr = inputCantidad.getText().toString();
            if (cantidadStr.isEmpty()) {
                Toast.makeText(venta.this, "La cantidad no puede estar vacía", Toast.LENGTH_SHORT).show();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            guardarVenta(idProducto, cantidad);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void guardarVenta(String idProducto, int cantidad) {
        OrdinarioBD dbHelper = new OrdinarioBD(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT precio, cantidad_disponible FROM productos WHERE id = ?", new String[]{idProducto});
        if (cursor.moveToFirst()) {
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
            int cantidadDisponible = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_disponible"));

            if (cantidad > cantidadDisponible) {
                Toast.makeText(this, "No hay suficiente stock", Toast.LENGTH_SHORT).show();
                cursor.close();
                database.close();
                return;
            }

            double importe = precio * cantidad;
            totalVenta += importe;

            ContentValues values = new ContentValues();
            values.put("id_producto", idProducto);
            values.put("cantidad", cantidad);
            values.put("precio", precio);
            values.put("importe", importe);

            SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
            long result = writableDb.insert("venta", null, values);
            Log.d("venta", "Resultado de inserción: " + result);

            ContentValues updateValues = new ContentValues();
            updateValues.put("cantidad_disponible", cantidadDisponible - cantidad);
            writableDb.update("productos", updateValues, "id = ?", new String[]{idProducto});

            writableDb.close();
        }
        cursor.close();
        database.close();
    }

    private void finalizarVenta() {
        Intent intent = new Intent(venta.this, ticket.class);
        intent.putExtra("totalVenta", totalVenta);
        startActivity(intent);
    }
}
