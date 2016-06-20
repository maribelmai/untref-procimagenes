package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordesAvanzados;
import ar.edu.untref.procesamientoimagenes.tareas.filtros.FiltroGaussiano;
import ar.edu.untref.procesamientoimagenes.util.Operacion;

/**
 * Created by maribelmai on 8/6/16.
 */
public class TareaDetectarBordesCanny extends AsyncTask<Void, Bitmap, Bitmap> {

    private static final String TAG = TareaDetectarBordesCanny.class.getSimpleName();
    private Bitmap bitmap;
    private ActividadBordesAvanzados actividadBordesAvanzados;
    private float sigma1;
    private float sigma2;
    private float sigma3;
    private float umbral1;
    private float umbral2;
    private Progreso progreso;

    public TareaDetectarBordesCanny(Bitmap bitmap, ActividadBordesAvanzados actividadBordesAvanzados, float sigma1, float sigma2, float sigma3, float umbral1, float umbral2) {

        this.bitmap = bitmap;
        this.actividadBordesAvanzados = actividadBordesAvanzados;
        this.sigma1 = sigma1;
        this.sigma2 = sigma2;
        this.sigma3 = sigma3;
        this.umbral1 = umbral1;
        this.umbral2 = umbral2;

        this.sigma1 = 1;
        this.sigma2 = 3;
        this.sigma3 = 10;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

//        DetectorCanny detectorCanny = new DetectorCanny();
//        detectorCanny.setSourceImage(this.bitmap);
//        detectorCanny.setLowThreshold(umbral1);
//        detectorCanny.setHighThreshold(umbral2);
//        detectorCanny.process();
//        return detectorCanny.getEdgesImage();

//        Bitmap bitmap1 = new TareaAplicarFiltroGaussiano(null, this.bitmap, sigma1).ejecutar();
        Bitmap bitmap1 = FiltroGaussiano.aplicarFiltroGaussiano(bitmap, 1);

        progreso = Progreso.GAUSS_1_CALCULADO;
        publishProgress(bitmap1);
//        Bitmap bitmap2 = new TareaAplicarFiltroGaussiano(null, this.bitmap, sigma2).ejecutar();

        Bitmap bitmap2 = FiltroGaussiano.aplicarFiltroGaussiano(bitmap, 3);
        progreso = Progreso.GAUSS_2_CALCULADO;
        publishProgress(bitmap2);
//        Bitmap bitmap3 = new TareaAplicarFiltroGaussiano(null, this.bitmap, sigma3).ejecutar();
        Bitmap bitmap3 = FiltroGaussiano.aplicarFiltroGaussiano(bitmap, 10);

        progreso = Progreso.GAUSS_3_CALCULADO;
        publishProgress(bitmap3);

        int[][] matrizResultado1 = obtenerMatrizCanny(bitmap1, umbral1, umbral2);

        progreso = Progreso.CANNY_1_CALCULADO;
        Bitmap canny1Bitmap = Operacion.umbralizar(matrizResultado1);
        publishProgress(canny1Bitmap);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int[][] matrizResultado2 = obtenerMatrizCanny(bitmap2, umbral1, umbral2);

        progreso = Progreso.CANNY_2_CALCULADO;
        Bitmap canny2Bitmap = Operacion.umbralizar(matrizResultado2);
        publishProgress(canny2Bitmap);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int[][] matrizResultado3 = obtenerMatrizCanny(bitmap3, umbral1, umbral2);

        progreso = Progreso.CANNY_3_CALCULADO;
        Bitmap canny3Bitmap = Operacion.umbralizar(matrizResultado3);
        publishProgress(canny3Bitmap);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int[][] matrizResultadoFinal = new int[this.bitmap.getWidth()][this.bitmap.getHeight()];

        for (int i = 0; i < matrizResultadoFinal.length; i++) {
            for (int j = 0; j < matrizResultadoFinal[0].length; j++) {
                int resultado1 = matrizResultado1[i][j];
                int resultado2 = matrizResultado2[i][j];
                int resultado3 = matrizResultado3[i][j];

//                if (resultado1 == 255 && resultado2 == 255 && resultado3 == 255) {
//                    matrizResultadoFinal[i][j] = 1;
//                }
                if(resultado1 > resultado2 && resultado1 > resultado3){
                    matrizResultadoFinal [i][j] = resultado1;

                }
                else if(resultado2 > resultado3 && resultado2 > resultado1){
                    matrizResultadoFinal [i][j] = resultado2;
                }
                else {
                    matrizResultadoFinal [i][j] = resultado3;
                }
            }
        }

        Bitmap imagenResultado = Operacion.umbralizar(matrizResultadoFinal);

        return imagenResultado;

//        Bitmap nuevoBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
//
//        try {
//            Bitmap bitmap1 = new TareaAplicarFiltroGaussiano(null, this.bitmap, sigma1).ejecutar();
//            progreso = Progreso.GAUSS_1_CALCULADO;
//            publishProgress(bitmap1);
//            Bitmap bitmap2 = new TareaAplicarFiltroGaussiano(null, this.bitmap, sigma2).ejecutar();
//            progreso = Progreso.GAUSS_2_CALCULADO;
//            publishProgress(bitmap2);
//            Bitmap bitmap3 = new TareaAplicarFiltroGaussiano(null, this.bitmap, sigma3).ejecutar();
//            progreso = Progreso.GAUSS_3_CALCULADO;
//            publishProgress(bitmap3);
//
//            int[][] bitmap1GHorizontal = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap1.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.HORIZONTAL), TipoImagen.GRIS);
//            int[][] bitmap1GVertical = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap1.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.VERTICAL), TipoImagen.GRIS);
//            int[][] matrizMagnitudGradiente1 = Operacion.obtenerMatrizMagnitudGradiente(bitmap1GHorizontal, bitmap1GVertical);
//
//            int[][] bitmap2GHorizontal = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap2.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.HORIZONTAL), TipoImagen.GRIS);
//            int[][] bitmap2GVertical = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap2.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.VERTICAL), TipoImagen.GRIS);
//            int[][] matrizMagnitudGradiente2 = Operacion.obtenerMatrizMagnitudGradiente(bitmap2GHorizontal, bitmap2GVertical);
//
//            int[][] bitmap3GHorizontal = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap3.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.HORIZONTAL), TipoImagen.GRIS);
//            int[][] bitmap3GVertical = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap3.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.VERTICAL), TipoImagen.GRIS);
//            int[][] matrizMagnitudGradiente3 = Operacion.obtenerMatrizMagnitudGradiente(bitmap3GHorizontal, bitmap3GVertical);
//
//            int[][] angulosBitmap1 = obtenerAngulos(bitmap1GHorizontal, bitmap1GVertical);
//            int[][] angulosBitmap2 = obtenerAngulos(bitmap2GHorizontal, bitmap2GVertical);
//            int[][] angulosBitmap3 = obtenerAngulos(bitmap3GHorizontal, bitmap3GVertical);
//
//            int[][] canny1 = obtenerCanny(bitmap1, matrizMagnitudGradiente1, angulosBitmap1);
//            progreso = Progreso.CANNY_1_CALCULADO;
//            Bitmap canny1Bitmap = Operacion.umbralizar(canny1);
//            publishProgress(canny1Bitmap);
//
//            Thread.sleep(1000);
//
//            int[][] canny2 = obtenerCanny(bitmap2, matrizMagnitudGradiente2, angulosBitmap2);
//            progreso = Progreso.CANNY_2_CALCULADO;
//            Bitmap canny2Bitmap = Operacion.umbralizar(canny2);
//            publishProgress(canny2Bitmap);
//
//            Thread.sleep(1000);
//
//            int[][] canny3 = obtenerCanny(bitmap3, matrizMagnitudGradiente3, angulosBitmap3);
//            progreso = Progreso.CANNY_3_CALCULADO;
//            Bitmap canny3Bitmap = Operacion.umbralizar(canny3);
//            publishProgress(canny3Bitmap);
//
//            Thread.sleep(1000);
//
//            int[][] cannyFinal = obtenerCannyFinal(canny1, canny2, canny3);
//            progreso = Progreso.CANNY_FINAL_CALCULADO;
//            nuevoBitmap = Operacion.umbralizar(cannyFinal);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return nuevoBitmap;
    }

    public static int [][] obtenerMatrizCanny(Bitmap imagen, float umbral1, float umbral2){

        int mascaraX[][] = { { 1, 0, -1 }, { 2, 0, -2 }, { 1, 0, -1 } };
        int mascaraY[][] = { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };

        int[][] matrizImagenSobelX = aplicarMascara(imagen, mascaraX);
        int[][] matrizImagenSobelY  = aplicarMascara(imagen, mascaraY);

        int[][] matrizMagnitudDeBorde = sintetizar(matrizImagenSobelX, matrizImagenSobelY);
        int[][] matrizDeAngulos = obtenerAnguloDelGradiente(matrizImagenSobelX, matrizImagenSobelY);

        int[][] matrizSupresionNoMaximosSinTransformar = aplicarSupresionNoMaximos(matrizMagnitudDeBorde, matrizDeAngulos);

        int[][] matrizResultado = new int [imagen.getWidth()][imagen.getHeight()];

        for (int i = 1; i < matrizMagnitudDeBorde.length - 1; i++) {
            for (int j = 1; j < matrizMagnitudDeBorde[0].length - 1; j++) {
                if(matrizSupresionNoMaximosSinTransformar[i][j] > umbral2){
                    matrizResultado[i][j] = 1;
                }else if(matrizSupresionNoMaximosSinTransformar[i][j] < umbral1){
                    matrizResultado[i][j] = 0;
                }else{
                    if(matrizResultado[i-1][j] == 1 || matrizResultado[i+1][j] == 1 || matrizResultado[i][j+1] == 1 || matrizResultado[i][j-1] == 1){
                        matrizResultado[i][j] = 1;
                    }
                }
            }
        }
        return matrizResultado;
    }

    public static int[][] aplicarMascara(Bitmap image, int mascara[][]) {

        int anchoMascara = 3;
        int altoMascara = 3;
        int sumarEnAncho = (-1) * (anchoMascara / 2);
        int sumarEnAlto = (-1) * (altoMascara / 2);
        int[][] matriz = new int[image.getWidth()][image.getHeight()];

        // Iterar la imagen, sacando los bordes.
        for (int i = anchoMascara / 2; i < image.getWidth() - (anchoMascara / 2); i++) {
            for (int j = altoMascara / 2; j < image.getHeight() - (altoMascara / 2); j++) {

                int sumatoriaX = 0;
                // Iterar la máscara
                for (int iAnchoMascara = 0; iAnchoMascara < anchoMascara; iAnchoMascara++) {
                    for (int iAltoMascara = 0; iAltoMascara < altoMascara; iAltoMascara++) {

                        int indiceIDeLaImagen = i + sumarEnAncho + iAnchoMascara;
                        int indiceJDeLaImagen = j + sumarEnAlto + iAltoMascara;

                        double nivelDeRojo = Color.red(image.getPixel(indiceIDeLaImagen, indiceJDeLaImagen));
                        sumatoriaX += nivelDeRojo * mascara[iAnchoMascara][iAltoMascara];
                    }
                }

                matriz[i][j] = sumatoriaX;
            }
        }
        return matriz;
    }

    public static int[][] sintetizar(int[][] matrizEnX, int[][] matrizEnY) {

        int[][] matrizSitetizada  = new int[matrizEnX.length][matrizEnX[0].length];

        for (int i=0; i<matrizEnX.length ;i++){
            for (int j=0; j<matrizEnX[0].length ;j++){
                matrizSitetizada[i][j] = (int) Math.hypot(matrizEnX[i][j], matrizEnY[i][j]);
            }
        }
        return matrizSitetizada;
    }

    private static int[][] obtenerAnguloDelGradiente(int[][] matrizSobelX,int[][] matrizSobelY){

        int[][] matrizDeAngulos  = new int[matrizSobelX.length][matrizSobelX[0].length];

        for (int i=0; i<matrizSobelX.length ;i++){
            for (int j=0; j<matrizSobelX[0].length ;j++){

                float angulo = (float) Math.atan((float) matrizSobelY[i][j] / matrizSobelX[i][j]);
                double grados = Math.toDegrees(angulo);

                if ( grados < 0){
                    grados = 180 + grados;
                }

                matrizDeAngulos[i][j] = acomodarGrados(grados);
            }
        }

        return matrizDeAngulos;
    }

    private static int acomodarGrados(double grados) {

        int anguloFinal = 0;

        if ( grados >= 22.5 && grados <= 67.5 ){

            anguloFinal = 45;

        } else if ( grados > 67.5 && grados <= 112.5 ){

            anguloFinal = 90;

        } else if ( grados > 112.5 && grados <= 157.5 ){

            anguloFinal = 135;

        }

        return anguloFinal;
    }

    private static int[][] aplicarSupresionNoMaximos(int[][] matrizMagnitudDeBorde, int[][] matrizDeAngulos) {

        for (int i = 1; i < matrizMagnitudDeBorde.length - 1; i++) {
            for (int j = 1; j < matrizMagnitudDeBorde[0].length - 1; j++) {
                if(matrizMagnitudDeBorde[i][j] != 0){

                    int[] pixelesDireccionales = new int[2];
                    int magnitud = matrizMagnitudDeBorde[i][j];

                    if(matrizDeAngulos[i][j] == 0){

                        pixelesDireccionales = obtenerPixelesDeMagnitud0Grados(matrizMagnitudDeBorde,i,j);

                    }else if(matrizDeAngulos[i][j] == 45){

                        pixelesDireccionales = obtenerPixelesDeMagnitud45Grados(matrizMagnitudDeBorde,i,j);

                    }else if (matrizDeAngulos[i][j] == 90){

                        pixelesDireccionales = obtenerPixelesDeMagnitud90Grados(matrizMagnitudDeBorde,i,j);

                    }else if(matrizDeAngulos[i][j] == 135){

                        pixelesDireccionales = obtenerPixelesDeMagnitud135Grados(matrizMagnitudDeBorde,i,j);

                    }

                    if(pixelesDireccionales[0] > magnitud || pixelesDireccionales[1] > magnitud){

                        matrizMagnitudDeBorde[i][j] = 0;

                    }
                }
            }
        }

        return matrizMagnitudDeBorde;
    }

    private static int[] obtenerPixelesDeMagnitud135Grados(int[][] matrizMagnitudDeBorde, int i, int j) {
        int[] pixeles = new int[2];

        pixeles[0] = matrizMagnitudDeBorde[i-1][j+1];
        pixeles[1] = matrizMagnitudDeBorde[i+1][j-1];

        return pixeles;
    }

    private static int[] obtenerPixelesDeMagnitud90Grados(int[][] matrizMagnitudDeBorde, int i, int j) {

        int[] pixeles = new int[2];

        pixeles[0] = matrizMagnitudDeBorde[i][j-1];
        pixeles[1] = matrizMagnitudDeBorde[i][j+1];

        return pixeles;
    }

    private static int[] obtenerPixelesDeMagnitud45Grados(int[][] matrizMagnitudDeBorde, int i, int j) {

        int[] pixeles = new int[2];

        pixeles[0] = matrizMagnitudDeBorde[i+1][j-1];
        pixeles[1] = matrizMagnitudDeBorde[i-1][j+1];

        return pixeles;
    }

    private static int[] obtenerPixelesDeMagnitud0Grados(int[][] matrizMagnitudDeBorde, int i, int j) {

        int[] pixeles = new int[2];

        pixeles[0] = matrizMagnitudDeBorde[i-1][j];
        pixeles[1] = matrizMagnitudDeBorde[i+1][j];

        return pixeles;
    }
//    private int[][] obtenerCannyFinal(int[][] canny1, int[][] canny2, int[][] canny3) {
//
//        int ancho = canny1.length;
//        int alto = canny1[0].length;
//
//        int[][] cannyFinal = new int[ancho][alto];
//
//        for (int i = 0; i < ancho; i++) {
//            for (int j = 0; j < alto; j ++) {
//
//                if (canny1[i][j] == 1 && canny2[i][j] == 1 && canny3[i][j] == 1) {
//                    cannyFinal[i][j] = 1;
//                }
//            }
//        }
//
//        return cannyFinal;
//    }
//
//    private int[][] obtenerCanny(Bitmap bitmap, int[][] matrizMagnitudGradiente, int[][] angulosBitmap) {
//
////        for (int i = 1; i < matrizMagnitudDeBorde.length - 1; i++) {
////            for (int j = 1; j < matrizMagnitudDeBorde[0].length - 1; j++) {
////                if(matrizMagnitudDeBorde[i][j] != 0){
////
////                    int[] pixelesDireccionales = new int[2];
////                    int magnitud = matrizMagnitudDeBorde[i][j];
////
////                    if(matrizDeAngulos[i][j] == 0){
////
////                        pixelesDireccionales = obtenerPixelesDeMagnitud0Grados(matrizMagnitudDeBorde,i,j);
////
////                    }else if(matrizDeAngulos[i][j] == 45){
////
////                        pixelesDireccionales = obtenerPixelesDeMagnitud45Grados(matrizMagnitudDeBorde,i,j);
////
////                    }else if (matrizDeAngulos[i][j] == 90){
////
////                        pixelesDireccionales = obtenerPixelesDeMagnitud90Grados(matrizMagnitudDeBorde,i,j);
////
////                    }else if(matrizDeAngulos[i][j] == 135){
////
////                        pixelesDireccionales = obtenerPixelesDeMagnitud135Grados(matrizMagnitudDeBorde,i,j);
////
////                    }
////
////                    if(pixelesDireccionales[0] > magnitud || pixelesDireccionales[1] > magnitud){
////
////                        matrizMagnitudDeBorde[i][j] = 0;
////
////                    }
////                }
////            }
////        }
////
////        return matrizMagnitudDeBorde;
//
//
//
//
//        int t1 = 80;
//        int t2 = 100;
//
//        int[][] nuevaMagnitudGradiente = new int[bitmap.getWidth()][bitmap.getHeight()];
//        int[][] canny = new int[bitmap.getWidth()][bitmap.getHeight()];
//
//        for (int i = 0; i < bitmap.getWidth(); i++) {
//            for (int j = 0; j < bitmap.getHeight(); j++) {
//
//                int gradiente = matrizMagnitudGradiente[i][j];
//
//                //Es borde
//                if (Math.abs(gradiente) > 0) {
//
//                    DireccionBorde direccionBorde = DireccionBorde.obtenerDireccionBorde(angulosBitmap[i][j]);
//
//                    if (direccionBorde == DireccionBorde.HORIZONTAL) {
//
//                        int gradienteIzquierdo = 0;
//                        int gradienteDerecho = 0;
//
//                        if(i > 0) {
//                            gradienteIzquierdo = matrizMagnitudGradiente[i - 1][j];
//                        }
//
//                        if(i < bitmap.getWidth() - 1) {
//                            gradienteDerecho = matrizMagnitudGradiente[i + 1][j];
//                        }
//
//                        if (gradiente > gradienteIzquierdo && gradiente > gradienteDerecho) {
//                            canny[i][j] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradiente;
//                        }
//                        else if (gradienteDerecho > gradiente && gradienteDerecho > gradienteIzquierdo) {
//                            canny[i + 1][j] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradienteDerecho;
//                        }
//                        else if (gradienteIzquierdo > gradiente && gradienteIzquierdo > gradienteDerecho) {
//                            canny[i - 1][j] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradienteIzquierdo;
//                        }
//
//                    }
//                    else if (direccionBorde == DireccionBorde.VERTICAL) {
//
//                        int gradienteNorte = 0;
//                        int gradienteSur = 0;
//
//                        if(j > 0) {
//                            gradienteNorte = matrizMagnitudGradiente[i][j-1];
//                        }
//
//                        if(j < bitmap.getHeight() - 1) {
//                            gradienteSur = matrizMagnitudGradiente[i][j+1];
//                        }
//
//                        if (gradiente > gradienteNorte && gradiente > gradienteSur) {
//                            canny[i][j] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradiente;
//                        }
//                        else if (gradienteSur > gradiente && gradienteSur > gradienteNorte) {
//                            canny[i][j+1] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradienteSur;
//                        }
//                        else if (gradienteNorte > gradiente && gradienteNorte > gradienteSur) {
//                            canny[i][j-1] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradienteNorte;
//                        }
//                    }
//                    else if (direccionBorde == DireccionBorde.DIAGONAL_DERECHA) {
//
//                        int gradienteNorte = 0;
//                        int gradienteSur = 0;
//
//                        if(j > 0 && i < bitmap.getWidth() - 1) {
//                            gradienteNorte = matrizMagnitudGradiente[i+1][j-1];
//                        }
//
//                        if(i > 0 - 1 && j < bitmap.getHeight() - 1) {
//                            gradienteSur = matrizMagnitudGradiente[i-1][j+1];
//                        }
//
//                        if (gradiente > gradienteNorte && gradiente > gradienteSur) {
//                            canny[i][j] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradiente;
//                        }
//                        else if (gradienteSur > gradiente && gradienteSur > gradienteNorte) {
//                            canny[i-1][j+1] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradienteSur;
//                        }
//                        else if (gradienteNorte > gradiente && gradienteNorte > gradienteSur) {
//                            canny[i+1][j-1] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradienteNorte;
//                        }
//
//                    }
//                    else if (direccionBorde == DireccionBorde.DIAGONAL_IZQUIERDA) {
//
//                        int gradienteNorte = 0;
//                        int gradienteSur = 0;
//
//                        if(j > 0 && i > 0) {
//                            gradienteNorte = matrizMagnitudGradiente[i-1][j-1];
//                        }
//
//                        if(i < bitmap.getWidth() - 1 && j < bitmap.getHeight() - 1) {
//                            gradienteSur = matrizMagnitudGradiente[i+1][j+1];
//                        }
//
//                        if (gradiente > gradienteNorte && gradiente > gradienteSur) {
//                            canny[i][j] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradiente;
//                        }
//                        else if (gradienteSur > gradiente && gradienteSur > gradienteNorte) {
//                            canny[i-1][j-1] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradienteSur;
//                        }
//                        else if (gradienteNorte > gradiente && gradienteNorte > gradienteSur) {
//                            canny[i+1][j+1] = 1;
//                            nuevaMagnitudGradiente[i][j] = gradienteNorte;
//                        }
//                    }
//                }
//            }
//        }
//
//        //UMBRALIZACION CON HISTERESIS
//        for (int i = 0; i < bitmap.getWidth(); i++) {
//            for (int j = 0; j < bitmap.getHeight(); j++) {
//
//                int borde = canny[i][j];
//
//                if (borde == 1 && nuevaMagnitudGradiente[i][j] < t1) {
//                    canny[i][j] = 0;
//                }
//                else if (borde == 1 && nuevaMagnitudGradiente[i][j] > t1 && nuevaMagnitudGradiente[i][j] < t2) {
//
//                    boolean tieneBordesVecinos = false;
//
//                    if (i > 0) {
//                        tieneBordesVecinos = canny[i-1][j] == 1;
//
//                        if (!tieneBordesVecinos && j > 0) {
//                            tieneBordesVecinos = canny[i-1][j-1] == 1;
//                        }
//                        if (!tieneBordesVecinos && j < bitmap.getHeight() - 1) {
//                            tieneBordesVecinos = canny[i-1][j+1] == 1;
//                        }
//                    }
//
//                    if (!tieneBordesVecinos && i < bitmap.getWidth() - 1) {
//                        tieneBordesVecinos = canny[i+1][j] == 1;
//
//                        if (!tieneBordesVecinos && j > 0) {
//                            tieneBordesVecinos = canny[i+1][j-1] == 1;
//                        }
//                        if (!tieneBordesVecinos && j < bitmap.getHeight() - 1) {
//                            tieneBordesVecinos = canny[i+1][j+1] == 1;
//                        }
//                    }
//
//                    if (!tieneBordesVecinos) {
//                        canny[i][j] = 0;
//                    }
//                }
//            }
//        }
//
//        return canny;
//    }
//
//    private static int[] obtenerPixelesDeMagnitud135Grados(int[][] matrizMagnitudDeBorde, int i, int j) {
//        int[] pixeles = new int[2];
//
//        pixeles[0] = matrizMagnitudDeBorde[i-1][j+1];
//        pixeles[1] = matrizMagnitudDeBorde[i+1][j-1];
//
//        return pixeles;
//    }
//
//    private static int[] obtenerPixelesDeMagnitud90Grados(int[][] matrizMagnitudDeBorde, int i, int j) {
//
//        int[] pixeles = new int[2];
//
//        pixeles[0] = matrizMagnitudDeBorde[i][j-1];
//        pixeles[1] = matrizMagnitudDeBorde[i][j+1];
//
//        return pixeles;
//    }
//
//    private static int[] obtenerPixelesDeMagnitud45Grados(int[][] matrizMagnitudDeBorde, int i, int j) {
//
//        int[] pixeles = new int[2];
//
//        pixeles[0] = matrizMagnitudDeBorde[i+1][j-1];
//        pixeles[1] = matrizMagnitudDeBorde[i-1][j+1];
//
//        return pixeles;
//    }
//
//    private static int[] obtenerPixelesDeMagnitud0Grados(int[][] matrizMagnitudDeBorde, int i, int j) {
//
//        int[] pixeles = new int[2];
//
//        pixeles[0] = matrizMagnitudDeBorde[i-1][j];
//        pixeles[1] = matrizMagnitudDeBorde[i+1][j];
//
//        return pixeles;
//    }
//
//
//
//    private int[][] obtenerAngulos(int[][] gradientesHorizontal, int[][] gradientesVertical) {
//
//        int ancho = gradientesHorizontal.length;
//        int alto = gradientesHorizontal[0].length;
//
//        int[][] matrizAngulos = new int[ancho][alto];
//
//        for (int i = 0; i < ancho; i++) {
//            for (int j = 0; j < alto; j ++) {
//
//                float gx = gradientesHorizontal[i][j];
//                float gy = gradientesVertical [i][j];
//
//                int angulo = gx == 0 ? 0 : (int) Math.toDegrees(Math.atan(gy / gx));
//
//                if (angulo < 0) {
//                    angulo += 180;
//                }
//
//                matrizAngulos[i][j] = discretizar(angulo);
//            }
//        }
//
//        return matrizAngulos;
//    }
//
//    private int discretizar(int angulo) {
//
//        int anguloDiscretizado = 0;
//
//        if (angulo >= 22.5 && angulo <= 67.5) {
//            anguloDiscretizado = 45;
//        }
//        else if (angulo > 67.5 && angulo <= 112.5) {
//            anguloDiscretizado = 90;
//        }
//        else if (angulo > 112.5 && angulo <= 157.5) {
//            anguloDiscretizado = 135;
//        }
//
//        return anguloDiscretizado;
//    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        this.actividadBordesAvanzados.bordesDetectados(bitmap);
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);

        this.actividadBordesAvanzados.mostrarProgresoCanny(progreso, values[0]);
    }

    public enum Progreso {

        GAUSS_1_CALCULADO,
        GAUSS_2_CALCULADO,
        GAUSS_3_CALCULADO,
        CANNY_1_CALCULADO,
        CANNY_2_CALCULADO,
        CANNY_3_CALCULADO,
        CANNY_FINAL_CALCULADO,
    }
}
