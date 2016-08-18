package valjevac.kresimir.pokemonApp.mvp.interactors.impl;

import android.support.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.pokemonApp.interfaces.SignupListener;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.User;
import valjevac.kresimir.pokemonApp.mvp.interactors.SignupInteractor;
import valjevac.kresimir.pokemonApp.network.ApiManager;
import valjevac.kresimir.pokemonApp.network.BaseCallback;

public class SignupInteractorImpl implements SignupInteractor {

    Call<BaseResponse<BaseData<User>>> registerUserCall;

    public SignupInteractorImpl() {
    }

    @Override
    public void register(BaseResponse<BaseData<User>> request, final SignupListener listener) {
        registerUserCall = ApiManager.getService().insertUser(request);

        registerUserCall.enqueue(new BaseCallback<BaseResponse<BaseData<User>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                if (registerUserCall.isCanceled()) {
                    return;
                }

                listener.onSignupError(error);
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<User>> body, Response<BaseResponse<BaseData<User>>> response) {
                listener.onSignupSuccess(body);
            }
        });
    }

    @Override
    public void cancel() {

        if (registerUserCall != null) {
            registerUserCall.cancel();
        }
    }
}
