package ar.edu.untref.procesamientoimagenes.tareas.bordes;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordes;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarBordesPrewitt extends AsyncTask<Void, Void, int[][]> {

    private static final String LOG_TAG = TareaAplicarBordesPrewitt.class.getSimpleName();
    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;
    private TipoBorde tipoBorde;

    public TareaAplicarBordesPrewitt(ActividadBordes actividadBordes, Bitmap bitmapOriginal, TipoBorde tipoBorde) {

        this.actividadBordes = actividadBordes;
        this.bitmapOriginal = bitmapOriginal;
        this.tipoBorde = tipoBorde;
    }

    @Override
    protected int[][] doInBackground(Void... params) {

        int[][] matrizGradiente = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];

        int[][] matrizBordesPrewitt = new int[3][3];
        int posicionCentralMascara = 3/2;

        if (tipoBorde == TipoBorde.HORIZONTAL) {

            matrizBordesPrewitt[0][0] = -1;
            matrizBordesPrewitt[0][1] =  0;
            matrizBordesPrewitt[0][2] =  1;

            matrizBordesPrewitt[1][0] = -1;
            matrizBordesPrewitt[1][1] =  0;
            matrizBordesPrewitt[1][2] =  1;

            matrizBordesPrewitt[2][0] = -1;
            matrizBordesPrewitt[2][1] =  0;
            matrizBordesPrewitt[2][2] =  1;
        }
        else if (tipoBorde == TipoBorde.VERTICAL) {

            matrizBordesPrewitt[0][0] = 1;
            matrizBordesPrewitt[0][1] = 1;
            matrizBordesPrewitt[0][2] = 1;

            matrizBordesPrewitt[1][0] = 0;
            matrizBordesPrewitt[1][1] = 0;
            matrizBordesPrewitt[1][2] = 0;

            matrizBordesPrewitt[2][0] = -1;
            matrizBordesPrewitt[2][1] = -1;
            matrizBordesPrewitt[2][2] = -1;
        }

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado
                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        double valorMascara = matrizBordesPrewitt[xMascara][yMascara];
                        int nivelGrisPixel = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));
                        double operacion = nivelGrisPixel * valorMascara;

                        valorResultado += operacion;
                    }
                }

                int valorEntero = (int) valorResultado;
                matrizGradiente[x][y] = Math.abs(valorEntero);;
            }
        }

        return matrizGradiente;
    }

    @Override
    protected void onPostExecute(int[][] magnitudes) {
        super.onPostExecute(magnitudes);
        actividadBordes.bordesDetectados(magnitudes);
    }

    public enum TipoBorde {

        VERTICAL,
        HORIZONTAL
    }
}
