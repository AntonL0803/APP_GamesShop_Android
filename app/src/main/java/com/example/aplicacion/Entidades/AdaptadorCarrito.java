package com.example.aplicacion.Entidades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.R;

import org.w3c.dom.Text;

import java.util.List;

public class AdaptadorCarrito extends RecyclerView.Adapter<AdaptadorCarrito.MiViewHolderCarrito> implements View.OnClickListener {
    private List<Producto> productos;
    private View.OnClickListener listener;
    @Override
    public void onClick(View view) {

    }

    @NonNull
    @Override
    public MiViewHolderCarrito onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View nuevaVista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_producto_carrito, parent, false);
        nuevaVista.setOnClickListener(listener);
        return new MiViewHolderCarrito(nuevaVista);
    }

    @Override
    public void onBindViewHolder(@NonNull MiViewHolderCarrito holder, int position) {
        holder.tvNombre.setText("Nombre del producto");
        holder.tvPrecio.setText("Precio: 0.00$");
        holder.ivProducto.setImageResource(R.drawable.perfil);
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    public class MiViewHolderCarrito extends RecyclerView.ViewHolder {
        ImageView ivProducto;
        TextView tvNombre;
        TextView tvPrecio;
        TextView cantidad;
        Button btnMinus;
        Button btnPlus;
        TextView tvQuantity;

        public MiViewHolderCarrito(@NonNull View nuevaVista) {
            super(nuevaVista);
            cantidad = nuevaVista.findViewById(R.id.cantidad);
            ivProducto = nuevaVista.findViewById(R.id.imagenProductoCarrito);
            tvNombre = nuevaVista.findViewById(R.id.nombreProductoCarrito);
            tvPrecio =  nuevaVista.findViewById(R.id.precioProductoCarrito);
            btnMinus = nuevaVista.findViewById(R.id.btnMinus);
            btnPlus = nuevaVista.findViewById(R.id.btnPlus);
            tvQuantity = nuevaVista.findViewById(R.id.tvQuantity);
        }
    }
}
