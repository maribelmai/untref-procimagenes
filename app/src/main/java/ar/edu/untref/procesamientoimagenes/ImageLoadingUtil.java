package ar.edu.untref.procesamientoimagenes;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by mmaisano on 19/03/16.
 */
public class ImageLoadingUtil {

    public static Bitmap readBitmapFromPPM2 (String file) throws IOException {

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
}
