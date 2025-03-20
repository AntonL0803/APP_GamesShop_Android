package com.example.aplicacion.Interfaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class Login extends AppCompatActivity {
    private EditText etUsuarioLogin, etContrasenaLogin; // Campos de texto para ingresar usuario y contraseña
    private TextView tvRegistrateLogin; // Enlace de registro
    private Button btIniciarSesion; // Botón para iniciar sesión
    private FirebaseAuth mAuth; // Instancia de FirebaseAuth para autenticación
    private GoogleSignInClient mGoogleSignInClient; // Cliente de Google Sign-In
    private SignInButton googleSignInButton; // Botón para iniciar sesión con Google
    private ActivityResultLauncher<Intent> signInResultLauncher; // Lanzador de actividad para Google Sign-In
    private VideoView videoView; // Video de fondo en la pantalla de inicio de sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Habilita el modo Edge-to-Edge para ajustar la interfaz
        setContentView(R.layout.activity_main_login);

        // Configuración del VideoView para reproducir un video de fondo
        videoView = findViewById(R.id.videoViewLogin);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fondo_login);
        videoView.setVideoURI(uri);
        videoView.start();

        // Configura el VideoView para que el video se repita en bucle
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
        });

        // Ajuste de la ventana para que se adapte a las barras del sistema (por ejemplo, la barra de estado)
        View rootView = findViewById(R.id.main);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Inicialización de las vistas (campos de texto, botones, etc.)
        etUsuarioLogin = findViewById(R.id.etUsuarioLogin);
        etContrasenaLogin = findViewById(R.id.etContrasenaLogin);
        tvRegistrateLogin = findViewById(R.id.tvRegistrateLogin);
        btIniciarSesion = findViewById(R.id.btIniciarSesionLogin);
        googleSignInButton = findViewById(R.id.btIniciarSesionGoogle);

        mAuth = FirebaseAuth.getInstance(); // Inicialización de FirebaseAuth

        // Configuración de listeners para los botones
        btIniciarSesion.setOnClickListener(view -> signInWithEmail()); // Login con email y contraseña
        googleSignInButton.setOnClickListener(view -> signInWithGoogle()); // Login con Google
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD); // Establece el tamaño del botón de Google

        // Redirección a la pantalla de registro
        tvRegistrateLogin.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
        });

        // Resultado de la actividad de Google Sign-In
        signInResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                firebaseAuthWithGoogle(task.getResult()); // Autenticación con Google
                            } else {
                                Toast.makeText(this, "Error en Google sign in.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(Login.this, "Error en Google Sign-In por el dato", Toast.LENGTH_SHORT).show();
                }
            });
    }

    // Método para iniciar sesión con correo y contraseña
    private void signInWithEmail() {
        String email = etUsuarioLogin.getText().toString().trim();
        String password = etContrasenaLogin.getText().toString().trim();

        // Verificación de campos vacíos
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Por favor, ingresa un email y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de formato de correo electrónico
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(Login.this, "Por favor, ingresa un email válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de longitud de la contraseña
        if (password.length() < 6) {
            Toast.makeText(Login.this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Intentar iniciar sesión con email y contraseña
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                            // Reproduce un sonido al iniciar sesión correctamente
                            MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.sonidologinexito);
                            mediaPlayer.start();
                            mediaPlayer.setOnCompletionListener(MediaPlayer::release); // Libera el reproductor después de la reproducción.

                            // Redirige a la página principal
                            Intent intent = new Intent(Login.this, MainPage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Muestra un mensaje de error dependiendo del tipo de error
                            if (task.getException() instanceof FirebaseNetworkException) {
                                Toast.makeText(Login.this, "Error de red. Verifica tu conexión.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Login.this, "Error: Usuario o contraseña incorrectos.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                });
    }

    // Método para iniciar sesión con Google
    private void signInWithGoogle() {
        // Configuración de Google Sign-In (está deprecado porque la mayoría de los móviles no soportan la nueva versión): pide el ID de cliente web y el email.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Se especifica el ID de cliente web
                .requestEmail() // Solicita el email
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut(); // Desconecta previamente al usuario de Google (si es necesario)
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInResultLauncher.launch(signInIntent); // Lanza el intent de Google Sign-In
    }

    // Método para autenticar al usuario con Google en Firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //Crea una credencial de autenticación con el ID de token de Google.
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    //Obtiene el usuario autenticado con Google en Firebase
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String email = user.getEmail();
                            String emailKey = email.replace(".", "_").replace("@", "_");
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(emailKey);

                            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                // Asigna los primeros datos al iniciar sesión con Google Firebase
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        dbRef.child("nombre").setValue(user.getDisplayName());
                                        dbRef.child("email").setValue(user.getEmail());
                                        dbRef.child("direccion").setValue("");
                                        dbRef.child("newsletter").setValue(false);
                                    }
                                    // Guarda los datos del usuario en SharedPreferences
                                    SharedPreferences preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("email", email);
                                    editor.putString("direccion", "");
                                    // editor.putString("newsletter", false);
                                    editor.putString("nombre", user.getDisplayName());
                                    editor.apply();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Database", "Error al acceder a la base de datos", error.toException());
                                }
                            });

                            // Inicia la actividad principal y cierra la pantalla de login
                            Toast.makeText(this, "Inicio de sesión exitoso con Google.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, MainPage.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Error al autenticar con Firebase.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}