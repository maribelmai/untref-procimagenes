package ar.edu.untref.procesamientoimagenes.actividad;

import android.os.Bundle;
import android.support.annotation.Nullable;

import ar.edu.untref.procesamientoimagenes.R;

/**
 * Created by maribel on 4/6/16.
 */
public class ActividadHistograma extends ActividadBasica {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Histograma");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_histograma;
    }
}
