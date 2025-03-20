package com.example.aplicacion.Interfaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion.R;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registro extends AppCompatActivity {
    // Declaración de variables para los elementos de la interfaz
    private EditText etnombreRegis, etEmailRegis, etDireccionRegis, etContrasenaRegis;
    private Button btRegis;
    private MaterialSwitch newsRegis;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference nodoPadre; // Referencia a Firebase
    private SharedPreferences sharedPreferences;
    private TextView tvIrAlLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializamos las Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        nodoPadre = database.getReference("Usuarios");// Referencia a la ubicación donde se almacenarán los datos del usuario

        // Vincular elementos UI
        etEmailRegis = findViewById(R.id.etEmailRegistro);
        etnombreRegis = findViewById(R.id.etNombreRegistro);
        etDireccionRegis = findViewById(R.id.etDireccionRegistro);
        etContrasenaRegis = findViewById(R.id.etContrasenaRegistro);
        btRegis = findViewById(R.id.btRegistro);
        newsRegis = findViewById(R.id.newsRegistro);
        tvIrAlLogin = findViewById(R.id.tvLogin);

        btRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarNuevoUsuario();
            }
        });

        //Botón de registro: Redirección al login
        tvIrAlLogin.setOnClickListener(view -> {
            Intent intentAlLogin = new Intent(Registro.this, Login.class);
            startActivity(intentAlLogin);
        });
    }

    // Método para registrar un nuevo usuario
    private void registrarNuevoUsuario() {
        // Obtener datos ingresados por el usuario
        String nombreRegis = etnombreRegis.getText().toString();
        String emailRegis = etEmailRegis.getText().toString();
        String passwordRegis = etContrasenaRegis.getText().toString();
        String direccionRegis = etDireccionRegis.getText().toString();
        Boolean newRegis = newsRegis.isChecked();

        // Validaciones de los campos
        if (nombreRegis.isEmpty() || emailRegis.isEmpty() || direccionRegis.isEmpty() || passwordRegis.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Función de Android: Validación del formato del email
        if (!Patterns.EMAIL_ADDRESS.matcher(emailRegis).matches()) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de la longitud mínima de la contraseña
        if (passwordRegis.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear y registra un usuario en Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(emailRegis, passwordRegis)
                //se ejecuta cuando el intento de registro termina
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Obtener usuario registrado
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) { //registro fue exitoso y el usuario está disponible.
                            //Crear un HashMap para almacenar los datos del usuario
                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("nombre", nombreRegis);
                            userData.put("email", emailRegis);
                            userData.put("direccion", direccionRegis);
                            userData.put("newsletter", newRegis);

                            // Convertir el email en clave válida para Firebase (reemplaza caracteres especiales)
                            String emailKey = emailRegis.replace(".", "_").replace("@", "_");

                            // Guardar los datos en la base de datos Firebase
                            nodoPadre.child(emailKey).setValue(userData).addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) { //Escritura

                                    // Enviar email de verificación al usuario
                                    user.sendEmailVerification();
                                    Toast.makeText(this, "Registro exitoso. Verifica tu email.", Toast.LENGTH_LONG).show();

                                    // Pasar datos al login mediante un Intent
                                    Intent intent = new Intent(Registro.this, Login.class);
                                    intent.putExtra("Nombre", nombreRegis);
                                    intent.putExtra("Email", emailRegis);
                                    intent.putExtra("Dirección", direccionRegis);
                                    intent.putExtra("newsletter", newRegis);
                                    startActivity(intent);
                                    finish(); // Cierra la actividad de registro
                                } else {
                                    Toast.makeText(this, "Error al guardar datos en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Error: el correo introducido ya existe", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
