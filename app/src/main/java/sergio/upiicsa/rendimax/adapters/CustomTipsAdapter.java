package sergio.upiicsa.rendimax.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import sergio.upiicsa.rendimax.R;

/**
 * Created by Sergio on 5/21/2017.
 */

public class CustomTipsAdapter extends ArrayAdapter<String> {

    private final Activity contexto;
    private final String[] nombre;
    private final String[] descripcion;

    public CustomTipsAdapter(Activity context, String[] nombre, String[] descripcion) {
        super(context, R.layout.lista_opciones, nombre);

        this.contexto = context;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = contexto.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.lista_tips, null, true);

        TextView tituloTv = (TextView) rowView.findViewById(R.id.nombre_item_tips);
        TextView descripcionTv = (TextView) rowView.findViewById(R.id.descripcion_item_tips);

        if (nombre[position].length() > 20) {
            tituloTv.setText(nombre[position].substring(0,  Math.min(descripcion[position].length(), 20)) + "...");
        } else {
            tituloTv.setText(nombre[position]);
        }

        if (descripcion[position].length() > 40) {
            descripcionTv.setText(descripcion[position].substring(0, Math.min(descripcion[position].length(), 40)) + " ...");
        } else {
            descripcionTv.setText(descripcion[position]);
        }
        return rowView;
    }
}
