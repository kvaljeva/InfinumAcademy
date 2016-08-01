package valjevac.kresimir.homework3.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

import valjevac.kresimir.homework3.PokemonApplication;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.network.ApiManager;

public class BitmapHelper {
    private static final int QUALITY = 100;

    private static final int MAX_SIZE = 360;

    private static final String BASE_RESOURCE_URI  = "android.resource://valjevac.kresimir.homework3/";

    private static final String INTERNAL_CONTENT = "content://";

    public static Uri getResourceUri(int resourceId) {
        return Uri.parse(BASE_RESOURCE_URI + resourceId);
    }

    public static void loadResourceBitmap(ImageView imageView, int resourceId, boolean scale) {
        Uri imageUri = getResourceUri(resourceId);

        loadBitmap(imageView, imageUri.toString(), scale);
    }

    public static String getImageBase64(Uri location) {
        Bitmap image = null;

        try {
            image = MediaStore.Images.Media.getBitmap(PokemonApplication.getAppContext().getContentResolver(), location);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (image != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, QUALITY, outputStream);

            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        }

        return null;
    }

    public static void loadBitmap(ImageView imageView, String location, boolean scale) {

        if (location == null) {
            Glide.with(imageView.getContext())
                    .load(R.drawable.ic_person_details)
                    .crossFade()
                    .into(imageView);

            return;
        }

        Uri uri;

        if (!location.contains(INTERNAL_CONTENT)) {
            String url = ApiManager.API_ENDPOINT + location;
            uri = Uri.parse(url);
        }
        else {
            uri = Uri.parse(location);
        }


        if (scale) {
            Glide.with(imageView.getContext())
                    .load(uri)
                    .override(MAX_SIZE, MAX_SIZE)
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
        }
        else {
            Glide.with(imageView.getContext())
                    .load(uri)
                    .crossFade()
                    .into(imageView);
        }
    }
}
