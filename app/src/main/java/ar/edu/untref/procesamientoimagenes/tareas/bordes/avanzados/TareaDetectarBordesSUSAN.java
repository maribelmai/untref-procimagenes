package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordesAvanzados;

/**
 * Created by maribelmai on 14/6/16.
 */
public class TareaDetectarBordesSUSAN extends AsyncTask<Void, Void, Bitmap> {

    public static final int UMBRAL = 10;
    private static final String TAG = TareaDetectarBordesSUSAN.class.getSimpleName();
    private Bitmap bitmapOriginal;
    private ActividadBordesAvanzados actividadBordesAvanzados;
    private TipoSUSAN tipoSUSAN;

    public TareaDetectarBordesSUSAN(Bitmap bitmapOriginal, ActividadBordesAvanzados actividadBordesAvanzados, TipoSUSAN tipoSUSAN) {
        this.bitmapOriginal = bitmapOriginal;
        this.actividadBordesAvanzados = actividadBordesAvanzados;
        this.tipoSUSAN = tipoSUSAN;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        Bitmap bitmapNuevo = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        int[][] matrizSUSAN = generarMatrizSUSAN();
        int ancho = bitmapOriginal.getWidth();
        int alto = bitmapOriginal.getHeight();

        int posicionCentralMascara = matrizSUSAN.length/2;

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {

                int rojoCentral = Color.red(bitmapOriginal.getPixel(x, y));
                int verdeCentral = Color.green(bitmapOriginal.getPixel(x, y));
                int azulCentral = Color.blue(bitmapOriginal.getPixel(x, y));
                int contador = 0;
                int pixeles = 0;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen++, yMascara ++) {

                        if (matrizSUSAN[xMascara][yMascara] == 1
                                && xMascaraEnImagen > 0 && yMascaraEnImagen > 0
                                && xMascaraEnImagen < bitmapOriginal.getWidth() && yMascaraEnImagen < bitmapOriginal.getHeight()) {

                            int rojo = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));
                            int verde = Color.green(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));
                            int azul = Color.blue(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));

                            boolean esElMismoColor = (Math.abs(rojoCentral - rojo) <= UMBRAL
                                                        && Math.abs(verdeCentral - verde) <= UMBRAL
                                                        && Math.abs(azulCentral - azul) <= UMBRAL);

                            if (esElMismoColor) {
                                contador ++;
                            }

                            pixeles ++;
                        }
                    }
                }

                float resultado = 1 - ((float)contador/pixeles);

                if (Math.abs(resultado - 0.5) < 0.2 && (tipoSUSAN == TipoSUSAN.BORDES || tipoSUSAN == TipoSUSAN.AMBOS) ) {

                    Log.i(TAG, "BORDE --> " + resultado);
                    bitmapNuevo.setPixel(x,y, Color.RED);
                }
                else if (Math.abs(resultado - 0.75) < 0.2 && (tipoSUSAN == TipoSUSAN.ESQUINAS || tipoSUSAN == TipoSUSAN.AMBOS)) {

                    Log.i(TAG, "ESQUINA --> " + resultado);
                    bitmapNuevo.setPixel(x,y, Color.GREEN);
                }
            }
        }

        return bitmapNuevo;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        this.actividadBordesAvanzados.recibirSUSAN(bitmap);
    }

    private int[][] generarMatrizSUSAN() {

        int[][] matrizSUSAN = new int[7][7];

        matrizSUSAN[0] = new int[]{0,0,1,1,1,0,0};
        matrizSUSAN[1] = new int[]{0,1,1,1,1,1,0};
        matrizSUSAN[2] = new int[]{1,1,1,1,1,1,1};
        matrizSUSAN[3] = new int[]{1,1,1,1,1,1,1};
        matrizSUSAN[4] = new int[]{1,1,1,1,1,1,1};
        matrizSUSAN[5] = new int[]{0,1,1,1,1,1,0};
        matrizSUSAN[6] = new int[]{0,0,1,1,1,0,0};

        return matrizSUSAN;
    }

    public enum TipoSUSAN {

        BORDES,
        ESQUINAS,
        AMBOS
    }
}
