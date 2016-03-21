package ar.edu.untref.procesamientoimagenes;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ar.edu.untref.procesamientoimagenes.util.ImageLoadingUtil;

/**
 * Created by mmaisano on 19/03/16.
 */
public class Aplicacion extends Application {

    private static final String LOG_TAG = Aplicacion.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        configurarUniversalImageLoader();
    }

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

            ImageLoader.getInstance().displayImage("file:///" + imagen.getAbsolutePath(), imageView);
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

    private void configurarUniversalImageLoader() {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new LruMemoryCache(20 * 1024 * 1024))
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);
    }
}
