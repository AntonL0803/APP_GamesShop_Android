package com.example.aplicacion;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    TabItem perfil;
    TabItem tienda;
    TabItem pedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Configurar ajustes de insets para pantalla completa
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = findViewById(R.id.tabLay);

        // Asignar íconos a las pestañas (solo si existen)
        if (tabLayout.getTabAt(0) != null){
            tabLayout.getTabAt(0).setIcon(R.drawable.perfil);
        }
        if (tabLayout.getTabAt(1) != null){
            tabLayout.getTabAt(1).setIcon(R.drawable.tienda);
        }
        if (tabLayout.getTabAt(2) != null){
            tabLayout.getTabAt(2).setIcon(R.drawable.pedido);
        }
    }

}