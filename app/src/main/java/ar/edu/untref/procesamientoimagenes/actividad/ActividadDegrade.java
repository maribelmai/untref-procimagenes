package ar.edu.untref.procesamientoimagenes.actividad;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import ar.edu.untref.procesamientoimagenes.R;
import butterknife.Bind;

/**
 * Created by mmaisano on 20/03/16.
 */
public class ActividadDegrade extends ActividadBasica {

    private static final String LOG_TAG = ActividadDegrade.class.getSimpleName();

    @Bind(R.id.degradeGrises)
    ImageView degradeGrises;

    @Bind(R.id.degradeColores)
    ImageView degradeColores;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Degradé");

        generarDegradeGrises();
        generarDegradeColores();
    }

    private void generarDegradeGrises() {

        int ancho = 256;
        int alto = 256;

        Bitmap bitmapDegradeGrises = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

        for(int i=0; i <= ancho -1; i++){
            for(int j=0; j<= alto -1; j++){
                bitmapDegradeGrises.setPixel(i, j, Color.rgb(i, i, i));
            }
        }

        try {
            File archivo = getAplicacion().guardarArchivo(bitmapDegradeGrises, "/", "degradeGrises.png");
            getAplicacion().mostrarImagen(archivo, degradeGrises);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generarDegradeColores() {

        int ancho = 50;
        int alto = 255*6; //SON 6 ETAPAS

        Integer r = 255;
        Integer g = 0;
        Integer b = 0;

        Integer etapa = 1;

        Bitmap bitmapDegradeColores = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

        for (int y = 0; y < alto ; y ++) {

            for (int x = 0; x < ancho; x++) {

                bitmapDegradeColores.setPixel(x, y, Color.rgb(r, g, b));
            }

            if (etapa == 1) {
                b ++;

                if (b == 255) {
                    etapa = 2;
                }
            }
            else if (etapa == 2) {

                r --;

                if (r == 0) {
                    etapa = 3;
                }
            }
            else if (etapa == 3) {

                g ++;

                if (g == 255) {
                    etapa = 4;
                }
            }
            else if (etapa == 4) {

                b--;

                if (b == 0) {
                    etapa = 5;
                }
            }
            else if (etapa == 5) {

                r ++;

                if (r == 255) {
                    etapa = 6;
                }
            }
            else {

                g--;
            }
        }

        try {
            File archivo = getAplicacion().guardarArchivo(bitmapDegradeColores, "/", "degradeColores.png");
            getAplicacion().mostrarImagen(archivo, degradeColores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected int getLayout() {
        return R.layout.actividad_degrade;
    }


}
