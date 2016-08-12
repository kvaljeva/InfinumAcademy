package valjevac.kresimir.homework3.mvp.interactors.impl;

import android.support.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.interfaces.SignupListener;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.User;
import valjevac.kresimir.homework3.mvp.interactors.SignupInteractor;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

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
