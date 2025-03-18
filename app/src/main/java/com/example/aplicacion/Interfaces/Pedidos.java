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
 * A simple {@link Fragment} subclass.
 * Use the {@link Pedidos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Pedidos extends Fragment {
    private FirebaseDatabase db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private List<Pedido> pedidos;
    private List<String> codigoPedido;
    private RecyclerView rvPedidos;
    private AdaptadorPedidos adaptador;
    private SearchView buscador;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Pedidos() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        pedidos = new ArrayList<Pedido>();
        codigoPedido = new ArrayList<>();


        rvPedidos = view.findViewById(R.id.rvPedidos);
        buscador = view.findViewById(R.id.buscadorPedidos);

        cargaPedidos();
        return view;
    }

    public void cargaPedidos(){
        String emailUser = user.getEmail().replace("@", "_").replace(".", "_");
        DatabaseReference pedidosRef = FirebaseDatabase.getInstance().getReference()
                .child("Usuarios").child(emailUser).child("pedidos");

        pedidosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    pedidos.clear();
                    codigoPedido.clear();
                    for (DataSnapshot pedidosCaptura: snapshot.getChildren()) {
                        Pedido pedido = pedidosCaptura.getValue(Pedido.class);
                        if (pedido != null){
                            pedidos.add(pedido);

                            //Trozo de codigo para limitar el codigo de pedido a 9 caracteres
                            String codigo = pedidosCaptura.getKey();
                            Log.d("PedidoKey", "Key del pedido: " + codigo);

                            if (codigo != null && codigo.length() > 9) {
                                codigo = codigo.substring(0, 9);
                            }
                            codigoPedido.add(codigo);

                            Log.d("pedido", "El pedido introducido es el siguiente" + pedido);
                        }
                    }
                }
                adaptador = new AdaptadorPedidos(pedidos, codigoPedido);
                LinearLayoutManager layout = new LinearLayoutManager(getView().getContext());
                rvPedidos.setAdapter(adaptador);
                rvPedidos.setLayoutManager(layout);

                buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String busqueda) {
                        adaptador.getFilter().filter(busqueda);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String nuevoTexto) {
                        adaptador.getFilter().filter(nuevoTexto);
                        return false;
                    }
                });
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}