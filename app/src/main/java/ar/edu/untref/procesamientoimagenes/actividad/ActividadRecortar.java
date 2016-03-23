package ar.edu.untref.procesamientoimagenes.actividad;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by mmaisano on 21/03/16.
 */
public class ActividadRecortar extends ActividadBasica {

    private static final String LOG_TAG = ActividadRecortar.class.getSimpleName();
    @Bind(R.id.imagenOriginal)
    ImageView imagenOriginal;

    @Bind(R.id.imagenRecortada)
    ImageView imagenRecortada;

    @Bind(R.id.desdeHasta)
    TextView desdeHasta;

    @Bind(R.id.guardar)
    View guardar;

    private Point desde;
    private Point hasta;

    private boolean desdeSeleccionado;
    private boolean hastaSeleccionado;

    private File imagen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.recortar);

        this.desde = new Point();
        this.hasta = new Point();

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        getAplicacion().mostrarImagen(this.imagen, imagenOriginal);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_recortar;
    }

    @OnTouch(R.id.imagenOriginal)
    public boolean limitarRecorte(MotionEvent motionEvent) {

        if (!desdeSeleccionado) {
            this.desde.set((int) motionEvent.getX(), (int) motionEvent.getY());
            this.desdeHasta.setText(Html.fromHtml(getString(R.string.desdeHasta)
                    .replace("{x}", "<b>" + String.valueOf(desde.x) + "</b>")
                    .replace("{y}", "<b>" + String.valueOf(desde.y) + "</b>")
                    .replace("{x2},{y2}", "<b>" + "No seleccionado" + "</b>")));
            desdeSeleccionado = true;

        }
        else if (!hastaSeleccionado) {
            this.hasta.set((int) motionEvent.getX(), (int) motionEvent.getY());
            this.desdeHasta.setText(Html.fromHtml(getString(R.string.desdeHasta)
                    .replace("{x}", "<b>" + String.valueOf(desde.x) + "</b>")
                    .replace("{y}", "<b>" + String.valueOf(desde.y) + "</b>")
                    .replace("{x2}", "<b>" + String.valueOf(hasta.x) + "</b>")
                    .replace("{y2}", "<b>" + String.valueOf(hasta.y)) + "</b>"));
            hastaSeleccionado = true;
        }
        else {
            limpiar();
        }

        if (desdeSeleccionado && hastaSeleccionado) {
            mostrarImagenRecortada();
        }

        return false;
    }

    private void mostrarImagenRecortada() {

        int desdeX;
        int hastaX;
        int desdeY;
        int hastaY;

        if (hasta.x > desde.x) {
            desdeX = desde.x;
            hastaX = hasta.x;
        }
        else {
            desdeX = hasta.x;
            hastaX = desde.x;
        }

        if (hasta.y > desde.y) {
            desdeY = desde.y;
            hastaY = hasta.y;
        }
        else {
            desdeY = hasta.y;
            hastaY = desde.y;
        }

        int nuevoAncho = hastaX - desdeX;
        int nuevoAlto = hastaY - desdeY;

        Bitmap bitmapOriginal = ((BitmapDrawable) imagenOriginal.getDrawable()).getBitmap();
        Bitmap bitmapNuevo = Bitmap.createBitmap(nuevoAncho + 1, nuevoAlto + 1, Bitmap.Config.RGB_565);

        for (int i = desdeX , x = 0 ; i <= hastaX; i++ , x ++) {

            for (int j = desdeY, y = 0; j <= hastaY ; j++, y++) {

                bitmapNuevo.setPixel(x, y, bitmapOriginal.getPixel(i, j));
            }
        }

        imagenRecortada.setImageBitmap(bitmapNuevo);
        guardar.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.guardar)
    public void guardar() {

        try {
            String nombreOriginal = imagen.getName();
            String nuevoNombre = nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_recortada_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            getAplicacion().guardarArchivo(((BitmapDrawable) imagenRecortada.getDrawable()).getBitmap(), "/", nuevoNombre);
            Toast.makeText(this, nuevoNombre + " guardada correctamente", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Ocurrió un error guardando el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.limpiar)
    public void limpiar() {

        desdeSeleccionado = false;
        hastaSeleccionado = false;
        this.desde = new Point();
        this.hasta = new Point();
        this.desdeHasta.setText(R.string.sinDesdeHasta);
        guardar.setVisibility(View.INVISIBLE);
        imagenRecortada.setImageDrawable(null);
    }
}
