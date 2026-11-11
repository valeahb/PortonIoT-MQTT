package com.example.portoniot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    EditText etRut, etEmail, etPassword, etPasswordConfirmar;
    Button btnRegistrar, btnLimpiar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        relacionamosVistas();

        mAuth = FirebaseAuth.getInstance();

        btnLimpiar.setOnClickListener(v -> {
            etRut.setText("");
            etEmail.setText("");
            etPassword.setText("");
            etPasswordConfirmar.setText("");
        });
    }

    public void relacionamosVistas() {

        etRut = findViewById(R.id.rutRegistro);
        etEmail = findViewById(R.id.emailRegistro);
        etPassword = findViewById(R.id.passwordRegistro);
        etPasswordConfirmar = findViewById(R.id.passwordConfirmar);

        btnRegistrar = findViewById(R.id.registrar);
        btnLimpiar = findViewById(R.id.mostrar);
    }

    public void registrarUsuario(View v) {

        String rut = etRut.getText().toString()
                .trim().toUpperCase();

        String email = etEmail.getText().toString().trim();

        String password = etPassword.getText().toString();

        String passwordConfirmar =
                etPasswordConfirmar.getText().toString();

        if (rut.isEmpty() || email.isEmpty()
                || password.isEmpty() || passwordConfirmar.isEmpty()) {

            Toast.makeText(this,
                    "Debe completar todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!rut.matches("^\\d{7,8}-[0-9K]$")) {

            Toast.makeText(this,
                    "Ingrese un RUT válido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            Toast.makeText(this,
                    "Ingrese un correo electrónico válido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordConfirmar)) {

            Toast.makeText(this,
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {

            Toast.makeText(this,
                    "La contraseña debe tener al menos 8 caracteres",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear cuenta en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        // Preparar los datos del usuario
                        Map<String, Object> datos = new HashMap<>();

                        datos.put("usuarios/" + uid + "/rut", rut);
                        datos.put("usuarios/" + uid + "/email", email);

                        datos.put("rut_lookup/" + rut + "/email", email);

                        // Guardar ambos registros en Firebase
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .updateChildren(datos)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(this,
                                            "Usuario registrado correctamente",
                                            Toast.LENGTH_LONG).show();

                                    mAuth.signOut();

                                    Intent intent = new Intent(
                                            RegistroActivity.this,
                                            MainActivity.class
                                    );

                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(this,
                                            "Error al guardar los datos: "
                                                    + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });

                    } else {

                        Toast.makeText(this,
                                "Error al registrar: "
                                        + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void regresar(View view) {

        Intent intent = new Intent(
                this,
                MainActivity.class
        );

        startActivity(intent);
        finish();
    }
}
