package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough.MatrizAcumuladora;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough.TransformadaDeHoughCirculos;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.hough.TransformadaDeHoughRectas;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribelmai on 20/6/16.
 */
public class ActividadDeteccionFormas extends ActividadBasica {

    private static final String TAG = ActividadDeteccionFormas.class.getSimpleName();
    private File imagen;
    private Bitmap bitmapOriginal;
    private ProgressDialog progressDialogBordes;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.min_x)
    EditText minX;

    @Bind(R.id.max_x)
    EditText maxX;

    @Bind(R.id.min_y)
    EditText minY;

    @Bind(R.id.max_y)
    EditText maxY;

    @Bind(R.id.discretizaciones_x)
    EditText discretizacionesX;

    @Bind(R.id.discretizaciones_y)
    EditText discretizacionesY;

    @Bind(R.id.centro_x_min)
    EditText minCentroX;

    @Bind(R.id.centro_x_max)
    EditText maxCentroX;

    @Bind(R.id.centro_y_min)
    EditText minCentroY;

    @Bind(R.id.centro_y_max)
    EditText maxCentroY;

    @Bind(R.id.radio_min)
    EditText minRadio;

    @Bind(R.id.radio_max)
    EditText maxRadio;

    @Bind(R.id.discretizaciones_centro_x)
    EditText discretizacionesCentroX;

    @Bind(R.id.discretizaciones_centro_y)
    EditText discretizacionesCentroY;

    @Bind(R.id.discretizaciones_radio)
    EditText discretizacionesRadio;

    private static final float EPSILON = 0.5F;
    private static final int MINIMO_PUNTOS_RECTA = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Hough");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.progressDialogBordes = new ProgressDialog(this);
        this.progressDialogBordes.setCancelable(false);
        this.progressDialogBordes.setTitle("Espere por favor");
        this.progressDialogBordes.setMessage("Detectando ...");

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmapOriginal = bitmap.copy(Bitmap.Config.RGB_565, true);
    }

    @OnClick(R.id.detectar_rectas)
    public void detectarRectas() {

        String minXValor = minX.getText().toString().trim();
        String maxXValor = maxX.getText().toString().trim();
        String minYValor = minY.getText().toString().trim();
        String maxYValor = maxY.getText().toString().trim();
        String discretizacionesX = this.discretizacionesX.getText().toString().trim();
        String discretizacionesY = this.discretizacionesY.getText().toString().trim();

        if (minXValor.isEmpty() || maxXValor.isEmpty() || minYValor.isEmpty() || maxYValor.isEmpty()
                || discretizacionesY.isEmpty() || discretizacionesX.isEmpty()) {
            Toast.makeText(this, "Especificar X e Y y discretizaciones", Toast.LENGTH_LONG).show();
        } else {

            int minX = Integer.valueOf(minXValor);
            int maxX = Integer.valueOf(maxXValor);
            int minY = Integer.valueOf(minYValor);
            int maxY = Integer.valueOf(maxYValor);

            int discretizacionesDeX = Integer.valueOf(discretizacionesX);
            int discretizacionesDeY = Integer.valueOf(discretizacionesY);

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            MatrizAcumuladora matriz = new MatrizAcumuladora(minY, maxY, minX, maxX, discretizacionesDeY, discretizacionesDeX);
            Bitmap bitmapTransformado = TransformadaDeHoughRectas.aplicarTransformadaDeHough(bitmap.copy(Bitmap.Config.RGB_565, true), matriz);
            imageView.setImageBitmap(bitmapTransformado);
        }
    }

    @OnClick(R.id.detectar_circulos)
    public void detectarCirculos() {

        String minCentroXValor = minCentroX.getText().toString().trim();
        String maxCentroXValor = maxCentroX.getText().toString().trim();
        String minCentroYValor = minCentroY.getText().toString().trim();
        String maxCentroYValor = maxCentroY.getText().toString().trim();
        String minRadioValor = minRadio.getText().toString().trim();
        String maxRadioValor = maxRadio.getText().toString().trim();
        String discretizacionesCentroX = this.discretizacionesCentroX.getText().toString().trim();
        String discretizacionesCentroY = this.discretizacionesCentroY.getText().toString().trim();
        String discretizacionesRadio = this.discretizacionesRadio.getText().toString().trim();

        if (minCentroXValor.isEmpty() || maxCentroXValor.isEmpty()
                || minCentroYValor.isEmpty() || maxCentroYValor.isEmpty()
                || minRadioValor.isEmpty() || maxRadioValor.isEmpty()
                || discretizacionesCentroX.isEmpty()
                || discretizacionesCentroY.isEmpty()
                || discretizacionesRadio.isEmpty()) {

            Toast.makeText(this, "Especificar X e Y del centro, radio y discretizaciones", Toast.LENGTH_LONG).show();
        } else {

            int minCentroX = Integer.valueOf(minCentroXValor);
            int maxCentroX = Integer.valueOf(maxCentroXValor);
            int minCentroY = Integer.valueOf(minCentroYValor);
            int maxCentroY = Integer.valueOf(maxCentroYValor);
            int minRadio = Integer.valueOf(minRadioValor);
            int maxRadio = Integer.valueOf(maxRadioValor);

            int discretizacionesDeX = Integer.valueOf(discretizacionesCentroX);
            int discretizacionesDeY = Integer.valueOf(discretizacionesCentroY);
            int discretizacionesDeRadio = Integer.valueOf(discretizacionesRadio);

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            MatrizAcumuladora matriz = new MatrizAcumuladora(minCentroX, maxCentroX, minCentroY, maxCentroY, minRadio, maxRadio, discretizacionesDeY, discretizacionesDeX, discretizacionesDeRadio);
            Bitmap bitmapTransformado = TransformadaDeHoughCirculos.aplicarTransformadaDeHough(bitmap.copy(Bitmap.Config.RGB_565, true), matriz);
            imageView.setImageBitmap(bitmapTransformado);
        }
    }

    @Override
    protected int getLayout () {
        return R.layout.actividad_deteccion_formas;
    }

    @Override
    public void finish() {

        String nombreOriginal = imagen.getName();
        File file = null;

        try {
            file = getAplicacion().guardarArchivo(((BitmapDrawable) imageView.getDrawable()).getBitmap(), "/tmp/", nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf(".")));
        } catch (Exception e) {
            Log.e(TAG, "Temporal no se pudo guardar: " + e);
        }

        Intent intent = new Intent();
        intent.putExtra(Constante.EXTRA_IMAGEN, file);
        setResult(Constante.RESULT_CODE_IMAGEN_MODIFICADA, intent);
        super.finish();
    }
}
