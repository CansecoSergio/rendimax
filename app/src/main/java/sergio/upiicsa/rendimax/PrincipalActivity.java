package sergio.upiicsa.rendimax;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PrincipalActivity extends AppCompatActivity
        implements InicioFragment.OnFragmentInteractionListener, TipsFragment.OnFragmentInteractionListener,
                    EstadisticasFragment.OnFragmentInteractionListener, ProyeccionesFragment.OnFragmentInteractionListener,
                        PerfilFragment.OnFragmentInteractionListener{

    private Fragment mFragment = null;
    Class fragmentClass = null;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        actualizaFragment();

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                switch (item.getItemId()){

                    case R.id.itemInicio:
                        fragmentClass = InicioFragment.class;
                        try {
                            mFragment = (Fragment) fragmentClass.newInstance();
                            actualizaFragment();
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(PrincipalActivity.this,
                                    R.string.error_vista, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            e.printStackTrace();
                        }
                        break;

                    case R.id.itemTips:
                        fragmentClass = TipsFragment.class;
                        try {
                            mFragment = (Fragment) fragmentClass.newInstance();
                            actualizaFragment();
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(PrincipalActivity.this,
                                    R.string.error_vista, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            e.printStackTrace();
                        }
                        break;

                    case R.id.itemEstadisticas:
                        fragmentClass = EstadisticasFragment.class;
                        try {
                            mFragment = (Fragment) fragmentClass.newInstance();
                            actualizaFragment();
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(PrincipalActivity.this,
                                    R.string.error_vista, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            e.printStackTrace();
                        }
                        break;

                    case R.id.itemProyecciones:
                        fragmentClass = ProyeccionesFragment.class;
                        try {
                            mFragment = (Fragment) fragmentClass.newInstance();
                            actualizaFragment();
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(PrincipalActivity.this,
                                    R.string.error_vista, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            e.printStackTrace();
                        }
                        break;

                    case R.id.itemPerfil:
                        fragmentClass = PerfilFragment.class;
                        try {
                            mFragment = (Fragment) fragmentClass.newInstance();
                            actualizaFragment();
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(PrincipalActivity.this,
                                    R.string.error_vista, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            e.printStackTrace();
                        }
                        break;

                    default:
                        Toast toast = Toast.makeText(PrincipalActivity.this,
                                R.string.error_vista, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        break;
                }
                return false;
            }
        });
    }

    private void actualizaFragment() {
        if (mFragment == null && fragmentClass == null){
            fragmentClass = InicioFragment.class;
            try {
                mFragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contenidoFragment,
                mFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
