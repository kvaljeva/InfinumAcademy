package valjevac.kresimir.homework3.mvp.views;

import android.widget.EditText;

public interface LoginView extends BaseView {

    void onLoginSuccess();

    void onLoginFail();

    void onEmptyField(EditText editText);
}
