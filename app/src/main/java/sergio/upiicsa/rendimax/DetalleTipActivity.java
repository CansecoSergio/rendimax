package sergio.upiicsa.rendimax;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import org.w3c.dom.Text;

import sergio.upiicsa.rendimax.clases.Bancos;
import sergio.upiicsa.rendimax.clases.Tips;

public class DetalleTipActivity extends AppCompatActivity {

    private static final String TAGACTDETALLE = "ACTIVITY-DETALLE";

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
    private DatabaseReference mDatabaseReferenceTips = mDatabaseReference.child("tips");

    private Toolbar mToolbar;
    private TextView mToolbarText;
    private TextView mTextViewFrase;
    private TextView mTextViewdescripcion;
    private ImageView mImageViewTip;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayoutDetalleTip;
    private String extraNombreTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_tip);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_detalle_tip);
        mToolbarText = (TextView) findViewById(R.id.titulo_toolbar_detalleTip);
        mTextViewFrase = (TextView) findViewById(R.id.detalle_tip_frase_del_tip);
        mTextViewdescripcion = (TextView) findViewById(R.id.detalle_tip_descripcion_del_tip);
        mImageViewTip = (ImageView) findViewById(R.id.detalle_tip_imagen);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarTipsDetalle);
        mLinearLayoutDetalleTip = (LinearLayout) findViewById(R.id.layout_detalle_tip);

        setSupportActionBar(mToolbar);

        mLinearLayoutDetalleTip.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

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
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    irActivityLogin();
                }
            }
        };

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                extraNombreTip = extras.getString("NOMBRE_TIP");
            }
        } else{
            extraNombreTip = (String) savedInstanceState.getSerializable("NOMBRE_TIP");
        }

        if (extraNombreTip != null && extraNombreTip.length() > 0) {
            mToolbarText.setText(extraNombreTip);

            mDatabaseReferenceTips.orderByChild("nombre").equalTo(extraNombreTip)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                            Tips tip = messageSnapshot.getValue(Tips.class);

                            if (tip != null) {
                                cargaDetalleDelTip(tip);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void cargaDetalleDelTip(Tips tip) {
        mTextViewFrase.setText(tip.getFrase()!=null?tip.getFrase():" - ");
        mTextViewdescripcion.setText(tip.getDescripcion()!=null?tip.getDescripcion():" - ");

        Glide.with(this)
                .load(tip.getImagenUrl()!=null?tip.getImagenUrl():"")
                .centerCrop()
                //.fitCenter()
                .crossFade()
                //.override(1000, 200)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .into(mImageViewTip);

        mLinearLayoutDetalleTip.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
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
}
