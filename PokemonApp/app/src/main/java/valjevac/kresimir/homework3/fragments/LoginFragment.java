package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.PokemonListActivity;
import valjevac.kresimir.homework3.helpers.ApiErrorHelper;
import valjevac.kresimir.homework3.helpers.NetworkHelper;
import valjevac.kresimir.homework3.helpers.SharedPreferencesHelper;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.User;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class LoginFragment extends Fragment {
    private Unbinder unbinder;
    public OnFragmentInteractionListener listener;

    @BindView(R.id.et_user_email)
    EditText etUserEmail;

    @BindView(R.id.et_user_password)
    EditText etUserPassword;

    @BindView(R.id.rl_login_container)
    RelativeLayout rlLoginContainer;

    private boolean isPasswordVisible;

    private final static int CURRENT_ERROR = 0;

    private final static int DRAWABLE_RIGHT_ICON = 2;

    Call<BaseResponse<Data<User>>> loginUserCall;

    public LoginFragment() {

    }

    public interface OnFragmentInteractionListener {

        void onLoginButtonClick(boolean isSuccess);

        void onSignupButtonClick();
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);

        isPasswordVisible = false;

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (loginUserCall != null) {
            loginUserCall.cancel();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement onFragmentInteractionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (listener != null) {
            listener = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }


    @OnClick(R.id.btn_register)
    public void openRegistrationForm() {
        listener.onSignupButtonClick();
    }

    @OnClick(R.id.btn_log_in)
    public void sendLoginInfo() {

        if (!validateInputFields()) {
            return;
        }

        sendUserData(etUserEmail.getText().toString(), etUserPassword.getText().toString());
    }

    private void sendUserData(String email, String password) {
        User user = new User(email, password);
        Data<User> data = new Data<>(ApiManager.TYPE_SESSION, user);
        BaseResponse<Data<User>> request = new BaseResponse<>(data);

        loginUserCall = ApiManager.getService().loginUser(request);

        loginUserCall.enqueue(new BaseCallback<BaseResponse<Data<User>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                if (!NetworkHelper.isNetworkAvailable()) {

                    Toast.makeText(getActivity(), "No internet connection.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ApiErrorHelper.createError(error)) {

                    Toast.makeText(getActivity(), ApiErrorHelper.getFullError(CURRENT_ERROR), Toast.LENGTH_SHORT).show();
                }

                listener.onLoginButtonClick(false);
            }

            @Override
            public void onSuccess(BaseResponse<Data<User>> body, Response<BaseResponse<Data<User>>> response) {
                SharedPreferencesHelper.setInt(body.getData().getId(), SharedPreferencesHelper.USER_ID);
                SharedPreferencesHelper.setString(body.getData().getAttributes().getAuthToken(), SharedPreferencesHelper.AUTH_TOKEN);
                SharedPreferencesHelper.setString(body.getData().getAttributes().getUsername(), SharedPreferencesHelper.USER);
                SharedPreferencesHelper.setString(body.getData().getAttributes().getEmail(), SharedPreferencesHelper.EMAIL);

                Intent intent = new Intent(getActivity(), PokemonListActivity.class);
                startActivity(intent);

                listener.onLoginButtonClick(true);
            }
        });
    }

    @OnTouch(R.id.et_user_password)
    public boolean setEditTextIcon(MotionEvent event) {
        final int DRAWABLE_RIGHT = DRAWABLE_RIGHT_ICON;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (etUserPassword.getRight() -
                    etUserPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                if (isPasswordVisible) {
                    etUserPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    etUserPassword.setTransformationMethod(new PasswordTransformationMethod());

                    isPasswordVisible = false;
                }
                else {
                    etUserPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0);
                    etUserPassword.setTransformationMethod(null);

                    isPasswordVisible = true;
                }

                return true;
            }
        }

        return false;
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

    private boolean validateInputFields() {
        EditText emptyEditText = validateEditTexts(rlLoginContainer);

        if (emptyEditText != null) {
            Toast.makeText(getActivity(), "This field cannot be empty.", Toast.LENGTH_SHORT).show();
            emptyEditText.requestFocus();
        }

        return emptyEditText == null;
    }
}
