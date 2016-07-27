package valjevac.kresimir.homework3.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import valjevac.kresimir.homework3.R;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_user_email)
    EditText etUserEmail;

    @BindView(R.id.et_user_password)
    EditText etUserPassword;

    private boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        isPasswordVisible = false;
    }

    @OnClick(R.id.btn_register)
    public void openRegistrationForm() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_log_in)
    public void sendLoginInfo() {

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
}
