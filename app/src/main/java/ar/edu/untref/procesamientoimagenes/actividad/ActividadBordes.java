package ar.edu.untref.procesamientoimagenes.actividad;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;

/**
 * Created by Eli- on 25/04/2016.
 */
public class ActividadBordes extends ActividadBasica {

    private static final String LOG_TAG = ActividadBordes.class.getSimpleName();
    private File imagen;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.imagen)
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Bordes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);

    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_bordes;
    }

    @Override
    public void finish() {

        String nombreOriginal = imagen.getName();
        File file = null;

        try {
            file = getAplicacion().guardarArchivo(((BitmapDrawable) imageView.getDrawable()).getBitmap(), "/tmp/", nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf(".")));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Temporal no se pudo guardar: " + e);
        }

        Intent intent = new Intent();
        intent.putExtra(Constante.EXTRA_IMAGEN, file);
        setResult(Constante.RESULT_CODE_IMAGEN_MODIFICADA, intent);
        super.finish();
    }
}
