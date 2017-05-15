package sergio.upiicsa.rendimax;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    Button acercaRendimaxButton;
    SignInButton mSignInButton;

    private GoogleApiClient mGoogleApiClient;
    private static final int SIGN_IN_CODE = 999;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mSignInButton = (SignInButton) findViewById(R.id.googleSignInButton);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        acercaRendimaxButton = (Button) findViewById(R.id.acerca_de_rendimax);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);

        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });

        acercaRendimaxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoInformacion();
            }
        });

        //Obtencion de la instancia a la que está conectada la aplicacion
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Oyente que se ejecutara si esta logeado
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    irActivityPrincipal();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void dialogoInformacion() {
        new AlertDialog.Builder(this)
                .setTitle("Acerca de Rendimax")
                .setMessage("Equipo 1 - Titulación II:\n\n"
                + "\t - Bravo Zitlalpopoca Mariel Karen\n"
                + "\t - Castañeda Mendoza Emmanuel\n"
                + "\t - Canseco Sánchez Sergio Augusto\n"
                + "\t - Tobón Ornelas Vanessa\n"
                + "\t - Suárez Aldana Teresa")
                .setPositiveButton(R.string.boton_aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_info)
                .show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast toast = Toast.makeText(this, R.string.error_conexion_google, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            //Si se termina con éxito se manda la cuenta de Google
            autenticarFirebaseConGoogle(result.getSignInAccount());
        } else {
            Toast.makeText(this, R.string.no_logeo, Toast.LENGTH_SHORT).show();
        }
    }

    private void autenticarFirebaseConGoogle(GoogleSignInAccount googleSignInAccount) {
        progressBar.setVisibility(View.VISIBLE);
        mSignInButton.setVisibility(View.GONE);
        acercaRendimaxButton.setVisibility(View.GONE);

        AuthCredential credencial = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credencial).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                mSignInButton.setVisibility(View.VISIBLE);
                acercaRendimaxButton.setVisibility(View.VISIBLE);

                if (!task.isSuccessful()) {
                    Toast.makeText(getApplication(), R.string.no_autentico_firebase,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void irActivityPrincipal() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
