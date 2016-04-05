package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ar.edu.untref.procesamientoimagenes.Aplicacion;
import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.fragmentos.FragmentoListaArchivos;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribel on 4/4/16.
 */
public class ActividadOperaciones extends ActividadBasica {

    @Bind(R.id.nombreImagen1)
    TextView nombreImagen1;

    @Bind(R.id.nombreImagen2)
    TextView nombreImagen2;

    @Bind(R.id.imagen1)
    ImageView imagen1;

    @Bind(R.id.imagen2)
    ImageView imagen2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Operaciones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_operaciones;
    }

    @OnClick({R.id.nombreImagen1, R.id.nombreImagen2})
    public void seleccionarImagen(final View view) {

        View archivosView = LayoutInflater.from(this).inflate(R.layout.view_seleccionar_archivo, null);
        final FragmentoListaArchivos fragmentoArchivos = (FragmentoListaArchivos) getSupportFragmentManager().findFragmentById(R.id.fragmentoArchivos);
        fragmentoArchivos.setDisplay(ImageView.class.cast(archivosView.findViewById(R.id.imagenSeleccionada)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(archivosView);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (view.getId() == R.id.nombreImagen1) {
                    Aplicacion.getContext().mostrarImagen(fragmentoArchivos.obtenerSeleccion(), imagen1);
                } else if (view.getId() == R.id.nombreImagen2) {
                    Aplicacion.getContext().mostrarImagen(fragmentoArchivos.obtenerSeleccion(), imagen2);
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(fragmentoArchivos);
                transaction.commit();
            }
        });

        if (!isFinishing()) {
            alertDialog.show();
        }
    }
}
