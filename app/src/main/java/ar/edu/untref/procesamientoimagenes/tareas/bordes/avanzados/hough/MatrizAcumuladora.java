package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maribelmai on 21/6/16.
 */
public class MatrizAcumuladora {

    private static final String TAG = MatrizAcumuladora.class.getSimpleName();
    private int rhoMin;
    private int rhoMax;
    private int tethaMin;
    private int tethaMax;
    private Integer discretizacionesRho;
    private Integer discretizacionesTetha;
    private Map<Parametro, List<Point>> espacioDeParametros = new HashMap<>();

    public MatrizAcumuladora(int minRho, int maxRho, int minTheta, int maxTheta, Integer discretizacionesRho, Integer discretizacionesTetha) {

        this.rhoMin = minRho;
        this.rhoMax = maxRho;
        this.tethaMin = minTheta;
        this.tethaMax = maxTheta;
        this.discretizacionesRho = discretizacionesRho;
        this.discretizacionesTetha = discretizacionesTetha;
        this.cargarEspacioParametros();
    }

    private void cargarEspacioParametros() {

        int distanciaEntreRhos = this.rhoMax - this.rhoMin;

        if (this.discretizacionesRho > 2) {
            distanciaEntreRhos = this.rhoMax / this.discretizacionesRho;
        }

        int distanciaEntreTethas = this.tethaMax - this.tethaMin;
        if (this.discretizacionesTetha > 2) {
            distanciaEntreTethas = this.tethaMax / this.discretizacionesTetha;
        }

        //Todas las posibles combinaciones entre theta y rho
        for (int rho = 0; rho < this.discretizacionesRho; rho++) {
            for (int tetha = 0; tetha < this.discretizacionesTetha; tetha++) {

                espacioDeParametros.put(new Parametro(tetha * distanciaEntreTethas, rho * distanciaEntreRhos), new ArrayList<Point>());
            }
        }
    }

    public List<LineaHough> getMaximos() {

        int cantidadElementosMaximo = 0;
        List<LineaHough> lineas = new ArrayList<>();

        for (Parametro parametro : espacioDeParametros.keySet()) {

            int cantidadPuntos = espacioDeParametros.get(parametro).size();

            Log.i(TAG, "getMaximos: cantidad puntos: " + cantidadPuntos + " en " + parametro.getRo() + " " + parametro.getTetha());

            if (cantidadPuntos > cantidadElementosMaximo) {
                cantidadElementosMaximo = cantidadPuntos;
            }
        }

        for (Parametro parametro : espacioDeParametros.keySet()) {

            int cantidadPuntos = espacioDeParametros.get(parametro).size();

            if (cantidadPuntos >= ((float)cantidadElementosMaximo * 0.8f)) {

                lineas.add(new LineaHough(parametro.getRo(), parametro.getTetha()));
            }
        }

        return lineas;
    }

    public Map<Parametro, List<Point>> getEspacioDeParametros() {
        return espacioDeParametros;
    }
}
