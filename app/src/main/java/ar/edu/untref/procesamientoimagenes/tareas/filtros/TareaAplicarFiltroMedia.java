package ar.edu.untref.procesamientoimagenes.tareas.filtros;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadFiltros;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarFiltroMedia extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarFiltroMedia.class.getSimpleName();
    private ActividadFiltros actividadFiltros;
    private Bitmap bitmapOriginal;
    private Integer tamanioMascara;

    public TareaAplicarFiltroMedia(ActividadFiltros actividadFiltros, Bitmap bitmapOriginal, Integer tamanioMascara) {

        this.actividadFiltros = actividadFiltros;
        this.bitmapOriginal = bitmapOriginal;
        this.tamanioMascara = tamanioMascara;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        float[][] matrizFiltroMedia = new float[tamanioMascara][tamanioMascara];

        for (int x = 0; x < tamanioMascara; x++) {
            for (int y = 0; y < tamanioMascara; y++) {
                matrizFiltroMedia[x][y] = (float) 1/(tamanioMascara*tamanioMascara);
            }
        }

        int posicionCentralMascara = tamanioMascara/2;

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado

                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        float valorMascara = matrizFiltroMedia[xMascara][yMascara];
                        int nivelGrisPixel = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));

                        float operacion = nivelGrisPixel * valorMascara;

                        //Log.i(LOG_TAG, "Pixel: " + x + "," + y + "--->" + xMascara + "->" + xMascaraEnImagen + ", " + yMascara + "->" + yMascaraEnImagen);

                        valorResultado += operacion;
                    }
                }

                int valorEntero = (int) valorResultado;

                mutableBitmap.setPixel(x,y, Color.rgb(valorEntero, valorEntero, valorEntero));
                //Log.i(LOG_TAG, "----");
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
