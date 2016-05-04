package ar.edu.untref.procesamientoimagenes.tareas.bordes;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordes;
import ar.edu.untref.procesamientoimagenes.util.Operacion;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarMetodoDelLaplacianoConEvaluacionDePendiente extends AsyncTask<Void, Void, Bitmap> {

    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;

    public TareaAplicarMetodoDelLaplacianoConEvaluacionDePendiente(ActividadBordes actividadBordes, Bitmap bitmapOriginal) {

        this.actividadBordes = actividadBordes;
        this.bitmapOriginal = bitmapOriginal;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        int[][] matrizGradiente = generarMatrizGradientes();

        //Busco los cruces por cero
        int[][] matrizCrucePorCero = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        for (int i = 0; i < bitmapOriginal.getWidth(); i++) {
            for (int j = 0; j < bitmapOriginal.getHeight(); j++) {

                if (Operacion.hayCambioDeSignoPorFila(matrizGradiente, i, j)) {
                    matrizCrucePorCero[i][j] = 255;
                } else {
                    matrizCrucePorCero[i][j] = 0;
                }

                int alpha = Color.alpha(bitmapOriginal.getPixel(i,j));
                int colorPixel = matrizCrucePorCero[i][j];
                mutableBitmap.setPixel(i, j, Color.argb(alpha, colorPixel, colorPixel, colorPixel));
            }
        }

        return mutableBitmap;
    }

    private int[][] generarMatrizGradientes() {

        int[][] matrizBordesLaplaciano = new int[3][3];

        matrizBordesLaplaciano[0][0] =  0;
        matrizBordesLaplaciano[0][1] = -1;
        matrizBordesLaplaciano[0][2] =  0;

        matrizBordesLaplaciano[1][0] = -1;
        matrizBordesLaplaciano[1][1] =  4;
        matrizBordesLaplaciano[1][2] = -1;

        matrizBordesLaplaciano[2][0] =  0;
        matrizBordesLaplaciano[2][1] = -1;
        matrizBordesLaplaciano[2][2] =  0;

        int[][] matrizGradiente = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
        int posicionCentralMascara = 3/2;

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado
                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        double valorMascara = matrizBordesLaplaciano[xMascara][yMascara];
                        int nivelGrisPixel = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));
                        double operacion = nivelGrisPixel * valorMascara;

                        valorResultado += operacion;
                    }
                }

                int valorEntero = (int) valorResultado;
                matrizGradiente[x][y] = valorEntero;
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