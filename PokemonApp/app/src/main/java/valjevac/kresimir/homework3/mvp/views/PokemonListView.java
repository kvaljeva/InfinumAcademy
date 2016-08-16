package valjevac.kresimir.homework3.mvp.views;

import java.util.ArrayList;

import valjevac.kresimir.homework3.models.Pokemon;

public interface PokemonListView extends BaseView {

    void onPokemonListLoadSuccess(ArrayList<Pokemon> pokemonList);

    void onPokemonListLoadFail(ArrayList<Pokemon> cachedPokemonList);

    void onListRefreshNeeded(ArrayList<Pokemon> pokemonList);

    void onDrawerContentReady(String username, String email);

    void onLogout();
}
