package ar.edu.untref.procesamientoimagenes.tareas.bordes;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordes;
import ar.edu.untref.procesamientoimagenes.util.Operacion;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarMetodoDelLaplacianoDelGaussiano extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarMetodoDelLaplacianoDelGaussiano.class.getSimpleName();
    private Float sigma;
    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;

    public TareaAplicarMetodoDelLaplacianoDelGaussiano(Float sigma, ActividadBordes actividadBordes, Bitmap bitmapOriginal) {

        this.sigma = sigma;
        this.actividadBordes = actividadBordes;
        this.bitmapOriginal = bitmapOriginal;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        int [][] matrizPixelesLoG = new int[bitmapOriginal.getWidth()] [bitmapOriginal.getHeight()];

        //Genero la máscara en base a la fórmula del Laplaciano del Gaussiano
        int tamanioMascara = sigma < 1 ? 5 : (int) ((3 * sigma) + 1);

        double[][] mascaraLoG = new double[tamanioMascara][tamanioMascara];
        int posicionCentralMascara = tamanioMascara/2;

        for (int x = 0; x < tamanioMascara; x++) {

            for (int y = 0; y < tamanioMascara; y++) {
                mascaraLoG[x][y] = obtenerLoG(x,y, posicionCentralMascara);
                System.out.print(mascaraLoG[x][y]);
            }
            System.out.println("");
        }

        //Paso la máscara

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado
                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        double valorMascara = mascaraLoG[xMascara][yMascara];
                        int nivelGrisPixel = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));

                        double operacion = nivelGrisPixel * valorMascara;
                        valorResultado += operacion;
                    }
                }

                int valorEntero = (int) valorResultado;
                matrizPixelesLoG[x][y] = Color.rgb(valorEntero, valorEntero, valorEntero);
            }
        }

        //Pinto los bordes negros
        for (int x = 0; x < bitmapOriginal.getWidth(); x++) {
            for (int y = 0 ; y < bitmapOriginal.getHeight(); y ++) {

                if (x < posicionCentralMascara || x >= bitmapOriginal.getWidth() - posicionCentralMascara
                        || y < posicionCentralMascara || y >= bitmapOriginal.getHeight() - posicionCentralMascara) {
                    matrizPixelesLoG[x][y] = Color.rgb(0, 0, 0);
                }
            }
        }

        //Busco los cruces por cero
        int[][] matrizCrucePorCero = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        for (int i = 0; i < bitmapOriginal.getWidth(); i++) {
            for (int j = 0; j < bitmapOriginal.getHeight(); j++) {

                if (Operacion.hayCambioDeSignoPorFila(matrizPixelesLoG, i, j)) {
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

    private double obtenerLoG(int x, int y, int posicionCentralMascara) {

        int xEnMascara = x-posicionCentralMascara;
        int yEnMascara = y-posicionCentralMascara;

        return (-1) *
                ( 1 / (Math.sqrt( 2 * Math.PI) * Math.pow(sigma, 3))) *
                ( 2 - ((Math.pow(xEnMascara, 2) + Math.pow(yEnMascara, 2)) / Math.pow(sigma, 2))) *
                Math.exp(-((Math.pow(xEnMascara,2) + Math.pow(yEnMascara,2))/(2 * Math.pow(sigma,2)))) ;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        actividadBordes.bordesDetectados(bitmap);
    }
}
