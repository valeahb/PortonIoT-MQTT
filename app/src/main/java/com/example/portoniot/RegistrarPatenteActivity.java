
package com.example.portoniot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrarPatenteActivity extends AppCompatActivity {

    EditText etPatente;
    Button btnGuardarPatente, btnVolver;

    FirebaseAuth mAuth;
    DatabaseReference referenciaPatentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_patente);

        etPatente = findViewById(R.id.etPatente);
        btnGuardarPatente = findViewById(R.id.btnGuardarPatente);
        btnVolver = findViewById(R.id.btnVolver);

        mAuth = FirebaseAuth.getInstance();
        referenciaPatentes = FirebaseDatabase.getInstance()
                .getReference("patentes");

        btnGuardarPatente.setOnClickListener(v -> guardarPatente());

        btnVolver.setOnClickListener(v -> finish());
    }

    private void guardarPatente() {

        String patente = etPatente.getText().toString()
                .trim().toUpperCase();

        if (patente.isEmpty()) {
            Toast.makeText(this, "Ingrese una patente",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!patente.matches("^(?:[A-Z]{4}[0-9]{2}|[A-Z]{2}[0-9]{4}|[A-Z]{3}[0-9]{3})$")) {
            Toast.makeText(this, "Formato de patente incorrecto",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Debe iniciar sesión",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> datos = new HashMap<>();
        datos.put("autorizada", true);
        datos.put("usuarioUid", uid);

        referenciaPatentes.child(patente).setValue(datos)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "Patente registrada correctamente",
                            Toast.LENGTH_LONG).show();

                    etPatente.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Error al guardar: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
