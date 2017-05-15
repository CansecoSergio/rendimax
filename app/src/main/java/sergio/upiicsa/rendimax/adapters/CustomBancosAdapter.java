package sergio.upiicsa.rendimax.adapters;

import android.app.Activity;
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

public class CustomBancosAdapter extends ArrayAdapter<String> {

    private final Activity contexto;
    private final String[] urlImagen;
    private final String[] nombre;

    public CustomBancosAdapter(Activity contexto, String[] urlImagen, String[] nombre) {
        super(contexto, R.layout.lista_bancos, nombre);
        this.contexto = contexto;
        this.urlImagen = urlImagen;
        this.nombre = nombre;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = contexto.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.lista_bancos, null, true);

        TextView titulo = (TextView) rowView.findViewById(R.id.nombre_item_lista_bancos);
        ImageView imagen = (ImageView) rowView.findViewById(R.id.imagen_item_lista_bancos);

        titulo.setText(nombre[position]);
        Glide.with(contexto)
                .load(urlImagen[position])
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .into(imagen);

        return rowView;
    }
}
