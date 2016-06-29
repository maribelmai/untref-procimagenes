package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados;

import android.graphics.Bitmap;

import ar.edu.untref.procesamientoimagenes.modelo.TipoBorde;
import ar.edu.untref.procesamientoimagenes.modelo.TipoImagen;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.GeneradorMatrizBordes;
import ar.edu.untref.procesamientoimagenes.tareas.filtros.FiltroGaussiano;
import ar.edu.untref.procesamientoimagenes.util.AplicadorMascaraBordes;
import ar.edu.untref.procesamientoimagenes.util.Operacion;

public class DetectorCanny {

//    public static Imagen mostrarImagenNoMaximos(Imagen imagenOriginal){
//
//        MatrizDeColores matrices = calcularSupresionNoMaximos(imagenOriginal);
//
//        int[][] matrizR = MatricesManager.aplicarTransformacionLineal(matrices.getMatrizRojos());
//        int[][] matrizV = MatricesManager.aplicarTransformacionLineal(matrices.getMatrizVerdes());
//        int[][] matrizA = MatricesManager.aplicarTransformacionLineal(matrices.getMatrizAzules());
//
//        Imagen imagenResultante = new Imagen(MatricesManager.obtenerImagenDeMatrices(matrizR, matrizV, matrizA), imagenOriginal.getFormato(), imagenOriginal.getNombre()+"_nomaximos");
//
//        return imagenResultante;
//    }

    public static int[][] calcularSupresionNoMaximos(Bitmap imagenOriginal){

//        float[][] mascaraDeSobelEnX = DetectorDeBordes.calcularMascaraDeSobelEnX();
//        float[][] mascaraDeSobelEnY = DetectorDeBordes.calcularMascaraDeSobelEnY();
//
//        Imagen imagenFiltradaEnX = new Imagen(imagenOriginal.getBufferedImage(), imagenOriginal.getFormato(), imagenOriginal.getNombre(), imagenOriginal.getMatriz(Canal.ROJO), imagenOriginal.getMatriz(Canal.VERDE), imagenOriginal.getMatriz(Canal.AZUL));
//        Imagen imagenFiltradaEnY = new Imagen(imagenOriginal.getBufferedImage(), imagenOriginal.getFormato(), imagenOriginal.getNombre(), imagenOriginal.getMatriz(Canal.ROJO), imagenOriginal.getMatriz(Canal.VERDE), imagenOriginal.getMatriz(Canal.AZUL));
//
//        FiltroNuevo filtroEnX = new FiltroNuevo(mascaraDeSobelEnX);
//        FiltroNuevo filtroEnY = new FiltroNuevo(mascaraDeSobelEnY);
//
//        //Aplicamos filtros en X y en Y
//        int[][] matrizRojoEnX = filtroEnX.filtrar(imagenFiltradaEnX, Canal.ROJO);
//        int[][] matrizVerdeEnX = filtroEnX.filtrar(imagenFiltradaEnX, Canal.VERDE);
//        int[][] matrizAzulEnX = filtroEnX.filtrar(imagenFiltradaEnX, Canal.AZUL);
//
//        int[][] matrizRojoEnY = filtroEnY.filtrar(imagenFiltradaEnY, Canal.ROJO);
//        int[][] matrizVerdeEnY = filtroEnY.filtrar(imagenFiltradaEnY, Canal.VERDE);
//        int[][] matrizAzulEnY = filtroEnY.filtrar(imagenFiltradaEnY, Canal.AZUL);
//
//        //Sintetizamos usando la raiz de los cuadrados
//        int[][] matrizRojosSintetizados = DetectorDeBordes.sintetizar(matrizRojoEnX, matrizRojoEnY);
//        int[][] matrizVerdesSintetizados = DetectorDeBordes.sintetizar(matrizVerdeEnX, matrizVerdeEnY);
//        int[][] matrizAzulesSintetizados = DetectorDeBordes.sintetizar(matrizAzulEnX, matrizAzulEnY);

        int[][] bitmap1GHorizontal = AplicadorMascaraBordes.obtenerMatrizGradientes(imagenOriginal.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.HORIZONTAL), TipoImagen.GRIS);
        int[][] bitmap1GVertical = AplicadorMascaraBordes.obtenerMatrizGradientes(imagenOriginal.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.VERTICAL), TipoImagen.GRIS);
        int[][] matrizMagnitudGradiente1 = Operacion.obtenerMatrizMagnitudGradiente(bitmap1GHorizontal, bitmap1GVertical);

        int[][] matrizDeAngulosCanalRojo = calcularAnguloDelGradiente(bitmap1GHorizontal, bitmap1GVertical);
//        int[][] matrizDeAngulosCanalVerde = calcularAnguloDelGradiente(matrizVerdeEnX, matrizVerdeEnY);
//        int[][] matrizDeAngulosCanalAzul = calcularAnguloDelGradiente(matrizAzulEnX, matrizAzulEnY);


        int[][] matrizBordesRojos = buscarBordesImportantes(matrizMagnitudGradiente1, matrizDeAngulosCanalRojo);
//        int[][] matrizBordesVerdes = buscarBordesImportantes(matrizVerdesSintetizados, matrizDeAngulosCanalVerde);
//        int[][] matrizBordesAzules = buscarBordesImportantes(matrizAzulesSintetizados, matrizDeAngulosCanalAzul);

//        MatrizDeColores matrizFinal = new MatrizDeColores(matrizBordesRojos, matrizBordesVerdes, matrizBordesAzules);

        return matrizBordesRojos;
    }

    private static int[][] buscarBordesImportantes(int[][] matriz, int[][] matrizAngulos) {

        for (int x = 0; x < matriz.length; x++) {
            for (int y = 0; y < matriz[0].length; y++) {

                int[] pixelesAMirar = obtenerPixelesAMirar(matriz, matrizAngulos, x, y);
                int pixelAMirar1 = pixelesAMirar[0];
                int pixelAMirar2 = pixelesAMirar[1];

                if (pixelAMirar1 > matriz[x][y] || pixelAMirar2 > matriz[x][y]){

                    matriz[x][y] = 0;
                }
            }
        }

        return matriz;
    }

    private static int[] obtenerPixelesAMirar(int[][] matrizDeMagnitud, int[][] matrizAngulos, int x, int y) {

        int[] pixeles = new int[]{0,0};

        if (matrizAngulos[x][y]==0 && x>0 && x < matrizDeMagnitud[0].length -1){

            pixeles[0] = matrizDeMagnitud[x-1][y];
            pixeles[1] = matrizDeMagnitud[x+1][y];
        } else if (matrizAngulos[x][y] == 45 && x>0 && x<matrizDeMagnitud[0].length -1 && y>0 && y<matrizDeMagnitud.length-1){

            pixeles[0] = matrizDeMagnitud[x+1][y-1];
            pixeles[1] = matrizDeMagnitud[x-1][y+1];
        } else if (matrizAngulos[x][y] == 90 && y>0 && y<matrizDeMagnitud.length-1){

            pixeles[0] = matrizDeMagnitud[x][y-1];
            pixeles[1] = matrizDeMagnitud[x][y+1];
        } else if (matrizAngulos[x][y] == 135 && x>0 && x<matrizDeMagnitud[0].length-1 && y>0 && y<matrizDeMagnitud.length-1){

            pixeles[0] = matrizDeMagnitud[x-1][y+1];
            pixeles[1] = matrizDeMagnitud[x+1][y-1];
        }

        return pixeles;
    }

    public static int[][] calcularAnguloDelGradiente(int[][] matrizX, int[][] matrizY){

        int[][] matrizDeAngulos = new int[matrizX[0].length][matrizX.length];

        for (int i=0; i<matrizX[0].length ;i++){
            for (int j=0; j<matrizX.length ;j++){

                float arcTan = 0;
                if(matrizX[i][j] != 0){

                    arcTan = (float) Math.atan((float) matrizY[i][j] / matrizX[i][j]);
                }

                double grados = Math.toDegrees(arcTan);

                if ( grados < 0){

                    grados = 180 + grados;
                }

                matrizDeAngulos[i][j] = discretizarAngulo(grados);
            }
        }

        return matrizDeAngulos;
    }

    private static int discretizarAngulo(double angulo) {

        int anguloResultante = 0;

        if ( angulo >= 22.5 && angulo <=67.5 ){

            anguloResultante = 45;
        } else if ( angulo >= 67.5 && angulo <=112.5 ){

            anguloResultante = 90;
        } else if ( angulo >= 112.5 && angulo <=157.5 ){

            anguloResultante = 135;
        }

        return anguloResultante;
    }

    public static int[][] aplicarUmbralizacionConHisteresis(int[][] matrizNoMaximos, int umbral1, int umbral2){

        int[][] matrizResultante = new int[matrizNoMaximos[0].length][matrizNoMaximos.length];

        for (int i=0; i<matrizNoMaximos[0].length ;i++){
            for (int j=0; j<matrizNoMaximos.length ;j++){

                int valor = matrizNoMaximos[j][i];

                if (valor < umbral1){

                    valor = 0;
                } else if ( valor > umbral2 || esCuatroVecinoDeUnBorde(matrizNoMaximos, j, i)){

                    valor = 1;
                } else {

                    valor = 0;
                }

                matrizResultante[i][j] = valor;
            }
        }

        return matrizResultante;
    }

//    public static MatrizDeColores calcularMatrizConFiltrosGauss(Imagen imagenOriginal, int sigma) {
//
//        Imagen imagenConFiltroGauss = FiltroGaussiano.aplicarFiltroGaussiano(imagenOriginal, sigma);
//
//        int[][] matrizRojoImagenGauss = imagenConFiltroGauss.getMatriz(Canal.ROJO);
//        int[][] matrizVerdeImagenGauss = imagenConFiltroGauss.getMatriz(Canal.VERDE);
//        int[][] matrizAzulImagenGauss = imagenConFiltroGauss.getMatriz(Canal.AZUL);
//
//        MatrizDeColores matricesFiltradasResultantes = new MatrizDeColores (matrizRojoImagenGauss, matrizVerdeImagenGauss, matrizAzulImagenGauss);
//
//        return matricesFiltradasResultantes;
//    }

    private static boolean esCuatroVecinoDeUnBorde(int[][] matrizNoMaximos, int i, int j) {

        boolean esBorde = false;

        if ( 	tieneVecinoDerechoBorde(matrizNoMaximos, i, j)
                || tieneVecinoIzquierdoBorde(matrizNoMaximos, i, j)
                || tieneVecinoSuperiorBorde(matrizNoMaximos, i, j)
                || tieneVecinoInferiorBorde(matrizNoMaximos, i, j)){


            esBorde = true;
        }

        return esBorde;
    }

    private static boolean tieneVecinoDerechoBorde(int[][] matrizNoMaximos, int i,
                                                   int j) {
        return i < matrizNoMaximos[0].length-1 && matrizNoMaximos[i+1][j]>0;
    }

    private static boolean tieneVecinoIzquierdoBorde(int[][] matrizNoMaximos, int i,
                                                     int j) {
        return i > 0 && matrizNoMaximos[i-1][j] > 0;
    }

    private static boolean tieneVecinoSuperiorBorde(int[][] matrizNoMaximos, int i,
                                                    int j) {
        return j > 0 && matrizNoMaximos[i][j-1] > 0;
    }

    private static boolean tieneVecinoInferiorBorde(int[][] matrizNoMaximos, int i,
                                                    int j) {
        return j < matrizNoMaximos.length-1 && matrizNoMaximos[i][j+1] > 0;
    }

    public static Bitmap aplicarDetectorDeCanny(Bitmap imagenActual, int sigma1, int sigma2, int umbral1, int umbral2){

        Bitmap bitmapGaussiano = FiltroGaussiano.aplicarFiltroGaussiano(imagenActual, 1);

//        MatrizDeColores matrizDeColores1 = DetectorDeBordesDeCanny.calcularMatrizConFiltrosGauss(imagenActual, sigma1);
//        MatrizDeColores matrizDeColores2 = DetectorDeBordesDeCanny.calcularMatrizConFiltrosGauss(imagenActual, sigma2);

//        Imagen imagen1 = new Imagen((MatricesManager.obtenerImagenDeMatrices(matrizDeColores1.getMatrizRojos(), matrizDeColores1.getMatrizVerdes(), matrizDeColores1.getMatrizAzules())), imagenActual.getFormato(), imagenActual.getNombre());
//        Imagen imagen2 = new Imagen((MatricesManager.obtenerImagenDeMatrices(matrizDeColores2.getMatrizRojos(), matrizDeColores2.getMatrizVerdes(), matrizDeColores2.getMatrizAzules())), imagenActual.getFormato(), imagenActual.getNombre());

        int[][] noMaximos1 = calcularSupresionNoMaximos(bitmapGaussiano.copy(Bitmap.Config.RGB_565, true));
//        MatrizDeColores noMaximos2 = DetectorDeBordesDeCanny.calcularSupresionNoMaximos(imagen2);

//        int[][] matrizRojos1 = noMaximos1.getMatrizRojos();
//        int[][] matrizVerdes1 = noMaximos1.getMatrizVerdes();
//        int[][] matrizAzules1 = noMaximos1.getMatrizAzules();
//
//        int[][] matrizRojos2 = noMaximos2.getMatrizRojos();
//        int[][] matrizVerdes2 = noMaximos2.getMatrizVerdes();
//        int[][] matrizAzules2 = noMaximos2.getMatrizAzules();
//
//        int[][] matrizResultanteRojo = new int[matrizRojos1[0].length][matrizRojos1.length];
//        int[][] matrizResultanteVerde = new int[matrizRojos1[0].length][matrizRojos1.length];
//        int[][] matrizResultanteAzul = new int[matrizRojos1[0].length][matrizRojos1.length];

        int[][] matrizHisteresisRojo1 = aplicarUmbralizacionConHisteresis(noMaximos1, umbral1, umbral2);
//        int[][] matrizHisteresisVerde1 = DetectorDeBordesDeCanny.aplicarUmbralizacionConHisteresis(matrizVerdes1, umbral1, umbral2);
//        int[][] matrizHisteresisAzul1 = DetectorDeBordesDeCanny.aplicarUmbralizacionConHisteresis(matrizAzules1, umbral1, umbral2);

//        int[][] matrizHisteresisRojo2 = DetectorDeBordesDeCanny.aplicarUmbralizacionConHisteresis(matrizRojos2, umbral1, umbral2);
//        int[][] matrizHisteresisVerde2 = DetectorDeBordesDeCanny.aplicarUmbralizacionConHisteresis(matrizVerdes2, umbral1, umbral2);
//        int[][] matrizHisteresisAzul2 = DetectorDeBordesDeCanny.aplicarUmbralizacionConHisteresis(matrizAzules2, umbral1, umbral2);

//        for (int i=0; i < matrizRojos1.length ;i++){
//            for (int j=0; j < matrizRojos1[0].length ;j++){
//
//                if (matrizHisteresisRojo1[i][j] > matrizHisteresisRojo2[i][j]){
//
//                    matrizResultanteRojo[j][i] = matrizHisteresisRojo1[i][j];
//                }else{
//                    matrizResultanteRojo[j][i] = matrizHisteresisRojo2[i][j];
//                }
//
//                if (matrizHisteresisVerde1[i][j] > matrizHisteresisVerde2[i][j]){
//
//                    matrizResultanteVerde[j][i] = matrizHisteresisVerde1[i][j];
//                }else{
//                    matrizResultanteVerde[j][i] = matrizHisteresisVerde2[i][j];
//                }
//
//                if (matrizHisteresisAzul1[i][j] > matrizHisteresisAzul2[i][j]){
//
//                    matrizResultanteAzul[j][i] = matrizHisteresisAzul1[i][j];
//                }else{
//                    matrizResultanteAzul[j][i] = matrizHisteresisAzul2[i][j];
//                }
//
//            }
//        }

//        int[][] matrizRojoFinal = MatricesManager.aplicarTransformacionLineal(matrizResultanteRojo);
//        int[][] matrizVerdeFinal = MatricesManager.aplicarTransformacionLineal(matrizResultanteVerde);
//        int[][] matrizAzulFinal = MatricesManager.aplicarTransformacionLineal(matrizResultanteAzul);
//
//        BufferedImage bufferedNuevo = MatricesManager.obtenerImagenDeMatrices(matrizRojoFinal, matrizVerdeFinal, matrizAzulFinal);
//        Imagen imagenNueva = new Imagen(bufferedNuevo, imagenActual.getFormato(), imagenActual.getNombre()+"_histeresis");

        return Operacion.umbralizar(matrizHisteresisRojo1);
//        ;
    }

}
