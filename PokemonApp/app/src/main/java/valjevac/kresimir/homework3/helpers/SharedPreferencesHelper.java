package valjevac.kresimir.homework3.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import valjevac.kresimir.homework3.PokemonApplication;

public class SharedPreferencesHelper {

    private static final String PACKAGE = "valjevac.kresimir.homework3";

    public static final String USER_ID = "UserId";

    public static final String USER = "User";

    public static final String AUTH_TOKEN = "AuthToken";

    public static final String EMAIL = "Email";

    private static SharedPreferences sharedPrefs;

    private static SharedPreferences getSharedPrefs() {
        if (sharedPrefs == null) {
            sharedPrefs = PokemonApplication.getInstance().getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
        }

        return sharedPrefs;
    }

    public static void setString(String value, String key) {
        getSharedPrefs().edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return getSharedPrefs().getString(key, "");
    }

    public static void setInt(int value, String key) {
        getSharedPrefs().edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        return getSharedPrefs().getInt(key, 0);
    }
}
