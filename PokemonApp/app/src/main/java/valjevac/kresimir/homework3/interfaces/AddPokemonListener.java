package valjevac.kresimir.homework3.interfaces;

import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Pokemon;

public interface AddPokemonListener {

    void onPokemonAddSuccess(BaseResponse<BaseData<Pokemon>> body);

    void onPokemonAddFail(String error);
}
