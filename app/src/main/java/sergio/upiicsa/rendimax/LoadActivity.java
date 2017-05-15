package sergio.upiicsa.rendimax;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{

    //Declaracion de GoogleApiClient para hace posible las operaciones de login y log out
    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        progressBar = (ProgressBar) findViewById(R.id.progressBarLoad);
        progressBar.setVisibility(View.VISIBLE);

        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

        mGoogleApiClient = new GoogleApiClient.Builder(LoadActivity.this)
                .enableAutoManage(this, this)
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
                    irActivityPrincipal();
                } else {
                    irActivityLogin();
                }
            }
        };

        //Ejecución de la clase AsyncTask para la revisión si hay sesión existente o no
        //new RevisionSesion().execute();
    }

    private void irActivityLogin() {
        Intent intent = new Intent(LoadActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void irActivityPrincipal() {
        Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Destruye el activity actual al presionar atrás
        this.finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast toast = Toast.makeText(LoadActivity.this, R.string.error_conexion_google, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /*private class RevisionSesion extends AsyncTask<Void, Void, Boolean> implements
        GoogleApiClient.OnConnectionFailedListener {

        //Uso de LoadActivity.this para referenciar objetos con el UI Thread
        ProgressDialog mProgressDialog = new ProgressDialog(LoadActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setMessage("\tCargando...");
            mProgressDialog.show();
        }

        //Método para la revision de login existente, retorna un boleano para su revisión en el
        //método onPostExecute
        @Override
        protected Boolean doInBackground(Void... voids) {

            //True si hay conexion a la red, en caso contrario false
            if (revisaConexion()){
                GoogleSignInOptions googleSignInOptions =
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                mGoogleApiClient = new GoogleApiClient.Builder(LoadActivity.this)
                        .enableAutoManage(LoadActivity.this, this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                        .build();

                //Resultado de un Logeo silencioso a la cuenta de Google
                OptionalPendingResult<GoogleSignInResult> optionalPendingResult =
                        Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

                if (optionalPendingResult.isDone()){
                    GoogleSignInResult result = optionalPendingResult.get();
                    handleSignInResult(result);
                } else {
                    optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                        @Override
                        public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                            handleSignInResult(googleSignInResult);
                        }
                    });
                }
            } else {
                //Cierra el dialogo de carga y ejecuta el método onPostExecute
                mProgressDialog.dismiss();
                return false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean != null){
                //Elementos relacionados con el UI Thread únicamente son ligados en onPreExecute
                //y onPostExecute
                Toast toast = Toast.makeText(LoadActivity.this, R.string.error_conexion_internet, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

        private boolean revisaConexion() {
            //Instancias para la revisión de conexión actual a la red del dispositivo
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            //Si la instancia "cm" es diferente de null entonces hay conexion y la variable es true
            //en caso contrario false
            if (netInfo != null && netInfo.isConnectedOrConnecting()){
                return true;
            } else {
                return false;
            }
        }

        private void handleSignInResult(GoogleSignInResult result) {
            if (result.isSuccess()){
                mProgressDialog.dismiss();
                Intent intent = new Intent(LoadActivity.this, PrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                mProgressDialog.dismiss();
                Intent intent = new Intent(LoadActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        //Necesario para manejar si hubo algún error de conexión al implementar GoogleApiClient
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast toast = Toast.makeText(LoadActivity.this, R.string.error_conexion_google, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }*/
}


//Codigo para logout
/*Auth.GoogleSignInApi.signOut
status.isSucces();
 */

//Codigo para revocar
/*Auth.GoogleSignInApi.revokeAcces
status.succes();
 */
