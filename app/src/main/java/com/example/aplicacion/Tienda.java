package com.example.aplicacion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Tienda#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tienda extends Fragment {
    private RecyclerView rvTienda;
    private Switch swTienda;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Tienda() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tienda.
     */
    // TODO: Rename and change types and number of parameters
    public static Tienda newInstance(String param1, String param2) {
        Tienda fragment = new Tienda();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);
        rvTienda = view.findViewById(R.id.rvTienda);
        swTienda = view.findViewById(R.id.switch1);

        boolean isGrid = false;
        AdaptadorTienda adaptador = new AdaptadorTienda(isGrid);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);

        rvTienda.setLayoutManager(layoutManager);
        rvTienda.setAdapter(adaptador);

        swTienda.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rvTienda.setLayoutManager(gridLayoutManager);
                adaptador.setGridLayout(true);
            } else {
                rvTienda.setLayoutManager(layoutManager);
                adaptador.setGridLayout(false);
            }
        });
        return view;
    }
}