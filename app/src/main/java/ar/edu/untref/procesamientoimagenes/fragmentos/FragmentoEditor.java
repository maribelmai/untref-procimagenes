package ar.edu.untref.procesamientoimagenes.fragmentos;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import ar.edu.untref.procesamientoimagenes.ImageLoadingUtil;
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

            if (imagen.getName().endsWith(".ppm")) {
                try {
                    Bitmap loadedBitmap = ImageLoadingUtil.readPPM(imagen.getAbsolutePath());
                    imagenOriginal.setImageBitmap(loadedBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (imagen.getName().endsWith(".pgm")) {
               Toast.makeText(getActivity(), "Todavía no estamos soportando este tipo de archivo", Toast.LENGTH_LONG).show();
            }
            else {
                Picasso.with(getActivity()).load("file:///" + imagen.getAbsolutePath()).into(imagenOriginal);
            }
        }
    }
}
