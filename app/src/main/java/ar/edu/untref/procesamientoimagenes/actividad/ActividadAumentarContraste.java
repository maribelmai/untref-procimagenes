package ar.edu.untref.procesamientoimagenes.actividad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import butterknife.OnClick;

/**
 * Created by maribel on 4/19/16.
 */
public class ActividadAumentarContraste extends ActividadBasica {

    private static final String TAG = ActividadAumentarContraste.class.getSimpleName();
    private File imagen;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.imagen)
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Aumentar contraste");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_aumentar_contraste;
    }

    @OnClick(R.id.aumentar_contraste)
    public void aumentarContraste() {

        //Lo aplico a la imagen
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Bitmap bitmapNuevo = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);

        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {

                int nivelDeGris = Color.red(bitmap.getPixel(x,y));
                int nuevoNivelGris;

                if (nivelDeGris >= 127) {
                    nuevoNivelGris = (nivelDeGris + 5) < 255 ? nivelDeGris + 5 : nivelDeGris;
                }
                else {
                    nuevoNivelGris = (nivelDeGris -5) > 0 ? nivelDeGris -5 : nivelDeGris;
                }

                bitmapNuevo.setPixel(x,y,Color.rgb(nuevoNivelGris, nuevoNivelGris, nuevoNivelGris));
            }
        }

        imageView.setImageBitmap(bitmapNuevo);
    }

    @Override
    public void finish() {

        String nombreOriginal = imagen.getName();
        File file = null;

        try {
            file = getAplicacion().guardarArchivo(((BitmapDrawable) imageView.getDrawable()).getBitmap(), "/tmp/", nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf(".")));
        } catch (Exception e) {
            Log.e(TAG, "Temporal no se pudo guardar: " + e);
        }

        Intent intent = new Intent();
        intent.putExtra(Constante.EXTRA_IMAGEN, file);
        setResult(Constante.RESULT_CODE_IMAGEN_MODIFICADA, intent);
        super.finish();
    }
}
