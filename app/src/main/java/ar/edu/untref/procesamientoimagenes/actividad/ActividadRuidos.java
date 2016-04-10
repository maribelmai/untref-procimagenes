package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribel on 4/6/16.
 */
public class ActividadRuidos extends ActividadBasica {

    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;


    private File imagen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Ruidos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);
    }

    @OnClick(R.id.ruidoGaussiano)
    public void aplicarRuidoGaussiano(){

    }

    @OnClick(R.id.ruidoRayleigh)
    public void aplicarRuidoRayleigh(){

    }

    @OnClick(R.id.ruidoExponencial)
    public void aplicarRuidoExponencial(){

    }

    @OnClick(R.id.ruidoSalyPimienta)
    public void aplicarRuidoSalyPimienta(){

    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_ruidos;
    }
}
