package valjevac.kresimir.pokemonApp.mvp.presenters;

public interface UserSettingsPresenter {

    void updateEmail(String email, boolean changesMade);

    void deleteAccount(int id);

    void cancel();
}
