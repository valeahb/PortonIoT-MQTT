package com.example.portoniot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText etRut, etPassword;
    Button btnAcceso, btnLimpiar;
    TextView tvRegistrarse, tvRecuperarPassword;

    FirebaseAuth mAuth;
    DatabaseReference referenciaRut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relacionamosVistas();

        mAuth = FirebaseAuth.getInstance();

        referenciaRut = FirebaseDatabase.getInstance()
                .getReference("rut_lookup");

        btnLimpiar.setOnClickListener(v -> {
            etRut.setText("");
            etPassword.setText("");
        });

        tvRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RegistroActivity.class
            );
            startActivity(intent);
        });

        tvRecuperarPassword.setOnClickListener(v ->
                recuperarPassword());
    }

    private void relacionamosVistas() {

        etRut = findViewById(R.id.rut);
        etPassword = findViewById(R.id.password);

        btnAcceso = findViewById(R.id.acceso);
        btnLimpiar = findViewById(R.id.mostrar);

        tvRegistrarse = findViewById(R.id.registrarse);
        tvRecuperarPassword =
                findViewById(R.id.recuperarPassword);
    }

    public void validar(View v) {

        String rut = etRut.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (rut.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Debe ingresar RUT y contraseña",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!rut.matches("^\\d{7,8}-[0-9Kk]$")) {
            Toast.makeText(this,
                    "Ingrese un RUT válido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this,
                    "La contraseña debe tener al menos 8 caracteres",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        buscarUsuario(rut, password);
    }

    private void buscarUsuario(String rut, String password) {

        referenciaRut.child(rut)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) {
                        Toast.makeText(this,
                                "RUT no registrado",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String email = snapshot.child("email")
                            .getValue(String.class);

                    if (email == null || email.isEmpty()) {
                        Toast.makeText(this,
                                "No se encontró el correo del usuario",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    iniciarSesion(email, password);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Error al consultar Firebase: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void iniciarSesion(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(this,
                                "Inicio de sesión correcto",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                MainActivity.this,
                                InicioActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(this,
                                "RUT o contraseña incorrectos",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void recuperarPassword() {

        String rut = etRut.getText().toString().trim();

        if (rut.isEmpty()) {
            Toast.makeText(this,
                    "Ingrese su RUT para recuperar la contraseña",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!rut.matches("^\\d{7,8}-[0-9Kk]$")) {
            Toast.makeText(this,
                    "Ingrese un RUT válido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        referenciaRut.child(rut)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) {
                        Toast.makeText(this,
                                "No se encontró una cuenta asociada",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String email = snapshot.child("email")
                            .getValue(String.class);

                    if (email == null || email.isEmpty()) {
                        Toast.makeText(this,
                                "No se encontró un correo registrado",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    Toast.makeText(this,
                                            "Se envió el enlace de recuperación "
                                                    + "al correo registrado",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this,
                                            "No se pudo enviar el correo: "
                                                    + task.getException()
                                                    .getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error al consultar el usuario",
                                Toast.LENGTH_LONG).show()
                );
    }
}
