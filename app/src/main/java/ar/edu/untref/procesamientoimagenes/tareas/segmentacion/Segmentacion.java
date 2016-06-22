package ar.edu.untref.procesamientoimagenes.tareas.segmentacion;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ar.edu.untref.procesamientoimagenes.modelo.Curva;

/**
 * Created by maribelmai on 22/6/16.
 */
public class Segmentacion {

    private static List<Point> lOut;
    private static List<Point> lIn;
    private static int[] promedioDeColores = null;
    private static int[][] matrizSigmas = null;
    private static int diferenciaMaxima;

    public static Bitmap identificarObjeto(Bitmap bitmapOriginal, int veces, int diferenciaMaxima, Point puntoInicial, Point puntoFinal) {

        Segmentacion.diferenciaMaxima = diferenciaMaxima;

        Curva curvaSeleccionada = obtenerCurvaDesdePuntosSeleccionados(puntoInicial.x, puntoInicial.y, puntoFinal.x, puntoFinal.y);

        int desdeX = curvaSeleccionada.getDesde().x;
        int hastaX = curvaSeleccionada.getHasta().x;
        int desdeY = curvaSeleccionada.getDesde().y;
        int hastaY = curvaSeleccionada.getHasta().y;

        matrizSigmas = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];

        lIn = new CopyOnWriteArrayList<>();
        lOut = new CopyOnWriteArrayList<>();

        for (int i = 0; i < bitmapOriginal.getWidth(); i++) {
            for (int j = 0; j < bitmapOriginal.getHeight(); j++) {

                if (estaEnBorde(i, j, desdeX, hastaX, desdeY, hastaY)) {

                    matrizSigmas[i][j] = -1;
                    lIn.add(new Point(i, j));

                } else if (estaAlRededorDelBorde(i, j, desdeX, hastaX, desdeY, hastaY)) {

                    matrizSigmas[i][j] = 1;
                    lOut.add(new Point(i, j));

                } else if (estaAdentro(i, j, desdeX, hastaX, desdeY, hastaY)) {

                    matrizSigmas[i][j] = -3;
                } else {

                    matrizSigmas[i][j] = 3;
                }
            }
        }

        promedioDeColores = obtenerPromedioRGB(bitmapOriginal, puntoInicial, puntoFinal);

        for (int i = 0; i < veces; i++) {

            Iterator<Point> iteradorPuntos = lOut.iterator();
            while (iteradorPuntos.hasNext()) {

                expandir(bitmapOriginal, lIn, lOut, promedioDeColores, iteradorPuntos);
            }

            Iterator<Point> iteradorPuntosLin = lIn.iterator();
            while (iteradorPuntosLin.hasNext()) {

                sacarLinNoCorrespondientes(lIn, iteradorPuntosLin);
            }

            Iterator<Point> iteradorPuntosLin2 = lIn.iterator();
            while (iteradorPuntosLin2.hasNext()) {

                contraer(bitmapOriginal, lIn, lOut, promedioDeColores, iteradorPuntosLin2);
            }

            Iterator<Point> iteradorPuntosLout2 = lOut.iterator();
            while (iteradorPuntosLout2.hasNext()) {

                sacarLoutNoCorrespondientes(lOut, iteradorPuntosLout2, bitmapOriginal);
            }
        }

//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap imagenCopia = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        for (Point unPoint : lIn) {
            imagenCopia.setPixel(unPoint.x, unPoint.y, Color.GREEN);
        }

        return imagenCopia;
    }

    private static void expandir(Bitmap imagen, List<Point> lIn, List<Point> lOut, int[] promedio,
                          Iterator<Point> iteradorPuntos) {

        Point unPoint = iteradorPuntos.next();

        if (calcularFd(promedio, imagen, unPoint) > 0) {
            if (unPoint.x > 0 && unPoint.y < imagen.getHeight() - 1 && unPoint.y > 0
                    && unPoint.x < imagen.getWidth() - 1) {
                int valorMatrizIzquierda = matrizSigmas[unPoint.x - 1][unPoint.y];
                int valorMatrizDerecha = matrizSigmas[unPoint.x + 1][unPoint.y];
                int valorMatrizArriba = matrizSigmas[unPoint.x][unPoint.y - 1];
                int valorMatrizAbajo = matrizSigmas[unPoint.x][unPoint.y + 1];

                if (valorMatrizIzquierda == 3) {

                    matrizSigmas[unPoint.x - 1][unPoint.y] = 1;
                    lOut.add(new Point(unPoint.x - 1, unPoint.y));
                }

                if (valorMatrizDerecha == 3) {

                    matrizSigmas[unPoint.x + 1][unPoint.y] = 1;
                    lOut.add(new Point(unPoint.x + 1, unPoint.y));
                }

                if (valorMatrizArriba == 3) {

                    matrizSigmas[unPoint.x][unPoint.y - 1] = 1;
                    lOut.add(new Point(unPoint.x, unPoint.y - 1));
                }

                if (valorMatrizAbajo == 3) {

                    matrizSigmas[unPoint.x][unPoint.y + 1] = 1;
                    lOut.add(new Point(unPoint.x, unPoint.y + 1));
                }

                lIn.add(new Point(unPoint.x, unPoint.y));
                matrizSigmas[unPoint.x][unPoint.y] = -1;
                lOut.remove(unPoint);
            }
        }
    }

    private static int calcularFd(int[] promedio, Bitmap imagen, Point posicion) {

        int fD = 1;

        boolean debeSeguir = sonColoresSimilares(imagen, Color.rgb(promedio[0], promedio[1], promedio[2]), posicion.x, posicion.y, diferenciaMaxima);

        if (!debeSeguir) {

            fD = -1;
        }

        return fD;
    }

    private static boolean sonColoresSimilares(Bitmap imagen, int colorPromedio, int i, int j, int diferencia) {

        int colorEnEsePunto = imagen.getPixel(i, j);

        boolean esSimilar = Math.abs(Color.red(colorEnEsePunto) - Color.red(colorPromedio)) <= diferencia
                && Math.abs(Color.green(colorEnEsePunto) - Color.green(colorPromedio)) <= diferencia
                && Math.abs(Color.blue(colorEnEsePunto) - Color.blue(colorPromedio)) <= diferencia;

        return esSimilar;
    }

    private static void contraer(Bitmap imagen, List<Point> lIn, List<Point> lOut, int[] promedio,
                          Iterator<Point> iteradorPuntosLin2) {
        Point unPoint = iteradorPuntosLin2.next();

        if (calcularFd(promedio, imagen, unPoint) < 0) {

            int valorMatrizIzquierda = matrizSigmas[unPoint.x - 1][unPoint.y];
            int valorMatrizDerecha = matrizSigmas[unPoint.x + 1][unPoint.y];
            int valorMatrizArriba = matrizSigmas[unPoint.x][unPoint.y - 1];
            int valorMatrizAbajo = matrizSigmas[unPoint.x][unPoint.y + 1];

            if (valorMatrizIzquierda == -3) {
                matrizSigmas[unPoint.x - 1][unPoint.y] = -1;
                lIn.add(new Point(unPoint.x - 1, unPoint.y));
            }

            if (valorMatrizDerecha == -3) {
                matrizSigmas[unPoint.x + 1][unPoint.y] = -1;
                lIn.add(new Point(unPoint.x + 1, unPoint.y));
            }

            if (valorMatrizArriba == -3) {
                matrizSigmas[unPoint.x][unPoint.y - 1] = -1;
                lIn.add(new Point(unPoint.x, unPoint.y - 1));
            }

            if (valorMatrizAbajo == -3) {
                matrizSigmas[unPoint.x][unPoint.y + 1] = -1;
                lIn.add(new Point(unPoint.x, unPoint.y + 1));
            }

            matrizSigmas[unPoint.x][unPoint.y] = 1;
            lOut.add(unPoint);
            lIn.remove(unPoint);
        }
    }

    private static void sacarLinNoCorrespondientes(List<Point> lIn, Iterator<Point> iteradorPuntosLin) {
        Point unPoint = iteradorPuntosLin.next();

        int valorMatrizIzquierda = matrizSigmas[unPoint.x - 1][unPoint.y];
        int valorMatrizDerecha = matrizSigmas[unPoint.x + 1][unPoint.y];
        int valorMatrizArriba = matrizSigmas[unPoint.x][unPoint.y - 1];
        int valorMatrizAbajo = matrizSigmas[unPoint.x][unPoint.y + 1];

        if (valorMatrizIzquierda < 0 && valorMatrizDerecha < 0 && valorMatrizArriba < 0 && valorMatrizAbajo < 0) {

            matrizSigmas[unPoint.x][unPoint.y] = -3;
            lIn.remove(unPoint);

        }
    }

    private static void sacarLoutNoCorrespondientes(List<Point> lOut, Iterator<Point> iteradorPuntosLout2,
                                             Bitmap imagen) {
        Point unPoint = iteradorPuntosLout2.next();

        if (unPoint.x > 0 && unPoint.y < imagen.getHeight() - 1 && unPoint.y > 0 && unPoint.x < imagen.getWidth() - 1) {
            int valorMatrizIzquierda = matrizSigmas[unPoint.x - 1][unPoint.y];
            int valorMatrizDerecha = matrizSigmas[unPoint.x + 1][unPoint.y];
            int valorMatrizArriba = matrizSigmas[unPoint.x][unPoint.y - 1];
            int valorMatrizAbajo = matrizSigmas[unPoint.x][unPoint.y + 1];
            if (valorMatrizIzquierda > 0 && valorMatrizDerecha > 0 && valorMatrizArriba > 0 && valorMatrizAbajo > 0) {
                matrizSigmas[unPoint.x][unPoint.y] = 3;
                lOut.remove(unPoint);
            }
        }
    }

    private static boolean estaAdentro(int i, int j, int desdeX, int hastaX, int desdeY, int hastaY) {
        boolean estaAdentro = false;
        if (i > desdeX && i < hastaX && j > desdeY && j < hastaY) {
            estaAdentro = true;
        }

        return estaAdentro;
    }

    private static boolean estaAlRededorDelBorde(int i, int j, int desdeX, int hastaX, int desdeY, int hastaY) {

        boolean estaAlrededor = false;
        if (j + 1 == desdeY && i <= hastaX && i >= desdeX) {
            estaAlrededor = true;
        } else if (i + 1 == desdeX && j >= desdeY && j <= hastaY) {
            estaAlrededor = true;
        } else if (i - 1 == hastaX && j >= desdeY && j <= hastaY) {
            estaAlrededor = true;
        } else if (j - 1 == hastaY && i >= desdeX && i <= hastaX) {
            estaAlrededor = true;
        }

        return estaAlrededor;
    }

    private static boolean estaEnBorde(int i, int j, int desdeX, int hastaX, int desdeY, int hastaY) {

        boolean estaEnBorde = false;

        if (j == desdeY && i <= hastaX && i >= desdeX) {
            estaEnBorde = true;
        } else if (i == desdeX && j >= desdeY && j <= hastaY) {
            estaEnBorde = true;
        } else if (i == hastaX && j >= desdeY && j <= hastaY) {
            estaEnBorde = true;
        } else if (j == hastaY && i >= desdeX && i <= hastaX) {
            estaEnBorde = true;
        }

        return estaEnBorde;
    }

    private static int[] obtenerPromedioRGB(Bitmap imagen, Point punto1, Point punto2) {

        int rojoAcumulado = 0;
        int verdeAcumulado = 0;
        int azulAcumulado = 0;

        int contadorCantPixel = 0;

        int promedioRojo = 0;
        int promedioVerde = 0;
        int promedioAzul = 0;

        int primerPuntoX = punto1.x;
        int primerPuntoY = punto1.y;
        int segundoPuntoX = punto2.x;
        int segundoPuntoY = punto2.y;

        int desdeX = 0;
        int hastaX = 0;
        int desdeY = 0;
        int hastaY = 0;

        if (primerPuntoX <= segundoPuntoX) {
            desdeX = primerPuntoX;
            hastaX = segundoPuntoX;
        } else {
            desdeX = segundoPuntoX;
            hastaX = primerPuntoX;
        }

        if (primerPuntoY <= segundoPuntoY) {
            desdeY = primerPuntoY;
            hastaY = segundoPuntoY;
        } else {
            desdeY = segundoPuntoY;
            hastaY = primerPuntoY;
        }

        for (int i = desdeX + 1; i < hastaX - 1; i++) {
            for (int j = desdeY + 1; j < hastaY - 1; j++) {

                int pixel = imagen.getPixel(i, j);
                int blue = Color.blue(pixel);
                int green = Color.green(pixel);
                int red = Color.red(pixel);

                rojoAcumulado += red;
                verdeAcumulado += green;
                azulAcumulado += blue;

                contadorCantPixel++;
            }
        }

        promedioRojo = (int) (rojoAcumulado / contadorCantPixel);
        promedioVerde = (int) (verdeAcumulado / contadorCantPixel);
        promedioAzul = (int) (azulAcumulado / contadorCantPixel);

        int[] valoresPromedio = new int[3];
        valoresPromedio[0] = promedioRojo;
        valoresPromedio[1] = promedioVerde;
        valoresPromedio[2] = promedioAzul;

        return valoresPromedio;
    }

    private static Curva obtenerCurvaDesdePuntosSeleccionados(int primerPuntoX, int primerPuntoY, int segundoPuntoX,
                                                              int segundoPuntoY) {

        int desdeX = 0;
        int hastaX = 0;
        int desdeY = 0;
        int hastaY = 0;

        if (primerPuntoX <= segundoPuntoX) {
            desdeX = primerPuntoX;
            hastaX = segundoPuntoX;
        } else {
            desdeX = segundoPuntoX;
            hastaX = primerPuntoX;
        }

        if (primerPuntoY <= segundoPuntoY) {
            desdeY = primerPuntoY;
            hastaY = segundoPuntoY;
        } else {
            desdeY = segundoPuntoY;
            hastaY = primerPuntoY;
        }

        Point desde = new Point(desdeX, desdeY);
        Point hasta = new Point(hastaX, hastaY);

        return new Curva(desde, hasta);
    }

    public static Bitmap segmentarImagen(Bitmap imagen) {

        for (int i = 0; i < 50; i++) {

            Iterator<Point> iteradorPuntos = lOut.iterator();
            while (iteradorPuntos.hasNext()) {

                expandir(imagen,lIn, lOut, promedioDeColores, iteradorPuntos);
            }

            Iterator<Point> iteradorPuntosLin = lIn.iterator();
            while (iteradorPuntosLin.hasNext()) {

                sacarLinNoCorrespondientes(lIn, iteradorPuntosLin);
            }

            Iterator<Point> iteradorPuntosLin2 = lIn.iterator();
            while (iteradorPuntosLin2.hasNext()) {

                contraer(imagen, lIn, lOut, promedioDeColores, iteradorPuntosLin2);
            }

            Iterator<Point> iteradorPuntosLout2 = lOut.iterator();
            while (iteradorPuntosLout2.hasNext()) {

                sacarLoutNoCorrespondientes(lOut,
                        iteradorPuntosLout2, imagen);
            }
        }

        Bitmap imagenCopia = imagen.copy(Bitmap.Config.RGB_565, true);

        for (Point unPoint : lIn) {
            imagenCopia.setPixel(unPoint.x, unPoint.y, Color.GREEN);
        }

        return imagenCopia;
    }
}
