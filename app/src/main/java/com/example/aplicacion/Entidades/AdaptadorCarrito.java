package com.example.aplicacion.Entidades;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdaptadorCarrito extends RecyclerView.Adapter<AdaptadorCarrito.MiViewHolderCarrito> implements View.OnClickListener {
    private List<Producto> productos = new ArrayList<>();
    private Map<String, Integer> imagenes;
    private View.OnClickListener listener;
    private Button btnComprar;
    private CarritoManager carritoManager;
    FirebaseDatabase db;
    FirebaseUser user;
    FirebaseAuth mAuth;

    public AdaptadorCarrito(List<Producto> productos, Map<String, Integer> imagenes, Button btnComprar) {
        this.productos = new ArrayList<>(productos);
        this.btnComprar = btnComprar;
        this.imagenes = imagenes;
        this.carritoManager = new CarritoManager();
    }

    @NonNull
    @Override
    public MiViewHolderCarrito onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View nuevaVista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_producto_carrito, parent, false);
        nuevaVista.setOnClickListener(listener);
        return new MiViewHolderCarrito(nuevaVista);
    }

    @Override
    public void onBindViewHolder(@NonNull MiViewHolderCarrito holder, @SuppressLint("RecyclerView") int position) {
        //Direcatmente cojemos el producto en cuestión
        Producto producto = productos.get(position);
        if (producto == null){
            Toast.makeText(holder.itemView.getContext(), "Vuelva a cargar el carrito", Toast.LENGTH_SHORT).show();
            return;
        }

        //Calcular precio de toda la cantidad
        DecimalFormat formato = new DecimalFormat("#.##");

        //Mostrar datos en el view
        Integer imagenRes = imagenes.get(producto.getNombre());
        if (imagenRes != null) {
            holder.ivProducto.setImageResource(imagenRes);
        } else {
            holder.ivProducto.setImageResource(R.drawable.perfil);
        }

        if (producto.getNombre() != null){
            holder.tvNombre.setText(producto.getNombre().toString());
        } else {
            holder.tvNombre.setText("nombre no disponible");
        }

        holder.tvPrecio.setText("Precio: " + formato.format(producto.getPrecio() * producto.getCantidad()) + "€");
        holder.tvQuantity.setText(String.valueOf(producto.getCantidad()));

        // Botón "+"
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                producto.setCantidad(producto.getCantidad() + 1);
                holder.tvQuantity.setText(String.valueOf(producto.getCantidad()));
                holder.tvPrecio.setText("Precio: " + formato.format(producto.getPrecio() * producto.getCantidad()) + "€");

                carritoManager.actualizarCantidadFirebase(producto.getNombre(), producto.getCantidad());
                calcularPrecioTotal(formato);
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long nuevaCantidad = producto.getCantidad() - 1;
                if (nuevaCantidad <= 0) {
                    productos.remove(position);
                    notifyItemRemoved(position);
                    carritoManager.eliminarProductoFirebase(producto.getNombre());
                } else {
                    producto.setCantidad(nuevaCantidad);
                    holder.tvQuantity.setText(String.valueOf(nuevaCantidad));
                    holder.tvPrecio.setText("Precio: " + formato.format(producto.getPrecio() * producto.getCantidad()) + "€");
                    carritoManager.actualizarCantidadFirebase(producto.getNombre(), producto.getCantidad());
                }
                calcularPrecioTotal(formato);
            }
        });
        calcularPrecioTotal(formato);
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }
    @Override
    public void onClick(View view) {
    }

    public void calcularPrecioTotal(DecimalFormat formato){
        double total = productos.stream()
                .filter(productos -> productos != null && productos.getPrecio() != null)
                .mapToDouble(productos -> productos.getPrecio() * productos.getCantidad())
                .sum();
        btnComprar.setText("Pagar: " + formato.format(total) + "€");
    }
    public void eliminarTodosProductos(){
        productos.clear();
        notifyDataSetChanged();
    }

    public class MiViewHolderCarrito extends RecyclerView.ViewHolder {
        ImageView ivProducto;
        TextView tvNombre;
        TextView tvPrecio;
        TextView cantidad;
        Button btnMinus;
        Button btnPlus;
        TextView tvQuantity;

        public MiViewHolderCarrito(@NonNull View nuevaVista) {
            super(nuevaVista);
            cantidad = nuevaVista.findViewById(R.id.cantidad);
            ivProducto = nuevaVista.findViewById(R.id.imagenProductoCarrito);
            tvNombre = nuevaVista.findViewById(R.id.nombreProductoCarrito);
            tvPrecio =  nuevaVista.findViewById(R.id.precioProductoTienda);
            btnMinus = nuevaVista.findViewById(R.id.btnMinus);
            btnPlus = nuevaVista.findViewById(R.id.btnPlus);
            tvQuantity = nuevaVista.findViewById(R.id.tvQuantity);
        }
    }
}
