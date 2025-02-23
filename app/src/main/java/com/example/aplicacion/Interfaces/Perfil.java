package com.example.aplicacion.Interfaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.aplicacion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Perfil#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Perfil extends Fragment {
    private TextView textViewUsuarioPerfil;
    private EditText editTextCpPerfil;
    private EditText editTextEmail;
    private Switch newsletterPerfil;
    private Button cerrarSesion;
    private ImageView imageViewPerfil;
    //private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser usuarioActual;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil_fragmento, container, false);

        // Vincular elementos UI con IDs
        textViewUsuarioPerfil = rootView.findViewById(R.id.etNombrePerfil);
        editTextEmail = rootView.findViewById(R.id.etEmailAddressPerfil);
        editTextCpPerfil = rootView.findViewById(R.id.etCPPerfil);
        newsletterPerfil = rootView.findViewById(R.id.switchNewsPerfil);
        cerrarSesion = rootView.findViewById(R.id.cerrarSesionPerfil);
        imageViewPerfil = rootView.findViewById(R.id.imageView2);

        //  Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(editTextEmail.getText().toString());

        //Recuper datos del Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtener valores de Firebase
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String cp = snapshot.child("cp").getValue(String.class);
                    Boolean newsletter = snapshot.child("newsletter").getValue(Boolean.class);

                    // Asignar valores a los elementos UI
                    textViewUsuarioPerfil.setText(nombre);
                    editTextEmail.setText(email);
                    editTextCpPerfil.setText(cp);
                    newsletterPerfil.setChecked(newsletter != null && newsletter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error al cargarlos datos", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción para cerrar sesión (Ejemplo)
        cerrarSesion.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "Cerrando sesión...", Toast.LENGTH_SHORT).show();
            // Aquí puedes agregar lógica para cerrar sesión
        });

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_perfil_fragmento, container, false);
        return rootView;
    }

}