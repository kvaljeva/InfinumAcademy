package valjevac.kresimir.homework3.helpers;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Data;
import valjevac.kresimir.homework3.models.Move;
import valjevac.kresimir.homework3.models.PokemonType;
import valjevac.kresimir.homework3.network.ApiManager;
import valjevac.kresimir.homework3.network.BaseCallback;

public class PokemonHelper {
    private static PokemonHelper instance;

    private boolean typesLoaded = false;

    private boolean movesLoaded = false;

    private static Call<BaseResponse<ArrayList<Data<Move>>>> pokemonMovesCall;

    private static Call<BaseResponse<ArrayList<Data<PokemonType>>>> pokemonTypesCall;

    private static ArrayList<Move> moves;

    private static ArrayList<PokemonType> types;

    public static void init() {

        if (instance == null) {
            instance = new PokemonHelper();
        }
        else {
            return;
        }

        moves = new ArrayList<>();
        types = new ArrayList<>();

        if (!instance.typesLoaded) {
            loadPokemonTypes();
        }

        if (!instance.movesLoaded) {
            loadPokemonMoves();
        }
    }

    public static ArrayList<Move> getMoves() {
        return moves;
    }

    public static ArrayList<PokemonType> getTypes() {
        return types;
    }

    private static void loadPokemonTypes() {

        if (!NetworkHelper.isNetworkAvailable()) {
            return;
        }

        pokemonTypesCall = ApiManager.getService().getTypes();
        pokemonTypesCall.enqueue(new BaseCallback<BaseResponse<ArrayList<Data<PokemonType>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                instance.movesLoaded = false;
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<Data<PokemonType>>> body, Response<BaseResponse<ArrayList<Data<PokemonType>>>> response) {

                for (Data data : body.getData()) {
                    types.add((PokemonType) data.getAttributes());
                }

                instance.typesLoaded = true;
            }
        });
    }

    private static void loadPokemonMoves() {

        if (!NetworkHelper.isNetworkAvailable()) {
            return;
        }

        pokemonMovesCall = ApiManager.getService().getMoves();
        pokemonMovesCall.enqueue(new BaseCallback<BaseResponse<ArrayList<Data<Move>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                instance.movesLoaded = false;
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<Data<Move>>> body, Response<BaseResponse<ArrayList<Data<Move>>>> response) {

                for (Data data : body.getData()) {
                    moves.add((Move) data.getAttributes());
                }

                instance.movesLoaded = true;
            }
        });
    }

    public static void cancelRequests() {

        if (pokemonMovesCall != null) {
            pokemonMovesCall.cancel();
        }

        if (pokemonTypesCall != null) {
            pokemonTypesCall.cancel();
        }
    }

    public static boolean isCallActive() {
        return pokemonMovesCall != null || pokemonTypesCall != null;
    }
}
