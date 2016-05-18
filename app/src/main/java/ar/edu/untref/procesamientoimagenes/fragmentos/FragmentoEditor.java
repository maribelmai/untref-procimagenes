package ar.edu.untref.procesamientoimagenes.fragmentos;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadAumentarContraste;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordes;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadDegrade;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadDifusion;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadFiltros;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadFormas;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadHistograma;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadObtenerPixel;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadOperaciones;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadRecortar;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadRuidos;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadUmbral;
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
            getAplicacion().mostrarImagen(imagen, imagenOriginal);
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
            Toast.makeText(getActivity(), R.string.selecciona_una_imagen, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Constante.RESULT_CODE_IMAGEN_MODIFICADA) {

            File file = (File) data.getSerializableExtra(Constante.EXTRA_IMAGEN);
            imagenOriginal.setImageDrawable(null);
            mostrarImagen();
            getAplicacion().mostrarImagen(file, imagenEditada);
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

        if (imagen != null) {

            Intent intent = new Intent(getActivity(), ActividadRecortar.class);
            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
            startActivity(intent);
        }
        else {
            Toast.makeText(getActivity(), R.string.selecciona_una_imagen, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.degrade)
    public void crearDegrades() {

        Intent intent = new Intent(getActivity(), ActividadDegrade.class);
        startActivity(intent);
    }

    @OnClick(R.id.formas)
    public void crearFormas() {

        Intent intent = new Intent(getActivity(), ActividadFormas.class);
        startActivity(intent);
    }

    @OnClick(R.id.operaciones)
    public void operarConImagenes() {

        Intent intent = new Intent(getActivity(), ActividadOperaciones.class);
        if (imagen != null) {

            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
        }
        startActivity(intent);
    }

    @OnClick(R.id.contraste)
    public void aumentarContraste() {

        Intent intent = new Intent(getActivity(), ActividadAumentarContraste.class);
        if (imagen != null) {

            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
        }
        startActivityForResult(intent, Constante.RESULT_CODE_IMAGEN_MODIFICADA);
    }

    @OnClick(R.id.histograma)
    public void operarConHistograma() {

        if (imagen != null) {

            Intent intent = new Intent(getActivity(), ActividadHistograma.class);
            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
            startActivity(intent);
        }
        else {
            Toast.makeText(getActivity(), R.string.selecciona_una_imagen, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.umbral)
    public void umbralizar() {

        if (imagen != null) {

            Intent intent = new Intent(getActivity(), ActividadUmbral.class);
            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
            startActivityForResult(intent, Constante.RESULT_CODE_IMAGEN_MODIFICADA);
        }
        else {
            Toast.makeText(getActivity(), R.string.selecciona_una_imagen, Toast.LENGTH_SHORT).show();
        }
    }
    @OnClick(R.id.ruidos)
    public void aplicarRuido() {

        if (imagen != null) {

            Intent intent = new Intent(getActivity(), ActividadRuidos.class);
            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
            startActivityForResult(intent, Constante.RESULT_CODE_IMAGEN_MODIFICADA);
        }
        else {
            Toast.makeText(getActivity(), R.string.selecciona_una_imagen, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.filtros)
    public void aplicarFiltros() {

        if (imagen != null) {

            Intent intent = new Intent(getActivity(), ActividadFiltros.class);
            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
            startActivityForResult(intent, Constante.RESULT_CODE_IMAGEN_MODIFICADA);
        }
        else {
            Toast.makeText(getActivity(), R.string.selecciona_una_imagen, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.bordes)
    public void aplicarBordes() {

        if (imagen != null) {
            Intent intent = new Intent(getActivity(), ActividadBordes.class);
            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
            startActivityForResult(intent, Constante.RESULT_CODE_IMAGEN_MODIFICADA);
        }
        else {
            Toast.makeText(getActivity(), R.string.selecciona_una_imagen, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.difusion)
    public void aplicarDifusion() {

        if (imagen != null) {
            Intent intent = new Intent(getActivity(), ActividadDifusion.class);
            intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
            startActivityForResult(intent, Constante.RESULT_CODE_IMAGEN_MODIFICADA);
        }
        else {
            Toast.makeText(getActivity(), R.string.selecciona_una_imagen, Toast.LENGTH_SHORT).show();
        }
    }
}
