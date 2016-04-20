package ar.edu.untref.procesamientoimagenes.tareas;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadFiltros;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarFiltroMediana extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarFiltroMediana.class.getSimpleName();
    private ActividadFiltros actividadFiltros;
    private Bitmap bitmapOriginal;
    private Integer tamanioMascara;

    public TareaAplicarFiltroMediana(ActividadFiltros actividadFiltros, Bitmap bitmapOriginal, Integer tamanioMascara) {

        this.actividadFiltros = actividadFiltros;
        this.bitmapOriginal = bitmapOriginal;
        this.tamanioMascara = tamanioMascara;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        int posicionCentralMascara = tamanioMascara/2;

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado

                List<Integer> valoresGris = new ArrayList<>();

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        int nivelGrisPixel = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));
                        valoresGris.add(nivelGrisPixel);
                    }
                }

                Collections.sort(valoresGris);

                int cantidadValores = valoresGris.size();
                int color;
                //Si es un tamanio par
                if (cantidadValores % 2 == 0) {
                    color = (valoresGris.get(cantidadValores/2) + valoresGris.get(cantidadValores/2 + 1)) / 2;
                }
                //Si es un tamanio impar
                else {
                    color = valoresGris.get(cantidadValores/2 + 1);
                }

                mutableBitmap.setPixel(x,y, Color.rgb(color, color, color));
            }
        }

        //Pinto los bordes negros
        for (int x = 0; x < bitmapOriginal.getWidth(); x++) {
            for (int y = 0 ; y < bitmapOriginal.getHeight(); y ++) {

                if (x < posicionCentralMascara || x >= bitmapOriginal.getWidth() - posicionCentralMascara
                        || y < posicionCentralMascara || y >= bitmapOriginal.getHeight() - posicionCentralMascara) {
                    mutableBitmap.setPixel(x,y, Color.rgb(0, 0, 0));
                }
            }
        }

        return mutableBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmapResultante) {
        super.onPostExecute(bitmapResultante);

        actividadFiltros.filtroAplicado(bitmapResultante);
    }
}
