package com.example.aplicacion.Entidades;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.Interfaces.ProductoDetallado;
import com.example.aplicacion.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdaptadorTienda extends RecyclerView.Adapter<AdaptadorTienda.MiViewHolder> implements View.OnClickListener {

    private List<String> nombreProductos;
    private List<Double> precioProductos;
    private View.OnClickListener listener;
    private BotonMas listenerBoton;
    private boolean isGridLayout;

    public AdaptadorTienda(List<String> nombreProductos, List<Double> precioProductos, boolean isGridLayout, BotonMas listenerBoton) {
        this.nombreProductos = nombreProductos;
        this.precioProductos = precioProductos;
        this.isGridLayout = isGridLayout;
        this.listenerBoton = listenerBoton;
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
        if (viewType == 1){
            nuevaVista = inflater.inflate(R.layout.tarjeta_producto_grid, parent, false);
        } else {
            nuevaVista = inflater.inflate(R.layout.tarjeta_producto_linear, parent, false);
        }
        nuevaVista.setOnClickListener(listener);
        return new MiViewHolder(nuevaVista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorTienda.MiViewHolder holder,int position) {
        int currentPosition = holder.getBindingAdapterPosition();
        holder.ivProducto.setImageResource(R.drawable.perfil);
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

    public void agregarAlCarrito(int position){
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference nodoPadre = db.getReference().child("Carrito");

        String productoSeleccionado = nombreProductos.get(position);
        Double precioSeleccionado = precioProductos.get(position);

        Producto producto = new Producto(productoSeleccionado, precioSeleccionado, 1);
        DatabaseReference productoRef = nodoPadre.child(productoSeleccionado);
        productoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Producto productoExistente = snapshot.getValue(Producto.class);
                    if (productoExistente != null){
                        int nuevaCantidad = productoExistente.getCantidad() + 1;
                        productoRef.child("cantidad").setValue(nuevaCantidad);
                    }
                } else {
                    productoRef.setValue(producto);
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
