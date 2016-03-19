package ar.edu.untref.procesamientoimagenes.adaptadores;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.fragmentos.FragmentoEditor;
import butterknife.ButterKnife;

/**
 * Created by mmaisano on 19/03/16.
 */
public class AdaptadorArchivos extends RecyclerView.Adapter<AdaptadorArchivos.ViewHolder> {

    private List<File> archivos = new ArrayList<>();
    private FragmentoEditor fragmentoEditor;

    public AdaptadorArchivos(FragmentoEditor fragmentoEditor) {
        this.fragmentoEditor = fragmentoEditor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archivo, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        File archivo = archivos.get(position);
        holder.archivo = archivo;
        holder.nombreArchivo.setText(archivo.getName());
    }

    @Override
    public int getItemCount() {
        return archivos.size();
    }

    public void agregarItems(List<File> listaArchivos) {
        this.archivos = listaArchivos;
        notifyItemRangeChanged(0, listaArchivos.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        File archivo;
        ImageView icono;
        TextView nombreArchivo;

        private final View.OnClickListener previsualizar = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentoEditor.cargarImagen(archivo);
            }
        };

        public ViewHolder(View itemView) {

            super(itemView);
            this.nombreArchivo = ButterKnife.findById(itemView, R.id.nombreArchivo);
            itemView.setOnClickListener(this.previsualizar);
        }
    }
}
