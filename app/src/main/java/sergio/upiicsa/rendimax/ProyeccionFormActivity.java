package sergio.upiicsa.rendimax;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.List;

import sergio.upiicsa.rendimax.clases.Bancos;
import sergio.upiicsa.rendimax.clases.Proyecciones;

public class ProyeccionFormActivity extends AppCompatActivity {

    private static final String TAGACTDETALLE = "ACTIVITY-DETALLE";

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
    private DatabaseReference mDatabaseReferenceBancos = mDatabaseReference.child("bancos");
    private DatabaseReference mDatabaseReferenceProyecciones = mDatabaseReference.child("proyecciones");

    private FloatingActionButton mFloatingActionButtonProyecciones;

    private TextInputLayout mTextInputLayoutNombre;
    private TextInputLayout mTextInputLayoutCantidad;
    private TextInputLayout mTextInputLayoutCantidadAnual;
    private TextInputLayout mTextInputLayoutAnios;
    private TextInputLayout mTextInputLayoutBanco;

    private EditText mEditTextNombre;
    private EditText mEditTextCantidad;
    private EditText mEditTextCantidadAnual;
    private EditText mEditTextAnios;
    private EditText mEditTextBanco;

    private LinearLayout mLinearLayoutFrmProyecciones;
    private ProgressBar mProgressBarFrmProyecciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyeccion_form);

        mLinearLayoutFrmProyecciones = (LinearLayout) findViewById(R.id.layout_frm_proyeccion);
        mProgressBarFrmProyecciones = (ProgressBar) findViewById(R.id.progressBarFrmProyeccion);

        mTextInputLayoutNombre = (TextInputLayout) findViewById(R.id.til_nombre_proyeccion);
        mTextInputLayoutCantidad = (TextInputLayout) findViewById(R.id.til_cantidad_proyeccion);
        mTextInputLayoutCantidadAnual = (TextInputLayout) findViewById(R.id.til_cantidad_anual_proyeccion);
        mTextInputLayoutAnios = (TextInputLayout) findViewById(R.id.til_anios_proyeccion);
        mTextInputLayoutBanco = (TextInputLayout) findViewById(R.id.til_banco_proyeccion);
        mFloatingActionButtonProyecciones = (FloatingActionButton) findViewById(R.id.fab_nueva_proyeccion);

        mEditTextNombre = (EditText) findViewById(R.id.et_nombre_proyeccion);
        mEditTextCantidad = (EditText) findViewById(R.id.et_cantidad_proyeccion);
        mEditTextCantidadAnual = (EditText) findViewById(R.id.et_cantidad_anual_proyeccion);
        mEditTextAnios = (EditText) findViewById(R.id.et_anios_proyeccion);
        mEditTextBanco = (EditText) findViewById(R.id.et_banco_proyeccion);

        mEditTextAnios.setFocusable(false);
        mEditTextBanco.setFocusable(false);

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
                }
            }
        };

        mEditTextAnios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creaDialogoAnios();
            }
        });

        mEditTextBanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseReferenceBancos.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> listaBancos = new ArrayList<String>();

                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            if (data != null) {
                                Bancos bancos = data.getValue(Bancos.class);
                                if (bancos!= null) {
                                    listaBancos.add(bancos.getNombre().toString());
                                }
                            }
                        }

                        if (listaBancos.size() > 0) {
                            creaDialogoBancos(listaBancos);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mFloatingActionButtonProyecciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarErrors();
                Boolean bandera = false;

                if (mTextInputLayoutNombre.getEditText().getText().toString().equals("")) {
                    bandera = true;
                    mTextInputLayoutNombre.setError(getResources().getString(R.string.error_nombre));
                }

                if (mTextInputLayoutCantidad.getEditText().getText().toString().equals("")) {
                    bandera = true;
                    mTextInputLayoutCantidad.setError(getResources().getString(R.string.error_cantidad_inicial));
                }

                if (mTextInputLayoutCantidadAnual.getEditText().getText().toString().equals("")) {
                    bandera = true;
                    mTextInputLayoutCantidadAnual.setError(getResources().getString(R.string.error_cantidad_inicial_anual));
                }

                if (mTextInputLayoutAnios.getEditText().getText().toString().equals("")) {
                    bandera = true;
                    mTextInputLayoutAnios.setError(getResources().getString(R.string.error_anios));
                }

                if (mTextInputLayoutBanco.getEditText().getText().toString().equals("")) {
                    bandera = true;
                    mTextInputLayoutBanco.setError(getResources().getString(R.string.error_banco));
                }

                if (bandera == true) {

                } else {
                    mFloatingActionButtonProyecciones.setVisibility(View.GONE);
                    mLinearLayoutFrmProyecciones.setVisibility(View.GONE);
                    mProgressBarFrmProyecciones.setVisibility(View.VISIBLE);

                    String email = firebaseUser.getEmail().toString();
                    String nombre = mTextInputLayoutNombre.getEditText().getText().toString().toUpperCase();
                    Double cantidadInicial = Double.parseDouble(mTextInputLayoutCantidad.getEditText().getText().toString());
                    Double cantidadAnual = Double.parseDouble(mTextInputLayoutCantidadAnual.getEditText().getText().toString());
                    Integer anios = Integer.parseInt(mTextInputLayoutAnios.getEditText().getText().toString());
                    String banco = mTextInputLayoutBanco.getEditText().getText().toString();
                    String llave = nombre + "_" + email;
                    Date fechaProyeccion = new Date();

                    final Proyecciones proyeccion = new Proyecciones(email, nombre, cantidadInicial, cantidadAnual, anios, banco, llave, fechaProyeccion);

                    mDatabaseReferenceProyecciones.push().setValue(proyeccion, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                mFloatingActionButtonProyecciones.setVisibility(View.VISIBLE);
                                mLinearLayoutFrmProyecciones.setVisibility(View.VISIBLE);
                                mProgressBarFrmProyecciones.setVisibility(View.GONE);

                                System.out.println("Data could not be saved " + databaseError.getMessage());
                            } else {
                                Intent intent = new Intent(getApplicationContext(), DetalleProyeccionActivity.class);
                                intent.putExtra("NOMBRE_PROYECCION", proyeccion.getNombre());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                System.out.println("Data saved successfully.");
                            }
                        }
                    });
                }
            }
        });
    }


    private void limpiarErrors(){
        mTextInputLayoutNombre.setError("");
        mTextInputLayoutCantidad.setError("");
        mTextInputLayoutCantidadAnual.setError("");
        mTextInputLayoutAnios.setError("");
        mTextInputLayoutBanco.setError("");
    }


    private void creaDialogoBancos(List<String> listaBancos) {
        final String bancos[] = new String[listaBancos.size()];

        int i = 0;
        for (String str: listaBancos) {
            bancos[i] = str;
            i++;
        }
        final AlertDialog bancosDialog;

        // Creando el diálogo con sus componentes
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona la institución");
        builder.setSingleChoiceItems(bancos, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        mEditTextBanco.setText(bancos[item]);
                        break;
                    case 1:
                        mEditTextBanco.setText(bancos[item]);
                        break;
                    case 2:
                        mEditTextBanco.setText(bancos[item]);
                        break;
                    default:
                        mEditTextBanco.setText(bancos[0]);
                        break;
                }
                dialog.dismiss();
            }
        });
        bancosDialog = builder.create();
        bancosDialog.show();

    }

    private void creaDialogoAnios() {
        final AlertDialog aniosDialog;

        // Arreglo de años
        final String[] anios = {"1", "2", "3", "4", "5"};

        // Creando el diálogo con sus componentes
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona el número de años");
        builder.setSingleChoiceItems(anios, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        mEditTextAnios.setText("1");
                        break;
                    case 1:
                        mEditTextAnios.setText("2");
                        break;
                    case 2:
                        mEditTextAnios.setText("3");
                        break;
                    case 3:
                        mEditTextAnios.setText("4");
                        break;
                    case 4:
                        mEditTextAnios.setText("5");
                        break;
                    default:
                        mEditTextAnios.setText("1");
                        break;
                }
                dialog.dismiss();
            }
        });
        aniosDialog = builder.create();
        aniosDialog.show();
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
        this.finish();
    }
}
