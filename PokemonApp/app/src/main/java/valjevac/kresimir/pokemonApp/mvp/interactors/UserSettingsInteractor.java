package valjevac.kresimir.pokemonApp.mvp.interactors;

import valjevac.kresimir.pokemonApp.interfaces.DeleteAccountListener;
import valjevac.kresimir.pokemonApp.interfaces.UpdateEmailListener;
import valjevac.kresimir.pokemonApp.models.BaseData;
import valjevac.kresimir.pokemonApp.models.BaseResponse;
import valjevac.kresimir.pokemonApp.models.User;

public interface UserSettingsInteractor {

    void updateEmail(BaseResponse<BaseData<User>> request, UpdateEmailListener listener);

    void deleteAccount(int id, DeleteAccountListener listener);

    void cancel();
}
