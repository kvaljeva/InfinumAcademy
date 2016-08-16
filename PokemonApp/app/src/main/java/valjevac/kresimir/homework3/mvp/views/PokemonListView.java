package valjevac.kresimir.homework3.mvp.views;

import android.support.annotation.StringRes;

import java.util.ArrayList;

import valjevac.kresimir.homework3.models.Pokemon;

public interface PokemonListView extends BaseView {

    void onPokemonListLoadSuccess(ArrayList<Pokemon> pokemonList);

    void onPokemonListLoadFail(ArrayList<Pokemon> cachedPokemonList);

    void onListRefreshNeeded(ArrayList<Pokemon> pokemonList);

    void onDrawerContentReady(String username, String email);

    void showProgressMessage(@StringRes final int message);

    void hideProgressMessage();

    void onLogout();
}
