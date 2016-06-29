package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.List;
import java.util.Set;

/**
 * Created by maribelmai on 21/6/16.
 */
public class LineaHough {

    private static final String TAG = LineaHough.class.getSimpleName();
    private Integer x;
    private Integer y;

    public LineaHough(Integer y, Integer x) {
        this.x = x;
        this.y = y;
    }

    public void dibujar (MatrizAcumuladora acumuladora, Bitmap imagen) {

        Canvas c = new Canvas(imagen);
        Paint p = new Paint();
        p.setColor(Color.MAGENTA);

        Set<Parametro> parametros = acumuladora.getEspacioDeParametros().keySet();

        Parametro parametroEncontrado = null;

        for (Parametro parametro : parametros) {

            if(parametro.getY() == this.y && parametro.getX() == this.x) {

                parametroEncontrado = parametro;
            }
        }

        //Chequeo en todas menos verticales

        List<Point> puntosDeEstaRecta = acumuladora.getEspacioDeParametros().get(parametroEncontrado);

        if (puntosDeEstaRecta.size() > 1) {

            Point punto1 = puntosDeEstaRecta.get(0);
            Point punto2 = puntosDeEstaRecta.get(1);

            int x0, y0, x1, y1;

            // Recta Horizontal
            if(punto1.y == punto2.y) {

                x0 = 0;
                x1 = imagen.getWidth();
                y0 = punto1.y;
                y1 = punto1.y;

                // Recta Vertical
            } else if (punto1.x == punto2.x) {

                x0 = punto1.x;
                x1 = punto1.x;
                y0 = 0;
                y1 = imagen.getHeight();

                // Diagonales
            } else {

                x0 = punto1.x;
                x1 = punto2.x;
                y0 = punto1.y;
                y1 = punto2.y;
            }
            c.drawLine(x0, y0, x1, y1, p);
        }

        //VERTICALES

        //Chequeo en todas menos verticales

        List<Point> puntosVerticalesDeEstaRecta = acumuladora.getEspacioDeParametrosVerticales().get(parametroEncontrado);

        if (puntosVerticalesDeEstaRecta.size() > 1) {

            Point punto1 = puntosVerticalesDeEstaRecta.get(0);
            Point punto2 = puntosVerticalesDeEstaRecta.get(1);

            int x0, y0, x1, y1;

            if (punto1.x == punto2.x) {

                x0 = punto1.x;
                x1 = punto1.x;
                y0 = 0;
                y1 = imagen.getHeight();

                c.drawLine(x0, y0, x1, y1, p);
            }
        }
    }

    @Override
    public String toString() {
        return "LineaHough{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
