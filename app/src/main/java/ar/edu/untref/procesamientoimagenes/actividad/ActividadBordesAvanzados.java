package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.TareaDetectarBordesCanny;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.TareaDetectarBordesSUSAN;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribelmai on 8/6/16.
 */
public class ActividadBordesAvanzados extends ActividadBasica {

    private static final String LOG_TAG = ActividadBordesAvanzados.class.getSimpleName();
    private File imagen;
    private Bitmap bitmapOriginal;
    private ProgressDialog progressDialogBordes;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.canny_gauss_1)
    ImageView cannyGauss1;
    @Bind(R.id.canny_gauss_2)
    ImageView cannyGauss2;
    @Bind(R.id.canny_gauss_3)
    ImageView cannyGauss3;

    @Bind(R.id.canny_1)
    ImageView canny1;
    @Bind(R.id.canny_2)
    ImageView canny2;
    @Bind(R.id.canny_3)
    ImageView canny3;

    @Bind(R.id.canny_final)
    ImageView cannyFinal;

    @Bind(R.id.tipo_susan)
    RadioGroup tipoSusan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Bordes (Avanzado)");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.progressDialogBordes = new ProgressDialog(this);
        this.progressDialogBordes.setCancelable(false);
        this.progressDialogBordes.setTitle("Espere por favor");
        this.progressDialogBordes.setMessage("Detectando bordes...");

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);

        Bitmap bitmap= ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmapOriginal = bitmap.copy(Bitmap.Config.RGB_565, true);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_bordes_avanzados;
    }

    @OnClick(R.id.bordes_canny)
    public void detectarBordesCanny() {

        reset();

        if (this.progressDialogBordes != null && !isFinishing()) {
            this.progressDialogBordes.show();
        }

        new TareaDetectarBordesCanny(bitmapOriginal, this, 1, 3, 10, 70f, 100f).execute();
    }

    @OnClick(R.id.bordes_susan)
    public void detectarBordesSusan() {

        reset();

        if (this.progressDialogBordes != null && !isFinishing()) {
            this.progressDialogBordes.show();
        }

        TareaDetectarBordesSUSAN.TipoSUSAN tipoSUSAN = TareaDetectarBordesSUSAN.TipoSUSAN.AMBOS;
        if (tipoSusan.getCheckedRadioButtonId() == R.id.bordes) {
            tipoSUSAN = TareaDetectarBordesSUSAN.TipoSUSAN.BORDES;
        }
        else if (tipoSusan.getCheckedRadioButtonId() == R.id.esquinas) {
            tipoSUSAN = TareaDetectarBordesSUSAN.TipoSUSAN.ESQUINAS;
        }

        new TareaDetectarBordesSUSAN(bitmapOriginal, this, tipoSUSAN).execute();
    }

    private void reset() {

        cannyGauss1.setImageBitmap(null);
        cannyGauss2.setImageBitmap(null);
        cannyGauss3.setImageBitmap(null);
        canny1.setImageBitmap(null);
        canny2.setImageBitmap(null);
        canny3.setImageBitmap(null);
        cannyFinal.setImageBitmap(null);
    }

    public void bordesDetectados(Bitmap bitmap) {

        if (this.progressDialogBordes != null && !isFinishing()) {
            this.progressDialogBordes.cancel();
        }

        this.cannyFinal.setImageBitmap(bitmap);
    }

    public void mostrarProgresoCanny(TareaDetectarBordesCanny.Progreso progreso, Bitmap bitmap) {

        if (progreso == TareaDetectarBordesCanny.Progreso.GAUSS_1_CALCULADO) {
            cannyGauss1.setImageBitmap(bitmap);
        }
        else if (progreso == TareaDetectarBordesCanny.Progreso.GAUSS_2_CALCULADO) {
            cannyGauss2.setImageBitmap(bitmap);
        }
        else if (progreso == TareaDetectarBordesCanny.Progreso.GAUSS_3_CALCULADO) {
            cannyGauss3.setImageBitmap(bitmap);
        }
        else if (progreso == TareaDetectarBordesCanny.Progreso.CANNY_1_CALCULADO) {
            canny1.setImageBitmap(bitmap);
        }
        else if (progreso == TareaDetectarBordesCanny.Progreso.CANNY_2_CALCULADO) {
            canny2.setImageBitmap(bitmap);
        }
        else if (progreso == TareaDetectarBordesCanny.Progreso.CANNY_3_CALCULADO) {
            canny3.setImageBitmap(bitmap);
        }
        else if (progreso == TareaDetectarBordesCanny.Progreso.CANNY_FINAL_CALCULADO) {
            cannyFinal.setImageBitmap(bitmap);
        }
    }

    public void recibirSUSAN(Bitmap bitmap) {

        if (this.progressDialogBordes != null && !isFinishing()) {
            this.progressDialogBordes.cancel();
        }

        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void finish() {

        String nombreOriginal = imagen.getName();
        File file = null;

        try {
            file = getAplicacion().guardarArchivo(((BitmapDrawable) cannyFinal.getDrawable()).getBitmap(), "/tmp/", nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf(".")));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Temporal no se pudo guardar: " + e);
            try {
                file = getAplicacion().guardarArchivo(((BitmapDrawable) imageView.getDrawable()).getBitmap(), "/tmp/", nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf(".")));
            } catch (IOException e1) {
                Log.e(LOG_TAG, "Temporal no se pudo guardar: " + e);
            }
        }

        Intent intent = new Intent();
        intent.putExtra(Constante.EXTRA_IMAGEN, file);
        setResult(Constante.RESULT_CODE_IMAGEN_MODIFICADA, intent);
        super.finish();
    }
}
