package com.example.aplicacion.Entidades;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdaptadorCarrito extends RecyclerView.Adapter<AdaptadorCarrito.MiViewHolderCarrito> implements View.OnClickListener {
    private List<Producto> productos = new ArrayList<>();
    private Map<Integer, Integer> imagenes;
    private View.OnClickListener listener;
    FirebaseDatabase db;
    FirebaseUser user;
    FirebaseAuth mAuth;


    public AdaptadorCarrito(List<Producto> productos, Map<Integer, Integer> imagenes) {
        this.productos = productos;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        this.imagenes = imagenes;
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

        //Calcular precio de toda la cantidad
        DecimalFormat formato = new DecimalFormat("#.##");
        String precioTotal = formato.format(productos.get(position).getPrecio() * productos.get(position).getCantidad());

        //Mostrar datos en el view
        holder.tvNombre.setText(productos.get(position).getNombre().toString());
        holder.tvPrecio.setText("Precio: " + precioTotal + "€");
        holder.ivProducto.setImageResource(R.drawable.perfil);
        holder.tvQuantity.setText(String.valueOf(productos.get(position).getCantidad()));

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumarVista(holder, position);
                calcularTotal(formato, holder, position);
                actualizarCantidad(productos.get(position).getNombre(), position);
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restarVista(holder, position);
                calcularTotal(formato, holder, position);
                actualizarCantidad(productos.get(position).getNombre(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }
    @Override
    public void onClick(View view) {
    }
    public void sumarVista(MiViewHolderCarrito holder, int position){
        productos.get(position).setCantidad(productos.get(position).getCantidad() + 1);
        holder.tvQuantity.setText(String.valueOf(productos.get(position).getCantidad()));
    }
    public void restarVista(MiViewHolderCarrito holder, int position){
        productos.get(position).setCantidad(productos.get(position).getCantidad() - 1);
        holder.tvQuantity.setText(String.valueOf(productos.get(position).getCantidad()));
    }
    public void actualizarCantidad(String nombreProducto, int position) {
        String emailUser = user.getEmail();
        DatabaseReference cantidadRef = db.getReference().child("Usuarios")
                .child(emailUser.replace("@", "_").replace(".", "_"))
                .child("carrito").child(nombreProducto).child("cantidad");
        cantidadRef.setValue(productos.get(position).getCantidad());
    }
    public void calcularTotal(DecimalFormat formato, MiViewHolderCarrito holder, int position){
        String precioTotal = formato.format(productos.get(position).getPrecio() * productos.get(position).getCantidad());
        holder.tvPrecio.setText("Precio: " + precioTotal + "€");
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
