package valjevac.kresimir.homework3.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import valjevac.kresimir.homework3.R;

public class BitmapHelper {

    public static byte[] compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

        return outStream.toByteArray();
    }

    public static Bitmap decompressBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private static Bitmap scaleBitmap(Bitmap bitmap) {
        final int maxSize = 480;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int scaleWidth;
        int scaleHeight;

        if(width > height){
            scaleWidth = maxSize;
            scaleHeight = (height * maxSize) / width;
        } else {
            scaleHeight = maxSize;
            scaleWidth = (width * maxSize) / height;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, false);

        bitmap.recycle();

        return resizedBitmap;
    }

    public static Bitmap loadBitmap(Context context, Uri location, boolean scale) {
        Bitmap fallbackBitmap = BitmapFactory.decodeResource(context.getApplicationContext().getResources(),
                R.drawable.ic_person_details);

        if (location == null) {
            return fallbackBitmap;
        }

        Bitmap bitmap;
        InputStream bitmapStream = null;

        try {
            bitmapStream = context.getApplicationContext().getContentResolver().openInputStream(location);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmapStream != null) {
            bitmap = BitmapFactory.decodeStream(bitmapStream);

            try {
                bitmapStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if (scale) {
                bitmap = scaleBitmap(bitmap);
            }

            return bitmap;
        }

        if (scale) {
            fallbackBitmap = scaleBitmap(fallbackBitmap);
        }

        return fallbackBitmap;
    }
}
