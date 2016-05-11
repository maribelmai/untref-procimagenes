package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.AlertDialog;
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
import java.util.List;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;
import butterknife.OnClick;

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

    @Bind(R.id.resultadoOtsu)
    TextView resultadoOtsu;

    private File imagen;
    List<Integer> pixelesOtsuUno;
    List<Integer> pixelesOtsuDos;

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
        this.resultadoGlobal.setText(getString(R.string.resultado_Global).replace("{umbralGlobal}",""));
        this.resultadoOtsu.setText(getString(R.string.resultado_Otsu).replace("{umbralOtsu}",""));
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
        int cantidadDePixeles= bitmap1.getWidth() * bitmap1.getHeight();
        int[] histograma= calcularHistograma(bitmap1);
        int umbral= calcularPromedioUmbral(histograma);

        float mediaGrupoUno= 0;
        float mediaGrupoDos= 0;
        Integer cantidadIteraciones=0;
        boolean continuar= true;

        while (continuar){
            int cantidadPixelesGrupo1=0 ;
            int cantidadPixelesGrupo2=0 ;
            int acumuladoColorGrupo1= 0;
            int acumuladoColorGrupo2= 0;

            cantidadIteraciones= cantidadIteraciones +1;
            for (int i = 0; i <=umbral; i++) {
                cantidadPixelesGrupo1= cantidadPixelesGrupo1 + histograma[i];
                acumuladoColorGrupo1= acumuladoColorGrupo1+( histograma[i]*i);
            }

            for (int i=umbral+1; i< 256;i++){
                cantidadPixelesGrupo2= cantidadPixelesGrupo2 + histograma[i];
                acumuladoColorGrupo2= acumuladoColorGrupo2+( histograma[i]*i);
            }

            mediaGrupoUno= acumuladoColorGrupo1/ cantidadPixelesGrupo1;
            mediaGrupoDos= acumuladoColorGrupo2/ cantidadPixelesGrupo2;
            int umbralAnterior= umbral;
            umbral=(int)( mediaGrupoUno + mediaGrupoDos)/2;
            //if ((umbralAnterior==umbral) | ((umbralAnterior-umbral)<= 1) | ((umbral-umbralAnterior)<= 1)) {
            if ((umbralAnterior==umbral)) {
                continuar = false;
            }
        }
        umbralizar(umbral);
        this.resultadoGlobal.setText(getString(R.string.resultado_Global).replace("{umbralGlobal}", String.valueOf(umbral)+" Cantidad de Iteraciones: " + cantidadIteraciones.toString()));
    }

    private int calcularPromedioUmbral(int[] histograma) {

        int cantidadPixeles=0;
        int acumuladoColor=0;
        int mediaGrupo=0;

        for (int i=0; i< 256;i++){
            cantidadPixeles= cantidadPixeles + histograma[i];
            acumuladoColor= acumuladoColor+( histograma[i]*i);
        }

        mediaGrupo= acumuladoColor/ cantidadPixeles;
        return mediaGrupo;
    }

    @OnClick(R.id.umbralizarOtsu)
    public void umbralizarOtsu() {

        Bitmap bitmap1 = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        float[] probabilidadPorColor= calcularProbabilidades(bitmap1);
        float maximo=0;
        int maximoT=0;

        for (int t=0; t< 256;t++){

            float w1=0;
            float w2=0;
            for (int i = 0; i <=t; i++) {
                w1+= probabilidadPorColor[i];
            }
            for (int i = t+1; i <256; i++) {
                w2+= probabilidadPorColor[i];
            }
            //buscarWOtsu(bitmap1, t, probabilidadPorColor);
            float mu1 = 0;
            float mu2 = 0;

            for (int i=0; i<=t ;i++) {
                float aux= probabilidadPorColor[i];
                if(w1==0){
                    mu1 +=0;
                }else{
                    mu1 += (i*aux)/w1;
                }

            }
            for (int i=t+1; i< 256;i++) {
                float aux= probabilidadPorColor[i];
                if(w2==0){
                    mu2 += 0;
                }else{
                    mu2 += (i*aux)/w2;
                }

            }

            float mudeT = (mu1 * w1) + (mu2 * w2);
            float varianza= (float)( (w1* (Math.pow((mu1 - mudeT),2))) + (w2* (Math.pow((mu2 - mudeT),2))) );
            if (varianza > maximo) {
                maximo = varianza;
                maximoT= t;
            }
        }

        umbralizar(maximoT);
        this.resultadoOtsu.setText(getString(R.string.resultado_Otsu).replace("{umbralOtsu}", String.valueOf(maximoT)));
    }

    private float[] calcularProbabilidades(Bitmap bitmap) {

        int cantidadTotalPixeles= bitmap.getHeight() * bitmap.getWidth();
        int cantidadTotalPixeles2=0;
        float [] probabilidadPorColor = new float[256];

        // Calculo el histograma, pero en float
        for (int x = 0; x < bitmap.getWidth() ; x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int valorGris = Color.red(bitmap.getPixel(x,y));
                probabilidadPorColor[valorGris] ++;
                cantidadTotalPixeles2 ++;
            }
        }
        // Divido el histograma por la cantidad total de pixeles
        for (int i = 0; i < 256 ; i++) {
            float aux= probabilidadPorColor[i] / cantidadTotalPixeles;
            probabilidadPorColor[i]= aux;
        }
        return probabilidadPorColor;
    }

    private int[] calcularHistograma(Bitmap bitmap) {

        int[] histograma = new int[256];
        for (int x = 0; x < bitmap.getWidth() ; x++) {

            for (int y = 0; y < bitmap.getHeight(); y++) {

                int valorGris = Color.red(bitmap.getPixel(x,y));
                histograma[valorGris] ++;
            }
        }
        return histograma;
    }


    private void buscarWOtsu(Bitmap bitmap1, int t, float [] probabilidadPorColor) {

        /*
        pixelesOtsuUno = new ArrayList<>();
        pixelesOtsuDos = new ArrayList<>();
        w1=0;
        w2=0;

        for (int x = 0; x < bitmap1.getWidth(); x++) {
            for (int y = 0; y < bitmap1.getHeight(); y++) {

                Integer valorPixelBitmap1 = Color.red(bitmap1.getPixel(x, y));
                if (valorPixelBitmap1 <= umbral) {
                    pixelesOtsuUno.add(valorPixelBitmap1);
                    w1+= probabilidadPorColor[valorPixelBitmap1];
                } else {
                    pixelesOtsuDos.add(valorPixelBitmap1);
                    w2+= probabilidadPorColor[valorPixelBitmap1];
                }

            }
        }
        */


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
