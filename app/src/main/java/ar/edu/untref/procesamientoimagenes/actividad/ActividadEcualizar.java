package ar.edu.untref.procesamientoimagenes.actividad;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ar.edu.untref.procesamientoimagenes.Aplicacion;
import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribel on 4/11/16.
 */
public class ActividadEcualizar extends ActividadBasica {

    private static final String LOG_TAG = ActividadEcualizar.class.getSimpleName();
    private File imagen;

    @Bind(R.id.imagenOriginal)
    ImageView imagenOriginal;

    @Bind(R.id.imagenEcualizada)
    ImageView imagenEcualizada;

    @Bind(R.id.imagenEcualizada2daVez)
    ImageView imagenEcualizada2daVez;

    @Bind(R.id.histogramaOriginal)
    LineChart graficoHistogramaOriginal;

    @Bind(R.id.histogramaEcualizado)
    LineChart graficoHistogramaEcualizado;

    @Bind(R.id.histogramaEcualizado2daVez)
    LineChart graficoHistogramaEcualizado2daVez;

    @Bind(R.id.iniciarEcualizacion2daVez)
    View iniciarEcualizacionSegundaVez;

    private int[] histogramaImagenOriginal;
    private int[] histogramaEcualizado;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Ecualización");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        getAplicacion().mostrarImagen(imagen, imagenOriginal);

        histogramaImagenOriginal = calcularHistograma(imagenOriginal);
        dibujarHistograma(histogramaImagenOriginal, graficoHistogramaOriginal);
    }

    @OnClick(R.id.iniciarEcualizacion)
    public void iniciarPrimeraEcualizacion(View view) {

        view.setVisibility(View.GONE);

        Bitmap bitmap = ((BitmapDrawable)imagenOriginal.getDrawable()).getBitmap();
        Bitmap bitmapNuevo = ecualizar(histogramaImagenOriginal, bitmap);

        imagenEcualizada.setImageBitmap(bitmapNuevo);
        histogramaEcualizado = calcularHistograma(imagenEcualizada);
        dibujarHistograma(histogramaEcualizado, graficoHistogramaEcualizado);
        graficoHistogramaEcualizado.setVisibility(View.VISIBLE);
        iniciarEcualizacionSegundaVez.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.iniciarEcualizacion2daVez)
    public void iniciarSegundaEcualizacion(View view) {

        view.setVisibility(View.GONE);

        Bitmap bitmap = ((BitmapDrawable)imagenEcualizada.getDrawable()).getBitmap();
        Bitmap bitmapNuevo = ecualizar(histogramaEcualizado, bitmap);

        imagenEcualizada2daVez.setImageBitmap(bitmapNuevo);
        int[] nuevoHistograma = calcularHistograma(imagenEcualizada2daVez);
        dibujarHistograma(nuevoHistograma, graficoHistogramaEcualizado2daVez);
        graficoHistogramaEcualizado2daVez.setVisibility(View.VISIBLE);
    }

    @NonNull
    private Bitmap ecualizar(int[] histograma, Bitmap bitmap) {

        Bitmap bitmapNuevo = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);

        float totalPixeles = bitmap.getHeight() * bitmap.getWidth();

        //Calculo las probabilidades de cada nivel de gris
        float[] probabilidadesDeNivelesDeGris = new float[256];

        for (int i = 0; i < 256; i ++) {
            probabilidadesDeNivelesDeGris[i] = histograma[i]/totalPixeles;
        }

        //Calculo el vector de acumulados
        float[] acumulado = new float[256];
        acumulado[0] = probabilidadesDeNivelesDeGris[0];

        for (int i = 1; i < 256; i++) {
            acumulado[i] = acumulado[i-1] + probabilidadesDeNivelesDeGris[i];
        }

        //Vector ecualizador
        float[] ecualizador = new float[256];

        for (int i = 0; i < 256; i++) {
            ecualizador[i] = acumulado[i] * 255;
        }

        //dibujo la nueva imagen

        for (int x = 0; x < bitmapNuevo.getWidth(); x++) {
            for (int y = 0; y < bitmapNuevo.getHeight(); y++) {

                int colorNuevo = (int) ecualizador[Color.red(bitmap.getPixel(x, y))];
                bitmapNuevo.setPixel(x,y, Color.rgb(colorNuevo,colorNuevo,colorNuevo));
            }
        }
        return bitmapNuevo;
    }

    private int[] calcularHistograma(ImageView imageView) {

        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        int[] ocurrenciasPorColor = new int[256];

        for (int x = 0; x < bitmap.getWidth() ; x++) {

            for (int y = 0; y < bitmap.getHeight(); y++) {

                int valorGris = Color.red(bitmap.getPixel(x,y));
                ocurrenciasPorColor[valorGris] ++;
            }
        }

        return ocurrenciasPorColor;
    }

    private void dibujarHistograma(int[] ocurrenciasPorColor, LineChart histograma) {

        histograma.setViewPortOffsets(0, 0, 0, 0);
        histograma.setDescription("");
        histograma.setScaleEnabled(true);
        histograma.setPinchZoom(false);

        histograma.setDrawGridBackground(false);

        XAxis x = histograma.getXAxis();
        x.setDrawGridLines(false);

        YAxis y = histograma.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(true);
        y.setAxisLineColor(Color.BLACK);

        histograma.getAxisRight().setEnabled(false);
        histograma.getLegend().setEnabled(false);
        histograma.animateXY(2000, 2000);

        histograma.invalidate();

        ArrayList<String> valoresX = new ArrayList<>();
        for (int i = 0; i < ocurrenciasPorColor.length; i++) {
            valoresX.add(String.valueOf(i));
        }

        ArrayList<Entry> valoresY = new ArrayList<>();

        for (int i = 0; i < ocurrenciasPorColor.length; i++) {
            valoresY.add(new Entry(ocurrenciasPorColor[i], i));
        }

        LineDataSet datos = new LineDataSet(valoresY, "Histograma");
        datos.setDrawFilled(true);
        datos.setDrawCircles(false);
        datos.setLineWidth(1.0f);
        datos.setHighLightColor(Color.rgb(244, 117, 117));
        datos.setColor(Color.BLACK);
        datos.setFillColor(Color.BLACK);
        datos.setFillAlpha(50);
        datos.setDrawHorizontalHighlightIndicator(false);
        datos.setFillFormatter(new FillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return 0;
            }
        });

        LineData data = new LineData(valoresX, datos);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        histograma.setData(data);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_ecualizar;
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

                if (imagenEcualizada.getDrawable() != null) {

                    Bitmap bitmap = ((BitmapDrawable)imagenEcualizada.getDrawable()).getBitmap();

                    try {
                        String nombre = "ecualizada_" + System.currentTimeMillis() + ".png";
                        Aplicacion.getContext().guardarArchivo(bitmap, "/", nombre);
                        Toast.makeText(this, "Guardada correctamente: " + nombre, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(this, "Primero ecualiza la imagen", Toast.LENGTH_SHORT).show();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
