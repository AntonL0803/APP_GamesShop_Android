package com.example.aplicacion.Interfaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aplicacion.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private EditText editTextCpPerfil;
    private Switch newsletterPerfil;
    private Button cerrarSesion, modificarDatos;
    private ImageView imageViewPerfil;
    //private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser usuarioActual;
    private GoogleSignInClient googleSignInClient;
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase db;
    private String userId;
    private ImageButton ibTomarFoto;

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

        /*ibTomarFoto = findViewById(R.id.imgbotonTomarFoto);
        ibTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica los permisos antes de abrir la cámara
                if (ContextCompat.checkSelfPermission(Perfil.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(Perfil.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    // Solicita permisos si no se tienen
                    ActivityCompat.requestPermissions(Perfil.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                } else {
                    openCamera();
                }
            }
        });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil_fragmento, container, false);

        // Vincular elementos UI con IDs
        textViewUsuarioPerfil = rootView.findViewById(R.id.etNombrePerfil);
        textViewEmail = rootView.findViewById(R.id.tvEmailAddressPerfil);
        editTextCpPerfil = rootView.findViewById(R.id.etCPPerfil);
        newsletterPerfil = rootView.findViewById(R.id.switchNewsPerfil);
        cerrarSesion = rootView.findViewById(R.id.btcerrarSesionPerfil);
        imageViewPerfil = rootView.findViewById(R.id.imagenPerfil);
        modificarDatos = rootView.findViewById(R.id.btmodificarPerfil);
        ibTomarFoto = rootView.findViewById(R.id.imgbotonTomarFoto);

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
            configurarEventosDeCambio();
        });

        // Tomar foto de perfil
        ibTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            } else {
                openCamera();
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
                String usuarioClave = emailUser.replace("@", "_").replace(".", "_"); // Reemplazamos caracteres no válidos
                DatabaseReference usuarioReferenciado = usuariosReferencia.child(usuarioClave); // Referencia correcta al usuario

                usuarioReferenciado.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Obtener valores de Firebase
                            String nombre = snapshot.child("nombre").getValue(String.class);
                            String email = snapshot.child("email").getValue(String.class);
                            String cp = snapshot.child("cp").getValue(String.class);
                            Boolean newsletter = snapshot.child("newsletter").getValue(Boolean.class);
                            if (newsletter == null) {
                                newsletter = false;  // Si es null, asignamos un valor por defecto
                            }

                            // Asignar valores a los elementos UI
                            textViewUsuarioPerfil.setText(nombre != null ? nombre : "Nombre no disponible");
                            textViewEmail.setText(email != null ? email : "Email no disponible");
                            editTextCpPerfil.setText(cp != null ? cp : "");
                            newsletterPerfil.setChecked(newsletter);

                            // Cargar la imagen de perfil desde la URL de Google
                            Uri photoUrl = usuarioActual.getPhotoUrl();
                            if (photoUrl != null) {
                                Glide.with(getContext())
                                        .load(photoUrl)
                                        .into(imageViewPerfil); // Asegúrate de tener un ImageView para mostrar la foto
                            } else {
                                // Si no tiene foto de perfil, asignamos una predeterminada
                                imageViewPerfil.setImageResource(R.drawable.imagendefecto);
                            }
                           /* // Guardar datos localmente con SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("nombre", nombre);
                            editor.putString("email", email);
                            editor.putString("cp", cp);
                            editor.putBoolean("newsletter", newsletter != null && newsletter);
                            editor.apply();*/
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
            usuario.child("cp").setValue(editTextCpPerfil.getText().toString());
            usuario.child("nombre").setValue(textViewUsuarioPerfil.getText().toString());
            usuario.child("newsletter").setValue(newsletterPerfil.isChecked());
            Toast.makeText(getActivity(), "Perfil actualizado.", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            Toast.makeText(getActivity(), "No se ha realizado ningún cambio.", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                imageViewPerfil.setImageBitmap(photo);
                subirImagenAFirebase(photo);
            } else {
                Toast.makeText(getActivity(), "No se ha capturado ninguna imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getActivity(), "Permisos necesarios no otorgados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Subir la imagen al Firebase Storage
    private void subirImagenAFirebase(Bitmap photo) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + usuarioActual.getUid() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profilePicRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                DatabaseReference usuario = db.getReference().child("Usuarios")
                        .child(usuarioActual.getEmail().replace("@", "_").replace(".", "_"));
                usuario.child("photoUrl").setValue(uri.toString());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Error al subir la foto", Toast.LENGTH_SHORT).show();
        });
    }
}