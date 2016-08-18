package valjevac.kresimir.pokemonApp.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import valjevac.kresimir.pokemonApp.PokemonApplication;

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

    public static void setBoolean(boolean value, String key) {
        getSharedPrefs().edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key) {
        return getSharedPrefs().getBoolean(key, false);
    }

    public static void logout() {

        SharedPreferencesHelper.setInt(0, SharedPreferencesHelper.USER_ID);
        SharedPreferencesHelper.setString("", SharedPreferencesHelper.AUTH_TOKEN);
        SharedPreferencesHelper.setString("", SharedPreferencesHelper.USER);
        SharedPreferencesHelper.setString("", SharedPreferencesHelper.EMAIL);
    }

    public static void login(int userId, String authToken, String username, String email) {

        SharedPreferencesHelper.setInt(userId, SharedPreferencesHelper.USER_ID);
        SharedPreferencesHelper.setString(authToken, SharedPreferencesHelper.AUTH_TOKEN);
        SharedPreferencesHelper.setString(username, SharedPreferencesHelper.USER);
        SharedPreferencesHelper.setString(email, SharedPreferencesHelper.EMAIL);
    }
}
