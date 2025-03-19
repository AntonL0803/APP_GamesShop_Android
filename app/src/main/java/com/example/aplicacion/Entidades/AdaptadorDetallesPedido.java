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

/**
 * Adaptador para mostrar los detalles de los productos en un pedido.
 * Este adaptador se utiliza con un RecyclerView para mostrar los productos,
 * su cantidad y su precio, junto con la imagen correspondiente.
 */
public class AdaptadorDetallesPedido extends RecyclerView.Adapter<AdaptadorDetallesPedido.MiViewHolderPedido> {
    private List<Producto> productos; // Lista de productos en el pedido
    private Map<String, Integer> imagenes; // Mapa que contiene los nombres de productos y sus imágenes

    /**
     * Constructor del adaptador.
     * @param productos Lista de productos que se van a mostrar en el RecyclerView.
     * @param imagenes Mapa con los nombres de los productos y las referencias a sus imágenes.
     */
    public AdaptadorDetallesPedido(List<Producto> productos, Map<String, Integer> imagenes) {
        this.productos = productos;
        this.imagenes = imagenes;
    }

    /**
     * Crea una nueva vista para cada elemento en el RecyclerView.
     * @param parent El grupo de vistas en el que se insertará la nueva vista.
     * @param viewType Tipo de vista (no se utiliza en este caso).
     * @return Un nuevo ViewHolder que contiene la vista inflada.
     */
    @NonNull
    @Override
    public AdaptadorDetallesPedido.MiViewHolderPedido onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla la vista desde el archivo XML y la devuelve envuelta en un ViewHolder
        View nuevaVista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_producto_detalles_pedidos, parent, false);
        return new MiViewHolderPedido(nuevaVista);
    }

    /**
     * Asocia los datos del producto con las vistas correspondientes en el ViewHolder.
     * @param holder El ViewHolder que contiene las vistas donde se van a mostrar los datos.
     * @param position La posición del producto en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull AdaptadorDetallesPedido.MiViewHolderPedido holder, int position) {
        Producto producto = productos.get(position); // Obtiene el producto en la posición actual

        // Establece el nombre, precio y cantidad del producto en las vistas correspondientes
        holder.nombreProducto.setText(producto.getNombre());
        holder.precio.setText("Precio: " + producto.getPrecio().toString() + "€");

        int cantidad = (int) producto.getCantidad();
        holder.cantidad.setText("Cantidad: " + cantidad);

        // Establece la imagen del producto usando el nombre como clave en el mapa de imágenes
        holder.imagenProducto.setImageResource(imagenes.get(producto.getNombre()));
    }

    /**
     * Devuelve el número de productos en la lista.
     * @return El número de productos en la lista.
     */
    @Override
    public int getItemCount() {
        return productos.size();
    }

    /**
     * ViewHolder que mantiene las vistas de cada elemento en el RecyclerView.
     */
    public class MiViewHolderPedido extends RecyclerView.ViewHolder {
        TextView nombreProducto; // Texto que muestra el nombre del producto
        TextView precio; // Texto que muestra el precio del producto
        TextView cantidad; // Texto que muestra la cantidad del producto
        ImageView imagenProducto; // Imagen del producto

        /**
         * Constructor del ViewHolder.
         * @param vista La vista de cada elemento que se infló y contiene los elementos UI.
         */
        public MiViewHolderPedido(@NonNull View vista) {
            super(vista);
            // Asocia cada vista con los elementos correspondientes del XML
            nombreProducto = vista.findViewById(R.id.tvNombreProducto);
            precio = vista.findViewById(R.id.tvPrecioProducto);
            cantidad = vista.findViewById(R.id.tvCantidadProducto);
            imagenProducto= vista.findViewById(R.id.ivImagenProductoPedido);
        }
    }
}
