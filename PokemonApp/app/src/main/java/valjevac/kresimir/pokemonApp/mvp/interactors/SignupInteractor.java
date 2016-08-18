package valjevac.kresimir.pokemonApp.mvp.interactors;

import valjevac.kresimir.pokemonApp.interfaces.SignupListener;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.User;

public interface SignupInteractor {

    void register(BaseResponse<BaseData<User>> request, SignupListener listener);

    void cancel();
}
