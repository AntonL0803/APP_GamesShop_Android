package com.example.aplicacion.Interfaces;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aplicacion.Entidades.AdaptadorCarrito;
import com.example.aplicacion.Entidades.CarritoManager;
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
    private String nombre;
    private RecyclerView rvCarro;
    private Button btnComprar;
    private FirebaseDatabase db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private CarritoManager carritoManager;
    private List<Producto> productos = new ArrayList<>();
    private Map<String, Integer> imagenes = new HashMap<String, Integer>() {{
        put("Super Mario Bros Wonder", R.drawable.supermariobroswonder);
        put("Biomutant", R.drawable.biomutant);
        put("Crash", R.drawable.crash);
        put("Donkey Kong Country", R.drawable.donkeykongcountry);
        put("Detective Pikachu", R.drawable.detectivepikachu);
        put("Dragones III", R.drawable.dragones3);
        put("Matching Driving Adventures", R.drawable.drivingadventures);
        put("Everybody Switch", R.drawable.everybodyswitch);
        put("Fitness Boxing", R.drawable.fitnessboxing);
        put("Harvestella", R.drawable.harvestella);
        put("Hollow Knight", R.drawable.hollowknight);
        put("Just Dance", R.drawable.justdance);
        put("Kirby y la tierra olvidada", R.drawable.kirby);
        put("Mario VS Donkey Kong", R.drawable.mariodockerkong);
        put("Mario Party Jamboree", R.drawable.mariopartyjamboree);
        put("Mario Party Superstars", R.drawable.mariopartysuperstars);
        put("Minecraft", R.drawable.minecraft);
        put("Monster Hunter Rise", R.drawable.monsterhunterrise);
        put("mySims Cozy Bundle", R.drawable.mysims);
        put("Princess Peach Showtime", R.drawable.peach);
        put("Pokemon Diamante Brillante", R.drawable.pokemondiamante);
        put("Two Point Campus", R.drawable.twopointcampus);
        put("Zelda tears of the kingdom", R.drawable.zeldakingdom);
        put("Zelda Links Awakening", R.drawable.zeldalink);
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
        carritoManager = new CarritoManager();
        rvCarro = view.findViewById(R.id.rvCarrito);
        btnComprar = view.findViewById(R.id.btPagarCarrito);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            cargarDatosCarrito();
        } else {
            Log.e("Carro", "Usuario no autenticado");
        }
        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialog();

            }
        });
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
                }
                for (Producto producto : productos) {
                    Log.d("Carro", "Producto: " + producto.getNombre() + ", Cantidad: " + producto.getCantidad());
                }
                AdaptadorCarrito adaptador = new AdaptadorCarrito(productos, imagenes, btnComprar);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                rvCarro.setLayoutManager(layoutManager);
                rvCarro.setAdapter(adaptador);
                adaptador.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void mostrarDialog(){
        EditText editText = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Datos necesrios")
                .setMessage("Introduce un nombre para el pedido")
                .setView(editText)
                .setPositiveButton("Aceptar",(dialog, which) -> {
                    nombre = editText.getText().toString();
                    if(!nombre.isEmpty()){
                        Toast.makeText(getContext(), "Porfavor, introduzca un valor", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}