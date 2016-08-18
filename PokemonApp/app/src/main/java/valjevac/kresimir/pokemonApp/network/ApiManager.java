package valjevac.kresimir.pokemonApp.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import valjevac.kresimir.pokemonApp.BuildConfig;
import valjevac.kresimir.pokemonApp.network.deserializers.DateDeserializer;

public class ApiManager {

    public static final String BASE_URL = "https://pokeapi.infinum.co";

    public static final String TYPE_SESSION = "session";

    public static final String TYPE_USERS = "users";

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .create();

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
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GSON))
            .client(client)
            .build();

    private static final PokemonService POKEMON_SERVICE = REST_ADAPTER.create(PokemonService.class);

    public static PokemonService getService() {
        return POKEMON_SERVICE;
    }
}
