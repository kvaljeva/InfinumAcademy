package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.StarterActivity;
import valjevac.kresimir.homework3.custom.ProgressView;
import valjevac.kresimir.homework3.helpers.SharedPreferencesHelper;
import valjevac.kresimir.homework3.mvp.presenters.impl.LoginPresenterImpl;
import valjevac.kresimir.homework3.mvp.views.LoginView;

public class LoginFragment extends Fragment implements LoginView {
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

    private boolean isUserAuthorized;

    private final static int DRAWABLE_RIGHT_ICON = 2;

    private final static int CENTER_OFFSET = 30;

    private final static int TRANSLATE_DURATION = 1200;

    private final static int SCALE_DURATION = 500;

    private final static int ACTION_DELAY = 300;

    private static final String SPLASH_ANIMATION = "SplashAnimation";

    LoginPresenterImpl loginPresenter;

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
            if (arguments != null && !animateSplash) {

                animateSplash = arguments.getBoolean(StarterActivity.SPLASH_ANIMATION);

                arguments.clear();
            }
        }

        loginPresenter = new LoginPresenterImpl(this);
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

        if (loginPresenter != null) {
            loginPresenter.cancel();
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
    public void onSignupClick() {
        listener.onSignupButtonClick();
    }

    @OnClick(R.id.btn_log_in)
    public void onLoginClick() {
        loginPresenter.login(etUserEmail.getText().toString(), etUserPassword.getText().toString(), rlLoginFormContainer);
    }

    @Override
    public void onLoginSuccess() {
        listener.onLoginButtonClick(true);
    }

    @Override
    public void onLoginFail() {
        listener.onLoginButtonClick(false);
    }

    @Override
    public void onEmptyField(EditText editText) {
        editText.requestFocus();
    }

    @Override
    public void showProgress() {
        rlLoginFormContainer.setVisibility(View.GONE);
        progressView.show();
    }

    @Override
    public void hideProgress() {
        rlLoginFormContainer.setVisibility(View.VISIBLE);
        progressView.hide();
    }

    @Override
    public void showMessage(@StringRes int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
