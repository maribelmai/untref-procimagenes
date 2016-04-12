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
import ar.edu.untref.procesamientoimagenes.tareas.TareaAplicarFiltroMedia;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribel on 4/10/16.
 */
public class ActividadFiltros extends ActividadBasica {

    private static final String LOG_TAG = ActividadFiltros.class.getSimpleName();
    private File imagen;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.tamanioMatrizMedia)
    EditText tamanioMatrizMedia;

    private Bitmap bitmapOriginal;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle("Espere por favor");
        this.progressDialog.setMessage("Aplicando filtro...");

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);

        Bitmap bitmap= ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmapOriginal = bitmap.copy(Bitmap.Config.RGB_565, true);
    }

    @OnClick(R.id.aplicarFiltroMedia)
    public void aplicarFiltroMedia() {

        ocultarTeclado();
        Integer tamanioMascara = Integer.valueOf(tamanioMatrizMedia.getText().toString());

        if (tamanioMascara %2 == 0) {

            Toast.makeText(this, "El tamaño de la máscara debe ser un número impar", Toast.LENGTH_SHORT).show();
        }
        else {

            if (!isFinishing()) {
                this.progressDialog.show();
            }
            new TareaAplicarFiltroMedia(this, bitmapOriginal, tamanioMascara).execute();
        }
    }


    @Override
    protected int getLayout() {
        return R.layout.actividad_filtros;
    }

    public void filtroAplicado(Bitmap bitmapResultante) {
        imageView.setImageBitmap(bitmapResultante);

        if (!isFinishing()){
            this.progressDialog.hide();
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
}
