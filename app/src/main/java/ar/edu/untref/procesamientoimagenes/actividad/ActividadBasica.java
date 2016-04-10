package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import ar.edu.untref.procesamientoimagenes.Aplicacion;
import butterknife.ButterKnife;

/**
 * Created by mmaisano on 19/03/16.
 */
public abstract class ActividadBasica extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
    }

    public Aplicacion getAplicacion() {
        return Aplicacion.class.cast(getApplicationContext());
    }

    protected abstract int getLayout();

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected String darFormatoRGB(int r, int g, int b) {
        return "<b><font color=\"#db2222\">R: " + r + "</><font color=\"#158e34\"> &nbsp;&nbsp; G: " + g + "</><font color=\"#0342ab\"> &nbsp;&nbsp; B: " + b + "</></>";
    }

    protected String darFormatoHSV(int pixel) {

        float[] hsv = new float[3];
        Color.colorToHSV(pixel, hsv);

        int h = (int) hsv[0];
        int s = (int) (hsv[1] * 100);
        int v = (int) (hsv[2] * 100);

        return "H: " + h + "    S: " + s + "    V: " + v;
    }

    protected void ocultarTeclado() {

        try {

            InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception ignored) {
            //NO SE CAPTURA, PUEDE QUE EL TECLADO YA ESTÉ CERRADO Y ENTONCES FALLE
        }
    }
}
