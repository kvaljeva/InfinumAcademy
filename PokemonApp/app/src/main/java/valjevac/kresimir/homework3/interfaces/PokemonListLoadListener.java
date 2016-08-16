package valjevac.kresimir.homework3.interfaces;

import java.util.ArrayList;

import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.ExtendedData;
import valjevac.kresimir.homework3.models.Pokemon;
import valjevac.kresimir.homework3.models.PokemonType;

public interface PokemonListLoadListener {

    void onPokemonListLoadSuccess(BaseResponse<ArrayList<ExtendedData<Pokemon, ArrayList<PokemonType>>>> body);

    void onPokemonListLoadFail(String error);
}
