package valjevac.kresimir.pokemonApp.interfaces;

import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.Pokemon;

public interface AddPokemonListener {

    void onPokemonAddSuccess(BaseResponse<BaseData<Pokemon>> body);

    void onPokemonAddFail(String error);
}
