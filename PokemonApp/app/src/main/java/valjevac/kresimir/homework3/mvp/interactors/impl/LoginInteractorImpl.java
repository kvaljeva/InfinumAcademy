package valjevac.kresimir.homework3.mvp.interactors.impl;

import android.support.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.interfaces.LoginListener;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.User;
import valjevac.kresimir.homework3.mvp.interactors.LoginInteractor;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

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
