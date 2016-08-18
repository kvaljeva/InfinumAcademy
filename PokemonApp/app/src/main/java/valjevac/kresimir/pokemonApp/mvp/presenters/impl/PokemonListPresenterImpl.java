package valjevac.kresimir.pokemonApp.mvp.presenters.impl;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import valjevac.kresimir.pokemonApp.ProcessPokemonList;
import valjevac.kresimir.pokemonApp.R;
import valjevac.kresimir.pokemonApp.database.SQLitePokemonList;
import valjevac.kresimir.pokemonApp.enums.MessageLength;
import valjevac.kresimir.pokemonApp.fragments.PokemonListFragment;
import valjevac.kresimir.pokemonApp.helpers.ApiErrorHelper;
import valjevac.kresimir.pokemonApp.helpers.NetworkHelper;
import valjevac.kresimir.pokemonApp.helpers.SharedPreferencesHelper;
import valjevac.kresimir.pokemonApp.interfaces.DeletePokemonListener;
import valjevac.kresimir.pokemonApp.interfaces.LogoutListener;
import valjevac.kresimir.pokemonApp.interfaces.PokemonList;
import valjevac.kresimir.pokemonApp.interfaces.PokemonListLoadListener;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.ExtendedData;
import valjevac.kresimir.pokemonApp.models.Pokemon;
import valjevac.kresimir.pokemonApp.models.PokemonType;
import valjevac.kresimir.pokemonApp.mvp.interactors.impl.PokemonListInteractorImpl;
import valjevac.kresimir.pokemonApp.mvp.presenters.PokemonListPresenter;
import valjevac.kresimir.pokemonApp.mvp.views.PokemonListView;

public class PokemonListPresenterImpl implements PokemonListPresenter {

    PokemonListView view;

    private ArrayList<Pokemon> pokemons;

    private PokemonList pokemonListDatabase;

    private ProcessPokemonList pokemonListProcessor;

    private PokemonListInteractorImpl interactor;

    public final String POKEMON_ID = "PokemonId";

    public final String LIST_POSITION = "Position";

    public PokemonListPresenterImpl(PokemonListView view, ArrayList<Pokemon> pokemons) {
        this.view = view;
        this.pokemons = pokemons;

        pokemonListDatabase = new SQLitePokemonList();
        interactor = new PokemonListInteractorImpl();
    }

    @Override
    public void getPokemonList(boolean showProgress) {

        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            loadCachedList();
            return;
        }

        if (showProgress) {
            view.showProgressMessage(R.string.loading_pokemon_list);
        }

        interactor.getPokemonList(new PokemonListLoadListener() {
            @Override
            public void onPokemonListLoadSuccess(BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>> body) {
                pokemonListProcessor = new ProcessPokemonList(body, pokemonListDatabase, (PokemonListFragment) view);

                Thread thread = new Thread(pokemonListProcessor);
                thread.start();
            }

            @Override
            public void onPokemonListLoadFail(String error) {

                if (ApiErrorHelper.createError(error)) {
                    view.showMessage(ApiErrorHelper.getFullError(0));
                }

                loadCachedList();
            }
        });
    }

    @Override
    public void deletePokemon(final int pokemonId, final int position) {

        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            return;
        }

        view.showProgressDialog();

        interactor.deletePokemon(pokemonId, new DeletePokemonListener() {
            @Override
            public void onDeletePokemonSuccess() {
                pokemons.remove(position);

                view.hideProgressDialog();
                view.onPokemonDeleted(position);
                view.showProgressMessage(R.string.delete_success, MessageLength.LONG);
            }

            @Override
            public void onDeletePokemonFail(String error) {
                HashMap<String, Integer> data = new HashMap<>();
                data.put(POKEMON_ID, pokemonId);
                data.put(LIST_POSITION, position);

                view.hideProgressDialog();
                view.showActionProgressMessage(R.string.delete_fail, MessageLength.LONG, data);
            }
        });
    }

    @Override
    public void logout() {

        interactor.logout(new LogoutListener() {
            @Override
            public void onLogout() {
                view.onLogout();
            }
        });
    }

    @Override
    public void cancel() {

        if (pokemonListProcessor != null) {
            pokemonListProcessor.cancel();
        }

        if (interactor != null) {
            interactor.cancel();
        }

        view.hideProgressMessage();
    }

    private void loadCachedList() {
        pokemons.clear();
        pokemons.addAll(pokemonListDatabase.getPokemons());
        Collections.reverse(pokemons);

        view.hideProgress();
        view.onPokemonListLoadFail(pokemons);
    }

    @Override
    public void getDrawerContent(String defaultUsername, String defaultEmail) {
        String username = SharedPreferencesHelper.getString(SharedPreferencesHelper.USER);
        String email = SharedPreferencesHelper.getString(SharedPreferencesHelper.EMAIL);

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email)) {
            view.onDrawerContentReady(username, email);
        }
        else {
            view.onDrawerContentReady(defaultUsername, defaultEmail);
        }
    }

    @Override
    public void updatePokemonList(ArrayList<Pokemon> pokemonList) {
        pokemons.clear();
        pokemons.addAll(pokemonList);

        view.hideProgress();
        view.hideProgressMessage();
        view.onPokemonListLoadSuccess(pokemons);
    }

    @Override
    public void updatePokemonList(Pokemon pokemon) {
        if (pokemon != null) {
            pokemons.add(0, pokemon);
            view.onListRefreshNeeded(pokemons);
        }
    }

    @Override
    public void openUserSettings() {
        view.onOpenUserSettings();
    }
}
