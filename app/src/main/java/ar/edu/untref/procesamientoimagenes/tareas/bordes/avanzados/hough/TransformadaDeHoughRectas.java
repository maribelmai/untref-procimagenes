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
                int tetha = parametro.getTetha();
                int ro = parametro.getRo();
                evaluarPunto(acumuladora, i, j, parametro, tetha, ro);
            }
        }
    }

    private static void evaluarPunto(MatrizAcumuladora acumuladora, int i, int j, Parametro parametro, int tetha, int rho) {

        boolean perteneceALaRecta = Math.abs(rho - i*Math.cos(Math.toRadians(tetha))- j*Math.sin(Math.toRadians(tetha))) < EPSILON;

        if (perteneceALaRecta) {
            acumuladora.getEspacioDeParametros().get(parametro).add(new Point(i, j));
        }
    }
}
