package com.example.aplicacion.Interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion.R;

public class Portada extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_portada);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvTitulo = findViewById(R.id.tvTituloSplash);
        Button btEmpezar = findViewById(R.id.btEmpezarSplash);

        tvTitulo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        btEmpezar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        btEmpezar.setOnClickListener(view -> {
            Intent intent = new Intent(Portada.this, Login.class);
            startActivity(intent);
            finish(); // Cierra la pantalla de bienvenida
        });
    }
}