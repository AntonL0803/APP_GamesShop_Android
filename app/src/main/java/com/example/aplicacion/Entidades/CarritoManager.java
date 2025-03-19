package com.example.aplicacion.Entidades;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para manejar las operaciones del carrito de compras del usuario.
 * Permite actualizar la cantidad de un producto, eliminar un producto
 * y vaciar todo el carrito en la base de datos Firebase.
 */
public class CarritoManager {
    private DatabaseReference carritoRef; // Referencia a la base de datos para el carrito del usuario
    private FirebaseUser user; // Usuario actual autenticado
    private FirebaseAuth mAuth; // Autenticación de Firebase
    private List<Producto> productos; // Lista de productos en el carrito

    /**
     * Constructor de la clase CarritoManager.
     * Inicializa la referencia al carrito en Firebase según el email del usuario.
     */
    public CarritoManager() {
        mAuth = FirebaseAuth.getInstance(); // Obtener instancia de autenticación
        user = mAuth.getCurrentUser(); // Obtener el usuario actual autenticado
        // Si el usuario está autenticado, se construye la referencia al carrito
        if (user != null) {
            String emailUser = user.getEmail().replace("@", "_").replace(".", "_");
            // Establece la referencia al carrito del usuario en Firebase
            carritoRef = FirebaseDatabase.getInstance().getReference()
                    .child("Usuarios").child(emailUser).child("carrito");
        }
        productos = new ArrayList<>(); // Inicializa la lista de productos en el carrito
    }

    /**
     * Actualiza la cantidad de un producto en el carrito en Firebase.
     * @param nombreProducto El nombre del producto cuyo stock se desea actualizar.
     * @param cantidad La nueva cantidad del producto.
     */
    public void actualizarCantidadFirebase(String nombreProducto, long cantidad) {
        // Verifica si la referencia al carrito es válida
        if (carritoRef != null) {
            // Actualiza la cantidad del producto en Firebase
            carritoRef.child(nombreProducto).child("cantidad").setValue(cantidad);
        } else {
            // Si la referencia es nula, se logea el error
            Log.e("CarritoManager", "carritoRef es nulo (Metodo actualizarCantidadFirebase)");
        }
    }

    /**
     * Elimina un producto del carrito en Firebase.
     * @param nombreProducto El nombre del producto a eliminar.
     */
    public void eliminarProductoFirebase(String nombreProducto) {
        // Verifica si la referencia al carrito es válida
        if (carritoRef != null) {
            // Elimina el producto del carrito en Firebase
            carritoRef.child(nombreProducto).removeValue();
        } else {
            // Si la referencia es nula, se logea el error
            Log.e("CarritoManager", "carritoRef es nulo (Metodo eliminarProductoFirebase)");
        }
    }

    /**
     * Vacía todo el carrito del usuario en Firebase.
     * Elimina todos los productos del carrito en la base de datos.
     */
    public void vaciarCarritoFirebase() {
        // Verifica si la referencia al carrito es válida
        if (carritoRef != null) {
            // Elimina todos los productos del carrito en Firebase
            carritoRef.removeValue();
        } else {
            // Si la referencia es nula, se logea el error
            Log.e("CarritoManager", "carritoRef es nulo (Metodo vaciarCarritoFirebase)");
        }
    }
}
