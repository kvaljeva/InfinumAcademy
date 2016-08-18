package valjevac.kresimir.pokemonApp.mvp.interactors.impl;

import android.support.annotation.Nullable;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.pokemonApp.interfaces.AddPokemonListener;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.Pokemon;
import valjevac.kresimir.pokemonApp.mvp.interactors.AddPokemonInteractor;
import valjevac.kresimir.pokemonApp.network.ApiManager;
import valjevac.kresimir.pokemonApp.network.BaseCallback;

public class AddPokemonInteractorImpl implements AddPokemonInteractor {

    Call<BaseResponse<BaseData<Pokemon>>> insertPokemonCall;

    public AddPokemonInteractorImpl() {
    }

    @Override
    public void addPokemon(Pokemon pokemon, int[] types, int[] moves, RequestBody body, final AddPokemonListener listener) {

        insertPokemonCall = ApiManager.getService().insertPokemon(
                pokemon.getName(),
                pokemon.getHeight(),
                pokemon.getWeight(),
                pokemon.getGenderId(),
                true,
                pokemon.getDescription(),
                types,
                moves,
                body
        );

        insertPokemonCall.enqueue(new BaseCallback<BaseResponse<BaseData<Pokemon>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {
                listener.onPokemonAddFail(error);
            }

            @Override
            public void onSuccess(BaseResponse<BaseData<Pokemon>> body, Response<BaseResponse<BaseData<Pokemon>>> response) {
                listener.onPokemonAddSuccess(body);
            }
        });
    }

    @Override
    public void cancel() {

        if (insertPokemonCall != null) {
            insertPokemonCall.cancel();
        }
    }
}
