package ar.edu.untref.procesamientoimagenes.tareas.filtros;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by maribelmai on 20/6/16.
 */
public class FiltroGaussiano {

    private static int[] dimensionesDeMatricesPosibles = { 3, 5, 9, 13, 15, 19 };

    public static Bitmap aplicarFiltroGaussiano(Bitmap bitmapOriginal, int sigma) {

        float[][] mascara = generarMascaraGaussiana(sigma);

        Bitmap imagenFiltrada = Bitmap.createBitmap(bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), Bitmap.Config.RGB_565);

        int width = mascara.length;
        int height = mascara[0].length;
        int posicionCentralMascara = width/2;

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado
                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        double valorMascara = mascara[xMascara][yMascara];
                        int nivelGrisPixel = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));

                        double operacion = nivelGrisPixel * valorMascara;
                        valorResultado += operacion;
                    }
                }

                int valorEntero = (int) valorResultado;
                imagenFiltrada.setPixel(x,y, Color.rgb(valorEntero, valorEntero, valorEntero));
            }
        }

//        //Creamos el filtro - Se pasa de una matriz cuadrada (vector de 2 dimensiones) a un vector lineal
//        for(int i=0; i < width; i++){
//            for(int j=0; j < height; j++){
//                filtroK[i*width + j] = mascara[i][j];
//            }
//        }
//
//        Kernel kernel = new Kernel(width, height, filtroK);
//        Filtro filtro = new Filtro(kernel);
//
//        //Aplicamos el filtro
//        filtro.filter(imagenOriginal, imagenFiltrada);

        return imagenFiltrada;
    }

    private static float[][] generarMascaraGaussiana(int sigma) {

        // Siempre son matrices cuadradas
        //int dimension = dimensionesDeMatricesPosibles[sigma-1];



        int auxTamanio = (int) (2 * Math.sqrt(2D) * sigma);
        int posibleTamanio = auxTamanio % 2 == 0 ? auxTamanio : auxTamanio + 1;
        int dimension = sigma < 1 ? 5 : posibleTamanio + 1;float[][] mascara = new float[dimension][dimension];

        for (int j = 0; j < dimension; ++j) {
            for (int i = 0; i < dimension; ++i) {
                mascara[i][j] = calcularValorGaussiano(sigma, i - (dimension/2), j - (dimension/2));
            }
        }

        return mascara;
    }

    private static float calcularValorGaussiano(int sigma, int x, int y) {
        float valor = (float) ((1 / (2 * Math.PI * sigma * sigma))
                *
                Math.pow(Math.E,-(x * x + y * y) / (2 * sigma * sigma)));

        return valor;
    }

}