package ar.edu.untref.procesamientoimagenes.actividad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import ar.edu.untref.procesamientoimagenes.Aplicacion;
import ar.edu.untref.procesamientoimagenes.R;
import ar.edu.untref.procesamientoimagenes.fragmentos.FragmentoListaArchivos;
import ar.edu.untref.procesamientoimagenes.modelo.Constante;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by maribelmai on 29/6/16.
 */
public class ActividadSIFT extends ActividadBasica {

    private static final String LOG_TAG = ActividadSIFT.class.getSimpleName();

    @Bind(R.id.imagen1)
    ImageView imagen1;

    @Bind(R.id.imagen2)
    ImageView imagen2;

    @Bind(R.id.resultado)
    ImageView resultado;

    @Bind(R.id.diferenciaAceptada)
    EditText factorDiferenciaAceptada;

    private File imagen;

    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Operaciones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(Constante.EXTRA_IMAGEN)) {
            this.imagen = (File) getIntent().getSerializableExtra(Constante.EXTRA_IMAGEN);
            getAplicacion().mostrarImagen(imagen, imagen1);
        }
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

    @Override
    protected int getLayout() {
        return R.layout.actividad_sift;
    }

    @OnClick(R.id.sift)
    public void siftImagen1(){

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
        Bitmap bitmap = ((BitmapDrawable) imagen1.getDrawable()).getBitmap();
        Bitmap mutable = bitmap.copy(Bitmap.Config.RGB_565, true);

        Mat rgba = new Mat();
        Utils.bitmapToMat(mutable, rgba);
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY);
        detector.detect(rgba, keyPoints);
        Features2d.drawKeypoints(rgba, keyPoints, rgba);
        Utils.matToBitmap(rgba, mutable);
        imagen1.setImageBitmap(mutable);
    }

    @OnClick(R.id.comparar)
    public void comparar() {

        try {

            String diferenciaAceptada = factorDiferenciaAceptada.getText().toString();
            Float diferenciaAceptadaValor = 3F;

            if (!diferenciaAceptada.isEmpty() && diferenciaAceptada != null) {
                diferenciaAceptadaValor = Float.valueOf(diferenciaAceptada);
            }

            Mat refMat = new Mat();
            Mat srcMat = new Mat();

            Bitmap refBitmap = ((BitmapDrawable) imagen1.getDrawable()).getBitmap();
            refBitmap = refBitmap.copy(Bitmap.Config.RGB_565, true);
            Bitmap srcBitmap = ((BitmapDrawable) imagen2.getDrawable()).getBitmap();
            srcBitmap = srcBitmap.copy(Bitmap.Config.RGB_565, true);

            if (refBitmap != null && srcBitmap != null) {

                Utils.bitmapToMat(refBitmap, refMat);
                Utils.bitmapToMat(srcBitmap, srcMat);

                MatOfDMatch matches = new MatOfDMatch();
                MatOfDMatch goodMatches = new MatOfDMatch();

                LinkedList<DMatch> listOfGoodMatches = new LinkedList<>();

                LinkedList<Point> refObjectList = new LinkedList<>();
                LinkedList<Point> srcObjectList = new LinkedList<>();

                MatOfKeyPoint refKeypoints = new MatOfKeyPoint();
                MatOfKeyPoint srcKeyPoints = new MatOfKeyPoint();

                Mat refDescriptors = new Mat();
                Mat srcDescriptors = new Mat();

                MatOfPoint2f reference = new MatOfPoint2f();
                MatOfPoint2f source = new MatOfPoint2f();

                FeatureDetector orbFeatureDetector = FeatureDetector.create(FeatureDetector.SIFT);
                orbFeatureDetector.detect(refMat, refKeypoints);
                orbFeatureDetector.detect(srcMat, srcKeyPoints);

                DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
                descriptorExtractor.compute(refMat, refKeypoints, refDescriptors);
                descriptorExtractor.compute(srcMat, srcKeyPoints, srcDescriptors);

                DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
                matcher.match(refDescriptors, srcDescriptors, matches);

                double max_dist = 0;
                double min_dist = 100;
                List<DMatch> matchesList = matches.toList();

                for (int i = 0; i < refDescriptors.rows(); i++) {
                    Double distance = (double) matchesList.get(i).distance;
                    if (distance < min_dist) min_dist = distance;
                    if (distance > max_dist) max_dist = distance;
                }

                for (int i = 0; i < refDescriptors.rows(); i++) {
                    if (matchesList.get(i).distance <= diferenciaAceptadaValor * min_dist) {
                        listOfGoodMatches.add(matchesList.get(i));
                    }
                }

                goodMatches.fromList(listOfGoodMatches);

                List<KeyPoint> refObjectListKeypoints = refKeypoints.toList();
                List<KeyPoint> srcObjectListKeypoints = srcKeyPoints.toList();

                for (int i = 0; i < listOfGoodMatches.size(); i++) {
                    refObjectList.addLast(refObjectListKeypoints.get(listOfGoodMatches.get(i).queryIdx).pt);
                    srcObjectList.addLast(srcObjectListKeypoints.get(listOfGoodMatches.get(i).trainIdx).pt);
                }

                reference.fromList(refObjectList);
                source.fromList(srcObjectList);

                Imgproc.cvtColor(refMat, refMat, Imgproc.COLOR_RGBA2RGB, 1);
                Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGBA2RGB, 1);

                Mat outputImage = new Mat();
                Bitmap comboBmp = combineImages(refBitmap, srcBitmap);
                Utils.bitmapToMat(comboBmp, outputImage);

                Features2d.drawMatches(refMat, refKeypoints, srcMat, srcKeyPoints, goodMatches, outputImage);

                Bitmap bitmap = Bitmap.createBitmap(outputImage.cols(), outputImage.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(outputImage, bitmap);
                resultado.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "Selecciona dos imágenes", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ocurrio un error ejecutando SIFT", Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap combineImages(Bitmap c, Bitmap s) {
        Bitmap cs;

        int width, height;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        return cs;
    }

    @Override
    public void finish() {

        String nombreOriginal = imagen.getName();
        File file = null;

        try {
            file = getAplicacion().guardarArchivo(((BitmapDrawable) resultado.getDrawable()).getBitmap(), "/tmp/", nombreOriginal.substring(0, nombreOriginal.lastIndexOf(".")) + "_" + System.currentTimeMillis() + nombreOriginal.substring(nombreOriginal.lastIndexOf(".")));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Temporal no se pudo guardar: " + e);
        }

        Intent intent = new Intent();
        intent.putExtra(Constante.EXTRA_IMAGEN, file);
        setResult(Constante.RESULT_CODE_IMAGEN_MODIFICADA, intent);
        super.finish();
    }
}
