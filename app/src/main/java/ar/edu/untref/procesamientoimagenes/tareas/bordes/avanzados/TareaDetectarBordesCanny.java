package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordesAvanzados;
import ar.edu.untref.procesamientoimagenes.modelo.TipoBorde;
import ar.edu.untref.procesamientoimagenes.modelo.TipoImagen;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.GeneradorMatrizBordes;
import ar.edu.untref.procesamientoimagenes.tareas.filtros.TareaAplicarFiltroGaussiano;
import ar.edu.untref.procesamientoimagenes.util.AplicadorMascaraBordes;

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
    private Progreso progreso;

    public TareaDetectarBordesCanny(Bitmap bitmap, ActividadBordesAvanzados actividadBordesAvanzados, float sigma1, float sigma2, float sigma3) {

        this.bitmap = bitmap;
        this.actividadBordesAvanzados = actividadBordesAvanzados;
        this.sigma1 = sigma1;
        this.sigma2 = sigma2;
        this.sigma3 = sigma3;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        Bitmap nuevoBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);

        try {
            Bitmap bitmap1 = new TareaAplicarFiltroGaussiano(null, this.bitmap, sigma1).ejecutar();
            progreso = Progreso.GAUSS_1_CALCULADO;
            publishProgress(bitmap1);
            Bitmap bitmap2 = new TareaAplicarFiltroGaussiano(null, this.bitmap, sigma2).ejecutar();
            progreso = Progreso.GAUSS_2_CALCULADO;
            publishProgress(bitmap2);
            Bitmap bitmap3 = new TareaAplicarFiltroGaussiano(null, this.bitmap, sigma3).ejecutar();
            progreso = Progreso.GAUSS_3_CALCULADO;
            publishProgress(bitmap3);

            int[][] bitmap1GHorizontal = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap1.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.HORIZONTAL), TipoImagen.GRIS);
            int[][] bitmap1GVertical = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap1.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.VERTICAL), TipoImagen.GRIS);

            int[][] bitmap2GHorizontal = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap2.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.HORIZONTAL), TipoImagen.GRIS);
            int[][] bitmap2GVertical = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap2.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.VERTICAL), TipoImagen.GRIS);

            int[][] bitmap3GHorizontal = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap3.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.HORIZONTAL), TipoImagen.GRIS);
            int[][] bitmap3GVertical = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap3.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.VERTICAL), TipoImagen.GRIS);

            int[][] angulosBitmap1 = obtenerAngulos(bitmap1GHorizontal, bitmap1GVertical);
            int[][] angulosBitmap2 = obtenerAngulos(bitmap2GHorizontal, bitmap2GVertical);
            int[][] angulosBitmap3 = obtenerAngulos(bitmap3GHorizontal, bitmap3GVertical);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nuevoBitmap;
    }

    private int[][] obtenerAngulos(int[][] gradientesHorizontal, int[][] gradientesVertical) {

        int ancho = gradientesHorizontal.length;
        int alto = gradientesHorizontal[0].length;

        int[][] matrizAngulos = new int[ancho][alto];

        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j ++) {

                float gx = gradientesHorizontal[i][j];
                float gy = gradientesVertical [i][j];

                int angulo = gx == 0 ? 0 : (int) Math.toDegrees(Math.atan(gy / gx));

                if (angulo < 0) {
                    angulo += 180;
                }

                matrizAngulos[i][j] = discretizar(angulo);

                Log.i(TAG, "obtenerAngulos: " + angulo);
            }
        }

        return matrizAngulos;
    }

    private int discretizar(int angulo) {

        int anguloDiscretizado = 0;

        if (angulo >= 22.5 && angulo <= 67.5) {
            anguloDiscretizado = 45;
        }
        else if (angulo > 67.5 && angulo <= 112.5) {
            anguloDiscretizado = 90;
        }
        else if (angulo > 112.5 && angulo <= 157.5) {
            anguloDiscretizado = 135;
        }

        return anguloDiscretizado;
    }

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
    }
}
