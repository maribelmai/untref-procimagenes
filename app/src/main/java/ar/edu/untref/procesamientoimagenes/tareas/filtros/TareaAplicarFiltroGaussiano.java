package ar.edu.untref.procesamientoimagenes.tareas.filtros;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadFiltros;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarFiltroGaussiano extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarFiltroGaussiano.class.getSimpleName();
    private ActividadFiltros actividadFiltros;
    private Bitmap bitmapOriginal;
    private Float sigma;

    public TareaAplicarFiltroGaussiano(ActividadFiltros actividadFiltros, Bitmap bitmapOriginal, Float sigma) {

        this.actividadFiltros = actividadFiltros;
        this.bitmapOriginal = bitmapOriginal;
        this.sigma = sigma;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        Bitmap mutableBitmap = ejecutar();

        return mutableBitmap;
    }

    public Bitmap ejecutar() {

        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        int auxTamanio = (int) (2 * Math.sqrt(2D) * sigma);
        int posibleTamanio = auxTamanio % 2 == 0 ? auxTamanio : auxTamanio + 1;
        int tamanioMascara = sigma < 1 ? 5 : posibleTamanio + 1;

        double[][] matrizFiltroGaussiano = new double[tamanioMascara][tamanioMascara];
        int posicionCentralMascara = tamanioMascara/2;

        for (int x = 0; x < tamanioMascara; x++) {

            for (int y = 0; y < tamanioMascara; y++) {
                matrizFiltroGaussiano[x][y] = obtenerGaussiano(x,y, posicionCentralMascara);
                System.out.print(matrizFiltroGaussiano[x][y]);
            }
            System.out.println("");
        }

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado
                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        double valorMascara = matrizFiltroGaussiano[xMascara][yMascara];
                        int nivelGrisPixel = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));

                        double operacion = nivelGrisPixel * valorMascara;
                        valorResultado += operacion;
                    }
                }

                int valorEntero = (int) valorResultado;
                mutableBitmap.setPixel(x,y, Color.rgb(valorEntero, valorEntero, valorEntero));
            }
        }

//        //Pinto los bordes negros
//        for (int x = 0; x < bitmapOriginal.getWidth(); x++) {
//            for (int y = 0 ; y < bitmapOriginal.getHeight(); y ++) {
//
//                if (x < posicionCentralMascara || x >= bitmapOriginal.getWidth() - posicionCentralMascara
//                        || y < posicionCentralMascara || y >= bitmapOriginal.getHeight() - posicionCentralMascara) {
//                    mutableBitmap.setPixel(x,y, Color.rgb(0, 0, 0));
//                }
//            }
//        }

        return mutableBitmap;
    }

    private double obtenerGaussiano(int x, int y, int posicionCentralMascara) {

        int xEnMascara = x-posicionCentralMascara;
        int yEnMascara = y-posicionCentralMascara;
        return (1/
                (2 * Math.PI * Math.pow(sigma,2)))
                * Math.exp(-((Math.pow(xEnMascara,2) + Math.pow(yEnMascara,2))/(2 * Math.pow(sigma,2)))) ;
    }

    @Override
    protected void onPostExecute(Bitmap bitmapResultante) {
        super.onPostExecute(bitmapResultante);

        if (actividadFiltros != null) {
            actividadFiltros.filtroAplicado(bitmapResultante);
        }
    }





//    /**
//     * @return imagen binaria que detecta los bordes y esquinas de la imagen original
//     */
//    public Bitmap aplicarSusanBorde(Bitmap imagenOriginal, String flagDetector) {
//
//        BufferedImage imagenResultado = new BufferedImage(imagenOriginal.getWidth(), imagenOriginal.getHeight(), imagenOriginal.getType());
//
//        int sumarEnAncho = (-1) * (TAMANIO_MASCARA / 2);
//        int sumarEnAlto = (-1) * (TAMANIO_MASCARA / 2);
//
//        // Iterar la imagen, sacando los bordes.
//        for (int i = TAMANIO_MASCARA / 2; i < imagenResultado.getWidth() - (TAMANIO_MASCARA / 2); i++) {
//            for (int j = TAMANIO_MASCARA / 2; j < imagenResultado.getHeight() - (TAMANIO_MASCARA / 2); j++) {
//
//                // Tomo el valor del píxel central de la máscara (el (3,3) de la máscara)
//                int indiceICentralDeLaImagen = i + sumarEnAncho + (TAMANIO_MASCARA / 2);
//                int indiceJCentralDeLaImagen = j + sumarEnAlto + (TAMANIO_MASCARA / 2);
//                double valorCentral = new Color(imagenOriginal.getRGB(indiceICentralDeLaImagen, indiceJCentralDeLaImagen)).getRed();
//
//                int cantidadDePixelesSimilaresAlCentral = 0;
//
//                // Iterar la máscara
//                for(int iAnchoMascara = 0; iAnchoMascara < TAMANIO_MASCARA; iAnchoMascara++) {
//                    for(int iAltoMascara = 0; iAltoMascara < TAMANIO_MASCARA; iAltoMascara++) {
//
//                        int indiceIDeLaImagen = i + sumarEnAncho + iAnchoMascara;
//                        int indiceJDeLaImagen = j + sumarEnAlto + iAltoMascara;
//
//                        double valor = new Color(imagenOriginal.getRGB(indiceIDeLaImagen, indiceJDeLaImagen)).getRed();
//
//                        // Se multiplica el valor leído por la máscara, para sacar los que no pertenezcan a la parte circular.
//                        valor = valor * mascara[iAnchoMascara][iAltoMascara];
//
//                        if (Math.abs(valor - valorCentral) < umbralT) {
//
//                            cantidadDePixelesSimilaresAlCentral++;
//                        }
//                    }
//                }
//                // Fin iteración máscara
//
//                double Sr0 = 1.0 - ((double)cantidadDePixelesSimilaresAlCentral / (double)CANTIDAD_PIXELES_MASCARA);
//
//
//                switch (flagDetector) {
//
//                    case "E":
//                        if(Math.abs( Sr0 - criterioDeEsquina) < 0.2){
//
//                            imagenResultado.setRGB(i, j, pixelVerde);
//                        } else {
//
//                            imagenResultado.setRGB(i, j, pixelNegro);
//                        }
//                        break;
//
//                    case "B":
//                        if(Math.abs( Sr0 - criterioDeBorde) < 0.1){
//
//                            imagenOriginal.setRGB(i, j, pixelVerde);
//                        } else {
//
//// 	 					imagenResultado.setRGB(i, j, pixelNegro);
//                        }
//                        break;
//
//                    case "BE":
//                        if(Math.abs( Sr0 - criterioDeEsquina) < 0.1 || Math.abs( Sr0 - criterioDeBorde) < 0.1){
//
//                            imagenResultado.setRGB(i, j, pixelVerde);
//                        } else {
//
//                            imagenResultado.setRGB(i, j, pixelNegro);
//                        }
//                        break;
//
//                    case "EB":
//                        if(Math.abs( Sr0 - criterioDeEsquina) < 0.1 || Math.abs( Sr0 - criterioDeBorde) < 0.1){
//
//                            imagenResultado.setRGB(i, j, pixelVerde);
//                        } else {
//
//                            imagenResultado.setRGB(i, j, pixelNegro);
//                        }
//                        break;
//                }
//            }
//        }
//        return imagenOriginal;
//    }
}
