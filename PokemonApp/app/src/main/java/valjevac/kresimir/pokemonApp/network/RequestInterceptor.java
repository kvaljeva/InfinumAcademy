package valjevac.kresimir.pokemonApp.network;

import android.content.res.Resources;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import valjevac.kresimir.pokemonApp.PokemonApplication;
import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.helpers.SharedPreferencesHelper;

public class RequestInterceptor implements Interceptor {

    public static final String LOGIN_URL = "https://pokeapi.infinum.co/api/v1/users/login";

    private static final String AUTHORIZATION = "Authorization";

    public static final String AUTH_TOKEN = "auth-token";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request modifiedRequest;

        String token = SharedPreferencesHelper.getString(SharedPreferencesHelper.AUTH_TOKEN);
        String email = SharedPreferencesHelper.getString(SharedPreferencesHelper.EMAIL);

        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(email)) {
            Request.Builder builder = originalRequest.newBuilder();
            Resources res = PokemonApplication.getAppContext().getResources();

            String headerInfo =  String.format(res.getString(R.string.authorization_header_content), token, email);
            builder.addHeader(AUTHORIZATION, headerInfo);

            modifiedRequest = builder.build();

            return chain.proceed(modifiedRequest);
        }

        return chain.proceed(originalRequest);
    }
}
