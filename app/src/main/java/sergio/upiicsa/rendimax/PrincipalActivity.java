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

    private static final String TAGPRINCIPAL = "TAG-PRINCIPAL";
    private Fragment mFragment = null;
    Class fragmentClass = null;
    private Toolbar mToolbar;
    private BottomNavigationView mBottomNavigationView;
    private TextView mToolbarText;
    private ImageButton mImageButton;
    private ImageButton mImageButtonAdd;

    private static final String TAGACTINICIO = "ACTIVITY-INICIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_principal);
        mToolbarText = (TextView) findViewById(R.id.titulo_toolbar);
        mImageButton = (ImageButton) mToolbar.findViewById(R.id.boton_recargar_principal);
        mImageButtonAdd = (ImageButton) mToolbar.findViewById(R.id.boton_agregar_proyeccion);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        //AppCompatActivity activity = (AppCompatActivity) getActivity();
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(PrincipalActivity.this,
                        "Reload", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        /*setSupportActionBar(mToolbar);
        mToolbar.setClickable(true);
        mToolbar.findViewById(R.id.boton_agregar_proyeccion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProyeccionFormActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });*/

        mImageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProyeccionFormActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        mImageButton.setVisibility(View.GONE);
        mImageButtonAdd.setVisibility(View.GONE);
        actualizaFragment();

        /*mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(PrincipalActivity.this,
                        "Reload", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });*/

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
                                mImageButtonAdd.setVisibility(View.VISIBLE);
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

        mImageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void actualizaFragment() {
        mImageButton.setVisibility(View.GONE);
        mImageButtonAdd.setVisibility(View.GONE);
        if (mFragment == null && fragmentClass == null){
            fragmentClass = InicioFragment.class;
            try {
                mToolbarText.setText(R.string.menu_inicio);
                mFragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                mImageButton.setVisibility(View.VISIBLE);
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
            mImageButton.setVisibility(View.VISIBLE);
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
