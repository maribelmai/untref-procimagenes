package ar.edu.untref.procesamientoimagenes.actividad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import ar.edu.untref.procesamientoimagenes.util.Transformacion;
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

    @Bind(R.id.contaminacionRuidoGaussiano)
    EditText contaminacionRuidoGaussiano;

    @Bind(R.id.valorDesvio)
    EditText desvio;

    @Bind(R.id.valorMedia)
    EditText media;

    @Bind(R.id.phi)
    EditText phi;

    @Bind(R.id.lamda)
    EditText lamda;

    @Bind(R.id.contaminacionSalYPimienta)
    EditText contaminacionSalYPimienta;

    @Bind(R.id.p0)
    EditText p0;

    @Bind(R.id.p1)
    EditText p1;

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

        ocultarTeclado();

        if (!desvio.getText().toString().trim().isEmpty() && !media.getText().toString().trim().isEmpty()
                && !contaminacionRuidoGaussiano.getText().toString().trim().isEmpty()) {

            int[][] matrizPixelesR = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
            int[][] matrizPixelesG = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
            int[][] matrizPixelesB = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];

            double desvio = Double.parseDouble(this.desvio.getText().toString());
            double media = Double.parseDouble(this.media.getText().toString());

            long cantidadTotalPixeles = bitmapOriginal.getWidth() * bitmapOriginal.getHeight();
            int porcentajeContaminacion = Integer.parseInt(contaminacionRuidoGaussiano.getText().toString());
            int cantidadPixelesAContaminar = (int) (cantidadTotalPixeles * porcentajeContaminacion / 100);

            boolean necesarioContaminarPorcentaje = porcentajeContaminacion < 100;

            List<Point> pixeles = new ArrayList<>();
            if (necesarioContaminarPorcentaje) {
                pixeles = obtenerPixelesAleatorios(cantidadPixelesAContaminar);
            }
            Random random = new Random();

            for (int x = 0; x < bitmapOriginal.getWidth(); x++) {

                for (int y = 0; y < bitmapOriginal.getHeight(); y++) {

                    Point p = new Point(x,y);

                    int pixelOriginal = bitmapOriginal.getPixel(x, y);
                    int nivelRojo = Color.red(pixelOriginal);
                    int nivelVerde = Color.green(pixelOriginal);
                    int nivelAzul = Color.blue(pixelOriginal);

                    int nuevoNivelRojo = nivelRojo;
                    int nuevoNivelVerde = nivelAzul;
                    int nuevoNivelAzul = nivelVerde;

                    if (necesarioContaminarPorcentaje && pixeles.contains(p) || !necesarioContaminarPorcentaje) {

                        double randomCreado = random.nextGaussian() * desvio + media;

                        nuevoNivelRojo = (int) (nivelRojo + randomCreado);
                        nuevoNivelVerde = (int) (nivelVerde + randomCreado);
                        nuevoNivelAzul = (int) (nivelAzul + randomCreado);
                    }

                    matrizPixelesR[x][y] = nuevoNivelRojo;
                    matrizPixelesG[x][y] = nuevoNivelVerde;
                    matrizPixelesB[x][y] = nuevoNivelAzul;
                }
            }

            imageView.setImageBitmap(Transformacion.hacerTransformacionLineal(matrizPixelesR, matrizPixelesG, matrizPixelesB));
            sufijoGuardar = "gaussD" + desvio + "m" + media;
        }
        else {
            Toast.makeText(this, "Desvío y media no pueden ser vacíos", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Point> obtenerPixelesAleatorios(int cantidad) {

        Random random = new Random();
        List<Point> pixeles= new ArrayList<>();

        for(int i = 0; i<cantidad; i++){

            int xRandom = random.nextInt(bitmapOriginal.getWidth());
            int yRandom = random.nextInt(bitmapOriginal.getHeight());

            Point pixel = new Point(xRandom, yRandom);

            while (pixeles.contains(pixel)) {
                xRandom = random.nextInt(bitmapOriginal.getWidth());
                yRandom = random.nextInt(bitmapOriginal.getHeight());
                pixel = new Point(xRandom, yRandom);
            }

            pixeles.add(pixel);
        }

        return pixeles;
    }

    @OnClick(R.id.ruidoRayleigh)
    public void aplicarRuidoRayleigh(){

        ocultarTeclado();

        if (!phi.getText().toString().trim().isEmpty()) {

            int[][] matrizPixelesR = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
            int[][] matrizPixelesG = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
            int[][] matrizPixelesB = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];

            for (int x = 0; x < bitmapOriginal.getWidth(); x++) {

                for (int y = 0; y < bitmapOriginal.getHeight(); y++) {

                    float phi = Float.parseFloat(this.phi.getText().toString());
                    double randomCreado = generarAleatorioRayleigh(phi);

                    int pixelOriginal = bitmapOriginal.getPixel(x, y);
                    int nivelRojo = Color.red(pixelOriginal);
                    int nivelVerde = Color.green(pixelOriginal);
                    int nivelAzul = Color.blue(pixelOriginal);
                    int nuevoColorR = (int) (nivelRojo * randomCreado);
                    int nuevoColorG = (int) (nivelVerde * randomCreado);
                    int nuevoColorB = (int) (nivelAzul * randomCreado);
                    matrizPixelesR[x][y] = nuevoColorR;
                    matrizPixelesG[x][y] = nuevoColorG;
                    matrizPixelesB[x][y] = nuevoColorB;
                }
            }
                imageView.setImageBitmap(Transformacion.hacerCompresionRangoDinamico(matrizPixelesR, matrizPixelesG, matrizPixelesB));
        }
        else {
            Toast.makeText(this, "Phi no puede ser vacío", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.ruidoExponencial)
    public void aplicarRuidoExponencial(){
        ocultarTeclado();

        if (!lamda.getText().toString().trim().isEmpty()) {

            int[][] matrizPixelesR = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
            int[][] matrizPixelesG = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];
            int[][] matrizPixelesB = new int[bitmapOriginal.getWidth()][bitmapOriginal.getHeight()];

            for (int x = 0; x < bitmapOriginal.getWidth(); x++) {

                for (int y = 0; y < bitmapOriginal.getHeight(); y++) {

                    double lamda = Double.parseDouble(this.lamda.getText().toString());
                    double randomCreado = generarAleatorioExponencial(lamda);

                    int pixelOriginal = bitmapOriginal.getPixel(x, y);
                    int nivelRojo = Color.red(pixelOriginal);
                    int nivelVerde = Color.green(pixelOriginal);
                    int nivelAzul = Color.blue(pixelOriginal);
                    int nuevoColorR = (int) (nivelRojo * randomCreado);
                    int nuevoColorG = (int) (nivelVerde * randomCreado);
                    int nuevoColorB = (int) (nivelAzul * randomCreado);
                    matrizPixelesR[x][y] = nuevoColorR;
                    matrizPixelesG[x][y] = nuevoColorG;
                    matrizPixelesB[x][y] = nuevoColorB;
                }
            }
            imageView.setImageBitmap(Transformacion.hacerCompresionRangoDinamico(matrizPixelesR, matrizPixelesG, matrizPixelesB));
        }
        else {
            Toast.makeText(this, "Lamda no puede ser vacío", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.ruidoSalyPimienta)
    public void aplicarRuidoSalyPimienta(){

        ocultarTeclado();

        if (!contaminacionSalYPimienta.getText().toString().trim().isEmpty()) {

            String p0Text = p0.getText().toString().trim();
            String p1Text = p1.getText().toString().trim();

            if (p0Text.isEmpty() || p1Text.isEmpty()) {

                Toast.makeText(this, "Ingrese valor para p0 y p1", Toast.LENGTH_SHORT).show();
            }
            else {

                float p0 = Float.parseFloat(p0Text);
                float p1 = Float.parseFloat(p1Text);

                if (p0 < 0 || p1 < 0 || p0 > 1 || p1 > 1 || p0 > p1) {
                    Toast.makeText(this, "Valores no válidos para p1 y p0", Toast.LENGTH_SHORT).show();
                }
                else {

                    Random generadorRandom = new Random(1);

                    long cantidadTotalPixeles = bitmapOriginal.getWidth() * bitmapOriginal.getHeight();
                    int porcentajeContaminacion = Integer.parseInt(contaminacionSalYPimienta.getText().toString());
                    int cantidadPixelesAContaminar = (int) (cantidadTotalPixeles * porcentajeContaminacion / 100);

                    List<Point> pixeles = obtenerPixelesAleatorios(cantidadPixelesAContaminar);
                    Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

                    for (Point point: pixeles) {

                        float random = generadorRandom.nextFloat();

                        if (random >= p1) {
                            mutableBitmap.setPixel(point.x, point.y, Color.WHITE);
                        }
                        else if (random <= p0){
                            mutableBitmap.setPixel(point.x, point.y, Color.BLACK);
                        }
                    }

                    imageView.setImageBitmap(mutableBitmap);
                }
            }
        }
        else {
            Toast.makeText(this, "Ingrese un nivel de contaminación", Toast.LENGTH_SHORT).show();
        }
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

    private double generarAleatorioExponencial(double a) {

        return -a*Math.log(Math.random());
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
