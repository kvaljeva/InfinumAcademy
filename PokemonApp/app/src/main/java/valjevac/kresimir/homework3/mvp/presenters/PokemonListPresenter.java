package valjevac.kresimir.homework3.mvp.presenters;

import java.util.ArrayList;

import valjevac.kresimir.homework3.models.Pokemon;

public interface PokemonListPresenter {

    void getPokemonList(boolean showProgress);

    void getDrawerContent(String defaultUsername, String defaultEmail);

    void updatePokemonList(ArrayList<Pokemon> pokemonList);

    void updatePokemonList(Pokemon pokemon);

    void logout();

    void cancel();
}
