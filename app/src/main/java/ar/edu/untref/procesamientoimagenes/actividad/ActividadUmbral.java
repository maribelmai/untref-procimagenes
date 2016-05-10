package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;
import butterknife.OnClick;

import static ar.edu.untref.procesamientoimagenes.R.id.umbralizarGlobal;

/**
 * Created by maribel on 4/6/16.
 */
public class ActividadUmbral extends ActividadBasica {

    private static final String LOG_TAG = ActividadUmbral.class.getSimpleName();

    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.imagenUmbralizada)
    ImageView imagenUmbralizada;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.umbralSeleccionado)
    TextView umbralSeleccionado;

    @Bind(R.id.muestraColor)
    View muestraColor;

    @Bind(R.id.seleccionUmbralSeekbar)
    SeekBar seleccionUmbral;

    @Bind(R.id.resultadoGlobal)
    TextView resultadoGlobal;

    private File imagen;
    private SeekBar.OnSeekBarChangeListener valorSeleccionado = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            int progress = seekBar.getProgress();

            umbralSeleccionado.setText(String.valueOf(progress));
            muestraColor.setBackgroundColor(Color.rgb(progress, progress, progress));
            umbralizar(progress);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Umbralizar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.seleccionUmbral.setOnSeekBarChangeListener(this.valorSeleccionado);

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);
    }

    @OnClick(R.id.umbralizar)
    public void seleccionarUmbral() {

        View view = LayoutInflater.from(this).inflate(R.layout.view_seleccion_valor, null);
        final EditText inputEscalar = (EditText) view.findViewById(R.id.valor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(R.string.umbralizar);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String valorIngresado = inputEscalar.getText().toString();
                Integer valorEntero = Integer.valueOf(valorIngresado);

                if (valorEntero < 256) {

                    umbralSeleccionado.setText(valorIngresado);
                    muestraColor.setBackgroundColor(Color.rgb(valorEntero, valorEntero, valorEntero));
                    seleccionUmbral.setProgress(valorEntero);
                    umbralizar(valorEntero);
                }
                else {
                    Toast.makeText(ActividadUmbral.this, "El umbral ingresado tiene que estar entre 0 y 255", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void umbralizar(int umbral) {

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap bitmapModificado = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);

        for (int x = 0; x < bitmap.getWidth(); x++) {

            for (int y = 0; y < bitmap.getHeight(); y++) {

                int valorPixel = Color.blue(bitmap.getPixel(x, y));

                if (valorPixel > umbral) {
                    bitmapModificado.setPixel(x, y, Color.rgb(255, 255, 255));
                } else {
                    bitmapModificado.setPixel(x, y, Color.rgb(0, 0, 0));
                }
            }
        }

        imagenUmbralizada.setImageBitmap(bitmapModificado);
    }

    @OnClick(R.id.umbralizarGlobal)
    public void umbralizarGlobal() {

        Bitmap bitmap1 = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        List<Integer> pixelesGrupoUno = new ArrayList<>();
        List<Integer> pixelesGrupoDos = new ArrayList<>();
        Integer umbral= 128;
        int numeroAux=0 ;
        int mediaGrupoUno= 0;
        int mediaGrupoDos= 0;

        boolean continuar= true;

        while (continuar){
            for (int x = 0; x < bitmap1.getWidth(); x++) {
                for (int y = 0; y < bitmap1.getHeight(); y++) {

                    Integer valorPixelBitmap1 = Color.red(bitmap1.getPixel(x, y));
                    if(valorPixelBitmap1 >= umbral) {
                        pixelesGrupoUno.add(valorPixelBitmap1);
                    }
                    else {
                        pixelesGrupoDos.add(valorPixelBitmap1);
                    }

                }
            }

            numeroAux=0;
            for (int i=0; i<pixelesGrupoUno.size();i++){
                numeroAux= numeroAux + pixelesGrupoUno.get(i);
            }

            mediaGrupoUno= numeroAux /pixelesGrupoDos.size();
            numeroAux=0;
            for (int i=0; i<pixelesGrupoDos.size();i++){
                numeroAux= numeroAux + pixelesGrupoDos.get(i);
            }
            mediaGrupoDos= numeroAux /pixelesGrupoDos.size();

            int umbralAnterior= umbral;
            umbral=( mediaGrupoUno + mediaGrupoDos)/2;
            if (umbralAnterior==umbral) {
                continuar = false;
            }
        }
        umbralizar(mediaGrupoUno);
        //imagenUmbralizada.setImageBitmap(bitmap);
        //return null;
    }

    @Override
    public void finish() {

        Intent intent = new Intent();
        String nombreOriginal = imagen.getName();
        File file = null;

        try {
            file = getAplicacion().guardarArchivo(((BitmapDrawable) imagenUmbralizada.getDrawable()).getBitmap(), "/tmp/", nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf(".")));
            intent.putExtra(Constante.EXTRA_IMAGEN, file);
            setResult(Constante.RESULT_CODE_IMAGEN_MODIFICADA, intent);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Temporal no se pudo guardar: " + e);
        }

        super.finish();
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_umbral;
    }
}
