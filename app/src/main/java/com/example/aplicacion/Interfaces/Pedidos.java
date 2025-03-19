package com.example.aplicacion.Interfaces;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.aplicacion.Entidades.AdaptadorPedidos;
import com.example.aplicacion.Entidades.Pedido;
import com.example.aplicacion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass for displaying user orders.
 * Use the {@link Pedidos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Pedidos extends Fragment {
    private FirebaseDatabase db; // Instancia de Firebase Database
    private FirebaseAuth mAuth; // Instancia de Firebase Authentication
    private FirebaseUser user; // Usuario actual autenticado en Firebase
    private List<Pedido> pedidos; // Lista de pedidos del usuario
    private List<String> codigoPedido; // Lista de códigos de pedido
    private RecyclerView rvPedidos; // RecyclerView para mostrar los pedidos
    private AdaptadorPedidos adaptador; // Adaptador para el RecyclerView
    private SearchView buscador; // SearchView para buscar pedidos

    // Parámetros para inicializar el fragment
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Variables para los parámetros del fragment
    private String mParam1;
    private String mParam2;

    // Constructor vacío requerido para el fragment
    public Pedidos() {
        // Constructor vacío
    }

    // Factory method to create a new instance of this fragment
    public static Pedidos newInstance(String param1, String param2) {
        Pedidos fragment = new Pedidos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1); // Recuperar parámetro 1
            mParam2 = getArguments().getString(ARG_PARAM2); // Recuperar parámetro 2
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout del fragment
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        mAuth = FirebaseAuth.getInstance(); // Inicializar Firebase Auth
        user = mAuth.getCurrentUser(); // Obtener el usuario autenticado
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app"); // Inicializar Firebase Database
        pedidos = new ArrayList<Pedido>(); // Crear lista vacía de pedidos
        codigoPedido = new ArrayList<>(); // Crear lista vacía de códigos de pedidos

        // Inicializar RecyclerView y SearchView
        rvPedidos = view.findViewById(R.id.rvPedidos);
        buscador = view.findViewById(R.id.buscadorPedidos);

        cargaPedidos(); // Cargar los pedidos
        return view;
    }

    // Método para cargar los pedidos desde Firebase
    public void cargaPedidos() {
        String emailUser = user.getEmail().replace("@", "_").replace(".", "_"); // Obtener email del usuario y formatearlo
        DatabaseReference pedidosRef = FirebaseDatabase.getInstance().getReference()
                .child("Usuarios").child(emailUser).child("pedidos"); // Referencia a los pedidos del usuario

        // Listener para cargar los datos de Firebase
        pedidosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pedidos.clear(); // Limpiar lista de pedidos
                    codigoPedido.clear(); // Limpiar lista de códigos de pedido
                    // Recorrer los pedidos y agregar a la lista
                    for (DataSnapshot pedidosCaptura : snapshot.getChildren()) {
                        Pedido pedido = pedidosCaptura.getValue(Pedido.class); // Obtener el pedido desde Firebase
                        if (pedido != null) {
                            pedidos.add(pedido); // Agregar el pedido a la lista

                            // Limitar el código de pedido a 9 caracteres
                            String codigo = pedidosCaptura.getKey();
                            Log.d("PedidoKey", "Key del pedido: " + codigo);

                            if (codigo != null && codigo.length() > 9) {
                                codigo = codigo.substring(0, 9); // Limitar a 9 caracteres
                            }
                            codigoPedido.add(codigo); // Agregar el código del pedido

                            Log.d("pedido", "El pedido introducido es el siguiente" + pedido);
                        }
                    }
                }
                // Inicializar el adaptador y configurarlo con el RecyclerView
                adaptador = new AdaptadorPedidos(pedidos, codigoPedido, getContext());
                LinearLayoutManager layout = new LinearLayoutManager(getView().getContext());
                rvPedidos.setAdapter(adaptador); // Establecer adaptador
                rvPedidos.setLayoutManager(layout); // Establecer el LayoutManager

                // Configurar el SearchView para filtrar los pedidos
                buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String busqueda) {
                        adaptador.getFilter().filter(busqueda); // Filtrar los pedidos por texto
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String nuevoTexto) {
                        adaptador.getFilter().filter(nuevoTexto); // Filtrar los pedidos por texto
                        return false;
                    }
                });
                adaptador.notifyDataSetChanged(); // Notificar cambios al adaptador
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error de Firebase si ocurre
            }
        });
    }
}
