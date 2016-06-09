package ar.edu.untref.procesamientoimagenes.tareas.filtros;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import ar.edu.untref.procesamientoimagenes.actividad.ActividadFiltros;

/**
 * Created by maribel on 4/10/16.
 */
public class TareaAplicarFiltroGaussiano extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_TAG = TareaAplicarFiltroGaussiano.class.getSimpleName();
    private ActividadFiltros actividadFiltros;
    private Bitmap bitmapOriginal;
    private Float sigma;

    public TareaAplicarFiltroGaussiano(ActividadFiltros actividadFiltros, Bitmap bitmapOriginal, Float sigma) {

        this.actividadFiltros = actividadFiltros;
        this.bitmapOriginal = bitmapOriginal;
        this.sigma = sigma;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        int auxTamanio = (int) (3 * sigma);
        int posibleTamanio = auxTamanio % 2 == 0 ? auxTamanio : auxTamanio + 1;
        int tamanioMascara = sigma < 1 ? 5 : posibleTamanio + 1;

        double[][] matrizFiltroGaussiano = new double[tamanioMascara][tamanioMascara];
        int posicionCentralMascara = tamanioMascara/2;

        for (int x = 0; x < tamanioMascara; x++) {

            for (int y = 0; y < tamanioMascara; y++) {
                matrizFiltroGaussiano[x][y] = obtenerGaussiano(x,y, posicionCentralMascara);
                System.out.print(matrizFiltroGaussiano[x][y]);
            }
            System.out.println("");
        }

        for (int x = posicionCentralMascara; x < bitmapOriginal.getWidth() - posicionCentralMascara; x++) {
            for (int y = posicionCentralMascara; y < bitmapOriginal.getHeight() - posicionCentralMascara; y++) {

                //Para cada pixel, recorro la máscara alrededor de ese pixel para calcular el valor resultado
                float valorResultado = 0F;

                for (int xMascaraEnImagen = x - posicionCentralMascara, xMascara = 0; xMascaraEnImagen <= x + posicionCentralMascara; xMascaraEnImagen ++, xMascara ++) {
                    for (int yMascaraEnImagen = y - posicionCentralMascara, yMascara = 0; yMascaraEnImagen <= y + posicionCentralMascara; yMascaraEnImagen ++, yMascara++) {

                        double valorMascara = matrizFiltroGaussiano[xMascara][yMascara];
                        int nivelGrisPixel = Color.red(bitmapOriginal.getPixel(xMascaraEnImagen, yMascaraEnImagen));

                        double operacion = nivelGrisPixel * valorMascara;
                        valorResultado += operacion;
                    }
                }

                int valorEntero = (int) valorResultado;
                mutableBitmap.setPixel(x,y, Color.rgb(valorEntero, valorEntero, valorEntero));
            }
        }

        //Pinto los bordes negros
        for (int x = 0; x < bitmapOriginal.getWidth(); x++) {
            for (int y = 0 ; y < bitmapOriginal.getHeight(); y ++) {

                if (x < posicionCentralMascara || x >= bitmapOriginal.getWidth() - posicionCentralMascara
                        || y < posicionCentralMascara || y >= bitmapOriginal.getHeight() - posicionCentralMascara) {
                    mutableBitmap.setPixel(x,y, Color.rgb(0, 0, 0));
                }
            }
        }

        return mutableBitmap;
    }

    private double obtenerGaussiano(int x, int y, int posicionCentralMascara) {

        int xEnMascara = x-posicionCentralMascara;
        int yEnMascara = y-posicionCentralMascara;
        return (1/
                (2 * Math.PI * Math.pow(sigma,2)))
                * Math.exp(-((Math.pow(xEnMascara,2) + Math.pow(yEnMascara,2))/(2 * Math.pow(sigma,2)))) ;
    }

    @Override
    protected void onPostExecute(Bitmap bitmapResultante) {
        super.onPostExecute(bitmapResultante);

        if (actividadFiltros != null) {
            actividadFiltros.filtroAplicado(bitmapResultante);
        }
    }
}
