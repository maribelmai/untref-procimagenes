package ar.edu.untref.procesamientoimagenes.actividad;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.tareas.segmentacion.Segmentacion;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribelmai on 22/6/16.
 */
public class ActividadSeguimientoVideo extends ActividadBasica {

    @Bind(R.id.imagen)
    ImageView imagen;

    @Bind(R.id.primer_frame)
    CropImageView primerFrame;

    private Bitmap bitmapOriginal;

    private Point punto1;
    private Point punto2;

    private int i;
    private Callback callbackVideo = new CallbackVideo();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        primerFrame.setInitialFrameScale(0.5f);
        File directorio = new File(getAplicacion().getDirectorioImagenes());
        String ruta = directorio.getAbsolutePath() + "/frame_1.jpeg";

        primerFrame.startLoad(Uri.fromFile(new File(ruta)), new LoadCallback() {
            @Override
            public void onSuccess() {

                Bitmap bitmap = ((BitmapDrawable) primerFrame.getDrawable()).getBitmap();
                bitmapOriginal = bitmap.copy(Bitmap.Config.RGB_565, true);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_seguimiento_video;
    }

    @OnClick(R.id.identificar)
    public void identificarObjeto() {

        RectF seleccion = primerFrame.getActualCropRect();

        punto1 = new Point((int)seleccion.left, (int)seleccion.top);
        punto2 = new Point((int)seleccion.right, (int)seleccion.bottom);

        Bitmap bitmapResultante = Segmentacion.identificarObjeto(bitmapOriginal, 100, 50, punto1, punto2);
        primerFrame.setImageBitmap(bitmapResultante);
        reproducirVideo();
    }

    private void reproducirVideo() {

        i = 1;
        Picasso.with(this).load("file:///android_asset/video_senora/Frame" + i + ".jpeg").noFade().noPlaceholder().into(imagen, this.callbackVideo);
    }

    public class CallbackVideo implements Callback {


        private final String TAG = CallbackVideo.class.getSimpleName();

        @Override
        public void onSuccess() {

            i++;

            Log.i(TAG, "cargando...: " + i);

            Picasso.with(ActividadSeguimientoVideo.this).load("file:///android_asset/video_senora/Frame" + i + ".jpeg").into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    Log.i(TAG, "segmentando...: " + i);
                    final Bitmap bitmapResultante = Segmentacion.segmentarImagen(bitmap);
                    Log.i(TAG, "segmentado...: " + i);

//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {

                            try {
                                Log.i(TAG, "guardando...: " + i);
                                File file = getAplicacion().guardarArchivo(bitmapResultante, "/tmp/", System.currentTimeMillis() + ".png");
                                Log.i(TAG, "abriendo...: " + i);
                                Picasso.with(ActividadSeguimientoVideo.this).load(file).noPlaceholder().noFade().into(imagen, CallbackVideo.this);
                            } catch (Exception e) {
                                Log.e(TAG, "Temporal no se pudo guardar: " + e);
                            }
//                        }
//                    }, 100);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.i(TAG, "onBitmapFailed: " + errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.i(TAG, "prepare...: " + i);
                }
            });
        }

        @Override
        public void onError() {
            Picasso.with(ActividadSeguimientoVideo.this).load("file:///android_asset/video_senora/Frame" + (i - 1) + ".jpeg").noPlaceholder().noFade().into(imagen);
        }
    }

    @OnClick(R.id.reproducir)
    public void reproducirDeNuevo() {
        reproducirVideo();
    }
}
