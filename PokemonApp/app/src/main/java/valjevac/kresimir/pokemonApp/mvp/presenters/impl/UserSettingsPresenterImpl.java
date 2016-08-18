package valjevac.kresimir.pokemonApp.mvp.presenters.impl;

import android.text.TextUtils;
import android.util.Patterns;

import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.helpers.ApiErrorHelper;
import valjevac.kresimir.pokemonApp.helpers.NetworkHelper;
import valjevac.kresimir.pokemonApp.helpers.SharedPreferencesHelper;
import valjevac.kresimir.pokemonApp.interfaces.DeleteAccountListener;
import valjevac.kresimir.pokemonApp.interfaces.UpdateEmailListener;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.User;
import valjevac.kresimir.pokemonApp.mvp.interactors.UserSettingsInteractor;
import valjevac.kresimir.pokemonApp.mvp.interactors.impl.UserSettingsInteractorImpl;
import valjevac.kresimir.pokemonApp.mvp.presenters.UserSettingsPresenter;
import valjevac.kresimir.pokemonApp.mvp.views.UserSettingsView;
import valjevac.kresimir.pokemonApp.network.ApiManager;

public class UserSettingsPresenterImpl implements UserSettingsPresenter {

    private UserSettingsView view;

    private UserSettingsInteractor interactor;

    private static final String NOT_FOUND = "not found";

    public UserSettingsPresenterImpl(UserSettingsView view) {
        this.view = view;
        this.interactor = new UserSettingsInteractorImpl();
    }

    @Override
    public void updateEmail(String email, boolean changesMade) {
        if (!changesMade) {
            return;
        }

        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            return;
        }

        if (!validateEmailAddress(email)) {
            view.showMessage(R.string.wrong_email_format);
            return;
        }

        view.showProgress();

        String authToken = SharedPreferencesHelper.getString(SharedPreferencesHelper.AUTH_TOKEN);
        int userId = SharedPreferencesHelper.getInt(SharedPreferencesHelper.USER_ID);

        User user = new User(userId, email);
        BaseData<User> data = new BaseData<>(ApiManager.TYPE_USERS, user);
        BaseResponse<BaseData<User>> request = new BaseResponse<>(data);

        request.setAuthToken(authToken);

        interactor.updateEmail(request, new UpdateEmailListener() {
            @Override
            public void onEmailUpdateSuccess(BaseResponse<BaseData<User>> body) {
                view.hideProgress();

                SharedPreferencesHelper.setString(body.getData().getAttributes().getEmail(), SharedPreferencesHelper.EMAIL);

                view.showMessage(R.string.email_updated);
                view.onEmailUpdateSuccess(body.getData().getAttributes().getEmail());
            }

            @Override
            public void onEmailUpdateFail(String error) {
                view.hideProgress();

                if (ApiErrorHelper.createError(error)) {

                    if (ApiErrorHelper.getFullErrorLowercase(0).equals(NOT_FOUND)) {
                        view.showMessage(R.string.user_not_found);
                    }
                    else {
                        view.showMessage(ApiErrorHelper.getFullError(0));
                    }
                }
            }
        });
    }

    @Override
    public void deleteAccount(int id) {
        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            return;
        }

        view.showProgress();

        interactor.deleteAccount(id, new DeleteAccountListener() {
            @Override
            public void onDeleteAccountSuccess() {
                view.hideProgress();

                SharedPreferencesHelper.logout();

                view.onAccountDeletedSuccess();
            }

            @Override
            public void onDeleteAccountFail(String error) {
                view.hideProgress();

                if (ApiErrorHelper.createError(error)) {
                    view.showMessage(ApiErrorHelper.getFullError(0));
                }
            }
        });
    }

    @Override
    public void cancel() {

        if (interactor != null) {
            interactor.cancel();
        }
    }

    private boolean validateEmailAddress(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
