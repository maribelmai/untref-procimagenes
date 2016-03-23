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
 * Created by mmaisano on 20/03/16.
 */
public class ActividadDegrade extends ActividadBasica {

    private static final String LOG_TAG = ActividadDegrade.class.getSimpleName();

    @Bind(R.id.degradeGrises)
    ImageView degradeGrises;

    @Bind(R.id.degradeColores)
    ImageView degradeColores;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Degradé");

        generarDegradeGrises();
        generarDegradeColores();
    }

    private void generarDegradeGrises() {

        int ancho = 256;
        int alto = 256;

        Bitmap bitmapDegradeGrises = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

        //TODO: Llenar toda la primer fila de 0, la segunda de 1, la tercera de 2, etc.
        //Por ejemplo:
        // bitmapDegradeGrises.setPixel(0, 0, Color.rgb(0,0,0));
        // bitmapDegradeGrises.setPixel(0, 0, 0); <--- Es lo mismo que lo de arriba pero sirve solo para grises.
        // Esa línea hace que el píxel en la posición 0, 0 sea de color negro.
        int i;
        int j;

        for(i=0;i<=255;i++){
            for(j=0;j<=255;j++){
                bitmapDegradeGrises.setPixel(i, j, Color.rgb(i, i, i));
            }

        }


        //Muestro lo que dibujaste así ves cómo va quedando

        try {
            File archivo = getAplicacion().guardarArchivo(bitmapDegradeGrises, "/", "degradeGrises.png");
            getAplicacion().mostrarImagen(archivo, degradeGrises);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generarDegradeColores() {

        int ancho = 256;
        int alto = 1600;

        Bitmap bitmapDegradeGrises = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);
        int i;
        int j;
        int y=0;

        for(i=0;i<=255;i++){
            for(j=0;j<=255;j++){
                bitmapDegradeGrises.setPixel(i, y, Color.rgb( 255 , 0 , j ));
                y=y+1;
            }
        }
        for(i=0;i<=255;i++){
            for(j=0;j<=255;j++){
                bitmapDegradeGrises.setPixel(i, y, Color.rgb( 255-j , 0 , 255 ));
                y=y+1;
            }
        }
        for(i=0;i<=255;i++){
            for (j = 0; j <= 255; j++) {
                bitmapDegradeGrises.setPixel(i, y, Color.rgb( 0 , j , 255 ));
                y=y+1;
            }
        }
        for(i=0;i<=255;i++){
            for (j = 0; j <= 255; j++) {
                bitmapDegradeGrises.setPixel(i, y, Color.rgb( 0 ,  255 , 255-j ));
                y=y+1;
            }
        }
        for(i=0;i<=255;i++){
            for (j = 0; j <= 255; j++) {
                bitmapDegradeGrises.setPixel(i, y, Color.rgb( j , 255 , 0 ));
                y=y+1;
            }

        }
        for(i=0;i<=255;i++){
            for (j = 0; j <= 255; j++) {
                bitmapDegradeGrises.setPixel(i, y, Color.rgb( 255 , 255-j , 0 ));
                y=y+1;
            }

        }

        //Muestro lo que dibujaste así ves cómo va quedando

        try {
            File archivo = getAplicacion().guardarArchivo(bitmapDegradeGrises, "/", "degradeGrises.png");
            getAplicacion().mostrarImagen(archivo, degradeColores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected int getLayout() {
        return R.layout.actividad_degrade;
    }


}
