package sergio.upiicsa.rendimax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ProyeccionFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyeccion_form);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
