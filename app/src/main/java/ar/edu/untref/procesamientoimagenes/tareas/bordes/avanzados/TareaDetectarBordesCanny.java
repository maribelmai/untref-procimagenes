package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordesAvanzados;
import ar.edu.untref.procesamientoimagenes.modelo.TipoBorde;
import ar.edu.untref.procesamientoimagenes.modelo.TipoImagen;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.DireccionBorde;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.GeneradorMatrizBordes;
import ar.edu.untref.procesamientoimagenes.tareas.filtros.FiltroGaussiano;
import ar.edu.untref.procesamientoimagenes.util.AplicadorMascaraBordes;
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

        Bitmap nuevoBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);

        try {

            Bitmap bitmap1 = FiltroGaussiano.aplicarFiltroGaussiano(bitmap, 1);
            progreso = Progreso.GAUSS_1_CALCULADO;
            publishProgress(bitmap1);

            int[][] bitmap1GHorizontal = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap1.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.HORIZONTAL), TipoImagen.GRIS);
            int[][] bitmap1GVertical = AplicadorMascaraBordes.obtenerMatrizGradientes(bitmap1.copy(Bitmap.Config.RGB_565, false), GeneradorMatrizBordes.getMatrizSobel(TipoBorde.VERTICAL), TipoImagen.GRIS);
            int[][] matrizMagnitudGradiente1 = Operacion.obtenerMatrizMagnitudGradiente(bitmap1GHorizontal, bitmap1GVertical);

            int[][] sobel = new int[matrizMagnitudGradiente1.length][matrizMagnitudGradiente1[0].length];


            for (int i = 0 ; i < matrizMagnitudGradiente1.length ; i++) {
                for (int j = 0; j < matrizMagnitudGradiente1[0].length; j++) {

                    if (matrizMagnitudGradiente1[i][j] > 110) {
                        sobel[i][j] = 1;
                    }
                }
            }

            progreso = Progreso.CANNY_1_CALCULADO;
            publishProgress(Operacion.umbralizar(sobel));

//            Thread.sleep(1000);
//
            int[][] angulos = obtenerAngulos(bitmap1GHorizontal, bitmap1GVertical);
            int[][] canny1 = suprimirNoMaximos(matrizMagnitudGradiente1, angulos);
//
//            progreso = Progreso.CANNY_FINAL_CALCULADO;
            nuevoBitmap = Operacion.umbralizar(canny1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nuevoBitmap;

//        return DetectorCanny.aplicarDetectorDeCanny(bitmap, 1, 3, (int)umbral1, (int)umbral2);
    }

    private int[][] obtenerCannyFinal(int[][] canny1) {

        int ancho = canny1.length;
        int alto = canny1[0].length;

        int[][] cannyFinal = new int[ancho][alto];

        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j ++) {

                if (canny1[i][j] == 1) {
                    cannyFinal[i][j] = 1;
                }
            }
        }

        return cannyFinal;
    }

    private int[][] suprimirNoMaximos(int[][] matrizMagnitudGradiente, int[][] angulosBitmap) {

        int ancho = angulosBitmap.length;
        int alto = angulosBitmap[0].length;

        int t1 = 70;
        int t2 = 70;

        int[][] nuevaMagnitudGradiente = new int[ancho][alto];
        int[][] canny = new int[ancho][alto];

        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {

                int gradiente = matrizMagnitudGradiente[i][j];

                //Es borde
                if (Math.abs(gradiente) > 110) {

                    DireccionBorde direccionBorde = DireccionBorde.obtenerDireccionBorde(angulosBitmap[i][j]);

                    if (direccionBorde == DireccionBorde.HORIZONTAL) {

                        if ((matrizMagnitudGradiente[i][j-1] <= matrizMagnitudGradiente[i][j])
                                &&(matrizMagnitudGradiente[i][j+1] <= matrizMagnitudGradiente[i][j])){

                            nuevaMagnitudGradiente[i][j] = matrizMagnitudGradiente[i][j];
                            canny[i][j] = 1;
                            canny[i][j-1] = 0;
                            canny[i][j+1] = 0;
                        }
                    }
                    else if (direccionBorde == DireccionBorde.VERTICAL) {

                        if ((matrizMagnitudGradiente[i-1][j] <= matrizMagnitudGradiente[i][j])
                                &&(matrizMagnitudGradiente[i+1][j] <= matrizMagnitudGradiente[i][j])){

                            nuevaMagnitudGradiente[i][j] = matrizMagnitudGradiente[i][j];
                            canny[i][j] = 1;
                            canny[i-1][j] = 0;
                            canny[i+1][j] = 0;
                        }
                    }
                    else if (direccionBorde == DireccionBorde.DIAGONAL_DERECHA) {

                        if ((matrizMagnitudGradiente[i+1][j-1] <= matrizMagnitudGradiente[i][j])
                                &&(matrizMagnitudGradiente[i-1][j+1] <= matrizMagnitudGradiente[i][j])){

                            nuevaMagnitudGradiente[i][j] = matrizMagnitudGradiente[i][j];
                            canny[i][j] = 1;
                            canny[i-1][j+1] = 0;
                            canny[i+1][j-1] = 0;
                        }

                    }
                    else if (direccionBorde == DireccionBorde.DIAGONAL_IZQUIERDA) {

                        if ((matrizMagnitudGradiente[i-1][j-1] <= matrizMagnitudGradiente[i][j])
                                &&(matrizMagnitudGradiente[i+1][j+1] <= matrizMagnitudGradiente[i][j])){

                            nuevaMagnitudGradiente[i][j] = matrizMagnitudGradiente[i][j];
                            canny[i][j] = 1;
                            canny[i-1][j-1] = 0;
                            canny[i+1][j+1] = 0;
                        }
                    }
                }
            }
        }

        //UMBRALIZACION CON HISTERESIS
//        for (int i = 0; i < ancho; i++) {
//            for (int j = 0; j < alto; j++) {
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
//                        if (!tieneBordesVecinos && j < alto - 1) {
//                            tieneBordesVecinos = canny[i-1][j+1] == 1;
//                        }
//                    }
//
//                    if (!tieneBordesVecinos && i < ancho - 1) {
//                        tieneBordesVecinos = canny[i+1][j] == 1;
//
//                        if (!tieneBordesVecinos && j > 0) {
//                            tieneBordesVecinos = canny[i+1][j-1] == 1;
//                        }
//                        if (!tieneBordesVecinos && j < alto - 1) {
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

        return canny;
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
        CANNY_1_CALCULADO,
        CANNY_2_CALCULADO,
        CANNY_3_CALCULADO,
        CANNY_FINAL_CALCULADO,
    }
}
