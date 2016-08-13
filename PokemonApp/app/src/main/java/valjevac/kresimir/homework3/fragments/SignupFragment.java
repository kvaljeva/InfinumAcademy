package valjevac.kresimir.homework3.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
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
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.activities.StarterActivity;
import valjevac.kresimir.homework3.custom.ProgressView;
import valjevac.kresimir.homework3.mvp.presenters.SignupPresenter;
import valjevac.kresimir.homework3.mvp.presenters.impl.SignupPresenterImpl;
import valjevac.kresimir.homework3.mvp.views.SignupView;

public class SignupFragment extends Fragment implements SignupView {

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

    private boolean isPasswordVisible;

    private final static int DRAWABLE_RIGHT_ICON = 2;

    private final static boolean SIGNUP_SUCCESSFULL = true;

    private final static boolean SIGNUP_FAILED = false;

    private SignupPresenter presenter;

    public SignupFragment() {

    }

    public interface OnFragmentInteractionListener {

        void onBackButtonPressed();

        void onRegisterButtonPressed(boolean isSuccess);
    }

    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new SignupPresenterImpl(this);
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

        if (presenter != null) {
            presenter.cancel();
        }
    }

    @Override
    public void onSignupSuccess() {
        listener.onRegisterButtonPressed(SIGNUP_SUCCESSFULL);
    }

    @Override
    public void onSignupFail() {
        listener.onRegisterButtonPressed(SIGNUP_FAILED);
    }

    @Override
    public void onEmptyField(EditText editText) {
        editText.requestFocus();
    }

    @Override
    public void showProgress() {
        rlSignupFormContainer.setVisibility(View.GONE);
        progressView.show();
    }

    @Override
    public void hideProgress() {
        rlSignupFormContainer.setVisibility(View.VISIBLE);
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

    @Override
    public void onPasswordsMissmatch() {
        etPassword.requestFocus();
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

        presenter.register(etNickname.getText().toString(), etEmail.getText().toString(),
                etPassword.getText().toString(), etPasswordConfirm.getText().toString(), rlSignupFormContainer);
    }
}
