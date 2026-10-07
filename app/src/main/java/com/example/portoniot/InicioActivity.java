package com.example.portoniot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InicioActivity extends AppCompatActivity {

    Button btnVerificarPatente, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        relacionamosVistas();

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(InicioActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });
    }

    public void relacionamosVistas() {

        btnVerificarPatente = (Button) findViewById(R.id.verificarPatente);
        btnCerrarSesion = (Button) findViewById(R.id.cerrarSesion);

    }
}

