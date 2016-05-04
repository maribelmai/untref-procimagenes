package ar.edu.untref.procesamientoimagenes.actividad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarBordesPrewitt;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarBordesSobel;
import butterknife.Bind;
import butterknife.OnClick;

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

    @Bind(R.id.seleccionUmbralSeekbar)
    SeekBar seleccionUmbralSeekbar;

    @Bind(R.id.deteccionFinalizada)
    View deteccionFinalizada;

    private Bitmap bitmapOriginal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Bordes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);

        Bitmap bitmap= ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmapOriginal = bitmap.copy(Bitmap.Config.RGB_565, true);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_bordes;
    }

    @OnClick(R.id.bordePrewittHorizontal)
    public void detectarBorderPrewittHorizontal() {

        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TareaAplicarBordesPrewitt.TipoBorde.HORIZONTAL).execute();
    }

    @OnClick(R.id.bordePrewittVertical)
    public void detectarBorderPrewittVertical() {

        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TareaAplicarBordesPrewitt.TipoBorde.VERTICAL).execute();
    }

    @OnClick(R.id.bordeSobelHorizontal)
    public void detectarBorderSobelHorizontal() {

        new TareaAplicarBordesSobel(this, bitmapOriginal, TareaAplicarBordesSobel.TipoBorde.HORIZONTAL).execute();
    }

    @OnClick(R.id.bordeSobelVertical)
    public void detectarBorderSobelVertical() {

        new TareaAplicarBordesSobel(this, bitmapOriginal, TareaAplicarBordesSobel.TipoBorde.VERTICAL).execute();
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

    public void bordesDetectados(final int[][] magnitudes) {

        deteccionFinalizada.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);

        int ancho = magnitudes.length;
        int alto = magnitudes[0].length;

        int minimo = Integer.MAX_VALUE;
        int maximo = Integer.MIN_VALUE;

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {

                int valorActual = magnitudes[x][y];

                if (minimo > valorActual) {
                    minimo = valorActual;
                }

                if (maximo < valorActual) {
                    maximo = valorActual;
                }

            }

        }

        seleccionUmbralSeekbar.setMax(maximo - minimo);
        seleccionUmbralSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                deteccionFinalizada.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);

                int umbral = seekBar.getProgress();
                Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

                for (int x = 0; x < bitmapOriginal.getWidth(); x++) {
                    for (int y = 0 ; y < bitmapOriginal.getHeight(); y ++) {

                        if (magnitudes[x][y] < umbral) {
                            mutableBitmap.setPixel(x,y, Color.BLACK);
                        }
                        else {
                            mutableBitmap.setPixel(x,y, Color.WHITE);
                        }
                    }
                }

                imageView.setImageBitmap(mutableBitmap);
            }
        });
    }
}
