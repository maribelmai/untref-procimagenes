package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.canny;

import android.graphics.Bitmap;
import android.graphics.Color;

import ar.edu.untref.procesamientoimagenes.tareas.filtros.FiltroGaussiano;

public class DetectorDeBordeCanny {
	
	public static Bitmap aplicarSupresionNoMaximos(Bitmap imagenOriginal){
		
		return convertirMatrizEnImagen(obtenerMatrizSupresionNoMaximos(imagenOriginal), imagenOriginal.getWidth(), imagenOriginal.getHeight());
	}
	
	public static int[][] obtenerMatrizSupresionNoMaximos(Bitmap imagenOriginal){
		
		int mascaraX[][] = { { 1, 0, -1 }, { 2, 0, -2 }, { 1, 0, -1 } };
		int mascaraY[][] = { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };
		
		int[][] matrizImagenSobelX = aplicarMascara(imagenOriginal, mascaraX);
		int[][] matrizImagenSobelY  = aplicarMascara(imagenOriginal, mascaraY);
		
		int[][] matrizMagnitudDeBorde = sintetizar(matrizImagenSobelX, matrizImagenSobelY);
		int[][] matrizDeAngulos = obtenerAnguloDelGradiente(matrizImagenSobelX, matrizImagenSobelY);
		
		int[][] matrizSupresionNoMaximosSinTransformar = aplicarSupresionNoMaximos(matrizMagnitudDeBorde, matrizDeAngulos);
		
		int[][] matrizSupresionNoMaximosConTransformacionLineal = aplicarTransformacionLineal(matrizSupresionNoMaximosSinTransformar);
		
		return matrizSupresionNoMaximosConTransformacionLineal;
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

                matrizDeAngulos[i][j] = acomodarGrados(angulo);
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
	
	public static Bitmap aplicarUmbralizacionConHisteresis(Bitmap imagen, int umbral1, int umbral2){
		return convertirMatrizEnImagen(obtenerMatrizUmbralizacionConHisteresis(imagen, umbral1, umbral2), imagen.getWidth(), imagen.getHeight());
	}

	public static int[][] obtenerMatrizUmbralizacionConHisteresis(Bitmap imagen, int umbral1, int umbral2){
		int[][] matrizResultado = new int[imagen.getWidth()][imagen.getHeight()];
		
		int mascaraX[][] = { { 1, 0, -1 }, { 2, 0, -2 }, { 1, 0, -1 } };
		int mascaraY[][] = { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };
		
		int[][] matrizImagenSobelX = aplicarMascara(imagen, mascaraX);
		int[][] matrizImagenSobelY  = aplicarMascara(imagen, mascaraY);
		
		int[][] matrizMagnitudDeBorde = sintetizar(matrizImagenSobelX, matrizImagenSobelY);

		for (int i = 1; i < matrizMagnitudDeBorde.length - 1; i++) {
			for (int j = 1; j < matrizMagnitudDeBorde[0].length - 1; j++) {

				if(matrizMagnitudDeBorde[i][j] > umbral2){
					matrizResultado[i][j] = 255;
				}
                else if(matrizMagnitudDeBorde[i][j] < umbral1){
					matrizResultado[i][j] = 0;
				}
                else{
//					if(matrizResultado[i-1][j] == 255 || matrizResultado[i+1][j] == 255 || matrizResultado[i][j+1] == 255 || matrizResultado[i][j-1] == 255){
//						matrizResultado[i][j] = 255;
//					}

                    boolean tieneBordesVecinos = false;

                    if (i > 0) {
                        tieneBordesVecinos = matrizResultado[i-1][j] == 1;

                        if (!tieneBordesVecinos && j > 0) {
                            tieneBordesVecinos = matrizResultado[i-1][j-1] == 1;
                        }
                        if (!tieneBordesVecinos && j < imagen.getHeight() - 1) {
                            tieneBordesVecinos = matrizResultado[i-1][j+1] == 1;
                        }
                    }

                    if (!tieneBordesVecinos && i < imagen.getWidth() - 1) {
                        tieneBordesVecinos = matrizResultado[i+1][j] == 1;

                        if (!tieneBordesVecinos && j > 0) {
                            tieneBordesVecinos = matrizResultado[i+1][j-1] == 1;
                        }
                        if (!tieneBordesVecinos && j < imagen.getHeight() - 1) {
                            tieneBordesVecinos = matrizResultado[i+1][j+1] == 1;
                        }
                    }

                    if (!tieneBordesVecinos) {
                        matrizResultado[i][j] = 0;
                    }
                    else {
                        matrizResultado[i][j] = 1;
                    }
				}
			}
		}
		
		return matrizResultado;
	}

	public static int[][] calcularMatrizDeLaImagen(Bitmap image) {

		int[][] matriz = new int[image.getWidth()][image.getHeight()];

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				matriz[i][j] = Color.red(image.getPixel(i, j));
			}
		}
		return matriz;
	}
	
	public static Bitmap detectorDeBordeCanny(Bitmap imagenOriginal, int sigma1, int sigma2, int umbral1, int umbral2){

		Bitmap imagenResultado1 = FiltroGaussiano.aplicarFiltroGaussiano(imagenOriginal, sigma1);
		Bitmap imagenResultado2 = FiltroGaussiano.aplicarFiltroGaussiano(imagenOriginal, sigma2);
		
		int[][] matrizResultado1 = obtenerMatrizCanny(imagenResultado1, umbral1, umbral2);
		int[][] matrizResultado2 = obtenerMatrizCanny(imagenResultado2, umbral1, umbral2);
		
		int[][] matrizResultadoFinal = new int[imagenOriginal.getWidth()][imagenOriginal.getHeight()];
		
		for (int i = 0; i < matrizResultadoFinal.length; i++) {
			for (int j = 0; j < matrizResultadoFinal[0].length; j++) {
				int resultado1 = matrizResultado1[i][j];
				int resultado2 = matrizResultado2[i][j];
				if(resultado1 > resultado2){
					matrizResultadoFinal [i][j] = resultado1;
				}else{
					matrizResultadoFinal [i][j] = resultado2;
				}
			}
		}

		Bitmap imagenResultado = convertirMatrizEnImagen(matrizResultadoFinal, imagenOriginal.getWidth(), imagenOriginal.getHeight());
		
		return imagenResultado;
	}
	
	public static int [][] obtenerMatrizCanny(Bitmap imagen, int umbral1, int umbral2){
		
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
					matrizResultado[i][j] = 255;
				}else if(matrizSupresionNoMaximosSinTransformar[i][j] < umbral1){
					matrizResultado[i][j] = 0;
				}else{
					if(matrizResultado[i-1][j] == 255 || matrizResultado[i+1][j] == 255 || matrizResultado[i][j+1] == 255 || matrizResultado[i][j-1] == 255){
						matrizResultado[i][j] = 255;
					}
				}
			}
		}
		return matrizResultado;
	}

	public static Bitmap convertirMatrizEnImagen(int[][] matriz, int ancho, int alto){

		Bitmap bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

		for (int i = 0; i < ancho; i++) {
			for (int j = 0; j < alto; j++) {
				int pixel=matriz[i][j];
				bitmap.setPixel(i, j, Color.rgb(pixel, pixel, pixel));
			}
		}

		return bitmap;
	}

	public static int[][] aplicarTransformacionLineal(int[][] matriz) {

		int nrows = matriz.length;
		int ncols = matriz[0].length;

		int[][] matrizTransformada = new int[nrows][ncols];

		float rojoMin = matrizTransformada[0][0];
		float rojoMax = matrizTransformada[0][0];

		for (int f = 0; f < nrows; f++) {
			for (int g = 0; g < ncols; g++) {

				int rojoActual = matriz[f][g];

				if (rojoMin > rojoActual) {
					rojoMin = rojoActual;
				}

				if (rojoMax < rojoActual) {
					rojoMax = rojoActual;
				}
			}

		}

		for (int i = 0; i < nrows; i++) {
			for (int j = 0; j < ncols; j++) {

				int rojoActual = matriz[i][j];
				int rojoTransformado = (int) ((((255f) / (rojoMax - rojoMin)) * rojoActual) - ((rojoMin * 255f) / (rojoMax - rojoMin)));

				matrizTransformada[i][j] = rojoTransformado;
			}
		}

		return matrizTransformada;
	}
	
}
