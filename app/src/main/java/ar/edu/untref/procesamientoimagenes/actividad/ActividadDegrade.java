package ar.edu.untref.procesamientoimagenes.actividad;

import android.graphics.Bitmap;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Degradé");

        generarDegradeGrises();
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




        //Muestro lo que dibujaste así ves cómo va quedando

        try {
            File archivo = getAplicacion().guardarArchivo(bitmapDegradeGrises, "/", "degradeGrises.png");
            getAplicacion().mostrarImagen(archivo, degradeGrises);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_degrade;
    }


}
