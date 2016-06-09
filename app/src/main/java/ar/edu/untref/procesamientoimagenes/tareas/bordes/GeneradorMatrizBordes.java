package ar.edu.untref.procesamientoimagenes.tareas.bordes;

import ar.edu.untref.procesamientoimagenes.modelo.TipoBorde;

/**
 * Created by maribelmai on 8/6/16.
 */
public class GeneradorMatrizBordes {

    public static int[][] getMatrizSobel(TipoBorde tipoBordeElegido) {

        int[][] matrizBordesSobel = new int[3][3];

        if (tipoBordeElegido == TipoBorde.HORIZONTAL) {

            matrizBordesSobel[0][0] = -1;
            matrizBordesSobel[0][1] =  0;
            matrizBordesSobel[0][2] =  1;

            matrizBordesSobel[1][0] = -2;
            matrizBordesSobel[1][1] =  0;
            matrizBordesSobel[1][2] =  2;

            matrizBordesSobel[2][0] = -1;
            matrizBordesSobel[2][1] =  0;
            matrizBordesSobel[2][2] =  1;
        }
        else if (tipoBordeElegido == TipoBorde.VERTICAL) {

            matrizBordesSobel[0][0] = -1;
            matrizBordesSobel[0][1] = -2;
            matrizBordesSobel[0][2] = -1;

            matrizBordesSobel[1][0] = 0;
            matrizBordesSobel[1][1] = 0;
            matrizBordesSobel[1][2] = 0;

            matrizBordesSobel[2][0] = 1;
            matrizBordesSobel[2][1] = 2;
            matrizBordesSobel[2][2] = 1;
        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_DERECHA) {

            matrizBordesSobel[0][0] = 0;
            matrizBordesSobel[0][1] = 1;
            matrizBordesSobel[0][2] = 2;

            matrizBordesSobel[1][0] = -1;
            matrizBordesSobel[1][1] = 0;
            matrizBordesSobel[1][2] = 1;

            matrizBordesSobel[2][0] = -2;
            matrizBordesSobel[2][1] = -1;
            matrizBordesSobel[2][2] =  0;

        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_IZQUIERDA) {

            matrizBordesSobel[0][0] = -2;
            matrizBordesSobel[0][1] = -1;
            matrizBordesSobel[0][2] = 0;

            matrizBordesSobel[1][0] = -1;
            matrizBordesSobel[1][1] = 0;
            matrizBordesSobel[1][2] = 1;

            matrizBordesSobel[2][0] = 0;
            matrizBordesSobel[2][1] = 1;
            matrizBordesSobel[2][2] = 2;
        }

        return matrizBordesSobel;
    }
}
