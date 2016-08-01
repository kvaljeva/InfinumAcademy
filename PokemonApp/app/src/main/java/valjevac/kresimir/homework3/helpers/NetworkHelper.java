package valjevac.kresimir.homework3.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

import valjevac.kresimir.homework3.PokemonApplication;

public class NetworkHelper {

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) PokemonApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
