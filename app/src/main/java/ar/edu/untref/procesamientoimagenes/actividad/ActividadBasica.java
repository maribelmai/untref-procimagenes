package ar.edu.untref.procesamientoimagenes.actividad;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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

    @NonNull
    public String getDirectorioImagenes() {
        return Environment.getExternalStorageDirectory() + "/ProcImagenes";
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
}
