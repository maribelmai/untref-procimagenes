package ar.edu.untref.procesamientoimagenes.tareas.bordes;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordes;
import ar.edu.untref.procesamientoimagenes.modelo.TipoBorde;
import ar.edu.untref.procesamientoimagenes.util.Operacion;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarBordesPrewitt extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = TareaAplicarBordesPrewitt.class.getSimpleName();
    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;
    private TipoBorde tipoBorde;
    private int[][] matrizGradiente;

    public TareaAplicarBordesPrewitt(ActividadBordes actividadBordes, Bitmap bitmapOriginal, TipoBorde tipoBorde) {

        this.actividadBordes = actividadBordes;
        this.bitmapOriginal = bitmapOriginal;
        this.tipoBorde = tipoBorde;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if (tipoBorde != TipoBorde.COMPLETO) {

            int[][] matrizGradiente = generarMatrizGradientes(tipoBorde);

            return Operacion.hacerTransformacionLineal(matrizGradiente);
        }
        else {
            int[][] matrizGradienteHorizontal = generarMatrizGradientes(TipoBorde.HORIZONTAL);
            int[][] matrizGradienteVertical = generarMatrizGradientes(TipoBorde.VERTICAL);

            matrizGradiente = Operacion.obtenerMatrizMagnitudGradiente(matrizGradienteHorizontal, matrizGradienteVertical);
            Bitmap magnitudGradiente = Operacion.hacerTransformacionLineal(matrizGradiente);
            return magnitudGradiente;
        }
    }

    private int[][] generarMatrizGradientes(TipoBorde tipoBordeElegido) {

        int[][] matrizBordesPrewitt = new int[3][3];

        if (tipoBordeElegido == TipoBorde.HORIZONTAL) {

            Log.i(TAG, "generarMatrizGradientes: horizontal");
            matrizBordesPrewitt[0][0] =  1;
            matrizBordesPrewitt[0][1] =  0;
            matrizBordesPrewitt[0][2] = -1;

            matrizBordesPrewitt[1][0] =  1;
            matrizBordesPrewitt[1][1] =  0;
            matrizBordesPrewitt[1][2] = -1;

            matrizBordesPrewitt[2][0] =  1;
            matrizBordesPrewitt[2][1] =  0;
            matrizBordesPrewitt[2][2] = -1;
        }
        else if (tipoBordeElegido == TipoBorde.VERTICAL) {

            Log.i(TAG, "generarMatrizGradientes: vertical");
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
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_DERECHA) {

            Log.i(TAG, "generarMatrizGradientes: diagonal derecha");
            matrizBordesPrewitt[0][0] = 0;
            matrizBordesPrewitt[0][1] = 1;
            matrizBordesPrewitt[0][2] = 1;

            matrizBordesPrewitt[1][0] = -1;
            matrizBordesPrewitt[1][1] = 0;
            matrizBordesPrewitt[1][2] = 1;

            matrizBordesPrewitt[2][0] = -1;
            matrizBordesPrewitt[2][1] = -1;
            matrizBordesPrewitt[2][2] = 0;

        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_IZQUIERDA) {

            Log.i(TAG, "generarMatrizGradientes: diagonal izquierda");
            matrizBordesPrewitt[0][0] = 1;
            matrizBordesPrewitt[0][1] = 1;
            matrizBordesPrewitt[0][2] = 0;

            matrizBordesPrewitt[1][0] = 1;
            matrizBordesPrewitt[1][1] = 0;
            matrizBordesPrewitt[1][2] = -1;

            matrizBordesPrewitt[2][0] = 0;
            matrizBordesPrewitt[2][1] = -1;
            matrizBordesPrewitt[2][2] = -1;
        }

        int[][] matrizGradiente = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
        int posicionCentralMascara = 3/2;

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
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        actividadBordes.bordesDetectados(bitmap, matrizGradiente);
    }
}
