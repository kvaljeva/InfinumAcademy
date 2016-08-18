package valjevac.kresimir.pokemonApp.mvp.interactors.impl;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.pokemonApp.interfaces.DeletePokemonListener;
import valjevac.kresimir.pokemonApp.interfaces.LogoutListener;
import valjevac.kresimir.pokemonApp.interfaces.PokemonListLoadListener;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.ExtendedData;
import valjevac.kresimir.pokemonApp.models.Pokemon;
import valjevac.kresimir.pokemonApp.models.PokemonType;
import valjevac.kresimir.pokemonApp.mvp.interactors.PokemonListInteractor;
import valjevac.kresimir.pokemonApp.network.ApiManager;
import valjevac.kresimir.pokemonApp.network.BaseCallback;

public class PokemonListInteractorImpl implements PokemonListInteractor {

    Call<Void> logoutUserCall;

    Call<BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>>> pokemonListCall;

    Call<Void> deletePokemonCall;

    BaseCallback<BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>>> pokemonListCallback;

    public PokemonListInteractorImpl() {
    }

    @Override
    public void getPokemonList(final PokemonListLoadListener listener) {
        pokemonListCall = ApiManager.getService().getPokemons();

        pokemonListCallback = new BaseCallback<BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                listener.onPokemonListLoadFail(error);
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>> body,
                                  Response<BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>>> response) {

                listener.onPokemonListLoadSuccess(body);
            }
        };

        pokemonListCall.enqueue(pokemonListCallback);
    }

    @Override
    public void deletePokemon(int pokemonId, final DeletePokemonListener listener) {
        deletePokemonCall = ApiManager.getService().deletePokemon(pokemonId);

        deletePokemonCall.enqueue(new BaseCallback<Void>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onDeletePokemonFail(error);
            }

            @Override
            public void onSuccess(Void body, Response<Void> response) {
                listener.onDeletePokemonSuccess();
            }
        });
    }

    @Override
    public void logout(final LogoutListener listener) {
        logoutUserCall = ApiManager.getService().logoutUser();

        logoutUserCall.enqueue(new BaseCallback<Void>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onLogout();
            }

            @Override
            public void onSuccess(Void body, Response<Void> response) {
                listener.onLogout();
            }
        });
    }

    @Override
    public void cancel() {

        if (logoutUserCall != null) {
            logoutUserCall.cancel();
        }

        if (pokemonListCall != null) {
            pokemonListCall.cancel();
        }

        if (pokemonListCallback != null) {
            pokemonListCallback.cancel();
        }

        if (deletePokemonCall != null) {
            deletePokemonCall.cancel();
        }
    }
}
