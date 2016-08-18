package valjevac.kresimir.pokemonApp.interfaces;

import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.User;

public interface SignupListener {

    void onSignupSuccess(BaseResponse<BaseData<User>> body);

    void onSignupError(String error);
}
