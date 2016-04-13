package ar.edu.untref.procesamientoimagenes.util;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by maribel on 4/13/16.
 */
public class Compresion {

    private static final String LOG_TAG = Compresion.class.getSimpleName();

    public static Bitmap hacerCompresionRangoDinamico(Bitmap bitmap) {

        int[][] matrizPixeles = new int[bitmap.getWidth()][bitmap.getHeight()];

        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                matrizPixeles[x][y] = Color.red(bitmap.getPixel(x,y));
            }
        }

        return hacerCompresionRangoDinamico(matrizPixeles);
    }

    public static Bitmap hacerCompresionRangoDinamico(int[][] matrizPixeles) {

        int valorMaximo = Integer.MIN_VALUE;

        //Obtengo máximo
        for (int x = 0; x < matrizPixeles.length; x++) {

            for (int y = 0; y < matrizPixeles[0].length; y++) {

                int pixel = matrizPixeles[x][y];

                if (pixel > valorMaximo) {
                    valorMaximo = pixel;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(matrizPixeles.length, matrizPixeles[0].length, Bitmap.Config.RGB_565);

        for (int x = 0; x < matrizPixeles.length; x++) {

            for (int y = 0; y < matrizPixeles[0].length; y++) {

                int pixel = matrizPixeles[x][y];
                int nuevoPixel= (int) ((255/(Math.log(1 + valorMaximo)) * Math.log(1 + pixel)) );

                bitmap.setPixel(x, y, Color.rgb(nuevoPixel, nuevoPixel, nuevoPixel));
            }
        }

        return bitmap;
    }
}
