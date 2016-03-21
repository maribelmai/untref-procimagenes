package ar.edu.untref.procesamientoimagenes.dialogos;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadObtenerPixel;
import butterknife.ButterKnife;

/**
 * Created by mmaisano on 20/03/16.
 */
public class DialogoModificarColor {

    public void mostrar(final ActividadObtenerPixel actividadObtenerPixel, final String xIngresado, final String yIngresado, final ImageView imageView) {

        final int x = Integer.parseInt(xIngresado);
        final int y = Integer.parseInt(yIngresado);

        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        int pixel = bitmap.getPixel(x, y);

        int RValue = (pixel >> 16) & 0xff;
        int G = (pixel >> 8) & 0xff;
        int B = pixel & 0xff;

        View viewModificarColor = LayoutInflater.from(actividadObtenerPixel).inflate(R.layout.view_seleccionar_color, null);
        final EditText red = ButterKnife.findById(viewModificarColor, R.id.r);
        red.setText(String.valueOf(RValue));
        final EditText green = ButterKnife.findById(viewModificarColor, R.id.g);
        green.setText(String.valueOf(G));
        final EditText blue = ButterKnife.findById(viewModificarColor, R.id.b);
        blue.setText(String.valueOf(B));
        final View previewColor = ButterKnife.findById(viewModificarColor, R.id.previewColor);
        previewColor.setBackgroundColor(Color.rgb(RValue, G, B));

        TextWatcher colorSeleccionado = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NADA
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!red.getText().toString().isEmpty() &&
                        !green.getText().toString().isEmpty() &&
                        !blue.getText().toString().isEmpty()) {

                    int redValue = Integer.parseInt(red.getText().toString());
                    int greenValue = Integer.parseInt(green.getText().toString());
                    int blueValue = Integer.parseInt(blue.getText().toString());

                    if (redValue > 255) {
                        redValue = 255;
                        red.setText("255");
                    }
                    if (greenValue > 255) {
                        greenValue = 255;
                        green.setText("255");
                    }
                    if (blueValue > 255) {
                        blueValue = 255;
                        blue.setText("255");
                    }

                    previewColor.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //NADA
            }
        };

        TextView modificandoPixel = ButterKnife.findById(viewModificarColor, R.id.modificandoPixel);
        modificandoPixel.setText(modificandoPixel.getText().toString().replace("{valorX}", xIngresado));
        modificandoPixel.setText(modificandoPixel.getText().toString().replace("{valorY}", yIngresado));

        red.addTextChangedListener(colorSeleccionado);
        green.addTextChangedListener(colorSeleccionado);
        blue.addTextChangedListener(colorSeleccionado);

        AlertDialog.Builder builder = new AlertDialog.Builder(actividadObtenerPixel);
        builder.setView(viewModificarColor);
        builder.setTitle(R.string.modificar_color_titulo_dialogo);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!red.getText().toString().isEmpty() &&
                        !green.getText().toString().isEmpty() &&
                        !blue.getText().toString().isEmpty()) {

                    int redValue = Integer.parseInt(red.getText().toString());
                    int greenValue = Integer.parseInt(green.getText().toString());
                    int blueValue = Integer.parseInt(blue.getText().toString());

                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    bitmap.recycle();

                    mutableBitmap.setPixel(x, y, Color.rgb(redValue, greenValue, blueValue));
                    imageView.setImageBitmap(mutableBitmap);

                    actividadObtenerPixel.mostrarColor(x, y);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //NADA
            }
        });

        builder.show();
    }
}
