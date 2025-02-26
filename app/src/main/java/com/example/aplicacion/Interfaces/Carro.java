package com.example.aplicacion.Interfaces;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aplicacion.Entidades.AdaptadorCarrito;
import com.example.aplicacion.Entidades.Producto;
import com.example.aplicacion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Carro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Carro extends Fragment {
    private RecyclerView rvCarro;
    private FirebaseDatabase db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private List<Producto> productos = new ArrayList<>();
    private Map<Integer, Integer> imagenes = new HashMap<Integer, Integer>() {{
        put(1, R.drawable.supermariobroswonder);
        put(2, R.drawable.biomutant);
        put(3, R.drawable.crash);
        put(4, R.drawable.donkeykongcountry);
        put(5, R.drawable.detectivepikachu);
        put(6, R.drawable.dragones3);
        put(7, R.drawable.drivingadventures);
        put(8, R.drawable.everybodyswitch);
        put(9, R.drawable.fitnessboxing);
        put(10, R.drawable.harvestella);
        put(11, R.drawable.hollowknight);
        put(12, R.drawable.justdance);
        put(13, R.drawable.kirby);
        put(14, R.drawable.mariodockerkong);
        put(15, R.drawable.mariopartyjamboree);
        put(16, R.drawable.mariopartysuperstars);
        put(17, R.drawable.minecraft);
        put(18, R.drawable.monsterhunterrise);
        put(19, R.drawable.mysims);
        put(20, R.drawable.peach);
        put(21, R.drawable.pokemondiamante);
        put(22, R.drawable.twopointcampus);
        put(23, R.drawable.zeldakingdom);
        put(24, R.drawable.zeldalink);
    }};


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Carro() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Carro.
     */
    // TODO: Rename and change types and number of parameters
    public static Carro newInstance(String param1, String param2) {
        Carro fragment = new Carro();
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
        View view = inflater.inflate(R.layout.fragment_carro, container, false);
        rvCarro = view.findViewById(R.id.rvCarrito);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            cargarDatosCarrito();
        } else {
            Log.e("Carro", "Usuario no autenticado");
        }
        return view;
    }
    public void cargarDatosCarrito(){
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        productos = new ArrayList<>();
        DatabaseReference usuariosReferencia = db.getReference().child("Usuarios");
        String emailUser = user.getEmail();
        DatabaseReference emailCarritoReferencia = usuariosReferencia.child(emailUser.replace("@", "_").replace(".", "_")).child("carrito");

        emailCarritoReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    productos.clear();
                    for (DataSnapshot productoSnapshot : snapshot.getChildren()) {
                        Producto producto = productoSnapshot.getValue(Producto.class);
                        if (producto != null){
                            productos.add(producto);
                        }
                    }
                    AdaptadorCarrito adaptador = new AdaptadorCarrito(productos);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    rvCarro.setLayoutManager(layoutManager);
                    rvCarro.setAdapter(adaptador);
                    rvCarro.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

                    adaptador.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}