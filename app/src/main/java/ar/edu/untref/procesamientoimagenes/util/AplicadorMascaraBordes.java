package ar.edu.untref.procesamientoimagenes.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import ar.edu.untref.procesamientoimagenes.modelo.TipoImagen;

/**
 * Created by maribel on 6/1/16.
 */
public class AplicadorMascaraBordes {

    public static int[][] obtenerMatrizGradientes(Bitmap bitmap, int[][] matrizBordes, TipoImagen tipoImagen) {

        if (tipoImagen == TipoImagen.COLOR) {
            bitmap = Transformacion.colorAEscalaDeGrises(bitmap);
        }

        int[][] matrizGradiente = new int[bitmap.getWidth()][bitmap.getHeight()];
        int posicionCentralMascara = matrizBordes.length/2;

        for (int x = posicionCentralMascara; x < bitmap.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmap.getHeight() - posicionCentralMascara; y++) {

                float resultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        double valorMascara = matrizBordes[xMascara][yMascara];
                        int pixel = bitmap.getPixel(xMascaraEnImagen, yMascaraEnImagen);

                        int gris = Color.red(pixel);

                        resultado += (gris * valorMascara);
                    }
                }

                matrizGradiente[x][y] = (int) resultado;
            }
        }

        return matrizGradiente;
    }
}
