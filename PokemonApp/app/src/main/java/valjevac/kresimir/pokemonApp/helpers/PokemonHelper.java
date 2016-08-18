package valjevac.kresimir.pokemonApp.helpers;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.ExtendedData;
import valjevac.kresimir.pokemonApp.models.Move;
import valjevac.kresimir.pokemonApp.models.MoveData;
import valjevac.kresimir.pokemonApp.models.PokemonType;
import valjevac.kresimir.pokemonApp.network.ApiManager;
import valjevac.kresimir.pokemonApp.network.BaseCallback;

public class PokemonHelper {
    private static PokemonHelper instance;

    private boolean typesLoaded = false;

    private boolean movesLoaded = false;

    private static final String movesUrl = "/api/v1/moves";

    private static Call<BaseResponse<ArrayList<ExtendedData<Move, MoveData>>>> pokemonMovesCall;

    private static Call<BaseResponse<ArrayList<BaseData<PokemonType>>>> pokemonTypesCall;

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
            loadPokemonMoves(movesUrl);
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
        pokemonTypesCall.enqueue(new BaseCallback<BaseResponse<ArrayList<BaseData<PokemonType>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                instance.movesLoaded = false;
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<BaseData<PokemonType>>> body, Response<BaseResponse<ArrayList<BaseData<PokemonType>>>> response) {

                for (BaseData data : body.getData()) {
                    PokemonType pokemonType = (PokemonType) data.getAttributes();

                    pokemonType.setId(data.getId());

                    types.add(pokemonType);
                }

                instance.typesLoaded = true;
            }
        });
    }

    private static void loadPokemonMoves(String page) {

        if (!NetworkHelper.isNetworkAvailable()) {
            return;
        }

        pokemonMovesCall = ApiManager.getService().getMoves(page);
        pokemonMovesCall.enqueue(new BaseCallback<BaseResponse<ArrayList<ExtendedData<Move, MoveData>>>>() {
            @Override
            public void onUnknownError(@Nullable String error) {

                instance.movesLoaded = false;
            }

            @Override
            public void onSuccess(BaseResponse<ArrayList<ExtendedData<Move, MoveData>>> body, Response<BaseResponse<ArrayList<ExtendedData<Move, MoveData>>>> response) {

                for (ExtendedData data : body.getData()) {
                    MoveData moveData = (MoveData) data.getRelationships().getModel().getData();
                    Move move = (Move) data.getAttributes();

                    move.setId(data.getId());
                    move.setType(moveData.getName());

                    moves.add(move);
                }

                String nextPage = body.getLinks().getNext();

                if (!TextUtils.isEmpty(nextPage)) {
                    loadPokemonMoves(nextPage);
                }
                else {
                    instance.movesLoaded = true;
                }
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
