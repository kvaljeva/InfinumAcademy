package valjevac.kresimir.homework3.mvp.interactors;

import valjevac.kresimir.homework3.interfaces.LoginListener;

public interface LoginInteractor {

    void login(String email, String password, LoginListener listener);

    void cancel();
}
