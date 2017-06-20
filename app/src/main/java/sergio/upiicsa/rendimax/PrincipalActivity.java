package sergio.upiicsa.rendimax;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import sergio.upiicsa.rendimax.fragments.EstadisticasFragment;
import sergio.upiicsa.rendimax.fragments.InicioFragment;
import sergio.upiicsa.rendimax.fragments.PerfilFragment;
import sergio.upiicsa.rendimax.fragments.ProyeccionesFragment;
import sergio.upiicsa.rendimax.fragments.TipsFragment;

public class PrincipalActivity extends AppCompatActivity
        implements
            InicioFragment.OnFragmentInteractionListener,
            TipsFragment.OnFragmentInteractionListener,
            EstadisticasFragment.OnFragmentInteractionListener,
            ProyeccionesFragment.OnFragmentInteractionListener,
            PerfilFragment.OnFragmentInteractionListener{

    private Fragment mFragment = null;
    Class fragmentClass = null;
    private Toolbar mToolbar;
    private BottomNavigationView mBottomNavigationView;
    private TextView mToolbarText;

    private static final String TAGACTINICIO = "ACTIVITY-INICIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_principal);
        mToolbarText = (TextView) findViewById(R.id.titulo_toolbar);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        setSupportActionBar(mToolbar);
        actualizaFragment();


        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                //Obtencion de la última clase seleccionada
                Class claseAnterior = fragmentClass;

                if (claseAnterior == null) {
                    claseAnterior = InicioFragment.class;
                }

                switch (item.getItemId()){

                    case R.id.itemInicio:
                        fragmentClass = InicioFragment.class;
                        if (claseAnterior.equals(fragmentClass) != true){
                            try {
                                mFragment = (Fragment) fragmentClass.newInstance();
                                actualizaFragment();
                                mToolbarText.setText(R.string.menu_inicio);
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(PrincipalActivity.this,
                                        R.string.error_vista, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                e.printStackTrace();
                            }
                        }
                        break;

                    case R.id.itemTips:
                        fragmentClass = TipsFragment.class;
                        if (claseAnterior.equals(fragmentClass) != true) {
                            try {
                                mFragment = (Fragment) fragmentClass.newInstance();
                                actualizaFragment();
                                mToolbarText.setText(R.string.menu_tips);
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(PrincipalActivity.this,
                                        R.string.error_vista, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                e.printStackTrace();
                            }
                        }
                        break;

                    case R.id.itemEstadisticas:
                        fragmentClass = EstadisticasFragment.class;
                        if (claseAnterior.equals(fragmentClass) != true) {
                            try {
                                mFragment = (Fragment) fragmentClass.newInstance();
                                actualizaFragment();
                                mToolbarText.setText(R.string.menu_estadisticas);
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(PrincipalActivity.this,
                                        R.string.error_vista, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                e.printStackTrace();
                            }
                        }
                        break;

                    case R.id.itemProyecciones:
                        fragmentClass = ProyeccionesFragment.class;
                        if (claseAnterior.equals(fragmentClass) != true) {
                            try {
                                mFragment = (Fragment) fragmentClass.newInstance();
                                actualizaFragment();
                                mToolbarText.setText(R.string.menu_proyecciones);
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(PrincipalActivity.this,
                                        R.string.error_vista, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                e.printStackTrace();
                            }
                        }
                        break;

                    case R.id.itemPerfil:
                        fragmentClass = PerfilFragment.class;
                        if (claseAnterior.equals(fragmentClass) != true) {
                            try {
                                mFragment = (Fragment) fragmentClass.newInstance();
                                actualizaFragment();
                                mToolbarText.setText(R.string.menu_perfil);
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(PrincipalActivity.this,
                                        R.string.error_vista, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                e.printStackTrace();
                            }
                        }
                        break;

                    default:
                        Toast toast = Toast.makeText(PrincipalActivity.this,
                                R.string.error_vista, Toast.LENGTH_SHORT);
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
                mToolbarText.setText(R.string.menu_inicio);
                mFragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try{
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contenidoFragment, mFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizaFragment();
    }
}
