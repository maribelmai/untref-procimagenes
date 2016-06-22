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

    @Bind(R.id.theta_min)
    EditText minTheta;

    @Bind(R.id.theta_max)
    EditText maxTheta;

    @Bind(R.id.rho_max)
    EditText maxRho;

    @Bind(R.id.rho_min)
    EditText minRho;

    @Bind(R.id.discretizaciones_theta)
    EditText discretizacionesTheta;

    @Bind(R.id.discretizaciones_rho)
    EditText discretizacionesRho;

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

        String minThetaValor = minTheta.getText().toString().trim();
        String maxThetaValor = maxTheta.getText().toString().trim();
        String minRhoValor = minRho.getText().toString().trim();
        String maxRhoValor = maxRho.getText().toString().trim();
        String discretizacionestheta = discretizacionesTheta.getText().toString().trim();
        String discretizacionesrho = discretizacionesRho.getText().toString().trim();

        if (minThetaValor.isEmpty() || maxThetaValor.isEmpty() || minRhoValor.isEmpty() || maxRhoValor.isEmpty()
                || discretizacionesrho.isEmpty() || discretizacionestheta.isEmpty()) {
            Toast.makeText(this, "Especificar Theta y Rho y discretizaciones", Toast.LENGTH_LONG).show();
        } else {

            int minTheta = Integer.valueOf(minThetaValor);
            int maxTheta = Integer.valueOf(maxThetaValor);
            int minRho = Integer.valueOf(minRhoValor);
            int maxRho = Integer.valueOf(maxRhoValor);

            int discretizacionesTetha = Integer.valueOf(discretizacionestheta);
            int discretizacionesDeRho = Integer.valueOf(discretizacionesrho);

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            MatrizAcumuladora matriz = new MatrizAcumuladora(minRho, maxRho, minTheta, maxTheta, discretizacionesDeRho, discretizacionesTetha);
            Bitmap bitmapTransformado = TransformadaDeHoughRectas.aplicarTransformadaDeHough(bitmap, matriz);
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
