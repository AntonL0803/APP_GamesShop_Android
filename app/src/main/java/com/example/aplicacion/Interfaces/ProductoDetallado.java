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

import org.checkerframework.common.subtyping.qual.Bottom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductoDetallado#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductoDetallado extends Fragment {
    private ImageView imagenProductoDetallado;
    private ImageButton imagenButton;
    private TextView titulo;
    private TextView precio;
    private TextView descripcion;
    private FirebaseDatabase db;

    private Button btAddProducto;

    public ProductoDetallado() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(android.transition.TransitionInflater.from(getContext())
                .inflateTransition(android.R.transition.slide_right));
        setExitTransition(android.transition.TransitionInflater.from(getContext())
                .inflateTransition(android.R.transition.slide_left));

        if (getArguments() != null) {
            String nombre = getArguments().getString("nombre");
            Double precio = getArguments().getDouble("precio");
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProductoDetallado.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductoDetallado newInstance(String nombre, String precio, int imagenID) {
        ProductoDetallado fragment = new ProductoDetallado();
        Bundle args = new Bundle();
        args.putString("nombre", nombre);
        args.putString("precio", precio);
        args.putInt("imagenID", imagenID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producto_detallado, container, false);
        imagenProductoDetallado = view.findViewById(R.id.ivProductoDetallado);
        imagenButton = view.findViewById(R.id.ibflechaProductoDetallado);
        titulo = view.findViewById(R.id.tituloProductoDetallado);
        precio = view.findViewById(R.id.tvPrecioProductoDetallado);
        descripcion = view.findViewById(R.id.descripcionProductoDetallado);
        btAddProducto = view.findViewById(R.id.btAñadirProductoProductosDetallados);

        imagenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                transaction.setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right
                );
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        imagenProductoDetallado.setImageResource(getArguments().getInt("imagenID"));
        titulo.setText(getArguments().getString("nombre"));
        precio.setText(getArguments().getString("precio"));
        cargarDescripcion(descripcion);
        btAddProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarAlCarrito();
            }
        });
        return view;
    }
    public void cargarDescripcion(TextView tvDescripcion){
        db =FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference nodoPadre = db.getReference().child("Productos").child(titulo.getText().toString()).child("descripcion");

        nodoPadre.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    tvDescripcion.setText(snapshot.getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseError", "Error al obtener datos: " + error.getMessage());
            }
        });

        /*
        nodoPadre.orderByChild("nombre").equalTo(getArguments().getString("nombre"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot productoSnapshot : snapshot.getChildren()){
                                 Producto producto = productoSnapshot.getValue(Producto.class);
                                 tvDescripcion.setText(producto.getDescripcion());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

         */
    }
    public void agregarAlCarrito() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");

        DatabaseReference usuariosReferencia = db.getReference().child("Usuarios");
        String emailUser = user.getEmail();
        DatabaseReference emailProductoDetalladoReferencia = usuariosReferencia.child(emailUser.replace("@", "_").replace(".", "_")).child("carrito");



        String productoSeleccionado = titulo.getText().toString().trim();
        String[] precioSinProcesar = precio.getText().toString().split(" ");
        Double precioSeleccionado = Double.parseDouble(precioSinProcesar[1]);

        emailProductoDetalladoReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child(productoSeleccionado).exists()) {
                        Integer cantidadActual = snapshot.child(productoSeleccionado).child("cantidad").getValue(Integer.class);
                        if (cantidadActual == null) {
                            cantidadActual = 0;
                        }
                        emailProductoDetalladoReferencia.child(productoSeleccionado).child("cantidad").setValue(cantidadActual + 1);
                    } else {
                        Producto nuevoProducto = new Producto(productoSeleccionado, precioSeleccionado, 1);
                        emailProductoDetalladoReferencia.child(productoSeleccionado).setValue(nuevoProducto);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}