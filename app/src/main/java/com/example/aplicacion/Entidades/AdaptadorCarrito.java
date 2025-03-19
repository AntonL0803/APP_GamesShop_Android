package com.example.aplicacion.Entidades;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private List<Producto> productos = new ArrayList<>(); // Lista de productos en el carrito
    private Map<String, Integer> imagenes; // Mapa de imágenes asociadas a productos
    private View.OnClickListener listener; // Listener para eventos de clic
    private Button btnComprar; // Botón de compra
    private CarritoManager carritoManager; // Instancia para gestionar el carrito
    FirebaseDatabase db; // Instancia de Firebase Database
    FirebaseUser user; // Usuario actual autenticado en Firebase
    FirebaseAuth mAuth; // Instancia de Firebase Authentication

    // Constructor que recibe lista de productos, mapa de imágenes y botón de compra
    public AdaptadorCarrito(List<Producto> productos, Map<String, Integer> imagenes, Button btnComprar) {
        this.productos = new ArrayList<>(productos); // Copia de la lista de productos
        this.btnComprar = btnComprar; // Inicialización del botón de compra
        this.imagenes = imagenes; // Inicialización del mapa de imágenes
        this.carritoManager = new CarritoManager(); // Inicialización del gestor de carrito
    }

    @NonNull
    @Override
    public MiViewHolderCarrito onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar la vista para cada item en el RecyclerView
        View nuevaVista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_producto_carrito, parent, false);
        nuevaVista.setOnClickListener(listener); // Establecer el listener de clic
        return new MiViewHolderCarrito(nuevaVista); // Devolver el ViewHolder creado
    }

    @Override
    public void onBindViewHolder(@NonNull MiViewHolderCarrito holder, @SuppressLint("RecyclerView") int position) {
        // Obtener el producto actual en la posición
        Producto producto = productos.get(position);
        if (producto == null) {
            // Mostrar un mensaje si el producto es nulo
            Toast.makeText(holder.itemView.getContext(), "Vuelva a cargar el carrito", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear formato de precio
        DecimalFormat formato = new DecimalFormat("#.##");

        // Asignar la imagen del producto, si existe en el mapa de imágenes
        Integer imagenRes = imagenes.get(producto.getNombre());
        if (imagenRes != null) {
            holder.ivProducto.setImageResource(imagenRes); // Asignar imagen
        } else {
            holder.ivProducto.setImageResource(R.drawable.perfil); // Imagen por defecto si no existe
        }

        // Asignar el nombre del producto
        if (producto.getNombre() != null) {
            holder.tvNombre.setText(producto.getNombre().toString());
        } else {
            holder.tvNombre.setText("nombre no disponible"); // Nombre por defecto si no está disponible
        }

        // Calcular y mostrar el precio total del producto basado en su cantidad
        holder.tvPrecio.setText("Precio: " + formato.format(producto.getPrecio() * producto.getCantidad()) + "€");
        holder.tvQuantity.setText(String.valueOf(producto.getCantidad()));

        // Acción del botón "+" para aumentar la cantidad del producto
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                producto.setCantidad(producto.getCantidad() + 1); // Aumentar la cantidad
                holder.tvQuantity.setText(String.valueOf(producto.getCantidad())); // Actualizar la UI
                holder.tvPrecio.setText("Precio: " + formato.format(producto.getPrecio() * producto.getCantidad()) + "€");

                carritoManager.actualizarCantidadFirebase(producto.getNombre(), producto.getCantidad()); // Actualizar en Firebase
                calcularPrecioTotal(formato); // Recalcular el precio total
            }
        });

        // Acción del botón "-" para disminuir la cantidad del producto
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long nuevaCantidad = producto.getCantidad() - 1;
                if (nuevaCantidad <= 0) {
                    productos.remove(position); // Eliminar producto si la cantidad es 0 o menor
                    notifyItemRemoved(position); // Notificar la eliminación del item
                    carritoManager.eliminarProductoFirebase(producto.getNombre()); // Eliminar producto de Firebase
                } else {
                    producto.setCantidad(nuevaCantidad); // Actualizar la cantidad
                    holder.tvQuantity.setText(String.valueOf(nuevaCantidad)); // Actualizar la UI
                    holder.tvPrecio.setText("Precio: " + formato.format(producto.getPrecio() * producto.getCantidad()) + "€");
                    carritoManager.actualizarCantidadFirebase(producto.getNombre(), producto.getCantidad()); // Actualizar en Firebase
                }
                calcularPrecioTotal(formato); // Recalcular el precio total
            }
        });

        // Llamar a la función que recalcula el precio total
        calcularPrecioTotal(formato);
    }

    @Override
    public int getItemCount() {
        return productos.size(); // Retornar la cantidad de productos en el carrito
    }

    @Override
    public void onClick(View view) {
        // Método vacío implementado de la interfaz OnClickListener
    }

    // Método que recalcula el precio total del carrito y actualiza el texto del botón de compra
    public void calcularPrecioTotal(DecimalFormat formato) {
        double total = productos.stream()
                .filter(productos -> productos != null && productos.getPrecio() != null) // Filtrar productos no nulos
                .mapToDouble(productos -> productos.getPrecio() * productos.getCantidad()) // Calcular el precio por producto
                .sum(); // Sumar el precio total
        btnComprar.setText("Pagar: " + formato.format(total) + "€"); // Actualizar el texto del botón de compra
    }

    // Método para eliminar todos los productos del carrito
    public void eliminarTodosProductos() {
        productos.clear(); // Limpiar la lista de productos
        notifyDataSetChanged(); // Notificar que los datos han cambiado
    }

    // ViewHolder para representar cada item en el RecyclerView
    public class MiViewHolderCarrito extends RecyclerView.ViewHolder {
        ImageView ivProducto; // Imagen del producto
        TextView tvNombre; // Nombre del producto
        TextView tvPrecio; // Precio del producto
        TextView cantidad; // Cantidad del producto
        Button btnMinus; // Botón para disminuir la cantidad
        Button btnPlus; // Botón para aumentar la cantidad
        TextView tvQuantity; // TextView que muestra la cantidad

        public MiViewHolderCarrito(@NonNull View nuevaVista) {
            super(nuevaVista);
            cantidad = nuevaVista.findViewById(R.id.cantidad); // Inicializar cantidad
            ivProducto = nuevaVista.findViewById(R.id.imagenProductoCarrito); // Inicializar imagen
            tvNombre = nuevaVista.findViewById(R.id.nombreProductoCarrito); // Inicializar nombre
            tvPrecio = nuevaVista.findViewById(R.id.precioProductoTienda); // Inicializar precio
            btnMinus = nuevaVista.findViewById(R.id.btnMinus); // Inicializar botón "-"
            btnPlus = nuevaVista.findViewById(R.id.btnPlus); // Inicializar botón "+"
            tvQuantity = nuevaVista.findViewById(R.id.tvQuantity); // Inicializar TextView de cantidad
        }
    }
}
