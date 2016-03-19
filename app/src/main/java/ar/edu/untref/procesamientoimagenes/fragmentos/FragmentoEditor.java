package ar.edu.untref.procesamientoimagenes.fragmentos;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import butterknife.Bind;

/**
 * Created by mmaisano on 19/03/16.
 */
public class FragmentoEditor extends FragmentoBasico {

    @Bind(R.id.imagenOriginal)
    ImageView imagenOriginal;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Override
    protected int getLayout() {
        return R.layout.fragmento_editor;
    }

    public void cargarImagen(File imagen) {

        if (isAdded()) {

            nombreImagen.setText(imagen.getName());
            Picasso.with(getActivity()).load("file:///" + imagen.getAbsolutePath()).into(imagenOriginal);
        }
    }
}
