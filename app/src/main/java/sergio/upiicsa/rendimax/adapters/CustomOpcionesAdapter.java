package sergio.upiicsa.rendimax.adapters;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import sergio.upiicsa.rendimax.R;

/**
 * Created by Sergio on 4/13/2017.
 */

public class CustomOpcionesAdapter extends ArrayAdapter<String> {

    private final Activity contexto;
    private final String[] nombre;
    private final String[] descripcion;
    private final Integer[] intImagen;

    public CustomOpcionesAdapter(Activity context, String[] nombre, String[] descripcion, Integer[] intImagen) {
        super(context, R.layout.lista_opciones, nombre);

        this.contexto = context;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.intImagen = intImagen;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = contexto.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.lista_opciones, null, true);

        ImageView imagen = (ImageView) rowView.findViewById(R.id.imagen_item_lista_opciones);
        TextView tituloTv = (TextView) rowView.findViewById(R.id.nombre_item_lista_opciones);
        TextView descripcionTv = (TextView) rowView.findViewById(R.id.descripcion_item_lista_opciones);

        imagen.setImageResource(intImagen[position]);
        tituloTv.setText(nombre[position]);
        descripcionTv.setText(descripcion[position].substring(0, Math.min(descripcion[position].length(), 40)) + " ...");

        return rowView;
    }
}
