package com.example.portoniot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.portoniot.datos.Conexion;

public class MainActivity extends AppCompatActivity {

    EditText etRut, etPassword;
    Button btnAcceso, btnLimpiar;
    TextView tvRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relacionamosVistas();

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRut.setText("");
                etPassword.setText("");
            }
        });

        tvRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(
                        MainActivity.this,
                        RegistroActivity.class
                );

                startActivity(i);
            }
        });
    }

    public void relacionamosVistas() {

        etRut = (EditText) findViewById(R.id.rut);
        etPassword = (EditText) findViewById(R.id.password);

        btnAcceso = (Button) findViewById(R.id.acceso);
        btnLimpiar = (Button) findViewById(R.id.mostrar);

        tvRegistrarse = (TextView) findViewById(R.id.registrarse);
    }

    public void validar(View v) {

        final String rut = etRut.getText().toString().trim();
        final String password = etPassword.getText().toString();

        // Validar RUT
        if (!rut.matches("^\\d{7,8}-[0-9Kk]$")) {

            Toast.makeText(
                    this,
                    "Ingrese un RUT válido. Ejemplo: 12345678-9",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Validar contraseña
        if (password.length() < 8 ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*[0-9].*") ||
                !password.matches(".*[^a-zA-Z0-9].*")) {

            Toast.makeText(
                    this,
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String url = Conexion.URL_WEB_SERVICES
                + "ingreso.php?rut="
                + rut
                + "&password="
                + password;

        RequestQueue servicio = Volley.newRequestQueue(this);

        StringRequest respuesta = new StringRequest(
                Request.Method.GET,
                url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(
                                getApplicationContext(),
                                response,
                                Toast.LENGTH_LONG
                        ).show();

                        if (response.trim().equals("Inicio de sesión correcto")) {

                            Intent i = new Intent(
                                    MainActivity.this,
                                    InicioActivity.class
                            );

                            startActivity(i);
                            finish();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(
                                getApplicationContext(),
                                "Error comunicación",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        servicio.add(respuesta);
    }

    public void regresar(View view) {

        Intent i = new Intent(
                this,
                MainActivity.class
        );

        startActivity(i);
    }
}