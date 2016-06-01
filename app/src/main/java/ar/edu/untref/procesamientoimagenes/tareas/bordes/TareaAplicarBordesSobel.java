package ar.edu.untref.procesamientoimagenes.tareas.bordes;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordes;
import ar.edu.untref.procesamientoimagenes.modelo.TipoBorde;
import ar.edu.untref.procesamientoimagenes.modelo.TipoImagen;
import ar.edu.untref.procesamientoimagenes.util.AplicadorMascaraBordes;
import ar.edu.untref.procesamientoimagenes.util.Operacion;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarBordesSobel extends AsyncTask<Void, Void, Bitmap> {

    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;
    private TipoBorde tipoBorde;
    private TipoImagen tipoImagen;
    private int[][] matrizGradientes;

    public TareaAplicarBordesSobel(ActividadBordes actividadBordes, Bitmap bitmapOriginal, TipoBorde tipoBorde, TipoImagen tipoImagen) {

        this.actividadBordes = actividadBordes;
        this.bitmapOriginal = bitmapOriginal;
        this.tipoBorde = tipoBorde;
        this.tipoImagen = tipoImagen;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if (tipoBorde != TipoBorde.COMPLETO) {
            matrizGradientes = generarMatrizGradientes(tipoBorde);
        }
        else {

            int[][] matrizGradienteHorizontal = generarMatrizGradientes(TipoBorde.HORIZONTAL);
            int[][] matrizGradienteVertical = generarMatrizGradientes(TipoBorde.VERTICAL);

            matrizGradientes = Operacion.obtenerMatrizMagnitudGradiente(matrizGradienteHorizontal, matrizGradienteVertical);
        }

        return Operacion.hacerTransformacionLineal(matrizGradientes);
    }

    private int[][] generarMatrizGradientes(TipoBorde tipoBordeElegido) {

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

        return AplicadorMascaraBordes.obtenerMatrizGradientes(bitmapOriginal, matrizBordesSobel, tipoImagen);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        actividadBordes.bordesDetectados(bitmap, matrizGradientes);
    }
}
