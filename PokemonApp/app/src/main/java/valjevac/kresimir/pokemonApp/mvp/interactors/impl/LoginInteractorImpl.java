package valjevac.kresimir.pokemonApp.mvp.interactors.impl;

import android.support.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.pokemonApp.interfaces.LoginListener;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.User;
import valjevac.kresimir.pokemonApp.mvp.interactors.LoginInteractor;
import valjevac.kresimir.pokemonApp.network.ApiManager;
import valjevac.kresimir.pokemonApp.network.BaseCallback;

public class LoginInteractorImpl implements LoginInteractor {

    Call<BaseResponse<BaseData<User>>> loginUserCall;

    public LoginInteractorImpl() {
    }

    @Override
    public void login(BaseResponse<BaseData<User>> request, final LoginListener listener) {
        loginUserCall = ApiManager.getService().loginUser(request);

        loginUserCall.enqueue(new BaseCallback<BaseResponse<BaseData<User>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                if (loginUserCall.isCanceled()) {
                    return;
                }

                listener.onLoginError(error);
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<User>> body, Response<BaseResponse<BaseData<User>>> response) {
                listener.onLoginSuccess(body);
            }
        });
    }

    @Override
    public void cancel() {

        if (loginUserCall != null) {
            loginUserCall.cancel();
        }
    }
}
