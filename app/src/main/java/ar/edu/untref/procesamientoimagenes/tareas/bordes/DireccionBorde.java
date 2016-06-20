package ar.edu.untref.procesamientoimagenes.tareas.bordes;

import android.util.SparseArray;

/**
 * Created by maribelmai on 13/6/16.
 */
public enum DireccionBorde {

    HORIZONTAL(0),
    VERTICAL(90),
    DIAGONAL_IZQUIERDA(135),
    DIAGONAL_DERECHA(45)
    ;

    private int angulo;
    private static SparseArray<DireccionBorde> direcciones = new SparseArray<>();

    static {

        for (DireccionBorde direccionBorde : values()) {
            direcciones.put(direccionBorde.angulo, direccionBorde);
        }
    }

    DireccionBorde(int angulo) {

        this.angulo = angulo;
    }

    public static DireccionBorde obtenerDireccionBorde(int angulo) {
        return direcciones.get(angulo);
    }
}
