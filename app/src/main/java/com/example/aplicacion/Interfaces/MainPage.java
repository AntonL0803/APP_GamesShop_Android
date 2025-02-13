package com.example.aplicacion.Interfaces;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aplicacion.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainPage extends AppCompatActivity {
    TabLayout tabLayout;
    FrameLayout frame;
    TabItem perfil;
    TabItem tienda;
    TabItem carrito;
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
        frame = findViewById(R.id.frameLayout);

        // Asignar íconos a las pestañas (solo si existen)
        if (tabLayout.getTabAt(0) != null){
            tabLayout.getTabAt(0).setIcon(R.drawable.perfil);
        }
        if (tabLayout.getTabAt(1) != null){
            tabLayout.getTabAt(1).setIcon(R.drawable.tienda);
        }
        if (tabLayout.getTabAt(2) != null){
            tabLayout.getTabAt(2).setIcon(R.drawable.carro);
        }
        if (tabLayout.getTabAt(3) != null){
            tabLayout.getTabAt(3).setIcon(R.drawable.pedido);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                if (tab.getPosition() == 0){
                    transaction.replace(R.id.frameLayout, new Perfil());
                    transaction.addToBackStack(null);
                } else if (tab.getPosition() == 1) {
                    transaction.replace(R.id.frameLayout, new Tienda());
                    transaction.addToBackStack(null);
                } else if (tab.getPosition() == 2) {
                    transaction.replace(R.id.frameLayout, new Carro());
                    transaction.addToBackStack(null);
                } else if (tab.getPosition() == 3) {
                    transaction.replace(R.id.frameLayout, new Pedidos());
                    transaction.addToBackStack(null);
                }

                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}