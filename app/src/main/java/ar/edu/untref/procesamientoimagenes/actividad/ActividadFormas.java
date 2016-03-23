package ar.edu.untref.procesamientoimagenes.actividad;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import ar.edu.untref.procesamientoimagenes.R;
import butterknife.Bind;

/**
 * Created by mmaisano on 23/03/16.
 */
public class ActividadFormas extends ActividadBasica {

    @Bind(R.id.cuadrado)
    ImageView cuadrado;

    @Bind(R.id.circulo)
    ImageView circulo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Formas");

        generarCuadrado();
        generarCirculo();
    }

    private void generarCirculo() {

    }

    private void generarCuadrado() {

        int ancho = 200;
        int alto = 200;

        int blanco = Color.rgb(255, 255, 255);

        Bitmap bitmapCuadrado = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

        for(int x=0; x <= ancho -1; x++){
            for(int y=0; y<= alto -1; y++){

                if (x >= 50 && x <= 150 && y >= 50 && y <=150) {

                    bitmapCuadrado.setPixel(x, y, blanco);
                }
            }
        }

        try {
            File archivo = getAplicacion().guardarArchivo(bitmapCuadrado, "/", "cuadrado.png");
            getAplicacion().mostrarImagen(archivo, cuadrado);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_formas;
    }
}
