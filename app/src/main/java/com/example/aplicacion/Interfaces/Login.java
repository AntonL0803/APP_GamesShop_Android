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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {
    private EditText etUsuarioLogin, etContrasenaLogin;
    private TextView tvRegistrateLogin;
    private Button btIniciarSesion;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSignInButton;

    private static final int RC_SIGN_IN = 9001; // Código de solicitud para el inicio de sesión con Google

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
        googleSignInButton = findViewById(R.id.btIniciarSesionGoogle);

        FirebaseApp.initializeApp(this);
        //Cogemos todos los paquetes de firebaseAuthentication
        mAuth = FirebaseAuth.getInstance();

        // Botón de inicio de sesión
        btIniciarSesion.setOnClickListener(view -> signInWithEmail());

        // Redirección al registro
        tvRegistrateLogin.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(view -> signInWithGoogle());

        /*// Botón de Google Sign-In
        //btIniciarSesionGoogle.setOnClickListener(v -> signInWithGoogle());
        // Configura el botón de Google Sign-In
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);  // Puedes elegir entre STANDARD o ICON_ONLY
        googleSignInButton.setOnClickListener(view -> {
            signInWithGoogle();
        });*/
    }

    private void signInWithEmail() {
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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                        // Redirigir a la pantalla principal
                        intentAlMain();
                    } else {
                        // Manejo de errores
                        if (task.getException() instanceof FirebaseNetworkException) {
                            Toast.makeText(Login.this, "Error de red. Verifica tu conexión a Internet.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Login.this, "Error: Usuario o contraseña introducidos incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Método para iniciar sesión con Google
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Manejar el resultado del inicio de sesión con Google
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GoogleSignIn", "Google sign-in exitoso: " + account.getEmail());
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Google sign-in fallido", e);
                Toast.makeText(this, "Error de inicio de sesión con Google. " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // Autenticación con Firebase usando el ID de Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Inicio de sesión exitoso con Google.", Toast.LENGTH_SHORT).show();
                        // Redirigir a la página principal
                        intentAlMain();
                    } else {
                        Toast.makeText(Login.this, "Error al autenticar con Firebase.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void intentAlMain() {
        Intent intent = new Intent(Login.this, MainPage.class);
        startActivity(intent);
        finish(); // Cerrar la actividad de login
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

