package valjevac.kresimir.pokemonApp.interfaces;

import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.User;

public interface LoginListener {

    void onLoginSuccess(BaseResponse<BaseData<User>> body);

    void onLoginError(String error);
}
