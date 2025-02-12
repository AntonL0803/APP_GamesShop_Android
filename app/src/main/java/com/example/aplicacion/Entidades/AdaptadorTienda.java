package com.example.aplicacion.Entidades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.R;

import java.util.List;

public class AdaptadorTienda extends RecyclerView.Adapter<AdaptadorTienda.MiViewHolder> implements View.OnClickListener {

    private List<Producto> productos;
    private View.OnClickListener listener;
    private boolean isGridLayout;

    public AdaptadorTienda(boolean isGridLayout){
        this.productos = productos;
        this.isGridLayout = isGridLayout;
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
            ivProducto = nuevaVista.findViewById(R.id.imagenProductoTarjeta2);
            tvNombre = nuevaVista.findViewById(R.id.nombreProductoTarjeta2);
            tvPrecio =  nuevaVista.findViewById(R.id.precioProductoTarjeta2);
        }
    }
}
