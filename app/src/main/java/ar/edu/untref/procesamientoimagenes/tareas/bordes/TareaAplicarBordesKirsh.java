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
public class TareaAplicarBordesKirsh extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarBordesKirsh.class.getSimpleName();
    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;
    private TipoBorde tipoBorde;
    private int[][] matrizGradiente;

    public TareaAplicarBordesKirsh(ActividadBordes actividadBordes, Bitmap bitmapOriginal, TipoBorde tipoBorde) {

        this.actividadBordes = actividadBordes;
        this.bitmapOriginal = bitmapOriginal;
        this.tipoBorde = tipoBorde;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if (tipoBorde != TipoBorde.COMPLETO) {

            matrizGradiente = generarMatrizGradientes(tipoBorde);
            return Operacion.hacerTransformacionLineal(matrizGradiente);
        }
        else {

            int[][] matrizGradienteHorizontal = generarMatrizGradientes(TipoBorde.HORIZONTAL);
            int[][] matrizGradienteVertical = generarMatrizGradientes(TipoBorde.VERTICAL);

            matrizGradiente = Operacion.obtenerMatrizMagnitudGradiente(matrizGradienteHorizontal, matrizGradienteVertical);
            Bitmap bitmap = Operacion.hacerTransformacionLineal(matrizGradiente);

            return bitmap;
        }
    }

    private int[][] generarMatrizGradientes(TipoBorde tipoBordeElegido) {

        int[][] matrizBordesKirsh = new int[3][3];

        if (tipoBordeElegido == TipoBorde.HORIZONTAL) {

            matrizBordesKirsh[0][0] = 5;
            matrizBordesKirsh[0][1] = -3;
            matrizBordesKirsh[0][2] = -3;

            matrizBordesKirsh[1][0] = 5;
            matrizBordesKirsh[1][1] = 0;
            matrizBordesKirsh[1][2] = -3;

            matrizBordesKirsh[2][0] = 5;
            matrizBordesKirsh[2][1] = -3;
            matrizBordesKirsh[2][2] = -3;
        }
        else if (tipoBordeElegido == TipoBorde.VERTICAL) {

            matrizBordesKirsh[0][0] = 5;
            matrizBordesKirsh[0][1] = 5;
            matrizBordesKirsh[0][2] = 5;

            matrizBordesKirsh[1][0] = -3;
            matrizBordesKirsh[1][1] = 0;
            matrizBordesKirsh[1][2] = -3;

            matrizBordesKirsh[2][0] = -3;
            matrizBordesKirsh[2][1] = -3;
            matrizBordesKirsh[2][2] = -3;
        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_DERECHA) {

            matrizBordesKirsh[0][0] = -3;
            matrizBordesKirsh[0][1] = -3;
            matrizBordesKirsh[0][2] = -3;

            matrizBordesKirsh[1][0] = 5;
            matrizBordesKirsh[1][1] = 0;
            matrizBordesKirsh[1][2] = -3;

            matrizBordesKirsh[2][0] = 5;
            matrizBordesKirsh[2][1] = 5;
            matrizBordesKirsh[2][2] = -3;

        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_IZQUIERDA) {

            matrizBordesKirsh[0][0] = 5;
            matrizBordesKirsh[0][1] = 5;
            matrizBordesKirsh[0][2] = -3;

            matrizBordesKirsh[1][0] = 5;
            matrizBordesKirsh[1][1] = 0;
            matrizBordesKirsh[1][2] = -3;

            matrizBordesKirsh[2][0] = -3;
            matrizBordesKirsh[2][1] = -3;
            matrizBordesKirsh[2][2] = -3;
        }

        int[][] matrizGradiente = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
        int posicionCentralMascara = 3/2;

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado
                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        double valorMascara = matrizBordesKirsh[xMascara][yMascara];
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
