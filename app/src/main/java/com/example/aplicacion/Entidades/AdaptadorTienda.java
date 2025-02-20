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

    private List<String> nombreProductos;
    private List<Double> precioProductos;
    private View.OnClickListener listener;
    private boolean isGridLayout;

    public AdaptadorTienda(List<String> nombreProductos, List<Double> precioProductos, boolean isGridLayout) {
        this.nombreProductos = nombreProductos;
        this.precioProductos = precioProductos;
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
        holder.tvNombre.setText(nombreProductos.get(position));
        holder.tvPrecio.setText("Precio: " + String.valueOf(precioProductos.get(position)));
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

        public MiViewHolder(@NonNull View nuevaVista) {
            super(nuevaVista);
            ivProducto = nuevaVista.findViewById(R.id.imagenProductoCarrito);
            tvNombre = nuevaVista.findViewById(R.id.nombreProductoTarjeta2);
            tvPrecio =  nuevaVista.findViewById(R.id.precioProductoCarrito);
        }
    }
}
