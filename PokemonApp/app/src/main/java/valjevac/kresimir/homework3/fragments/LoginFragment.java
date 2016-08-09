package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LoginFragment extends Fragment {
    private Unbinder unbinder;

    public OnFragmentInteractionListener listener;

    @BindView(R.id.et_user_email)
    EditText etUserEmail;

    @BindView(R.id.et_user_password)
    EditText etUserPassword;

    @BindView(R.id.rl_login_container)
    RelativeLayout rlLoginContainer;

    @BindView(R.id.rl_login_form_container)
    RelativeLayout rlLoginFormContainer;

    @BindView(R.id.iv_login_pokemon_logo)
    ImageView ivPokemonLogo;

    @BindView(R.id.iv_login_pokeball_image)
    ImageView ivPokeballLogin;

    @BindView(R.id.pv_login)
    ProgressView progressView;

    private boolean isPasswordVisible;

    private boolean animateSplash;

    private boolean argumentsConsumed = false;

    private boolean isUserAuthorized;

    private final static int CURRENT_ERROR = 0;

    private final static int DRAWABLE_RIGHT_ICON = 2;

    private final static int CENTER_OFFSET = 30;

    private final static int TRANSLATE_DURATION = 1200;

    private final static int SCALE_DURATION = 500;

    private final static int ACTION_DELAY = 150;

    private static final String SPLASH_ANIMATION = "SplashAnimation";

    Call<BaseResponse<BaseData<User>>> loginUserCall;

    public LoginFragment() {

    }

    public interface OnFragmentInteractionListener {

        void onLoginButtonClick(boolean isSuccess);

        void onSignupButtonClick();

        void onSessionExists();
    }

    public static LoginFragment newInstance(boolean animateSplash) {
        LoginFragment fragment = new LoginFragment();

        Bundle args = new Bundle();
        args.putBoolean(StarterActivity.SPLASH_ANIMATION, animateSplash);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            animateSplash = savedInstanceState.getBoolean(SPLASH_ANIMATION);
        }
        else {

            Bundle arguments = getArguments();
            if (arguments != null && !argumentsConsumed && !animateSplash) {

                animateSplash = arguments.getBoolean(StarterActivity.SPLASH_ANIMATION);
                argumentsConsumed = true;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);

        isPasswordVisible = false;
        isUserAuthorized = false;

        String email = SharedPreferencesHelper.getString(SharedPreferencesHelper.EMAIL);
        String authToken = SharedPreferencesHelper.getString(SharedPreferencesHelper.AUTH_TOKEN);

        isUserAuthorized = !TextUtils.isEmpty(email) && !TextUtils.isEmpty(authToken);

        view.post(new Runnable() {
            @Override
            public void run() {

                loadSplashAnimation();
            }
        });

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(SPLASH_ANIMATION, animateSplash);
    }

    @OnClick(R.id.btn_register)
    public void openRegistrationForm() {
        listener.onSignupButtonClick();
    }

    @OnClick(R.id.btn_log_in)
    public void sendLoginInfo() {

        if (!NetworkHelper.isNetworkAvailable()) {
            Toast.makeText(getActivity(), R.string.no_internet_conn, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateInputFields()) {
            return;
        }

        displayProgress(true);

        sendUserData(etUserEmail.getText().toString(), etUserPassword.getText().toString());
    }

    private void sendUserData(String email, String password) {
        User user = new User(email, password);
        BaseData<User> data = new BaseData<>(ApiManager.TYPE_SESSION, user);
        BaseResponse<BaseData<User>> request = new BaseResponse<>(data);

        loginUserCall = ApiManager.getService().loginUser(request);

        loginUserCall.enqueue(new BaseCallback<BaseResponse<BaseData<User>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                displayProgress(false);

                listener.onLoginButtonClick(false);

                if (!NetworkHelper.isNetworkAvailable()) {

                    Toast.makeText(getActivity(), R.string.no_internet_conn, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ApiErrorHelper.createError(error)) {

                    Toast.makeText(getActivity(), ApiErrorHelper.getFullError(CURRENT_ERROR), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<User>> body, Response<BaseResponse<BaseData<User>>> response) {
                SharedPreferencesHelper.login(body.getData().getId(), body.getData().getAttributes().getAuthToken(),
                        body.getData().getAttributes().getUsername(), body.getData().getAttributes().getEmail());

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
                    etUserPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etUserPassword.setSelection(etUserPassword.length());

                    isPasswordVisible = false;
                }
                else {
                    etUserPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0);
                    etUserPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etUserPassword.setSelection(etUserPassword.length());

                    isPasswordVisible = true;
                }

                return true;
            }
        }

        return false;
    }

    private boolean validateInputFields() {
        if (!checkForEmptyFields()) {
            return false;
        }

        if (!validateEmailAddress(etUserEmail.getText().toString())) {
            Toast.makeText(getActivity(), "Wrong email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etUserPassword.length() < 8) {
            Toast.makeText(getActivity(), "Password has to be at least 8 characters long", Toast.LENGTH_SHORT).show();
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

    private boolean checkForEmptyFields() {
        EditText emptyEditText = validateEditTexts(rlLoginFormContainer);

        if (emptyEditText != null) {
            Toast.makeText(getActivity(), R.string.empty_field_warning, Toast.LENGTH_SHORT).show();
            emptyEditText.requestFocus();
        }

        return emptyEditText == null;
    }

    private void displayProgress(boolean isVisible) {
        if (isVisible) {
            rlLoginFormContainer.setVisibility(View.GONE);
            progressView.show();
        }
        else {
            rlLoginFormContainer.setVisibility(View.VISIBLE);
            progressView.hide();
        }
    }

    private void loadSplashAnimation() {
        if (!animateSplash) {
            setScreenViewsVisibility(true);
            return;
        }

        setScreenViewsVisibility(false);

        TranslateAnimation translation;
        int nextY = Math.round(ivPokemonLogo.getY()) - CENTER_OFFSET;
        int parentCenter = (rlLoginContainer.getHeight() / 2) - CENTER_OFFSET;

        translation = new TranslateAnimation(0, 0, parentCenter, -nextY);
        translation.setInterpolator(new AccelerateInterpolator());
        translation.setDuration(TRANSLATE_DURATION);
        translation.setFillAfter(true);

        translation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ivPokemonLogo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                final ScaleAnimation scale = new ScaleAnimation(0f, 1f, 0f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                scale.setDuration(SCALE_DURATION);
                scale.setFillAfter(true);

                scale.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        ivPokeballLogin.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        if (isUserAuthorized) {
                            final Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSessionExists();
                                }
                            }, ACTION_DELAY);
                        }
                        else {
                            rlLoginFormContainer.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                ivPokeballLogin.setAnimation(scale);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ivPokemonLogo.startAnimation(translation);

        animateSplash = false;
    }

    private void setScreenViewsVisibility(boolean isVisible) {
        if (isVisible) {

            rlLoginFormContainer.setVisibility(View.VISIBLE);
            ivPokeballLogin.setVisibility(View.VISIBLE);
            ivPokemonLogo.setVisibility(View.VISIBLE);
        }
        else {

            rlLoginFormContainer.setVisibility(View.INVISIBLE);
            ivPokeballLogin.setVisibility(View.INVISIBLE);
            ivPokemonLogo.setVisibility(View.INVISIBLE);
        }
    }
}
