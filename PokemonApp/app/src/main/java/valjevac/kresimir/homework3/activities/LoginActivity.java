package valjevac.kresimir.homework3.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.helpers.ApiErrorHelper;
import valjevac.kresimir.homework3.helpers.SharedPreferencesHelper;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.User;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_user_email)
    EditText etUserEmail;

    @BindView(R.id.et_user_password)
    EditText etUserPassword;

    @BindView(R.id.rl_login_container)
    RelativeLayout rlLoginContainer;

    private boolean isPasswordVisible;

    private final static int CURRENT_ERROR = 0;

    Call<BaseResponse> loginUserCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        isPasswordVisible = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (loginUserCall != null) {
            loginUserCall.cancel();
        }
    }

    @OnClick(R.id.btn_register)
    public void openRegistrationForm() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
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
        BaseResponse request = new BaseResponse(data);

        loginUserCall = ApiManager.getService().loginUser(request);

        loginUserCall.enqueue(new BaseCallback<BaseResponse>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                ApiErrorHelper.createError(error);

                Toast.makeText(LoginActivity.this, ApiErrorHelper.getFullError(CURRENT_ERROR), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(BaseResponse body, Response<BaseResponse> response) {
                User userData = new Gson().fromJson(body.getData().getAttributes().toString(), User.class);

                SharedPreferencesHelper.setInt(body.getData().getId(), SharedPreferencesHelper.USER_ID);
                SharedPreferencesHelper.setString(userData.getAuthToken(), SharedPreferencesHelper.AUTH_TOKEN);
                SharedPreferencesHelper.setString(userData.getUsername(), SharedPreferencesHelper.USER);
                SharedPreferencesHelper.setString(userData.getEmail(), SharedPreferencesHelper.EMAIL);

                Intent intent = new Intent(LoginActivity.this, PokemonListActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }

    @OnTouch(R.id.et_user_password)
    public boolean setEditTextIcon(MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;

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
            Toast.makeText(LoginActivity.this, "This field cannot be empty.", Toast.LENGTH_SHORT).show();
            emptyEditText.requestFocus();
        }

        return emptyEditText == null;
    }
}
