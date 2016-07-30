package valjevac.kresimir.homework3.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

import valjevac.kresimir.homework3.PokemonApplication;
import valjevac.kresimir.homework3.R;

public class BitmapHelper {
    private static final int QUALITY = 100;
    private static final int MAX_SIZE = 360;
    private static final String baseResourceUri  = "android.resource://valjevac.kresimir.homework3/";

    private static Bitmap getBitmap(Uri location) {
        try {
            return Glide
                    .with(PokemonApplication.getAppContext())
                    .load(location)
                    .asBitmap()
                    .into(-1, -1)
                    .get();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static Uri getResourceUri(int resourceId) {
        return Uri.parse(baseResourceUri + resourceId);
    }

    public static void loadResourceBitmap(ImageView imageView, int resourceId, boolean scale) {
        Uri imageUri = getResourceUri(resourceId);

        loadBitmap(imageView, imageUri, scale);
    }

    public static String getImageBase64(Uri location) {
        Bitmap image = getBitmap(location);

        if (image != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, QUALITY, outputStream);

            byte[] imageBytes = outputStream.toByteArray();

            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        return null;
    }

    public static void loadBitmap(ImageView imageView, Uri location, boolean scale) {

        if (location == null) {
            Glide.with(imageView.getContext())
                    .load(R.drawable.ic_person_details)
                    .crossFade()
                    .into(imageView);
        }

        if (scale) {
            Glide.with(imageView.getContext())
                    .load(location)
                    .override(MAX_SIZE, MAX_SIZE)
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
        }
        else {
            Glide.with(imageView.getContext())
                    .load(location)
                    .crossFade()
                    .into(imageView);
        }
    }
}
