package valjevac.kresimir.homework3.helpers;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.network.ApiManager;

public class BitmapHelper {
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
