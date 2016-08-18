package valjevac.kresimir.pokemonApp.interfaces;

import java.util.ArrayList;

import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.ExtendedData;
import valjevac.kresimir.pokemonApp.models.Pokemon;
import valjevac.kresimir.pokemonApp.models.PokemonType;

public interface PokemonListLoadListener {

    void onPokemonListLoadSuccess(BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>> body);

    void onPokemonListLoadFail(String error);
}
