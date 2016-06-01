package ar.edu.untref.procesamientoimagenes.util;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by maribel on 4/13/16.
 */
public class Transformacion {

    private static final String LOG_TAG = Transformacion.class.getSimpleName();

    public static Bitmap hacerCompresionRangoDinamico(Bitmap bitmap) {

        int[][] matrizPixelesR = new int[bitmap.getWidth()][bitmap.getHeight()];
        int[][] matrizPixelesG = new int[bitmap.getWidth()][bitmap.getHeight()];
        int[][] matrizPixelesB = new int[bitmap.getWidth()][bitmap.getHeight()];

        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                matrizPixelesR[x][y] = Color.red(bitmap.getPixel(x,y));
                matrizPixelesG[x][y] = Color.green(bitmap.getPixel(x,y));
                matrizPixelesB[x][y] = Color.blue(bitmap.getPixel(x,y));
            }
        }

        return hacerCompresionRangoDinamico(matrizPixelesR, matrizPixelesG, matrizPixelesB);
    }

    public static Bitmap hacerCompresionRangoDinamico(int[][] matrizPixelesR, int[][] matrizPixelesG, int[][] matrizPixelesB) {

        int valorMaximoR = Integer.MIN_VALUE;
        int valorMaximoG = Integer.MIN_VALUE;
        int valorMaximoB = Integer.MIN_VALUE;

        //Obtengo máximo
        for (int x = 0; x < matrizPixelesR.length; x++) {

            for (int y = 0; y < matrizPixelesR[0].length; y++) {

                int pixelR = matrizPixelesR[x][y];
                int pixelG = matrizPixelesG[x][y];
                int pixelB = matrizPixelesB[x][y];

                if (pixelR > valorMaximoR) {
                    valorMaximoR = pixelR;
                }
                if (pixelG > valorMaximoG) {
                    valorMaximoG = pixelG;
                }
                if (pixelB > valorMaximoB) {
                    valorMaximoB = pixelB;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(matrizPixelesR.length, matrizPixelesR[0].length, Bitmap.Config.RGB_565);

        for (int x = 0; x < matrizPixelesR.length; x++) {

            for (int y = 0; y < matrizPixelesR[0].length; y++) {

                int pixelR = matrizPixelesR[x][y];
                int pixelG = matrizPixelesG[x][y];
                int pixelB = matrizPixelesB[x][y];
                int nuevoPixelR= (int) ((255/(Math.log(1 + valorMaximoR)) * Math.log(1 + pixelR)) );
                int nuevoPixelG= (int) ((255/(Math.log(1 + valorMaximoG)) * Math.log(1 + pixelG)) );
                int nuevoPixelB= (int) ((255/(Math.log(1 + valorMaximoB)) * Math.log(1 + pixelB)) );

                bitmap.setPixel(x, y, Color.rgb(nuevoPixelR, nuevoPixelG, nuevoPixelB));
            }
        }

        return bitmap;
    }

    public static Bitmap hacerTransformacionLineal(int[][] matrizPixelesR, int[][] matrizPixelesG, int[][] matrizPixelesB) {

        float minimoR = 0;
        float maximoR = 255;
        float minimoG = 0;
        float maximoG = 255;
        float minimoB = 0;
        float maximoB = 255;

        for (int x = 0; x < matrizPixelesR.length; x++) {
            for (int y = 0; y < matrizPixelesR[0].length; y++) {

                int valorActualR = matrizPixelesR[x][y];
                int valorActualG = matrizPixelesG[x][y];
                int valorActualB = matrizPixelesB[x][y];

                if (minimoR > valorActualR) {
                    minimoR = valorActualR;
                }

                if (maximoR < valorActualR) {
                    maximoR = valorActualR;
                }
                if (minimoG > valorActualG) {
                    minimoG = valorActualG;
                }

                if (maximoG < valorActualG) {
                    maximoG = valorActualG;
                }
                if (minimoB > valorActualB) {
                    minimoB = valorActualB;
                }

                if (maximoB < valorActualB) {
                    maximoB = valorActualB;
                }

            }

        }

        Bitmap bitmap = Bitmap.createBitmap(matrizPixelesR.length, matrizPixelesR[0].length, Bitmap.Config.RGB_565);

        for (int x = 0; x < matrizPixelesR.length-1; x++) {
            for (int y = 0; y < matrizPixelesR[0].length-1; y++) {

                int valorActualR = matrizPixelesR[x][y];
                int valorActualG = matrizPixelesG[x][y];
                int valorActualB = matrizPixelesB[x][y];
                int valorTransformadoR = (int) ((((255f) / (maximoR - minimoR)) * valorActualR) - ((minimoR * 255f) / (maximoR - minimoR)));
                int valorTransformadoG = (int) ((((255f) / (maximoG - minimoG)) * valorActualG) - ((minimoG * 255f) / (maximoG - minimoG)));
                int valorTransformadoB = (int) ((((255f) / (maximoB - minimoB)) * valorActualB) - ((minimoB * 255f) / (maximoB - minimoB)));


                bitmap.setPixel(x, y, Color.rgb(valorTransformadoR, valorTransformadoG, valorTransformadoB));
            }
        }

        return bitmap;
    }

    public static Bitmap colorAEscalaDeGrises(Bitmap bitmap) {

        Bitmap bitmapGris = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);

        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {

                int pixel = bitmap.getPixel(x,y);
                int rojo = Color.red(pixel);
                int verde = Color.green(pixel);
                int azul = Color.blue(pixel);
                int valorGris = (rojo + verde + azul) / 3;
                int nuevoColor = Color.rgb(valorGris, valorGris, valorGris);
                bitmapGris.setPixel(x, y, nuevoColor);
            }
        }

        return bitmapGris;
    }
}
