package com.goldfish_dictionary;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class Util {
    public static byte[] imageViewToByte(ImageView imageView) {
        if (imageView == null) {
            throw new NullPointerException();
        }
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bmp = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static String previewText(String text, int limit) {
        if (text == null) {
            throw new NullPointerException();
        }
        if (limit < 0) {
            throw new IllegalArgumentException();
        }
        if (text.equals("")) return "";
        if (limit == 0) return "...";

        String res = text.replaceAll("\n", " ").replaceAll("\\s\\s+", " ").trim();
        if (res.length() <= limit) return res;
        return res.substring(0, limit) + "..." ;
    }
}
