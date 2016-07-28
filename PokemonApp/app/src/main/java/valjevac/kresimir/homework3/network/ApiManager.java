package valjevac.kresimir.homework3.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import valjevac.kresimir.homework3.BuildConfig;

public class ApiManager {

    public static final String API_ENDPOINT = "https://pokeapi.infinum.co";

    public static final String TYPE_SESSION = "session";

    private static final Gson GSON = new GsonBuilder().create();

    private static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {

            if (BuildConfig.DEBUG) {
                Log.e("API_TAG", message);
            }
        }
    }).setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new RequestInterceptor())
            .addNetworkInterceptor(httpLoggingInterceptor)
            .build();

    private static Retrofit REST_ADAPTER = new Retrofit.Builder()
            .baseUrl(API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(GSON))
            .client(client)
            .build();

    private static final PokemonService POKEMON_SERVICE = REST_ADAPTER.create(PokemonService.class);

    public static PokemonService getService() {
        return POKEMON_SERVICE;
    }
}
