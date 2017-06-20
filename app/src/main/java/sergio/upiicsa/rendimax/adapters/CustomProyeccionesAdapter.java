package sergio.upiicsa.rendimax.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import sergio.upiicsa.rendimax.R;

/**
 * Created by Sergio on 5/26/2017.
 */

public class CustomProyeccionesAdapter extends ArrayAdapter<String> {
    private final Activity contexto;
    private final String[] nombreProyeccion;
    private final String[] fechaProyecciones;


    public CustomProyeccionesAdapter(Activity contexto, String[] nombreProyeccion, String[] fechaProyecciones) {
        super(contexto, R.layout.lista_proyecciones, nombreProyeccion);

        this.contexto = contexto;
        this.nombreProyeccion = nombreProyeccion;
        this.fechaProyecciones = fechaProyecciones;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = contexto.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.lista_proyecciones, null, true);

        TextView tituloTv = (TextView) rowView.findViewById(R.id.nombre_item_proyeccion);
        TextView descripcionTv = (TextView) rowView.findViewById(R.id.descripcion_item_proyeccion);

        if (nombreProyeccion[position].length() > 20) {
            tituloTv.setText(nombreProyeccion[position].substring(0,  Math.min(nombreProyeccion[position].length(), 20)) + "...");
        } else {
            tituloTv.setText(nombreProyeccion[position]);
        }

        if (fechaProyecciones[position].length() > 40) {
            descripcionTv.setText(fechaProyecciones[position].substring(0, Math.min(fechaProyecciones[position].length(), 40)) + " ...");
        } else {
            descripcionTv.setText(fechaProyecciones[position]);
        }
        return rowView;
    }
}
