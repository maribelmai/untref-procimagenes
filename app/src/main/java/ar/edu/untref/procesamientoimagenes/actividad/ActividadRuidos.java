package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribel on 4/6/16.
 */
public class ActividadRuidos extends ActividadBasica {

    private static final String LOG_TAG = ActividadRuidos.class.getSimpleName();
    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    private Bitmap bitmapOriginal;
    private File imagen;

    private String sufijoGuardar = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Ruidos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);

        Bitmap bitmap= ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmapOriginal = bitmap.copy(Bitmap.Config.RGB_565, true);
    }

    @OnClick(R.id.ruidoGaussiano)
    public void aplicarRuidoGaussiano(){

        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        double desvio = 20D;
        double media = 50D;

        Random random = new Random();

        for (int x = 0; x < bitmapOriginal.getWidth(); x++) {

            for (int y = 0; y < bitmapOriginal.getHeight(); y++) {
                double randomCreado = random.nextGaussian() * desvio + media;

                int pixelOriginal = bitmapOriginal.getPixel(x, y);
                int nivelGris = Color.red(pixelOriginal);
                int nuevoColor = (int) (nivelGris + randomCreado);
                mutableBitmap.setPixel(x, y, Color.rgb(nuevoColor, nuevoColor, nuevoColor));
            }
        }

        imageView.setImageBitmap(mutableBitmap);
        sufijoGuardar = "gaussD" + desvio + "m" + media;
    }

    private List<Point> obtenerPixelesAleatorios(int cantidad) {

        List<Point> pixeles= new ArrayList<>();

        for(int i = 0; i<cantidad; i++){

            int xRandom = new Random().nextInt(bitmapOriginal.getWidth());
            int yRandom = new Random().nextInt(bitmapOriginal.getHeight());

            Point pixel = new Point(xRandom, yRandom);
            pixeles.add(pixel);
        }

        return pixeles;
    }

    @OnClick(R.id.ruidoRayleigh)
    public void aplicarRuidoRayleigh(){
        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        for (int x = 0; x < bitmapOriginal.getWidth(); x++) {

            for (int y = 0; y < bitmapOriginal.getHeight(); y++) {
                double randomCreado = generarAleatorioRayleigh(0.1f);

                int pixelOriginal = bitmapOriginal.getPixel(x, y);
                int nivelGris = Color.red(pixelOriginal);
                int nuevoColor = (int) (nivelGris * randomCreado);
                mutableBitmap.setPixel(x, y, Color.rgb(nuevoColor, nuevoColor, nuevoColor));
            }
        }

        imageView.setImageBitmap(mutableBitmap);

    }

    @OnClick(R.id.ruidoExponencial)
    public void aplicarRuidoExponencial(){

    }

    @OnClick(R.id.ruidoSalyPimienta)
    public void aplicarRuidoSalyPimienta(){

    }

    private float generarAleatorioRayleigh(float phi) {

        float x;
        float y;

        do
            x = (float) Math.random();
        while (x <= 0); // x no puede ser cero

        y = (float) (Float.valueOf(phi) * (Math.sqrt( (-2) * Math.log10(1-x) ) ));
        return y;
    }

    private Bitmap hacerTransformacionLinealMultiplicacion(Bitmap bitmap1) {

        int[][] matrizPixeles = new int[bitmap1.getWidth()][bitmap1.getHeight()];

        for (int x = 0; x < bitmap1.getWidth(); x++) {

            for (int y = 0; y < bitmap1.getHeight(); y++) {

                int valorPixelBitmap = Color.red(bitmap1.getPixel(x, y));

                matrizPixeles[x][y] = valorPixelBitmap;
            }
        }

        int MAXIMO_POSIBLE = 255;
        int valorMinimo = matrizPixeles[0][0];
        int valorMaximo = matrizPixeles[0][0];

        //Obtengo mínimo y máximo
        for (int x = 0; x < matrizPixeles.length; x++) {

            for (int y = 0; y < matrizPixeles[0].length; y++) {

                int pixel = matrizPixeles[x][y];

                if (pixel < valorMinimo) {
                    valorMinimo = pixel;
                } else if (pixel > valorMaximo) {
                    valorMaximo = pixel;
                }
            }
        }

        Log.i(LOG_TAG, "Valor mínimo: " + valorMinimo);
        Log.i(LOG_TAG, "Valor máximo: " + valorMaximo);

        Bitmap bitmap = Bitmap.createBitmap(matrizPixeles.length, matrizPixeles[0].length, Bitmap.Config.RGB_565);

        if (valorMaximo > MAXIMO_POSIBLE) {

            //Aplico transformación en un rango de -255 a 255 No pueden haber colores negativos -> Los llevo a 0
            for (int x = 0; x < matrizPixeles.length; x++) {

                for (int y = 0; y < matrizPixeles[0].length; y++) {

                    int pixel = matrizPixeles[x][y];
                    int nuevoPixel = pixel * MAXIMO_POSIBLE / valorMaximo;

                    bitmap.setPixel(x, y, Color.rgb(nuevoPixel, nuevoPixel, nuevoPixel));

                    //Log.i(LOG_TAG, pixel + " --> " + nuevoPixel);
                }
            }
        }

        return bitmap;

    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_ruidos;
    }

    @Override
    public void finish() {

        String nombreOriginal = imagen.getName();
        File file = null;

        try {
            file = getAplicacion().guardarArchivo(((BitmapDrawable) imageView.getDrawable()).getBitmap(), "/tmp/", nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + sufijoGuardar + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf(".")));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Temporal no se pudo guardar: " + e);
        }

        Intent intent = new Intent();
        intent.putExtra(Constante.EXTRA_IMAGEN, file);
        setResult(Constante.RESULT_CODE_IMAGEN_MODIFICADA, intent);
        super.finish();
    }
}
