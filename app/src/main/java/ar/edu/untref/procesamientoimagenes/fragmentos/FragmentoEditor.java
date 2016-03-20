package ar.edu.untref.procesamientoimagenes.fragmentos;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadObtenerPixel;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by mmaisano on 19/03/16.
 */
public class FragmentoEditor extends FragmentoBasico {

    @Bind(R.id.imagenOriginal)
    ImageView imagenOriginal;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;
    private File imagen;

    @Override
    protected int getLayout() {
        return R.layout.fragmento_editor;
    }

    public void cargarImagen(File imagen) {

        if (isAdded()) {

            this.imagen = imagen;
            mostrarImagen();
        }
    }

    private void mostrarImagen() {

        if (isAdded()) {
            nombreImagen.setText(imagen.getName());
            getAplicacion().mostrarImagen(imagen, imagenOriginal, getActivity());
        }
    }

    @OnClick(R.id.obtenerPixel)
    public void obtenerPixel() {

        if (imagen != null) {

            Intent intent = new Intent(getActivity(), ActividadObtenerPixel.class);
            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
            startActivity(intent);
        }
        else {
            Toast.makeText(getActivity(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.guardar)
    public void guardar() {

    }
}
