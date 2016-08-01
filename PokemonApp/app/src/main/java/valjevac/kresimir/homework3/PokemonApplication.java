package valjevac.kresimir.homework3;

import android.app.Application;
import android.content.Context;

public class PokemonApplication extends Application {
    public static Context context;

    private static PokemonApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        PokemonApplication.context = getApplicationContext();
    }

    public static PokemonApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return PokemonApplication.context;
    }
}
