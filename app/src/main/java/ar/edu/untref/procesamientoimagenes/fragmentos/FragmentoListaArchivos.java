package ar.edu.untref.procesamientoimagenes.fragmentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.adaptadores.AdaptadorArchivos;
import butterknife.Bind;

/**
 * Created by mmaisano on 19/03/16.
 */
public class FragmentoListaArchivos extends FragmentoBasico {

    @Bind(R.id.listaArchivos)
    RecyclerView listaArchivos;

    @Bind(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private AdaptadorArchivos adaptadorArchivos;
    private SwipeRefreshLayout.OnRefreshListener onRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            cargarArchivos();
            refreshLayout.setRefreshing(false);
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.listaArchivos.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.adaptadorArchivos = new AdaptadorArchivos();
        this.listaArchivos.setAdapter(this.adaptadorArchivos);

        this.refreshLayout.setOnRefreshListener(this.onRefresh);

        this.cargarArchivos();
    }

    private void cargarArchivos() {

        File carpeta = new File(getDirectorioImagenes());
        File[] archivos = carpeta.listFiles();

        List<File> listaArchivos = new ArrayList<>();
        Collections.addAll(listaArchivos, archivos);

        this.adaptadorArchivos.agregarItems(listaArchivos);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragmento_listado_archivos;
    }
}
