package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados.TareaDetectarBordesCanny;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribelmai on 8/6/16.
 */
public class ActividadBordesAvanzados extends ActividadBasica {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Bordes (Avanzado)");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.progressDialogBordes = new ProgressDialog(this);
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

        if (this.progressDialogBordes != null && !isFinishing()) {
            this.progressDialogBordes.show();
        }

        new TareaDetectarBordesCanny(bitmapOriginal, this, 1, 3, 10).execute();
    }

    public void bordesDetectados(Bitmap bitmap) {

        if (this.progressDialogBordes != null && !isFinishing()) {
            this.progressDialogBordes.cancel();
        }

        this.imageView.setImageBitmap(bitmap);
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
    }
}
