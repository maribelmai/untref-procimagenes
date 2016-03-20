package ar.edu.untref.procesamientoimagenes;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import ar.edu.untref.procesamientoimagenes.util.ImageLoadingUtil;

/**
 * Created by mmaisano on 19/03/16.
 */
public class Aplicacion extends Application {

    private static final String LOG_TAG = Aplicacion.class.getSimpleName();

    public void mostrarImagen(File imagen, ImageView imageView, Context context) {

        if (imagen.getName().endsWith(".ppm")) {
            try {
                Bitmap loadedBitmap = ImageLoadingUtil.readPPM(imagen.getAbsolutePath());
                imageView.setImageBitmap(loadedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (imagen.getName().endsWith(".pgm")) {
            Toast.makeText(context, "Todavía no estamos soportando este tipo de archivo", Toast.LENGTH_LONG).show();
        }
        else {
            Picasso.with(context).load("file:///" + imagen.getAbsolutePath()).into(imageView);
        }
    }
}
