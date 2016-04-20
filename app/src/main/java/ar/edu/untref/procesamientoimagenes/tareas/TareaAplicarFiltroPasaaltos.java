package ar.edu.untref.procesamientoimagenes.tareas;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadFiltros;
import ar.edu.untref.procesamientoimagenes.util.Compresion;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarFiltroPasaaltos extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarFiltroPasaaltos.class.getSimpleName();
    private ActividadFiltros actividadFiltros;
    private Bitmap bitmapOriginal;
    private Integer tamanioMascara;

    public TareaAplicarFiltroPasaaltos(ActividadFiltros actividadFiltros, Bitmap bitmapOriginal, Integer tamanioMascara) {

        this.actividadFiltros = actividadFiltros;
        this.bitmapOriginal = bitmapOriginal;
        this.tamanioMascara = tamanioMascara;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        int[][] matrizFiltroPasaalto = new int[tamanioMascara][tamanioMascara];
        int posicionCentralMascara = tamanioMascara/2;

        for (int x = 0; x < tamanioMascara; x++) {
            for (int y = 0; y < tamanioMascara; y++) {
                matrizFiltroPasaalto[x][y] = -1;
            }
        }

        matrizFiltroPasaalto[posicionCentralMascara][posicionCentralMascara] = (int) (Math.pow(tamanioMascara, 2) - 1);

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado

                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        float valorMascara = matrizFiltroPasaalto[xMascara][yMascara];
                        int nivelGrisPixel = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));

                        float operacion = nivelGrisPixel * valorMascara;
                        valorResultado += operacion;
                    }
                }

                int valorEntero = (int) valorResultado;

                mutableBitmap.setPixel(x,y, Color.rgb(valorEntero, valorEntero, valorEntero));
            }
        }

        return mutableBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmapResultante) {
        super.onPostExecute(bitmapResultante);
        actividadFiltros.filtroAplicado(Compresion.hacerCompresionRangoDinamico(bitmapResultante));
    }
}
