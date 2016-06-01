package ar.edu.untref.procesamientoimagenes.tareas.bordes;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadBordes;
import ar.edu.untref.procesamientoimagenes.modelo.TipoImagen;
import ar.edu.untref.procesamientoimagenes.util.Operacion;
import ar.edu.untref.procesamientoimagenes.util.Transformacion;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarMetodoDelLaplacianoDelGaussiano extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarMetodoDelLaplacianoDelGaussiano.class.getSimpleName();
    private Float sigma;
    private ActividadBordes actividadBordes;
    private Bitmap bitmapOriginal;
    private TipoImagen tipoImagen;
    private Integer pendiente;

    public TareaAplicarMetodoDelLaplacianoDelGaussiano(Float sigma, ActividadBordes actividadBordes, Bitmap bitmapOriginal, TipoImagen tipoImagen, Integer pendiente) {

        this.sigma = sigma;
        this.actividadBordes = actividadBordes;
        this.bitmapOriginal = bitmapOriginal;
        this.tipoImagen = tipoImagen;
        this.pendiente = pendiente;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if (tipoImagen == TipoImagen.COLOR) {
            bitmapOriginal = Transformacion.colorAEscalaDeGrises(bitmapOriginal);
        }

        int [][] matrizPixelesLoG = new int[bitmapOriginal.getWidth()] [bitmapOriginal.getHeight()];

        //Genero la máscara en base a la fórmula del Laplaciano del Gaussiano
        double[][] mascaraLoG = generarMascaraLaplacianoDelGaussiano();
        int posicionCentralMascara = mascaraLoG.length / 2;

        //Paso la máscara

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado
                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        double valorMascara = mascaraLoG[xMascara][yMascara];
                        int nivelGrisPixel = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));

                        double operacion = nivelGrisPixel * valorMascara;
                        valorResultado += operacion;
                    }
                }

                int valorEntero = (int) valorResultado;
                matrizPixelesLoG[x][y] = valorEntero;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), Bitmap.Config.RGB_565);

        for (int x = 0; x < bitmapOriginal.getWidth(); x++) {
            for (int y = 0; y < bitmapOriginal.getHeight(); y++) {

                if (Operacion.hayCambioDeSignoPorFila(matrizPixelesLoG, x, y, pendiente) || Operacion.hayCambioDeSignoPorColumna(matrizPixelesLoG,x,y, pendiente)) {
                    bitmap.setPixel(x,y,Color.WHITE);
                } else {
                    bitmap.setPixel(x,y,Color.BLACK);
                }
            }
        }

        return bitmap;
    }

    private double[][] generarMascaraLaplacianoDelGaussiano() {

        int auxTamanio = (int) (3 * sigma);

        int posibleTamanio = auxTamanio % 2 == 0 ? auxTamanio : auxTamanio + 1;
        int tamanioMascara = sigma < 1 ? 5 : posibleTamanio + 1;
        double[][] matrizDeLaplacianoDelGaussiano = new double[tamanioMascara][tamanioMascara];

        double primerTermino = -1.0 * (Math.sqrt(2 * Math.PI) * Math.pow(sigma, 3.0));

        for (int x = 0; x < tamanioMascara; x++) {
            for (int y = 0; y < tamanioMascara; y++) {

                double segundoTermino = (Math.pow(x, 2.0) + Math.pow(y, 2.0)) / Math.pow(sigma, 2.0);
                matrizDeLaplacianoDelGaussiano[x][y] = primerTermino * (2 - segundoTermino) * Math.exp((-0.5) * segundoTermino);
            }
        }

        return matrizDeLaplacianoDelGaussiano;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        actividadBordes.bordesDetectados(bitmap, null);
    }
}
