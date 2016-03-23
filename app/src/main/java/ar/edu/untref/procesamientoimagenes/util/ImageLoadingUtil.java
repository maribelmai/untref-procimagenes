package ar.edu.untref.procesamientoimagenes.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * Created by mmaisano on 19/03/16.
 */
public class ImageLoadingUtil {

    private static final String LOG_TAG = ImageLoadingUtil.class.getSimpleName();

    public static Bitmap readPPM (String file) throws IOException {

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(new File(file)));
        if (reader.read() != 'P' || reader.read() != '6')
            return null;

        reader.read(); //Eat newline
        String widths = "" , heights = "";
        char temp;
        while ((temp = (char) reader.read()) != ' ') {
            widths += temp;
        }
        while ((temp = (char) reader.read()) >= '0' && temp <= '9')
            heights += temp;
        if (reader.read() != '2' || reader.read() != '5' || reader.read() != '5')
            return null;
        reader.read();

        int width = Integer.valueOf(widths);
        int height = Integer.valueOf(heights);
        int[] colors = new int[width * height];

        byte [] pixel = new byte[3];
        int len = 0;
        int cnt = 0;
        int total = 0;
        int[] rgb = new int[3];
        while ((len = reader.read(pixel)) > 0) {
            for (int i = 0; i < len; i ++) {
                rgb[cnt] = pixel[i]>=0?pixel[i]:(pixel[i] + 255);
                if ((++cnt) == 3) {
                    cnt = 0;
                    colors[total++] = Color.rgb(rgb[0], rgb[1], rgb[2]);
                }
            }
        }

        Bitmap bmp = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
        return bmp;
    }

    //TODO: Encontrar cómo se hace
    public static Bitmap readPGM2(String path) {

        int[][] data2D = null;
        int width = 0;
        int height = 0;

        try {

            FileInputStream fileInputStream = new FileInputStream(path);
            DataInputStream dis = new DataInputStream(fileInputStream);
            StreamTokenizer streamTokenizer = new StreamTokenizer(dis);

            BufferedReader d = new BufferedReader(new InputStreamReader(fileInputStream));
            String magic = d.readLine();    // first line contains P2 or P5
            String line = d.readLine();     // second line contains height and width
            while (line.startsWith("#")) {
                line = d.readLine();
            }
            Scanner s = new Scanner(line);

            width = s.nextInt();
            height = s.nextInt();

            data2D = new int[width][height];
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    data2D[row][col] = dis.readUnsignedByte();
                    System.out.print(data2D[row][col] + " ");
                }
                System.out.println();
            }
        } catch (Exception e) {

        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
            }
        }

        return bitmap;
    }

    public static Bitmap readRaw(String path) throws IOException {

        FileInputStream fileInputStream = null;

        File file = new File(path);

        byte[] src = new byte[(int) file.length()];

        fileInputStream = new FileInputStream(file);
        fileInputStream.read(src);
        fileInputStream.close();


        Bitmap bm = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        bm.copyPixelsFromBuffer(ByteBuffer.wrap(src));

        return bm;
    }
}
