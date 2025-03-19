package com.example.aplicacion.Interfaces;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aplicacion.Entidades.AdaptadorCarrito;
import com.example.aplicacion.Entidades.CarritoManager;
import com.example.aplicacion.Entidades.Pedido;
import com.example.aplicacion.Entidades.Producto;
import com.example.aplicacion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private double total;
    private RecyclerView rvCarro;
    private Button btnComprar;
    private FirebaseDatabase db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private CarritoManager carritoManager;
    private List<Producto> productos = new ArrayList<>();
    private Map<String, Integer> imagenes = new HashMap<String, Integer>() {{
        // Mapa de productos con sus respectivas imágenes.
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

    // TODO: Renombrar los parámetros según sea necesario.
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Renombrar y cambiar los tipos de los parámetros.
    private String mParam1;
    private String mParam2;

    public Carro() {
        // Constructor vacío requerido.
    }

    /**
     * Método de fábrica para crear una nueva instancia del fragmento Carro.
     *
     * @param param1 Parámetro 1.
     * @param param2 Parámetro 2.
     * @return Una nueva instancia del fragmento Carro.
     */
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
        // Si hay argumentos, inicializamos los parámetros.
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

        // Verificar si el usuario está autenticado.
        if (user != null) {
            cargarDatosCarritoAdaptador();  // Cargar los productos del carrito si el usuario está autenticado.
        } else {
            Log.e("Carro", "Usuario no autenticado");
        }

        // Configurar el botón de "Pagar Carrito".
        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialog();  // Mostrar el cuadro de diálogo para ingresar el nombre del pedido.
            }
        });
        return view;
    }

    // Método para obtener la lista de productos del carrito desde Firebase.
    public void getListaProductos(){
        String emailUser = user.getEmail().replace("@", "_").replace(".", "_");
        DatabaseReference carritoRef = FirebaseDatabase.getInstance().getReference()
                .child("Usuarios").child(emailUser).child("carrito");
        carritoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    // Iterar sobre los productos en el carrito.
                    for (DataSnapshot productoSnapshot : snapshot.getChildren()) {
                        productos.add(productoSnapshot.getValue(Producto.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Método para crear un pedido en Firebase con los productos del carrito.
    public void crearPedido(){
        getListaProductos();

        // Formato de la fecha actual.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fechaActual = sdf.format(new Date());

        total = 0;

        // Calcular el total del pedido.
        for (Producto producto : productos) {
            total += producto.getPrecio() * producto.getCantidad();
        }

        // Obtener el email del usuario y formatearlo para Firebase.
        String emailUser = user.getEmail().replace("@", "_").replace(".", "_");
        DatabaseReference pedidosRef = FirebaseDatabase.getInstance().getReference()
                .child("Usuarios").child(emailUser).child("pedidos");

        // Crear un objeto Pedido con los datos necesarios.
        Pedido pedido = new Pedido(nombre.toLowerCase(), fechaActual, total, productos);

        // Guardar el pedido en Firebase.
        pedidosRef.push().setValue(pedido);

        // Realizar la transacción de fragmentos.
        FragmentTransaction transaccion = getActivity().getSupportFragmentManager().beginTransaction();
        transaccion.replace(R.id.frameLayoutPrincipal, new Pedidos());
        transaccion.commit();

        // Vaciar el carrito en Firebase.
        carritoManager.vaciarCarritoFirebase();
    }

    // Método para cargar los productos del carrito y actualizar el adaptador.
    public void cargarDatosCarritoAdaptador(){
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        productos = new ArrayList<>();
        DatabaseReference usuariosReferencia = db.getReference().child("Usuarios");
        String emailUser = user.getEmail();
        DatabaseReference emailCarritoReferencia = usuariosReferencia.child(emailUser.replace("@", "_").replace(".", "_")).child("carrito");

        // Escuchar los cambios en los productos del carrito.
        emailCarritoReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    productos.clear();  // Limpiar la lista de productos.
                    for (DataSnapshot productoSnapshot : snapshot.getChildren()) {
                        Producto producto = productoSnapshot.getValue(Producto.class);
                        if (producto != null){
                            productos.add(producto);  // Agregar los productos a la lista.
                        }
                    }
                }
                // Configurar el adaptador con los productos y las imágenes.
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

    // Método para mostrar el cuadro de diálogo para ingresar el nombre del pedido.
    public void mostrarDialog(){
        EditText editText = new EditText(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Datos necesarios")
                .setMessage("Introduce un nombre para el pedido")
                .setView(editText)
                .setPositiveButton("Aceptar",(dialog, which) -> {
                    nombre = editText.getText().toString();
                    if(nombre.isEmpty()){
                        Toast.makeText(getContext(), "Porfavor, introduzca un valor", Toast.LENGTH_SHORT).show();
                    } else {
                        crearPedido();  // Crear el pedido si el nombre es válido.
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();

        dialog.show();

        // Configurar las dimensiones del cuadro de diálogo.
        Window window = dialog.getWindow();
        if (window != null){
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = 900;
            layoutParams.height = 600;
            window.setAttributes(layoutParams);
        }
    }
}
