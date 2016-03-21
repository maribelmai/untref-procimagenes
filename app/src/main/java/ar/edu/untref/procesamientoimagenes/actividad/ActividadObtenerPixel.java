package ar.edu.untref.procesamientoimagenes.actividad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.dialogos.DialogoModificarColor;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

/**
 * Created by mmaisano on 20/03/16.
 */
public class ActividadObtenerPixel extends ActividadBasica {

    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.valorX)
    EditText valorX;

    @Bind(R.id.valorY)
    EditText valorY;

    @Bind(R.id.viewColor)
    View viewColor;

    @Bind(R.id.rgb)
    TextView rgb;

    @Bind(R.id.modificarColor)
    View modificarColor;

    private static final String LOG_TAG = ActividadObtenerPixel.class.getSimpleName();
    private File imagen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.titulo_obtener_pixel);
        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        getAplicacion().mostrarImagen(imagen, imageView, this);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_obtener_pixel;
    }

    @OnTouch(R.id.imagen)
    public boolean obtenerPixel(MotionEvent motionEvent) {

        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        Log.i(LOG_TAG, "Tocado: " + x + "," + y);

        valorX.setText(String.valueOf(x));
        valorY.setText(String.valueOf((y)));

        modificarColor.setVisibility(View.VISIBLE);

        return false;
    }

    @OnTextChanged({R.id.valorY, R.id.valorX})
    public void obtenerColor() {

        String xIngresado = valorX.getText().toString();
        String yIngresado = valorY.getText().toString();

        if (!xIngresado.isEmpty() && !yIngresado.isEmpty()) {

            int x = Integer.valueOf(xIngresado);
            int y = Integer.valueOf(yIngresado);

            mostrarColor(x,y);
        }
    }

    @OnClick(R.id.modificarColor)
    public void modificarColor() {

        new DialogoModificarColor().mostrar(this, valorX.getText().toString(), valorY.getText().toString(), imageView);
    }

    public void mostrarColor(int x, int y) {

        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        try {
            int pixel = bitmap.getPixel(x, y);
            viewColor.setBackgroundColor(pixel);

            int R = (pixel >> 16) & 0xff;
            int G = (pixel >> 8) & 0xff;
            int B = pixel & 0xff;

            String rgbString = darFormato(R, G, B);
            rgb.setText(Html.fromHtml(rgbString));
        }
        catch (IllegalArgumentException e) {
            Toast.makeText(this, "Pixel no válido", Toast.LENGTH_SHORT).show();
        }
    }

    private String darFormato(int r, int g, int b) {
        return "<b><font color=\"#db2222\">R: " + r + "</><font color=\"#158e34\"> &nbsp;&nbsp; G: " + g + "</><font color=\"#0342ab\"> &nbsp;&nbsp; B: " + b + "</></>";
    }

    @Override
    public void finish() {

        String nombreOriginal = imagen.getName();
        File file = null;

        try {
            file = getAplicacion().guardarArchivo(((BitmapDrawable) imageView.getDrawable()).getBitmap(), "/tmp/", nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf(".")));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Temporal no se pudo guardar: " + e);
        }

        Intent intent = new Intent();
        intent.putExtra(Constante.EXTRA_IMAGEN, file);
        setResult(Constante.RESULT_CODE_IMAGEN_MODIFICADA, intent);
        super.finish();
    }
}
