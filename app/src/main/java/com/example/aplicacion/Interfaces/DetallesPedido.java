package com.example.aplicacion.Interfaces;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aplicacion.Entidades.AdaptadorDetallesPedido;
import com.example.aplicacion.Entidades.Pedido;
import com.example.aplicacion.Entidades.Producto;
import com.example.aplicacion.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragmento para mostrar los detalles de un pedido en un BottomSheet.
 */
public class DetallesPedido extends BottomSheetDialogFragment {

    private Pedido pedido; // Pedido que se va a mostrar en los detalles
    private RecyclerView rvPedidoDetallado; // RecyclerView para mostrar los productos del pedido

    // Mapa de nombres de productos a sus imágenes correspondientes
    private Map<String, Integer> imagenes = new HashMap<String, Integer>() {{
        put("Super Mario Bros Wonder", R.drawable.supermariobroswonder);
        put("Biomutant", R.drawable.biomutant);
        put("Crash", R.drawable.crash);
        put("Donkey Kong Country", R.drawable.donkeykongcountry);
        put("Detective Pikachu", R.drawable.detectivepikachu);
        put("Dragones III", R.drawable.dragones3);
        put("Matching Driving Adventures", R.drawable.drivingadventures);
        put("Everybody Switch", R.drawable.everybodyswitch);
        put("Fitness Boxing", R.drawable.fitnessboxing);
        put("Harvestella", R.drawable.harvestella);
        put("Hollow Knight", R.drawable.hollowknight);
        put("Just Dance", R.drawable.justdance);
        put("Kirby y la tierra olvidada", R.drawable.kirby);
        put("Mario VS Donkey Kong", R.drawable.mariodockerkong);
        put("Mario Party Jamboree", R.drawable.mariopartyjamboree);
        put("Mario Party Superstars", R.drawable.mariopartysuperstars);
        put("Minecraft", R.drawable.minecraft);
        put("Monster Hunter Rise", R.drawable.monsterhunterrise);
        put("mySims Cozy Bundle", R.drawable.mysims);
        put("Princess Peach Showtime", R.drawable.peach);
        put("Pokemon Diamante Brillante", R.drawable.pokemondiamante);
        put("Two Point Campus", R.drawable.twopointcampus);
        put("Zelda tears of the kingdom", R.drawable.zeldakingdom);
        put("Zelda Links Awakening", R.drawable.zeldalink);
    }};

    // Constructor vacío requerido
    public DetallesPedido() {
    }

    /**
     * Método estático para crear una nueva instancia del fragmento con un pedido.
     * @param pedido Pedido que se desea mostrar
     * @return Nueva instancia de DetallesPedido
     */
    public static DetallesPedido newInstance(Pedido pedido) {
        DetallesPedido fragment = new DetallesPedido();
        Bundle args = new Bundle();
        args.putSerializable("pedido", (Serializable) pedido);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener el pedido que se pasa como argumento
        if (getArguments() != null) {
            pedido = (Pedido) getArguments().getSerializable("pedido");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_detalles_pedido, container, false);
        this.pedido = (Pedido) getArguments().getSerializable("pedido");

        // Obtener la lista de productos del pedido
        List<Producto> productos = pedido.getProductos();
        if (productos != null) {
            Log.d("Pedido Detallado Productos", "La lista de productos tiene cosas dentro");
        }

        // Configurar el RecyclerView para mostrar los productos detallados del pedido
        rvPedidoDetallado = view.findViewById(R.id.rvPedidoDetallado);
        AdaptadorDetallesPedido adaptador = new AdaptadorDetallesPedido(productos, imagenes);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        rvPedidoDetallado.setAdapter(adaptador);
        rvPedidoDetallado.setLayoutManager(layout);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Modificar la altura del BottomSheet al iniciar el fragmento
        BottomSheetDialog vista = (BottomSheetDialog) getDialog();
        if (vista != null) {
            View view = vista.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (view != null) {
                BottomSheetBehavior<View> comportamiento = BottomSheetBehavior.from(view);

                // Obtener la altura de la pantalla y calcular la altura deseada del BottomSheet
                int alturaPantalla = getResources().getDisplayMetrics().heightPixels;
                int alturaDeseada = (int) (alturaPantalla * 0.8);

                // Establecer la altura y el comportamiento del BottomSheet
                view.getLayoutParams().height = alturaDeseada;
                view.requestLayout();
                comportamiento.setPeekHeight(alturaDeseada);
                comportamiento.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }
}
