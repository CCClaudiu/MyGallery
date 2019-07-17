package com.claudiu.mygallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class BitmapUtils {
    static private int getDegrees(String path) {
        ExifInterface exifInterface;
        try {
            exifInterface = new ExifInterface(path);
            int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            int degree = 0;
            if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
                degree = 90;
            } else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
                degree = 180;
            } else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
                degree = 270;
            }
            return degree;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static Bitmap getOriginBitmap(String pathString, int requireWidth, int requireHeight) {
        int sample = 1;
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(pathString, op);

        if (op.outHeight > requireHeight || op.outWidth > requireWidth) {
            final int heightRatio = Math.round((float) op.outHeight / (float) requireHeight);
            final int widthRatio = Math.round((float) op.outWidth / (float) requireWidth);
            sample = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        op.inJustDecodeBounds = false;
        op.inSampleSize = sample;

        int degree = getDegrees(pathString);
        if (degree == 0) {
            return BitmapFactory.decodeFile(pathString, op);
        }
        Bitmap srcBitmap = BitmapFactory.decodeFile(pathString, op);
        if (srcBitmap == null) {
            return null;
        }
        Matrix m = new Matrix();
        m.setRotate(degree,
                (float) srcBitmap.getWidth(), (float) srcBitmap.getHeight());
        try {
            Bitmap b2 = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), m, true);
            if (b2 != srcBitmap) {
                srcBitmap.recycle();
            }
            return b2;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }
}
