package com.example.aplicacion.Interfaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Registro extends AppCompatActivity {
    private EditText etnombreRegis, etEmailRegis, etCPRegis, etContrasenaRegis;
    private Button btRegis;
    private MaterialSwitch newsRegis;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database; // Referencia a Firebase
    private DatabaseReference nodoPadre;
    private SharedPreferences sharedPreferences;

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
        nodoPadre = database.getReference("Usuarios");// Referencia correcta para almacenar usuarios

        // Vincular elementos UI
        etEmailRegis = findViewById(R.id.etEmailRegistro);
        etnombreRegis = findViewById(R.id.etNombreRegistro);
        etCPRegis = findViewById(R.id.etCPRegistro);
        etContrasenaRegis = findViewById(R.id.etContrasenaRegistro);
        btRegis = findViewById(R.id.btRegistro);
        newsRegis = findViewById(R.id.newsRegistro);

        btRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarNuevoUsuario();
            }
        });
    }

    private void registrarNuevoUsuario() {
        String nombreRegis = etnombreRegis.getText().toString();
        String emailRegis = etEmailRegis.getText().toString();
        String passwordRegis = etContrasenaRegis.getText().toString();
        String cpRegis = etCPRegis.getText().toString();
        Boolean newRegis = newsRegis.isChecked();

        if (nombreRegis.isEmpty() || emailRegis.isEmpty() || cpRegis.isEmpty() || passwordRegis.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailRegis).matches()) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordRegis.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear usuario en Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(emailRegis, passwordRegis)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Guardar datos en Firebase Database
                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("nombre", nombreRegis);
                            userData.put("email", emailRegis);
                            userData.put("direccion", cpRegis);
                            userData.put("newsletter", newRegis);

                            String emailKey = emailRegis.replace(".", "_").replace("@", "_");
                            nodoPadre.child(emailKey).setValue(userData).addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    user.sendEmailVerification(); // Enviar verificación de email
                                    Toast.makeText(this, "Registro exitoso. Verifica tu email.", Toast.LENGTH_LONG).show();

                                    // Enviar datos al Login con Intent
                                    Intent intent = new Intent(Registro.this, Login.class);
                                    intent.putExtra("Nombre", nombreRegis);
                                    intent.putExtra("Email", emailRegis);
                                    intent.putExtra("Dirección", cpRegis);
                                    intent.putExtra("newsletter", newRegis);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "Error al guardar datos en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
