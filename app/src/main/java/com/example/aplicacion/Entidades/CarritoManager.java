package com.example.aplicacion.Entidades;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CarritoManager {
    private DatabaseReference carritoRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private List<Producto> productos;

    public CarritoManager() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            String emailUser = user.getEmail().replace("@", "_").replace(".", "_");
            carritoRef = FirebaseDatabase.getInstance().getReference()
                    .child("Usuarios").child(emailUser).child("carrito");
        }
        productos = new ArrayList<>();
    }

    public void actualizarCantidadFirebase(String nombreProducto, long cantidad) {
        if (carritoRef != null) {
            carritoRef.child(nombreProducto).child("cantidad").setValue(cantidad);
        } else {
            Log.e("CarritoManager", "carritoRef es nulo (Metodo actualizarCantidadFirebase)");
        }
    }

    public void eliminarProductoFirebase(String nombreProducto) {
        if (carritoRef != null) {
            carritoRef.child(nombreProducto).removeValue();
        } else {
            Log.e("CarritoManager", "carritoRef es nulo (Metodo eliminarProductoFirebase)");
        }
    }
    public void vaciarCarritoFirebase() {
        if (carritoRef != null) {
            carritoRef.removeValue();
        } else {
            Log.e("CarritoManager", "carritoRef es nulo (Metodo vaciarCarritoFirebase)");
        }
    }
}
