package ar.edu.untref.procesamientoimagenes.actividad;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
        Point punto1 = new Point((int)seleccion.left, (int)seleccion.top);
        Point punto2 = new Point((int)seleccion.right, (int)seleccion.bottom);

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
            Picasso.with(ActividadSeguimientoVideo.this).load("file:///android_asset/video_senora/Frame" + i + ".jpeg").noPlaceholder().noFade().into(imagen, CallbackVideo.this);
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
