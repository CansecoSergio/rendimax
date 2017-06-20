package sergio.upiicsa.rendimax.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.List;

import sergio.upiicsa.rendimax.DetalleProyeccionActivity;
import sergio.upiicsa.rendimax.DetalleTipActivity;
import sergio.upiicsa.rendimax.LoginActivity;
import sergio.upiicsa.rendimax.ProyeccionFormActivity;
import sergio.upiicsa.rendimax.R;
import sergio.upiicsa.rendimax.adapters.CustomProyeccionesAdapter;
import sergio.upiicsa.rendimax.adapters.CustomTipsAdapter;
import sergio.upiicsa.rendimax.clases.Proyecciones;
import sergio.upiicsa.rendimax.clases.Tips;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProyeccionesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProyeccionesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProyeccionesFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAGPERFIL = "TAG-PROYECCIONES";

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
    private DatabaseReference mDatabaseReferenceProyecciones = mDatabaseReference.child("proyecciones");

    private View proyeccionesFragment;
    private LinearLayout mLinearLayoutListView;

    //private LinearLayout layoutListaProyecciones;
    private Button mButtonAgregarProyeccion;
    private TextView mTextViewSinProyeccion;
    private ListView mListViewProyecciones;
    private ProgressBar mProgressBarProyecciones;

    private OnFragmentInteractionListener mListener;

    public ProyeccionesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProyeccionesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProyeccionesFragment newInstance(String param1, String param2) {
        ProyeccionesFragment fragment = new ProyeccionesFragment();
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
        proyeccionesFragment = inflater.inflate(R.layout.fragment_proyecciones, container, false);
        mButtonAgregarProyeccion = (Button) proyeccionesFragment.findViewById(R.id.agregar_nueva_proyeccion);
        mLinearLayoutListView = (LinearLayout) proyeccionesFragment.findViewById(R.id.layout_list_proyecciones);
        mTextViewSinProyeccion = (TextView) proyeccionesFragment.findViewById(R.id.texto_sin_proyecciones);
        mListViewProyecciones = (ListView) proyeccionesFragment.findViewById(R.id.list_view_proyecciones);
        mProgressBarProyecciones = (ProgressBar) proyeccionesFragment.findViewById(R.id.progressBarProyecciones);

        mLinearLayoutListView.setVisibility(View.GONE);
        mProgressBarProyecciones.setVisibility(View.VISIBLE);

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
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    irActivityLogin();
                } else {
                    cargaListaProyecciones(firebaseUser);
                }
            }
        };

        mButtonAgregarProyeccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProyeccionFormActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP /*| Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK*/);
                startActivity(intent);
            }
        });

        mListViewProyecciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String itemSeleccionado = mListViewProyecciones.getItemAtPosition(i).toString();

                Log.e("Tip:" ," "+ itemSeleccionado);

                if (itemSeleccionado != null) {
                    Intent intent = new Intent(getActivity(), DetalleProyeccionActivity.class);
                    intent.putExtra("NOMBRE_PROYECCION", itemSeleccionado);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP/* | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK*/);
                    startActivity(intent);
                }
            }
        });

        return proyeccionesFragment;
    }

    private void cargaListaProyecciones(FirebaseUser firebaseUser) {
        String email = firebaseUser.getEmail().toString();
        final List<Proyecciones> listaProyecciones = new ArrayList<Proyecciones>();

        mDatabaseReferenceProyecciones.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                                Proyecciones proyeccion = messageSnapshot.getValue(Proyecciones.class);
                                Log.e("Fecha: ", proyeccion.getFechaProyeccion().toString());
                                listaProyecciones.add(proyeccion);

                            }
                            Integer tamanioLista = listaProyecciones.size();

                            if (tamanioLista > 0 && tamanioLista != null) {
                                cargaListViewProyecciones(listaProyecciones);
                            } else {
                                mLinearLayoutListView.setVisibility(View.VISIBLE);
                                mProgressBarProyecciones.setVisibility(View.GONE);
                                mTextViewSinProyeccion.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void cargaListViewProyecciones(List<Proyecciones> listaProyecciones) {
        String[] nombresProyecciones = new String[listaProyecciones.size()];
        String[] fechasProyecciones = new String[listaProyecciones.size()];

        int i = 0;
        for (Proyecciones p: listaProyecciones) {
            nombresProyecciones[i] = p.getNombre().toString();
            fechasProyecciones[i] = p.getFechaProyeccion().toString();

            i++;
        }

        if (nombresProyecciones != null && fechasProyecciones != null) {
            try{
                CustomProyeccionesAdapter customProyeccionesAdapter = new CustomProyeccionesAdapter(getActivity(), nombresProyecciones, fechasProyecciones);
                mListViewProyecciones.setAdapter(customProyeccionesAdapter);

                mLinearLayoutListView.setVisibility(View.VISIBLE);
                mProgressBarProyecciones.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
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
}
