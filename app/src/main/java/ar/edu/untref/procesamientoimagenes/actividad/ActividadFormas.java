package ar.edu.untref.procesamientoimagenes.actividad;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    @Bind(R.id.imagenVacia)
    ImageView imagenVacia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Formas");

        generarCuadrado();
        generarCirculo();
        generarImagenVacia();
    }

    private void generarImagenVacia() {

        int ancho = 200;
        int alto = 200;

        Bitmap bitmapVacio = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

        for(int x=0; x < ancho; x++){
            for(int y=0; y < alto; y++){
                bitmapVacio.setPixel(x, y, Color.GRAY);
            }
        }

        try {
            File archivo = getAplicacion().guardarArchivo(bitmapVacio, "/", "imagen_vacia.png");
            getAplicacion().mostrarImagen(archivo, imagenVacia);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generarCirculo() {

        int ancho = 200;
        int alto = 200;

        int blanco = Color.rgb(255, 255, 255);

        Bitmap bitmapCirculo = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(blanco);

        Canvas canvas = new Canvas(bitmapCirculo);
        canvas.drawCircle(100, 100, 50, paint);

        circulo.setImageBitmap(bitmapCirculo);

        try {
            getAplicacion().guardarArchivo(bitmapCirculo, "/", "circulo.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
