package ar.edu.untref.procesamientoimagenes.fragmentos;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
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

    @Bind(R.id.imagenEditada)
    ImageView imagenEditada;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.guardar)
    View botonGuardar;

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
            imagenEditada.setImageDrawable(null);
            botonGuardar.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.obtenerPixel)
    public void obtenerPixel() {

        if (imagen != null) {

            Intent intent = new Intent(getActivity(), ActividadObtenerPixel.class);
            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
            startActivityForResult(intent, Constante.REQUEST_CODE_PIXEL);
        }
        else {
            Toast.makeText(getActivity(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constante.REQUEST_CODE_PIXEL && resultCode == Constante.RESULT_CODE_IMAGEN_MODIFICADA) {

            File file = (File) data.getSerializableExtra(Constante.EXTRA_IMAGEN);
            imagenOriginal.setImageDrawable(null);
            mostrarImagen();
            getAplicacion().mostrarImagen(file, imagenEditada, getActivity());
            botonGuardar.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.guardar)
    public void guardar() {

        String nombreOriginal = imagen.getName();
        try {
            String nuevoNombre = nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            getAplicacion().guardarArchivo(((BitmapDrawable) imagenEditada.getDrawable()).getBitmap(), "/", nuevoNombre);
            Toast.makeText(getActivity(), nuevoNombre + " guardado correctamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Ocurrió un error guardando el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.recortar)
    public void recortar() {

    }
}
