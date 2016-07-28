package valjevac.kresimir.homework3.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
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
import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.User;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rl_signup_container)
    RelativeLayout rlSignupContainer;

    @BindView(R.id.et_signup_user_password)
    EditText etPassword;

    @BindView(R.id.et_signup_user_confirm_password)
    EditText etPasswordConfirm;

    @BindView(R.id.et_signup_user_email)
    EditText etEmail;

    @BindView(R.id.et_signup_user_nickname)
    EditText etNickname;

    Call<BaseResponse> registerUserCall;

    private boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        isPasswordVisible = false;

        setUpToolbar();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (registerUserCall != null) {
            registerUserCall.cancel();
        }
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {

                getSupportActionBar().setTitle(R.string.sign_up_activity_title);
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
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

    private boolean validateInputFields() {
        EditText emptyEditText = validateEditTexts(rlSignupContainer);

        if (emptyEditText != null) {
            Toast.makeText(SignUpActivity.this, "This field cannot be empty.", Toast.LENGTH_SHORT).show();
            emptyEditText.requestFocus();
        }

        boolean passwordsMatch = isPasswordMatching(etPassword.getText().toString(), etPasswordConfirm.getText().toString());

        if (!TextUtils.isEmpty(etPassword.getText()) && !passwordsMatch) {
            Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
        }

        return emptyEditText == null && passwordsMatch;
    }

    @OnTouch({R.id.et_signup_user_password, R.id.et_signup_user_confirm_password})
    public boolean setEditTextIcon(View view, MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;

        EditText editText = (EditText) view;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (view.getRight() -
                    editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                if (isPasswordVisible) {
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    editText.setTransformationMethod(new PasswordTransformationMethod());

                    isPasswordVisible = false;
                }
                else {
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0);
                    editText.setTransformationMethod(null);

                    isPasswordVisible = true;
                }

                return true;
            }
        }

        return false;
    }

    @OnClick(R.id.btn_register_confirm)
    public void registerUser() {

        if (!validateInputFields()) {
            return;
        }

        String username = etNickname.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmationPassword = etPasswordConfirm.getText().toString();

        sendUserData(username, email, password, confirmationPassword);
    }

    private void sendUserData(String username, String email, String password, String confirmationPassword) {
        User user = new User(username, email, password, confirmationPassword);
        Data<User> data = new Data<>(ApiManager.TYPE_SESSION, user);
        BaseResponse request = new BaseResponse(data);

        registerUserCall = ApiManager.getService().insertUser(request);

        registerUserCall.enqueue(new BaseCallback<BaseResponse>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(BaseResponse body, Response<BaseResponse> response) {
                Toast.makeText(SignUpActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SignUpActivity.this, PokemonListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }
        });
    }
}
