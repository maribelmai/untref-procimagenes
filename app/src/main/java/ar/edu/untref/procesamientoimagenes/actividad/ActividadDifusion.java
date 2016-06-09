package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import ar.edu.untref.procesamientoimagenes.util.Operacion;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribel on 5/11/16.
 */
public class ActividadDifusion extends ActividadBasica {

    private static final String LOG_TAG = ActividadBordes.class.getSimpleName();
    private static final float LAMBDA = 0.25f;
    private File imagen;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.sigma)
    EditText sigma;

    @Bind(R.id.repeticiones)
    EditText repeticiones;

    @Bind(R.id.coeficiente)
    RadioGroup coeficiente;

    private Bitmap bitmapOriginal;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Difusión");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle("Espere por favor");
        this.progressDialog.setMessage("Generando difusión...");

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmapOriginal = bitmap.copy(Bitmap.Config.RGB_565, true);
    }

    @OnClick(R.id.isotropica)
    public void difusionIsotropica() {

        int cantidadRepeticiones = Integer.parseInt(repeticiones.getText().toString());
        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        for (int i = 0; i < cantidadRepeticiones; i++) {

            int[][] matriz = new int[mutableBitmap.getWidth()][mutableBitmap.getHeight()];
            for (int x = 1; x < mutableBitmap.getWidth() - 1; x++) {

                for (int y = 1; y < mutableBitmap.getHeight() - 1; y++) {

                    int derivadaNorte = calcularDerivadaNorte(mutableBitmap, x, y);
                    int derivadaSur = calcularDerivadaSur(mutableBitmap, x, y);
                    int derivadaOeste = calcularDerivadaOeste(mutableBitmap, x, y);
                    int derivadaEste = calcularDerivadaEste(mutableBitmap, x, y);

                    int color = (int) (Color.red(mutableBitmap.getPixel(x, y)) +
                            (LAMBDA * (derivadaNorte + derivadaSur + derivadaOeste + derivadaEste)));

                    matriz[x][y] = color;
                }
            }

            mutableBitmap = Operacion.hacerTransformacionLineal(matriz);
        }

        imageView.setImageBitmap(mutableBitmap);
    }


    @OnClick(R.id.anisotropica)
    public void difusionAnisotropica() {

        String sigma = this.sigma.getText().toString().trim();
        String repeticionesString =  repeticiones.getText().toString().trim();

        if (!sigma.isEmpty() && !repeticionesString.isEmpty()) {

            float valorSigma = Float.valueOf(sigma);
            int cantidadRepeticiones = Integer.parseInt(repeticionesString);

            Bitmap resultado = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);
            aplicarDifusionAnisotropica(resultado, cantidadRepeticiones, valorSigma);

            imageView.setImageBitmap(resultado);
        }

        if (sigma.isEmpty()) {
            this.sigma.setError("Requerido");
        }

        if (repeticionesString.isEmpty()) {
            this.repeticiones.setError("Requerido");
        }

    }

    private void aplicarDifusionAnisotropica(Bitmap resultado, int cantidadRepeticiones, float valorSigma) {

        int ancho = resultado.getWidth();
        int alto = resultado.getHeight();

        int[][] coloresImagen = new int[alto][ancho];

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                coloresImagen[x][y] = Color.red(bitmapOriginal.getPixel(x,y));
            }
        }

        for (int repeticion = 0; repeticion < cantidadRepeticiones; repeticion ++) {
            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {

                    int nivelDeGris = coloresImagen[x][y];

                    float derivadaNorte = calcularDerivadaNorte(coloresImagen, x, y);
                    float derivadaEste = calcularDerivadaEste(coloresImagen, x, y);
                    float derivadaOeste = calcularDerivadaOeste(coloresImagen, x, y);
                    float derivadaSur = calcularDerivadaSur(coloresImagen, x, y);

                    boolean lorentz = coeficiente.getCheckedRadioButtonId() == R.id.lorentz;

                    double coeficienteNorte = lorentz ? gradienteLorentz(valorSigma, derivadaNorte) : gradienteLeclerc(valorSigma, derivadaNorte);
                    double coeficienteSur = lorentz ? gradienteLorentz(valorSigma, derivadaSur) : gradienteLeclerc(valorSigma, derivadaSur);
                    double coeficienteOeste = lorentz ? gradienteLorentz(valorSigma, derivadaOeste) : gradienteLeclerc(valorSigma, derivadaOeste);
                    double coeficienteEste = lorentz ? gradienteLorentz(valorSigma, derivadaEste) : gradienteLeclerc(valorSigma, derivadaEste);

                    double DnIijCnij = derivadaNorte * coeficienteNorte;
                    double DsIijCsij = derivadaSur * coeficienteSur;
                    double DeIijCeij = derivadaEste * coeficienteEste;
                    double DoIijCoij = derivadaOeste * coeficienteOeste;

                    double nuevoValor = nivelDeGris + LAMBDA * (DnIijCnij + DsIijCsij + DeIijCeij + DoIijCoij);

                    coloresImagen[x][y] = (int) nuevoValor;
                }
            }

            coloresImagen = Operacion.obtenerMatrizTransformada(coloresImagen);

            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {

                    int nuevoGris = coloresImagen[x][y];
                    resultado.setPixel(x, y, Color.rgb(nuevoGris, nuevoGris, nuevoGris));
                }
            }
        }
    }

    private float gradienteLorentz(double sigma, float derivada) {
        return 1/ ( ((float) (Math.pow(Math.abs(derivada), 2) / Math.pow(sigma, 2))) + 1);
    }

    public float gradienteLeclerc(double sigma, double derivada) {
        return (float) Math.exp(((-1) * Math.pow(Math.abs(derivada), 2)) / Math.pow(sigma, 2));
    }

    private int calcularDerivadaEste(int[][] imagen, int x, int y) {

        int colorVecino;

        int coordenadaVecina = y + 1;
        int colorActual = imagen[x][y];

        if (coordenadaVecina < imagen.length && coordenadaVecina >= 0) {
            colorVecino = imagen[x][coordenadaVecina];

        } else {
            colorVecino = colorActual;
        }

        return colorVecino - colorActual;
    }

    private int calcularDerivadaNorte(int[][] imagen, int x, int y) {

        int colorCorrido;

        int coordenadaVecina = x - 1;
        int colorActual = imagen[x][y];

        if (coordenadaVecina < imagen[0].length && coordenadaVecina >= 0) {
            colorCorrido = imagen[coordenadaVecina][y];

        } else {
            colorCorrido = colorActual;
        }

        return colorCorrido - colorActual;
    }

    private int calcularDerivadaOeste(int[][] imagen, int x, int y) {

        int colorVecino;

        int coordenadaVecina = y - 1;
        int colorActual = imagen[x][y];

        if (coordenadaVecina < imagen.length && coordenadaVecina >= 0) {
            colorVecino = imagen[x][coordenadaVecina];

        } else {
            colorVecino = colorActual;
        }

        return colorVecino - colorActual;
    }

    private int calcularDerivadaSur(int[][] imagen, int x, int y) {

        int colorVecino;

        int coordenadaVecina = x + 1;
        int colorActual = imagen[x][y];

        if (coordenadaVecina < imagen[0].length && coordenadaVecina >= 0) {
            colorVecino = imagen[coordenadaVecina][y];

        } else {
            colorVecino = colorActual;
        }

        return colorVecino - colorActual;
    }
//ke

    private int calcularDerivadaEste(Bitmap imagen, int x, int y) {

        int colorVecino;

        int coordenadaVecina = y + 1;
        int colorActual = Color.red(imagen.getPixel(x, y));

        if (coordenadaVecina < imagen.getWidth() && coordenadaVecina >= 0) {
            colorVecino = Color.red(imagen.getPixel(x, coordenadaVecina));

        } else {
            colorVecino = colorActual;
        }

        return colorVecino - colorActual;
    }

    private int calcularDerivadaOeste(Bitmap imagen, int x, int y) {

        int colorVecino;

        int coordenadaVecina = y - 1;
        int colorActual = Color.red(imagen.getPixel(x, y));

        if (coordenadaVecina < imagen.getWidth() && coordenadaVecina >= 0) {
            colorVecino = Color.red(imagen.getPixel(x, coordenadaVecina));

        } else {
            colorVecino = colorActual;
        }

        return colorVecino - colorActual;
    }

    private int calcularDerivadaSur(Bitmap imagen, int x, int y) {

        int colorVecino;

        int coordenadaVecina = x + 1;
        int colorActual = Color.red(imagen.getPixel(x, y));

        if (coordenadaVecina < imagen.getHeight() && coordenadaVecina >= 0) {
            colorVecino = Color.red(imagen.getPixel(coordenadaVecina, y));

        } else {
            colorVecino = colorActual;
        }

        return colorVecino - colorActual;
    }

    private int calcularDerivadaNorte(Bitmap imagen, int x, int y) {

        int colorCorrido;

        int coordenadaVecina = x - 1;
        int colorActual = Color.red(imagen.getPixel(x, y));

        if (coordenadaVecina < imagen.getHeight() && coordenadaVecina >= 0) {
            colorCorrido = Color.red(imagen.getPixel(coordenadaVecina, y));

        } else {
            colorCorrido = colorActual;
        }

        return colorCorrido - colorActual;
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_difusion;
    }

    @Override
    public void finish() {

        String nombreOriginal = imagen.getName();
        File file = null;

        try {
            file = getAplicacion().guardarArchivo(((BitmapDrawable) imageView.getDrawable()).getBitmap(), "/tmp/", nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf(".")));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Temporal no se pudo guardar: " + e);
        }

        Intent intent = new Intent();
        intent.putExtra(Constante.EXTRA_IMAGEN, file);
        setResult(Constante.RESULT_CODE_IMAGEN_MODIFICADA, intent);
        super.finish();
    }
}
