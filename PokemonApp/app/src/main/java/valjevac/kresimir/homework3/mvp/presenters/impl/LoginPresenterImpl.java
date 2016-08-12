package valjevac.kresimir.homework3.mvp.presenters.impl;

import valjevac.kresimir.homework3.mvp.interactors.LoginInteractor;
import valjevac.kresimir.homework3.mvp.presenters.LoginPresenter;
import valjevac.kresimir.homework3.mvp.views.LoginView;

public class LoginPresenterImpl implements LoginPresenter {

    private LoginView view;

    private LoginInteractor interactor;

    public LoginPresenterImpl(LoginView view, LoginInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void login(String email, String password) {

    }
}
