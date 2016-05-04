package ar.edu.untref.procesamientoimagenes.util;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by maribel on 5/4/16.
 */
public class Operacion {

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

    public static Bitmap obtenerBitmapDesdeMagnitudes(Bitmap bitmapOriginal, int[][] magnitudes) {

        int ancho = magnitudes.length;
        int alto = magnitudes[0].length;

        int minimo = Integer.MAX_VALUE;
        int maximo = Integer.MIN_VALUE;

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {

                int valorActual = magnitudes[x][y];

                if (minimo > valorActual) {
                    minimo = valorActual;
                }

                if (maximo < valorActual) {
                    maximo = valorActual;
                }
            }
        }

        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        for (int i = 1; i < mutableBitmap.getWidth() - 1; i++) {
            for (int j = 1; j < mutableBitmap.getHeight() - 1; j++) {
                int nuevoPixel = getPixelTransformado(maximo, minimo, magnitudes[i][j]);
                mutableBitmap.setPixel(i, j, Color.rgb(nuevoPixel, nuevoPixel, nuevoPixel));
            }
        }

        return mutableBitmap;
    }

    public static int getPixelTransformado(int grisMax, int grisMin, int grisActual) {

        int grisTransformado = (int) ((((255f) / (grisMax - grisMin)) * grisActual) - ((grisMin * 255f) / (grisMax - grisMin)));
        return grisTransformado;
    }
}
