package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import ar.edu.untref.procesamientoimagenes.tareas.segmentacion.Segmentacion;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribelmai on 22/6/16.
 */
public class ActividadSegmentacionImagen extends ActividadBasica {

    private static final String TAG = ActividadDeteccionFormas.class.getSimpleName();
    private File imagen;
    private Bitmap bitmapOriginal;
    private ProgressDialog progressDialogBordes;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.imagen)
    CropImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Segmentación");
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

        imageView.setInitialFrameScale(0.5f);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_segmentacion_imagen;
    }

    @OnClick(R.id.segmentar)
    public void segmentar() {

        Integer vecesValor = 50;
        EditText veces = EditText.class.cast(findViewById(R.id.veces));

        if (!veces.getText().toString().isEmpty()) {

            Integer valorIngresado = Integer.valueOf(veces.getText().toString().trim());

            if (valorIngresado < bitmapOriginal.getWidth() && valorIngresado < bitmapOriginal.getHeight()) {
                vecesValor = valorIngresado;
            }
            else {
                Toast.makeText(this, "El valor debe ser inferior a las dimensiones de la imagen", Toast.LENGTH_SHORT).show();
            }
        }

        RectF seleccion = imageView.getActualCropRect();
        Point punto1 = new Point((int)seleccion.left, (int)seleccion.top);
        Point punto2 = new Point((int)seleccion.right, (int)seleccion.bottom);

        Integer diferenciaAceptada = 10;
        EditText diferenciaValor = EditText.class.cast(findViewById(R.id.maxima_diferencia));

        if (!diferenciaValor.getText().toString().isEmpty()) {
            diferenciaAceptada = Integer.valueOf(diferenciaValor.getText().toString().trim());
        }

        Bitmap bitmapResultante = Segmentacion.identificarObjeto(bitmapOriginal, vecesValor, diferenciaAceptada, punto1, punto2);
        imageView.setImageBitmap(bitmapResultante);
    }
}
