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
public class ActividadUmbral extends ActividadBasica {

    @Bind(R.id.imagen)
    ImageView imageView;

    @Bind(R.id.imagenUmbralizada)
    ImageView imagenUmbralizada;

    @Bind(R.id.nombreImagen)
    TextView nombreImagen;

    @Bind(R.id.umbralSeleccionado)
    TextView umbralSeleccionado;

    @Bind(R.id.muestraColor)
    View muestraColor;

    private File imagen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Umbralizar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
        this.nombreImagen.setText(this.imagen.getName());
        getAplicacion().mostrarImagen(imagen, imageView);
    }

    @OnClick(R.id.umbralizar)
    public void seleccionarUmbral() {

        View view = LayoutInflater.from(this).inflate(R.layout.view_seleccion_valor, null);
        final EditText inputEscalar = (EditText) view.findViewById(R.id.valor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(R.string.umbralizar);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String valorIngresado = inputEscalar.getText().toString();
                Integer valorEntero = Integer.valueOf(valorIngresado);

                if (valorEntero < 256) {

                    umbralSeleccionado.setText(valorIngresado);
                    muestraColor.setBackgroundColor(Color.rgb(valorEntero, valorEntero, valorEntero));
                    umbralizar(valorEntero);
                }
                else {
                    Toast.makeText(ActividadUmbral.this, "El umbral ingresado tiene que estar entre 0 y 255", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void umbralizar(int umbral) {

        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Bitmap bitmapModificado = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);

        for (int x = 0; x < bitmap.getWidth(); x++) {

            for (int y = 0; y < bitmap.getHeight(); y++) {

                int valorPixel = Color.blue(bitmap.getPixel(x,y));

                if (valorPixel > umbral) {
                    bitmapModificado.setPixel(x, y, Color.rgb(255, 255, 255));
                }
                else {
                    bitmapModificado.setPixel(x, y, Color.rgb(0, 0, 0));
                }
            }
        }

        imagenUmbralizada.setImageBitmap(bitmapModificado);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_umbral;
    }
}