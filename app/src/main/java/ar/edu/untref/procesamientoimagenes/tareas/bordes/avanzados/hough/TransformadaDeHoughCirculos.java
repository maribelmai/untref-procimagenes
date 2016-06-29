package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by maribelmai on 21/6/16.
 */
public class TransformadaDeHoughCirculos {

    private static final String TAG = TransformadaDeHoughCirculos.class.getSimpleName();
    public static final double EPSILON = 0.1;

    public static Bitmap aplicarTransformadaDeHough(Bitmap imagenOriginal, MatrizAcumuladora acumuladora){

        for (int i=0; i< imagenOriginal.getWidth(); i++){
            for (int j=0; j< imagenOriginal.getHeight(); j++){
                verificarBlanco(acumuladora, i, j, imagenOriginal.getPixel(i,j));
            }
        }

        Bitmap nuevaImagen = imagenOriginal.copy(Bitmap.Config.RGB_565, true);

        for (CirculoHough circuloHough: acumuladora.getMaximosCirculos()) {
            circuloHough.dibujar(acumuladora, nuevaImagen);
        }

        return nuevaImagen;
    }

    private static void verificarBlanco(MatrizAcumuladora acumuladora, int i, int j, int color) {

        if (color == Color.WHITE) {

            for (Parametro parametro : acumuladora.getEspacioDeParametros().keySet()) {
                int x = parametro.getX();
                int y = parametro.getY();
                int radio = parametro.getRadio();
                evaluarPunto(acumuladora, i, j, parametro, x, y, radio);
            }
        }
    }

    private static void evaluarPunto(MatrizAcumuladora acumuladora, int i, int j, Parametro parametro, int x, int y, int radio) {

        boolean perteneceALaCircunferencia = (Math.pow(i - x, 2) + Math.pow(j - y, 2) - y) == Math.pow(radio, 2);

        if (perteneceALaCircunferencia) {
            acumuladora.getEspacioDeParametros().get(parametro).add(new Point(i, j));
        }
//        else {
//            Log.d(TAG, "el punto: " + i + "," + j + " no pertenece a la circunferencia con radio dio " + (Math.pow(i - x, 2) + Math.pow(j - y, 2) - y) + " en lugar de " + Math.pow(radio, 2));
//        }
    }
}
