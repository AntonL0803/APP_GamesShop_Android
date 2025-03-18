package com.example.aplicacion.Entidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.Interfaces.DetallesPedido;
import com.example.aplicacion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorPedidos extends RecyclerView.Adapter<AdaptadorPedidos.MiViewHolderPedidos> implements View.OnClickListener, Filterable {
    private FirebaseDatabase db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private List<Pedido> pedidos;
    private List<String> codigoPedido;
    private List<Pedido> pedidosFiltrados;
    private Context contexto;

    public AdaptadorPedidos(List<Pedido> pedidos, List<String> codigoPedido, Context contexto) {
        this.pedidos = pedidos;
        this.codigoPedido = codigoPedido;
        pedidosFiltrados = new ArrayList<>(pedidos);
        this.contexto = contexto;
    }

    @NonNull
    @Override
    public MiViewHolderPedidos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View nuevaVista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_pedidos, parent, false);
        return new MiViewHolderPedidos(nuevaVista);
    }

    @Override
    public void onBindViewHolder(@NonNull MiViewHolderPedidos holder, int position) {
        Pedido pedido = pedidosFiltrados.get(position);
        String codigo = codigoPedido.get(position);

        holder.nombre.setText(pedido.getNombre().toString());
        holder.codigo.setText(codigo.toString());
        holder.fecha.setText(pedido.getFecha().toString());

        DecimalFormat formato = new DecimalFormat("#.##");
        holder.precio.setText("Total: " + formato.format(pedido.getTotal()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetallesPedido fragmento = DetallesPedido.newInstance(pedido);
                fragmento.show(((FragmentActivity) contexto).getSupportFragmentManager(), fragmento.getTag());
            }
        });
    }


    @Override
    public int getItemCount() {
        if (pedidosFiltrados != null){
            return pedidosFiltrados.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence busqueda) {
                List<Pedido> listaFiltrada = new ArrayList<>();
                if (busqueda == null || busqueda.length() == 0) {
                    listaFiltrada.addAll(pedidos);
                } else {
                    String filtro = busqueda.toString().toLowerCase();
                    for (int i = 0; i < pedidos.size(); i++) {
                        if (pedidos.get(i).getNombre().contains(filtro) || codigoPedido.get(i).contains(filtro)) {
                            listaFiltrada.add(pedidos.get(i));
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = listaFiltrada;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                pedidosFiltrados.clear();
                pedidosFiltrados.addAll((List<Pedido>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public class MiViewHolderPedidos extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView codigo;
        TextView fecha;
        TextView precio;
        public MiViewHolderPedidos(@NonNull View nuevaVista) {
            super(nuevaVista);
            nombre = nuevaVista.findViewById(R.id.tvNombrePedido);
            codigo = nuevaVista.findViewById(R.id.tvCodigoPedido);
            fecha = nuevaVista.findViewById(R.id.tvFechaPedido);
            precio = nuevaVista.findViewById(R.id.tvPrecioPedido);
        }
    }
}
