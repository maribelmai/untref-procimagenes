package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import java.util.List;
import java.util.Set;

/**
 * Created by maribelmai on 21/6/16.
 */
public class LineaHough {

    private static final String TAG = LineaHough.class.getSimpleName();
    private Integer tetha;
    private Integer ro;

    public LineaHough(Integer ro, Integer tetha) {
        this.tetha = tetha;
        this.ro = ro;
    }

    public void dibujar (MatrizAcumuladora acumuladora, Bitmap imagen) {

        Set<Parametro> parametros = acumuladora.getEspacioDeParametros().keySet();

        Parametro parametroEncontrado = null;

        for (Parametro parametro : parametros) {

            if(parametro.getRo() == this.ro && parametro.getTetha() == this.tetha) {

                parametroEncontrado = parametro;
            }
        }

        List<Point> puntosDeEstaRecta = acumuladora.getEspacioDeParametros().get(parametroEncontrado);

        if (puntosDeEstaRecta.size() > 1) {

            Point punto1 = puntosDeEstaRecta.get(0);
            Point punto2 = puntosDeEstaRecta.get(1);

            Log.i(TAG, "draw: " + punto1 + " a " + punto2);


            int x0, y0, x1, y1;

            // Recta Vertical
            if(punto1.x == punto2.x) {

                x0 = 0;
                x1 = imagen.getWidth();
                y0 = punto1.y;
                y1 = punto1.y;

                // Recta Horizontal
            } else if (punto1.y == punto2.y) {

                x0 = punto1.x;
                x1 = punto1.x;
                y0 = 0;
                y1 = imagen.getHeight();

                // Diagonales
            } else {

                // Ecuacion de la recta que pasa por dos puntos (x0, y0), (x1, y1)
                // (x - x0) * (y1 - y0) = (y - y0) * (x1 - x0)
                // => y = [ (x - x0) * (y1 - y0) / (x1 - x0) ] - y0

                // Cuando x = 0 ¿cuál es el y correspondiente de la recta en la imágen?
//            x0 = 0;
//            y0 = (x0 - punto1.x) * (punto2.y - punto1.y) / (punto2.x - punto1.x);
//            y0 = y0 - punto1.y;
//
//            // Necesito otro punto de la imágen, ¿cuando x0 es el ancho total, cuál va a ser el valor de y?
//            x1 = imagen.getWidth() - 1;
//            y1 = (x1 - punto1.x) * (punto2.y - punto1.y) / (punto2.x - punto1.x);
//            y1 = y1 - punto1.y;

                x0 = punto1.x;
                x1 = punto2.x;
                y0 = punto1.y;
                y1 = punto2.y;
            }

            Canvas c = new Canvas(imagen);
            Paint p = new Paint();
            p.setColor(Color.MAGENTA);
            c.drawLine(x0, y0, x1, y1, p);
        }

    }
}
