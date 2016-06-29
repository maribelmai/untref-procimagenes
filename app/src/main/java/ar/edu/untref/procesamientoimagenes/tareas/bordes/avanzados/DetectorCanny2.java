package ar.edu.untref.procesamientoimagenes.tareas.bordes.avanzados;

/**
 * Created by maribelmai on 29/6/16.
 */
public class DetectorCanny2 {

//    private ProcesadorDeImagenes procesador = new ProcesadorDeImagenes();
//
//    public Bitmap deteccionDeBordes(Bitmap original, int t1, int t2){
//
//        Integer[][] borde= new Integer[original.getWidth()][original.getHeight()];
//        borde=aplicarFiltroAImagen(original,1, t1,t2);
//        return crearImagenesDeBorde(borde,original.getWidth(),original.getHeight());
//    }
//
//    private Integer[][] aplicarFiltroAImagen(Imagen original,double desvio,int t1,int t2){
//        Integer[][] borde= new Integer[original.getWidth()][original.getHeight()];
//        Integer[][] magnitud= new Integer[original.getWidth()][original.getHeight()];
//        Integer[][] angulos= new Integer[original.getWidth()][original.getHeight()];
//        Integer[][] bordesMaximos= new Integer[original.getWidth()][original.getHeight()];
//        Imagen resultado = new Imagen(original.getWidth(), original.getHeight());
//        resultado = suavizarImagenes(original, desvio);
//        magnitud=calcularMagnitudesDeBorde(resultado);
//        angulos=obtenerAngulos(resultado);
//        bordesMaximos=sumprimirNoMaximos(magnitud,angulos,original.getWidth(),original.getHeight());
//        borde=umbralizacionConHisteresis(bordesMaximos,original.getWidth(),original.getHeight(),t1,t2);
//        return borde;
//    }
//
//    private Imagen suavizarImagenes(Imagen original, double desvio) {
//        return procesador.pasarFiltroGaussiano(original,desvio);
//    }
//
//    private Integer[][] calcularMagnitudesDeBorde(Imagen suavizada) {
//        int[][] matrizMascaraY= {{-1,-2,-1},{0,0,0},{1,2,1}};
//        int[][] matrizMascaraX={{-1,0,1},{-2,0,2},{-1,0,1}};
//        Integer[][] matrizX=new Integer[suavizada.getWidth()][suavizada.getHeight()];
//        Integer[][] matrizY=new Integer[suavizada.getWidth()][suavizada.getHeight()];
//        Integer[][] magnitud=new Integer[suavizada.getWidth()][suavizada.getHeight()];
//        matrizX=procesador.obtenerG( suavizada, suavizada.getWidth(),suavizada.getHeight(), matrizMascaraX);
//        matrizY=procesador.obtenerG( suavizada, suavizada.getWidth(),suavizada.getHeight(), matrizMascaraY);
//        magnitud = procesador.obtenerMatrizPyS(matrizX, matrizY,suavizada, suavizada.getWidth(), suavizada.getHeight());
//        return magnitud;
//    }
//
//    private Integer[][] obtenerAngulos(Imagen suavizada) {
//        Integer[][] angulos= new Integer[suavizada.getWidth()][suavizada.getHeight()];
//        int[][] matrizMascaraY= {{-1,-2,-1},{0,0,0},{1,2,1}};
//        int[][] matrizMascaraX={{-1,0,1},{-2,0,2},{-1,0,1}};
//        angulos = calcularMatrizDeAngulos(procesador.obtenerG(suavizada, suavizada.getWidth(), suavizada.getHeight(), matrizMascaraX),
//                procesador.obtenerG(suavizada, suavizada.getWidth(), suavizada.getHeight(), matrizMascaraY),suavizada.getWidth(),suavizada.getHeight());
//        return angulos;
//    }
//
//    private Integer[][] calcularMatrizDeAngulos( Integer[][] gx,Integer[][] gy, int ancho, int alto) {
//        Integer[][] angulosDeBorde = new Integer[ancho][alto];
//        double angulo;
//        for (int i=0;i<ancho;i++){
//            for (int j=0;j<alto;j++){
//                if (gx[i][j]>0){
//                    angulo = Math.toDegrees(Math.atan(gy[i][j]/gx[i][j]))+90;
//                    angulosDeBorde[i][j] = clasificarAngulo(angulo);
//                }else{
//                    angulosDeBorde[i][j] = 0;
//                }
//            }
//        }
//        return angulosDeBorde;
//    }
//
//    private Integer clasificarAngulo(double angulo) {
//        int anguloCorregido=0;
//        if (angulo >0 && angulo <=22.5){
//            anguloCorregido = 0;
//        }
//        if (angulo >22.5 && angulo <=67.5){
//            anguloCorregido = 45;
//        }
//        if (angulo >67.5 && angulo <=112.5){
//            anguloCorregido = 90;
//        }
//        if (angulo >112.5 && angulo <=157.5){
//            anguloCorregido = 135;
//        }
//        if (angulo >157.5 && angulo <=180){
//            anguloCorregido = 0;
//        }
//        return anguloCorregido;
//    }
//
//    private Integer[][] sumprimirNoMaximos(Integer[][] magnitud, Integer[][] angulos, int ancho, int alto) {
//        return supresion(magnitud, angulos,ancho,alto);
//    }
//
//    private Integer[][] supresion(Integer[][] magnitud, Integer[][] angulos, int ancho,int alto) {
//        Integer[][] magnitudDeBorde = new Integer[ancho][alto];
//        for (int i=1;i<ancho-1;i++){
//            for (int j=1;j<alto-1;j++){
//                if (angulos[i][j] == 0){
//                    magnitudDeBorde[i][j]=verArribaYAbajo(magnitud, i, j);
//                }
//                if (angulos[i][j] == 45){
//                    magnitudDeBorde[i][j]=verDiagonalPositiva(magnitud, i, j);
//                }
//                if (angulos[i][j] == 90){
//                    magnitudDeBorde[i][j]=verDerechaEIzquierda(magnitud, i, j);
//                }
//                if (angulos[i][j] == 135){
//                    magnitudDeBorde[i][j]=verDiagonalNegativa(magnitud, i, j);
//                }
//            }
//        }
//        return magnitudDeBorde;
//    }
//
//    private int verDiagonalNegativa(Integer[][] magnitud, int i, int j) {
//        int valor=0;
//        if ((magnitud[i-1][j-1] <= magnitud[i][j])&&(magnitud[i+1][j+1] <= magnitud[i][j])){
//            valor =magnitud[i][j];
//        }
//        return valor;
//    }
//
//    private int verDerechaEIzquierda(Integer[][] magnitud, int i, int j) {
//        int valor=0;
//        if ((magnitud[i-1][j] <= magnitud[i][j])&&(magnitud[i+1][j] <= magnitud[i][j])){
//            valor =magnitud[i][j];
//        }
//        return valor;
//    }
//
//    private int verDiagonalPositiva(Integer[][] magnitud, int i, int j) {
//        int valor=0;
//        if ((magnitud[i+1][j-1] <= magnitud[i][j])&&(magnitud[i-1][j+1] <= magnitud[i][j])){
//            valor =magnitud[i][j];
//        }
//        return valor;
//    }
//
//    private int verArribaYAbajo(Integer[][] magnitud, int i, int j) {
//        int valor=0;
//        if ((magnitud[i][j-1] <= magnitud[i][j])&&(magnitud[i][j+1] <= magnitud[i][j])){
//            valor =magnitud[i][j];
//        }
//        return valor;
//    }
//
//    private Integer[][] umbralizacionConHisteresis(Integer[][] bordesMaximos, int ancho, int alto,int t1,int t2) {
//        Integer[][] bordes = new Integer[ancho][alto];
//        for (int i=0;i<ancho;i++){
//            bordes[i][0]=0;
//            bordes[i][alto-1]=0;
//        }
//        for (int j=0;j<alto;j++){
//            bordes[0][j]=0;
//            bordes[ancho-1][j]=0;
//        }
//        for (int i=1;i<ancho-1;i++){
//            for (int j=1;j<alto-1;j++){
//                if (bordesMaximos[i][j] < t1){
//                    bordes[i][j]=0;
//                }else{
//                    if (bordesMaximos[i][j]> t2){
//                        bordes[i][j]=1;
//                    }else{
//                        bordes[i][j]=2;
//                    }
//                }
//            }
//        }
//        for (int i=1;i<ancho-1;i++){
//            for (int j=1;j<alto-1;j++){
//                if(bordes[i][j]==2){
//                    bordes[i][j]=verVecinos( i, j,bordes);
//                }
//            }
//        }
//        return bordes;
//    }
//
//    private int verVecinos(int i, int j, Integer[][] bordesMaximos) {
//        int valor=0;
//        if ((bordesMaximos[i-1][j-1]== 1)||(bordesMaximos[i][j-1]== 1)||(bordesMaximos[i+1][j-1]== 1)||(bordesMaximos[i+1][j]== 1)||
//                (bordesMaximos[i+1][j+1]== 1)||(bordesMaximos[i][j+1]== 1)||(bordesMaximos[i-1][j+1]== 1)||(bordesMaximos[i-1][j]== 1)){
//            valor= 1;
//        }
//        return valor;
//    }
//
//    private Imagen crearImagenesDeBorde(Integer[][] bordes,int ancho, int alto) {
//        Color negro = new Color(Color.black.getRGB());
//        Color blanco = new Color(Color.white.getRGB());
//        Imagen resultado =new Imagen(ancho,alto);
//        for (int i=0;i<ancho;i++){
//            for (int j=0;j<alto;j++){
//                if (bordes[i][j] > 0){
//                    resultado.setRGB(i, j, blanco.getRGB());
//                }else{
//                    resultado.setRGB(i, j, negro.getRGB());
//                }
//            }
//        }
//        return resultado;
//    }
}
