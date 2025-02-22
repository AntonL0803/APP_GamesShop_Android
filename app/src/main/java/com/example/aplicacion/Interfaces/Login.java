package com.example.aplicacion.Interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText etUsuarioLogin;
    private EditText etContrasenaLogin;
    private TextView tvRegistrateLogin;
    private Button btIniciarSesion;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etUsuarioLogin = findViewById(R.id.etUsuarioLogin);
        etContrasenaLogin = findViewById(R.id.etContrasenaLogin);
        tvRegistrateLogin = findViewById(R.id.tvRegistrateLogin);
        btIniciarSesion = findViewById(R.id.btIniciarSesionLogin);

        FirebaseApp.initializeApp(this);
        //Cogemos todos los paquetes de firebaseAuthentication
        mAuth = FirebaseAuth.getInstance();

        // Botón de inicio de sesión
        btIniciarSesion.setOnClickListener(view -> {
            String email = etUsuarioLogin.getText().toString().trim();
            String password = etContrasenaLogin.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Por favor, ingresa un email y contraseña.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(Login.this, "Por favor, ingresa un email válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(Login.this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
                return;
            }

            signInWithEmail(email, password);
        });

        // Redirección al registro
        tvRegistrateLogin.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
        });
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                        // Redirigir a la pantalla principal
                        Intent intent = new Intent(Login.this, MainPage.class);
                        intent.putExtra("Email", email); // Use the email parameter
                        startActivity(intent);
                        finish(); // Cerrar la actividad de login
                    } else {
                        // Manejo de errores
                        if (task.getException() instanceof FirebaseNetworkException) {
                            Toast.makeText(Login.this, "Error de red. Verifica tu conexión a Internet.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
        /*btIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = etUsuarioLogin.getText().toString().trim();
                String password = etContrasenaLogin.getText().toString().trim();

                if (!emailText.isEmpty() && !password.isEmpty()) {
                    signInWithEmail(emailText, password);
                }else{
                    Toast.makeText(Login.this, "Por favor, ingresa un email o contraseña. ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvRegistrateLogin.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
        });*/

        /*tvRegistrateLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = etUsuarioLogin.getText().toString().trim();
                String password = etContrasenaLogin.getText().toString().trim();

                if (!emailText.isEmpty() && !password.isEmpty()) {
                    registrarUsuario(emailText, password);
                }else{
                    Toast.makeText(Login.this, "Por favor, ingresa un email o contraseña. ", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


    /*public void signInWithEmail(String emailText, String password){
        mAuth.signInWithEmailAndPassword(emailText,password)
            .addOnCompleteListener(this, task -> { //task: es una expresión lambda que define qué hacer cuando finaliza la tarea (resulado de la operación: exito o error)
                if(task.isSuccessful()){
                    FirebaseUser Fuser = mAuth.getCurrentUser(); //FUser: representa el usuario actualmente autentificadO
                    if(Fuser != null && Fuser.isEmailVerified()){
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        // Redirigir a la pantalla principal (Perfil o Dashboard)
                        Intent intent = new Intent(Login.this, Perfil.class);
                        intent.putExtra("Email", Fuser.getEmail());
                        startActivity(intent);
                        finish(); // Cerrar la actividad de login
                    }
                }else{
                    Toast.makeText(this, "Error, el usuario introducido no existe", Toast.LENGTH_SHORT).show();
                }
            });
    }*/
    /*public void signInWithEmail(String emailText, String password) {
        mAuth.signInWithEmailAndPassword(emailText, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Login", "Autenticación exitosa");
                        FirebaseUser Fuser = mAuth.getCurrentUser();
                        if (Fuser != null && Fuser.isEmailVerified()) {
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                            // Redirigir a la tienda
                            Intent intent = new Intent(Login.this, MainPage.class);
                            intent.putExtra("Email", Fuser.getEmail());
                            startActivity(intent);
                            finish(); // Cierra la pantalla de login
                        } else {
                            Toast.makeText(this, "Verifica tu email antes de iniciar sesión.", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    } else {
                        Log.d("Login", "Error en la autenticación");
                    }
                });
    }*/
    /*public void signInWithEmail(String emailText, String password) {
        signInWithEmail(emailText, password, 0); // Inicia el proceso de autenticación con 0 reintentos
    }

    private void signInWithEmail(String emailText, String password, int retryCount) {
        int maxRetries = 3; // Número máximo de reintentos

        mAuth.signInWithEmailAndPassword(emailText, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Login", "Autenticación exitosa");
                        FirebaseUser Fuser = mAuth.getCurrentUser();
                        if (Fuser != null && Fuser.isEmailVerified()) {
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainPage.class);
                            intent.putExtra("Email", Fuser.getEmail());
                            startActivity(intent);
                            finish(); // Cierra la actividad de login
                        } else {
                            Toast.makeText(this, "Verifica tu email antes de iniciar sesión.", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    } else {
                        Log.d("Login", "Error en la autenticación", task.getException());
                        if (task.getException() instanceof FirebaseNetworkException) {
                            if (retryCount < maxRetries) {
                                Toast.makeText(this, "Error de red, reintentando... (" + (retryCount + 1) + "/" + maxRetries + ")", Toast.LENGTH_SHORT).show();
                                // Espera 2 segundos antes de reintentar
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    signInWithEmail(emailText, password, retryCount + 1); // Reintenta
                                }, 2000);
                            } else {
                                Toast.makeText(this, "Error de red, no se pudo conectar después de " + maxRetries + " intentos.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, "Error en la autenticación: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }*/
    /*public void signInWithEmail(String emailText, String password) {
        signInWithEmail(emailText, password, 0); // Inicia el proceso de autenticación con 0 reintentos
    }

    private void signInWithEmail(String emailText, String password, int retryCount) {
        int maxRetries = 3; // Número máximo de reintentos

        mAuth.signInWithEmailAndPassword(emailText, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Login", "Autenticación exitosa");
                        FirebaseUser Fuser = mAuth.getCurrentUser();
                        if (Fuser != null && Fuser.isEmailVerified()) {
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainPage.class);
                            intent.putExtra("Email", Fuser.getEmail());
                            startActivity(intent);
                            finish(); // Cierra la actividad de login
                        } else {
                            Toast.makeText(this, "Verifica tu email antes de iniciar sesión.", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }
                    } else {
                        Log.d("Login", "Error en la autenticación", task.getException());
                        if (task.getException() instanceof FirebaseNetworkException) {
                            if (retryCount < maxRetries) {
                                Toast.makeText(this, "Error de red, reintentando... (" + (retryCount + 1) + "/" + maxRetries + ")", Toast.LENGTH_SHORT).show();
                                // Espera 2 segundos antes de reintentar
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    signInWithEmail(emailText, password, retryCount + 1); // Reintenta
                                }, 2000);
                            } else {
                                Toast.makeText(this, "Error de red, no se pudo conectar después de " + maxRetries + " intentos.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, "Error en la autenticación: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }*/



    /*public void registrarUsuario(String emailText, String password){
        mAuth.createUserWithEmailAndPassword(emailText,password);
        Intent intent = new Intent(Login.this, Registro.class);
        startActivity(intent);
    }*/
   /* public void iniciarSesion(View view){
        Intent intent = new Intent();
        intent.putExtra("usuario", etUsuarioLogin.getText().toString());
        intent.putExtra("contrasena", etContrasenaLogin.getText().toString());
        setResult(RESULT_OK, intent);
    }*/

