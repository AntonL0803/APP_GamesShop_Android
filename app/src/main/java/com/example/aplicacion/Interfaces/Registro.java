package com.example.aplicacion.Interfaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    private Switch newsRegis;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database; // Referencia a Firebase
    private DatabaseReference myRef;
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
        //buscar cómo comprobar si los correos/usuarios está en la base de datos
        // Inicializamos las Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Usuarios");// Referencia correcta para almacenar usuarios

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
        sharedPreferences = getSharedPreferences(etEmailRegis.getText().toString(), MODE_PRIVATE);

        if (!nombreRegis.isEmpty() && !emailRegis.isEmpty() & !cpRegis.isEmpty() && !passwordRegis.isEmpty()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //(clave, valor)
            editor.putString("nombre", nombreRegis);
            editor.putString("email", emailRegis);
            editor.putString("cp", cpRegis);
            editor.putString("password", passwordRegis);
            editor.putBoolean("news", newRegis);
            editor.apply();

            editor.apply();

            // Enviar datos al perfil con Intent
            Intent intent = new Intent(Registro.this, Login.class);
            intent.putExtra("Nombre", nombreRegis);
            intent.putExtra("Email", emailRegis);
            intent.putExtra("CP", cpRegis);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Registro.this, "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show();
        }
    }
}
/*// Registrar el usuario con Firebase Auth
    //Meétodo para gestionar errores
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
  firebaseAuth.createUserWithEmailAndPassword(emailRegis, passwordRegis)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                    // Crear un HashMap con los datos del usuario
                        DatabaseReference userRef = myRef.child(user.getUid());
                        HashMap<String, Object> userData = new HashMap<>();
                        userData.put("nombre", nombreRegis);
                        userData.put("email", emailRegis);
                        userData.put("cp", cpRegis);
                        userData.put("news", newRegis);
                    }
            }
                    // Obtener el ID del usuario registrado
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    //Usuario usuario = new Usuario(nombreRegis, emailRegis, cpRegis);

                    // Guardar datos en Firebase Database
                    myRef.child(userId).setValue(user).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                            // Guardar sesión del usuario con SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId", userId);
                            editor.putString("nombre", nombreRegis);
                            editor.putString("email", emailRegis);
                            editor.putString("cp", cpRegis);
                            editor.apply();

                            // Enviar datos al perfil con Intent
                            Intent intent = new Intent(Registro.this, Perfil.class);
                            intent.putExtra("Nombre", nombreRegis);
                            intent.putExtra("Email", emailRegis);
                            intent.putExtra("CP", cpRegis);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(Registro.this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Registro.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };*/
