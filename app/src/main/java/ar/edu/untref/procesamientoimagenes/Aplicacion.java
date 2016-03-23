package ar.edu.untref.procesamientoimagenes;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ar.edu.untref.procesamientoimagenes.util.ImageLoadingUtil;

/**
 * Created by mmaisano on 19/03/16.
 */
public class Aplicacion extends Application {

    public void mostrarImagen(File imagen, ImageView imageView) {

        if (imagen.getName().endsWith(".ppm")) {
            try {
                Bitmap loadedBitmap = ImageLoadingUtil.readPPM(imagen.getAbsolutePath());
                imageView.setImageBitmap(loadedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (imagen.getName().endsWith(".raw")) {
            Toast.makeText(this, "Todavía no estamos soportando este tipo de archivo", Toast.LENGTH_LONG).show();

//            try {
//                Bitmap loadedBitmap = ImageLoadingUtil.readRaw(imagen.getAbsolutePath());
//                imageView.setImageBitmap(loadedBitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        else if (imagen.getName().endsWith(".pgm")) {
            Toast.makeText(this, "Todavía no estamos soportando este tipo de archivo", Toast.LENGTH_LONG).show();
        }
        else {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(imagen));
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File guardarArchivo(Bitmap bitmap, String subDirectorio, String nombreArchivo) throws IOException {

        String ruta = getDirectorioImagenes() + subDirectorio;
        File dir = new File(ruta);

        if(!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, nombreArchivo);
        FileOutputStream fOut = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();

        return file;
    }

    public String getDirectorioImagenes() {
        return Environment.getExternalStorageDirectory() + "/ProcImagenes";
    }
}
