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
public class ActividadObtenerEscalaDeGrises extends ActividadBasica {

    private static final String LOG_TAG = ActividadObtenerEscalaDeGrises.class.getSimpleName();
    private File imagen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Escala de Grises");
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_obtener_escala_de_grises;
    }


}
