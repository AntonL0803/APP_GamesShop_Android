// Importaciones necesarias para trabajar con el fragmento y la base de datos
package com.example.aplicacion.Interfaces;

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
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.aplicacion.Entidades.AdaptadorTienda;
import com.example.aplicacion.Entidades.BotonMas;
import com.example.aplicacion.Entidades.Producto;
import com.example.aplicacion.R;
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
 * Fragmento que representa la tienda de productos.
 * Muestra los productos en una lista o rejilla dependiendo de la opción seleccionada.
 */
public class Tienda extends Fragment {
    private RecyclerView rvTienda;  // RecyclerView para mostrar los productos
    private Switch swTienda;        // Switch para cambiar entre lista o rejilla

    private FirebaseDatabase db;   // Instancia de FirebaseDatabase

    private List<String> nombreProducto;  // Lista para almacenar los nombres de los productos
    private List<Double> precioProducto;  // Lista para almacenar los precios de los productos

    // Mapa de imágenes para cada producto
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

    // Parámetros para la creación del fragmento
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Constructor vacío requerido para crear el fragmento
    public Tienda() {
    }

    /**
     * Método de fábrica para crear una nueva instancia del fragmento.
     * @param param1 Primer parámetro.
     * @param param2 Segundo parámetro.
     * @return Nueva instancia de Tienda.
     */
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
        // Recupera los parámetros si existen
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Método para inflar el layout del fragmento y configurar las vistas.
     * @param inflater Inflador de vistas.
     * @param container Contenedor padre.
     * @param savedInstanceState Estado guardado.
     * @return Vista del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout y obtiene las vistas
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);
        rvTienda = view.findViewById(R.id.rvPedidos); // RecyclerView para productos
        swTienda = view.findViewById(R.id.switch1);    // Switch para cambiar entre vista de lista o rejilla
        nombreProducto = new ArrayList<>();
        precioProducto = new ArrayList<>();
        boolean isGrid = false; // Se inicia en vista de lista
        // Crea el adaptador
        AdaptadorTienda adaptador = new AdaptadorTienda(nombreProducto, precioProducto, isGrid, imagenes, null);

        // Configura el listener del botón "Más" en los productos (AdaptadorTienda)
        adaptador.setListenerBoton(new BotonMas() {
            @Override
            public void clickListener(int position) {
                adaptador.agregarAlCarrito(position);  // Agrega el producto al carrito
            }
        });

        // Configura el RecyclerView con el adaptador
        rvTienda.setAdapter(adaptador);

        // Configura los gestores de layout para vista en lista y rejilla
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);

        rvTienda.setLayoutManager(layoutManager); // Inicialmente vista en lista
        adaptador.notifyDataSetChanged();  // Notifica al adaptador que los datos se han actualizado en tiempo real

        // Configura el cambio entre lista y rejilla cuando se cambie el switch
        swTienda.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rvTienda.setLayoutManager(gridLayoutManager);  // Vista en rejilla
                adaptador.setGridLayout(true);
            } else {
                rvTienda.setLayoutManager(layoutManager);  // Vista en lista
                adaptador.setGridLayout(false);
            }
        });

        // Carga los datos de productos desde Firebase
        cargarDatosTienda(adaptador);

        // Configura el listener de clic en los productos para abrir la vista detallada
        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtiene los datos del producto clicado
                ImageView imagenProducto = view.findViewById(R.id.imagenProductoCarrito);
                TextView nombreProductoText = view.findViewById(R.id.nombreProductoTarjeta2);
                TextView precioProductoText = view.findViewById(R.id.precioProductoTienda);

                String nombreProducto = nombreProductoText.getText().toString().trim();
                String precioProductoStr = precioProductoText.getText().toString().trim();
                int imagenID = (int) imagenProducto.getTag();

                // Realiza la transición a la pantalla de detalles del producto
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.animator.slide_in_right,
                        R.animator.slide_out_left,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right
                );

                ProductoDetallado fragment = ProductoDetallado.newInstance(nombreProducto, precioProductoStr, imagenID);
                transaction.replace(R.id.frameLayoutPrincipal, fragment)
                        .addToBackStack(null)  // Añade el fragmento a la pila de retroceso
                        .commit();
            }
        });

        return view;  // Devuelve la vista inflada
    }

    /**
     * Método para cargar los datos de productos desde Firebase y actualizar la UI.
     * @param adaptador Adaptador de productos para el RecyclerView.
     */
    public void cargarDatosTienda(AdaptadorTienda adaptador) {
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference nodoPadre = db.getReference().child("Productos"); // Obtiene la referencia a la base de datos

        // Escucha los cambios en los datos de Firebase
        nodoPadre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nombreProducto.clear();  // Limpia las listas antes de agregar nuevos datos
                precioProducto.clear();

                if (snapshot.exists()) {
                    // Si existen datos, los agrega a las listas
                    for (DataSnapshot productoSnapshot : snapshot.getChildren()) {
                        Producto producto = productoSnapshot.getValue(Producto.class);
                        if (producto != null) {
                            nombreProducto.add(producto.getNombre());
                            precioProducto.add((producto.getPrecio()));

                            Log.d("Firebase", "Producto: " + producto.getNombre() + " - Precio: " + producto.getPrecio());
                        }
                    }
                    adaptador.notifyDataSetChanged();  // Notifica al adaptador que los datos se han actualizado
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
