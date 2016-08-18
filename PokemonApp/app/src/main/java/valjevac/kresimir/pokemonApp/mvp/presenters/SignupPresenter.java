package valjevac.kresimir.pokemonApp.mvp.presenters;

import android.view.ViewGroup;

public interface SignupPresenter {

    void register(String username, String email, String password, String confirmationPassword, ViewGroup viewGroup);

    void cancel();
}
