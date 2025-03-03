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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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
    private SignInClient signInClient;

    //private static final int RC_SIGN_IN = 9001;

    /*private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        SignInCredential credential = signInClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();
                        if (idToken != null) {
                            firebaseAuthWithGoogle(account);
                        } else {
                            Toast.makeText(this, "Error: ID Token nulo", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ApiException e) {
                        Log.e("GoogleSignIn", "Error en el inicio de sesión con Google", e);
                        Toast.makeText(this, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );*/
    /*private final ActivityResultLauncher<Intent> signInResultLauncher = registerForActivityResult(
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
            });*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_login);

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
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
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
                            String userId = user.getUid();
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId);

                            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        dbRef.child("username").setValue(user.getDisplayName());
                                        dbRef.child("email").setValue(user.getEmail());
                                        dbRef.child("codigopostal").setValue("");
                                    }
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

    /*private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInResultLauncher.launch(signInIntent);
    }
    private void signInWithGoogle() {
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                )
                .build();

        signInClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        signInLauncher.launch(intentSenderRequest);
                    } catch (Exception e) {
                        Log.e("GoogleSignIn", "Error lanzando IntentSender", e);
                    }
                })
                .addOnFailureListener(this, e -> Log.e("GoogleSignIn", "Error en Google Sign-In", e));
    }*/

     /*private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
   private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId);

                        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    dbRef.child("username").setValue(account.getDisplayName());
                                    dbRef.child("email").setValue(account.getEmail());
                                    dbRef.child("codigopostal").setValue("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Database", "Error al acceder a la base de datos", error.toException());
                            }
                        });

                        Toast.makeText(this, "Inicio de sesión exitoso con Google.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainPage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Error al autenticar con Firebase.", Toast.LENGTH_SHORT).show();
                    }

                });
    }*/


    /*// Autenticación con Firebase usando el ID de Google
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
}*/
 /*// Manejar el resultado del inicio de sesión con Google
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

    /*// Autenticación con Firebase usando el ID de Google
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
}*/
 /*// Manejar el resultado del inicio de sesión con Google
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
//package com.example.aplicacion.Interfaces;  import android.app.LauncherActivity; import android.content.Intent; import android.os.Bundle; import android.os.Handler; import android.os.Looper; import android.util.Log; import android.view.View; import android.widget.Button; import android.widget.EditText; import android.widget.TextView; import android.widget.Toast;  import androidx.activity.EdgeToEdge; import androidx.activity.result.ActivityResult; import androidx.activity.result.ActivityResultCallback; import androidx.activity.result.ActivityResultLauncher; import androidx.activity.result.contract.ActivityResultContract; import androidx.activity.result.contract.ActivityResultContracts; import androidx.appcompat.app.AppCompatActivity; import androidx.core.graphics.Insets; import androidx.core.view.ViewCompat; import androidx.core.view.WindowInsetsCompat;  import com.example.aplicacion.R; import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract; import com.firebase.ui.auth.IdpResponse; import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult; import com.google.android.gms.auth.api.signin.GoogleSignIn; import com.google.android.gms.auth.api.signin.GoogleSignInAccount; import com.google.android.gms.auth.api.signin.GoogleSignInClient; import com.google.android.gms.auth.api.signin.GoogleSignInOptions; import com.google.android.gms.common.SignInButton; import com.google.android.gms.common.api.ApiException; import com.google.android.gms.tasks.Task; import com.google.firebase.FirebaseApp; import com.google.firebase.FirebaseNetworkException; import com.google.firebase.auth.AuthCredential; import com.google.firebase.auth.FirebaseAuth; import com.google.firebase.auth.FirebaseUser; import com.google.firebase.auth.GoogleAuthProvider;  public class Login extends AppCompatActivity {     private EditText etUsuarioLogin, etContrasenaLogin;     private TextView tvRegistrateLogin;     private Button btIniciarSesion;     private FirebaseAuth mAuth;     private GoogleSignInClient mGoogleSignInClient;     private SignInButton googleSignInButton;      private final ActivityResultLauncher<Intent> signInResultLauncher =             registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),                     new ActivityResultCallback<ActivityResult>() {                         @Override                         public void onActivityResult(ActivityResult result) {                             if (result.getResultCode() == RESULT_OK) {                                 Toast.makeText(Login.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();                                 FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();                                 if (data != null) {                                     GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();                                     if (account != null) {                                         AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);                                         mAuth.signInWithCredential(credential)                                                 .addOnCompleteListener(Login.this, task -> {                                                     if (task.isSuccessful()) {                                                         Toast.makeText(Login.this, "Inicio de sesión exitoso con Google.", Toast.LENGTH_SHORT).show();                                                         startActivity(new Intent(Login.this, MainPage.class));                                                         finish();                                                     } else {                                                         // Firebase Authentication failed                                                         Toast.makeText(Login.this, "Error al autenticar con Firebase.", Toast.LENGTH_SHORT).show();                                                     }                                                 });                                     }                                 }                             } else {                                 Toast.makeText(Login.this, "Error en el inicio de sesión con Google.", Toast.LENGTH_SHORT).show();                             }                         }                     });        /*// Launcher para manejar el resultado del inicio de sesión con Google private final ActivityResultLauncher<Intent> signInResultLauncher = registerForActivityResult(         new ActivityResultContracts.StartActivityForResult(), result -> {             if (result.getResultCode() == RESULT_OK) {                 Toast.makeText(this, "entra", Toast.LENGTH_SHORT).show();                 Intent data = result.getData();                 if (data != null) {                     GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(task -> {                         if (task.isSuccessful()) {                             firebaseAuthWithGoogle(task.getResult());                         } else {                             Toast.makeText(this, "Error en Google sign in.", Toast.LENGTH_SHORT).show();                         }                     });                 }             }         });*/   /*private final ActivityResultLauncher<Intent> signInResultLauncher = registerForActivityResult(             new ActivityResultContracts.StartActivityForResult(), result -> {                 if (result.getResultCode() == RESULT_OK) {                      GoogleSignIn.getSignedInAccountFromIntent(result.getData()).addOnCompleteListener(task -> {                         if (task.isSuccessful()) {                             firebaseAuthWithGoogle(task.getResult());                         } else {                             Toast.makeText(this, "Error en Google sign in.", Toast.LENGTH_SHORT).show();                         }                     });                  }             });*/      @Override     protected void onCreate(Bundle savedInstanceState) {         super.onCreate(savedInstanceState);         EdgeToEdge.enable(this);         setContentView(R.layout.activity_main_login);          View rootView = findViewById(R.id.main);         if (rootView != null) {             ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {                 Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());                 v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);                 return insets;             });         }          etUsuarioLogin = findViewById(R.id.etUsuarioLogin);         etContrasenaLogin = findViewById(R.id.etContrasenaLogin);         tvRegistrateLogin = findViewById(R.id.tvRegistrateLogin);         btIniciarSesion = findViewById(R.id.btIniciarSesionLogin);         googleSignInButton = findViewById(R.id.btIniciarSesionGoogle);          FirebaseApp.initializeApp(this);         //Cogemos todos los paquetes de firebaseAuthentication         mAuth = FirebaseAuth.getInstance();          // Botón de inicio de sesión         btIniciarSesion.setOnClickListener(view -> signInWithEmail());          // Configuración de Google Sign-In         GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)                 .requestIdToken(getString(R.string.default_web_client_id))                 .requestEmail()                 .build();          mGoogleSignInClient = GoogleSignIn.getClient(this, gso);         googleSignInButton.setOnClickListener(view -> signInWithGoogle());         googleSignInButton.setSize(SignInButton.SIZE_STANDARD);          // Redirección al registro         tvRegistrateLogin.setOnClickListener(view -> {             Intent intent = new Intent(Login.this, Registro.class);             startActivity(intent);         });     }      private void signInWithEmail() {         String email = etUsuarioLogin.getText().toString().trim();         String password = etContrasenaLogin.getText().toString().trim();          if (email.isEmpty() || password.isEmpty()) {             Toast.makeText(Login.this, "Por favor, ingresa un email y contraseña.", Toast.LENGTH_SHORT).show();             return;         }          if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {             Toast.makeText(Login.this, "Por favor, ingresa un email válido.", Toast.LENGTH_SHORT).show();             return;         }          if (password.length() < 6) {             Toast.makeText(Login.this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();             return;         }          mAuth.signInWithEmailAndPassword(email, password)                 .addOnCompleteListener(task -> {                     new Handler(Looper.getMainLooper()).post(() -> {                         if (task.isSuccessful()) {                             Toast.makeText(Login.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();                             Intent intent = new Intent(Login.this, MainPage.class);                             startActivity(intent);                             finish();                         } else {                             if (task.getException() instanceof FirebaseNetworkException) {                                 Toast.makeText(Login.this, "Error de red. Verifica tu conexión.", Toast.LENGTH_LONG).show();                             } else {                                 Toast.makeText(Login.this, "Error: Usuario o contraseña incorrectos.", Toast.LENGTH_LONG).show();                             }                         }                     });                 });      }      // Método para iniciar sesión con Google     private void signInWithGoogle() {         //Manda a la página de inicio sesión con Google         Intent signInIntent = mGoogleSignInClient.getSignInIntent();         Toast.makeText(this, "funciona el cambio de pestaña", Toast.LENGTH_SHORT).show();         signInResultLauncher.launch(signInIntent);     }       /*private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {         IdpResponse response = result.getIdpResponse();         // Get the Firebase user         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();          if (response == null) {             Toast.makeText(this, "Inicio de sesión cancelado.", Toast.LENGTH_SHORT).show();             return;         }          if (result.getResultCode() == RESULT_OK) {             Toast.makeText(this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();              if (user != null) {                 // Check if the provider is Google                     if (account != null) {                         // Check if email is null                         String email = account.getEmail();                         if (email != null) {                             // Do something with the email                             Toast.makeText(this, "Email: " + email, Toast.LENGTH_SHORT).show();                         } else {                             Toast.makeText(this, "Email is null.", Toast.LENGTH_SHORT).show();                         }                          // Use the Google Sign-In credentials to authenticate with Firebase                         AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);                         mAuth.signInWithCredential(credential)                                 .addOnCompleteListener(this, authTask -> {                                     if (authTask.isSuccessful()) {                                         // Sign-in successful, redirect to MainPage                                         Toast.makeText(Login.this, "Inicio de sesión exitoso con Google.", Toast.LENGTH_SHORT).show();                                         startActivity(new Intent(Login.this, MainPage.class));                                         finish();                                     } else {                                         // Firebase Authentication failed                                         Toast.makeText(Login.this, "Error al autenticar con Firebase.", Toast.LENGTH_SHORT).show();                                     }                                 });                     } else {                         // Error occurred with the Google sign-in process                         Toast.makeText(this, "Error al obtener la cuenta de Google.", Toast.LENGTH_SHORT).show();                     }                 }             }         }     }*/ }      /*// Método para autenticar con Firebase usando la cuenta de Google     private void firebaseAuthWithGoogle(GoogleSignInAccount account) {         AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);         mAuth.signInWithCredential(credential)                 .addOnCompleteListener(this, task -> {                     if (task.isSuccessful()) {                         FirebaseUser user = mAuth.getCurrentUser();                         Toast.makeText(Login.this, "Inicio de sesión exitoso con Google.", Toast.LENGTH_SHORT).show();                         startActivity(new Intent(Login.this, MainPage.class));                         finish();                     } else {                         Toast.makeText(Login.this, "Error al autenticar con Firebase.", Toast.LENGTH_SHORT).show();                     }                 });     }*/        /*// Autenticación con Firebase usando el ID de Google     private void firebaseAuthWithGoogle(GoogleSignInAccount account) {         mAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))                 .addOnCompleteListener(this, task -> {                     if (task.isSuccessful()) {                         Toast.makeText(Login.this, "Inicio de sesión exitoso con Google.", Toast.LENGTH_SHORT).show();                         // Redirigir a la página principal                         intentAlMain();                     } else {                         Toast.makeText(Login.this, "Error al autenticar con Firebase.", Toast.LENGTH_LONG).show();                     }                 });     }     private void intentAlMain() {         Intent intent = new Intent(Login.this, MainPage.class);         startActivity(intent);         finish(); // Cerrar la actividad de login     } }*/      /*public void registrarUsuario(String emailText, String password){         mAuth.createUserWithEmailAndPassword(emailText,password);         Intent intent = new Intent(Login.this, Registro.class);         startActivity(intent);     }*/    /* public void iniciarSesion(View view){         Intent intent = new Intent();         intent.putExtra("usuario", etUsuarioLogin.getText().toString());         intent.putExtra("contrasena", etContrasenaLogin.getText().toString());         setResult(RESULT_OK, intent);     }*/
/*package com.example.aplicacion.Interfaces;

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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.auth.AuthCredential;
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

    private final ActivityResultLauncher<Intent> signInResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Log.d("Login", "onActivityResult: resultCode=" + result.getResultCode());
                            if (result.getResultCode() == RESULT_OK) {
                                Log.d("Login", "Google sign-in result OK");
                                Intent data = result.getData();
                                if (data != null) {
                                    Log.d("Login", "Google sign-in data received");
                                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                                    try {
                                        GoogleSignInAccount account = task.getResult(ApiException.class);
                                        Log.d("Login", "Google sign-in account received: " + account.getEmail());
                                        firebaseAuthWithGoogle(account.getIdToken());
                                    } catch (ApiException e) {
                                        Log.e("Login", "Google sign-in failed", e);
                                        Toast.makeText(Login.this, "Error en el inicio de sesión con Google.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.e("Login", "Google sign-in data is null");
                                    Toast.makeText(Login.this, "Error en el inicio de sesión con Google.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Login", "Google sign-in result not OK, resultCode=" + result.getResultCode());
                                // Puedes verificar los códigos de error específicos aquí si es necesario
                                if (result.getData() != null) {
                                    Log.e("Login", "Error data: " + result.getData().toString());
                                }
                                Toast.makeText(Login.this, "Error en el inicio de sesión con Google.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_login);

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

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        btIniciarSesion.setOnClickListener(view -> signInWithEmail());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(view -> signInWithGoogle());
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        tvRegistrateLogin.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
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

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInResultLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Login", "signInWithCredential:success");
                        Toast.makeText(Login.this, "Inicio de sesión exitoso con Google.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainPage.class);
                        Log.d("Login", "Starting MainPage activity");
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w("Login", "signInWithCredential:failure", task.getException());
                        Toast.makeText(Login.this, "Error al autenticar con Firebase.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}*/


