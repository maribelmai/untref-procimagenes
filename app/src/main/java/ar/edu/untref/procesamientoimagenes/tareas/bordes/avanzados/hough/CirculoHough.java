package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Set;

/**
 * Created by maribelmai on 21/6/16.
 */
public class CirculoHough {

    private static final String TAG = CirculoHough.class.getSimpleName();
    private Integer x;
    private Integer y;
    private Integer radio;

    public CirculoHough(Integer y, Integer x, Integer radio) {
        this.x = x;
        this.y = y;
        this.radio = radio;
    }

    public void dibujar (MatrizAcumuladora acumuladora, Bitmap imagen) {

        Canvas c = new Canvas(imagen);
        Paint p = new Paint();
        p.setColor(Color.MAGENTA);

        Set<Parametro> parametros = acumuladora.getEspacioDeParametros().keySet();

        Parametro parametroEncontrado = null;

        for (Parametro parametro : parametros) {

            if(parametro.getY().equals(this.y) && parametro.getX().equals(this.x)) {

                parametroEncontrado = parametro;
            }
        }

        c.drawCircle(parametroEncontrado.getX(), parametroEncontrado.getY(), parametroEncontrado.getRadio(), p);

    }

    @Override
    public String toString() {
        return "CirculoHough{" +
                "x=" + x +
                ", y=" + y +
                ", radio=" + radio +
                '}';
    }
}
