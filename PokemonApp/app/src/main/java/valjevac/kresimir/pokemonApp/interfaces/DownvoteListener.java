package valjevac.kresimir.pokemonApp.interfaces;

public interface DownvoteListener {

    void onDownvoteSuccess();

    void onDownvoteFail(String error);
}
