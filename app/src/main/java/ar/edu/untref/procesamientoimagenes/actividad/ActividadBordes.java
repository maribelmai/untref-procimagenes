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

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import ar.edu.untref.procesamientoimagenes.modelo.TipoBorde;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarBordesGenerico;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarBordesKirsh;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarBordesPrewitt;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarBordesSobel;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarMetodoDelLaplaciano;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarMetodoDelLaplacianoConEvaluacionDePendiente;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarMetodoDelLaplacianoDelGaussiano;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Eli- on 25/04/2016.
 */
public class ActividadBordes extends ActividadBasica {

    private static final String LOG_TAG = ActividadBordes.class.getSimpleName();
    private File imagen;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.sigma)
    EditText sigma;

    private Bitmap bitmapOriginal;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Bordes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle("Espere por favor");
        this.progressDialog.setMessage("Detectando bordes...");

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);

        Bitmap bitmap= ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmapOriginal = bitmap.copy(Bitmap.Config.RGB_565, true);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_bordes;
    }

    //PREWITT
    @OnClick(R.id.bordePrewittCompleto)
    public void detectarBorderPrewittCompleto() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TipoBorde.COMPLETO).execute();
    }

    @OnClick(R.id.bordePrewittHorizontal)
    public void detectarBorderPrewittHorizontal() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TipoBorde.HORIZONTAL).execute();
    }

    @OnClick(R.id.bordePrewittVertical)
    public void detectarBorderPrewittVertical() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TipoBorde.VERTICAL).execute();
    }

    @OnClick(R.id.bordePrewittDiagonalDerecha)
    public void detectarBorderPrewittDiagonalDerecha() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TipoBorde.DIAGONAL_DERECHA).execute();
    }

    @OnClick(R.id.bordePrewittDiagonalIzquierda)
    public void detectarBorderPrewittDiagonalIzquierda() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TipoBorde.DIAGONAL_IZQUIERDA).execute();
    }

    //SOBEL
    @OnClick(R.id.bordeSobelCompleto)
    public void detectarBordeSobelCompleto() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesSobel(this, bitmapOriginal, TipoBorde.COMPLETO).execute();
    }

    @OnClick(R.id.bordeSobelHorizontal)
    public void detectarBordeSobelHorizontal() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesSobel(this, bitmapOriginal, TipoBorde.HORIZONTAL).execute();
    }

    @OnClick(R.id.bordeSobelVertical)
    public void detectarBordeSobelVertical() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesSobel(this, bitmapOriginal, TipoBorde.VERTICAL).execute();
    }

    @OnClick(R.id.bordeSobelDiagonalDerecha)
    public void detectarBordeSobelDiagonalDerecha() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesSobel(this, bitmapOriginal, TipoBorde.DIAGONAL_DERECHA).execute();
    }

    @OnClick(R.id.bordeSobelDiagonalIzquierda)
    public void detectarBordeSobelDiagonalIzquierda() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesSobel(this, bitmapOriginal, TipoBorde.DIAGONAL_IZQUIERDA).execute();
    }

    //KIRSH
    @OnClick(R.id.bordeKirshHorizontal)
    public void detectarBordeKirshHorizontal() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesKirsh(this, bitmapOriginal, TipoBorde.HORIZONTAL).execute();
    }

    @OnClick(R.id.bordeKirshVertical)
    public void detectarBordeKirshVertical() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesKirsh(this, bitmapOriginal, TipoBorde.VERTICAL).execute();
    }

    @OnClick(R.id.bordeKirshDiagonalDerecha)
    public void detectarBordeKirshDiagonalDerecha() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesKirsh(this, bitmapOriginal, TipoBorde.DIAGONAL_DERECHA).execute();
    }

    @OnClick(R.id.bordeKirshDiagonalIzquierda)
    public void detectarBordeKirshDiagonalIzquierda() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesKirsh(this, bitmapOriginal, TipoBorde.DIAGONAL_IZQUIERDA).execute();
    }

    //GENERICO SIN NOMBRE PUNTO A)
    @OnClick(R.id.bordeHorizontal)
    public void detectarBordeHorizontal() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesGenerico(this, bitmapOriginal, TipoBorde.HORIZONTAL).execute();
    }

    @OnClick(R.id.bordeVertical)
    public void detectarBordeVertical() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesGenerico(this, bitmapOriginal, TipoBorde.VERTICAL).execute();
    }

    @OnClick(R.id.bordeDiagonalDerecha)
    public void detectarBordeDiagonalDerecha() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesGenerico(this, bitmapOriginal, TipoBorde.DIAGONAL_DERECHA).execute();
    }

    @OnClick(R.id.bordeDiagonalIzquierda)
    public void detectarBordeDiagonalIzquierda() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarBordesGenerico(this, bitmapOriginal, TipoBorde.DIAGONAL_IZQUIERDA).execute();
    }

    //LAPLACIANO

    @OnClick(R.id.bordeMetodoDelLaplaciano)
    public void detectarBordeMetodoLaplaciano() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarMetodoDelLaplaciano(this, bitmapOriginal).execute();
    }

    @OnClick(R.id.bordeMetodoDelLaplacianoCrucesPorCero)
    public void detectarBordeMetodoLaplacianoCrucesPorCero() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        new TareaAplicarMetodoDelLaplacianoConEvaluacionDePendiente(this, bitmapOriginal).execute();
    }

    @OnClick(R.id.bordeMetodoDelLaplacianoDelGaussiano)
    public void detectarBordeMetodoLaplacianoDelGaussiano() {

        if (!sigma.getText().toString().trim().isEmpty()) {

            float valorSigma = Float.parseFloat(sigma.getText().toString());

            if (!isFinishing()) {
                this.progressDialog.show();
            }

            new TareaAplicarMetodoDelLaplacianoDelGaussiano(valorSigma, this, bitmapOriginal).execute();
        }
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

    public void bordesDetectados(Bitmap bitmap) {

        if (!isFinishing()){
            this.progressDialog.hide();
        }
        imageView.setImageBitmap(bitmap);
    }

}
