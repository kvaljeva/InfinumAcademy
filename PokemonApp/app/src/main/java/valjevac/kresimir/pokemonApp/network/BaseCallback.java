package valjevac.kresimir.pokemonApp.network;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseCallback<T> implements Callback<T> {

    private volatile boolean isCanceled;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (isCanceled) {
            return;
        }

        if (response.isSuccessful()) {
            if (response.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                onNoContent(response.body(), response);
            }
            else {
                onSuccess(response.body(), response);
            }
        }
        else {
            int statusCode = response.code();
            ResponseBody errorBody = response.errorBody();

            failure(errorBody, statusCode);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (isCanceled) {
            return;
        }

        onUnknownError(t != null ? t.getMessage() : null);
    }

    private void failure(ResponseBody cause, int statusCode) {
        if (isCanceled) {
            return;
        }

        String error = null;

        try {
            error = cause.string();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                onUnauthorized(error);
            }
            else if (statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
                onForbidden(error);
            }
            else if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                onNotFound(error);
            }
            else {
                onUnknownError(error);
            }
        }
        catch (Exception e) {
            onUnknownError(e.getMessage());
        }
    }

    public void cancel() {
        isCanceled = true;
    }

    public void reset() {
        isCanceled = false;
    }

    private void onNoContent(T body, Response<T> response) {
        onSuccess(body, response);
    }

    private void onNotFound(String error) {
        onUnknownError(error);
    }

    private void onForbidden(String error) {
        onUnknownError(error);
    }

    private void onUnauthorized(String error) {
        onUnknownError(error);
    }

    public abstract void onUnknownError(@Nullable String error);

    public abstract void onSuccess(T body, Response<T> response);
}
