package com.example.portoniot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.portoniot.datos.Conexion;

public class RegistroActivity extends AppCompatActivity {

    EditText etRut, etPassword, etPasswordConfirmar;
    Button btnRegistrar, btnLimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        relacionamosVistas();

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etRut.setText("");
                etPassword.setText("");
                etPasswordConfirmar.setText("");
            }
        });
    }

    public void relacionamosVistas() {

        etRut = (EditText) findViewById(R.id.rutRegistro);

        etPassword = (EditText)
                findViewById(R.id.passwordRegistro);

        etPasswordConfirmar = (EditText)
                findViewById(R.id.passwordConfirmar);

        btnRegistrar = (Button)
                findViewById(R.id.registrar);

        btnLimpiar = (Button)
                findViewById(R.id.mostrar);
    }

    public void registrarUsuario(View v) {

        final String rut =
                etRut.getText().toString();

        final String password =
                etPassword.getText().toString();

        final String passwordConfirmar =
                etPasswordConfirmar.getText().toString();

        if (rut.isEmpty()
                || password.isEmpty()
                || passwordConfirmar.isEmpty()) {

            Toast.makeText(
                    this,
                    "Debe completar todos los campos",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!password.equals(passwordConfirmar)) {

            Toast.makeText(
                    this,
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String url = Conexion.URL_WEB_SERVICES
                + "registro.php?rut="
                + rut
                + "&password="
                + password;

        RequestQueue servicio =
                Volley.newRequestQueue(this);

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