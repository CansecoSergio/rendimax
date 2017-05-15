package sergio.upiicsa.rendimax.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sergio.upiicsa.rendimax.LoginActivity;
import sergio.upiicsa.rendimax.R;
import sergio.upiicsa.rendimax.adapters.CustomBancosAdapter;
import sergio.upiicsa.rendimax.adapters.CustomOpcionesAdapter;
import sergio.upiicsa.rendimax.clases.Opciones;
import sergio.upiicsa.rendimax.clases.SitiosWeb;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InicioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleApiClient mGoogleApiClient;

    private View inicioFragment;

    private LinearLayout layoutInicio;
    private TextView usuario;
    private ListView listViewOpciones;
    private ListView listViewSitios;
    private ProgressBar mprogressBarInicio;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
    private DatabaseReference mDatabaseReferenceSitiosWeb = mDatabaseReference.child("sitiosWeb");
    private DatabaseReference mDatabaseReferenceOpciones = mDatabaseReference.child("opciones");

    private List<SitiosWeb> listaSitiosWeb = new ArrayList<SitiosWeb>();
    private List<Opciones> listaOpciones = new ArrayList<Opciones>();

    private static final String TAGINICIO = "TAG-INICIO";
    private OnFragmentInteractionListener mListener;

    // Required empty public constructor
    public InicioFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
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
        // Inflate the layout for this fragment
        inicioFragment = inflater.inflate(R.layout.fragment_inicio, container, false);
        layoutInicio = (LinearLayout) inicioFragment.findViewById(R.id.layout_inicio);
        usuario = (TextView) inicioFragment.findViewById(R.id.nombre_usuario_inicio);
        listViewSitios = (ListView) inicioFragment.findViewById(R.id.list_view_sitios);
        listViewOpciones = (ListView) inicioFragment.findViewById(R.id.list_view_opciones);
        mprogressBarInicio = (ProgressBar) inicioFragment.findViewById(R.id.progressBarInicio);

        mprogressBarInicio.setVisibility(View.VISIBLE);
        layoutInicio.setVisibility(View.GONE);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        //Obtencion de la instancia a la que está conectada la aplicacion
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Oyente que se ejecutara si esta logeado
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    colocaNombreUsuario(firebaseUser);
                } else {
                    irActivityLogin();
                }
            }
        };

        mDatabaseReferenceSitiosWeb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.e("Count " ,""+ dataSnapshot.getChildrenCount());
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    /*String name = (String) messageSnapshot.child("nombre").getValue();
                    String siglas = (String) messageSnapshot.child("siglas").getValue();
                    String url = (String) messageSnapshot.child("url").getValue();
                    String urlFoto = (String) messageSnapshot.child("urlFoto").getValue();*/

                    SitiosWeb sitio = messageSnapshot.getValue(SitiosWeb.class);
                    listaSitiosWeb.add(sitio);
                }

                cargaListSitios(listaSitiosWeb);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReferenceOpciones.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.e("Count opciones" ," "+ dataSnapshot.getChildrenCount());
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    /*String name = (String) messageSnapshot.child("nombre").getValue();
                    String siglas = (String) messageSnapshot.child("siglas").getValue();
                    String url = (String) messageSnapshot.child("url").getValue();
                    String urlFoto = (String) messageSnapshot.child("urlFoto").getValue();*/

                    Opciones opcion = messageSnapshot.getValue(Opciones.class);
                    listaOpciones.add(opcion);
                }

                cargaListOpciones(listaOpciones);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listViewSitios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSeleccionado = listViewSitios.getItemAtPosition(i).toString();

                if (itemSeleccionado != null) {
                    mDatabaseReferenceSitiosWeb.child(itemSeleccionado.toLowerCase())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            SitiosWeb sitio = dataSnapshot.getValue(SitiosWeb.class);

                            if (sitio.getUrl() != null) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sitio.getUrl()));
                                startActivity(browserIntent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), R.string.ulr_no_disponible, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        listViewOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSeleccionado = listViewOpciones.getItemAtPosition(i).toString();
                if (itemSeleccionado.toLowerCase().equals("estadísticas")) {
                    itemSeleccionado = "estadisticas";
                }

                if (itemSeleccionado.toLowerCase().equals("mis proyecciones")) {
                    itemSeleccionado = "proyecciones";
                }

                if (itemSeleccionado != null) {
                    mDatabaseReferenceOpciones.child(itemSeleccionado.toLowerCase()).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Opciones opcion = dataSnapshot.getValue(Opciones.class);

                            if (opcion != null) {
                                abreDialogo(opcion);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), R.string.descripcion_no_disponible, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        return inicioFragment;
    }

    private void abreDialogo(Opciones opcion) {
        Integer icon = null;

        switch(opcion.getNombreOpcion().toLowerCase()) {
            case "inicio":
                icon = R.drawable.ic_home;
                break;
            case "tips":
                icon = R.drawable.ic_tips;
                break;
            case "estadísticas":
                icon = R.drawable.ic_estadisticas;
                break;
            case "mis proyecciones":
                icon = R.drawable.ic_proyecciones;
                break;
            case "perfil":
                icon = R.drawable.ic_perfil;
                break;
            default:
                icon = R.drawable.ic_no_menu;
                break;
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(opcion.getNombreOpcion())
                .setMessage(opcion.getDescripcion())
                .setPositiveButton(R.string.boton_aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setIcon(icon)
                .show();
    }

    private void cargaListOpciones(List<Opciones> listaOpciones) {
        Integer tamanioLista = listaOpciones.size();
        String[] nombres = new String[tamanioLista];
        String[] descripciones = new String[tamanioLista];
        Integer[] imagenes = new Integer[tamanioLista];

        int i = 0;
        for (Opciones opcion: listaOpciones){
            nombres[i] = opcion.getNombreOpcion();
            //Log.e("Nombre opcion", "" + opcion.getNombreOpcion());
            descripciones[i] = opcion.getDescripcion();
            switch(opcion.getNombreOpcion().toLowerCase()){
                case "inicio":
                    imagenes[i] = R.drawable.ic_home;
                    break;
                case "tips":
                    imagenes[i] = R.drawable.ic_tips;
                    break;
                case "estadísticas":
                    imagenes[i] = R.drawable.ic_estadisticas;
                    break;
                case "mis proyecciones":
                    imagenes[i] = R.drawable.ic_proyecciones;
                    break;
                case "perfil":
                    imagenes[i] = R.drawable.ic_perfil;
                    break;
                default:
                    imagenes[i] = R.drawable.ic_no_menu;
                    break;
            }
            i++;
        }

        if (nombres != null && descripciones != null && imagenes != null){
            try {
                CustomOpcionesAdapter opcionesAdapter =
                        new CustomOpcionesAdapter(getActivity(), nombres, descripciones, imagenes);
                listViewOpciones.setAdapter(opcionesAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void cargaListSitios(List<SitiosWeb> listaSitiosWeb) {
        List<String> listaSiglasSitios = new ArrayList<String>();
        if (listaSitiosWeb != null) {
            for (SitiosWeb sitio: listaSitiosWeb) {
                listaSiglasSitios.add(sitio.getSiglas());
            }
        }

        try {
            ArrayAdapter<String> arrayAdapterSitios = new ArrayAdapter<String>(getActivity(), R.layout.lista_sitios_web,
                    R.id.nombre_item_lista_sitios_web, listaSiglasSitios);
            listViewSitios.setAdapter(arrayAdapterSitios);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void colocaNombreUsuario(FirebaseUser firebaseUser) {
        if (firebaseUser.getDisplayName() != null){
            usuario.setText(" " + firebaseUser.getDisplayName().toString());
        }
        else{
            usuario.setText(R.string.sin_texto);
        }
    }

    @Override
    public void onStart() {
        //Manejo manual de la conexión de GoogleAPI CLient
        mGoogleApiClient.connect();
        //Listener de autenticación al iniciar la aplicación
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

        super.onStop();
    }

    private void irActivityLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        mprogressBarInicio.setVisibility(View.GONE);
        layoutInicio.setVisibility(View.VISIBLE);
    }
}
