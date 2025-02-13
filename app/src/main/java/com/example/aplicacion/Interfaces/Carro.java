package com.example.aplicacion.Interfaces;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aplicacion.Entidades.AdaptadorCarrito;
import com.example.aplicacion.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Carro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Carro extends Fragment {
    private RecyclerView rvCarro;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Carro() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Carro.
     */
    // TODO: Rename and change types and number of parameters
    public static Carro newInstance(String param1, String param2) {
        Carro fragment = new Carro();
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
        View view = inflater.inflate(R.layout.fragment_carro, container, false);
        rvCarro = view.findViewById(R.id.rvCarrito);
        AdaptadorCarrito adaptador = new AdaptadorCarrito();
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());

        rvCarro.setLayoutManager(layoutManager);
        rvCarro.setAdapter(adaptador);
        rvCarro.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }
}