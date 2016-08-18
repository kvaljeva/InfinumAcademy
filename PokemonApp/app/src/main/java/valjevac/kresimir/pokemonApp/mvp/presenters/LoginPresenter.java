package valjevac.kresimir.pokemonApp.mvp.presenters;

import android.view.ViewGroup;

public interface LoginPresenter {

    void login(String email, String password, ViewGroup viewGroup);

    void cancel();
}
