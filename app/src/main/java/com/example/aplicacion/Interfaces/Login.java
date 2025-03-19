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

public class Login extends AppCompatActivity {
    private EditText etUsuarioLogin, etContrasenaLogin;
    private TextView tvRegistrateLogin;
    private Button btIniciarSesion;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSignInButton;
    private ActivityResultLauncher<Intent> signInResultLauncher;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_login);

        videoView = findViewById(R.id.videoViewLogin);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fondo_pantalla_incial);
        videoView.setVideoURI(uri);
        videoView.start();

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
        });

        View rootView = findViewById(R.id.main);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        etUsuarioLogin = findViewById(R.id.etUsuarioLogin);
        etContrasenaLogin = findViewById(R.id.etContrasenaLogin);
        tvRegistrateLogin = findViewById(R.id.tvRegistrateLogin);
        btIniciarSesion = findViewById(R.id.btIniciarSesionLogin);
        googleSignInButton = findViewById(R.id.btIniciarSesionGoogle);

        //FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Botón de inicio de sesión
        btIniciarSesion.setOnClickListener(view -> signInWithEmail());
        googleSignInButton.setOnClickListener(view -> signInWithGoogle());
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        // Redirección al registro
        tvRegistrateLogin.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
        });

        signInResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    firebaseAuthWithGoogle(task.getResult());
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
                .addOnCompleteListener(task -> {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                            // Reproduce el sonido de inicio de sesión exitoso
                            MediaPlayer mediaPlayer = MediaPlayer.create(Login.this, R.raw.sonidologinexito);
                            mediaPlayer.start();
                            mediaPlayer.setOnCompletionListener(MediaPlayer::release); // Libera el reproductor después de la reproducción.

                            Intent intent = new Intent(Login.this, MainPage.class);
                            startActivity(intent);
                            finish();
                        } else {
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
        // Configuración de Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInResultLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String email = user.getEmail();
                            String emailKey = email.replace(".", "_").replace("@", "_");
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(emailKey);

                            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                //Asignamos los primeros datos al iniciar sesión con Google Firebase
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        dbRef.child("nombre").setValue(user.getDisplayName());
                                        dbRef.child("email").setValue(user.getEmail());
                                        dbRef.child("direccion").setValue("");
                                        dbRef.child("newsletter").setValue(false);
                                    }
                                    SharedPreferences preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("email", email);
                                    editor.putString("direccion", "");
                                    //editor.putString("newsletter", false);
                                    editor.putString("nombre", user.getDisplayName());
                                    editor.apply();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Database", "Error al acceder a la base de datos", error.toException());
                                }
                            });

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