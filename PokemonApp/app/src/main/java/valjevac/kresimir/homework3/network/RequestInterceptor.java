package valjevac.kresimir.homework3.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import valjevac.kresimir.homework3.helpers.SharedPreferencesHelper;

public class RequestInterceptor implements Interceptor {

    public static final String LOGIN_URL = "https://pokeapi.infinum.co/api/v1/users/login";

    public static final String AUTH_TOKEN = "auth-token";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request modifiedRequest;

        String token = SharedPreferencesHelper.getString(SharedPreferencesHelper.AUTH_TOKEN);

        if (!originalRequest.url().toString().equals(LOGIN_URL)) {
            Request.Builder builder = originalRequest.newBuilder();
            HttpUrl modifiedUrl = originalRequest.url().newBuilder()
                    .addQueryParameter(AUTH_TOKEN, token)
                    .build();

            builder.url(modifiedUrl.toString());
            modifiedRequest = builder.build();

            return chain.proceed(modifiedRequest);
        }

        return chain.proceed(originalRequest);
    }
}
