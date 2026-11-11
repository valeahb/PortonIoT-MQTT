package com.example.portoniot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class InicioActivity extends AppCompatActivity {

    Button btnVerificarPatente, btnRegistrarPatente, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        relacionamosVistas();

        btnRegistrarPatente.setOnClickListener(v -> {
            Intent intent = new Intent(
                    InicioActivity.this,
                    RegistrarPatenteActivity.class
            );
            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> {
            Intent i = new Intent(InicioActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }

    public void relacionamosVistas() {
        btnVerificarPatente = findViewById(R.id.verificarPatente);
        btnRegistrarPatente = findViewById(R.id.registrarPatente);
        btnCerrarSesion = findViewById(R.id.cerrarSesion);
    }
}
