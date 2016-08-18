package valjevac.kresimir.pokemonApp.mvp.views;

import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.HashMap;

import valjevac.kresimir.pokemonApp.models.Pokemon;

public interface PokemonListView extends BaseView {

    void onPokemonListLoadSuccess(ArrayList<Pokemon> pokemonList);

    void onPokemonListLoadFail(ArrayList<Pokemon> cachedPokemonList);

    void onPokemonDeleted(int position);

    void onListRefreshNeeded(ArrayList<Pokemon> pokemonList);

    void onDrawerContentReady(String username, String email);

    void showProgressMessage(@StringRes final int message);

    void showProgressMessage(@StringRes final int message, int length);

    void showActionProgressMessage(@StringRes final int message, int length, HashMap<String, Integer> data);

    void hideProgressMessage();

    void showProgressDialog();

    void hideProgressDialog();

    void onLogout();

    void onOpenUserSettings();
}
