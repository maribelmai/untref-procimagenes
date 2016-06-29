package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maribelmai on 21/6/16.
 */
public class MatrizAcumuladora {

    private static final String TAG = MatrizAcumuladora.class.getSimpleName();
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;
    private Integer discretizacionesX;
    private Integer discretizacionesY;
    private Map<Parametro, List<Point>> espacioDeParametros = new HashMap<>();
    private Map<Parametro, List<Point>> espacioDeParametrosVerticales = new HashMap<>();
    private int minCentroX;
    private int maxCentroX;
    private int minCentroY;
    private int maxCentroY;
    private int minRadio;
    private int maxRadio;
    private int discretizacionesDeCentroY;
    private int discretizacionesDeCentroX;
    private int discretizacionesDeRadio;

    public MatrizAcumuladora(int minRho, int maxRho, int minTheta, int maxTheta, Integer discretizacionesX, Integer discretizacionesY) {

        this.xMin = minRho;
        this.xMax = maxRho;
        this.yMin = minTheta;
        this.yMax = maxTheta;
        this.discretizacionesX = discretizacionesX;
        this.discretizacionesY = discretizacionesY;
        this.cargarEspacioParametros();
    }

    public MatrizAcumuladora(int minCentroX, int maxCentroX, int minCentroY, int maxCentroY, int minRadio, int maxRadio, int discretizacionesDeCentroY, int discretizacionesDeCentroX, int discretizacionesDeRadio) {

        this.minCentroX = minCentroX;
        this.maxCentroX = maxCentroX;
        this.minCentroY = minCentroY;
        this.maxCentroY = maxCentroY;
        this.minRadio = minRadio;
        this.maxRadio = maxRadio;
        this.discretizacionesDeCentroY = discretizacionesDeCentroY;
        this.discretizacionesDeCentroX = discretizacionesDeCentroX;
        this.discretizacionesDeRadio = discretizacionesDeRadio;
        this.cargarEspacioParametrosCirculos();
    }

    private void cargarEspacioParametros() {

        int distanciaEntreX = this.xMax - this.xMin;

        if (this.discretizacionesX > 2) {
            distanciaEntreX = distanciaEntreX / this.discretizacionesX;
        }

        int distanciaEntreY = this.yMax - this.yMin;
        if (this.discretizacionesY > 2) {
            distanciaEntreY = distanciaEntreY / this.discretizacionesY;
        }

        //Todas las posibles combinaciones entre theta y rho
        for (int x = xMin; x < xMax; x++) {
            for (int y = yMin; y < yMax; y++) {

                Parametro parametro = new Parametro(y * distanciaEntreY, x * distanciaEntreX);

                espacioDeParametros.put(parametro, new ArrayList<Point>());
                espacioDeParametrosVerticales.put(parametro, new ArrayList<Point>());
            }
        }
    }

    private void cargarEspacioParametrosCirculos() {

        int distanciaEntreX = this.maxCentroX - this.minCentroX;

        if (this.discretizacionesDeCentroX > 2) {
            distanciaEntreX = distanciaEntreX / this.discretizacionesDeCentroX;
        }

        int distanciaEntreY = this.maxCentroY - this.minCentroY;

        if (this.discretizacionesDeCentroY > 2) {
            distanciaEntreY = distanciaEntreY / this.discretizacionesDeCentroY;
        }

        int distanciaEntreRadios = this.maxRadio - this.minRadio;

        if (this.discretizacionesDeRadio > 2) {
            distanciaEntreRadios = distanciaEntreRadios / this.discretizacionesDeRadio;
        }

        for (int x = minCentroX; x < maxCentroX; x++) {
            for (int y = minCentroY; y < maxCentroY; y++) {
                for (int radio = minRadio; radio < maxRadio; radio++) {

                    Parametro parametro = new Parametro(y * distanciaEntreY, x * distanciaEntreX, radio*distanciaEntreRadios);

                    espacioDeParametros.put(parametro, new ArrayList<Point>());
                }
            }
        }
    }

    public List<LineaHough> getMaximos() {

        int cantidadElementosMaximo = 0;
        List<LineaHough> lineas = new ArrayList<>();

        for (Parametro parametro : espacioDeParametros.keySet()) {

            int cantidadPuntos = espacioDeParametros.get(parametro).size();

            if (cantidadPuntos > cantidadElementosMaximo) {
                cantidadElementosMaximo = cantidadPuntos;
            }
        }

        for (Parametro parametro : espacioDeParametrosVerticales.keySet()) {

            int cantidadPuntos = espacioDeParametrosVerticales.get(parametro).size();

            if (cantidadPuntos > cantidadElementosMaximo) {
                cantidadElementosMaximo = cantidadPuntos;
            }
        }

        for (Parametro parametro : espacioDeParametros.keySet()) {

            int cantidadPuntos = espacioDeParametros.get(parametro).size();

            if (cantidadPuntos >= ((float)cantidadElementosMaximo * 0.8f)) {

                lineas.add(new LineaHough(parametro.getY(), parametro.getX()));
            }
        }

        for (Parametro parametro : espacioDeParametrosVerticales.keySet()) {

            int cantidadPuntos = espacioDeParametrosVerticales.get(parametro).size();

            if (cantidadPuntos >= ((float)cantidadElementosMaximo * 0.8f)) {

                lineas.add(new LineaHough(parametro.getY(), parametro.getX()));
            }
        }

        return lineas;
    }

    public Map<Parametro, List<Point>> getEspacioDeParametros() {
        return espacioDeParametros;
    }

    public Map<Parametro, List<Point>> getEspacioDeParametrosVerticales() {
        return espacioDeParametrosVerticales;
    }

    public List<CirculoHough> getMaximosCirculos() {

        int cantidadElementosMaximo = 0;
        List<CirculoHough> circuloHoughs = new ArrayList<>();

        for (Parametro parametro : espacioDeParametros.keySet()) {

            int cantidadPuntos = espacioDeParametros.get(parametro).size();

            if (cantidadPuntos > cantidadElementosMaximo) {
                cantidadElementosMaximo = cantidadPuntos;
            }
        }

        for (Parametro parametro : espacioDeParametros.keySet()) {

            int cantidadPuntos = espacioDeParametros.get(parametro).size();

            if (cantidadPuntos >= ((float)cantidadElementosMaximo * 0.8f)) {

                circuloHoughs.add(new CirculoHough(parametro.getY(), parametro.getX(), parametro.getRadio()));
            }
        }

        return circuloHoughs;
    }
}
