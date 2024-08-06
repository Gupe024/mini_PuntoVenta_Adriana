package com.example.minipuntoventa_adriana;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class insertar extends AppCompatActivity {

    EditText nombreInput;
    EditText precioInput;
    EditText cantidadInput;
    Button btnGuardar;
    Button btnCancelar;
    Button btnSalir;
    TextView idDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar);

        nombreInput = findViewById(R.id.nombre_input);
        precioInput = findViewById(R.id.precio_input);
        cantidadInput = findViewById(R.id.cantidad_input);
        btnGuardar = findViewById(R.id.btn_guardar);
        btnCancelar = findViewById(R.id.btn_cancelar);
        btnSalir = findViewById(R.id.btn_salir);
        idDisplay = findViewById(R.id.id_display);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarProducto();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarIngreso();
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void guardarProducto() {
        String nombre = nombreInput.getText().toString();
        String precio = precioInput.getText().toString();
        String cantidad = cantidadInput.getText().toString();

        if (nombre.isEmpty() || precio.isEmpty() || cantidad.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        OrdinarioBD db = new OrdinarioBD(this);
        SQLiteDatabase database = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("precio", Double.parseDouble(precio));
        values.put("cantidad", Integer.parseInt(cantidad));
        values.put("fecha", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

        long id = database.insert("productos", null, values);
        database.close();

        if (id != -1) {
            idDisplay.setText("ID del producto: " + id);
            Toast.makeText(this, "Producto guardado exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar el producto", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelarIngreso() {
        finish();
    }
}
