package ar.edu.untref.procesamientoimagenes.tareas.bordes;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordes;
import ar.edu.untref.procesamientoimagenes.modelo.TipoBorde;
import ar.edu.untref.procesamientoimagenes.util.Operacion;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarBordesGenerico extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarBordesGenerico.class.getSimpleName();
    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;
    private TipoBorde tipoBorde;

    public TareaAplicarBordesGenerico(ActividadBordes actividadBordes, Bitmap bitmapOriginal, TipoBorde tipoBorde) {

        this.actividadBordes = actividadBordes;
        this.bitmapOriginal = bitmapOriginal;
        this.tipoBorde = tipoBorde;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if (tipoBorde != TipoBorde.COMPLETO) {

            int[][] matrizGradiente = generarMatrizGradientes(tipoBorde);
            return Operacion.obtenerBitmapDesdeMagnitudes(bitmapOriginal, matrizGradiente);
        }
        else {

            int[][] matrizGradienteHorizontal = generarMatrizGradientes(TipoBorde.HORIZONTAL);
            int[][] matrizGradienteVertical = generarMatrizGradientes(TipoBorde.VERTICAL);
            int[][] matrizGradienteDiagonalDerecha = generarMatrizGradientes(TipoBorde.DIAGONAL_DERECHA);
            int[][] matrizGradienteDiagonalIzquierda = generarMatrizGradientes(TipoBorde.DIAGONAL_IZQUIERDA);

            Bitmap horizontal = Operacion.obtenerBitmapDesdeMagnitudes(bitmapOriginal, matrizGradienteHorizontal);
            Bitmap vertical = Operacion.obtenerBitmapDesdeMagnitudes(bitmapOriginal, matrizGradienteVertical);
            Bitmap suma = Operacion.sumar(horizontal, vertical);
            horizontal.recycle();
            vertical.recycle();

            Bitmap diagonalDerecha = Operacion.obtenerBitmapDesdeMagnitudes(bitmapOriginal, matrizGradienteDiagonalDerecha);
            suma = Operacion.sumar(suma, diagonalDerecha);

            Bitmap diagonalIzquierda = Operacion.obtenerBitmapDesdeMagnitudes(bitmapOriginal, matrizGradienteDiagonalIzquierda);
            suma = Operacion.sumar(suma, diagonalIzquierda);
            return suma;
        }
    }

    private int[][] generarMatrizGradientes(TipoBorde tipoBordeElegido) {

        int[][] matrizBordesGenerico = new int[3][3];

        if (tipoBordeElegido == TipoBorde.HORIZONTAL) {

            matrizBordesGenerico[0][0] = 1;
            matrizBordesGenerico[0][1] = 1;
            matrizBordesGenerico[0][2] = -1;

            matrizBordesGenerico[1][0] = 1;
            matrizBordesGenerico[1][1] = -2;
            matrizBordesGenerico[1][2] = -1;

            matrizBordesGenerico[2][0] = 1;
            matrizBordesGenerico[2][1] = 1;
            matrizBordesGenerico[2][2] = -1;
        }
        else if (tipoBordeElegido == TipoBorde.VERTICAL) {

            matrizBordesGenerico[0][0] = 1;
            matrizBordesGenerico[0][1] = 1;
            matrizBordesGenerico[0][2] = 1;

            matrizBordesGenerico[1][0] = 1;
            matrizBordesGenerico[1][1] = -2;
            matrizBordesGenerico[1][2] = 1;

            matrizBordesGenerico[2][0] = -1;
            matrizBordesGenerico[2][1] = -1;
            matrizBordesGenerico[2][2] = -1;
        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_DERECHA) {

            matrizBordesGenerico[0][0] = 1;
            matrizBordesGenerico[0][1] = -1;
            matrizBordesGenerico[0][2] = -1;

            matrizBordesGenerico[1][0] = 1;
            matrizBordesGenerico[1][1] = -2;
            matrizBordesGenerico[1][2] = -1;

            matrizBordesGenerico[2][0] = 1;
            matrizBordesGenerico[2][1] = 1;
            matrizBordesGenerico[2][2] = 1;

        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_IZQUIERDA) {

            matrizBordesGenerico[0][0] = 1;
            matrizBordesGenerico[0][1] = 1;
            matrizBordesGenerico[0][2] = 1;

            matrizBordesGenerico[1][0] = 1;
            matrizBordesGenerico[1][1] = -2;
            matrizBordesGenerico[1][2] = -1;

            matrizBordesGenerico[2][0] = 1;
            matrizBordesGenerico[2][1] = -1;
            matrizBordesGenerico[2][2] = -1;
        }

        int[][] matrizGradiente = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
        int posicionCentralMascara = 3/2;

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado
                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        double valorMascara = matrizBordesGenerico[xMascara][yMascara];
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
        actividadBordes.bordesDetectados(bitmap);
    }
}
