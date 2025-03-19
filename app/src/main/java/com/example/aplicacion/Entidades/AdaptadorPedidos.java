package com.example.aplicacion.Entidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

/**
 * Adaptador para manejar los pedidos y permitir su filtrado en un RecyclerView.
 */
public class AdaptadorPedidos extends RecyclerView.Adapter<AdaptadorPedidos.MiViewHolderPedidos> implements View.OnClickListener, Filterable {
    private FirebaseDatabase db; // Instancia de Firebase Database
    private FirebaseAuth mAuth; // Instancia de Firebase Authentication
    private FirebaseUser user; // Usuario actual autenticado en Firebase
    private List<Pedido> pedidos; // Lista de pedidos sin filtrar
    private List<String> codigoPedido; // Lista de códigos de los pedidos
    private List<Pedido> pedidosFiltrados; // Lista de pedidos filtrados para la búsqueda
    private Context contexto; // Contexto de la actividad o fragmento

    /**
     * Constructor del adaptador de pedidos.
     * @param pedidos Lista de pedidos
     * @param codigoPedido Lista de códigos de los pedidos
     * @param contexto Contexto de la actividad
     */
    public AdaptadorPedidos(List<Pedido> pedidos, List<String> codigoPedido, Context contexto) {
        this.pedidos = pedidos;
        this.codigoPedido = codigoPedido;
        pedidosFiltrados = new ArrayList<>(pedidos); // Inicializar lista filtrada con todos los pedidos
        this.contexto = contexto;
    }

    @NonNull
    @Override
    public MiViewHolderPedidos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout de la tarjeta de pedido
        View nuevaVista = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_pedidos, parent, false);
        return new MiViewHolderPedidos(nuevaVista);
    }

    @Override
    public void onBindViewHolder(@NonNull MiViewHolderPedidos holder, int position) {
        // Obtener el pedido y código en la posición actual
        Pedido pedido = pedidosFiltrados.get(position);
        String codigo = codigoPedido.get(position);

        // Establecer los valores en las vistas
        holder.nombre.setText(pedido.getNombre().toString());
        holder.codigo.setText(codigo.toString());
        holder.fecha.setText(pedido.getFecha().toString());

        // Formatear el precio del pedido
        DecimalFormat formato = new DecimalFormat("#.##");
        holder.precio.setText("Total: " + formato.format(pedido.getTotal()));

        // Manejar el click en la tarjeta de pedido para mostrar los detalles
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
        // Devolver el número de elementos en la lista filtrada
        if (pedidosFiltrados != null) {
            return pedidosFiltrados.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onClick(View view) {
        // Método vacío necesario para implementar la interfaz OnClickListener
    }

    @Override
    public Filter getFilter() {
        // Implementación del filtro para la búsqueda de pedidos
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence busqueda) {
                List<Pedido> listaFiltrada = new ArrayList<>();
                // Si no hay texto en la búsqueda, se muestran todos los pedidos
                if (busqueda == null || busqueda.length() == 0) {
                    listaFiltrada.addAll(pedidos);
                } else {
                    String filtro = busqueda.toString().toLowerCase();
                    // Filtrar los pedidos por nombre o código de pedido
                    for (int i = 0; i < pedidos.size(); i++) {
                        if (pedidos.get(i).getNombre().contains(filtro) || codigoPedido.get(i).contains(filtro)) {
                            listaFiltrada.add(pedidos.get(i));
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = listaFiltrada; // Establecer los resultados filtrados
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // Actualizar la lista filtrada y notificar los cambios al adaptador
                pedidosFiltrados.clear();
                pedidosFiltrados.addAll((List<Pedido>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    /**
     * ViewHolder para los elementos del RecyclerView.
     */
    public class MiViewHolderPedidos extends RecyclerView.ViewHolder {
        TextView nombre; // Nombre del pedido
        TextView codigo; // Código del pedido
        TextView fecha; // Fecha del pedido
        TextView precio; // Precio total del pedido

        // Constructor del ViewHolder
        public MiViewHolderPedidos(@NonNull View nuevaVista) {
            super(nuevaVista);
            // Inicializar las vistas
            nombre = nuevaVista.findViewById(R.id.tvNombrePedido);
            codigo = nuevaVista.findViewById(R.id.tvCodigoPedido);
            fecha = nuevaVista.findViewById(R.id.tvFechaPedido);
            precio = nuevaVista.findViewById(R.id.tvPrecioPedido);
        }
    }
}
