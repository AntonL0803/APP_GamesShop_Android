package com.example.aplicacion.Interfaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aplicacion.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Perfil#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Perfil extends Fragment {
    private static final int REQUEST_PERMISSION = 1;
    private static final int CAMERA_REQUEST_CODE = 100;

    private TextView textViewUsuarioPerfil, textViewEmail;
    private EditText editTextDirecionPerfil;
    private MaterialSwitch newsletterPerfil;
    private Button cerrarSesion, modificarDatos, btnGuardar;
    private ImageView imageViewPerfil;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser usuarioActual;
    private GoogleSignInClient googleSignInClient;
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase db;
    private ActivityResultLauncher<Intent> cameraLauncher;

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
        firebaseAuth = FirebaseAuth.getInstance();
        usuarioActual = firebaseAuth.getCurrentUser();

        //Launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            if (photo != null) {
                                imageViewPerfil.setImageBitmap(photo);
                                subirImagenAFirebase(photo);
                            } else {
                                Toast.makeText(getActivity(), "No se ha capturado ninguna imagen", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("PerfilFragment", "Intent data es null");
                        }
                    } else {
                        Log.e("PerfilFragment", "Error: resultCode incorrecto");
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil_fragmento, container, false);

        // Vincular elementos UI con IDs
        textViewUsuarioPerfil = rootView.findViewById(R.id.etNombrePerfil);
        textViewEmail = rootView.findViewById(R.id.tvEmailAddressPerfil);
        editTextDirecionPerfil = rootView.findViewById(R.id.etDireccionPerfil);
        newsletterPerfil = rootView.findViewById(R.id.switchNewsPerfil);
        cerrarSesion = rootView.findViewById(R.id.btcerrarSesionPerfil);
        imageViewPerfil = rootView.findViewById(R.id.imagenPerfil);
        modificarDatos = rootView.findViewById(R.id.btmodificarPerfil);
        btnGuardar = rootView.findViewById(R.id.btGuardarPerfil);
        btnGuardar.setVisibility(View.GONE);

        editTextDirecionPerfil.setEnabled(false);
        textViewUsuarioPerfil.setEnabled(false);
        newsletterPerfil.setEnabled(false);

        //  Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        usuarioActual = firebaseAuth.getCurrentUser();
        sharedPreferences = getActivity().getSharedPreferences("PerfilUsuario", Context.MODE_PRIVATE);

        // Verificar si el usuario está autenticado
        if (usuarioActual != null) {
            cargarDatosUsuario();
        } else {
            Toast.makeText(getActivity(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }

        // Cerrar Sesión
        cerrarSesion.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "Cerrando sesión...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), Login.class));
            firebaseAuth.signOut();
            Toast.makeText(getActivity(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });

        modificarDatos.setOnClickListener(View -> {
            editTextDirecionPerfil.setEnabled(true);
            textViewUsuarioPerfil.setEnabled(true);
            newsletterPerfil.setEnabled(true);

            modificarDatos.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.VISIBLE);
        });

        btnGuardar.setOnClickListener(view -> {
            configurarEventosDeCambio();
            editTextDirecionPerfil.setEnabled(false);
            textViewUsuarioPerfil.setEnabled(false);
            newsletterPerfil.setEnabled(false);

            btnGuardar.setVisibility(View.GONE);
            modificarDatos.setVisibility(View.VISIBLE);
        });

        imageViewPerfil.setOnClickListener(v -> {
            //Intentamos abrir la cámara
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                abrirCamera();
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.CAMERA}, REQUEST_PERMISSION);
                System.out.println("No se puede abrir la camara, comprueba los permisos.");
            }
        });

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_perfil_fragmento, container, false);
        return rootView;
    }

    // Cargar datos desde Firebase Realtime Database
    private void cargarDatosUsuario() {
        db = FirebaseDatabase.getInstance("https://gameshopandroid-cf6f2-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference usuariosReferencia = db.getReference().child("Usuarios");

        if (usuarioActual != null) {
            String emailUser = usuarioActual.getEmail();
            if (emailUser != null) {
                String usuarioClave = emailUser.replace("@", "_").replace(".", "_");
                DatabaseReference usuarioReferenciado = usuariosReferencia.child(usuarioClave);// Referencia correcta al usuario

                usuarioReferenciado.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Obtener valores de Firebase
                            String nombre = snapshot.child("nombre").getValue(String.class);
                            String email = snapshot.child("email").getValue(String.class);
                            String direccion = snapshot.child("direccion").getValue(String.class);
                            Boolean newsletter = snapshot.child("newsletter").getValue(Boolean.class);

                            if (newsletter == null) {
                                newsletter = false;  // Si es null, asignamos un valor por defecto
                            }

                            // Verificar si hay una imagen en Base64 subida por el usuario
                            if (snapshot.child("photoBase64").exists()) {
                                String imagenBase64 = snapshot.child("photoBase64").getValue(String.class);
                                if (imagenBase64 != null && !imagenBase64.isEmpty()) {
                                    Log.d("PerfilFragment", "Base64 recuperada: " + imagenBase64);
                                    Bitmap imagenDecodificada = convertirBase64AImagen(imagenBase64);
                                    imageViewPerfil.setImageBitmap(imagenDecodificada);
                                } else {
                                    Log.e("PerfilFragment", "Imagen en Base64 está vacía o nula");
                                    comprobarImagenGoogle();
                                }
                            } else {
                                comprobarImagenGoogle();
                            }

                            // Asignar valores a los elementos UI
                            textViewUsuarioPerfil.setText(nombre != null ? nombre : "Nombre no disponible");
                            textViewEmail.setText(email != null ? email : "Email no disponible");
                            editTextDirecionPerfil.setText(direccion != null ? direccion : "");
                            newsletterPerfil.setChecked(newsletter);

                        } else {
                            Toast.makeText(getActivity(), "No se encontraron datos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error al obtener datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void configurarEventosDeCambio() {
        try{
            //Esto actualiza el firebase
            String emailUser = usuarioActual.getEmail();
            DatabaseReference usuario = db.getReference().child("Usuarios")
                    .child(emailUser.replace("@", "_").replace(".", "_"));
            usuario.child("direccion").setValue(editTextDirecionPerfil.getText().toString());
            usuario.child("nombre").setValue(textViewUsuarioPerfil.getText().toString());
            usuario.child("newsletter").setValue(newsletterPerfil.isChecked());
            Toast.makeText(getActivity(), "Perfil actualizado.", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            Toast.makeText(getActivity(), "No se ha realizado ningún cambio.", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }

    private void abrirCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            Log.d("PerfilFragment", "Cámara encontrada, abriendo...");
            cameraLauncher.launch(cameraIntent);
        } else {
            Log.e("PerfilFragment", "No se encontró una aplicación de cámara instalada.");
            Toast.makeText(getActivity(), "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void comprobarImagenGoogle() {
        Uri Urlfoto = usuarioActual.getPhotoUrl();
        if (Urlfoto != null) {
            Glide.with(getContext())
                    .load(Urlfoto)
                    .into(imageViewPerfil);
        } else {
            // Si no hay imagen en Google ni en Base64, poner la predeterminada
            imageViewPerfil.setImageResource(R.drawable.imagendefecto);
        }
    }

    private void subirImagenAFirebase(Bitmap photo) {
        String imagenBase64 = convertirImagenBase64(photo);

        if (usuarioActual != null) {
            String emailUser = usuarioActual.getEmail();
            if (emailUser != null) {
                String usuarioClave = emailUser.replace("@", "_").replace(".", "_");
                DatabaseReference usuarioRef = db.getReference("Usuarios").child(usuarioClave);

                usuarioRef.child("photoBase64").setValue(imagenBase64)
                        .addOnSuccessListener(aVoid -> {
                                Log.d("PerfilFragment", "Imagen guardada correctamente");
                                Toast.makeText(getActivity(), "Imagen guardada", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                                    Log.e("PerfilFragment", "Error al guardar la imagen: " + e.getMessage());
                            Toast.makeText(getActivity(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }

    private String convertirImagenBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap convertirBase64AImagen(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}