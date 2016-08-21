package valjevac.kresimir.pokemonApp.mvp.interactors.impl;

import android.support.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.pokemonApp.interfaces.DeleteAccountListener;
import valjevac.kresimir.pokemonApp.interfaces.UpdateEmailListener;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.User;
import valjevac.kresimir.pokemonApp.mvp.interactors.UserSettingsInteractor;
import valjevac.kresimir.pokemonApp.network.ApiManager;
import valjevac.kresimir.pokemonApp.network.BaseCallback;

public class UserSettingsInteractorImpl implements UserSettingsInteractor {

    Call<BaseResponse<BaseData<User>>> updateEmailCall;

    Call<Void> deleteAccountCall;

    public UserSettingsInteractorImpl() {
    }

    @Override
    public void updateEmail(BaseResponse<BaseData<User>> request, final UpdateEmailListener listener) {
        updateEmailCall = ApiManager.getService().updateEmail(request.getData().getAttributes().getId(), request);

        updateEmailCall.enqueue(new BaseCallback<BaseResponse<BaseData<User>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onEmailUpdateFail(error);
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<User>> body, Response<BaseResponse<BaseData<User>>> response) {
                listener.onEmailUpdateSuccess(body);
            }
        });
    }

    @Override
    public void deleteAccount(int id, final DeleteAccountListener listener) {
        deleteAccountCall = ApiManager.getService().deleteUser(id);

        deleteAccountCall.enqueue(new BaseCallback<Void>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onDeleteAccountFail(error);
            }

            @Override
            public void onSuccess(Void body, Response<Void> response) {
                listener.onDeleteAccountSuccess();
            }
        });
    }

    @Override
    public void cancel() {

        if (updateEmailCall != null) {
            updateEmailCall.cancel();
        }

        if (deleteAccountCall != null) {
            deleteAccountCall.cancel();
        }
    }
}
