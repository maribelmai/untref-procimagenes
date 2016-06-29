package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough;

/**
 * Created by maribelmai on 21/6/16.
 */
public class Parametro {

    private Integer x;
    private Integer y;
    private Integer radio;

    public Parametro(Integer y, Integer x) {
        this.x = x;
        this.y = y;
    }

    public Parametro(Integer y, Integer x, Integer radio) {
        this.x = x;
        this.y = y;
        this.radio = radio;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }
    public Integer getRadio() {
        return radio;
    }

    @Override
    public String toString() {
        return "Parametro{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
