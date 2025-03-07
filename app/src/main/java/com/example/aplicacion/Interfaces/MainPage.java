package com.example.aplicacion.Interfaces;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
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
        // Configure ajustes de insets para pantalla completa
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = findViewById(R.id.tabLay);
        frame = findViewById(R.id.frameLayoutPrincipal);

        // Asignar íconos a las pestañas (solo si existen)
        if (tabLayout.getTabAt(0) != null){
            tabLayout.getTabAt(0).setIcon(R.drawable.user_solid);
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

        cargarFragmentoInicial();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                if (tab.getPosition() == 0){
                    transaction.replace(R.id.frameLayoutPrincipal, new Perfil());
                    transaction.addToBackStack(null);
                } else if (tab.getPosition() == 1) {
                    transaction.replace(R.id.frameLayoutPrincipal, new Tienda());
                    transaction.addToBackStack(null);
                } else if (tab.getPosition() == 2) {
                    transaction.replace(R.id.frameLayoutPrincipal, new Carro());
                    transaction.addToBackStack(null);
                } else if (tab.getPosition() == 3) {
                    transaction.replace(R.id.frameLayoutPrincipal, new Pedidos());
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
    public void cargarFragmentoInicial() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        transaction.replace(R.id.frameLayoutPrincipal, new Tienda());
        tabLayout.selectTab(tabLayout.getTabAt(1));

        transaction.commit();
    }
}