package ar.edu.untref.procesamientoimagenes.util;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by maribel on 5/4/16.
 */
public class Operacion {

    private static final String TAG = Operacion.class.getSimpleName();

    public static Bitmap sumar (Bitmap bitmap1, Bitmap bitmap2) {

        int[][] matrizPixeles = new int[bitmap1.getWidth()][bitmap2.getHeight()];

        for (int x = 0; x < bitmap1.getWidth(); x++) {

            for (int y = 0; y < bitmap1.getHeight(); y++) {

                int valorPixelBitmap1 = Color.red(bitmap1.getPixel(x, y));
                int valorPixelBitmap2 = Color.red(bitmap2.getPixel(x, y));

                matrizPixeles[x][y] = valorPixelBitmap1 + valorPixelBitmap2;
            }
        }

        return hacerTransformacionLineal(matrizPixeles);
    }

    public static Bitmap hacerTransformacionLineal(int[][] matrizPixeles) {

        float minimo;
        float maximo;

        int ancho = matrizPixeles.length;
        int alto = matrizPixeles[0].length;

        minimo = 0;
        maximo = 255;

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {

                int valorActual = matrizPixeles[x][y];

                if (minimo > valorActual) {
                    minimo = valorActual;
                }

                if (maximo < valorActual) {
                    maximo = valorActual;
                }

            }

        }

        Bitmap bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {

                int valorActual = matrizPixeles[x][y];
                int valorTransformado = (int) ((((255f) / (maximo - minimo)) * valorActual) - ((minimo * 255f) / (maximo - minimo)));

                bitmap.setPixel(x,y, Color.rgb(valorTransformado, valorTransformado, valorTransformado));
            }
        }

        return bitmap;
    }

    public static boolean hayCambioDeSignoPorFila(int[][] matriz, int x, int y) {

        boolean hayCambio = false;

        if (y - 1 >= 0) {

            int valorActual = matriz[x][y];
            int valorAnterior = matriz[x][y - 1];

            if (valorAnterior == 0 && y - 2 >= 0) {
                valorAnterior = matriz[x][y - 2];
            }

            hayCambio = (valorAnterior < 0 && valorActual > 0)
                    || (valorAnterior > 0 && valorActual < 0);
        }
        return hayCambio;
    }

    public static boolean hayCambioDeSignoPorColumna(int[][] matriz, int x, int y) {

        boolean hayCambio = false;

        if (x - 1 >= 0) {

            int valorActual = matriz[x][y];
            int valorAnterior = matriz[x-1][y];

            if (valorAnterior == 0 && x - 2 >= 0) {
                valorAnterior = matriz[x - 2][y];
            }

            hayCambio = (valorAnterior < 0 && valorActual > 0)
                    || (valorAnterior > 0 && valorActual < 0);
        }
        return hayCambio;
    }

    public static boolean hayCambioDeSignoPorColumna(int[][] matriz, int x, int y, int pendiente) {

        boolean hayCambio = false;

        if (x - 1 >= 0) {

            int valorActual = matriz[x][y];
            int valorAnterior = matriz[x-1][y];

            if (valorAnterior == 0 && x - 2 >= 0) {
                valorAnterior = matriz[x - 2][y];
            }

            hayCambio = (valorAnterior < 0 && valorActual > 0)
                    || (valorAnterior > 0 && valorActual < 0);

            if (hayCambio) {
                hayCambio = valorActual + valorAnterior >= pendiente;
            }
        }
        return hayCambio;
    }

    public static boolean hayCambioDeSignoPorFila(int[][] matriz, int x, int y, int pendiente) {

        boolean hayCambio = false;

        if (y - 1 >= 0) {

            int valorActual = matriz[x][y];
            int valorAnterior = matriz[x][y - 1];

            if (valorAnterior == 0 && y - 2 >= 0) {
                valorAnterior = matriz[x][y - 2];
            }

            hayCambio = (valorAnterior < 0 && valorActual > 0)
                    || (valorAnterior > 0 && valorActual < 0);

            if (hayCambio) {
                hayCambio = valorActual + valorAnterior >= pendiente;
            }
        }

        return hayCambio;
    }

    public static int[][] obtenerMatrizMagnitudGradiente(int[][] matrizGradienteHorizontal, int[][] matrizGradienteVertical) {

        int ancho = matrizGradienteHorizontal.length;
        int alto = matrizGradienteHorizontal[0].length;

        int[][] magnitudes = new int[ancho][alto];

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {

                int magnitud = (int) Math.sqrt(Math.pow(matrizGradienteHorizontal[x][y], 2) + Math.pow(matrizGradienteVertical[x][y], 2));
                magnitudes[x][y] = magnitud;
            }
        }

        return magnitudes;
    }
}
