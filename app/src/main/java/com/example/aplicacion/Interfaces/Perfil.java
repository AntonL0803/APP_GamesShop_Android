package com.example.aplicacion.Interfaces;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.aplicacion.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Perfil#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Perfil extends Fragment {
    private EditText editTextUsuarioPerfil;
    private EditText editTextCpPerfil;
    private EditText editTextEmail;
    private ToggleButton togglePerfil;

    private TextView tvEmail, tvCP;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Perfil() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragmento.
     */
    // TODO: Rename and change types and number of parameters
    public static Perfil newInstance(String param1, String param2) {
        Perfil fragment = new Perfil();
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
        // Vinculamos los TextViews de la interfaz
        editTextUsuarioPerfil = getActivity().findViewById(R.id.etNombrePerfil);
        tvEmail = getActivity().findViewById(R.id.etEmailAddressPerfil);
        tvCP = getActivity().findViewById(R.id.etCPPerfil);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil_fragmento, container, false);

        //Recuperamos los datos del intent que se ha pasado desde el registro
        Intent intent = getActivity().getIntent();
        String nombre = intent.getStringExtra("Nombre");
        String email = intent.getStringExtra("Email");
        String cp = intent.getStringExtra("CP");

        // Set the values to the TextViews or EditTexts
        if (tvEmail != null) tvEmail.setText(email);
        if (tvCP != null) tvCP.setText(cp);
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_perfil_fragmento, container, false);
        return rootView;
    }

}