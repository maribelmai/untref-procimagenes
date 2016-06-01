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
public class TareaAplicarBordesKirsh extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarBordesKirsh.class.getSimpleName();
    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;
    private TipoBorde tipoBorde;
    private TipoImagen tipoImagen;
    private int[][] matrizGradiente;

    public TareaAplicarBordesKirsh(ActividadBordes actividadBordes, Bitmap bitmapOriginal, TipoBorde tipoBorde, TipoImagen tipoImagen) {

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

        int[][] matrizBordesKirsh = new int[3][3];

        if (tipoBordeElegido == TipoBorde.HORIZONTAL) {

            matrizBordesKirsh[0][0] = 5;
            matrizBordesKirsh[0][1] = -3;
            matrizBordesKirsh[0][2] = -3;

            matrizBordesKirsh[1][0] = 5;
            matrizBordesKirsh[1][1] = 0;
            matrizBordesKirsh[1][2] = -3;

            matrizBordesKirsh[2][0] = 5;
            matrizBordesKirsh[2][1] = -3;
            matrizBordesKirsh[2][2] = -3;
        }
        else if (tipoBordeElegido == TipoBorde.VERTICAL) {

            matrizBordesKirsh[0][0] = 5;
            matrizBordesKirsh[0][1] = 5;
            matrizBordesKirsh[0][2] = 5;

            matrizBordesKirsh[1][0] = -3;
            matrizBordesKirsh[1][1] = 0;
            matrizBordesKirsh[1][2] = -3;

            matrizBordesKirsh[2][0] = -3;
            matrizBordesKirsh[2][1] = -3;
            matrizBordesKirsh[2][2] = -3;
        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_DERECHA) {

            matrizBordesKirsh[0][0] = -3;
            matrizBordesKirsh[0][1] = -3;
            matrizBordesKirsh[0][2] = -3;

            matrizBordesKirsh[1][0] = 5;
            matrizBordesKirsh[1][1] = 0;
            matrizBordesKirsh[1][2] = -3;

            matrizBordesKirsh[2][0] = 5;
            matrizBordesKirsh[2][1] = 5;
            matrizBordesKirsh[2][2] = -3;

        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_IZQUIERDA) {

            matrizBordesKirsh[0][0] = 5;
            matrizBordesKirsh[0][1] = 5;
            matrizBordesKirsh[0][2] = -3;

            matrizBordesKirsh[1][0] = 5;
            matrizBordesKirsh[1][1] = 0;
            matrizBordesKirsh[1][2] = -3;

            matrizBordesKirsh[2][0] = -3;
            matrizBordesKirsh[2][1] = -3;
            matrizBordesKirsh[2][2] = -3;
        }

        return AplicadorMascaraBordes.obtenerMatrizGradientes(bitmapOriginal, matrizBordesKirsh, tipoImagen);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        actividadBordes.bordesDetectados(bitmap, matrizGradiente);
    }
}
