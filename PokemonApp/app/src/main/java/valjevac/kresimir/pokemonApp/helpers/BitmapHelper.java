package valjevac.kresimir.pokemonApp.helpers;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import valjevac.kresimir.pokemonApp.PokemonApplication;
import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.network.ApiManager;

public class BitmapHelper {
    private static final int MAX_SIZE = 360;

    private static final String INTERNAL_CONTENT = "content://";

    public static Uri getResourceUri(int resourceId) {
        String baseResourceUri = PokemonApplication.getAppContext().getPackageName();

        return Uri.parse(baseResourceUri + resourceId);
    }

    public static void loadResourceBitmap(ImageView imageView, int resourceId, boolean scale) {
        Uri imageUri = getResourceUri(resourceId);

        loadBitmap(imageView, imageUri.toString(), scale);
    }

    private static void loadDefaultBitmap(ImageView imageView) {

        Glide.with(imageView.getContext())
                .load(R.drawable.ic_person_details)
                .crossFade()
                .fitCenter()
                .into(imageView);
    }

    public static void loadBitmap(final ImageView imageView, String location, boolean scale) {

        if (location == null) {

            loadDefaultBitmap(imageView);
            return;
        }

        Uri uri;

        if (!location.contains(INTERNAL_CONTENT)) {
            String url = ApiManager.BASE_URL + location;
            uri = Uri.parse(url);
        }
        else {
            uri = Uri.parse(location);
        }

        if (scale) {
            Glide.with(imageView.getContext())
                    .load(uri)
                    .error(R.drawable.ic_person_details)
                    .override(MAX_SIZE, MAX_SIZE)
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
        }
        else {
            Glide.with(imageView.getContext())
                    .load(uri)
                    .error(R.drawable.ic_person_details)
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
        }
    }
}
