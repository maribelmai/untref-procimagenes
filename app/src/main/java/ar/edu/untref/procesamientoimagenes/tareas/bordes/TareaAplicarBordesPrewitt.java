package ar.edu.untref.procesamientoimagenes.tareas.bordes;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordes;
import ar.edu.untref.procesamientoimagenes.modelo.TipoBorde;
import ar.edu.untref.procesamientoimagenes.modelo.TipoImagen;
import ar.edu.untref.procesamientoimagenes.util.AplicadorMascaraBordes;
import ar.edu.untref.procesamientoimagenes.util.Operacion;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarBordesPrewitt extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = TareaAplicarBordesPrewitt.class.getSimpleName();
    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;
    private TipoBorde tipoBorde;
    private TipoImagen tipoImagen;
    private int[][] matrizGradiente;

    public TareaAplicarBordesPrewitt(ActividadBordes actividadBordes, Bitmap bitmapOriginal, TipoBorde tipoBorde, TipoImagen tipoImagen) {

        this.actividadBordes = actividadBordes;
        this.bitmapOriginal = bitmapOriginal;
        this.tipoBorde = tipoBorde;
        this.tipoImagen = tipoImagen;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if (tipoBorde != TipoBorde.COMPLETO) {

            matrizGradiente = generarMatrizGradientes(tipoBorde);
        }
        else {
            int[][] matrizGradienteHorizontal = generarMatrizGradientes(TipoBorde.HORIZONTAL);
            int[][] matrizGradienteVertical = generarMatrizGradientes(TipoBorde.VERTICAL);

            matrizGradiente = Operacion.obtenerMatrizMagnitudGradiente(matrizGradienteHorizontal, matrizGradienteVertical);
        }

        return Operacion.hacerTransformacionLineal(matrizGradiente);
    }

    private int[][] generarMatrizGradientes(TipoBorde tipoBordeElegido) {

        int[][] matrizBordesPrewitt = new int[3][3];

        if (tipoBordeElegido == TipoBorde.HORIZONTAL) {

            Log.i(TAG, "generarMatrizGradientes: horizontal");
            matrizBordesPrewitt[0][0] =  1;
            matrizBordesPrewitt[0][1] =  0;
            matrizBordesPrewitt[0][2] = -1;

            matrizBordesPrewitt[1][0] =  1;
            matrizBordesPrewitt[1][1] =  0;
            matrizBordesPrewitt[1][2] = -1;

            matrizBordesPrewitt[2][0] =  1;
            matrizBordesPrewitt[2][1] =  0;
            matrizBordesPrewitt[2][2] = -1;
        }
        else if (tipoBordeElegido == TipoBorde.VERTICAL) {

            Log.i(TAG, "generarMatrizGradientes: vertical");
            matrizBordesPrewitt[0][0] = 1;
            matrizBordesPrewitt[0][1] = 1;
            matrizBordesPrewitt[0][2] = 1;

            matrizBordesPrewitt[1][0] = 0;
            matrizBordesPrewitt[1][1] = 0;
            matrizBordesPrewitt[1][2] = 0;

            matrizBordesPrewitt[2][0] = -1;
            matrizBordesPrewitt[2][1] = -1;
            matrizBordesPrewitt[2][2] = -1;
        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_DERECHA) {

            Log.i(TAG, "generarMatrizGradientes: diagonal derecha");
            matrizBordesPrewitt[0][0] = 0;
            matrizBordesPrewitt[0][1] = 1;
            matrizBordesPrewitt[0][2] = 1;

            matrizBordesPrewitt[1][0] = -1;
            matrizBordesPrewitt[1][1] = 0;
            matrizBordesPrewitt[1][2] = 1;

            matrizBordesPrewitt[2][0] = -1;
            matrizBordesPrewitt[2][1] = -1;
            matrizBordesPrewitt[2][2] = 0;

        }
        else if (tipoBordeElegido == TipoBorde.DIAGONAL_IZQUIERDA) {

            Log.i(TAG, "generarMatrizGradientes: diagonal izquierda");
            matrizBordesPrewitt[0][0] = 1;
            matrizBordesPrewitt[0][1] = 1;
            matrizBordesPrewitt[0][2] = 0;

            matrizBordesPrewitt[1][0] = 1;
            matrizBordesPrewitt[1][1] = 0;
            matrizBordesPrewitt[1][2] = -1;

            matrizBordesPrewitt[2][0] = 0;
            matrizBordesPrewitt[2][1] = -1;
            matrizBordesPrewitt[2][2] = -1;
        }

        return AplicadorMascaraBordes.obtenerMatrizGradientes(bitmapOriginal, matrizBordesPrewitt, tipoImagen);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        actividadBordes.bordesDetectados(bitmap, matrizGradiente);
    }
}
