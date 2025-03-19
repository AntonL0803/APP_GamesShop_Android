package com.example.aplicacion.Entidades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdaptadorTienda extends RecyclerView.Adapter<AdaptadorTienda.MiViewHolder> implements View.OnClickListener {

    // Mapa que contiene el nombre del producto como clave y su imagen como valor.
    private HashMap<String, Integer> imagenes;
    // Lista de nombres de productos que se mostrarán en el RecyclerView.
    private List<String> nombreProductos;
    // Lista de precios de los productos.
    private List<Double> precioProductos;
    // Listener para los clics en los ítems del RecyclerView.
    private View.OnClickListener listener;
    // Listener para el botón de agregar al carrito.
    private BotonMas listenerBoton;
    // Bandera que indica si la vista será tipo Grid o Linear.
    private boolean isGridLayout;
    // Referencias a Firebase Database, Authentication y al usuario actual.
    private FirebaseDatabase db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    // Constructor que recibe las listas de productos, precios, la bandera del layout, las imágenes y el listener del botón.
    public AdaptadorTienda(List<String> nombreProductos, List<Double> precioProductos, boolean isGridLayout, Map<String, Integer> imagenes, BotonMas listenerBoton) {
        this.nombreProductos = nombreProductos;
        this.precioProductos = precioProductos;
        this.isGridLayout = isGridLayout;
        this.listenerBoton = listenerBoton;
        this.imagenes = new HashMap<>(imagenes); // Copia del mapa de imágenes
    }

    // Método para actualizar el listener del botón de agregar al carrito.
    public void setListenerBoton(BotonMas listenerBoton) {
        this.listenerBoton = listenerBoton;
    }

    // Método para cambiar entre la vista Grid y Linear, y notificar la recarga del RecyclerView.
    public void setGridLayout(boolean isGridLayout) {
        this.isGridLayout = isGridLayout;
        notifyDataSetChanged(); // Recargar el RecyclerView
    }

    // Método para determinar el tipo de vista (Grid o Linear) según la bandera isGridLayout.
    @Override
    public int getItemViewType(int position) {
        return isGridLayout ? 1 : 0; // 1 para Grid, 0 para Linear
    }

    // Método para inflar el layout del ViewHolder según el tipo de vista.
    public MiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View nuevaVista;
        if (viewType == 1) {  // Si es GridLayout, se infla el layout para grilla
            nuevaVista = inflater.inflate(R.layout.tarjeta_producto_grid, parent, false);
        } else {  // Si es LinearLayout, se infla el layout para lista
            nuevaVista = inflater.inflate(R.layout.tarjeta_producto_linear, parent, false);
        }
        nuevaVista.setOnClickListener(listener); // Asignar el listener para clics en el ítem
        return new MiViewHolder(nuevaVista); // Retornar el ViewHolder con el layout inflado
    }

    // Método para bindear los datos del producto a la vista del ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull AdaptadorTienda.MiViewHolder holder, int position) {
        int currentPosition = holder.getBindingAdapterPosition();  // Obtener la posición actual del ítem
        // Establecer la imagen del producto desde el mapa de imágenes usando el nombre del producto
        holder.ivProducto.setImageResource(imagenes.get(nombreProductos.get(position)));
        holder.ivProducto.setTag(imagenes.get(nombreProductos.get(position))); // Asignar la imagen como tag
        // Establecer el nombre y precio del producto en los TextViews
        holder.tvNombre.setText(nombreProductos.get(currentPosition));
        holder.tvPrecio.setText("Precio: " + String.valueOf(precioProductos.get(currentPosition)));

        // Configurar el OnClickListener para el botón de agregar al carrito
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100)  // Reducir tamaño
                        .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100)) // Restaurar tamaño
                        .start();
                listenerBoton.clickListener(currentPosition);  // Llamar al listener del botón
                agregarAlCarrito(currentPosition);  // Agregar el producto al carrito
            }
        });
    }

    // Método para agregar un producto al carrito en la base de datos Firebase.
    public void agregarAlCarrito(int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); // Obtener la instancia de FirebaseAuth
        FirebaseUser user = mAuth.getCurrentUser();  // Obtener el usuario actual
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");  // Instancia de la base de datos Firebase

        // Obtener la referencia a la base de datos de los usuarios
        DatabaseReference usuariosReferencia = db.getReference().child("Usuarios");
        String emailUser = user.getEmail();  // Obtener el email del usuario
        // Reemplazar los caracteres especiales en el email para usarlo como clave en Firebase
        DatabaseReference emailCarritoReferencia = usuariosReferencia.child(emailUser.replace("@", "_").replace(".", "_")).child("carrito");

        // Obtener el producto y precio seleccionados
        String productoSeleccionado = nombreProductos.get(position);
        Double precioSeleccionado = precioProductos.get(position);

        // Verificar si el carrito ya existe para el usuario y agregar el producto
        emailCarritoReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child(productoSeleccionado).exists()) {  // Si el producto ya está en el carrito
                        Integer cantidadActual = snapshot.child(productoSeleccionado).child("cantidad").getValue(Integer.class);  // Obtener la cantidad actual
                        if (cantidadActual == null) {
                            cantidadActual = 0;  // Si la cantidad es nula, se inicializa en 0
                        }
                        emailCarritoReferencia.child(productoSeleccionado).child("cantidad").setValue(cantidadActual + 1);  // Incrementar la cantidad
                    } else {  // Si el producto no está en el carrito
                        Producto nuevoProducto = new Producto(productoSeleccionado, precioSeleccionado, 1);  // Crear un nuevo producto
                        emailCarritoReferencia.child(productoSeleccionado).setValue(nuevoProducto);  // Agregar el nuevo producto al carrito
                    }
                } else {  // Si el carrito no existe aún
                    Producto nuevoProducto = new Producto(productoSeleccionado, precioSeleccionado, 1);  // Crear un nuevo producto
                    emailCarritoReferencia.child(productoSeleccionado).setValue(nuevoProducto);  // Agregar el producto al carrito
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores si ocurre alguna cancelación en la base de datos.
            }
        });
    }

    // Método para obtener la cantidad de productos en la lista.
    @Override
    public int getItemCount() {
        return nombreProductos.size();  // Retornar el tamaño de la lista de productos
    }

    // Método para configurar el listener de clics en los ítems del RecyclerView.
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        // Este método no se utiliza directamente, ya que el clic se maneja en los ViewHolders.
    }

    // Clase interna ViewHolder que representa cada ítem en el RecyclerView.
    public class MiViewHolder extends RecyclerView.ViewHolder {
        // Referencias a las vistas del layout del ítem.
        ImageView ivProducto;
        TextView tvNombre;
        TextView tvPrecio;
        ImageButton imageButton;

        // Constructor del ViewHolder que asigna las vistas correspondientes.
        public MiViewHolder(@NonNull View nuevaVista) {
            super(nuevaVista);
            ivProducto = nuevaVista.findViewById(R.id.imagenProductoCarrito);
            tvNombre = nuevaVista.findViewById(R.id.nombreProductoTarjeta2);
            tvPrecio = nuevaVista.findViewById(R.id.precioProductoTienda);
            imageButton = nuevaVista.findViewById(R.id.imageButton);
        }
    }
}
