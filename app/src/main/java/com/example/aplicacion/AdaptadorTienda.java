package com.example.aplicacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptadorTienda extends RecyclerView.Adapter<AdaptadorTienda.MiViewHolder> implements View.OnClickListener {

    private List<Producto> productos;
    private View.OnClickListener listener;

    public AdaptadorTienda(){
        this.productos = productos;
    }

    public MiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout para cada elemento
        View nuevaVista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_producto, parent, false);
        nuevaVista.setOnClickListener(listener);
        return new MiViewHolder(nuevaVista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorTienda.MiViewHolder holder, int position) {
        holder.ivProducto.setImageResource(R.drawable.perfil);
        holder.tvNombre.setText("Nombre del producto");
        holder.tvPrecio.setText("Precio: 0,00$");
    }

    @Override
    public int getItemCount() {
        return 10;
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

        public MiViewHolder(@NonNull View nuevaVista) {
            super(nuevaVista);
            ivProducto = nuevaVista.findViewById(R.id.imagenProductoTarjeta);
            tvNombre = nuevaVista.findViewById(R.id.nombreProductoTarjeta);
            tvPrecio =  nuevaVista.findViewById(R.id.precioProductoTarjeta);
        }
    }
}
