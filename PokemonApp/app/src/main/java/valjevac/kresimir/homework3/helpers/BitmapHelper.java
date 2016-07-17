package valjevac.kresimir.homework3.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapHelper {

    public static byte[] compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

        return outStream.toByteArray();
    }

    public static Bitmap decompressBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap loadBitmap(Context context, Uri location) {
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

            return bitmap;
        }

        return null;
    }
}
