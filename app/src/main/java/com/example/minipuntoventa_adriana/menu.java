package com.example.minipuntoventa_adriana;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class menu extends AppCompatActivity {

    Button btnVenta;
    Button btnInventario;
    Button btnSalir;
    Button btnInsertar;
    Button btnEliminar;
    Button btnActualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnVenta = findViewById(R.id.btn_venta);
        btnInventario = findViewById(R.id.btn_inventario);
        btnSalir = findViewById(R.id.btn_salir);
        btnInsertar = findViewById(R.id.btn_insertar_producto);
        btnEliminar = findViewById(R.id.btn_eliminar_producto);
        btnActualizar = findViewById(R.id.btn_actualizar_producto);
        btnInsertar.setVisibility(View.INVISIBLE);
        btnEliminar.setVisibility(View.INVISIBLE);
        btnActualizar.setVisibility(View.INVISIBLE);

        btnVenta.setOnClickListener(v -> {
            Intent intent = new Intent(menu.this, venta.class);
            startActivity(intent);
        });

        btnInventario.setOnClickListener(v -> {
            btnInsertar.setVisibility(View.VISIBLE);
            btnEliminar.setVisibility(View.VISIBLE);
            btnActualizar.setVisibility(View.VISIBLE);
        });

        btnSalir.setOnClickListener(v -> finishAffinity());

        btnInsertar.setOnClickListener(v -> {
            Intent intent = new Intent(menu.this, insertar.class);
            startActivity(intent);
        });

        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());

        btnActualizar.setOnClickListener(v -> mostrarDialogoActualizar());

    }

    private void mostrarDialogoEliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Producto");

        View dialogView = getLayoutInflater().inflate(R.layout.dialogo_eliminar, null);
        final EditText inputId = dialogView.findViewById(R.id.input_id_producto);
        final TextView textViewDetalles = dialogView.findViewById(R.id.text_view_detalles);
        final Button btnBuscar = dialogView.findViewById(R.id.btn_buscar);

        builder.setView(dialogView);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String id = inputId.getText().toString();
            if (id.isEmpty()) {
                Toast.makeText(menu.this, "El ID no puede estar vacío", Toast.LENGTH_SHORT).show();
            } else {
                eliminarProducto(id);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        btnBuscar.setOnClickListener(v -> {
            String id = inputId.getText().toString();
            if (!id.isEmpty()) {
                mostrarDetallesProducto(id, textViewDetalles);
            } else {
                Toast.makeText(menu.this, "Ingrese un ID para buscar", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void mostrarDetallesProducto(String id, TextView textViewDetalles) {
        OrdinarioBD dbHelper = new OrdinarioBD(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] projection = {"nombre", "precio", "cantidad", "fecha"};
        String selection = "id = ?";
        String[] selectionArgs = {id};

        Cursor cursor = database.query("productos", projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
            int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

            String detalles = String.format("Nombre: %s\nPrecio: %.2f\nCantidad: %d\nFecha: %s", nombre, precio, cantidad, fecha);
            textViewDetalles.setText(detalles);
        } else {
            textViewDetalles.setText("No se encontró el producto con ID: " + id);
        }
        cursor.close();
        database.close();
    }

    private void eliminarProducto(String id) {
        OrdinarioBD dbHelper = new OrdinarioBD(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int rowsDeleted = database.delete("productos", "id = ?", new String[]{id});
        database.close();

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se pudo eliminar el producto", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoActualizar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Producto");

        View view = getLayoutInflater().inflate(R.layout.dialogo_actualizar, null);
        builder.setView(view);

        final EditText inputId = view.findViewById(R.id.input_id_producto);
        final EditText inputPrecio = view.findViewById(R.id.input_precio_producto);
        final EditText inputCantidad = view.findViewById(R.id.input_cantidad_producto);
        final TextView textViewFecha = view.findViewById(R.id.text_view_fecha_producto);
        final TextView textViewNombre = view.findViewById(R.id.input_nombre_producto);
        final Button btnBuscar = view.findViewById(R.id.btn_buscar);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String id = inputId.getText().toString();
            String precio = inputPrecio.getText().toString();
            String cantidad = inputCantidad.getText().toString();

            if (id.isEmpty() || precio.isEmpty() || cantidad.isEmpty()) {
                Toast.makeText(menu.this, "Ningún campo puede estar vacío", Toast.LENGTH_SHORT).show();
            } else {
                actualizarProducto(id, precio, cantidad);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        btnBuscar.setOnClickListener(v -> {
            String id = inputId.getText().toString();
            if (!id.isEmpty()) {
                mostrarDetallesProducto(id, inputPrecio, inputCantidad, textViewFecha, textViewNombre);
            } else {
                Toast.makeText(menu.this, "Ingrese un ID para buscar", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private String mostrarDetallesProducto(String id, EditText inputPrecio, EditText inputCantidad, TextView textViewFecha, TextView textViewNombre) {
        OrdinarioBD dbHelper = new OrdinarioBD(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String[] projection = {"nombre", "precio", "cantidad", "fecha"};
        String selection = "id = ?";
        String[] selectionArgs = {id};

        Cursor cursor = database.query("productos", projection, selection, selectionArgs, null, null, null);
        String nombre = "";
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
            int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

            inputPrecio.setText(String.valueOf(precio));
            inputCantidad.setText(String.valueOf(cantidad));
            textViewFecha.setText("Fecha: " + fecha);
            textViewNombre.setText(nombre);
        } else {
            inputPrecio.setText("");
            inputCantidad.setText("");
            textViewFecha.setText("Fecha: No disponible");
            textViewNombre.setText("Nombre: No disponible");
            Toast.makeText(this, "No se encontró el producto con ID: " + id, Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        database.close();
        return nombre;
    }


    private void actualizarProducto(String id, String precio, String cantidad) {
        OrdinarioBD dbHelper = new OrdinarioBD(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("precio", Double.parseDouble(precio));
        values.put("cantidad", Integer.parseInt(cantidad));

        int rowsUpdated = database.update("productos", values, "id = ?", new String[]{id});
        database.close();

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se pudo actualizar el producto", Toast.LENGTH_SHORT).show();
        }
    }
}

