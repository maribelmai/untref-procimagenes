package ar.edu.untref.procesamientoimagenes.fragmentos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ar.edu.untref.procesamientoimagenes.Aplicacion;
import ar.edu.untref.procesamientoimagenes.actividad.ActividadBasica;
import butterknife.ButterKnife;

/**
 * Created by mmaisano on 19/03/16.
 */
public abstract class FragmentoBasico extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), getLayout(), null);
        ButterKnife.bind(this,view);
        return view;
    }

    protected abstract int getLayout();

    @NonNull
    protected String getDirectorioImagenes() {
        return ActividadBasica.class.cast(getActivity()).getDirectorioImagenes();
    }

    protected Aplicacion getAplicacion() {
        return ActividadBasica.class.cast(getActivity()).getAplicacion();
    }
}