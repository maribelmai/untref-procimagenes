package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough;

/**
 * Created by maribelmai on 21/6/16.
 */
public class Parametro {

    private Integer tetha;
    private Integer ro;

    public Parametro(Integer ro, Integer tetha) {
        this.tetha = tetha;
        this.ro = ro;
    }

    public Integer getTetha() {
        return tetha;
    }

    public Integer getRo() {
        return ro;
    }
}
