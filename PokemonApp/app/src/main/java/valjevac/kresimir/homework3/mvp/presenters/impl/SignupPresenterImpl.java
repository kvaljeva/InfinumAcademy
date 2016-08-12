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
import valjevac.kresimir.homework3.interfaces.SignupListener;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.User;
import valjevac.kresimir.homework3.mvp.interactors.impl.SignupInteractorImpl;
import valjevac.kresimir.homework3.mvp.presenters.SignupPresenter;
import valjevac.kresimir.homework3.mvp.views.SignupView;
import valjevac.kresimir.homework3.network.ApiManager;

public class SignupPresenterImpl implements SignupPresenter {

    private SignupView view;

    private SignupInteractorImpl interactor;

    private final static int CURRENT_ERROR = 0;

    public SignupPresenterImpl(SignupView view) {
        this.view = view;
        interactor = new SignupInteractorImpl();
    }

    @Override
    public void register(String username, String email, String password, String confirmationPassword, final ViewGroup viewGroup) {

        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            return;
        }

        if (!validateInputFields(email, password, confirmationPassword, viewGroup)) {
            return;
        }

        view.showProgress();

        User user = new User(username, email, password, confirmationPassword);
        BaseData<User> data = new BaseData<>(ApiManager.TYPE_SESSION, user);
        BaseResponse<BaseData<User>> request = new BaseResponse<>(data);

        interactor.register(request, new SignupListener() {
            @Override
            public void onSignupSuccess(BaseResponse<BaseData<User>> body) {
                SharedPreferencesHelper.setInt(body.getData().getId(), SharedPreferencesHelper.USER_ID);
                SharedPreferencesHelper.setString(body.getData().getAttributes().getAuthToken(), SharedPreferencesHelper.AUTH_TOKEN);
                SharedPreferencesHelper.setString(body.getData().getAttributes().getUsername(), SharedPreferencesHelper.USER);
                SharedPreferencesHelper.setString(body.getData().getAttributes().getEmail(), SharedPreferencesHelper.EMAIL);

                view.showMessage(R.string.registration_successful);
                view.onSignupSuccess();
            }

            @Override
            public void onSignupError(String error) {
                view.hideProgress();

                if (ApiErrorHelper.createError(error)) {
                    view.showMessage(ApiErrorHelper.getFullError(CURRENT_ERROR));
                }

                view.onSignupFail();
            }
        });
    }

    @Override
    public void cancel() {

        if (interactor != null) {
            interactor.cancel();
        }
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

    private boolean isPasswordMatching(String password, String confirmation) {
        return password.equals(confirmation);
    }

    private boolean checkForEmptyFields(ViewGroup viewGroup) {
        EditText emptyEditText = validateEditTexts(viewGroup);

        if (emptyEditText != null) {
            view.showMessage(R.string.empty_field_warning);
            view.onEmptyField(emptyEditText);
        }

        return emptyEditText == null;
    }

    private boolean validateEmailAddress(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateInputFields(String email, String password, String confirmPassword, ViewGroup viewGroup) {

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

        boolean passwordsMatch = isPasswordMatching(password, confirmPassword);

        if (!TextUtils.isEmpty(password) && !passwordsMatch) {
            view.showMessage(R.string.passwords_not_match_warning);
            view.onPasswordsMissmatch();
            return false;
        }

        return true;
    }
}
