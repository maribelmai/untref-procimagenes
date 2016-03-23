package ar.edu.untref.procesamientoimagenes.actividad;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.fragmentos.FragmentoEditor;
import ar.edu.untref.procesamientoimagenes.fragmentos.FragmentoListaArchivos;

public class ActividadPrincipal extends ActividadBasica {

    private static final String LOG_TAG = ActividadPrincipal.class.getSimpleName();
    private static final int PEDIR_PERMISOS_ESCRITURA = 1;
    private FragmentoListaArchivos fragmentoListaArchivos;
    private FragmentoEditor fragmentoEditor = new FragmentoEditor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.editorPlaceholder, fragmentoEditor);
        fragmentTransaction.commitAllowingStateLoss();

        this.fragmentoListaArchivos = FragmentoListaArchivos.getInstance(fragmentoEditor);
        copiarArchivosAMemoriaExterna();
    }

    @Override
    protected int getLayout() {
        return R.layout.actividad_principal;
    }

    //Configuracion Inicial
    private void copiarArchivosAMemoriaExterna() {

        boolean tengoPermisosEscritura = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        if (tengoPermisosEscritura) {

            File directorio = new File(getAplicacion().getDirectorioImagenes());

            if(!directorio.exists()){
                directorio.mkdirs();
            }

            copiarRaw(R.raw.cameraman, directorio.getAbsolutePath() + "/cameraman.png");
            copiarRaw(R.raw.cameraman_rot, directorio.getAbsolutePath() + "/cameraman_rot.png");
            copiarRaw(R.raw.coins, directorio.getAbsolutePath() + "/coins.jpg");
            copiarRaw(R.raw.coins_grayscale, directorio.getAbsolutePath() + "/coins_grayscale.jpeg");
            copiarRaw(R.raw.coins_squared, directorio.getAbsolutePath() + "/coins_squared.png");
            copiarRaw(R.raw.test, directorio.getAbsolutePath() + "/test.png");
            copiarRaw(R.raw.test_jpg, directorio.getAbsolutePath() + "/test_jpg.jpg");
            copiarRaw(R.raw.test_rotated_left, directorio.getAbsolutePath() + "/test_rotated_left.png");
            copiarRaw(R.raw.test_scalated_rotated, directorio.getAbsolutePath() + "/test_scalated_rotated.png");
            copiarRaw(R.raw.wdg3, directorio.getAbsolutePath() + "/wdg3.gif");
            copiarRaw(R.raw.abe_natsumi, directorio.getAbsolutePath() + "/abe_natsumi.pgm");
            copiarRaw(R.raw.hara_fumina, directorio.getAbsolutePath() + "/hara_fumina.ppm");
            copiarRaw(R.raw.butterfly, directorio.getAbsolutePath() + "/butterfly.bmp");
            copiarRaw(R.raw.lena_gray, directorio.getAbsolutePath() + "/lena_gray.raw");

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.listaArchivosPlaceholder, fragmentoListaArchivos);
            fragmentTransaction.commitAllowingStateLoss();
        }
        else {

            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, PEDIR_PERMISOS_ESCRITURA);
        }
    }

    private void copiarRaw(int id, String ruta) {

        try {

            InputStream in = getResources().openRawResource(id);
            FileOutputStream out = new FileOutputStream(ruta);
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        }
        catch (Exception e) {
            Log.w(LOG_TAG, "No se pudo copiar el archivo: " + e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PEDIR_PERMISOS_ESCRITURA) {

            boolean dioPermiso = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED;

            if (dioPermiso) {
                copiarArchivosAMemoriaExterna();
            }
            else {
                Toast.makeText(this, R.string.no_hay_permisos_escritura, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
