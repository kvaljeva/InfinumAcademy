package valjevac.kresimir.homework3.mvp.interactors.impl;

import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.ProcessPokemonList;
import valjevac.kresimir.homework3.helpers.ApiErrorHelper;
import valjevac.kresimir.homework3.interfaces.LogoutListener;
import valjevac.kresimir.homework3.interfaces.PokemonListLoadListener;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.ExtendedData;
import valjevac.kresimir.homework3.models.Pokemon;
import valjevac.kresimir.homework3.models.PokemonType;
import valjevac.kresimir.homework3.mvp.interactors.PokemonListInteractor;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class PokemonListInteractorImpl implements PokemonListInteractor {

    Call<Void> logoutUserCall;

    Call<BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>>> pokemonListCall;

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
    }
}
