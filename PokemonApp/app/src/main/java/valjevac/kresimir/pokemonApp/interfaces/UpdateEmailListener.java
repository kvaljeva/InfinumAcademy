package valjevac.kresimir.pokemonApp.interfaces;

import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.User;

public interface UpdateEmailListener {

    void onEmailUpdateSuccess(BaseResponse<BaseData<User>> body);

    void onEmailUpdateFail(String error);
}
