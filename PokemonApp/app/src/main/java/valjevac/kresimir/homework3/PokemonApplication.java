package valjevac.kresimir.homework3;

import android.app.Application;
import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class PokemonApplication extends Application {
    public static Context context;

    private static PokemonApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        PokemonApplication.context = getApplicationContext();

        FlowManager.init(new FlowConfig.Builder(this).build());
    }

    public static PokemonApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return PokemonApplication.context;
    }
}
