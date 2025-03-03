package com.example.aplicacion.Entidades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdaptadorTienda extends RecyclerView.Adapter<AdaptadorTienda.MiViewHolder> implements View.OnClickListener {

    private HashMap<Integer, Integer> imagenes;
    private List<String> nombreProductos;
    private List<Double> precioProductos;
    private View.OnClickListener listener;
    private BotonMas listenerBoton;
    private boolean isGridLayout;
    private FirebaseDatabase db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public AdaptadorTienda(List<String> nombreProductos, List<Double> precioProductos, boolean isGridLayout, Map<Integer, Integer> imagenes, BotonMas listenerBoton) {
        this.nombreProductos = nombreProductos;
        this.precioProductos = precioProductos;
        this.isGridLayout = isGridLayout;
        this.listenerBoton = listenerBoton;
        this.imagenes = new HashMap<>(imagenes);
    }

    public void setListenerBoton(BotonMas listenerBoton) {
        this.listenerBoton = listenerBoton;
    }

    public void setGridLayout(boolean isGridLayout) {
        this.isGridLayout = isGridLayout;
        notifyDataSetChanged(); // Recargar RecyclerView
    }

    @Override
    public int getItemViewType(int position) {
        return isGridLayout ? 1 : 0; // 1 para Grid, 0 para Linear
    }

    public MiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout para cada elemento
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View nuevaVista;
        if (viewType == 1) {
            nuevaVista = inflater.inflate(R.layout.tarjeta_producto_grid, parent, false);
        } else {
            nuevaVista = inflater.inflate(R.layout.tarjeta_producto_linear, parent, false);
        }
        nuevaVista.setOnClickListener(listener);
        return new MiViewHolder(nuevaVista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorTienda.MiViewHolder holder, int position) {
        int currentPosition = holder.getBindingAdapterPosition();
        posicionImagen(nombreProductos.get(currentPosition), holder);
        holder.tvNombre.setText(nombreProductos.get(currentPosition));
        holder.tvPrecio.setText("Precio: " + String.valueOf(precioProductos.get(currentPosition)));
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerBoton.clickListener(currentPosition);
                agregarAlCarrito(currentPosition);
            }
        });
    }

    public void agregarAlCarrito(int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");

        DatabaseReference usuariosReferencia = db.getReference().child("Usuarios");
        String emailUser = user.getEmail();
        DatabaseReference emailCarritoReferencia = usuariosReferencia.child(emailUser.replace("@", "_").replace(".", "_")).child("carrito");



        String productoSeleccionado = nombreProductos.get(position);
        Double precioSeleccionado = precioProductos.get(position);

        emailCarritoReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child(productoSeleccionado).exists()) {
                        Integer cantidadActual = snapshot.child(productoSeleccionado).child("cantidad").getValue(Integer.class);
                        if (cantidadActual == null) {
                            cantidadActual = 0;
                        }
                        emailCarritoReferencia.child(productoSeleccionado).child("cantidad").setValue(cantidadActual + 1);
                    } else {
                        Producto nuevoProducto = new Producto(productoSeleccionado, precioSeleccionado, 1);
                        emailCarritoReferencia.child(productoSeleccionado).setValue(nuevoProducto);
                    }
                } else {
                    Producto nuevoProducto = new Producto(productoSeleccionado, precioSeleccionado, 1);
                    emailCarritoReferencia.child(productoSeleccionado).setValue(nuevoProducto);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return nombreProductos.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {

    }

    public void posicionImagen(String nombreProductoTarjeta, MiViewHolder holder) {
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference nodoPadre = db.getReference().child("Productos");

        nodoPadre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productoSnapshot : snapshot.getChildren()) {
                    String nombre = productoSnapshot.child("nombre").getValue(String.class);
                    if (nombre.equals(nombreProductoTarjeta)) {
                        String clave = productoSnapshot.getKey();
                        if (clave != null && !clave.isEmpty()) {
                            try {
                                int posicion = Integer.parseInt(clave);
                                holder.ivProducto.setImageResource(imagenes.get(posicion));
                                holder.ivProducto.setTag(imagenes.get(posicion));
                            } catch (NumberFormatException e) {
                                holder.ivProducto.setImageResource(R.drawable.perfil);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Clase interna ViewHolder
    public class MiViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProducto;
        TextView tvNombre;
        TextView tvPrecio;
        ImageButton imageButton;

        public MiViewHolder(@NonNull View nuevaVista) {
            super(nuevaVista);
            ivProducto = nuevaVista.findViewById(R.id.imagenProductoCarrito);
            tvNombre = nuevaVista.findViewById(R.id.nombreProductoTarjeta2);
            tvPrecio =  nuevaVista.findViewById(R.id.precioProductoTienda);
            imageButton = nuevaVista.findViewById(R.id.imageButton);
        }
    }
}
