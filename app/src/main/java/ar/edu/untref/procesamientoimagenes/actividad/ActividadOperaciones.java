package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import ar.edu.untref.procesamientoimagenes.Aplicacion;
import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.fragmentos.FragmentoListaArchivos;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import ar.edu.untref.procesamientoimagenes.util.Compresion;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribel on 4/4/16.
 */
public class ActividadOperaciones extends ActividadBasica {

    private static final String LOG_TAG = ActividadOperaciones.class.getSimpleName();

    @Bind(R.id.imagen1)
    ImageView imagen1;

    @Bind(R.id.imagen2)
    ImageView imagen2;

    @Bind(R.id.imagenResultante)
    ImageView imagenResultante;

    @Bind(R.id.resultadoOperacion)
    TextView resultadoOperacion;
    private File imagen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Operaciones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(Constante.EXTRA_IMAGEN)) {
            this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
            getAplicacion().mostrarImagen(imagen, imagen1);
        }

        this.resultadoOperacion.setText(getString(R.string.resultado_operacion).replace("{operacion}", ""));
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_operaciones;
    }

    @OnClick({R.id.nombreImagen1, R.id.nombreImagen2})
    public void seleccionarImagen(final View view) {

        View archivosView = LayoutInflater.from(this).inflate(R.layout.view_seleccionar_archivo, null);
        final FragmentoListaArchivos fragmentoArchivos = (FragmentoListaArchivos) getSupportFragmentManager().findFragmentById(R.id.fragmentoArchivos);
        fragmentoArchivos.setDisplay(ImageView.class.cast(archivosView.findViewById(R.id.imagenSeleccionada)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(archivosView);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (view.getId() == R.id.nombreImagen1) {
                    Aplicacion.getContext().mostrarImagen(fragmentoArchivos.obtenerSeleccion(), imagen1);
                } else if (view.getId() == R.id.nombreImagen2) {
                    Aplicacion.getContext().mostrarImagen(fragmentoArchivos.obtenerSeleccion(), imagen2);
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(fragmentoArchivos);
                transaction.commit();
            }
        });

        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    @OnClick({R.id.sumar, R.id.restar, R.id.multiplicar, R.id.multiplicarPorEscalar, R.id.negativoDeImagen, R.id.potencia})
    public void operar(View view) {

        BitmapDrawable drawable1 = (BitmapDrawable) imagen1.getDrawable();
        BitmapDrawable drawable2 = (BitmapDrawable) imagen2.getDrawable();
        Bitmap resultante = null;

        Bitmap bitmap1;
        Bitmap bitmap2;

        if (drawable1 != null && view.getId() == R.id.multiplicarPorEscalar) {

            bitmap1 = drawable1.getBitmap();
            mostrarDialogoMultiplicacionEscalar(bitmap1);
        }
        else if(drawable1 != null && view.getId() == R.id.negativoDeImagen) {

            this.resultadoOperacion.setText(getString(R.string.resultado_operacion).replace("{operacion}", "(NEGATIVO)"));
            bitmap1 = drawable1.getBitmap();
            resultante= negativoDeImagen(bitmap1);
            imagenResultante.setImageBitmap(resultante);

        }
        else if(drawable1 != null && view.getId() == R.id.potencia) {

            this.resultadoOperacion.setText(getString(R.string.resultado_operacion).replace("{operacion}", "(POTENCIA)"));
            bitmap1 = drawable1.getBitmap();
            mostrarDialogoPotencia(bitmap1);
        }
        else if (drawable1 != null && drawable2 != null) {

            bitmap1 = drawable1.getBitmap();
            bitmap2 = drawable2.getBitmap();

            if (bitmap1.getHeight() == bitmap2.getHeight() && bitmap1.getWidth() == bitmap2.getWidth()) {

                if (view.getId() == R.id.sumar) {
                    this.resultadoOperacion.setText(getString(R.string.resultado_operacion).replace("{operacion}", "(SUMA)"));
                    resultante = sumar(bitmap1, bitmap2);
                } else if (view.getId() == R.id.restar) {
                    this.resultadoOperacion.setText(getString(R.string.resultado_operacion).replace("{operacion}", "(RESTA)"));
                    resultante = restar(bitmap1, bitmap2);
                } else if (view.getId() == R.id.multiplicar) {
                    this.resultadoOperacion.setText(getString(R.string.resultado_operacion).replace("{operacion}", "(PRODUCTO)"));
                    resultante = multiplicar(bitmap1, bitmap2);
                }

                imagenResultante.setImageBitmap(resultante);
            } else {
                Toast.makeText(this, "Las imágenes deben tener las mismas dimensiones", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Selecciona dos imágenes", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoMultiplicacionEscalar(final Bitmap bitmap1) {

        View view = LayoutInflater.from(this).inflate(R.layout.view_seleccion_valor, null);
        final EditText inputEscalar = (EditText) view.findViewById(R.id.valor);
        inputEscalar.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(R.string.multiplicacion_por_escalar);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String valorIngresado = inputEscalar.getText().toString();
                resultadoOperacion.setText(getString(R.string.resultado_operacion).replace("{operacion}", "(PRODUCTO POR " + valorIngresado + ")"));
                Bitmap resultante = multiplicarPorEscalar(bitmap1, Integer.valueOf(valorIngresado));
                imagenResultante.setImageBitmap(resultante);
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void mostrarDialogoPotencia(final Bitmap bitmap1) {

        View view = LayoutInflater.from(this).inflate(R.layout.view_seleccion_valor, null);
        final EditText inputEscalar = (EditText) view.findViewById(R.id.valor);
        inputEscalar.setHint("Gamma");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(R.string.potencia);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String valorIngresado = inputEscalar.getText().toString();
                Bitmap resultante = potencia(bitmap1, Float.valueOf(valorIngresado));
                imagenResultante.setImageBitmap(resultante);
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private Bitmap sumar(Bitmap bitmap1, Bitmap bitmap2) {

        int[][] matrizPixeles = new int[bitmap1.getWidth()][bitmap2.getHeight()];

        for (int x = 0; x < bitmap1.getWidth(); x++) {

            for (int y = 0; y < bitmap1.getHeight(); y++) {

                int valorPixelBitmap1 = Color.red(bitmap1.getPixel(x, y));
                int valorPixelBitmap2 = Color.red(bitmap2.getPixel(x, y));

                matrizPixeles[x][y] = valorPixelBitmap1 + valorPixelBitmap2;
            }
        }

        return hacerTransformacionLineal(matrizPixeles);
    }

    private Bitmap restar(Bitmap bitmap1, Bitmap bitmap2) {

        int[][] matrizPixeles = new int[bitmap1.getWidth()][bitmap2.getHeight()];

        for (int x = 0; x < bitmap1.getWidth(); x++) {

            for (int y = 0; y < bitmap1.getHeight(); y++) {

                int valorPixelBitmap1 = Color.red(bitmap1.getPixel(x, y));
                int valorPixelBitmap2 = Color.red(bitmap2.getPixel(x, y));

                matrizPixeles[x][y] = valorPixelBitmap1 - valorPixelBitmap2;
            }
        }

        return hacerTransformacionLineal(matrizPixeles);
    }

    private Bitmap potencia(Bitmap bitmap, Float gamma) {

        int valorMaximo = Integer.MIN_VALUE;

        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {

                int pixel = (int) Color.red(bitmap.getPixel(x,y));
                if (pixel > valorMaximo) {
                    valorMaximo = pixel;

                }
            }
        }

        double constante = 255 / Math.pow(valorMaximo, gamma);

        Bitmap bitmapNuevo = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.RGB_565);

        for(int x=0; x < bitmap.getWidth(); x++) {
            for(int y=0; y < bitmap.getHeight(); y++) {

                int nivelGris = Color.red(bitmap.getPixel(x,y));
                int nuevoColor = (int) (constante * Math.pow(nivelGris, gamma));

                bitmapNuevo.setPixel(x, y, Color.rgb(nuevoColor,nuevoColor,nuevoColor));
            }
        }

        return bitmapNuevo;
    }

    private Bitmap multiplicar(Bitmap bitmap1, Bitmap bitmap2) {

        int[][] matrizPixeles = new int[bitmap1.getWidth()][bitmap2.getHeight()];

        for (int x = 0; x < bitmap1.getWidth(); x++) {

            for (int y = 0; y < bitmap1.getHeight(); y++) {

                int valorPixelBitmap1 = Color.red(bitmap1.getPixel(x, y));
                int valorPixelBitmap2 = Color.red(bitmap2.getPixel(x, y));

                matrizPixeles[x][y] = valorPixelBitmap1 * valorPixelBitmap2;
            }
        }

        return Compresion.hacerCompresionRangoDinamico(matrizPixeles);
    }

    private Bitmap multiplicarPorEscalar(Bitmap bitmap1, Integer escalar) {

        int[][] matrizPixeles = new int[bitmap1.getWidth()][bitmap1.getHeight()];

        for (int x = 0; x < bitmap1.getWidth(); x++) {

            for (int y = 0; y < bitmap1.getHeight(); y++) {

                int valorPixelBitmap1 = Color.red(bitmap1.getPixel(x, y));

                matrizPixeles[x][y] = valorPixelBitmap1 * escalar;
            }
        }

        return Compresion.hacerCompresionRangoDinamico(matrizPixeles);
    }

    private Bitmap negativoDeImagen(Bitmap bitmap1) {

        int[][] matrizPixeles = new int[bitmap1.getWidth()][bitmap1.getHeight()];
        Bitmap bitmap = Bitmap.createBitmap(matrizPixeles.length, matrizPixeles[0].length, Bitmap.Config.RGB_565);

        for (int x = 0; x < bitmap1.getWidth(); x++) {

            for (int y = 0; y < bitmap1.getHeight(); y++) {

                int valorPixelBitmap1 = Color.red(bitmap1.getPixel(x, y));

                int nuevoPixel = 255 - valorPixelBitmap1;

                bitmap.setPixel(x, y, Color.rgb(nuevoPixel, nuevoPixel, nuevoPixel));
            }
        }

        return bitmap;
    }

    private Bitmap hacerTransformacionLineal(int[][] matrizPixeles) {

        float minimo;
        float maximo;

        int ancho = matrizPixeles.length;
        int alto = matrizPixeles[0].length;

        minimo = 0;
        maximo = 255;

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {

                int valorActual = matrizPixeles[x][y];

                if (minimo > valorActual) {
                    minimo = valorActual;
                }

                if (maximo < valorActual) {
                    maximo = valorActual;
                }

            }

        }

        Log.i(LOG_TAG, "hacerTransformacionLineal: " + minimo + " " + maximo);

        Bitmap bitmap = Bitmap.createBitmap(alto, ancho, Bitmap.Config.RGB_565);

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {

                int valorActual = matrizPixeles[x][y];
                int valorTransformado = (int) ((((255f) / (maximo - minimo)) * valorActual) - ((minimo * 255f) / (maximo - minimo)));

                bitmap.setPixel(x,y, Color.rgb(valorTransformado, valorTransformado, valorTransformado));
            }
        }

        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_guardar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.guardar:


                if ((imagenResultante.getDrawable()) != null) {

                    Bitmap bitmap = ((BitmapDrawable) imagenResultante.getDrawable()).getBitmap();

                    try {
                        String nombre = "operacion_" + System.currentTimeMillis() + ".png";
                        Aplicacion.getContext().guardarArchivo(bitmap, "/", nombre);
                        Toast.makeText(this, "Guardada correctamente: " + nombre, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(this, "Primero realiza una operación", Toast.LENGTH_SHORT).show();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
