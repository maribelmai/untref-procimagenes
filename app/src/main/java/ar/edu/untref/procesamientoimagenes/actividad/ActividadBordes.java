package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import ar.edu.untref.procesamientoimagenes.modelo.TipoBorde;
import ar.edu.untref.procesamientoimagenes.modelo.TipoImagen;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarBordesGenerico;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarBordesKirsh;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarBordesPrewitt;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarBordesSobel;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarMetodoDelLaplaciano;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarMetodoDelLaplacianoConEvaluacionDePendiente;
import ar.edu.untref.procesamientoimagenes.tareas.bordes.TareaAplicarMetodoDelLaplacianoDelGaussiano;
import butterknife.Bind;
import butterknife.OnCheckedChanged;
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

    @Bind(R.id.layoutImagenUmbralizada)
    View layoutImagenUmbralizada;

    @Bind(R.id.imagenUmbralizada)
    ImageView imagenUmbralizada;

    @Bind(R.id.seleccionUmbralSeekbar)
    SeekBar seleccionarUmbralSeekBar;

    @Bind(R.id.guardar)
    View guardar;

    @Bind(R.id.minimo)
    TextView minimo;

    @Bind(R.id.maximo)
    TextView maximo;

    @Bind(R.id.actual)
    TextView actual;

    private TipoImagen tipoImagen = TipoImagen.GRIS;
    private Bitmap bitmapOriginal;
    private ProgressDialog progressDialog;
    private String sufijo = "";

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

    @OnCheckedChanged(R.id.esColor)
    public void esColorChanged(boolean checked) {

        if (checked) {
            tipoImagen = TipoImagen.COLOR;
        }
        else {
            tipoImagen = TipoImagen.GRIS;
        }
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
        ocultarImagenUmbralizada();
        this.sufijo = "Prewitt";
        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TipoBorde.COMPLETO, tipoImagen).execute();
    }

    @OnClick(R.id.bordePrewittHorizontal)
    public void detectarBorderPrewittHorizontal() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TipoBorde.HORIZONTAL, tipoImagen).execute();
    }

    @OnClick(R.id.bordePrewittVertical)
    public void detectarBorderPrewittVertical() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TipoBorde.VERTICAL, tipoImagen).execute();
    }

    @OnClick(R.id.bordePrewittDiagonalDerecha)
    public void detectarBorderPrewittDiagonalDerecha() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TipoBorde.DIAGONAL_DERECHA, tipoImagen).execute();
    }

    @OnClick(R.id.bordePrewittDiagonalIzquierda)
    public void detectarBorderPrewittDiagonalIzquierda() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        new TareaAplicarBordesPrewitt(this, bitmapOriginal, TipoBorde.DIAGONAL_IZQUIERDA, tipoImagen).execute();
    }

    //SOBEL
    @OnClick(R.id.bordeSobelCompleto)
    public void detectarBordeSobelCompleto() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "Sobel";
        new TareaAplicarBordesSobel(this, bitmapOriginal, TipoBorde.COMPLETO, tipoImagen).execute();
    }

    @OnClick(R.id.bordeSobelHorizontal)
    public void detectarBordeSobelHorizontal() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        new TareaAplicarBordesSobel(this, bitmapOriginal, TipoBorde.HORIZONTAL, tipoImagen).execute();
    }

    @OnClick(R.id.bordeSobelVertical)
    public void detectarBordeSobelVertical() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        new TareaAplicarBordesSobel(this, bitmapOriginal, TipoBorde.VERTICAL, tipoImagen).execute();
    }

    @OnClick(R.id.bordeSobelDiagonalDerecha)
    public void detectarBordeSobelDiagonalDerecha() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        new TareaAplicarBordesSobel(this, bitmapOriginal, TipoBorde.DIAGONAL_DERECHA, tipoImagen).execute();
    }

    @OnClick(R.id.bordeSobelDiagonalIzquierda)
    public void detectarBordeSobelDiagonalIzquierda() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        new TareaAplicarBordesSobel(this, bitmapOriginal, TipoBorde.DIAGONAL_IZQUIERDA, tipoImagen).execute();
    }

    //KIRSH
    @OnClick(R.id.bordeKirshHorizontal)
    public void detectarBordeKirshHorizontal() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "KirshHorizontal";
        new TareaAplicarBordesKirsh(this, bitmapOriginal, TipoBorde.HORIZONTAL, tipoImagen).execute();
    }

    @OnClick(R.id.bordeKirshVertical)
    public void detectarBordeKirshVertical() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "KirshVertical";
        new TareaAplicarBordesKirsh(this, bitmapOriginal, TipoBorde.VERTICAL, tipoImagen).execute();
    }

    @OnClick(R.id.bordeKirshDiagonalDerecha)
    public void detectarBordeKirshDiagonalDerecha() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "KirshDiagonalDerecha";
        new TareaAplicarBordesKirsh(this, bitmapOriginal, TipoBorde.DIAGONAL_DERECHA, tipoImagen).execute();
    }

    @OnClick(R.id.bordeKirshDiagonalIzquierda)
    public void detectarBordeKirshDiagonalIzquierda() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "KirshDiagonalIzquierda";
        new TareaAplicarBordesKirsh(this, bitmapOriginal, TipoBorde.DIAGONAL_IZQUIERDA, tipoImagen).execute();
    }

    //GENERICO SIN NOMBRE PUNTO A)
    @OnClick(R.id.bordeHorizontal)
    public void detectarBordeHorizontal() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "GenericoHorizontal";
        new TareaAplicarBordesGenerico(this, bitmapOriginal, TipoBorde.HORIZONTAL, tipoImagen).execute();
    }

    @OnClick(R.id.bordeVertical)
    public void detectarBordeVertical() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "GenericoVertical";
        new TareaAplicarBordesGenerico(this, bitmapOriginal, TipoBorde.VERTICAL, tipoImagen).execute();
    }

    @OnClick(R.id.bordeDiagonalDerecha)
    public void detectarBordeDiagonalDerecha() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "GenericoDiagonalDerecha";
        new TareaAplicarBordesGenerico(this, bitmapOriginal, TipoBorde.DIAGONAL_DERECHA, tipoImagen).execute();
    }

    @OnClick(R.id.bordeDiagonalIzquierda)
    public void detectarBordeDiagonalIzquierda() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "GenericoDiagonalIzquierda";
        new TareaAplicarBordesGenerico(this, bitmapOriginal, TipoBorde.DIAGONAL_IZQUIERDA, tipoImagen).execute();
    }

    //LAPLACIANO

    @OnClick(R.id.bordeMetodoDelLaplaciano)
    public void detectarBordeMetodoLaplaciano() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "Laplaciano";
        new TareaAplicarMetodoDelLaplaciano(this, bitmapOriginal, tipoImagen).execute();
    }

    @OnClick(R.id.bordeMetodoDelLaplacianoCrucesPorCero)
    public void detectarBordeMetodoLaplacianoCrucesPorCero() {

        if (!isFinishing()) {
            this.progressDialog.show();
        }
        ocultarImagenUmbralizada();
        this.sufijo = "LaplacianoCrucesPorCero";
        new TareaAplicarMetodoDelLaplacianoConEvaluacionDePendiente(this, bitmapOriginal, 0, tipoImagen).execute();
    }

    @OnClick(R.id.bordeMetodoDelLaplacianoEvaluacionPendiente)
    public void detectarBordeMetodoLaplacianoEvaluacionPendiente() {

        View view = LayoutInflater.from(this).inflate(R.layout.view_seleccion_valor, null);
        final EditText inputEscalar = (EditText) view.findViewById(R.id.valor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Evaluación de la pendiente");
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String valorIngresado = inputEscalar.getText().toString();
                Integer valorEntero = Integer.valueOf(valorIngresado);

                if (!isFinishing()) {
                    progressDialog.show();
                }
                ocultarImagenUmbralizada();
                sufijo = "LaplacianoEvaluacionPendiente";
                new TareaAplicarMetodoDelLaplacianoConEvaluacionDePendiente(ActividadBordes.this, bitmapOriginal, valorEntero, tipoImagen).execute();
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @OnClick(R.id.bordeMetodoDelLaplacianoDelGaussiano)
    public void detectarBordeMetodoLaplacianoDelGaussiano() {

        if (!sigma.getText().toString().trim().isEmpty()) {

            float valorSigma = Float.parseFloat(sigma.getText().toString());

            if (!isFinishing()) {
                this.progressDialog.show();
            }

            ocultarImagenUmbralizada();
            this.sufijo = "LoG";
            new TareaAplicarMetodoDelLaplacianoDelGaussiano(valorSigma, this, bitmapOriginal, tipoImagen).execute();
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

    public void bordesDetectados(Bitmap bitmap, int[][] matrizGradientes) {

        if (!isFinishing()){
            this.progressDialog.hide();
        }
        imageView.setImageBitmap(bitmap);

        if (matrizGradientes != null) {
            configurarSeleccionUmbral(matrizGradientes);
        }
    }

    private void configurarSeleccionUmbral(final int[][] matrizGradientes) {

        int ancho = matrizGradientes.length;
        int alto = matrizGradientes[0].length;

        int minimo = Integer.MAX_VALUE;
        int maximo = Integer.MIN_VALUE;

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {

                int valorActual = matrizGradientes[x][y];

                if (minimo > valorActual) {
                    minimo = valorActual;
                }

                if (maximo < valorActual) {
                    maximo = valorActual;
                }
            }
        }

        this.minimo.setText(getString(R.string.min).replace("{min}", String.valueOf(minimo)));
        this.maximo.setText(getString(R.string.max).replace("{max}", String.valueOf(maximo)));

        seleccionarUmbralSeekBar.setMax(maximo - minimo);

        if (seleccionarUmbralSeekBar.getMax() >= 100) {
            seleccionarUmbralSeekBar.setProgress(100);
        }
        else {
            seleccionarUmbralSeekBar.setProgress((maximo - minimo) / 2);
        }

        this.actual.setText(getString(R.string.actual).replace("{actual}", String.valueOf(seleccionarUmbralSeekBar.getProgress())));
        mostrarImagenUmbralizada(seleccionarUmbralSeekBar, matrizGradientes);

        seleccionarUmbralSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ActividadBordes.this.actual.setText(getString(R.string.actual).replace("{actual}", String.valueOf(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mostrarImagenUmbralizada(seekBar, matrizGradientes);
            }
        });

        layoutImagenUmbralizada.setVisibility(View.VISIBLE);
    }

    private void mostrarImagenUmbralizada(SeekBar seekBar, int[][] matrizGradientes) {

        imagenUmbralizada.setVisibility(View.VISIBLE);

        int umbral = seekBar.getProgress();
        Bitmap mutableBitmap = bitmapOriginal.copy(Bitmap.Config.RGB_565, true);

        for (int x = 0; x < bitmapOriginal.getWidth(); x++) {
            for (int y = 0 ; y < bitmapOriginal.getHeight(); y ++) {

                if (matrizGradientes[x][y] < umbral) {
                    mutableBitmap.setPixel(x,y, Color.BLACK);
                }
                else {
                    mutableBitmap.setPixel(x,y, Color.WHITE);
                }
            }
        }

        imagenUmbralizada.setImageBitmap(mutableBitmap);
        guardar.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.guardar)
    public void guardar() {

        String nombreOriginal = imagen.getName();
        try {
            String nuevoNombre = nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + sufijo + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            getAplicacion().guardarArchivo(((BitmapDrawable) imagenUmbralizada.getDrawable()).getBitmap(), "/", nuevoNombre);
            Toast.makeText(this, nuevoNombre + " guardado correctamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ocurrió un error guardando el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void ocultarImagenUmbralizada() {

        layoutImagenUmbralizada.setVisibility(View.INVISIBLE);
        imagenUmbralizada.setVisibility(View.INVISIBLE);
        guardar.setVisibility(View.GONE);
    }
}
