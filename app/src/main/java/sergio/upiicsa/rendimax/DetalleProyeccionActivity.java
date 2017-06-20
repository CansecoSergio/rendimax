package sergio.upiicsa.rendimax;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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

import sergio.upiicsa.rendimax.clases.Bancos;
import sergio.upiicsa.rendimax.clases.Proyecciones;
import sergio.upiicsa.rendimax.clases.Tips;

public class DetalleProyeccionActivity extends AppCompatActivity {

    private static final String TAGACTDETALLE = "ACTIVITY-DETALLE";

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
    private DatabaseReference mDatabaseReferenceBancos = mDatabaseReference.child("bancos");
    private DatabaseReference mDatabaseReferenceProyecciones = mDatabaseReference.child("proyecciones");

    private LinearLayout mLinearLayoutDetalleProyecciones;
    private ProgressBar mProgressBarDetalleProyecciones;

    private Toolbar mToolbar;
    private TextView mToolbarText;
    private String extraNombreProyeccion;

    private TextView mTextViewBanco;
    private TextView mTextViewInvInicial;
    private TextView mTextViewInvAnual;
    private TextView mTextViewAnios;
    private TextView mTextViewTasaRendimiento;
    private TextView mTextViewTotalInversion;
    private TextView mTextViewTotalRendimiento;
    private TextView mTextViewTotalInverionGrafica;

    private LineChart mLineChartRendimiento;
    private LineChart mLineChartInversion;

    private Proyecciones mProyeccionesGeneral;
    private Bancos mBancosGeneral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_proyeccion);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_detalle_proyeccion);
        mToolbarText = (TextView) findViewById(R.id.titulo_toolbar_detalleProyeccion);
        mTextViewBanco = (TextView) findViewById(R.id.text_banco_detalle_proyeccion);
        mTextViewInvInicial = (TextView) findViewById(R.id.text_inv_inicial_detalle_proyeccion);
        mTextViewInvAnual = (TextView) findViewById(R.id.text_inv_anual_detalle_proyeccion);
        mTextViewAnios = (TextView) findViewById(R.id.text_inv_anios_detalle_proyeccion);
        mTextViewTasaRendimiento = (TextView) findViewById(R.id.text_tasa_banco_detalle_proyeccion);
        mTextViewTotalInversion = (TextView) findViewById(R.id.text_total_inversion_detalle_proyeccion);
        mTextViewTotalRendimiento = (TextView) findViewById(R.id.text_total_rendimiento_detalle_proyeccion);
        mTextViewTotalInverionGrafica = (TextView) findViewById(R.id.text_total_grafica_rendimiento_detalle_proyeccion);
        mLinearLayoutDetalleProyecciones = (LinearLayout) findViewById(R.id.layout_detalle_proyeccion);
        mProgressBarDetalleProyecciones = (ProgressBar) findViewById(R.id.progressBarDetalleProyeccion);
        mLineChartRendimiento = (LineChart) findViewById(R.id.proyeccion_grafica_rendimiento);
        mLineChartInversion = (LineChart) findViewById(R.id.proyeccion_grafica_inversion);

        mLinearLayoutDetalleProyecciones.setVisibility(View.GONE);
        mProgressBarDetalleProyecciones.setVisibility(View.VISIBLE);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                extraNombreProyeccion = extras.getString("NOMBRE_PROYECCION");
            }
        } else{
            extraNombreProyeccion = (String) savedInstanceState.getSerializable("NOMBRE_PROYECCION");
        }

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
                    if (extraNombreProyeccion != null && extraNombreProyeccion.length() > 0) {
                        obtieneInformacion();
                    }
                }
            }
        };
    }

    private void obtieneInformacion() {
        mToolbarText.setText(extraNombreProyeccion);

        String llave = extraNombreProyeccion + "_" + firebaseUser.getEmail().toString();

        mDatabaseReferenceProyecciones.orderByChild("llave").equalTo(llave)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            Proyecciones proyeccion = null;
                            for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                                 proyeccion = messageSnapshot.getValue(Proyecciones.class);
                            }
                            if (proyeccion != null) {
                                cargaDetalleDelProyeccion(proyeccion);
                                mProyeccionesGeneral = proyeccion;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void cargaDetalleDelProyeccion(final Proyecciones proyeccion) {
        final String banco = proyeccion.getBanco().toString();

        Log.e("Nombre banco: ", banco);

        mDatabaseReferenceBancos.orderByChild("nombre").equalTo(banco).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            Bancos bancos = null;
                            for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                                if (messageSnapshot != null) {
                                    bancos = messageSnapshot.getValue(Bancos.class);
                                }
                            }
                            if (bancos != null) {
                                Log.e("Banco: ", bancos.getNombre().toString());
                                mBancosGeneral = bancos;
                                llenaInformacionGraficas(proyeccion, bancos);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void llenaInformacionGraficas(Proyecciones proyeccion, Bancos bancos) {
        Integer anios = proyeccion.getAnios();
        Double inversionAnual = proyeccion.getCatidadAnual() + 0.0;
        Double inversionInicial = proyeccion.getCantidadInicial() + 0.0;
        Double tasaRendimientoBanco = bancos.getTasaRendimiento() + 0.0;

        float floatInversionAnual = inversionAnual.floatValue();
        float floatInversionInicial = inversionInicial.floatValue();
        float floatRendimientoBanco = tasaRendimientoBanco.floatValue();

        ArrayList<Entry> entriesGraficaInversion = new ArrayList<>();
        ArrayList<Entry> entriesGraficaRendimiento = new ArrayList<>();

        float inicial = floatInversionInicial;
        float anualidad = floatInversionAnual;
        float inversion = 0.0f + inicial;
        float rendimiento = 0.0f + inicial;
        float tasaBanco = 0.0f + floatRendimientoBanco;
        float anteriorRendimiento = 0.0f;

        if (anios != null) {
            for (int i = 0; i <= anios; i++) {
                if (i != 0) {
                    inversion += anualidad;

                    rendimiento = (rendimiento + anualidad) + ((rendimiento + anualidad) * (tasaBanco));
                }

                Log.e("Rendimiento: ", i + " " + rendimiento + "");

                entriesGraficaInversion.add(new Entry(inversion, i));
                entriesGraficaRendimiento.add(new Entry(rendimiento, i));
            }
        }

        try {
            LineDataSet datasetAportacion = new LineDataSet(entriesGraficaInversion, "Incremento de la aportación anual");
            datasetAportacion.setColors(new int[]{R.color.colorAccent}, getApplicationContext());
            datasetAportacion.setDrawFilled(true);
            datasetAportacion.setFillColor(getApplicationContext().getResources().getColor(R.color.colorAccent));

            LineDataSet datasetRendimiento = new LineDataSet(entriesGraficaRendimiento, "Incremento de la aportación más el rendimiento anual");
            datasetRendimiento.setColors(new int[]{R.color.colorPrimary}, getApplicationContext());
            datasetRendimiento.setDrawFilled(true);
            datasetRendimiento.setFillColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));

            datasetAportacion.setDrawCubic(true);
            datasetRendimiento.setDrawCubic(true);

            ArrayList<String> labels = new ArrayList<String>();
            for (Integer i = 0;  i <= anios; i++){
                labels.add(i.toString());
            }
            Log.e("Labels", labels.size() + "");
            Log.e("Aportacion", entriesGraficaInversion.size() + "");
            Log.e("Rendimiento", entriesGraficaRendimiento.size() + "");

            LineData dataAportacion = new LineData(labels, datasetAportacion);
            LineData dataRendimiento = new LineData(labels, datasetRendimiento);

            mLineChartRendimiento.setData(dataRendimiento);
            mLineChartInversion.setData(dataAportacion);

            mLineChartRendimiento.animateY(5000);
            mLineChartInversion.animateY(5000);

            mTextViewBanco.setText("  " + bancos.getNombre().toString());
            mTextViewInvInicial.setText("  $ " + proyeccion.getCantidadInicial().toString());
            mTextViewInvAnual.setText("  $ " + proyeccion.getCatidadAnual().toString());
            mTextViewAnios.setText("  " + proyeccion.getAnios().toString());
            mTextViewTasaRendimiento.setText("  " + bancos.getTasaRendimiento()*100 + "%");
            mTextViewTotalInversion.setText("  $ " + inversion);
            mTextViewTotalRendimiento.setText("  $ " + rendimiento);
            mTextViewTotalInverionGrafica.setText("  $ " + inversion);

            mLinearLayoutDetalleProyecciones.setVisibility(View.VISIBLE);
            mProgressBarDetalleProyecciones.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
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
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

}
