package valjevac.kresimir.pokemonApp.mvp.presenters;

import java.util.ArrayList;

import valjevac.kresimir.pokemonApp.models.Pokemon;

public interface PokemonListPresenter {

    void getPokemonList(boolean showProgress);

    void getDrawerContent(String defaultUsername, String defaultEmail);

    void updatePokemonList(ArrayList<Pokemon> pokemonList);

    void updatePokemonList(Pokemon pokemon);

    void deletePokemon(int pokemonId, int position);

    void logout();

    void openUserSettings();

    void cancel();
}
