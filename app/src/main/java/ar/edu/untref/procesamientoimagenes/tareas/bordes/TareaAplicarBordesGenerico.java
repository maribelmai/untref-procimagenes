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
public class TareaAplicarBordesGenerico extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarBordesGenerico.class.getSimpleName();
    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;
    private TipoBorde tipoBorde;
    private TipoImagen tipoImagen;
    private int[][] matrizGradiente;

    public TareaAplicarBordesGenerico(ActividadBordes actividadBordes, Bitmap bitmapOriginal, TipoBorde tipoBorde, TipoImagen tipoImagen) {

        this.actividadBordes = actividadBordes;
        this.bitmapOriginal = bitmapOriginal;
        this.tipoBorde = tipoBorde;
        this.tipoImagen = tipoImagen;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        matrizGradiente = generarMatrizGradientes(tipoBorde);
        return Operacion.hacerTransformacionLineal(matrizGradiente);
    }

    private int[][] generarMatrizGradientes(TipoBorde tipoBordeElegido) {

        int[][] matrizBordesGenerico = new int[3][3];

        if (tipoBordeElegido == TipoBorde.HORIZONTAL) {

            matrizBordesGenerico[0][0] = 1;
            matrizBordesGenerico[0][1] = 1;
            matrizBordesGenerico[0][2] = -1;

            matrizBordesGenerico[1][0] = 1;
            matrizBordesGenerico[1][1] = -2;
            matrizBordesGenerico[1][2] = -1;

            matrizBordesGenerico[2][0] = 1;
            matrizBordesGenerico[2][1] = 1;
            matrizBordesGenerico[2][2] = -1;
        }
        else if (tipoBordeElegido == TipoBorde.VERTICAL) {

            matrizBordesGenerico[0][0] = 1;
            matrizBordesGenerico[0][1] = 1;
            matrizBordesGenerico[0][2] = 1;

            matrizBordesGenerico[1][0] = 1;
            matrizBordesGenerico[1][1] = -2;
            matrizBordesGenerico[1][2] = 1;

            matrizBordesGenerico[2][0] = -1;
            matrizBordesGenerico[2][1] = -1;
            matrizBordesGenerico[2][2] = -1;
        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_DERECHA) {

            matrizBordesGenerico[0][0] = 1;
            matrizBordesGenerico[0][1] = -1;
            matrizBordesGenerico[0][2] = -1;

            matrizBordesGenerico[1][0] = 1;
            matrizBordesGenerico[1][1] = -2;
            matrizBordesGenerico[1][2] = -1;

            matrizBordesGenerico[2][0] = 1;
            matrizBordesGenerico[2][1] = 1;
            matrizBordesGenerico[2][2] = 1;

        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_IZQUIERDA) {

            matrizBordesGenerico[0][0] = 1;
            matrizBordesGenerico[0][1] = 1;
            matrizBordesGenerico[0][2] = 1;

            matrizBordesGenerico[1][0] = 1;
            matrizBordesGenerico[1][1] = -2;
            matrizBordesGenerico[1][2] = -1;

            matrizBordesGenerico[2][0] = 1;
            matrizBordesGenerico[2][1] = -1;
            matrizBordesGenerico[2][2] = -1;
        }

        return AplicadorMascaraBordes.obtenerMatrizGradientes(bitmapOriginal, matrizBordesGenerico, tipoImagen);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        actividadBordes.bordesDetectados(bitmap, matrizGradiente);
    }
}
