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
            for (int j = 1; j < mutableBitmap.getWidth() - 1; j++) {

                for (int k = 1; k < mutableBitmap.getHeight() - 1; k++) {

                    int derivadaNorte = calcularDerivadaNorte(mutableBitmap, j, k);
                    int derivadaSur = calcularDerivadaSur(mutableBitmap, j, k);
                    int derivadaOeste = calcularDerivadaOeste(mutableBitmap, j, k);
                    int derivadaEste = calcularDerivadaEste(mutableBitmap, j, k);

                    int color = (int) (Color.red(mutableBitmap.getPixel(j, k)) +
                            (LAMBDA * (derivadaNorte + derivadaSur + derivadaOeste + derivadaEste)));

                    matriz[j][k] = color;
                }
            }

            mutableBitmap = Operacion.hacerTransformacionLineal(matriz);
        }

        imageView.setImageBitmap(mutableBitmap);
    }


    @OnClick(R.id.anisotropica)
    public void difusionAnisotropica() {

        String sigma = this.sigma.getText().toString().trim();

        if (!sigma.isEmpty()) {

            float valorSigma = Float.valueOf(sigma);

            int cantidadRepeticiones = Integer.parseInt(repeticiones.getText().toString());
            Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

            for (int i = 0; i < cantidadRepeticiones; i++) {

                int[][] matriz = new int[mutableBitmap.getWidth()][mutableBitmap.getHeight()];
                for (int j = 1; j < mutableBitmap.getWidth() - 1; j++) {

                    for (int k = 1; k < mutableBitmap.getHeight() - 1; k++) {

                        int derivadaNorte = calcularDerivadaNorte(mutableBitmap, j, k);
                        int derivadaSur = calcularDerivadaSur(mutableBitmap, j, k);
                        int derivadaOeste = calcularDerivadaOeste(mutableBitmap, j, k);
                        int derivadaEste = calcularDerivadaEste(mutableBitmap, j, k);

                        boolean lorentz = coeficiente.getCheckedRadioButtonId() == R.id.lorentz;

                        double coeficienteNorte = lorentz ? gradienteLorentz(valorSigma, derivadaNorte) : gradienteLeclerc(valorSigma, derivadaNorte);
                        double coeficienteSur = lorentz ? gradienteLorentz(valorSigma, derivadaSur) : gradienteLeclerc(valorSigma, derivadaSur);
                        double coeficienteOeste = lorentz ? gradienteLorentz(valorSigma, derivadaOeste) : gradienteLeclerc(valorSigma, derivadaOeste);
                        double coeficienteEste = lorentz ? gradienteLorentz(valorSigma, derivadaEste) : gradienteLeclerc(valorSigma, derivadaEste);

                        double DnCn = derivadaNorte * coeficienteNorte;
                        double DsCs = derivadaSur * coeficienteSur;
                        double DeCe = derivadaEste * coeficienteOeste;
                        double DoCo = derivadaOeste * coeficienteEste;

                        int color = (int) (Color.red(mutableBitmap.getPixel(j, k)) +
                                (LAMBDA * (DnCn + DsCs + DeCe + DoCo)));

                        matriz[j][k] = color;
                    }
                }

                mutableBitmap = Operacion.hacerTransformacionLineal(matriz);
            }

            imageView.setImageBitmap(mutableBitmap);
        }
        else {
            this.sigma.setError("Requerido");
        }
    }

    public double gradienteLorentz(double sigma, double derivada) {
        return (1/(1 + (Math.pow(Math.abs(derivada), 2) / Math.pow(sigma, 2))));
    }

    public double gradienteLeclerc(double sigma, double derivada) {
        return Math.exp(((-1) * Math.pow(Math.abs(derivada), 2)) / Math.pow(sigma, 2));
    }

    private int calcularDerivadaEste(Bitmap mutableBitmap, int x, int y) {

        int derivada = 0;

        if (y != mutableBitmap.getWidth()) {

            int color = Color.red(mutableBitmap.getPixel(x, y));
            derivada = Color.red(mutableBitmap.getPixel(x - 1, y)) - color;
        }

        return derivada;
    }

    private int calcularDerivadaOeste(Bitmap mutableBitmap, int x, int y) {

        int derivada = 0;

        if (y != 0) {

            int color = Color.red(mutableBitmap.getPixel(x, y));
            derivada = Color.red(mutableBitmap.getPixel(x + 1, y)) - color;
        }

        return derivada;
    }

    private int calcularDerivadaSur(Bitmap mutableBitmap, int x, int y) {

        int derivada = 0;

        if (x != mutableBitmap.getHeight()) {

            int color = Color.red(mutableBitmap.getPixel(x, y));
            derivada = Color.red(mutableBitmap.getPixel(x, y + 1)) - color;
        }

        return derivada;
    }

    private int calcularDerivadaNorte(Bitmap mutableBitmap, int x, int y) {

        int derivada = 0;

        if (x != 0) {

            int color = Color.red(mutableBitmap.getPixel(x, y));
            derivada = Color.red(mutableBitmap.getPixel(x, y - 1)) - color;
        }

        return derivada;
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
