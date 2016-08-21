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

    void showMessage(@StringRes final int message, int length);

    void showActionMessage(@StringRes final int message, int length, HashMap<String, Integer> data);

    void hideMessage();

    void showProgressDialog();

    void hideProgressDialog();

    void onLogout();

    void onOpenUserSettings();
}
