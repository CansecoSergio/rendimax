package sergio.upiicsa.rendimax.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import sergio.upiicsa.rendimax.LoginActivity;
import sergio.upiicsa.rendimax.PrincipalActivity;
import sergio.upiicsa.rendimax.R;
import sergio.upiicsa.rendimax.adapters.CustomBancosAdapter;
import sergio.upiicsa.rendimax.clases.Bancos;
import sergio.upiicsa.rendimax.clases.SitiosWeb;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EstadisticasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EstadisticasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstadisticasFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Date sincronizacionDatos;
    //private DateFormat mxFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private GoogleApiClient mGoogleApiClient;

    private ListView listBancos;

    private LinearLayout layoutEstadisticas;
    private LinearLayout layoutTasaRendimiento;
    private LinearLayout layoutTasaInteres;
    private ProgressBar mProgressBarEstadisticas;
    private View estadisticasFragment;
    private ImageView imgBancoRendimiento;
    private ImageView imgBancoInteres;
    private TextView textTasaRendimiento;
    private TextView textTasaInteres;
    private TextView textNombreBancoRendimiento;
    private TextView textNombreBancoInteres;
    private TextView textFechaHoraDatos;
    private TextView mTextViewBancoRendimiento;
    private TextView mTextViewBancoInteres;
    private TextView mTextViewEnlacesBancos;

    private List<Bancos> listaInstituciones = new ArrayList<Bancos>();

    private BarChart mBarChart;
    private BarData mBarData;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
    private DatabaseReference mDatabaseReferenceBancos = mDatabaseReference.child("bancos");

    private static final String TAGPERFIL = "TAG-ESTADISTICAS";
    private OnFragmentInteractionListener mListener;

    public EstadisticasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EstadisticasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EstadisticasFragment newInstance(String param1, String param2) {
        EstadisticasFragment fragment = new EstadisticasFragment();
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
        estadisticasFragment = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        layoutEstadisticas = (LinearLayout) estadisticasFragment.findViewById(R.id.layout_estadisticas);
        layoutTasaInteres = (LinearLayout) estadisticasFragment.findViewById(R.id.layout_banco_tasa_interes);
        layoutTasaRendimiento = (LinearLayout) estadisticasFragment.findViewById(R.id.layout_banco_tasa_rendimiento);
        mProgressBarEstadisticas = (ProgressBar) estadisticasFragment.findViewById(R.id.progressBarEstadisticas);
        mBarChart = (BarChart) estadisticasFragment.findViewById(R.id.rendimiento_grafica);
        imgBancoInteres = (ImageView) estadisticasFragment.findViewById(R.id.img_banco_mayor_interes);
        imgBancoRendimiento = (ImageView) estadisticasFragment.findViewById(R.id.img_banco_mayor_rendimiento);
        textTasaInteres = (TextView) estadisticasFragment.findViewById(R.id.text_tasa_interes);
        textTasaRendimiento = (TextView) estadisticasFragment.findViewById(R.id.text_tasa_rendimiento);
        textNombreBancoInteres = (TextView) estadisticasFragment.findViewById(R.id.text_nombre_banco_tasa_interes);
        textNombreBancoRendimiento = (TextView) estadisticasFragment.findViewById(R.id.text_nombre_banco_tasa_rendimiento);
        textFechaHoraDatos = (TextView) estadisticasFragment.findViewById(R.id.fecha_hora_datos_estadisticas);
        mTextViewBancoInteres = (TextView) estadisticasFragment.findViewById(R.id.text_no_cargo_banco_interes);
        mTextViewBancoRendimiento = (TextView) estadisticasFragment.findViewById(R.id.text_no_cargo_banco_rendimiento);
        mTextViewEnlacesBancos = (TextView) estadisticasFragment.findViewById(R.id.text_no_cargo_paginas_bancos);
        listBancos = (ListView) estadisticasFragment.findViewById(R.id.list_view_bancos);

        layoutTasaInteres.setVisibility(View.GONE);
        layoutTasaRendimiento.setVisibility(View.GONE);
        listBancos.setVisibility(View.GONE);

        mTextViewBancoInteres.setVisibility(View.VISIBLE);
        mTextViewBancoRendimiento.setVisibility(View.VISIBLE);
        mTextViewEnlacesBancos.setVisibility(View.VISIBLE);

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
                if (firebaseUser == null) {
                    irActivityLogin();
                }
            }
        };

        mDatabaseReferenceBancos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    Bancos institucion = messageSnapshot.getValue(Bancos.class);
                    listaInstituciones.add(institucion);
                }

                if (listaInstituciones != null) {
                    try {
                        mBarData = new BarData(getEjeXValores(), getDataSet(listaInstituciones));

                        mBarChart.setData(mBarData);
                        mBarChart.setDescription(getActivity().getString(R.string.descripcion_grafica));
                        mBarChart.animateXY(2000, 2000);
                        mBarChart.setClickable(false);
                        mBarChart.invalidate();

                        llenaInformacionBancos(listaInstituciones);
                        cargaListBancos(listaInstituciones);
                        sincronizacionDatos = new Date();
                        textFechaHoraDatos.setText(sincronizacionDatos.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            Toast toast = Toast.makeText(getActivity(),
                                    R.string.error_estadisticas, Toast.LENGTH_SHORT);
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(getActivity(),
                            R.string.error_estadisticas, Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listBancos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                String itemSeleccionado = listBancos.getItemAtPosition(i).toString();

                switch (itemSeleccionado){
                    case "CITIBANAMEX":
                        itemSeleccionado = "banamex";
                        break;
                    case "BBVA BANCOMER":
                        itemSeleccionado = "bancomer";
                        break;
                    case "BANORTE":
                        itemSeleccionado = "banorte";
                        break;
                    default:
                        itemSeleccionado = null;
                        break;
                }

                Log.e("Item seleccionado: ", itemSeleccionado.toLowerCase());

                if (itemSeleccionado != null) {
                    mDatabaseReferenceBancos.child(itemSeleccionado.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Bancos banco = dataSnapshot.getValue(Bancos.class);

                            if (banco.getPaginaWeb() != null) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banco.getPaginaWeb()));
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

        return estadisticasFragment;
    }

    private void cargaListBancos(List<Bancos> listaInstituciones) {
        Integer tamanioLista = listaInstituciones.size();
        String nombreBanco[] = new String[tamanioLista];
        String urlImagenBanco[] = new String[tamanioLista];

        int i = 0;
        for (Bancos banco: listaInstituciones){
            nombreBanco[i] = banco.getSiglas();
            urlImagenBanco[i] = banco.getAppUrl();

            i++;
        }

        if (nombreBanco != null && urlImagenBanco != null){
            try {
                CustomBancosAdapter bancosAdapter = new CustomBancosAdapter(getActivity(), urlImagenBanco, nombreBanco);
                listBancos.setAdapter(bancosAdapter);

                listBancos.setVisibility(View.VISIBLE);
                mTextViewEnlacesBancos.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void llenaInformacionBancos(List<Bancos> listaInstituciones) {
        Float mayorInteres = 0.0f;
        Float mayorRendimiento = 0.0f;

        Bancos bancoMayorInteres = null;
        Bancos bancoMayorRendimiento = null;

        for (Bancos institucion: listaInstituciones) {
            if (Double.valueOf(institucion.getTasaInteres()).floatValue() > mayorInteres) {
                Log.e("Valor Interés " ,""+ institucion.getTasaInteres().toString());
                mayorInteres = Double.valueOf(institucion.getTasaInteres()).floatValue();
                bancoMayorInteres = institucion;
            }

            if (Double.valueOf(institucion.getTasaRendimiento()).floatValue() > mayorRendimiento) {
                mayorRendimiento = Double.valueOf(institucion.getTasaRendimiento()).floatValue();
                bancoMayorRendimiento = institucion;
            }
        }

        if (bancoMayorInteres != null && bancoMayorRendimiento != null) {
            textNombreBancoRendimiento.setText(bancoMayorRendimiento.getNombre().toString() != null  ?
                    "  " + bancoMayorRendimiento.getNombre() : " - " );
            textNombreBancoInteres.setText(bancoMayorInteres.getNombre().toString() != null ?
                    "  " + bancoMayorInteres.getNombre() : " - ");
            textTasaRendimiento.setText(bancoMayorRendimiento.getTasaRendimiento().toString() != null ?
                    "  " + bancoMayorRendimiento.getTasaRendimiento().toString() : " - ");
            textTasaInteres.setText(bancoMayorInteres.getTasaInteres().toString() != null ?
                    "  " + bancoMayorInteres.getTasaInteres().toString() : " - ");

            Glide.with(getActivity())
                    .load(bancoMayorRendimiento.getLogoUrl().toString())
                    .centerCrop()
                    //.fitCenter()
                    .crossFade()
                    //.override(1000, 200)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.5f)
                    .into(imgBancoRendimiento);

            Glide.with(getActivity())
                    .load(bancoMayorInteres.getLogoUrl().toString())
                    .centerCrop()
                    //.fitCenter()
                    .crossFade()
                    //.override(1000, 200)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.5f)
                    .into(imgBancoInteres);

            layoutTasaRendimiento.setVisibility(View.VISIBLE);
            layoutTasaInteres.setVisibility(View.VISIBLE);
            mTextViewBancoRendimiento.setVisibility(View.GONE);
            mTextViewBancoInteres.setVisibility(View.GONE);

        } else {
            textTasaRendimiento.setText("-");
            textTasaInteres.setText("-");
        }
    }

    private ArrayList<String> getEjeXValores() {
        ArrayList<String> xValores = new ArrayList<>();

        xValores.add("Rendimiento");
        xValores.add("Interés");

        return xValores;
    }

    private ArrayList<BarDataSet> getDataSet(List<Bancos> lista) {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSetCitiBanamex = new ArrayList<>();
        ArrayList<BarEntry> valueSetBBVABancomer = new ArrayList<>();
        ArrayList<BarEntry> valueBanorte = new ArrayList<>();
        for (Bancos institucionBancaria: lista) {
            if (institucionBancaria.getSiglas().equals("CITIBANAMEX")) {
                float tasaInteres, tasaRendimiento;

                tasaRendimiento = Double.valueOf(institucionBancaria.getTasaRendimiento()).floatValue();
                tasaInteres = Double.valueOf(institucionBancaria.getTasaInteres()).floatValue();

                Log.e("Clase del numero " ,""+ institucionBancaria.getTasaInteres().getClass().getSimpleName());

                BarEntry vscb1 = new BarEntry(tasaRendimiento, 0); // Rendimiento
                valueSetCitiBanamex.add(vscb1);
                BarEntry vscb2 = new BarEntry(tasaInteres, 1); // Interés
                valueSetCitiBanamex.add(vscb2);
            }

            if (institucionBancaria.getSiglas().equals("BBVA BANCOMER")) {
                float tasaInteres, tasaRendimiento;

                tasaRendimiento = Double.valueOf(institucionBancaria.getTasaRendimiento()).floatValue();
                tasaInteres = Double.valueOf(institucionBancaria.getTasaInteres()).floatValue();

                Log.e("Clase del numero " ,""+ institucionBancaria.getTasaInteres().getClass().getSimpleName());

                BarEntry vsbbva1 = new BarEntry(tasaRendimiento, 0); // Rendimiento
                valueSetBBVABancomer.add(vsbbva1);
                BarEntry vsbbva2 = new BarEntry(tasaInteres, 1); // Interés
                valueSetBBVABancomer.add(vsbbva2);
            }

            if (institucionBancaria.getSiglas().equals("BANORTE")) {
                float tasaInteres, tasaRendimiento;

                tasaRendimiento = Double.valueOf(institucionBancaria.getTasaRendimiento()).floatValue();
                tasaInteres = Double.valueOf(institucionBancaria.getTasaInteres()).floatValue();

                Log.e("Clase del numero " ,""+ institucionBancaria.getTasaInteres().getClass().getSimpleName());

                BarEntry vsb1 = new BarEntry(tasaRendimiento, 0); // Rendimiento
                valueBanorte.add(vsb1);
                BarEntry vsb2 = new BarEntry(tasaInteres, 1); // Interés
                valueBanorte.add(vsb2);
            }
        }

        if (valueSetCitiBanamex != null && valueSetBBVABancomer != null && valueBanorte != null) {
            BarDataSet barDataSet1 = new BarDataSet(valueSetCitiBanamex, "CitiBanamex");
            barDataSet1.setColor(Color.rgb(0,105,166)); //rgb(0,105,166)

            BarDataSet barDataSet2 = new BarDataSet(valueSetBBVABancomer, "BBVA Bancomer");
            barDataSet2.setColor(Color.rgb(0,176,238)); //rgb(0,176,238)

            BarDataSet barDataSet3 = new BarDataSet(valueBanorte, "Banorte");
            barDataSet3.setColor(Color.rgb(235,0,41)); //rgb(235,0,41)

            try {
                dataSets = new ArrayList<>();
                dataSets.add(barDataSet1);
                dataSets.add(barDataSet2);
                dataSets.add(barDataSet3);
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getActivity(),
                        R.string.error_llenar_grafica, Toast.LENGTH_SHORT);
            }
        }

        return dataSets;
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
