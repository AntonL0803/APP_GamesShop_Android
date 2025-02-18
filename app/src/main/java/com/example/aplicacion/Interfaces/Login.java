package com.example.aplicacion.Interfaces;

import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;

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

        btIniciarSesion.setOnClickListener(new View.OnClickListener() {
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
        tvRegistrateLogin.setOnClickListener(new View.OnClickListener() {
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
        });
    }
    public void signInWithEmail(String emailText, String password){
        mAuth.signInWithEmailAndPassword(emailText,password);
    }

    public void registrarUsuario(String emailText, String password){
        mAuth.createUserWithEmailAndPassword(emailText,password);
    }
   /* public void iniciarSesion(View view){
        Intent intent = new Intent();
        intent.putExtra("usuario", etUsuarioLogin.getText().toString());
        intent.putExtra("contrasena", etContrasenaLogin.getText().toString());
        setResult(RESULT_OK, intent);
    }*/

}