package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
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
import valjevac.kresimir.homework3.activities.MainActivity;
import valjevac.kresimir.homework3.activities.StarterActivity;
import valjevac.kresimir.homework3.custom.ProgressView;
import valjevac.kresimir.homework3.helpers.ApiErrorHelper;
import valjevac.kresimir.homework3.helpers.NetworkHelper;
import valjevac.kresimir.homework3.helpers.SharedPreferencesHelper;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.User;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class SignupFragment extends Fragment {
    private Unbinder unbinder;
    public OnFragmentInteractionListener listener;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_signup_user_password)
    EditText etPassword;

    @BindView(R.id.et_signup_user_confirm_password)
    EditText etPasswordConfirm;

    @BindView(R.id.et_signup_user_email)
    EditText etEmail;

    @BindView(R.id.et_signup_user_nickname)
    EditText etNickname;

    @BindView(R.id.rl_signup_form_container)
    RelativeLayout rlSignupFormContainer;

    @BindView(R.id.pv_signup)
    ProgressView progressView;

    Call<BaseResponse<BaseData<User>>> registerUserCall;

    private boolean isPasswordVisible;

    private final static int CURRENT_ERROR = 0;

    private final static int DRAWABLE_RIGHT_ICON = 2;

    private final static boolean SIGNUP_SUCCESSFULL = true;

    private final static boolean SIGNUP_FAILED = false;

    public SignupFragment() {

    }

    public interface OnFragmentInteractionListener {

        void onBackButtonPressed();

        void onRegisterButtonPressed(boolean isSuccess);
    }

    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        unbinder = ButterKnife.bind(this, view);

        isPasswordVisible = false;

        setUpToolbar();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (registerUserCall != null) {
            registerUserCall.cancel();
        }
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            StarterActivity activity = (StarterActivity) getActivity();

            activity.setSupportActionBar(toolbar);

            if (activity.getSupportActionBar() != null) {

                activity.getSupportActionBar().setTitle(R.string.sign_up_activity_title);
                activity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onBackButtonPressed();
                }
            });
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

    private boolean checkForEmptyFields() {
        EditText emptyEditText = validateEditTexts(rlSignupFormContainer);

        if (emptyEditText != null) {
            Toast.makeText(getActivity(), "This field cannot be empty.", Toast.LENGTH_SHORT).show();
            emptyEditText.requestFocus();
        }

        return emptyEditText == null;
    }

    private boolean validateEmailAddress(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateInputFields() {

        if (!checkForEmptyFields()) {
            return false;
        }

        if (!validateEmailAddress(etEmail.getText().toString())) {
            Toast.makeText(getActivity(), "Wrong email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPassword.length() < 8) {
            Toast.makeText(getActivity(), "Password has to be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean passwordsMatch = isPasswordMatching(etPassword.getText().toString(), etPasswordConfirm.getText().toString());

        if (!TextUtils.isEmpty(etPassword.getText()) && !passwordsMatch) {
            Toast.makeText(getActivity(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    @OnTouch(R.id.et_signup_user_password)
    public boolean setEditTextIcon(View view, MotionEvent event) {
        final int DRAWABLE_RIGHT = DRAWABLE_RIGHT_ICON;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (view.getRight() -
                    etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                if (isPasswordVisible) {
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    etPasswordConfirm.setTransformationMethod(new PasswordTransformationMethod());

                    isPasswordVisible = false;
                }
                else {
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0);
                    etPassword.setTransformationMethod(null);
                    etPasswordConfirm.setTransformationMethod(null);

                    isPasswordVisible = true;
                }

                return true;
            }
        }

        return false;
    }

    @OnClick(R.id.btn_register_confirm)
    public void registerUser() {

        if (!NetworkHelper.isNetworkAvailable()) {

            Toast.makeText(getActivity(), R.string.no_internet_conn, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateInputFields()) {
            return;
        }

        displayProgress(true);

        String username = etNickname.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmationPassword = etPasswordConfirm.getText().toString();

        sendUserData(username, email, password, confirmationPassword);
    }

    private void sendUserData(String username, String email, String password, String confirmationPassword) {
        User user = new User(username, email, password, confirmationPassword);
        BaseData<User> data = new BaseData<>(ApiManager.TYPE_SESSION, user);
        BaseResponse<BaseData<User>> request = new BaseResponse<>(data);

        registerUserCall = ApiManager.getService().insertUser(request);

        registerUserCall.enqueue(new BaseCallback<BaseResponse<BaseData<User>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                displayProgress(false);

                if (!NetworkHelper.isNetworkAvailable()) {

                    Toast.makeText(getActivity(), R.string.no_internet_conn, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ApiErrorHelper.createError(error)) {

                    Toast.makeText(getActivity(), ApiErrorHelper.getFullError(CURRENT_ERROR), Toast.LENGTH_SHORT).show();
                }

                listener.onRegisterButtonPressed(SIGNUP_FAILED);
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<User>> body, Response<BaseResponse<BaseData<User>>> response) {
                Toast.makeText(getActivity(), "Registered successfully!", Toast.LENGTH_SHORT).show();

                SharedPreferencesHelper.setInt(body.getData().getId(), SharedPreferencesHelper.USER_ID);
                SharedPreferencesHelper.setString(body.getData().getAttributes().getAuthToken(), SharedPreferencesHelper.AUTH_TOKEN);
                SharedPreferencesHelper.setString(body.getData().getAttributes().getUsername(), SharedPreferencesHelper.USER);
                SharedPreferencesHelper.setString(body.getData().getAttributes().getEmail(), SharedPreferencesHelper.EMAIL);

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                listener.onRegisterButtonPressed(SIGNUP_SUCCESSFULL);
            }
        });
    }

    private void displayProgress(boolean isVisible) {
        if (isVisible) {
            rlSignupFormContainer.setVisibility(View.GONE);
            progressView.show();
        }
        else {
            rlSignupFormContainer.setVisibility(View.VISIBLE);
            progressView.hide();
        }
    }
}
