package ar.edu.untref.procesamientoimagenes.actividad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

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
import java.util.ArrayList;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribel on 4/6/16.
 */
public class ActividadHistograma extends ActividadBasica {

    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.histograma)
    LineChart histograma;

    private File imagen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Histograma");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        getAplicacion().mostrarImagen(imagen, imageView);

        calcularHistograma();
    }

    private void calcularHistograma() {

        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        int[] ocurrenciasPorColor = new int[256];

        for (int x = 0; x < bitmap.getWidth() ; x++) {

            for (int y = 0; y < bitmap.getHeight(); y++) {

                int valorGris = Color.red(bitmap.getPixel(x,y));
                ocurrenciasPorColor[valorGris] ++;
            }
        }

        dibujarHistograma(ocurrenciasPorColor);
    }

    private void dibujarHistograma(int[] ocurrenciasPorColor) {

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

    @OnClick(R.id.iniciarEcualizacion)
    public void iniciarEcualizacion() {

        Intent intent = new Intent(this, ActividadEcualizar.class);
        intent.putExtra(Constante.EXTRA_IMAGEN, imagen);
        startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_histograma;
    }
}
