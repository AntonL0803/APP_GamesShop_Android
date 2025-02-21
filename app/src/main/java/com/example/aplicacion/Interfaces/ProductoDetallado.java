package com.example.aplicacion.Interfaces;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.aplicacion.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductoDetallado#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductoDetallado extends Fragment {
    private ImageButton imageButton;
    private TextView titulo;
    private TextView precio;
    private TextView descripcion;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductoDetallado() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProductoDetallado.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductoDetallado newInstance(String nombre, String precio) {
        ProductoDetallado fragment = new ProductoDetallado();
        Bundle args = new Bundle();
        args.putString("nombre", nombre);
        args.putString("precio", precio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producto_detallado, container, false);
        imageButton = view.findViewById(R.id.ibflechaProductoDetallado);
        titulo = view.findViewById(R.id.tituloProductoDetallado);
        precio = view.findViewById(R.id.tvPrecioProductoDetallado);
        descripcion = view.findViewById(R.id.descripcionProductoDetallado);

        if (getArguments() != null) {
            String nombre = getArguments().getString("nombre");
            String precio = getArguments().getString("precio");

            // Actualiza las vistas con los datos
            titulo.setText(nombre);
            this.precio.setText(String.valueOf(precio));
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
                requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void cargarDatos(String titulo){

    }
}