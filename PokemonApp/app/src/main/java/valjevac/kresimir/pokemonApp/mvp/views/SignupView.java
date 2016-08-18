package valjevac.kresimir.pokemonApp.mvp.views;

import android.widget.EditText;

public interface SignupView extends BaseView {

    void onSignupSuccess();

    void onSignupFail();

    void onEmptyField(EditText editText);

    void onPasswordsMissmatch();
}
