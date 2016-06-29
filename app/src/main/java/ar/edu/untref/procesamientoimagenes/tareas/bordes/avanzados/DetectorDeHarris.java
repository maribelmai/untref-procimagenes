package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados;

import android.graphics.Bitmap;
import android.graphics.Color;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadUmbral;
import ar.edu.untref.procesamientoimagenes.tareas.filtros.FiltroGaussiano;

/**
 * Created by maribelmai on 29/6/16.
 */

public class DetectorDeHarris {

    private Bitmap imagenOriginal;

    private int ancho;
    private int alto;

    public DetectorDeHarris(Bitmap imagen){
        this.imagenOriginal = imagen;
        this.ancho = imagenOriginal.getWidth();
        this.alto = imagenOriginal.getHeight();
    }

    public Bitmap detectarEsquinas(){

        //Calculo x e y con sobel
        int mascaraX[][] = { { 1, 0, -1 }, { 2, 0, -2 }, { 1, 0, -1 } };
        int mascaraY[][] = { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };

        int[][] matrizImagenSobelX = aplicarMascara(mascaraX);
        int[][] matrizImagenSobelY  = aplicarMascara(mascaraY);

        //Elevo punto a punto al cuadrado
        int[][] grisesEnXCuadrado = aplicarTransformacionLineal(elevarAlCuadrado(matrizImagenSobelX));
        int[][] grisesEnYCuadrado = aplicarTransformacionLineal(elevarAlCuadrado(matrizImagenSobelY));

        //Para cada una de las dos matrices al cuadrado paso Gaussiano con sigma 2
        Bitmap imagenConFiltroGaussEnX = FiltroGaussiano.aplicarFiltroGaussiano(convertirMatrizEnImagen(
                grisesEnXCuadrado,ancho, alto), 2);
        Bitmap imagenConFiltroGaussEnY = FiltroGaussiano.aplicarFiltroGaussiano(convertirMatrizEnImagen(
                grisesEnYCuadrado, ancho, alto), 2);

        int [][] matrizImagenFiltroGaussEnX = convertirImagenEnMatriz(imagenConFiltroGaussEnX);
        int [][] matrizImagenFiltroGaussEnY = convertirImagenEnMatriz(imagenConFiltroGaussEnY);

        //Multiplico las dos y paso filtro gaussiano otra vez.
        int[][] resultadoMultiplicacion = multiplicarValores(matrizImagenFiltroGaussEnX, matrizImagenFiltroGaussEnY);
        int[][] grisesXY = aplicarTransformacionLineal(resultadoMultiplicacion);

        Bitmap imagenXY = convertirMatrizEnImagen(grisesXY, ancho, alto);
        Bitmap imagenXYConFiltroGauss = FiltroGaussiano.aplicarFiltroGaussiano(imagenXY, 2);

        //Paso 4: con k=0.04 Calcular: cim1 = (Ix2*Iy2 - Ixy^2) - k*(Ix2 + Iy2)^2
        int[][] cimGrises = aplicarTransformacionLineal(calcularCim(matrizImagenFiltroGaussEnX, matrizImagenFiltroGaussEnY, convertirImagenEnMatriz(imagenXYConFiltroGauss)));

        //Umbralizo con OTSU
        ActividadUmbral actividadUmbral = new ActividadUmbral();
        int umbralizarOtsuABitmap = actividadUmbral.umbralizarOtsuABitmap(convertirMatrizEnImagen(cimGrises, ancho, alto));
        Bitmap imagenUmbralizada = actividadUmbral.obtenerBitmapUmbralizado(convertirMatrizEnImagen(cimGrises, ancho, alto), umbralizarOtsuABitmap);

        return superponerAImagenOriginal(imagenUmbralizada, imagenOriginal);
    }

    private Bitmap superponerAImagenOriginal(Bitmap umbralizada, Bitmap original) {

        int resaltado = Color.MAGENTA;
        Bitmap imagenFinal = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

        for (int i=0; i< ancho; i++){
            for (int j=0; j< alto; j++){

                if (Color.red(umbralizada.getPixel(i,j)) == 255){

                    imagenFinal.setPixel(i, j, resaltado);
                } else {

                    imagenFinal.setPixel(i, j, original.getPixel(i, j));
                }
            }
        }

        return imagenFinal;
    }

    /**
     * cim1 = (Ix2*Iy2 - Ixy^2) - k*(Ix2 + Iy2)^2  con k=0.04
     */
    private int[][] calcularCim(int[][] Ix2, int[][] Iy2, int[][] Ixy2) {

        int ancho = this.ancho;
        int alto = this.alto;
        int[][] matrizCim = new int[ancho][alto];

        for (int f = 0; f < ancho; f++) {
            for (int g = 0; g < alto; g++) {

                matrizCim[f][g] = (int) (((Ix2[f][g]*Iy2[f][g]) - Ixy2[f][g]) - (0.04 * Math.pow((Ix2[f][g] + Iy2[f][g]),2)));
            }
        }

        return matrizCim;
    }

    public int[][] aplicarMascara(int mascara[][]) {

        int anchoMascara = 3;
        int altoMascara = 3;
        int sumarEnAncho = (-1) * (anchoMascara / 2);
        int sumarEnAlto = (-1) * (altoMascara / 2);
        int[][] matriz = new int[ancho][alto];

        for (int i = anchoMascara / 2; i < ancho - (anchoMascara / 2); i++) {
            for (int j = altoMascara / 2; j < alto - (altoMascara / 2); j++) {

                int sumatoriaX = 0;
                for (int iAnchoMascara = 0; iAnchoMascara < anchoMascara; iAnchoMascara++) {
                    for (int iAltoMascara = 0; iAltoMascara < altoMascara; iAltoMascara++) {

                        int indiceIDeLaImagen = i + sumarEnAncho + iAnchoMascara;
                        int indiceJDeLaImagen = j + sumarEnAlto + iAltoMascara;

                        double nivelDeRojo = Color.red(imagenOriginal.getPixel(indiceIDeLaImagen, indiceJDeLaImagen));
                        sumatoriaX += nivelDeRojo * mascara[iAnchoMascara][iAltoMascara];
                    }
                }

                matriz[i][j] = sumatoriaX;
            }
        }
        return matriz;
    }

    public int[][] elevarAlCuadrado(int[][] matriz) {

        for (int f = 0; f < ancho; f++) {
            for (int g = 0; g < alto; g++) {

                matriz[f][g] = (int) Math.pow(matriz[f][g],2);
            }
        }

        return matriz;
    }

    public int[][] aplicarTransformacionLineal(int[][] matrizDesfasada) {

        float minimo;
        float maximo;

        int[][] matrizTransformada;

        matrizTransformada = new int[ancho][alto];

        minimo = 0;
        maximo = 255;

        for (int f = 0; f < ancho; f++) {
            for (int g = 0; g < alto; g++) {

                int valorActual = matrizDesfasada[f][g];

                if (minimo > valorActual) {
                    minimo = valorActual;
                }

                if (maximo < valorActual) {
                    maximo = valorActual;
                }

            }

        }

        float[] maximosYMinimos = new float[2];
        maximosYMinimos[0] = minimo;
        maximosYMinimos[1] = maximo;

        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {

                int valorActual = matrizDesfasada[i][j];
                int valorTransformado = (int) ((((255f) / (maximo - minimo)) * valorActual) - ((minimo * 255f) / (maximo - minimo)));

                matrizTransformada[i][j] = valorTransformado;
            }
        }

        return matrizTransformada;
    }

    public int[][] multiplicarValores(int[][] matrizRojoEnX, int[][] matrizRojoEnY) {

        int[][] matrizResultado = new int[ancho][alto];

        for (int f = 0; f < ancho; f++) {
            for (int g = 0; g < alto; g++) {

                matrizResultado[f][g] = matrizRojoEnX[f][g]*matrizRojoEnY[f][g];
            }
        }

        return matrizResultado;
    }

    public Bitmap getImagenOriginal() {
        return imagenOriginal;
    }

    public void setImagenOriginal(Bitmap imagenOriginal) {
        this.imagenOriginal = imagenOriginal;
    }

    public static Bitmap convertirMatrizEnImagen(int[][] matriz, int ancho, int alto){

        Bitmap bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {

                int pixel=matriz[i][j];
                bitmap.setPixel(i, j, Color.rgb(pixel,pixel,pixel));
            }
        }
        return bitmap;
    }

    public static int[][] convertirImagenEnMatriz(Bitmap image) {

        int[][] matriz = new int[image.getWidth()][image.getHeight()];

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {

                matriz[i][j] = Color.red(image.getPixel(i, j));
            }
        }
        return matriz;
    }
}