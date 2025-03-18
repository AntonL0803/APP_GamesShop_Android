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
import java.util.Map;

public class AdaptadorDetallesPedido extends RecyclerView.Adapter<AdaptadorDetallesPedido.MiViewHolderPedido> {
    private List<Producto> productos;

    private Map<String, Integer> imagenes;

    public AdaptadorDetallesPedido(List<Producto> productos, Map<String, Integer> imagenes) {
        this.productos = productos;
        this.imagenes = imagenes;
    }

    @NonNull
    @Override
    public AdaptadorDetallesPedido.MiViewHolderPedido onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View nuevaVista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_producto_detalles_pedidos, parent, false);
        return new MiViewHolderPedido(nuevaVista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorDetallesPedido.MiViewHolderPedido holder, int position) {
        Producto producto = productos.get(position);

        holder.nombreProducto.setText(producto.getNombre());
        holder.precio.setText("Precio: " + producto.getPrecio().toString() + "€");

        int cantidad = (int) producto.getCantidad();
        holder.cantidad.setText("Cantidad: " + cantidad);

        holder.imagenProducto.setImageResource(imagenes.get(producto.getNombre()));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public class MiViewHolderPedido extends RecyclerView.ViewHolder {
        TextView nombreProducto;
        TextView precio;
        TextView cantidad;
        ImageView imagenProducto;
        public MiViewHolderPedido(@NonNull View vista) {
            super(vista);
            nombreProducto = vista.findViewById(R.id.tvNombreProducto);
            precio = vista.findViewById(R.id.tvPrecioProducto);
            cantidad = vista.findViewById(R.id.tvCantidadProducto);
            imagenProducto= vista.findViewById(R.id.ivImagenProductoPedido);
        }
    }
}
