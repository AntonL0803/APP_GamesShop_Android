package com.example.aplicacion.Interfaces;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.aplicacion.Entidades.AdaptadorTienda;
import com.example.aplicacion.Entidades.BotonMas;
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
 * Use the {@link Tienda#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tienda extends Fragment {
    private RecyclerView rvTienda;
    private Switch swTienda;

    private FirebaseDatabase db;

    private List<String> nombreProducto;
    private List<Double> precioProducto;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

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

    public Tienda() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tienda.
     */
    // TODO: Rename and change types and number of parameters
    public static Tienda newInstance(String param1, String param2) {
        Tienda fragment = new Tienda();
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
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);
        rvTienda = view.findViewById(R.id.rvTienda);
        swTienda = view.findViewById(R.id.switch1);
        nombreProducto = new ArrayList<>();
        precioProducto = new ArrayList<>();
        boolean isGrid = false;
        AdaptadorTienda adaptador = new AdaptadorTienda(nombreProducto, precioProducto, isGrid, imagenes, null);

        adaptador.setListenerBoton(new BotonMas() {
            @Override
            public void clickListener(int position) {
                adaptador.agregarAlCarrito(position);
            }
        });

        rvTienda.setAdapter(adaptador);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);

        rvTienda.setLayoutManager(layoutManager);
        adaptador.notifyDataSetChanged();

        swTienda.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rvTienda.setLayoutManager(gridLayoutManager);
                adaptador.setGridLayout(true);
            } else {
                rvTienda.setLayoutManager(layoutManager);
                adaptador.setGridLayout(false);
            }
        });

        cargarDatos(adaptador);

        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView nombreProductoText = view.findViewById(R.id.nombreProductoTarjeta2);
                TextView precioProductoText = view.findViewById(R.id.precioProductoTienda);

                String nombreProducto = nombreProductoText.getText().toString().trim();
                String precioProductoStr = precioProductoText.getText().toString().trim();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.animator.slide_in_right,
                        R.animator.slide_out_left,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right
                );

                ProductoDetallado fragment = ProductoDetallado.newInstance(nombreProducto, precioProductoStr);
                transaction.replace(R.id.frameLayoutPrincipal, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
    public void cargarDatos(AdaptadorTienda adaptador){
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference nodoPadre = db.getReference().child("Productos");

        nodoPadre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nombreProducto.clear();
                precioProducto.clear();

                if (snapshot.exists()){
                    for (DataSnapshot productoSnapshot : snapshot.getChildren()) {
                        Producto producto = productoSnapshot.getValue(Producto.class);
                        if (producto != null){
                            nombreProducto.add(producto.getNombre());
                            precioProducto.add((producto.getPrecio()));

                            Log.d("Firebase", "Producto: " + producto.getNombre() + " - Precio: " + producto.getPrecio());
                        }
                    }
                    adaptador.notifyDataSetChanged();
                } else {
                    Log.d("Firebase", "No se encontraron los datos");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase", "Error al leer los datos");
            }
        });
    }

}