package valjevac.kresimir.homework3.activities;

import android.content.Intent;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import retrofit2.Call;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.models.Attributes;
import valjevac.kresimir.homework3.models.User;

public class LoginActivity extends AppCompatActivity {

    private static final String SESSION = "session";

    @BindView(R.id.et_user_email)
    EditText etUserEmail;

    @BindView(R.id.et_user_password)
    EditText etUserPassword;

    @BindView(R.id.rl_login_container)
    RelativeLayout rlLoginContainer;

    private boolean isPasswordVisible;

    private Call<User> loginUserCall;

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
        Attributes attributes = new Attributes(email, password);
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
