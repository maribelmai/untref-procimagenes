package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by maribelmai on 21/6/16.
 */
public class TransformadaDeHoughRectas {

    private static final String TAG = TransformadaDeHoughRectas.class.getSimpleName();
    public static final double EPSILON = 0.1;

    public static Bitmap aplicarTransformadaDeHough(Bitmap imagenOriginal, MatrizAcumuladora acumuladora){

        for (int i=0; i< imagenOriginal.getWidth(); i++){
            for (int j=0; j< imagenOriginal.getHeight(); j++){
                verificarBlanco(acumuladora, i, j, imagenOriginal.getPixel(i,j));
            }
        }

        Bitmap nuevaImagen = imagenOriginal.copy(Bitmap.Config.RGB_565, true);

        for (LineaHough linea: acumuladora.getMaximos()) {
            linea.dibujar(acumuladora, nuevaImagen);
        }

        return nuevaImagen;
    }

    private static void verificarBlanco(MatrizAcumuladora acumuladora, int i, int j, int color) {

        if (color == Color.WHITE) {

            for (Parametro parametro : acumuladora.getEspacioDeParametros().keySet()) {
                int x = parametro.getX();
                int y = parametro.getY();
                evaluarPunto(acumuladora, i, j, parametro, x, y);
            }
        }
    }

    private static void evaluarPunto(MatrizAcumuladora acumuladora, int i, int j, Parametro parametro, int a, int b) {

        boolean perteneceALaRectaHorizontal = (Double.compare(Math.abs(-a * i - b + j) , EPSILON) < 0);
        boolean perteneceALaRectaVertical = (Double.compare(Math.abs(-a * j - b + i) , EPSILON) < 0);

        if (perteneceALaRectaHorizontal) {
            acumuladora.getEspacioDeParametros().get(parametro).add(new Point(i, j));
        }

        if (perteneceALaRectaVertical) {
            acumuladora.getEspacioDeParametrosVerticales().get(parametro).add(new Point(i, j));
        }
    }
}
