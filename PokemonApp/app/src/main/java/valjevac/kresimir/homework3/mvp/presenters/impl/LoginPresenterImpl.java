package valjevac.kresimir.homework3.mvp.presenters.impl;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.helpers.ApiErrorHelper;
import valjevac.kresimir.homework3.helpers.NetworkHelper;
import valjevac.kresimir.homework3.helpers.SharedPreferencesHelper;
import valjevac.kresimir.homework3.interfaces.LoginListener;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.User;
import valjevac.kresimir.homework3.mvp.interactors.impl.LoginInteractorImpl;
import valjevac.kresimir.homework3.mvp.presenters.LoginPresenter;
import valjevac.kresimir.homework3.mvp.views.LoginView;
import valjevac.kresimir.homework3.network.ApiManager;

public class LoginPresenterImpl implements LoginPresenter {

    private LoginView view;

    private LoginInteractorImpl interactor;

    private final static int CURRENT_ERROR = 0;

    public LoginPresenterImpl(LoginView view) {
        this.view = view;
        this.interactor = new LoginInteractorImpl();
    }

    @Override
    public void login(String email, String password, ViewGroup viewGroup) {

        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            return;
        }

        if (!validateInputFields(email, password, viewGroup)) {
            return;
        }

        view.showProgress();

        User user = new User(email, password);
        BaseData<User> data = new BaseData<>(ApiManager.TYPE_SESSION, user);
        BaseResponse<BaseData<User>> request = new BaseResponse<>(data);

        interactor.login(request, new LoginListener() {
            @Override
            public void onLoginSuccess(BaseResponse<BaseData<User>> body) {
                SharedPreferencesHelper.login(body.getData().getId(), body.getData().getAttributes().getAuthToken(),
                        body.getData().getAttributes().getUsername(), body.getData().getAttributes().getEmail());

                view.onLoginSuccess();
            }

            @Override
            public void onLoginError(String error) {
                view.hideProgress();

                if (ApiErrorHelper.createError(error)) {
                    view.showMessage(ApiErrorHelper.getFullError(CURRENT_ERROR));
                }

                view.onLoginFail();
            }
        });
    }

    @Override
    public void cancel() {

        if (interactor != null) {
            interactor.cancel();
        }
    }

    private boolean validateInputFields(String email, String password, ViewGroup viewGroup) {
        if (!checkForEmptyFields(viewGroup)) {
            return false;
        }

        if (!validateEmailAddress(email)) {
            view.showMessage(R.string.wrong_email_format);
            return false;
        }

        if (password.length() < 8) {
            view.showMessage(R.string.password_length_warning);
            return false;
        }

        return true;
    }

    private EditText validateEditTexts(ViewGroup v) {

        EditText invalidEditText = null;

        for (int i = 0; i < v.getChildCount(); i++) {
            View child = v.getChildAt(i);

            if (child instanceof EditText) {
                EditText editText = (EditText) child;

                if(TextUtils.isEmpty(editText.getText())) {
                    return editText;
                }
            }
            else if(child instanceof ViewGroup) {
                invalidEditText = validateEditTexts((ViewGroup)child);

                if(invalidEditText != null) {
                    break;
                }
            }
        }

        return invalidEditText;
    }

    private boolean validateEmailAddress(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean checkForEmptyFields(ViewGroup v) {
        EditText emptyEditText = validateEditTexts(v);

        if (emptyEditText != null) {
            view.showMessage(R.string.empty_field_warning);
            view.onEmptyField(emptyEditText);
        }

        return emptyEditText == null;
    }
}
