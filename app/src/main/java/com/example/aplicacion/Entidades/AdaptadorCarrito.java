package com.example.aplicacion.Entidades;

import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorCarrito extends RecyclerView.Adapter<AdaptadorCarrito.MiViewHolderCarrito> implements View.OnClickListener {
    private List<Producto> productos = new ArrayList<>();
    private View.OnClickListener listener;
    FirebaseDatabase db;
    FirebaseUser user;
    FirebaseAuth mAuth;

    public AdaptadorCarrito(List<Producto> productos) {
        this.productos = productos;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
    }

    @NonNull
    @Override
    public MiViewHolderCarrito onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View nuevaVista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_producto_carrito, parent, false);
        nuevaVista.setOnClickListener(listener);
        return new MiViewHolderCarrito(nuevaVista);
    }

    @Override
    public void onBindViewHolder(@NonNull MiViewHolderCarrito holder, int position) {
        int posicionActual = holder.getBindingAdapterPosition();
        DecimalFormat formato = new DecimalFormat("#.##");
        holder.tvNombre.setText(productos.get(posicionActual).getNombre().toString());
        String precioTotal = formato.format(productos.get(posicionActual).getPrecio() * productos.get(posicionActual).getCantidad());
        holder.tvPrecio.setText("Precio: " + precioTotal + "€");
        holder.ivProducto.setImageResource(R.drawable.perfil);
        holder.tvQuantity.setText(String.valueOf(productos.get(posicionActual).getCantidad()));

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumarCantidad(productos.get(posicionActual).getNombre(), posicionActual);
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productos.get(posicionActual).getCantidad() == 0){
                } else {
                    restarCantidad(productos.get(posicionActual).getNombre(), posicionActual);
                }
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
    public void sumarCantidad(String nombreProducto, int posicionActual){
        String emailUser = user.getEmail();
        DatabaseReference usuariosReferencia = db.getReference().child("Usuarios")
                .child(emailUser.replace("@", "_").replace(".", "_"))
                .child("carrito")
                .child(nombreProducto)
                .child("cantidad");

        long nuevaCantidad = productos.get(posicionActual).getCantidad() + 1;
        usuariosReferencia.setValue(nuevaCantidad);
    }
    public void restarCantidad(String nombreProducto, int posicionActual) {
        String emailUser = user.getEmail();
        DatabaseReference usuariosReferencia = db.getReference().child("Usuarios")
                .child(emailUser.replace("@", "_").replace(".", "_"))
                .child("carrito")
                .child(nombreProducto)
                .child("cantidad");


        long nuevaCantidad = productos.get(posicionActual).getCantidad() - 1;
        if (nuevaCantidad >= 0) {
            usuariosReferencia.setValue(nuevaCantidad);
        }
    }
}
