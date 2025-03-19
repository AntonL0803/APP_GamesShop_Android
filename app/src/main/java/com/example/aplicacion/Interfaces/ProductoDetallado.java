package com.example.aplicacion.Interfaces;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aplicacion.Entidades.Producto;
import com.example.aplicacion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Fragmento que muestra los detalles de un producto específico.
 * Permite al usuario agregar el producto al carrito de compras.
 */
public class ProductoDetallado extends Fragment {
    private ImageView imagenProductoDetallado;  // Imagen del producto
    private ImageButton imagenButton;  // Botón de flecha para regresar
    private TextView titulo;  // Título (nombre) del producto
    private TextView precio;  // Precio del producto
    private TextView descripcion;  // Descripción del producto
    private FirebaseDatabase db;  // Referencia a la base de datos Firebase

    private Button btAddProducto;  // Botón para añadir el producto al carrito

    // Constructor vacío
    public ProductoDetallado() {
    }

    // Método llamado al crear el fragmento
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Transición al entrar y salir del fragmento
        setEnterTransition(android.transition.TransitionInflater.from(getContext())
                .inflateTransition(android.R.transition.slide_right));
        setExitTransition(android.transition.TransitionInflater.from(getContext())
                .inflateTransition(android.R.transition.slide_left));

        // Obtener los argumentos pasados al fragmento
        if (getArguments() != null) {
            String nombre = getArguments().getString("nombre");
            Double precio = getArguments().getDouble("precio");
        }
    }

    // Método estático para crear una nueva instancia del fragmento con parámetros
    public static ProductoDetallado newInstance(String nombre, String precio, int imagenID) {
        ProductoDetallado fragment = new ProductoDetallado();
        Bundle args = new Bundle();
        args.putString("nombre", nombre);  // Nombre del producto
        args.putString("precio", precio);  // Precio del producto
        args.putInt("imagenID", imagenID);  // ID de la imagen del producto
        fragment.setArguments(args);  // Pasar los argumentos al fragmento
        return fragment;
    }

    // Método llamado para inflar la vista y configurar las vistas del fragmento
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producto_detallado, container, false);
        // Inicializar vistas
        imagenProductoDetallado = view.findViewById(R.id.ivProductoDetallado);
        imagenButton = view.findViewById(R.id.ibflechaProductoDetallado);
        titulo = view.findViewById(R.id.tituloProductoDetallado);
        precio = view.findViewById(R.id.tvPrecioProductoDetallado);
        descripcion = view.findViewById(R.id.descripcionProductoDetallado);
        btAddProducto = view.findViewById(R.id.btAñadirProductoProductosDetallados);

        // Configurar el botón de flecha para regresar al fragmento anterior
        imagenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                transaction.setCustomAnimations(
                        R.animator.slide_in_left,  // Animación de entrada
                        R.animator.slide_out_right  // Animación de salida
                );
                requireActivity().getSupportFragmentManager().popBackStack();  // Regresar al fragmento anterior
            }
        });

        // Establecer los detalles del producto desde los argumentos
        imagenProductoDetallado.setImageResource(getArguments().getInt("imagenID"));
        titulo.setText(getArguments().getString("nombre"));
        precio.setText(getArguments().getString("precio"));
        cargarDescripcion(descripcion);  // Cargar la descripción del producto desde Firebase

        // Configurar el botón para agregar el producto al carrito
        btAddProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarAlCarrito();  // Agregar el producto al carrito
            }
        });

        return view;
    }

    // Método para cargar la descripción del producto desde Firebase
    public void cargarDescripcion(TextView tvDescripcion){
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference nodoPadre = db.getReference().child("Productos").child(titulo.getText().toString()).child("descripcion");

        nodoPadre.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    tvDescripcion.setText(snapshot.getValue(String.class));  // Establecer la descripción
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseError", "Error al obtener datos: " + error.getMessage());
            }
        });
    }

    // Método para agregar el producto al carrito del usuario autenticado
    public void agregarAlCarrito() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Log.d("FirebaseError", "El usuario no está autenticado.");
            return;
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");

        DatabaseReference usuariosReferencia = db.getReference().child("Usuarios");
        String emailUser = user.getEmail();

        // Referencia al carrito del usuario en la base de datos
        DatabaseReference emailProductoDetalladoReferencia = usuariosReferencia
                .child(emailUser.replace("@", "_").replace(".", "_"))
                .child("carrito");

        String productoSeleccionado = titulo.getText().toString().trim();  // Nombre del producto
        String precioTexto = precio.getText().toString().replaceAll("[^0-9.]", "");  // Precio del producto
        Double precioSeleccionado = Double.parseDouble(precioTexto);  // Convertir a Double

        // Verificar si el producto ya está en el carrito
        emailProductoDetalladoReferencia.child(productoSeleccionado).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Si el producto ya está en el carrito, actualizar la cantidad
                    Integer cantidadActual = snapshot.child("cantidad").getValue(Integer.class);
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    Double precio = snapshot.child("precio").getValue(Double.class);

                    if (cantidadActual == null) cantidadActual = 0;
                    if (nombre == null) nombre = productoSeleccionado;  // Usar el nombre del producto si no está en la BD
                    if (precio == null) precio = precioSeleccionado;  // Usar el precio actual si no está en la BD

                    // Actualizar la información del producto en el carrito
                    Producto productoActualizado = new Producto(nombre, precio, cantidadActual + 1);
                    emailProductoDetalladoReferencia.child(productoSeleccionado).setValue(productoActualizado);
                } else {
                    // Si el producto no está en el carrito, añadirlo con cantidad = 1
                    Producto nuevoProducto = new Producto(productoSeleccionado, precioSeleccionado, 1);
                    emailProductoDetalladoReferencia.child(productoSeleccionado).setValue(nuevoProducto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseError", "Error al actualizar carrito: " + error.getMessage());
            }
        });
    }
}
