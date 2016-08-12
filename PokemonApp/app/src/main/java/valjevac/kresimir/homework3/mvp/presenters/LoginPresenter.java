package valjevac.kresimir.homework3.mvp.presenters;

import android.view.ViewGroup;

public interface LoginPresenter {

    void login(String email, String password, ViewGroup viewGroup);

    void cancel();
}
