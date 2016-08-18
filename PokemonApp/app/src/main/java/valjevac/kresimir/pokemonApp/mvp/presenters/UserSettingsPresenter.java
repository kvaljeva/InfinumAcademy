package valjevac.kresimir.pokemonApp.mvp.presenters;

public interface UserSettingsPresenter {

    void updateEmail(String email);

    void deleteAccount(int id);

    void cancel();
}
